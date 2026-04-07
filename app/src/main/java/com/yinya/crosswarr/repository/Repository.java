package com.yinya.crosswarr.repository;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.yinya.crosswarr.models.ChallengeData;
import com.yinya.crosswarr.models.ExerciseData;
import com.yinya.crosswarr.models.UserData;
import com.yinya.crosswarr.network.FirebaseChallengeService;
import com.yinya.crosswarr.network.FirebaseExerciseService;
import com.yinya.crosswarr.network.FirebaseService;
import com.yinya.crosswarr.network.IExercisesCallback;
import com.yinya.crosswarr.network.models.FirebaseUserService;

import java.util.ArrayList;

public class Repository {
    private static Repository instance;
    private FirebaseService firebaseSvc;
    private FirebaseUserService firebaseUserService;
    private FirebaseExerciseService firebaseExerciseService;
    private FirebaseChallengeService firebaseChallengeService;
    // 1. LA PIZARRA PRIVADA (Mutable: el camarero puede escribir en ella)
    private final MutableLiveData<ArrayList<ExerciseData>> _exercises;


    private Repository() {
        firebaseSvc = FirebaseService.getInstance();
        firebaseUserService = FirebaseUserService.getInstance(firebaseSvc);
        firebaseExerciseService = FirebaseExerciseService.getInstance(firebaseSvc);
        firebaseChallengeService = FirebaseChallengeService.getInstance(firebaseSvc);
        // 1.1. Inicializo la pizarra con un array vacio
        _exercises = new MutableLiveData<>(new ArrayList<>());
    }

    public static Repository getInstance() {
        if (instance == null) {
            instance = new Repository();
        }
        return instance;
    }

    // 2. LA PIZARRA PÚBLICA (Solo lectura: la pantalla solo puede mirar, no escribir)
    public LiveData<ArrayList<ExerciseData>> getExercisesLiveData() {
        return _exercises;
    }

    // Create
    public void createUser(UserData userData) {
        firebaseUserService.createUser(userData);
    }

    public void createExercise(ExerciseData exerciseData) {
        firebaseExerciseService.createExercise(exerciseData);
    }

    public void createChallenge(ChallengeData challengeData) {
        firebaseChallengeService.createChallenge(challengeData);
    }

    // Read

    public void fetchExercisesFromFirebase() {

        // El camarero le pide los datos al cocinero (tu FirebaseExerciseService)
        firebaseExerciseService.fetchExercises(new IExercisesCallback() {
            @Override
            public void onSuccess(ArrayList<ExerciseData> listFromFirebase) {
                // ¡Llegó la comida! La apuntamos en la pizarra.
                // Automáticamente, cualquiera que esté mirando la pizarra se enterará.
                _exercises.postValue(listFromFirebase);
            }

            @Override
            public void onFailure(Exception e) {
                // Si hay error, podríamos tener otra pizarra para errores
                Log.e("Repository", "Error descargando ejercicios", e);
            }
        });
    }

}