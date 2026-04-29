package com.yinya.crosswarr.models;

import com.google.firebase.Timestamp;
import com.yinya.crosswarr.network.models.FirebaseChallengeData;

/**
 * Modelo de datos que representa un Desafío (Reto) dentro de la aplicación.
 * Esta clase se utiliza a nivel local en la lógica de negocio y en la interfaz de usuario (UI)
 * para manejar toda la información relacionada con un entrenamiento (ejercicios, tiempos, repeticiones).
 */
public class ChallengeData {
    private String id;
    private String title;
    private Timestamp creationDate;
    private Timestamp activationDate;
    private int challengeTime;
    private String exerciseSup;
    private String exerciseInf;
    private String exerciseCore;
    private boolean state;
    private int repetitionSup;
    private int repetitionInf;
    private int repetitionCore;
    private String type;
    private boolean requiresEquipment;

    /**
     * Constructor vacío por defecto.
     * Es estrictamente necesario para que Firebase Firestore pueda deserializar
     * los documentos de la base de datos y convertirlos en objetos Java automáticamente.
     */
    public ChallengeData() {
    }

    /**
     * Constructor parcial para un desafío (versión simplificada sin repeticiones ni equipamiento).
     *
     * @param id             Identificador único del desafío.
     * @param title          Título o nombre del desafío.
     * @param creationDate   Fecha y hora en la que se creó el registro.
     * @param activationDate Fecha y hora en la que el desafío pasa a estar disponible para los usuarios.
     * @param challengeTime  Duración total del desafío (generalmente en minutos).
     * @param exerciseSup    Nombre del ejercicio de tren superior.
     * @param exerciseInf    Nombre del ejercicio de tren inferior.
     * @param exerciseCore   Nombre del ejercicio de core (abdomen).
     * @param state          Estado actual del desafío (activo/inactivo o pasado/futuro).
     * @param type           Tipo de entrenamiento (ej: "amrap", "emom", "ft").
     */
    public ChallengeData(String id, String title, Timestamp creationDate, Timestamp activationDate, int challengeTime, String exerciseSup, String exerciseInf, String exerciseCore, boolean state, String type) {
        this.id = id;
        this.title = title;
        this.creationDate = creationDate;
        this.activationDate = activationDate;
        this.challengeTime = challengeTime;
        this.exerciseSup = exerciseSup;
        this.exerciseInf = exerciseInf;
        this.exerciseCore = exerciseCore;
        this.state = state;
        this.type = type;
    }

    /**
     * Constructor completo para un desafío, incluyendo repeticiones y requerimiento de material.
     *
     * @param id                Identificador único del desafío.
     * @param title             Título o nombre del desafío.
     * @param creationDate      Fecha en la que se creó en la base de datos.
     * @param activationDate    Fecha a partir de la cual el reto es visible/jugable.
     * @param challengeTime     Duración total del reto en minutos.
     * @param exerciseSup       Ejercicio para el tren superior.
     * @param exerciseInf       Ejercicio para el tren inferior.
     * @param exerciseCore      Ejercicio para la zona media (core).
     * @param state             Estado booleano que indica si está activo.
     * @param repetitionSup     Número de repeticiones para el ejercicio superior.
     * @param repetitionInf     Número de repeticiones para el ejercicio inferior.
     * @param repetitionCore    Número de repeticiones para el ejercicio de core.
     * @param type              Tipo de modalidad (ej: "amrap", "ft", "emom").
     * @param requiresEquipment Booleano que indica si se necesita material para completarlo.
     */
    public ChallengeData(String id, String title, Timestamp creationDate, Timestamp activationDate, int challengeTime, String exerciseSup, String exerciseInf, String exerciseCore, boolean state, int repetitionSup, int repetitionInf, int repetitionCore, String type, boolean requiresEquipment) {
        this.id = id;
        this.title = title;
        this.creationDate = creationDate;
        this.activationDate = activationDate;
        this.challengeTime = challengeTime;
        this.state = state;
        this.exerciseSup = exerciseSup;
        this.exerciseInf = exerciseInf;
        this.exerciseCore = exerciseCore;
        this.repetitionSup = repetitionSup;
        this.repetitionInf = repetitionInf;
        this.repetitionCore = repetitionCore;
        this.type = type;
        this.requiresEquipment = requiresEquipment;
    }

    // --- GETTERS Y SETTERS ---

    /**
     * @return El identificador único del desafío.
     */
    public String getId() {
        return id;
    }

    /**
     * @param id El nuevo identificador a asignar.
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * @return El título del desafío.
     */
    public String getTitle() {
        return title;
    }

    /**
     * @param title El nuevo título a asignar.
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * @return El Timestamp de Firebase indicando cuándo se creó el desafío.
     */
    public Timestamp getCreationDate() {
        return creationDate;
    }

    /**
     * @param creationDate El nuevo Timestamp de creación.
     */
    public void setCreationDate(Timestamp creationDate) {
        this.creationDate = creationDate;
    }

    /**
     * @return El Timestamp de Firebase indicando cuándo se activa el desafío.
     */
    public Timestamp getActivationDate() {
        return activationDate;
    }

    /**
     * @param activationDate El nuevo Timestamp de activación.
     */
    public void setActivationDate(Timestamp activationDate) {
        this.activationDate = activationDate;
    }

    /**
     * @return El tiempo asignado para completar el desafío (generalmente en minutos).
     */
    public int getChallengeTime() {
        return challengeTime;
    }

    /**
     * @param challengeTime El nuevo tiempo del desafío.
     */
    public void setChallengeTime(int challengeTime) {
        this.challengeTime = challengeTime;
    }

    /**
     * @return El nombre del ejercicio asignado al tren superior.
     */
    public String getExerciseSup() {
        return exerciseSup;
    }

    /**
     * @param exerciseSup El nuevo ejercicio de tren superior.
     */
    public void setExerciseSup(String exerciseSup) {
        this.exerciseSup = exerciseSup;
    }

    /**
     * @return El nombre del ejercicio asignado al tren inferior.
     */
    public String getExerciseInf() {
        return exerciseInf;
    }

    /**
     * @param exerciseInf El nuevo ejercicio de tren inferior.
     */
    public void setExerciseInf(String exerciseInf) {
        this.exerciseInf = exerciseInf;
    }

    /**
     * @return El nombre del ejercicio asignado al core.
     */
    public String getExerciseCore() {
        return exerciseCore;
    }

    /**
     * @param exerciseCore El nuevo ejercicio de core.
     */
    public void setExerciseCore(String exerciseCore) {
        this.exerciseCore = exerciseCore;
    }

    /**
     * @return El estado de disponibilidad del desafío (true = activo).
     */
    public boolean isState() {
        return state;
    }

    /**
     * @param state El nuevo estado del desafío.
     */
    public void setState(boolean state) {
        this.state = state;
    }

    /**
     * @return Las repeticiones requeridas para el ejercicio de tren superior.
     */
    public int getRepetitionSup() {
        return repetitionSup;
    }

    /**
     * @param repetitionSup El nuevo número de repeticiones de tren superior.
     */
    public void setRepetitionSup(int repetitionSup) {
        this.repetitionSup = repetitionSup;
    }

    /**
     * @return Las repeticiones requeridas para el ejercicio de tren inferior.
     */
    public int getRepetitionInf() {
        return repetitionInf;
    }

    /**
     * @param repetitionInf El nuevo número de repeticiones de tren inferior.
     */
    public void setRepetitionInf(int repetitionInf) {
        this.repetitionInf = repetitionInf;
    }

    /**
     * @return Las repeticiones requeridas para el ejercicio de core.
     */
    public int getRepetitionCore() {
        return repetitionCore;
    }

    /**
     * @param repetitionCore El nuevo número de repeticiones de core.
     */
    public void setRepetitionCore(int repetitionCore) {
        this.repetitionCore = repetitionCore;
    }

    /**
     * @return La modalidad o tipo de entrenamiento (AMRAP, EMOM, FT).
     */
    public String getType() {
        return type;
    }

    /**
     * @param type El nuevo tipo de entrenamiento.
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * @return True si el desafío requiere equipamiento extra, false en caso contrario.
     */
    public boolean isRequiresEquipment() {
        return requiresEquipment;
    }

    /**
     * @param requiresEquipment El nuevo estado de requerimiento de material.
     */
    public void setRequiresEquipment(boolean requiresEquipment) {
        this.requiresEquipment = requiresEquipment;
    }

    // --- MÉTODOS DE CONVERSIÓN ---

    /**
     * Convierte este modelo local ({@link ChallengeData}) en un modelo específico
     * para la capa de red/base de datos ({@link FirebaseChallengeData}).
     * Es útil para separar la lógica de la vista (UI) de la lógica de almacenamiento.
     *
     * @return Una instancia de FirebaseChallengeData mapeada con los valores actuales.
     */
    public FirebaseChallengeData asFirebaseChallengeData() {
        FirebaseChallengeData firebaseChallengeData = new FirebaseChallengeData(this.id, this.title, this.creationDate, this.activationDate, this.challengeTime, this.exerciseSup, this.exerciseInf, this.exerciseCore, this.state, this.repetitionSup, this.repetitionInf, this.repetitionCore, this.type, this.requiresEquipment);
        return firebaseChallengeData;
    }
}
