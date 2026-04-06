package com.yinya.crosswarr.network;

import android.util.Log;

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

    public void createExercise() {

    }

    public void fetchExercises() {
        firebaseService.getDocument("crosswarr", "exercises", new IFirebaseCallback() {

            @Override
            public void onSuccess(Map<String, Object> data) {

             /*   ArrayList<ExercisesData> allExercises = new HashMap<>();
                String[] arrayNames = {
                        "upper_body_with_equipment", "upper_body_without_equipment",
                        "lower_body_with_equipment", "lower_body_without_equipment",
                        "core_with_equipment", "core_without_equipment"
                };
                for (String arrayName : arrayNames) {
                    List<Map<String, Object>> firebaseList = (List<Map<String, Object>>) dataFromFirebase.get(arrayName);
                    if (firebaseList != null) {

                    }
                }*/
            }

            @Override
            public void onFailure(Exception e) {
                Log.e("FirebaseExerciseService", e.getMessage());
            }
        });
    }
}