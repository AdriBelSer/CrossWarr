package com.yinya.crosswarr.network;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;


import java.util.HashMap;
import java.util.Map;

public class FirebaseService{

    // llamo al metodo fectch de unaidjalsdk generico
    // Métodos CRUD challenges
    // Métodos genéricos para hacer esos CRUDs en firebase
    // Metodo fetch de un documento(defuera) GENERICO

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

    //CRUD GENERICO
    public void createDocumentWithId(String collectionName, String documentId, Map<String, Object> data) {

        db.collection(collectionName)
                .document(documentId)
                .set(data)
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

    public void getDocument(String collectionName, String documentName, IFirebaseCallback callback) {
        DocumentReference documentReference = db.collection(collectionName).document(documentName);

        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d("Get success", "DocumentSnapshot data: " + document.getData());
                        callback.onSuccess(document.getData());
                    } else {
                        Log.d("Get success, no document", "No such document");
                        callback.onFailure(new Exception(documentName + " no exists."));
                    }
                } else {
                    Log.d("Get failure", "get failed with ", task.getException());
                    callback.onFailure(new Exception("Error getting document"));
                }
            }
        });
    }

}
