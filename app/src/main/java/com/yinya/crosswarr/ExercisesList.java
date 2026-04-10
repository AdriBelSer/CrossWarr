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

import com.yinya.crosswarr.adapters.ExercisesUserViewAdapter;
import com.yinya.crosswarr.adapters.OnExerciseClickListener;
import com.yinya.crosswarr.databinding.FragmentExercisesListBinding;
import com.yinya.crosswarr.models.ExerciseData;
import com.yinya.crosswarr.repository.Repository;

import java.util.ArrayList;

public class ExercisesList extends Fragment {

    private FragmentExercisesListBinding binding; //Binding para el layout
    private ArrayList<ExerciseData> exercises; //Lista de ejercicios
    private ExercisesUserViewAdapter adapter;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflar el layout
        binding = FragmentExercisesListBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        exercises = new ArrayList<>();

        // Configurar el RecyclerView
        adapter = new ExercisesUserViewAdapter(exercises, getContext(), new OnExerciseClickListener() {
            @Override
            public void onExerciseClick(ExerciseData exercise, View view) {
                exerciseUserClicked(exercise, view);
            }
        });
        binding.exercisesRecyclerview.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.exercisesRecyclerview.setAdapter(adapter);


        // Configurar los observer
        setupObservers();

        // Inicializa la lista de ejercicios
        loadExercise(); // Cargar los ejercicios

    }

    // TODO: Poner para que solo le salga al usuario si está isUsed a true (que eso quiere decir que se ha usado en un challenge)

    private void setupObservers() {
        // Observamos la Pizarra (LiveData) de nuestro Repositorio
        // getViewLifecycleOwner() asegura que si la pantalla se cierra, dejamos de mirar la pizarra
        Repository.getInstance().getExercisesLiveData().observe(getViewLifecycleOwner(), listFromFirebase -> {

            // Si la lista que llega de la pizarra no es nula...
            if (listFromFirebase != null) {
                // Borramos lo que hubiera antes en la pantalla
                exercises.clear();

                //TODO: Borrar esto y descomentar lo de abajo para filtrar según se ha usado o no el ejercicio en un challenge
                exercises.addAll(listFromFirebase);
                /*//Filtramos para que solo salgan los que tienen isUsed a true
                for (ExerciseData ex : listFromFirebase) {
                    if (ex.isUsed()) {
                        exercises.add(ex);
                    }
                }*/

                // Le avisamos al adapter que ya tenemos los datos reales filtrados
                if (adapter != null) {
                    adapter.notifyDataSetChanged();
                }
            }
        });
    }


    private void loadExercise() {
        Repository.getInstance().fetchExercisesFromFirebase();
    }

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

}