package com.yinya.crosswarr.network;

import com.yinya.crosswarr.models.UserData;

import java.util.ArrayList;

/**
 * Interfaz de comunicación (Callback) utilizada para gestionar de forma asíncrona
 * las respuestas de la base de datos relacionadas con los Usuarios.
 * Actúa como puente entre la capa de red (FirebaseUserService) y la interfaz de usuario,
 * permitiendo notificar al hilo principal cuándo la lista de perfiles está lista para ser
 * mostrada o si ha ocurrido algún problema durante la descarga.
 */
public interface IUsersCallback {

    /**
     * Se invoca cuando la operación de lectura o consulta en la base de datos
     * finaliza con éxito.
     *
     * @param users Una lista (ArrayList) que contiene todos los objetos {@link UserData}
     *              recuperados y procesados correctamente desde Firestore.
     */
    void onSuccess(ArrayList<UserData> users);

    /**
     * Se invoca cuando la operación en la base de datos falla por cualquier motivo
     * (por ejemplo, pérdida de conexión, falta de permisos de administrador o errores de lectura).
     *
     * @param e La excepción lanzada que contiene los detalles técnicos del error para su depuración.
     */
    void onFailure(Exception e);
}
