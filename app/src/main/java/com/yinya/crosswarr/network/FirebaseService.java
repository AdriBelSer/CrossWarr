package com.yinya.crosswarr.network;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class FirebaseService {
    //metodos CRUD usuarios
    // llamo al metodo fectch de unaidjalsdk generico y le
    //metodos CRUD challenges
    //Metodos CRUD ejercicios
    //métodos genéricos para hacer esos CRUDs en firebase
    //metodo fetch de un documento(defuera) GENERICO

    private static FirebaseService instance;
    FirebaseFirestore db;

    private FirebaseService() {
        db = FirebaseFirestore.getInstance();
    }

    public static FirebaseService getInstance() {
        if (instance == null) {
            instance = new FirebaseService();
        }
        return instance;
    }

    public void createUser(String uid){
        Map<String, Object> user = new HashMap<>();
        user.put("id", uid);

        db.collection("crosswarr").document(uid)
                .set(user)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("Creation success", "DocumentSnapshot successfully written!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("Creation failure", "Error writing document", e);
                    }
                });



    }
}
