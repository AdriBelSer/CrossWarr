package com.yinya.crosswarr.network;

import android.util.Log;

import com.google.firebase.Timestamp;
import com.yinya.crosswarr.models.ChallengeData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Servicio especializado en la gestión de Desafíos (Challenges) en la base de datos de Firebase.
 * Implementa el patrón Singleton y utiliza el servicio genérico {@link FirebaseService}
 * para realizar operaciones CRUD (Crear, Leer, Eliminar) específicas para los entrenamientos diarios.
 */
public class FirebaseChallengeService {
    private static FirebaseChallengeService instance;
    FirebaseService firebaseService;

    /**
     * Constructor privado para forzar el patrón Singleton.
     *
     * @param firebaseService Instancia del servicio genérico de Firebase que maneja las conexiones base.
     */
    private FirebaseChallengeService(FirebaseService firebaseService) {
        this.firebaseService = firebaseService;
    }

    /**
     * Obtiene la instancia única y global de este servicio.
     * Si no existe, la inicializa de forma segura inyectando la dependencia requerida.
     *
     * @param firebaseService Instancia activa del servicio central de Firebase.
     * @return La instancia Singleton de {@link FirebaseChallengeService}.
     */
    public static FirebaseChallengeService getInstance(FirebaseService firebaseService) {
        if (instance == null) {
            instance = new FirebaseChallengeService(firebaseService);
        }
        return instance;
    }

    // --- Métodos CRUD para Challenges ---

    /**
     * Sube un nuevo desafío a Firebase.
     * Convierte el objeto local {@link ChallengeData} a un mapa de datos (HashMap) compatible
     * con Firestore y lo añade al documento central de desafíos.
     *
     * @param challengeData El objeto con toda la información del desafío que se va a guardar.
     */

    public void createChallenge(ChallengeData challengeData) {
        HashMap<String, Object> challengeMap = challengeData.asFirebaseChallengeData().asHashMap();
        String mapKey = challengeData.getId();

        firebaseService = FirebaseService.getInstance();
        firebaseService.addMapToDocument(firebaseService.COLLECTION_NAME, "challenges", mapKey, challengeMap, aVoid -> {
            Log.d("FirebaseChallengeService", "Desafío añadido correctamente con clave: " + mapKey);
        }, e -> {
            Log.e("FirebaseChallengeService", "Error al añadir el desafío", e);
        });
    }

    /**
     * Descarga el documento completo de desafíos desde Firestore, lo parsea y convierte
     * el diccionario de datos crudos en una lista de objetos {@link ChallengeData} estructurados.
     * * Además, este método calcula dinámicamente si un reto está "activo" comparando su
     * fecha de activación con la fecha actual del sistema.
     *
     * @param callback Interfaz para devolver de forma asíncrona la lista de desafíos procesada o un error.
     */

    public void fetchChallenges(IChallengesCallback callback) {
        firebaseService.getDocument("crosswarr", "challenges", new IFirebaseCallback() {

            @Override
            public void onSuccess(Map<String, Object> dataFromFirebase) {
                ArrayList<ChallengeData> allChallenges = new ArrayList<>();

                if (dataFromFirebase != null) {
                    // entry.getKey() será "challenge_2026..." y entry.getValue() será el Map con los datos
                    for (Map.Entry<String, Object> entry : dataFromFirebase.entrySet()) {
                        Object value = entry.getValue();

                        // Nos aseguramos de que el valor sea realmente un Map de datos
                        if (value instanceof Map) {
                            Map<String, Object> map = (Map<String, Object>) value;
                            ChallengeData ch = new ChallengeData();

                            // Mapeo de cadenas de texto (Strings)
                            ch.setId((String) map.get("id"));
                            ch.setTitle((String) map.get("title"));
                            ch.setExerciseSup((String) map.get("exerciseSup"));
                            ch.setExerciseInf((String) map.get("exerciseInf"));
                            ch.setExerciseCore((String) map.get("exerciseCore"));
                            ch.setType((String) map.get("type"));

                            // Mapeo de booleanos con valor por defecto
                            if (map.get("requiresEquipment") != null) {
                                ch.setRequiresEquipment((Boolean) map.get("requiresEquipment"));
                            } else {
                                // Si no está especificado, asumimos que no requiere material.
                                ch.setRequiresEquipment(false);
                            }

                            // Mapeo de Fechas (Timestamps)
                            ch.setCreationDate((Timestamp) map.get("creationDate"));
                            ch.setActivationDate((Timestamp) map.get("activationDate"));

                            // LÓGICA DE ESTADO:
                            // Para "activar" o "desactivar" los desafíos comparamos la fecha de activación con la fecha actual

                            if (ch.getActivationDate() != null) {
                                java.util.Date activationDate = ch.getActivationDate().toDate();
                                java.util.Date currentDate = new java.util.Date();

                                // Si la fecha de activación ya quedó en el pasado, está activo (true)
                                if (activationDate.before(currentDate)) {
                                    ch.setState(true);
                                } else {
                                    ch.setState(false); // Es un reto futuro
                                }
                            } else {
                                // Por seguridad, si algún reto antiguo no tiene fecha
                                ch.setState(false);
                            }

                            // Mapeo de Números (Protección obligatoria: Firestore devuelve enteros como Long)
                            if (map.get("challengeTime") != null) {
                                ch.setChallengeTime(((Long) map.get("challengeTime")).intValue());
                            }
                            if (map.get("repetitionSup") != null) {
                                ch.setRepetitionSup(((Long) map.get("repetitionSup")).intValue());
                            }
                            if (map.get("repetitionInf") != null) {
                                ch.setRepetitionInf(((Long) map.get("repetitionInf")).intValue());
                            }
                            if (map.get("repetitionCore") != null) {
                                ch.setRepetitionCore(((Long) map.get("repetitionCore")).intValue());
                            }

                            allChallenges.add(ch);
                        }
                    }
                }

                callback.onSuccess(allChallenges);
            }

            @Override
            public void onFailure(Exception e) {
                callback.onFailure(e);
            }
        });
    }

    /**
     * Elimina un desafío específico del documento central en Firestore utilizando su ID único.
     *
     * @param challengeData El objeto {@link ChallengeData} que representa el reto a eliminar.
     */
    public void deleteChallenge(ChallengeData challengeData) {
        String targetMapKey = challengeData.getId();
        firebaseService.removeMapFromDocument("crosswarr", "challenges", targetMapKey, aVoid -> {
            android.util.Log.d("FirebaseChallengeService", "Desafío borrado de Firebase: " + targetMapKey);
        }, e -> {
            android.util.Log.e("FirebaseChallengeService", "Error al borrar el desafío", e);
        });
    }
}
