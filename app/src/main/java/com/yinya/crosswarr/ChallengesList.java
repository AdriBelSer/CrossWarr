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

import com.yinya.crosswarr.adapters.ChallengesUserViewAdapter;
import com.yinya.crosswarr.adapters.OnChallengeClickListener;
import com.yinya.crosswarr.databinding.FragmentChallengesListBinding;
import com.yinya.crosswarr.models.ChallengeData;
import com.yinya.crosswarr.repository.Repository;

import java.util.ArrayList;

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

        adapter = new ChallengesUserViewAdapter(challenges, getContext(), new OnChallengeClickListener() {
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
                challenges.clear();

                //TODO: Aquí en el futuro se pueden filtrar los challenges (ej: si state es true/false)
                //TODO: Borrar esto y descomentar lo de abajo para filtrar según se ha usado o no el ejercicio en un challenge
                challenges.addAll(listFromFirebase);

                if (adapter != null) {
                    adapter.notifyDataSetChanged();
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

        // Las fechas Timestamp normalmente no se pasan enteras,
        // pero si las necesitas en la otra vista, las pasamos como String o milisegundos.
        // bundle.putString("creationDate", challenge.getCreationDate().toDate().toString());

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