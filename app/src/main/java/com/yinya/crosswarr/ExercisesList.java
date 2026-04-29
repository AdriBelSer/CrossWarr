package com.yinya.crosswarr;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.yinya.crosswarr.adapters.ExercisesUserViewAdapter;
import com.yinya.crosswarr.adapters.OnExerciseClickListener;
import com.yinya.crosswarr.databinding.FragmentExercisesListBinding;
import com.yinya.crosswarr.models.ExerciseData;
import com.yinya.crosswarr.repository.Repository;

import java.util.ArrayList;

/**
 * Fragmento principal del catálogo de ejercicios para el usuario estándar.
 * Muestra una lista o "diccionario" de los movimientos disponibles en la aplicación.
 * Implementa filtros de negocio en tiempo real para mostrar únicamente los ejercicios
 * que están actualmente en uso por algún reto y que coinciden con las preferencias
 * de material del usuario.
 */
public class ExercisesList extends Fragment {

    private FragmentExercisesListBinding binding; //Binding para el layout
    private ArrayList<ExerciseData> exercises; //Lista de ejercicios
    private ExercisesUserViewAdapter adapter;// Adaptador específico para la vista del usuario

    /**
     * Invocado para inflar el diseño XML asociado a este fragmento utilizando View Binding.
     *
     * @param inflater           El objeto LayoutInflater utilizado para inflar las vistas.
     * @param container          El ViewGroup padre en el que se insertará la vista.
     * @param savedInstanceState Si no es null, el fragmento está siendo reconstruido.
     * @return La vista raíz (View) del diseño inflado.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflar el layout de forma segura
        binding = FragmentExercisesListBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    /**
     * Invocado inmediatamente después de que onCreateView ha retornado.
     * Configura el RecyclerView, intercepta el botón de retroceso y dispara
     * la carga reactiva de datos y observadores.
     *
     * @param view               La Vista devuelta por onCreateView().
     * @param savedInstanceState Si no es null, el fragmento está siendo reconstruido.
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        exercises = new ArrayList<>();

        // Configurar el RecyclerView y su adaptador con el listener de clics
        adapter = new ExercisesUserViewAdapter(exercises, getContext(), new OnExerciseClickListener() {
            @Override
            public void onExerciseClick(ExerciseData exercise, View view) {
                exerciseUserClicked(exercise, view);
            }
        });
        binding.exercisesRecyclerview.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.exercisesRecyclerview.setAdapter(adapter);

        // LÓGICA DE NAVEGACIÓN: Interceptar el botón físico "Atrás"
        // Evita que el usuario salga accidentalmente de la aplicación o de la pestaña principal
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                // Se deja intencionalmente vacío para que no haga nada al pulsar "atrás"
            }
        });

        // Configurar los observadores reactivos (LiveData)
        setupObservers();

        // Inicializa la lista de ejercicios
        loadExercise(); // Cargar los ejercicios

    }

    /**
     * Configura la suscripción al LiveData del Repositorio y aplica las reglas de negocio.
     * Cuando los ejercicios se descargan, realiza una consulta cruzada a Firebase para obtener
     * el perfil del usuario activo. Con ambos datos, aplica dos filtros:
     * 1. Elimina de la vista los ejercicios que no se usan en ningún reto (isUsed == false).
     * 2. Elimina los ejercicios que requieren equipo si el usuario configuró que no tiene material.
     */
    private void setupObservers() {
        Repository.getInstance().getExercisesLiveData().observe(getViewLifecycleOwner(), listFromFirebase -> {
            if (listFromFirebase != null) {

                FirebaseUser fUser = FirebaseAuth.getInstance().getCurrentUser();
                if (fUser != null) {

                    // Descargamos los ajustes del usuario para saber si tiene material en casa
                    com.yinya.crosswarr.network.FirebaseService.getInstance().getDocument("crosswarr", fUser.getUid(), new com.yinya.crosswarr.network.IFirebaseCallback() {

                        @Override
                        public void onSuccess(java.util.Map<String, Object> dataFromFirebase) {
                            if (binding == null) return;
                            boolean userHasEquipment = false;

                            // Extraemos el ajuste "useMaterials" del usuario
                            if (dataFromFirebase != null && dataFromFirebase.get("settings") != null) {
                                java.util.Map<String, Object> settings = (java.util.Map<String, Object>) dataFromFirebase.get("settings");
                                Boolean useMat = (Boolean) settings.get("useMaterials");
                                if (useMat != null) userHasEquipment = useMat;
                            }

                            exercises.clear();

                            // Aplicación de los filtros de negocio
                            for (ExerciseData ex : listFromFirebase) {
                                // Filtro 1: ¿Está activo (isUsed)? Si no lo está, lo saltamos y pasamos al siguiente.
                                if (!ex.isUsed()) {
                                    continue;
                                }

                                // Filtro 2: Comprobación de requerimientos de material
                                boolean exerciseRequiresEquipment = false;
                                if (ex.getType() != null) {
                                    String type = ex.getType().toLowerCase();
                                    // Lógica para determinar si el string de la BBDD implica uso de equipo
                                    exerciseRequiresEquipment = type.contains("with_equipment") && !type.contains("without_equipment");
                                }

                                // Si el ejercicio NO requiere material, entra.
                                // Si SÍ requiere material, solo entra si el usuario marcó "useMaterials" en su perfil.
                                if (!exerciseRequiresEquipment || userHasEquipment) {
                                    exercises.add(ex);
                                }
                            }

                            // Actualizar la interfaz y detener la animación de carga (Skeleton)
                            if (adapter != null) {
                                adapter.notifyDataSetChanged();
                                com.yinya.crosswarr.SkeletonUtils.hideSkeleton(binding.shimmerViewContainer, binding.exercisesRecyclerview);
                            }
                        }

                        @Override
                        public void onFailure(Exception e) {
                            android.util.Log.e("ExercisesList", "Error al traer datos del usuario", e);
                        }
                    });
                }
            }
        });
    }

    /**
     * Inicia la petición de ejercicios al Repositorio.
     * Implementa Lazy Loading: solo pide los datos a Firebase si la lista en memoria
     * está vacía o es nula, ahorrando ancho de banda y lecturas.
     */
    private void loadExercise() {
        if (Repository.getInstance().getExercisesLiveData().getValue() == null || Repository.getInstance().getExercisesLiveData().getValue().isEmpty()) {
            Repository.getInstance().fetchExercisesFromFirebase();
        }
    }

    /**
     * Navega a la vista de detalle de un ejercicio específico.
     * Empaqueta todos los atributos del modelo en un Bundle para pasarlos de forma
     * segura a la pantalla destino.
     *
     * @param exercise El objeto de ejercicio sobre el que el usuario ha hecho clic.
     * @param view     La vista de la tarjeta interactuada.
     */
    public void exerciseUserClicked(ExerciseData exercise, View view) {
        Bundle bundle = new Bundle();
        bundle.putString("name", exercise.getName()); // Pasa el nombre
        bundle.putString("description", exercise.getDescription()); // Pasa la descripción
        bundle.putString("type", exercise.getType()); // Pasa el tipo
        bundle.putString("image", exercise.getImage()); // Pasa la imagen
        bundle.putString("video", exercise.getVideo()); // Pasa el video
        bundle.putStringArrayList("materials", exercise.getMaterials()); // Pasa los materiales

        // Navegar al ExerciseDetailFragment con el Bundle
        Navigation.findNavController(view).navigate(R.id.exerciseDetail, bundle);
    }

    /**
     * Invocado cuando la vista del fragmento va a ser destruida.
     * Limpia el adaptador del RecyclerView y anula el View Binding para
     * evitar fugas de memoria (memory leaks).
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (binding != null && binding.exercisesRecyclerview != null) {
            binding.exercisesRecyclerview.setAdapter(null);
        }
        adapter = null;
        binding = null;
    }
}