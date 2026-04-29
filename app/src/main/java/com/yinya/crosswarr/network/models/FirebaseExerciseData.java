package com.yinya.crosswarr.network.models;

import com.yinya.crosswarr.models.ExerciseData;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Modelo de datos de red (Data Transfer Object) para los Ejercicios.
 * Esta clase representa la estructura exacta de un documento dentro de la colección de ejercicios
 * en Firebase Firestore. Actúa como intermediario entre la base de datos y el modelo
 * de negocio local de la aplicación ({@link ExerciseData}).
 */
public class FirebaseExerciseData {
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
     * Es estrictamente necesario para que el SDK de Firebase Firestore pueda instanciar
     * esta clase y volcar los datos descargados de la nube automáticamente.
     */
    public FirebaseExerciseData() {
    }

    /**
     * Constructor parcial para inicializar un ejercicio básico sin lista de materiales.
     *
     * @param id          Identificador único del documento en Firestore.
     * @param name        Nombre del ejercicio.
     * @param description Instrucciones de ejecución.
     * @param type        Categoría muscular (ej. Superior, Inferior).
     * @param image       URL de la imagen ilustrativa.
     * @param video       URL del vídeo demostrativo.
     * @param isUsed      Booleano que indica si el ejercicio está asignado a un desafío activo.
     */
    public FirebaseExerciseData(String id, String name, String description, String type, String image, String video, boolean isUsed) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.type = type;
        this.image = image;
        this.video = video;
        this.isUsed = isUsed;
    }

    /**
     * Constructor completo para estructurar un ejercicio con todos sus campos, incluyendo materiales.
     *
     * @param id          Identificador único del documento.
     * @param name        Nombre del ejercicio.
     * @param description Instrucciones de ejecución.
     * @param type        Categoría muscular.
     * @param image       URL de la imagen ilustrativa.
     * @param video       URL del vídeo demostrativo.
     * @param materials   Lista de Strings con los materiales necesarios.
     * @param isUsed      Booleano de uso activo en desafíos.
     */
    public FirebaseExerciseData(String id, String name, String description, String type, String image, String video, ArrayList<String> materials, boolean isUsed) {
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
     * @return El identificador único del documento.
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
     * @return La descripción del ejercicio.
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
     * @return El tipo o categoría del ejercicio.
     */
    public String getType() {
        return type;
    }

    /**
     * @param type El nuevo tipo a asignar.
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
     * @return La URL del vídeo del ejercicio.
     */
    public void setImage(String image) {
        this.image = image;
    }

    /**
     * @return La URL del vídeo del ejercicio.
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
     * @return La lista de materiales necesarios.
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
     * @return True si el ejercicio está en uso, false en caso contrario.
     */
    public boolean isUsed() {
        return isUsed;
    }

    /**
     * @param used El nuevo estado de uso a asignar.
     */
    public void setUsed(boolean used) {
        isUsed = used;
    }

    // --- MÉTODOS DE CONVERSIÓN ---

    /**
     * Convierte este modelo de red (Firebase) al modelo de negocio local ({@link ExerciseData}).
     * Este método se utiliza al recibir los datos de Firestore para que la capa de la vista (UI)
     * trabaje exclusivamente con los modelos locales.
     *
     * @return Una instancia de ExerciseData mapeada con los valores de este documento.
     */
    public ExerciseData asExerciseData() {
        ExerciseData exerciseData = new ExerciseData(this.id, this.name, this.description, this.type, this.image, this.video, this.materials, this.isUsed);
        return exerciseData;
    }

    /**
     * Empaqueta todos los atributos de este objeto en un {@link HashMap}.
     * Es ideal para realizar operaciones de actualización parcial (update) en Firestore.
     * Incluye una medida de seguridad (null checks) para los campos de imagen y vídeo,
     * asegurando que si no hay contenido multimedia, se envíe una cadena vacía en lugar de un valor nulo.
     *
     * @return Un mapa que contiene los nombres de los campos como claves y sus respectivos valores seguros.
     */
    public HashMap<String, Object> asHashMap() {
        HashMap<String, Object> hashMap = new HashMap<String, Object>();
        hashMap.put("id", this.id);
        hashMap.put("name", this.name);
        hashMap.put("description", this.description);
        hashMap.put("type", this.type);

        // Prevención de valores nulos para los enlaces multimedia
        if (this.image != null) {
            hashMap.put("image", this.image);
        } else {
            hashMap.put("image", "");
        }
        if (this.video != null) {
            hashMap.put("video", this.video);
        } else {
            hashMap.put("video", "");
        }
        hashMap.put("materials", this.materials);
        hashMap.put("isUsed", this.isUsed);

        return hashMap;
    }
}
