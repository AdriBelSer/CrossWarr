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

public class ChallengesList extends Fragment {

    private FragmentChallengesListBinding binding;
    private ArrayList<ChallengeData> challenges;
    private ChallengesUserViewAdapter adapter;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentChallengesListBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        challenges = new ArrayList<>();
        Map<String, Object> myCompletedChallenges = new HashMap<>();
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
        setupObservers();
        loadChallenge();
    }

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
                                    // Preguntamos si es un Mapa (Usuario antiguo con retos completados)
                                    if (dataFromFirebase.get("challenges") != null) {
                                        Object challengesObj = dataFromFirebase.get("challenges");
                                        if (challengesObj instanceof java.util.Map) {
                                            java.util.Map<String, Object> historyMap = (java.util.Map<String, Object>) challengesObj;
                                            if (adapter != null) {
                                                adapter.setCompletedChallenges(historyMap);
                                            }
                                        }
                                        // Preguntamos si es un ArrayList (Usuario nuevo con historial vacío)
                                        else if (challengesObj instanceof java.util.ArrayList) {
                                            if (adapter != null) {
                                                adapter.setCompletedChallenges(new java.util.HashMap<>());
                                            }
                                        }
                                    }

                                    challenges.clear();

                                    // Filtro
                                    for (ChallengeData challenge : listFromFirebase) {
                                        //FILTRO 1: ¿La fecha de activación ya ha pasado?
                                        if (!challenge.isState()) {
                                            continue;
                                        }
                                        //FILTRO 2: Materiales
                                        // Si no pide material (!requiresEquipment) -> Entra siempre.
                                        // Si pide material -> Solo entra si userHasEquipment es true.
                                        if (!challenge.isRequiresEquipment() || userHasEquipment) {
                                            challenges.add(challenge);
                                        }

                                    }

                                    if (adapter != null) {
                                        adapter.notifyDataSetChanged();
                                        com.yinya.crosswarr.SkeletonUtils.hideSkeleton(
                                                binding.shimmerViewContainer, // Tu ID del esqueleto en el XML
                                                binding.challengesRecyclerview // Tu ID de la lista en el XML
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

    private void loadChallenge() {
        Repository.getInstance().fetchChallengesFromFirebase();
    }

    private void challengeUserClicked(ChallengeData challenge, View view) {
        // Empaquetamos TODOS los datos necesarios para la vista de detalle
        Bundle bundle = new Bundle();
        bundle.putString("id", challenge.getId());
        bundle.putString("title", challenge.getTitle());
        bundle.putString("time", String.valueOf(challenge.getChallenteTime()));
        bundle.putString("exerciseSup", challenge.getExerciseSup());
        bundle.putString("exerciseInf", challenge.getExerciseInf());
        bundle.putString("exerciseCore", challenge.getExerciseCore());
        bundle.putString("state", String.valueOf(challenge.isState()));
        bundle.putString("repetitionSup", String.valueOf(challenge.getRepetitionSup()));
        bundle.putString("repetitionInf", String.valueOf(challenge.getRepetitionInf()));
        bundle.putString("repetitionCore", String.valueOf(challenge.getRepetitionCore()));
        bundle.putString("type", challenge.getType());

        // Navegamos al detalle (Asegúrate de que R.id.challengeDetail es el ID correcto en tu nav_graph)
        Navigation.findNavController(view).navigate(R.id.challengeDetail, bundle);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null; // Buena práctica para evitar fugas de memoria
    }
}