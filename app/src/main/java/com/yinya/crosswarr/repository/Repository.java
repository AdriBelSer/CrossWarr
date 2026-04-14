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
import com.yinya.crosswarr.network.FirebaseUserService;
import com.yinya.crosswarr.network.IChallengesCallback;
import com.yinya.crosswarr.network.IExercisesCallback;

import java.util.ArrayList;

public class Repository {
    private static Repository instance;
    private final MutableLiveData<ArrayList<ExerciseData>> _exercises;
    private final MutableLiveData<ArrayList<ChallengeData>> _challenges;
    private FirebaseService firebaseSvc;
    private FirebaseUserService firebaseUserService;
    private FirebaseExerciseService firebaseExerciseService;
    private FirebaseChallengeService firebaseChallengeService;


    private Repository() {
        firebaseSvc = FirebaseService.getInstance();
        firebaseUserService = FirebaseUserService.getInstance(firebaseSvc);
        firebaseExerciseService = FirebaseExerciseService.getInstance(firebaseSvc);
        firebaseChallengeService = FirebaseChallengeService.getInstance(firebaseSvc);
        _exercises = new MutableLiveData<>(new ArrayList<>());
        _challenges = new MutableLiveData<>(new ArrayList<>());
    }

    public static Repository getInstance() {
        if (instance == null) {
            instance = new Repository();
        }
        return instance;
    }

    public LiveData<ArrayList<ExerciseData>> getExercisesLiveData() {
        return _exercises;
    }

    public LiveData<ArrayList<ChallengeData>> getChallengesLiveData() {
        return _challenges;
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

    //TODO: Hacer el fetchUsers
    public void fetchUsersFromFirebase() {

    }

    public void fetchExercisesFromFirebase() {

        firebaseExerciseService.fetchExercises(new IExercisesCallback() {
            @Override
            public void onSuccess(ArrayList<ExerciseData> listFromFirebase) {

                _exercises.postValue(listFromFirebase);
            }

            @Override
            public void onFailure(Exception e) {
                // Si hay error, podríamos tener otra pizarra para errores
                Log.e("Repository", "Error descargando ejercicios", e);
            }
        });
    }

    public void fetchChallengesFromFirebase() {

        firebaseChallengeService.fetchChallenges(new IChallengesCallback() {
            @Override
            public void onSuccess(ArrayList<ChallengeData> listFromFirebase) {
                _challenges.postValue(listFromFirebase);
            }

            @Override
            public void onFailure(Exception e) {
                // Si hay error, podríamos tener otra pizarra para errores
                Log.e("Repository", "Error descargando desafíos", e);
            }
        });
    }

    // Update
    public void updateExerciseUsage(ExerciseData exerciseData) {
        firebaseExerciseService.updateExerciseUsage(exerciseData);
    }

    //Delete
/* //TODO: Hacer el deleteUsers
    public void deleteUser(UserData userData) {
        firebaseUserService.deleteUser(userData);
    }*/

    public void deleteExercise(ExerciseData exerciseData) {
        firebaseExerciseService.deleteExercise(exerciseData);
    }

    public void deleteChallenge(ChallengeData challengeData) {
        firebaseChallengeService.deleteChallenge(challengeData);
    }

}