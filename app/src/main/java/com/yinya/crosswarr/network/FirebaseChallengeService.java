package com.yinya.crosswarr.network;

import android.util.Log;

import com.google.firebase.Timestamp;
import com.yinya.crosswarr.models.ChallengeData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class FirebaseChallengeService {
    private static FirebaseChallengeService instance;
    FirebaseService firebaseService;

    private FirebaseChallengeService(FirebaseService firebaseService) {
        this.firebaseService = firebaseService;
    }

    public static FirebaseChallengeService getInstance(FirebaseService firebaseService) {
        if (instance == null) {
            instance = new FirebaseChallengeService(firebaseService);
        }
        return instance;
    }
    // Métodos CRUD challenges

    public void createChallenge(ChallengeData challengeData) {
        HashMap<String, Object> challengeMap = challengeData.asFirebaseChallengeData().asHashMap();
        String mapKey = challengeData.getId();

        firebaseService = FirebaseService.getInstance();
        firebaseService.addMapToDocument(
                "crosswarr",
                "challenges",
                mapKey,
                challengeMap,
                aVoid -> {
                    Log.d("FirebaseChallengeService", "Desafío añadido correctamente con clave: " + mapKey);
                },
                e -> {
                    Log.e("FirebaseChallengeService", "Error al añadir el desafío", e);
                }
        );
    }

    public void fetchChallenges(IChallengesCallback callback) {
        firebaseService.getDocument("crosswarr", "challenges", new IFirebaseCallback() {

            @Override
            public void onSuccess(Map<String, Object> dataFromFirebase) {
                ArrayList<ChallengeData> allChallenges = new ArrayList<>();

                if (dataFromFirebase != null) {
                    // entry.getKey() será "challenge_2026..." y entry.getValue() será el Mapa con los datos
                    for (Map.Entry<String, Object> entry : dataFromFirebase.entrySet()) {

                        Object value = entry.getValue();

                        // Nos aseguramos de que el valor sea realmente un Mapa de datos
                        if (value instanceof Map) {
                            Map<String, Object> map = (Map<String, Object>) value;
                            ChallengeData ch = new ChallengeData();

                            // Strings
                            ch.setId((String) map.get("id"));
                            ch.setTitle((String) map.get("title"));
                            ch.setExerciseSup((String) map.get("exerciseSup"));
                            ch.setExerciseInf((String) map.get("exerciseInf"));
                            ch.setExerciseCore((String) map.get("exerciseCore"));
                            ch.setType((String) map.get("type"));

                            // Fechas (Timestamps)
                            ch.setCreationDate((Timestamp) map.get("creationDate"));
                            ch.setActivationDate((Timestamp) map.get("activationDate"));

                            // Números (Protección contra Long)
                            if (map.get("challengeTime") != null) {
                                ch.setChallenteTime(((Long) map.get("challengeTime")).intValue());
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

                            // Boolean
                            Boolean state = (Boolean) map.get("state");
                            ch.setState(state != null ? state : false);

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
}
