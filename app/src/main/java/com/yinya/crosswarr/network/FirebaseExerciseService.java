package com.yinya.crosswarr.network;

import android.util.Log;

import com.yinya.crosswarr.models.ExerciseData;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FirebaseExerciseService {

    private static FirebaseExerciseService instance;
    FirebaseService firebaseService;
    ExerciseData exerciseData;

    public FirebaseExerciseService(FirebaseService firebaseService) {
        this.firebaseService = firebaseService;
    }

    public static FirebaseExerciseService getInstance(FirebaseService firebaseService) {
        if (instance == null) {
            instance = new FirebaseExerciseService(firebaseService);
        }
        return instance;
    }
    // Métodos CRUD ejercicios

    public void createExercise(ExerciseData exerciseData) {
        firebaseService = FirebaseService.getInstance();
        firebaseService.createDocumentWithId("crosswarr",
                exerciseData.getId(), exerciseData.asFirebaseExerciseData().asHashMap());

    }

    public void fetchExercises(IExercisesCallback callback) {
        firebaseService.getDocument("crosswarr", "exercises", new IFirebaseCallback() {

                @Override
                public void onSuccess(Map<String, Object> dataFromFirebase) {
                    // El cocinero recibe el Map crudo y lo "cocina"
                    ArrayList<ExerciseData> allExercises = new ArrayList<>();
                    // 1. Definimos los nombres de los 6 arrays que tienes en tu base de datos
                    String[] arrayNames = {
                            "upper_body_with_equipment", "upper_body_without_equipment",
                            "lower_body_with_equipment", "lower_body_without_equipment",
                            "core_with_equipment", "core_without_equipment"
                    };

                    // 2. Hacemos un bucle para revisar cada uno de los 6 arrays
                    for (String arrayName : arrayNames) {

                        // Sacamos la lista de ejercicios crudos (Mapas) de este array
                        List<Map<String, Object>> firebaseList = (List<Map<String, Object>>) dataFromFirebase.get(arrayName);

                        if (firebaseList != null) {
                            // Recorremos cada ejercicio individual dentro del array
                            for (Map<String, Object> map : firebaseList) {

                                // Fabricamos un objeto limpio y le metemos los datos
                                ExerciseData ex = new ExerciseData();
                                ex.setId((String) map.get("id"));
                                ex.setName((String) map.get("name"));
                                ex.setDescription((String) map.get("description"));
                                ex.setType((String) map.get("type"));
                                ex.setImage((String) map.get("image"));
                                ex.setVideo((String) map.get("video"));
                                ex.setMaterials((ArrayList<String>) map.get("materials"));

                                // Boolean requiere un cuidado especial porque a veces Firebase lo devuelve null
                                Boolean isUsed = (Boolean) map.get("isUsed");
                                ex.setUsed(isUsed != null ? isUsed : false);

                                // Metemos el ejercicio ya cocinado en nuestra lista final
                                allExercises.add(ex);
                            }
                        }
                    }

                    // ¡Plato listo! Llama al buscapersonas del Camarero y le da la lista
                    callback.onSuccess(allExercises);
                }

                @Override
                public void onFailure(Exception e) {
                    callback.onFailure(e);
                }
            });
        }
}