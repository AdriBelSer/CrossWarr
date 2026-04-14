package com.yinya.crosswarr.network;

import android.util.Log;

import com.yinya.crosswarr.models.ExerciseData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FirebaseExerciseService {

    private static FirebaseExerciseService instance;
    FirebaseService firebaseService;

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
        HashMap<String, Object> exerciseMap = exerciseData.asFirebaseExerciseData().asHashMap();
        String targetArrayName =  exerciseData.getType();

        firebaseService = FirebaseService.getInstance();
        firebaseService.addElementToArray(
                "crosswarr",
                "exercises",
                targetArrayName,
                exerciseMap,
                aVoid -> {
                    // Éxito
                    Log.d("FirebaseExerciseService", "Ejercicio añadido correctamente al array: " + targetArrayName);
                },
                e -> {
                    // Fallo
                    Log.e("FirebaseExerciseService", "Error al añadir el ejercicio", e);
                }
        );

    }

    public void fetchExercises(IExercisesCallback callback) {
        firebaseService.getDocument("crosswarr", "exercises", new IFirebaseCallback() {

            @Override
            public void onSuccess(Map<String, Object> dataFromFirebase) {

                ArrayList<ExerciseData> allExercises = new ArrayList<>();

                String[] arrayNames = {
                        "upper_body_with_equipment", "upper_body_without_equipment",
                        "lower_body_with_equipment", "lower_body_without_equipment",
                        "core_with_equipment", "core_without_equipment"
                };

                for (String arrayName : arrayNames) {
                    List<Map<String, Object>> firebaseList = (List<Map<String, Object>>) dataFromFirebase.get(arrayName);

                    if (firebaseList != null) {
                        for (Map<String, Object> map : firebaseList) {
                            ExerciseData ex = new ExerciseData();
                            ex.setId((String) map.get("id"));
                            ex.setName((String) map.get("name"));
                            ex.setDescription((String) map.get("description"));
                            ex.setType((String) map.get("type"));
                            ex.setImage((String) map.get("image"));
                            ex.setVideo((String) map.get("video"));
                            ex.setMaterials((ArrayList<String>) map.get("materials"));
                            Boolean isUsed = (Boolean) map.get("isUsed");
                            ex.setUsed(isUsed != null ? isUsed : false);

                            allExercises.add(ex);
                        }
                    }
                }

                callback.onSuccess(allExercises);
            }

            @Override
            public void onFailure(Exception e) {
                callback.onFailure(e);
            }
        });
    }

    public void updateExerciseUsage(ExerciseData oldExercise) {
        // 1. Guardamos una foto de cómo era el ejercicio VIEJO para que Firebase sepa cuál borrar
        HashMap<String, Object> oldMap = oldExercise.asFirebaseExerciseData().asHashMap();
        String targetArray = oldExercise.getType();

        // 2. Le cambiamos el estado a TRUE y sacamos una foto del ejercicio NUEVO
        oldExercise.setUsed(true);
        HashMap<String, Object> newMap = oldExercise.asFirebaseExerciseData().asHashMap();

        // 3. Magia: Borramos el viejo, y si sale bien, metemos el nuevo
        firebaseService.removeElementFromArray(
                "crosswarr",
                "exercises",
                targetArray,
                oldMap,
                aVoid -> {
                    // ¡Se borró el viejo! Ahora metemos el actualizado
                    firebaseService.addElementToArray(
                            "crosswarr",
                            "exercises",
                            targetArray,
                            newMap,
                            aVoid2 -> android.util.Log.d("FirebaseExercise", "Ejercicio actualizado a isUsed=true"),
                            e2 -> android.util.Log.e("FirebaseExercise", "Error añadiendo el nuevo", e2)
                    );
                },
                e -> android.util.Log.e("FirebaseExercise", "Error borrando el viejo", e)
        );
    }

    public void deleteExercise(ExerciseData exerciseData) {
        // Convertimos el ejercicio de vuelta a mapa para que Firebase lo reconozca
        HashMap<String, Object> exerciseMap = exerciseData.asFirebaseExerciseData().asHashMap();
        String targetArrayName = exerciseData.getType(); // "inf", "sup" o "core"

        firebaseService.removeElementFromArray(
                "crosswarr",
                "exercises",
                targetArrayName,
                exerciseMap,
                aVoid -> {
                    android.util.Log.d("FirebaseExerciseService", "Ejercicio borrado de Firebase: " + exerciseData.getName());
                },
                e -> {
                    android.util.Log.e("FirebaseExerciseService", "Error al borrar el ejercicio", e);
                }
        );
    }
}