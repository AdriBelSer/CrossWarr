package com.yinya.crosswarr;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.yinya.crosswarr.adapters.ExercisesEditionViewAdapter;
import com.yinya.crosswarr.adapters.OnExerciseAdminListener;
import com.yinya.crosswarr.databinding.FragmentExercisesEditionListBinding;
import com.yinya.crosswarr.models.ExerciseData;
import com.yinya.crosswarr.repository.Repository;

import java.util.ArrayList;

/**
 * Fragmento que muestra la lista completa de Ejercicios en el panel de administrador.
 * Permite visualizar el catálogo, navegar al detalle de cada ejercicio, acceder al formulario
 * de creación y gestionar el borrado seguro de los ejercicios.
 */
public class ExercisesEditionList extends Fragment {
    private FragmentExercisesEditionListBinding binding;
    private ArrayList<ExerciseData> exercises;
    private ExercisesEditionViewAdapter adapter;

    /**
     * Invocado para inflar el diseño XML asociado a este fragmento utilizando View Binding.
     *
     * @param inflater           El objeto LayoutInflater utilizado para inflar las vistas en el contexto.
     * @param container          El ViewGroup padre en el que se insertará la vista del fragmento.
     * @param savedInstanceState Si no es null, este fragmento está siendo reconstruido.
     * @return La vista raíz (View) del diseño inflado.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentExercisesEditionListBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    /**
     * Invocado inmediatamente después de que onCreateView ha retornado.
     * Configura el RecyclerView, el adaptador y los listeners de la lista.
     * Incluye una regla de negocio crítica: impide el borrado de ejercicios que ya estén asignados a un reto.
     *
     * @param view               La Vista devuelta por onCreateView().
     * @param savedInstanceState Si no es null, el fragmento está siendo reconstruido.
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        exercises = new ArrayList<>();

        // Configuración del adaptador con sus acciones (Clic normal y Clic en papelera)
        adapter = new ExercisesEditionViewAdapter(exercises, getContext(), new OnExerciseAdminListener() {
            @Override
            public void onExerciseClick(ExerciseData exercise, View view) {
                exerciseAdminClicked(exercise, view);
            }

            // LÓGICA DE INTEGRIDAD: No se puede borrar un ejercicio si está en uso por algún desafío.
            // Esto evita que los retos activos se rompan por falta de datos.
            @Override
            public void onDeleteClick(ExerciseData exercise) {
                if (exercise.isUsed()) {
                    android.widget.Toast.makeText(getContext(), R.string.exercises_edition_list_btn_delete_error, android.widget.Toast.LENGTH_LONG).show();
                } else {
                    // Si no está en uso, pedimos confirmación de seguridad antes de destruir el dato
                    new android.app.AlertDialog.Builder(getContext()).setTitle(R.string.exercises_edition_list_btn_delete_title).setMessage(requireContext().getString(R.string.exercises_edition_list_btn_delete_message)).setPositiveButton(R.string.exercises_edition_list_btn_delete_yes, (dialog, which) -> {

                        // 1. Ordenamos a Firebase que lo borre a través del Repositorio
                        Repository.getInstance().deleteExercise(exercise);

                        // 2. Actualización: lo quitamos de la lista visual al momento
                        exercises.remove(exercise);
                        adapter.notifyDataSetChanged();

                        android.widget.Toast.makeText(getContext(), R.string.exercises_edition_list_btn_delete_success, android.widget.Toast.LENGTH_SHORT).show();
                    }).setNegativeButton(R.string.exercises_edition_list_btn_delete_cancel, null).show();
                }
            }
        });

        // Configuración visual de la lista
        binding.exercisesEditionRecyclerview.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.exercisesEditionRecyclerview.setAdapter(adapter);

        // Configuración del botón flotante (FAB) para crear nuevos ejercicios
        if (binding.btnNewExercise != null) {
            binding.btnNewExercise.setOnClickListener(v -> {
                // Navegamos a la vista de edición sin pasarle datos (modo creación)
                Navigation.findNavController(v).navigate(R.id.exercisesEdition);
            });
        }

        setupObservers();
        loadExercise();
    }

    /**
     * Configura la suscripción al LiveData del Repositorio.
     * Actualiza automáticamente el RecyclerView cuando los datos de Firebase llegan o cambian,
     * y se encarga de ocultar la animación de carga (Shimmer/Skeleton).
     */
    private void setupObservers() {
        Repository.getInstance().getExercisesLiveData().observe(getViewLifecycleOwner(), listFromFirebase -> {
            if (listFromFirebase != null) {
                exercises.clear();
                exercises.addAll(listFromFirebase);
                if (adapter != null) {
                    adapter.notifyDataSetChanged();
                    com.yinya.crosswarr.SkeletonUtils.hideSkeleton(binding.shimmerViewContainer, binding.exercisesEditionRecyclerview);
                }
            }
        });
    }

    /**
     * Inicia la petición de datos al Repositorio.
     * Incluye una optimización de red (Lazy Loading): solo hace la petición a Firebase
     * si la lista actual en memoria está vacía o es nula, ahorrando lecturas innecesarias en la base de datos.
     */
    private void loadExercise() {
        if (Repository.getInstance().getExercisesLiveData().getValue() == null || Repository.getInstance().getExercisesLiveData().getValue().isEmpty()) {
            Repository.getInstance().fetchExercisesFromFirebase();
        }
    }

    /**
     * Navega a la vista de detalle del ejercicio.
     * Extrae los datos del modelo y los empaqueta en un Bundle para pasarlos de forma segura
     * a la pantalla de detalles.
     *
     * @param exercise El objeto de ejercicio seleccionado por el administrador.
     * @param view     La vista de la tarjeta sobre la que se hizo clic.
     */
    public void exerciseAdminClicked(ExerciseData exercise, View view) {
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
     * Limpia el adaptador del RecyclerView y anula la referencia del View Binding
     * para evitar fugas de memoria (memory leaks) al navegar por la aplicación.
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (binding != null && binding.exercisesEditionRecyclerview != null) {
            binding.exercisesEditionRecyclerview.setAdapter(null);
        }
        adapter = null;
        binding = null;
    }
}

