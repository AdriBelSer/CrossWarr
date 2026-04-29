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
import com.yinya.crosswarr.network.IUsersCallback;

import java.util.ArrayList;

/**
 * Repositorio central de la aplicación (Patrón Repository).
 * Actúa como la única fuente de la verdad para los datos, abstrayendo la lógica de acceso
 * a la base de datos (Firebase) del resto de la aplicación.
 * Implementa el patrón Singleton y utiliza {@link LiveData} para emitir actualizaciones
 * reactivas a las pantallas cuando los datos cambian.
 */
public class Repository {
    private static Repository instance;

    // Pizarras reactivas (MutableLiveData) donde se anotan los datos internamente
    private final MutableLiveData<ArrayList<ExerciseData>> _exercises;
    private final MutableLiveData<ArrayList<ChallengeData>> _challenges;
    private final MutableLiveData<ArrayList<UserData>> _users;

    // Servicios de red
    private FirebaseService firebaseSvc;
    private FirebaseUserService firebaseUserService;
    private FirebaseExerciseService firebaseExerciseService;
    private FirebaseChallengeService firebaseChallengeService;

    /**
     * Constructor privado para forzar el patrón Singleton.
     * Inicializa las conexiones con los servicios de red de Firebase y
     * prepara los LiveData con listas vacías para evitar errores de puntero nulo (NullPointerException).
     */
    private Repository() {
        firebaseSvc = FirebaseService.getInstance();
        firebaseUserService = FirebaseUserService.getInstance(firebaseSvc);
        firebaseExerciseService = FirebaseExerciseService.getInstance(firebaseSvc);
        firebaseChallengeService = FirebaseChallengeService.getInstance(firebaseSvc);

        _exercises = new MutableLiveData<>(new ArrayList<>());
        _challenges = new MutableLiveData<>(new ArrayList<>());
        _users = new MutableLiveData<>(new ArrayList<>());
    }

    /**
     * Obtiene la instancia única y global del Repositorio.
     *
     * @return La instancia Singleton de {@link Repository}.
     */
    public static Repository getInstance() {
        if (instance == null) {
            instance = new Repository();
        }
        return instance;
    }

    // --- GETTERS (Exposición Reactiva) ---

    /**
     * Expone la lista de usuarios como un {@link LiveData} de solo lectura.
     * Esto permite que los ViewModels observen los cambios sin poder modificarlos directamente.
     *
     * @return Un LiveData observable que contiene la lista de usuarios.
     */
    public LiveData<ArrayList<UserData>> getUsersLiveData() {
        return _users;
    }

    /**
     * Expone la lista de ejercicios como un {@link LiveData} de solo lectura.
     *
     * @return Un LiveData observable que contiene la lista de ejercicios.
     */
    public LiveData<ArrayList<ExerciseData>> getExercisesLiveData() {
        return _exercises;
    }

    /**
     * Expone la lista de desafíos como un {@link LiveData} de solo lectura.
     *
     * @return Un LiveData observable que contiene la lista de desafíos.
     */
    public LiveData<ArrayList<ChallengeData>> getChallengesLiveData() {
        return _challenges;
    }

    // --- CREATE (Creación de datos) ---

    /**
     * Delega la creación de un nuevo perfil de usuario al servicio de red correspondiente.
     *
     * @param userData El objeto con los datos del usuario a crear.
     */
    public void createUser(UserData userData) {
        firebaseUserService.createUser(userData);
    }

    /**
     * Delega la creación de un nuevo ejercicio al servicio de red correspondiente.
     *
     * @param exerciseData El objeto con los datos del ejercicio a crear.
     */
    public void createExercise(ExerciseData exerciseData) {
        firebaseExerciseService.createExercise(exerciseData);
    }

    /**
     * Delega la creación de un nuevo desafío al servicio de red correspondiente.
     *
     * @param challengeData El objeto con los datos del desafío a crear.
     */
    public void createChallenge(ChallengeData challengeData) {
        firebaseChallengeService.createChallenge(challengeData);
    }

    // --- READ (Lectura y Sincronización) ---

    /**
     * Descarga la lista completa de usuarios desde Firebase de forma asíncrona.
     * En caso de éxito, actualiza el valor del LiveData usando postValue()
     * para notificar automáticamente a cualquier observador activo en el hilo principal.
     */
    public void fetchUsersFromFirebase() {
        firebaseUserService.fetchUsers(new IUsersCallback() {
            @Override
            public void onSuccess(ArrayList<UserData> listFromFirebase) {
                _users.postValue(listFromFirebase);
            }

            @Override
            public void onFailure(Exception e) {
                // Si hay error, podríamos tener otra pizarra para errores
                Log.e("Repository", "Error descargando usuarios", e);
            }
        });
    }

    /**
     * Descarga la lista completa de ejercicios desde Firebase de forma asíncrona.
     * Al recibir los datos, se postean en el LiveData correspondiente para actualizar la UI.
     */
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

    /**
     * Descarga la lista completa de desafíos desde Firebase de forma asíncrona.
     * Actualiza el LiveData reactivo en caso de éxito.
     */
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

    // --- UPDATE (Actualización) ---

    /**
     * Delega la actualización del estado de uso de un ejercicio al servicio de red.
     *
     * @param exerciseData El ejercicio original que debe marcarse como utilizado.
     */
    public void updateExerciseUsage(ExerciseData exerciseData) {
        firebaseExerciseService.updateExerciseUsage(exerciseData);
    }

    // --- DELETE (Eliminación) ---

    /**
     * Delega la eliminación de un usuario al servicio de red.
     *
     * @param userData El usuario que se desea eliminar.
     */
    public void deleteUser(UserData userData) {
        firebaseUserService.deleteUser(userData);
    }

    /**
     * Delega la eliminación de un ejercicio al servicio de red.
     *
     * @param exerciseData El ejercicio que se desea eliminar.
     */
    public void deleteExercise(ExerciseData exerciseData) {
        firebaseExerciseService.deleteExercise(exerciseData);
    }

    /**
     * Delega la eliminación de un desafío al servicio de red.
     *
     * @param challengeData El desafío que se desea eliminar.
     */
    public void deleteChallenge(ChallengeData challengeData) {
        firebaseChallengeService.deleteChallenge(challengeData);
    }

}