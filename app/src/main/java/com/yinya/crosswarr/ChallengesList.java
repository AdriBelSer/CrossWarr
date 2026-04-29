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
import com.yinya.crosswarr.adapters.ChallengesUserViewAdapter;
import com.yinya.crosswarr.adapters.OnChallengeClickListener;
import com.yinya.crosswarr.databinding.FragmentChallengesListBinding;
import com.yinya.crosswarr.models.ChallengeData;
import com.yinya.crosswarr.repository.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Fragmento principal de la aplicación para los usuarios estándar.
 * Se encarga de mostrar el "feed" o lista de entrenamientos disponibles.
 * Implementa una lógica de filtrado avanzada para mostrar únicamente los desafíos activos
 * y cruzarlos con las preferencias del usuario (uso de material) y su historial personal
 * (para marcar los retos ya completados).
 */
public class ChallengesList extends Fragment {

    private FragmentChallengesListBinding binding;
    private ArrayList<ChallengeData> challenges;
    private ChallengesUserViewAdapter adapter;

    /**
     * Invocado para inflar el diseño XML asociado a este fragmento utilizando View Binding.
     *
     * @param inflater           El objeto LayoutInflater utilizado para inflar las vistas en el contexto.
     * @param container          El ViewGroup padre en el que se insertará la vista del fragmento.
     * @param savedInstanceState Si no es null, este fragmento está siendo reconstruido.
     * @return La vista raíz (View) del diseño inflado.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentChallengesListBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    /**
     * Invocado inmediatamente después de que onCreateView ha retornado.
     * Configura la intercepción del botón hardware "atrás", inicializa la lista, configura el
     * adaptador para la vista de usuario y vincula el RecyclerView. También dispara la petición de
     * carga de datos y establece los observadores.
     *
     * @param view               La Vista devuelta por onCreateView().
     * @param savedInstanceState Si no es null, el fragmento está siendo reconstruido.
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        challenges = new ArrayList<>();
        Map<String, Object> myCompletedChallenges = new HashMap<>();

        // Configuración del adaptador del usuario con su respectivo listener de clics
        adapter = new ChallengesUserViewAdapter(challenges, myCompletedChallenges, getContext(), new OnChallengeClickListener() {
            @Override
            public void onChallengeClick(ChallengeData challenge, View view) {
                challengeUserClicked(challenge, view);
            }
        });

        if (binding.challengesRecyclerview != null) {
            binding.challengesRecyclerview.setLayoutManager(new LinearLayoutManager(getContext()));
            binding.challengesRecyclerview.setAdapter(adapter);
        }

        // LÓGICA DE NEGOCIO: Bloqueo del botón físico "Atrás" del dispositivo.
        // Se añade un callback vacío para absorber el evento y evitar que el usuario
        // salga de la lista de desafíos por accidente.
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                // Se deja intencionalmente vacío para que no haga nada al pulsar "atrás"
            }
        });
        setupObservers();
        loadChallenge();
    }

    /**
     * Configura el motor reactivo de la pantalla.
     * Se suscribe al LiveData de desafíos del Repositorio. Cuando los datos llegan, hace una llamada
     * adicional a Firebase para descargar el perfil exacto del usuario activo.
     * Con ambas fuentes de datos, aplica filtros de negocio:
     * 1. Descarta los retos cuya fecha de activación aún no ha llegado.
     * 2. Descarta los retos con equipo si el usuario configuró en sus ajustes que no tiene material.
     * 3. Sincroniza el historial de retos completados para mostrar indicadores visuales en la interfaz.
     */
    private void setupObservers() {
        Repository.getInstance().getChallengesLiveData().observe(getViewLifecycleOwner(), listFromFirebase -> {
            if (listFromFirebase != null) {

                FirebaseUser fUser = FirebaseAuth.getInstance().getCurrentUser();
                if (fUser != null) {
                    // Descargamos el perfil del usuario actual para ver sus settings
                    com.yinya.crosswarr.network.FirebaseService.getInstance()
                            .getDocument("crosswarr", fUser.getUid(), new com.yinya.crosswarr.network.IFirebaseCallback() {

                                @Override
                                public void onSuccess(java.util.Map<String, Object> dataFromFirebase) {
                                    if (binding == null) return;
                                    boolean userHasEquipment = false; // Asumimos que no tiene por defecto

                                    // Extraemos el ajuste useMaterials
                                    if (dataFromFirebase != null && dataFromFirebase.get("settings") != null) {
                                        java.util.Map<String, Object> settings = (java.util.Map<String, Object>) dataFromFirebase.get("settings");
                                        Boolean useMat = (Boolean) settings.get("useMaterials");
                                        if (useMat != null) userHasEquipment = useMat;
                                    }
                                    // Sincronización del historial de retos completados (Manejo seguro de tipos de datos)
                                    if (dataFromFirebase.get("challenges") != null) {
                                        Object challengesObj = dataFromFirebase.get("challenges");

                                        // Si es un Mapa (Usuario antiguo/activo con retos completados)
                                        if (challengesObj instanceof java.util.Map) {
                                            java.util.Map<String, Object> historyMap = (java.util.Map<String, Object>) challengesObj;
                                            if (adapter != null) {
                                                adapter.setCompletedChallenges(historyMap);
                                            }
                                        }
                                        // Si es un ArrayList (Usuario nuevo inicializado con array vacío por defecto)
                                        else if (challengesObj instanceof java.util.ArrayList) {
                                            if (adapter != null) {
                                                adapter.setCompletedChallenges(new java.util.HashMap<>());
                                            }
                                        }
                                    }

                                    challenges.clear();

                                    // Iteración y aplicación de filtros de negocio
                                    for (ChallengeData challenge : listFromFirebase) {
                                        //Filtro 1: ¿El reto ya está activo según su fecha?
                                        if (!challenge.isState()) {
                                            continue;// Si es futuro, lo ignoramos y pasamos al siguiente
                                        }
                                        //Filtro 2: Materiales
                                        // Si no pide material (!requiresEquipment), entra siempre.
                                        // Si pide material, solo entra si userHasEquipment es true.
                                        if (!challenge.isRequiresEquipment() || userHasEquipment) {
                                            challenges.add(challenge);
                                        }
                                    }

                                    // Actualizar la interfaz y detener la animación de carga
                                    if (adapter != null) {
                                        adapter.notifyDataSetChanged();
                                        com.yinya.crosswarr.SkeletonUtils.hideSkeleton(
                                                binding.shimmerViewContainer, // ID del contenedor de animación (Skeleton)
                                                binding.challengesRecyclerview //ID de la lista en el XML
                                        );
                                    }
                                }

                                @Override
                                public void onFailure(Exception e) {
                                    if (binding == null) return;
                                    android.util.Log.e("ChallengesList", "Error al traer datos del usuario", e);
                                    com.yinya.crosswarr.SkeletonUtils.hideSkeleton(
                                            binding.shimmerViewContainer,
                                            binding.challengesRecyclerview
                                    );
                                }
                            });
                }
            }
        });
    }

    /**
     * Solicita al Repositorio que inicie la descarga de los desafíos generales desde Firebase.
     * Esta acción disparará una actualización en el LiveData que será capturada por `setupObservers`.
     */
    private void loadChallenge() {
        Repository.getInstance().fetchChallengesFromFirebase();
    }

    /**
     * Navega a la vista de detalle de entrenamiento (ChallengeDetail) cuando el usuario
     * pulsa sobre una tarjeta de desafío de la lista.
     *
     * @param challenge El objeto con la información del desafío seleccionado.
     * @param view      La vista de la tarjeta pulsada.
     */
    private void challengeUserClicked(ChallengeData challenge, View view) {
        // Empaquetamos todos los datos necesarios para la vista de detalle convirtiendo
        // los tipos numéricos a String para evitar NullPointerExceptions en el Bundle destino.
        Bundle bundle = new Bundle();
        bundle.putString("id", challenge.getId());
        bundle.putString("title", challenge.getTitle());
        bundle.putString("time", String.valueOf(challenge.getChallengeTime()));
        bundle.putString("exerciseSup", challenge.getExerciseSup());
        bundle.putString("exerciseInf", challenge.getExerciseInf());
        bundle.putString("exerciseCore", challenge.getExerciseCore());
        bundle.putString("state", String.valueOf(challenge.isState()));
        bundle.putString("repetitionSup", String.valueOf(challenge.getRepetitionSup()));
        bundle.putString("repetitionInf", String.valueOf(challenge.getRepetitionInf()));
        bundle.putString("repetitionCore", String.valueOf(challenge.getRepetitionCore()));
        bundle.putString("type", challenge.getType());

        // Navegamos al detalle del desafío
        Navigation.findNavController(view).navigate(R.id.challengeDetail, bundle);
    }

    /**
     * Invocado cuando la vista del fragmento va a ser destruida.
     * Libera el adaptador del RecyclerView y anula la referencia al View Binding
     * para prevenir memory leaks (fugas de memoria) al navegar entre pestañas.
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (binding != null && binding.challengesRecyclerview != null) {
            binding.challengesRecyclerview.setAdapter(null);
        }
        adapter = null;
        binding = null;
    }
}