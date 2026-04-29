package com.yinya.crosswarr.network;

import com.yinya.crosswarr.models.ExerciseData;

import java.util.ArrayList;

/**
 * Interfaz de comunicación (Callback) utilizada para gestionar de forma asíncrona
 * las respuestas de la base de datos relacionadas con los Ejercicios.
 * Actúa como un puente entre la capa de red (Firebase) y la interfaz de usuario (UI),
 * notificando al hilo principal cuándo los datos están listos o si ha ocurrido algún problema.
 */
public interface IExercisesCallback {

    /**
     * Se invoca cuando la operación de descarga o consulta a la base de datos
     * finaliza con éxito.
     *
     * @param exercises Una lista (ArrayList) que contiene todos los objetos {@link ExerciseData}
     *                  recuperados, categorizados y procesados correctamente desde Firestore.
     */
    void onSuccess(ArrayList<ExerciseData> exercises);

    /**
     * Se invoca cuando la operación en la base de datos falla por cualquier motivo
     * (por ejemplo, pérdida de conexión a internet, falta de permisos o problemas de lectura).
     *
     * @param e La excepción lanzada que contiene los detalles técnicos del error para su depuración.
     */
    void onFailure(Exception e);
}

