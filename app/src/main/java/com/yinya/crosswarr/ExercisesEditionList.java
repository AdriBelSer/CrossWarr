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

public class ExercisesEditionList extends Fragment {
    private FragmentExercisesEditionListBinding binding;
    private ArrayList<ExerciseData> exercises;
    private ExercisesEditionViewAdapter adapter;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentExercisesEditionListBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        exercises = new ArrayList<>();
        adapter = new ExercisesEditionViewAdapter(exercises, getContext(), new OnExerciseAdminListener() {
            @Override
            public void onExerciseClick(ExerciseData exercise, View view) {
                exerciseAdminClicked(exercise, view);
            }

            @Override
            public void onDeleteClick(ExerciseData exercise) {
                if (exercise.isUsed()) {
                    android.widget.Toast.makeText(getContext(),
                            R.string.exercises_edition_list_btn_delete_error,
                            android.widget.Toast.LENGTH_LONG).show();
                } else {
                    new android.app.AlertDialog.Builder(getContext())
                            .setTitle(R.string.exercises_edition_list_btn_delete_title)
                            .setMessage(requireContext().getString(R.string.exercises_edition_list_btn_delete_message))
                            .setPositiveButton(R.string.exercises_edition_list_btn_delete_yes, (dialog, which) -> {

                                // 1. Ordenamos a Firebase que lo borre
                                Repository.getInstance().deleteExercise(exercise);

                                // 2. Lo quitamos de la lista visual al momento
                                exercises.remove(exercise);
                                adapter.notifyDataSetChanged();

                                android.widget.Toast.makeText(getContext(), R.string.exercises_edition_list_btn_delete_success, android.widget.Toast.LENGTH_SHORT).show();
                            })
                            .setNegativeButton(R.string.exercises_edition_list_btn_delete_cancel, null)
                            .show();
                }
            }
        });


        binding.exercisesEditionRecyclerview.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.exercisesEditionRecyclerview.setAdapter(adapter);
        if (binding.btnNewExercise != null) {
            binding.btnNewExercise.setOnClickListener(v -> {
                // Navegamos a la vista de edición sin pasarle datos (modo creación)
                Navigation.findNavController(v).navigate(R.id.exercisesEdition);
            });
        }

        setupObservers();
        loadExercise();
    }

    private void setupObservers() {
        Repository.getInstance().getExercisesLiveData().observe(getViewLifecycleOwner(), listFromFirebase -> {
            if (listFromFirebase != null) {
                exercises.clear();
                exercises.addAll(listFromFirebase);
                if (adapter != null) {
                    adapter.notifyDataSetChanged();
                    com.yinya.crosswarr.SkeletonUtils.hideSkeleton(
                            binding.shimmerViewContainer,
                            binding.exercisesEditionRecyclerview
                    );
                }
            }
        });
    }

    private void loadExercise() {
        Repository.getInstance().fetchExercisesFromFirebase();
    }

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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}

