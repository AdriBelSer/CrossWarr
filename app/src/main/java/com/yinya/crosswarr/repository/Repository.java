package com.yinya.crosswarr.repository;

import com.yinya.crosswarr.models.UserData;
import com.yinya.crosswarr.network.FirebaseService;
import com.yinya.crosswarr.network.models.FirebaseUserService;

public class Repository {
    private static Repository instance;
    private FirebaseService firebaseSvc;
    private FirebaseUserService firebaseUserService;

    private Repository() {
        firebaseSvc = FirebaseService.getInstance();
        firebaseUserService = FirebaseUserService.getInstance(firebaseSvc);
    }

    public static Repository getInstance() {
        if (instance == null) {
            instance = new Repository();
        }
        return instance;
    }

public void createUser(UserData userData){
    firebaseUserService.createUser(userData);
}

}