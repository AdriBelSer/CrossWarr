package com.yinya.crosswarr.network;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class FirebaseService {

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

    public void addElementToArray(String collectionName, String documentName, String arrayName, Object newElement, OnSuccessListener<Void> successListener, OnFailureListener failureListener) {
        Map<String, Object> updateData = new HashMap<>();
        updateData.put(arrayName, FieldValue.arrayUnion(newElement));

        db.collection(collectionName)
                .document(documentName)
                .update(updateData)
                .addOnSuccessListener(successListener)
                .addOnFailureListener(failureListener);
    }

    //TODO: usar addMapToDocument para updates o no, ¿voy a hacer updates de algo?
    public void addMapToDocument(String collectionName, String documentName, String mapKey, Object mapValue, OnSuccessListener<Void> successListener, OnFailureListener failureListener) {

        Map<String, Object> data = new HashMap<>();
        data.put(mapKey, mapValue);

        db.collection(collectionName)
                .document(documentName)
                .set(data, com.google.firebase.firestore.SetOptions.merge())
                .addOnSuccessListener(successListener)
                .addOnFailureListener(failureListener);
    }

    public void removeElementFromArray(String collectionName, String documentName, String arrayName, Object elementToRemove, OnSuccessListener<Void> successListener, OnFailureListener failureListener) {
        Map<String, Object> updateData = new HashMap<>();
        // arrayRemove busca el objeto exacto y lo elimina del array
        updateData.put(arrayName, FieldValue.arrayRemove(elementToRemove));

        db.collection(collectionName)
                .document(documentName)
                .update(updateData)
                .addOnSuccessListener(successListener)
                .addOnFailureListener(failureListener);
    }

    public void removeMapFromDocument(String collectionName, String documentName, String mapKey, OnSuccessListener<Void> successListener, OnFailureListener failureListener) {
        Map<String, Object> updateData = new HashMap<>();

        // FieldValue.delete() es para borrar un campo entero
        updateData.put(mapKey, FieldValue.delete());

        db.collection(collectionName)
                .document(documentName)
                .update(updateData)
                .addOnSuccessListener(successListener)
                .addOnFailureListener(failureListener);
    }

}
