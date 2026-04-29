package com.yinya.crosswarr.models;

import com.yinya.crosswarr.network.models.FirebaseExerciseData;

import java.util.ArrayList;

/**
 * Modelo de datos que representa un Ejercicio dentro de la aplicación.
 * Esta clase se utiliza a nivel local para gestionar la información de los movimientos
 * (descripciones, enlaces multimedia y tipo de ejercicio) que componen los desafíos.
 */
public class ExerciseData {
    private String id;
    private String name;
    private String description;
    private String type;
    private String image;
    private String video;
    private ArrayList<String> materials;
    private boolean isUsed;

    /**
     * Constructor vacío por defecto.
     * Es estrictamente necesario para que Firebase Firestore pueda deserializar
     * los documentos de la base de datos y convertirlos en objetos Java automáticamente.
     */
    public ExerciseData() {
    }

    /**
     * Constructor parcial para un ejercicio (versión simplificada sin lista de materiales).
     *
     * @param id          Identificador único del ejercicio en la base de datos.
     * @param name        Nombre visible del ejercicio (ej. "Flexiones", "Sentadillas").
     * @param description Explicación detallada de cómo realizar el movimiento correctamente.
     * @param type        Categoría o zona del cuerpo que trabaja (ej. "Superior", "Inferior", "Core").
     * @param image       URL directa a la imagen ilustrativa del ejercicio.
     * @param video       URL directa al vídeo demostrativo o tutorial (ej. enlace de YouTube).
     * @param isUsed      Booleano que indica si este ejercicio está actualmente asignado a algún desafío activo.
     */
    public ExerciseData(String id, String name, String description, String type, String image, String video, boolean isUsed) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.type = type;
        this.image = image;
        this.video = video;
        this.isUsed = isUsed;
    }

    /**
     * Constructor completo para un ejercicio, incluyendo los materiales necesarios.
     *
     * @param id          Identificador único del ejercicio.
     * @param name        Nombre visible del ejercicio.
     * @param description Explicación detallada del movimiento.
     * @param type        Categoría o zona del cuerpo afectada.
     * @param image       URL de la imagen del ejercicio.
     * @param video       URL del vídeo explicativo.
     * @param materials   Lista de cadenas (Strings) con los nombres de los materiales necesarios (ej. "Mancuernas", "Comba").
     * @param isUsed      Booleano que indica si el ejercicio está en uso en algún reto.
     */
    public ExerciseData(String id, String name, String description, String type, String image, String video, ArrayList<String> materials, boolean isUsed) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.type = type;
        this.image = image;
        this.video = video;
        this.materials = materials;
        this.isUsed = isUsed;
    }

    // --- GETTERS Y SETTERS ---

    /**
     * @return El identificador único del ejercicio.
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
     * @return El nombre del ejercicio.
     */
    public String getName() {
        return name;
    }

    /**
     * @param name El nuevo nombre a asignar.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return La descripción o instrucciones del ejercicio.
     */
    public String getDescription() {
        return description;
    }

    /**
     * @param description La nueva descripción a asignar.
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * @return La categoría o tipo muscular del ejercicio.
     */
    public String getType() {
        return type;
    }

    /**
     * @param type El nuevo tipo o categoría a asignar.
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * @return La URL de la imagen del ejercicio.
     */
    public String getImage() {
        return image;
    }

    /**
     * @param image La nueva URL de imagen a asignar.
     */
    public void setImage(String image) {
        this.image = image;
    }

    /**
     * @return La URL del vídeo explicativo del ejercicio.
     */
    public String getVideo() {
        return video;
    }

    /**
     * @param video La nueva URL de vídeo a asignar.
     */
    public void setVideo(String video) {
        this.video = video;
    }

    /**
     * @return La lista de materiales requeridos para este ejercicio.
     */
    public ArrayList<String> getMaterials() {
        return materials;
    }

    /**
     * @param materials La nueva lista de materiales a asignar.
     */
    public void setMaterials(ArrayList<String> materials) {
        this.materials = materials;
    }

    /**
     * @return True si el ejercicio se está utilizando actualmente en algún desafío, false en caso contrario.
     */
    public boolean isUsed() {
        return isUsed;
    }

    /**
     * @param used El nuevo estado de uso del ejercicio.
     */
    public void setUsed(boolean used) {
        isUsed = used;
    }

    // --- MÉTODOS DE CONVERSIÓN ---

    /**
     * Convierte este modelo local ({@link ExerciseData}) en un modelo específico
     * para la capa de red y almacenamiento en Firebase ({@link FirebaseExerciseData}).
     * Esto facilita la separación de responsabilidades entre la UI y la base de datos.
     *
     * @return Una nueva instancia de FirebaseExerciseData con los datos mapeados.
     */
    public FirebaseExerciseData asFirebaseExerciseData() {
        FirebaseExerciseData firebaseExerciseData = new FirebaseExerciseData(this.id, this.name, this.description, this.type, this.image, this.video, this.materials, this.isUsed);
        return firebaseExerciseData;
    }
}
