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

import com.yinya.crosswarr.adapters.ChallengesEditionViewAdapter;
import com.yinya.crosswarr.adapters.OnChallengeAdminListener;
import com.yinya.crosswarr.databinding.FragmentChallengesEditionListBinding;
import com.yinya.crosswarr.models.ChallengeData;
import com.yinya.crosswarr.models.ExerciseData;
import com.yinya.crosswarr.repository.Repository;

import java.util.ArrayList;

public class ChallengesEditionList extends Fragment {
    private FragmentChallengesEditionListBinding binding;
    private ArrayList<ChallengeData> challenges;
    private ChallengesEditionViewAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentChallengesEditionListBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        challenges = new ArrayList<>();
        adapter = new ChallengesEditionViewAdapter(challenges, getContext(), new OnChallengeAdminListener() {
            @Override
            public void onChallengeClick(ChallengeData challenge, View view) {
                challengeAdminClicked(challenge, view);
            }

            @Override
            public void onDeleteClick(ChallengeData challenge) {
                // Comprobamos que tenga fecha
                if (challenge.getActivationDate() == null) {
                    android.widget.Toast.makeText(requireContext(),
                            "Error: El desafío no tiene fecha de activación y no se puede verificar.",
                            android.widget.Toast.LENGTH_SHORT).show();
                    return; // Cortamos la ejecución aquí
                }

                // Convertimos a Date de Java y comparamos
                java.util.Date activationDate = challenge.getActivationDate().toDate();
                java.util.Date currentDate = new java.util.Date();

                if (activationDate.before(currentDate)) {
                    // Ya es pasado, no se puede borrar
                    android.widget.Toast.makeText(requireContext(),
                            "No se puede borrar: Este desafío ya ha sido publicado.",
                            android.widget.Toast.LENGTH_LONG).show();

                } else {
                    // Es futuro, pedimos confirmación
                    new android.app.AlertDialog.Builder(requireContext())
                            .setTitle("Borrar Challenge")
                            .setMessage("¿Estás seguro de que quieres borrar el challenge '" + challenge.getTitle() + "'?")
                            .setPositiveButton("Sí, borrar", (dialog, which) -> {
                                Repository.getInstance().deleteChallenge(challenge);
                                challenges.remove(challenge);
                                adapter.notifyDataSetChanged();

                                android.widget.Toast.makeText(requireContext(), "Desafío borrado", android.widget.Toast.LENGTH_SHORT).show();
                            })
                            .setNegativeButton("Cancelar", null)
                            .show();
                }
            }
        });
        binding.challengesEditionRecyclerview.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.challengesEditionRecyclerview.setAdapter(adapter);
        if (binding.btnNewChallenge != null) {
            binding.btnNewChallenge.setOnClickListener(v -> {
                // Navegamos a la vista de edición sin pasarle datos (modo creación)
                Navigation.findNavController(v).navigate(R.id.challengesEdition);
            });
        }
        setupObservers();
        loadChallenge();
    }
    private void setupObservers() {
        Repository.getInstance().getChallengesLiveData().observe(getViewLifecycleOwner(), listFromFirebase -> {
            if (listFromFirebase != null) {
                challenges.clear();
                challenges.addAll(listFromFirebase);
                if (adapter != null) {
                    adapter.notifyDataSetChanged();
                    com.yinya.crosswarr.SkeletonUtils.hideSkeleton(
                            binding.shimmerViewContainer,
                            binding.challengesEditionRecyclerview
                    );
                }
            }
        });
    }
    private void loadChallenge () {
        Repository.getInstance().fetchChallengesFromFirebase();
    }
    public void challengeAdminClicked(ChallengeData challenge, View view) {
        Bundle bundle = new Bundle();
        bundle.putString("id", challenge.getId());
        bundle.putString("title", challenge.getTitle());
        // Convertimos los números a String para evitar NullPointerExceptions en el destino
        bundle.putString("time", String.valueOf(challenge.getChallenteTime()));
        bundle.putString("exerciseSup", challenge.getExerciseSup());
        bundle.putString("exerciseInf", challenge.getExerciseInf());
        bundle.putString("exerciseCore", challenge.getExerciseCore());
        bundle.putString("state", String.valueOf(challenge.isState()));
        bundle.putString("repetitionSup", String.valueOf(challenge.getRepetitionSup()));
        bundle.putString("repetitionInf", String.valueOf(challenge.getRepetitionInf()));
        bundle.putString("repetitionCore", String.valueOf(challenge.getRepetitionCore()));
        bundle.putString("type", challenge.getType());

        // Navegar al ChallengeDetailFragment con el Bundle
        Navigation.findNavController(view).navigate(R.id.challengeDetail, bundle);
    }
}
