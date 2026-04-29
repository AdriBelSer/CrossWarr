package com.yinya.crosswarr.network;

import android.util.Log;

import com.yinya.crosswarr.models.ExerciseData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Servicio especializado en la gestión de Ejercicios en la base de datos de Firebase.
 * Implementa el patrón Singleton. A diferencia de los desafíos, este servicio maneja
 * los datos almacenándolos dentro de listas (Arrays) categorizadas por zonas del cuerpo
 * y uso de material dentro de un único documento maestro.
 */
public class FirebaseExerciseService {
    private static FirebaseExerciseService instance;
    FirebaseService firebaseService;

    /**
     * Constructor para inicializar el servicio de ejercicios.
     *
     * @param firebaseService Instancia del servicio genérico de Firebase para realizar las peticiones.
     */
    public FirebaseExerciseService(FirebaseService firebaseService) {
        this.firebaseService = firebaseService;
    }

    /**
     * Obtiene la instancia única y global de este servicio (Patrón Singleton).
     *
     * @param firebaseService Instancia activa del servicio central de Firebase.
     * @return La instancia Singleton de {@link FirebaseExerciseService}.
     */
    public static FirebaseExerciseService getInstance(FirebaseService firebaseService) {
        if (instance == null) {
            instance = new FirebaseExerciseService(firebaseService);
        }
        return instance;
    }

    // --- Métodos CRUD para Ejercicios ---

    /**
     * Añade un nuevo ejercicio a la base de datos de Firebase.
     * El ejercicio se inserta dinámicamente en el array correspondiente
     * basándose en su tipo o categoría (ej. "upper_body_with_equipment").
     *
     * @param exerciseData El objeto con los datos del ejercicio a crear.
     */
    public void createExercise(ExerciseData exerciseData) {
        HashMap<String, Object> exerciseMap = exerciseData.asFirebaseExerciseData().asHashMap();
        String targetArrayName = exerciseData.getType();

        firebaseService = FirebaseService.getInstance();
        firebaseService.addElementToArray(firebaseService.COLLECTION_NAME, "exercises", targetArrayName, exerciseMap, aVoid -> {
            // Éxito
            Log.d("FirebaseExerciseService", "Ejercicio añadido correctamente al array: " + targetArrayName);
        }, e -> {
            // Fallo
            Log.e("FirebaseExerciseService", "Error al añadir el ejercicio", e);
        });

    }

    /**
     * Descarga y procesa todos los ejercicios almacenados en Firestore.
     * Recorre secuencialmente todos los arrays de categorías predefinidas en el documento maestro,
     * transformando los diccionarios de Firebase en objetos {@link ExerciseData} locales.
     *
     * @param callback Interfaz para devolver asíncronamente la lista unificada de todos los ejercicios.
     */
    public void fetchExercises(IExercisesCallback callback) {
        firebaseService.getDocument("crosswarr", "exercises", new IFirebaseCallback() {

            @Override
            public void onSuccess(Map<String, Object> dataFromFirebase) {

                ArrayList<ExerciseData> allExercises = new ArrayList<>();

                // Nombres de los arrays alojados en el documento de Firebase
                String[] arrayNames = {"upper_body_with_equipment", "upper_body_without_equipment", "lower_body_with_equipment", "lower_body_without_equipment", "core_with_equipment", "core_without_equipment"};

                for (String arrayName : arrayNames) {

                    // Prevenir advertencias de cast asegurando el tipo de lista
                    List<Map<String, Object>> firebaseList = (List<Map<String, Object>>) dataFromFirebase.get(arrayName);

                    if (firebaseList != null) {
                        for (Map<String, Object> map : firebaseList) {
                            ExerciseData ex = new ExerciseData();
                            ex.setId((String) map.get("id"));
                            ex.setName((String) map.get("name"));
                            ex.setDescription((String) map.get("description"));
                            ex.setType(arrayName);
                            ex.setImage((String) map.get("image"));
                            ex.setVideo((String) map.get("video"));
                            ex.setMaterials((ArrayList<String>) map.get("materials"));

                            // Protección contra valores nulos en booleanos
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

    /**
     * Actualiza el estado de uso de un ejercicio en la base de datos a "true" (utilizado).
     * Debido a las limitaciones técnicas de Firestore con la modificación de elementos complejos dentro
     * de un Array, se utiliza una estrategia de dos pasos: primero se elimina el objeto exacto original,
     * y si tiene éxito, se inserta el nuevo objeto actualizado en el mismo array.
     *
     * @param oldExercise El objeto de ejercicio original, tal cual se descargó de la base de datos.
     */
    public void updateExerciseUsage(ExerciseData oldExercise) {
        // Guardamos cómo era el ejercicio viejo para que Firebase sepa cuál borrar
        HashMap<String, Object> oldMap = oldExercise.asFirebaseExerciseData().asHashMap();
        String targetArray = oldExercise.getType();

        // Le cambiamos el estado a TRUE y sacamos el ejercicio nuevo
        oldExercise.setUsed(true);
        HashMap<String, Object> newMap = oldExercise.asFirebaseExerciseData().asHashMap();

        // Borramos el viejo, y si sale bien, metemos el nuevo
        firebaseService.removeElementFromArray("crosswarr", "exercises", targetArray, oldMap, aVoid -> {
            // Se borró el viejo, Ahora metemos el actualizado
            firebaseService.addElementToArray("crosswarr", "exercises", targetArray, newMap, aVoid2 -> android.util.Log.d("FirebaseExercise", "Ejercicio actualizado a isUsed=true"), e2 -> android.util.Log.e("FirebaseExercise", "Error añadiendo el nuevo", e2));
        }, e -> android.util.Log.e("FirebaseExercise", "Error borrando el viejo", e));
    }

    /**
     * Elimina un ejercicio específico de la base de datos.
     * Busca la coincidencia exacta del mapa proporcionado dentro del array correspondiente a su categoría.
     *
     * @param exerciseData El objeto de ejercicio que se desea eliminar definitivamente.
     */
    public void deleteExercise(ExerciseData exerciseData) {
        // Convertimos el ejercicio de vuelta a mapa para que Firebase lo reconozca
        HashMap<String, Object> exerciseMap = exerciseData.asFirebaseExerciseData().asHashMap();
        String targetArrayName = exerciseData.getType(); // ej. "upper_body_with_equipment"

        firebaseService.removeElementFromArray("crosswarr", "exercises", targetArrayName, exerciseMap, aVoid -> {
            android.util.Log.d("FirebaseExerciseService", "Ejercicio borrado de Firebase: " + exerciseData.getName());
        }, e -> {
            android.util.Log.e("FirebaseExerciseService", "Error al borrar el ejercicio", e);
        });
    }
}