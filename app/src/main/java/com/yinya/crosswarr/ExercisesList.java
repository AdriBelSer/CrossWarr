package com.yinya.crosswarr;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.yinya.crosswarr.adapters.ExercisesUserViewAdapter;
import com.yinya.crosswarr.databinding.FragmentExercisesListBinding;
import com.yinya.crosswarr.models.ExercisesData;

import java.util.ArrayList;
import java.util.Arrays;

public class ExercisesList extends Fragment {

    private FragmentExercisesListBinding binding; //Binding para el layout
    private ArrayList<ExercisesData> exercises; //Lista de ejercicios
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
        adapter = new ExercisesUserViewAdapter(exercises, getActivity());
        binding.exercisesRecyclerview.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.exercisesRecyclerview.setAdapter(adapter);

        // Inicializa la lista de ejercicios
        loadExercise(); // Cargar los ejercicios

    }

    // TODO Obtener los ejercicios de firebase (Estos son datos de prueba temporal)
    private void loadExercise() {
        // Ejercicio 1: Tren superior
        exercises.add(new ExercisesData(
                "Flexiones",
                "Flexiones de pecho clásicas para trabajar pectorales, hombros y tríceps.",
                "Tren superior",
                "https://drive.google.com/uc?export=view&id=13VdPaDWcVhwn8yBxzN5eqxxTjT4qCu3j",
                "https://www.youtube.com/shorts/cWrJFIdTje0",
                new ArrayList<>(Arrays.asList("Esterilla", "Tu propio peso")) // Lista de materiales
        ));

        // Ejercicio 2: Tren inferior
        exercises.add(new ExercisesData(
                "Sentadillas.",
                "Sentadillas profundas manteniendo la espalda recta para piernas y glúteos",
                "Tren inferior",
                "https://drive.google.com/uc?export=view&id=1QeaHGIKegtECayfTSpAsPQ3GQOX9hSlO",
                "https://www.youtube.com/shorts/UbIClfnHOuw",
                new ArrayList<>(Arrays.asList("Tu propio peso")) // Lista de materiales
        ));


        // Ejercicio 3: Core (Marcado como no disponible para probar el boolean)
        exercises.add(new ExercisesData(
                "Plancha abdominal.",
                "Plancha isométrica apoyando los antebrazos para fortalecer el abdomen",
                "Core",
                "https://drive.google.com/uc?export=view&id=13gaDrzBfGscCT6tqO5xbo5XRTT_yqPIu",
                "https://www.youtube.com/shorts/odo0h50hfwY",
                new ArrayList<>(Arrays.asList("Tu propio peso")) // Lista de materiales
        ));

        // Avisar al adaptador de que la lista ha cambiado para que dibuje los nuevos elementos
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }


}