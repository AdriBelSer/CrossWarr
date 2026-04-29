package com.yinya.crosswarr.network;

import java.util.Map;

/**
 * Interfaz de comunicación (Callback) genérica utilizada para gestionar de forma asíncrona
 * las lecturas y consultas directas a la base de datos Firebase Firestore.
 * A diferencia de los callbacks de negocio específicos, este devuelve los datos en bruto (raw)
 * en forma de diccionario (Map), delegando el parseo a objetos locales a los servicios correspondientes.
 */
public interface IFirebaseCallback {

    /**
     * Se invoca cuando la operación de lectura o consulta en la base de datos
     * finaliza exitosamente y devuelve información.
     *
     * @param data Un mapa (diccionario) clave-valor que contiene los datos exactos
     *             obtenidos del documento de Firestore, tal y como están almacenados en la nube.
     */
    void onSuccess(Map<String, Object> data);

    /**
     * Se invoca cuando la operación en la base de datos falla por cualquier motivo
     * (por ejemplo, el documento no existe, reglas de seguridad de Firebase, o falta de red).
     *
     * @param e La excepción lanzada que contiene los detalles técnicos del error.
     */
    void onFailure(Exception e);
}
