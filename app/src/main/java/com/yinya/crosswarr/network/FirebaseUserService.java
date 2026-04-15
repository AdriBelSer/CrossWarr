package com.yinya.crosswarr.network;

import com.yinya.crosswarr.models.UserData;

import java.util.ArrayList;
import java.util.Map;

public class FirebaseUserService {
    private static FirebaseUserService instance;
    FirebaseService firebaseService;

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
        firebaseService.createDocumentWithId("crosswarr", userData.getUid(), userData.asFirebaseUserData().asHashMap());
    }

    public void fetchUsers(IUsersCallback callback) {
        firebaseService.getUsersFromMixedCollection("crosswarr", new IFirebaseCallback() {
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

    public void deleteUser(UserData userData) {
        String targetMapKey = userData.getUid();
        firebaseService.removeMapFromDocument(
                "crosswarr",
                userData.getUid(),
                targetMapKey,
                aVoid -> {
                    android.util.Log.d("FirebaseUserService", "Usuario borrado de Firebase: " + targetMapKey);
                },
                e -> {
                    android.util.Log.e("FirebaseUserService", "Error al borrar el usuario", e);
                }
        );
    }
}

