package com.yinya.crosswarr.network;

import com.yinya.crosswarr.models.UserData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class FirebaseUserService {
    private static FirebaseUserService instance;
    private FirebaseService firebaseService;

    private FirebaseUserService(FirebaseService firebaseService) {
        this.firebaseService = firebaseService;
    }

    public static FirebaseUserService getInstance(FirebaseService firebaseService) {
        if (instance == null) {
            instance = new FirebaseUserService(firebaseService);
        }
        return instance;
    }

    // Metodos CRUD usuarios
    public void createUser(UserData userData) {
        firebaseService = FirebaseService.getInstance();
        firebaseService.createDocumentWithId(firebaseService.COLLECTION_NAME, userData.getUid(), userData.asFirebaseUserData().asHashMap());
    }

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

                            // Si el uid no viene dentro del mapa por algún motivo, usamos el nombre del documento (entry.getKey())
                            String uid = (String) map.get("uid");
                            user.setUid(uid != null ? uid : entry.getKey());

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

    public void upsertChallengeTime(String uid, String challengeId, int challengeTime, IFirebaseCallback callback) {
        Map<String, Object> data = new HashMap<>();
        data.put(challengeId, challengeTime);
        firebaseService.addItemToMap(firebaseService.COLLECTION_NAME, uid, "challenges", challengeId, challengeTime, aVoid -> callback.onSuccess(data), callback::onFailure);
    }

    public void deleteUser(UserData userData) {
        firebaseService.deleteDocument(
                firebaseService.COLLECTION_NAME,
                userData.getUid(),
                aVoid -> {
                    android.util.Log.d("FirebaseUserService", "Usuario borrado de Firebase: " + userData.getUid());
                },
                e -> {
                    android.util.Log.e("FirebaseUserService", "Error al borrar el usuario", e);
                }
        );
    }
}

