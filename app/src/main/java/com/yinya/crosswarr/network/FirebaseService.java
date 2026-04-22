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
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.HashMap;
import java.util.Map;

public class FirebaseService {
    public final String COLLECTION_NAME = "crosswarr";

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

    public void getUsersFromMixedCollection(String collectionName, IFirebaseCallback callback) {
        db.collection(collectionName).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Map<String, Object> allUsers = new HashMap<>();

                for (QueryDocumentSnapshot document : task.getResult()) {
                    String documentId = document.getId();

                    // Si el documento NO se llama "exercises" y NO se llama "challenges", entonces asumimos que es el documento de un usuario.
                    if (!documentId.equals("exercises") && !documentId.equals("challenges") && !documentId.equals("users")) {
                        allUsers.put(documentId, document.getData());
                    }
                }
                callback.onSuccess(allUsers);
            } else {
                Log.d("Get Collection failure", "get failed with ", task.getException());
                callback.onFailure(task.getException());
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

    public void addMapToDocument(String collectionName, String documentName, String mapKey, Object mapValue, OnSuccessListener<Void> successListener, OnFailureListener failureListener) {

        Map<String, Object> data = new HashMap<>();
        data.put(mapKey, mapValue);

        db.collection(collectionName)
                .document(documentName)
                .set(data, com.google.firebase.firestore.SetOptions.merge())
                .addOnSuccessListener(successListener)
                .addOnFailureListener(failureListener);
    }

    public void addItemToMap(String collectionName, String documentName, String mapFieldName, String itemKey, Object itemValue, OnSuccessListener<Void> successListener, OnFailureListener failureListener) {

        Map<String, Object> innerMap = new HashMap<>();
        innerMap.put(itemKey, itemValue);

        Map<String, Object> outerData = new HashMap<>();
        outerData.put(mapFieldName, innerMap);

        db.collection(collectionName)
                .document(documentName)
                .set(outerData, com.google.firebase.firestore.SetOptions.merge())
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

    public void deleteDocument(String collectionName, String documentId, OnSuccessListener<Void> successListener, OnFailureListener failureListener) {
        db.collection(collectionName)
                .document(documentId)
                .delete()
                .addOnSuccessListener(successListener)
                .addOnFailureListener(failureListener);
    }

}
