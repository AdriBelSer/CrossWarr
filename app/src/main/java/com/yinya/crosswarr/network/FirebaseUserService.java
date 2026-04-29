package com.yinya.crosswarr.network;

import com.yinya.crosswarr.models.UserData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Servicio especializado en la gestión de Usuarios en la base de datos de Firebase.
 * Implementa el patrón Singleton y utiliza el servicio genérico {@link FirebaseService}
 * para realizar operaciones CRUD (Crear, Leer, Actualizar, Eliminar) sobre los perfiles de los usuarios.
 * En esta arquitectura, cada usuario es un documento individual en la raíz de la colección principal.
 */
public class FirebaseUserService {
    private static FirebaseUserService instance;
    private FirebaseService firebaseService;

    /**
     * Constructor privado para forzar el patrón Singleton.
     *
     * @param firebaseService Instancia del servicio genérico de Firebase que maneja las conexiones base.
     */
    private FirebaseUserService(FirebaseService firebaseService) {
        this.firebaseService = firebaseService;
    }

    /**
     * Obtiene la instancia única y global de este servicio.
     * Si no existe, la inicializa inyectando la dependencia del servicio central.
     *
     * @param firebaseService Instancia activa del servicio central de Firebase.
     * @return La instancia Singleton de {@link FirebaseUserService}.
     */
    public static FirebaseUserService getInstance(FirebaseService firebaseService) {
        if (instance == null) {
            instance = new FirebaseUserService(firebaseService);
        }
        return instance;
    }

    // --- Métodos CRUD para Usuarios ---

    /**
     * Crea un nuevo documento de usuario en la base de datos tras un registro exitoso.
     * Utiliza el UID único generado por Firebase Authentication como el ID del documento
     * para vincular directamente la cuenta de acceso con su perfil de datos.
     *
     * @param userData Objeto {@link UserData} con la información inicial del usuario.
     */
    public void createUser(UserData userData) {
        firebaseService = FirebaseService.getInstance();
        firebaseService.createDocumentWithId(firebaseService.COLLECTION_NAME, userData.getUid(), userData.asFirebaseUserData().asHashMap());
    }

    /**
     * Descarga y procesa todos los perfiles de usuario registrados en la aplicación.
     * Extrae los datos de la colección mixta de Firestore, transformando los diccionarios
     * en objetos locales {@link UserData} y aplicando medidas de seguridad (como el fallback del UID).
     *
     * @param callback Interfaz para devolver asíncronamente la lista de todos los usuarios.
     */
    public void fetchUsers(IUsersCallback callback) {
        firebaseService.getUsersFromMixedCollection(firebaseService.COLLECTION_NAME, new IFirebaseCallback() {
            @Override
            public void onSuccess(Map<String, Object> dataFromFirebase) {
                ArrayList<UserData> allUsers = new ArrayList<>();

                if (dataFromFirebase != null) {
                    for (Map.Entry<String, Object> entry : dataFromFirebase.entrySet()) {
                        Object value = entry.getValue();
                        if (value instanceof Map) {
                            Map<String, Object> map = (Map<String, Object>) value;
                            UserData user = new UserData();

                            // Medida de seguridad: Si el campo uid no existe dentro del documento, usamos el nombre del documento (que por arquitectura es siempre el UID real).
                            String uid = (String) map.get("uid");
                            user.setUid(uid != null ? uid : entry.getKey());

                            // Mapeo de datos básicos del perfil
                            user.setName((String) map.get("name"));
                            user.setEmail((String) map.get("email"));
                            user.setRole((String) map.get("role"));
                            user.setPhoto((String) map.get("photo"));
                            user.setAccountCreationDate((com.google.firebase.Timestamp) map.get("accountCreationDate"));

                            allUsers.add(user);
                        }
                    }
                }
                callback.onSuccess(allUsers);
            }

            public void onFailure(Exception e) {
                callback.onFailure(e);
            }
        });
    }

    /**
     * Inserta o actualiza (Upsert) el tiempo registrado por un usuario en un desafío específico.
     * Este método modifica únicamente el mapa anidado "challenges" dentro del documento del usuario,
     * asociando el ID del desafío como clave y el tiempo completado (en milisegundos) como valor.
     * Si el usuario repite un reto, su tiempo anterior se sobrescribe con el nuevo.
     *
     * @param uid           Identificador único del usuario que ha completado el reto.
     * @param challengeId   Identificador único del desafío completado.
     * @param challengeTime Tiempo invertido en completar el desafío (generalmente en milisegundos).
     * @param callback      Interfaz para manejar el éxito o el fallo de la operación de guardado.
     */
    public void upsertChallengeTime(String uid, String challengeId, int challengeTime, IFirebaseCallback callback) {
        Map<String, Object> data = new HashMap<>();
        data.put(challengeId, challengeTime);
        firebaseService.addItemToMap(firebaseService.COLLECTION_NAME, uid, "challenges", challengeId, challengeTime, aVoid -> callback.onSuccess(data), callback::onFailure);
    }

    /**
     * Elimina permanentemente el perfil de datos de un usuario de la base de datos Firestore.
     *
     * @param userData El objeto del usuario cuya cuenta se desea borrar.
     */
    public void deleteUser(UserData userData) {
        firebaseService.deleteDocument(firebaseService.COLLECTION_NAME, userData.getUid(), aVoid -> {
            android.util.Log.d("FirebaseUserService", "Usuario borrado de Firebase: " + userData.getUid());
        }, e -> {
            android.util.Log.e("FirebaseUserService", "Error al borrar el usuario", e);
        });
    }
}

