package com.yinya.crosswarr.repository;

import com.yinya.crosswarr.network.FirebaseService;

public class Repository {
    private static Repository instance;
    private FirebaseService firebaseSvc;

    private Repository() {
        firebaseSvc = FirebaseService.getInstance();
    }

    public static Repository getInstance() {
        if (instance == null) {
            instance = new Repository();
        }
        return instance;
    }

public void createUser(String uid){
    firebaseSvc.createUser(uid);

}

}