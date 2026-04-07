package com.yinya.crosswarr.network;

import com.yinya.crosswarr.models.ChallengeData;
import com.yinya.crosswarr.network.models.FirebaseUserService;

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
        firebaseService = FirebaseService.getInstance();
        firebaseService.createDocumentWithId("crosswarr",
                challengeData.getId(), challengeData.asFirebaseChallengeData().asHashMap());
    }
}
