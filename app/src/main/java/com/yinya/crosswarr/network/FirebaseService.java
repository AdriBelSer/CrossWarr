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

/**
 * Servicio genérico central para gestionar todas las operaciones de entrada/salida (CRUD)
 * con la base de datos Firebase Firestore.
 * Implementa el patrón Singleton para mantener una única conexión activa.
 * Proporciona métodos reutilizables para manejar documentos enteros, arrays y mapas anidados.
 */
public class FirebaseService {

    private static FirebaseService instance;
    /**
     * Nombre de la colección raíz o principal de la aplicación en Firestore.
     */
    public final String COLLECTION_NAME = "crosswarr";
    FirebaseFirestore db;

    /**
     * Constructor privado para forzar el patrón Singleton.
     * Inicializa la instancia de FirebaseFirestore.
     */
    private FirebaseService() {
        db = FirebaseFirestore.getInstance();
    }

    /**
     * Obtiene la instancia única y global del servicio de base de datos.
     *
     * @return La instancia Singleton de {@link FirebaseService}.
     */
    public static FirebaseService getInstance() {
        if (instance == null) {
            instance = new FirebaseService();
        }
        return instance;
    }

    // --- CRUD GENÉRICO ---

    /**
     * Crea un nuevo documento o sobrescribe uno existente con un ID específico.
     *
     * @param collectionName Nombre de la colección donde se guardará el documento.
     * @param documentId     Identificador único deseado para el documento.
     * @param data           Mapa clave-valor con los datos a guardar.
     */
    public void createDocumentWithId(String collectionName, String documentId, Map<String, Object> data) {
        db.collection(collectionName).document(documentId).set(data).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d("Creation success", "DocumentSnapshot successfully written!");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w("Creation failure", "Error writing document", e);
            }
        });

    }

    /**
     * Recupera un documento completo de la base de datos a través de su ID.
     *
     * @param collectionName Nombre de la colección.
     * @param documentName   ID del documento a buscar.
     * @param callback       Interfaz para manejar asíncronamente el mapa de datos resultante o el error.
     */
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

    /**
     * Obtiene todos los perfiles de usuario de una colección mixta.
     * Filtra la respuesta ignorando documentos predefinidos del sistema
     * ("exercises", "challenges", "users") para devolver únicamente los documentos que pertenecen a usuarios.
     *
     * @param collectionName El nombre de la colección principal (ej. "crosswarr").
     * @param callback       Interfaz para manejar el mapa con todos los usuarios encontrados.
     */
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

    /**
     * Añade un nuevo elemento a un Array dentro de un documento existente utilizando FieldValue.arrayUnion().
     * Esto previene duplicados, ya que Firestore solo añade el elemento si no existe previamente.
     *
     * @param collectionName  Nombre de la colección.
     * @param documentName    ID del documento que contiene el array.
     * @param arrayName       Nombre del campo tipo Array en la base de datos.
     * @param newElement      El objeto o dato que se va a insertar.
     * @param successListener Listener a ejecutar si la operación es exitosa.
     * @param failureListener Listener a ejecutar si la operación falla.
     */
    public void addElementToArray(String collectionName, String documentName, String arrayName, Object newElement, OnSuccessListener<Void> successListener, OnFailureListener failureListener) {
        Map<String, Object> updateData = new HashMap<>();
        updateData.put(arrayName, FieldValue.arrayUnion(newElement));

        db.collection(collectionName).document(documentName).update(updateData).addOnSuccessListener(successListener).addOnFailureListener(failureListener);
    }

    /**
     * Añade o actualiza un campo principal (tipo Map u Objeto) dentro de un documento sin borrar el resto.
     * Utiliza SetOptions.merge() para fusionar los datos nuevos con los existentes.
     *
     * @param collectionName  Nombre de la colección.
     * @param documentName    ID del documento destino.
     * @param mapKey          El nombre de la clave o campo principal a crear/actualizar.
     * @param mapValue        El valor u objeto (generalmente un HashMap) que se asignará a la clave.
     * @param successListener Listener a ejecutar si la operación es exitosa.
     * @param failureListener Listener a ejecutar si la operación falla.
     */
    public void addMapToDocument(String collectionName, String documentName, String mapKey, Object mapValue, OnSuccessListener<Void> successListener, OnFailureListener failureListener) {

        Map<String, Object> data = new HashMap<>();
        data.put(mapKey, mapValue);

        db.collection(collectionName).document(documentName).set(data, com.google.firebase.firestore.SetOptions.merge()).addOnSuccessListener(successListener).addOnFailureListener(failureListener);
    }

    /**
     * Añade o actualiza un elemento dentro de un Mapa anidado (un mapa dentro de otro mapa) en Firestore.
     * Utiliza SetOptions.merge() para proteger la integridad de los datos circundantes.
     *
     * @param collectionName  Nombre de la colección.
     * @param documentName    ID del documento destino.
     * @param mapFieldName    El nombre del mapa contenedor en Firestore.
     * @param itemKey         La clave específica dentro del mapa anidado.
     * @param itemValue       El valor asignado a esa clave anidada.
     * @param successListener Listener a ejecutar en caso de éxito.
     * @param failureListener Listener a ejecutar en caso de error.
     */
    public void addItemToMap(String collectionName, String documentName, String mapFieldName, String itemKey, Object itemValue, OnSuccessListener<Void> successListener, OnFailureListener failureListener) {

        Map<String, Object> innerMap = new HashMap<>();
        innerMap.put(itemKey, itemValue);

        Map<String, Object> outerData = new HashMap<>();
        outerData.put(mapFieldName, innerMap);

        db.collection(collectionName).document(documentName).set(outerData, com.google.firebase.firestore.SetOptions.merge()).addOnSuccessListener(successListener).addOnFailureListener(failureListener);
    }

    /**
     * Elimina un elemento específico de un Array dentro de un documento utilizando FieldValue.arrayRemove().
     *
     * @param collectionName  Nombre de la colección.
     * @param documentName    ID del documento que contiene el array.
     * @param arrayName       Nombre del campo tipo Array.
     * @param elementToRemove El objeto exacto que se desea eliminar (debe coincidir con la estructura guardada).
     * @param successListener Listener a ejecutar en caso de éxito.
     * @param failureListener Listener a ejecutar en caso de error.
     */
    public void removeElementFromArray(String collectionName, String documentName, String arrayName, Object elementToRemove, OnSuccessListener<Void> successListener, OnFailureListener failureListener) {
        Map<String, Object> updateData = new HashMap<>();
        // arrayRemove busca el objeto exacto y lo elimina del array
        updateData.put(arrayName, FieldValue.arrayRemove(elementToRemove));

        db.collection(collectionName).document(documentName).update(updateData).addOnSuccessListener(successListener).addOnFailureListener(failureListener);
    }

    /**
     * Elimina un campo entero (como un mapa o un valor suelto) de un documento.
     * Utiliza FieldValue.delete() para instruir a Firestore a destruir esa clave específica.
     *
     * @param collectionName  Nombre de la colección.
     * @param documentName    ID del documento.
     * @param mapKey          El nombre del campo (clave) que se quiere eliminar completamente.
     * @param successListener Listener a ejecutar en caso de éxito.
     * @param failureListener Listener a ejecutar en caso de error.
     */
    public void removeMapFromDocument(String collectionName, String documentName, String mapKey, OnSuccessListener<Void> successListener, OnFailureListener failureListener) {
        Map<String, Object> updateData = new HashMap<>();

        // FieldValue.delete() es para borrar un campo entero
        updateData.put(mapKey, FieldValue.delete());

        db.collection(collectionName).document(documentName).update(updateData).addOnSuccessListener(successListener).addOnFailureListener(failureListener);
    }

    /**
     * Elimina permanentemente un documento entero de la colección especificada.
     *
     * @param collectionName  Nombre de la colección.
     * @param documentId      ID único del documento a destruir.
     * @param successListener Listener a ejecutar si la eliminación es exitosa.
     * @param failureListener Listener a ejecutar si ocurre un fallo.
     */
    public void deleteDocument(String collectionName, String documentId, OnSuccessListener<Void> successListener, OnFailureListener failureListener) {
        db.collection(collectionName).document(documentId).delete().addOnSuccessListener(successListener).addOnFailureListener(failureListener);
    }

}
