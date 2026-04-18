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
        Repository.getInstance().getExercisesLiveData().observe(getViewLifecycleOwner(), listFromFirebase -> {
            if (listFromFirebase != null) {

                FirebaseUser fUser = FirebaseAuth.getInstance().getCurrentUser();
                if (fUser != null) {
                    com.yinya.crosswarr.network.FirebaseService.getInstance()
                            .getDocument("crosswarr", fUser.getUid(), new com.yinya.crosswarr.network.IFirebaseCallback() {

                                @Override
                                public void onSuccess(java.util.Map<String, Object> dataFromFirebase) {
                                    boolean userHasEquipment = false;

                                    // Extraemos el ajuste useMaterials del usuario
                                    if (dataFromFirebase != null && dataFromFirebase.get("settings") != null) {
                                        java.util.Map<String, Object> settings = (java.util.Map<String, Object>) dataFromFirebase.get("settings");
                                        Boolean useMat = (Boolean) settings.get("useMaterials");
                                        if (useMat != null) userHasEquipment = useMat;
                                    }

                                    exercises.clear();

                                    for (ExerciseData ex : listFromFirebase) {
                                        // FILTRO 1: ¿Está activo (isUsed)? Si no lo está, lo saltamos y pasamos al siguiente.
                                        if (!ex.isUsed()) {
                                            continue;
                                        }

                                        // FILTRO 2: Materiales
                                        boolean exerciseRequiresEquipment = false;
                                        if (ex.getType() != null) {
                                            String type = ex.getType().toLowerCase();
                                            // Truco: Asegurarnos de que contenga "with_equipment" pero NO "without_equipment"
                                            exerciseRequiresEquipment = type.contains("with_equipment") && !type.contains("without_equipment");
                                        }

                                        // Si el ejercicio NO requiere material, entra.
                                        // Si SÍ requiere material, solo entra si el usuario marcó "useMaterials" en su perfil.
                                        if (!exerciseRequiresEquipment || userHasEquipment) {
                                            exercises.add(ex);
                                        }
                                    }

                                    if (adapter != null) {
                                        adapter.notifyDataSetChanged();
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