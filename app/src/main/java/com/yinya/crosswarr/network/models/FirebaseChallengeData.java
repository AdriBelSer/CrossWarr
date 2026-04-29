package com.yinya.crosswarr.network.models;

import com.google.firebase.Timestamp;
import com.yinya.crosswarr.models.ChallengeData;

import java.util.HashMap;

/**
 * Modelo de datos de red (Data Transfer Object) para los Desafíos.
 * Esta clase representa exactamente la estructura de un documento de la colección de desafíos
 * en Firebase Firestore. Sirve como intermediario entre la base de datos en la nube y el
 * modelo de datos local de la aplicación ({@link ChallengeData}).
 */
public class FirebaseChallengeData {

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
     * Es un requisito estricto del SDK de Firebase Firestore para poder deserializar
     * la información descargada de la nube y convertirla en este objeto Java.
     */
    public FirebaseChallengeData() {
    }

    /**
     * Constructor parcial para inicializar los datos básicos de un desafío en Firebase.
     *
     * @param id             Identificador único del documento en Firestore.
     * @param title          Título o nombre del desafío.
     * @param creationDate   Timestamp de creación del registro en la base de datos.
     * @param activationDate Timestamp a partir del cual el reto es visible.
     * @param challengeTime  Duración total del reto (en minutos).
     * @param exerciseSup    Nombre del ejercicio de tren superior.
     * @param exerciseInf    Nombre del ejercicio de tren inferior.
     * @param exerciseCore   Nombre del ejercicio de core.
     * @param state          Estado del reto (activo/inactivo).
     */
    public FirebaseChallengeData(String id, String title, Timestamp creationDate, Timestamp activationDate, int challengeTime, String exerciseSup, String exerciseInf, String exerciseCore, boolean state) {
        this.id = id;
        this.title = title;
        this.creationDate = creationDate;
        this.activationDate = activationDate;
        this.challengeTime = challengeTime;
        this.exerciseSup = exerciseSup;
        this.exerciseInf = exerciseInf;
        this.exerciseCore = exerciseCore;
        this.state = state;
    }

    /**
     * Constructor completo para estructurar un desafío con todos sus campos antes de subirlo a Firestore.
     *
     * @param id                Identificador único del documento.
     * @param title             Título del desafío.
     * @param creationDate      Fecha de creación original.
     * @param activationDate    Fecha de disponibilidad para los usuarios.
     * @param challengeTime     Tiempo del reto.
     * @param exerciseSup       Ejercicio superior.
     * @param exerciseInf       Ejercicio inferior.
     * @param exerciseCore      Ejercicio core.
     * @param state             Estado booleano de activación.
     * @param repetitionSup     Repeticiones para el ejercicio superior.
     * @param repetitionInf     Repeticiones para el ejercicio inferior.
     * @param repetitionCore    Repeticiones para el ejercicio core.
     * @param type              Modalidad (ej. "amrap", "emom").
     * @param requiresEquipment Indica si el reto requiere material externo.
     */
    public FirebaseChallengeData(String id, String title, Timestamp creationDate, Timestamp activationDate, int challengeTime, String exerciseSup, String exerciseInf, String exerciseCore, boolean state, int repetitionSup, int repetitionInf, int repetitionCore, String type, boolean requiresEquipment) {
        this.id = id;
        this.title = title;
        this.creationDate = creationDate;
        this.activationDate = activationDate;
        this.challengeTime = challengeTime;
        this.exerciseSup = exerciseSup;
        this.exerciseInf = exerciseInf;
        this.exerciseCore = exerciseCore;
        this.state = state;
        this.repetitionSup = repetitionSup;
        this.repetitionInf = repetitionInf;
        this.repetitionCore = repetitionCore;
        this.type = type;
        this.requiresEquipment = requiresEquipment;
    }

    // --- GETTERS Y SETTERS ---

    /**
     * @return El ID del documento en Firestore.
     */
    public String getId() {
        return id;
    }

    /**
     * @param id El nuevo ID a asignar.
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
     * @return La fecha de creación en formato Timestamp de Firebase.
     */
    public Timestamp getCreationDate() {
        return creationDate;
    }

    /**
     * @param creationDate La nueva fecha de creación.
     */
    public void setCreationDate(Timestamp creationDate) {
        this.creationDate = creationDate;
    }

    /**
     * @return La fecha de activación en formato Timestamp de Firebase.
     */
    public Timestamp getActivationDate() {
        return activationDate;
    }

    /**
     * @param activationDate La nueva fecha de activación.
     */
    public void setActivationDate(Timestamp activationDate) {
        this.activationDate = activationDate;
    }

    /**
     * @return El ejercicio de tren superior.
     */
    public String getExerciseSup() {
        return exerciseSup;
    }

    /**
     * @param exerciseSup El nuevo ejercicio superior.
     */
    public void setExerciseSup(String exerciseSup) {
        this.exerciseSup = exerciseSup;
    }

    /**
     * @return El ejercicio de tren inferior.
     */
    public String getExerciseInf() {
        return exerciseInf;
    }

    /**
     * @param exerciseInf El nuevo ejercicio inferior.
     */
    public void setExerciseInf(String exerciseInf) {
        this.exerciseInf = exerciseInf;
    }

    /**
     * @return El ejercicio de core.
     */
    public String getExerciseCore() {
        return exerciseCore;
    }

    /**
     * @param exerciseCore El nuevo ejercicio core.
     */
    public void setExerciseCore(String exerciseCore) {
        this.exerciseCore = exerciseCore;
    }

    /**
     * @return El estado del desafío.
     */
    public boolean isState() {
        return state;
    }

    /**
     * @param state El nuevo estado.
     */
    public void setState(boolean state) {
        this.state = state;
    }

    /**
     * @return El tiempo de duración del desafío.
     */
    public int getChallengeTime() {
        return challengeTime;
    }

    /**
     * @param challengeTime El nuevo tiempo de duración.
     */
    public void setChallengeTime(int challengeTime) {
        this.challengeTime = challengeTime;
    }

    /**
     * @return Las repeticiones del ejercicio superior.
     */
    public int getRepetitionSup() {
        return repetitionSup;
    }

    /**
     * @param repetitionSup Las nuevas repeticiones superiores.
     */
    public void setRepetitionSup(int repetitionSup) {
        this.repetitionSup = repetitionSup;
    }

    /**
     * @return Las repeticiones del ejercicio inferior.
     */
    public int getRepetitionInf() {
        return repetitionInf;
    }

    /**
     * @param repetitionInf Las nuevas repeticiones inferiores.
     */
    public void setRepetitionInf(int repetitionInf) {
        this.repetitionInf = repetitionInf;
    }

    /**
     * @return Las repeticiones del ejercicio de core.
     */
    public int getRepetitionCore() {
        return repetitionCore;
    }

    /**
     * @param repetitionCore Las nuevas repeticiones de core.
     */
    public void setRepetitionCore(int repetitionCore) {
        this.repetitionCore = repetitionCore;
    }

    /**
     * @return El tipo de entrenamiento (AMRAP, EMOM, etc.).
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
     * @return True si requiere equipamiento extra, false de lo contrario.
     */
    public boolean isRequiresEquipment() {
        return requiresEquipment;
    }

    /**
     * @param requiresEquipment El nuevo estado de requerimiento de equipamiento.
     */
    public void setRequiresEquipment(boolean requiresEquipment) {
        this.requiresEquipment = requiresEquipment;
    }

    // --- MÉTODOS DE CONVERSIÓN ---

    /**
     * Convierte este modelo de red (Firebase) al modelo de negocio local ({@link ChallengeData}).
     * Se utiliza inmediatamente después de descargar los datos de la base de datos,
     * para que la interfaz de usuario trabaje con los modelos locales de la app.
     *
     * @return Una instancia de ChallengeData con todos los valores de este documento.
     */
    public ChallengeData asChallengeData() {
        ChallengeData challengeData = new ChallengeData(this.id, this.title, this.creationDate, this.activationDate, this.challengeTime, this.exerciseSup, this.exerciseInf, this.exerciseCore, this.state, this.repetitionSup, this.repetitionInf, this.repetitionCore, this.type, this.requiresEquipment);
        return challengeData;
    }

    /**
     * Empaqueta todos los atributos de este objeto en un {@link HashMap}.
     * Es extremadamente útil para las operaciones de actualización (update) en Firestore,
     * ya que permite actualizar campos específicos del documento enviando un mapa clave-valor
     * en lugar de sobrescribir el documento completo.
     *
     * @return Un mapa que contiene los nombres de los campos de la base de datos como claves y sus respectivos valores.
     */
    public HashMap<String, Object> asHashMap() {
        HashMap<String, Object> hashMap = new HashMap<String, Object>();
        hashMap.put("id", this.id);
        hashMap.put("title", this.title);
        hashMap.put("creationDate", this.creationDate);
        hashMap.put("activationDate", this.activationDate);
        hashMap.put("challengeTime", this.challengeTime);
        hashMap.put("exerciseSup", this.exerciseSup);
        hashMap.put("exerciseInf", this.exerciseInf);
        hashMap.put("exerciseCore", this.exerciseCore);
        hashMap.put("state", this.state);
        hashMap.put("repetitionSup", this.repetitionSup);
        hashMap.put("repetitionInf", this.repetitionInf);
        hashMap.put("repetitionCore", this.repetitionCore);
        hashMap.put("type", this.type);
        hashMap.put("requiresEquipment", this.requiresEquipment);
        return hashMap;
    }
}
