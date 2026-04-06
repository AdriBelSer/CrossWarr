package com.yinya.crosswarr.network.models;

import com.yinya.crosswarr.models.UserData;
import com.yinya.crosswarr.network.FirebaseService;

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
        firebaseService.createDocumentWithId("crosswarr",
                userData.getUid(), userData.asFirebaseUserData().asHashMap());
    }
    /*
    * hacer un contructor que tenga
    * 1- singletom:     public static FirebaseService getInstance() {
        if (instance == null) {
            instance = new FirebaseService();
        }
        return instance;
        *
      2-y que en el constructor privado reciba ek servicio de firebase:
      * FirebaseService firebaseService
      *
      * */
}
