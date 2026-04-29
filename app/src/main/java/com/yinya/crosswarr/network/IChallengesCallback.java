package com.yinya.crosswarr.network;

import com.yinya.crosswarr.models.ChallengeData;

import java.util.ArrayList;

/**
 * Interfaz de comunicación (Callback) utilizada para gestionar de forma asíncrona
 * las respuestas de la base de datos relacionadas con los Desafíos (Challenges).
 * Permite separar la lógica de la red de la interfaz de usuario, notificando
 * al hilo principal cuándo los datos están listos para ser mostrados o si ocurrió algún error en la petición.
 */
public interface IChallengesCallback {

    /**
     * Se invoca cuando la operación de descarga o consulta a la base de datos
     * finaliza con éxito.
     *
     * @param challenges Una lista (ArrayList) que contiene todos los objetos {@link ChallengeData}
     *                   recuperados y procesados correctamente desde Firestore.
     */
    void onSuccess(ArrayList<ChallengeData> challenges);

    /**
     * Se invoca cuando la operación en la base de datos falla por algún motivo
     * (por ejemplo, pérdida de conexión a internet, falta de permisos o documento inexistente).
     *
     * @param e La excepción lanzada que contiene los detalles técnicos del error.
     */
    void onFailure(Exception e);

}
