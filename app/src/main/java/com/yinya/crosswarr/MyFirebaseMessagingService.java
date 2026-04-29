package com.yinya.crosswarr;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

/**
 * Servicio en segundo plano (Background Service) encargado de gestionar la integración
 * con Firebase Cloud Messaging (FCM).
 * Esta clase intercepta las notificaciones push entrantes y gestiona el ciclo de vida
 * del token único del dispositivo, permitiendo el envío de alertas personalizadas a los usuarios.
 */
public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = "FCM_Service";

    /**
     * Se invoca automáticamente cuando el dispositivo recibe un mensaje de Firebase.
     * Nota de comportamiento: Si el mensaje es una notificación (Notification Payload),
     * este método solo se ejecutará si la aplicación está en **primer plano** (abierta).
     * Si la app está en segundo plano o cerrada, el sistema operativo mostrará la
     * notificación automáticamente sin pasar por aquí.
     *
     * @param remoteMessage Objeto que contiene toda la información del mensaje recibido,
     *                      incluyendo los datos adicionales (Data Payload) y la notificación visible.
     */
    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        // Si el mensaje trae una notificación visible (Title & Body)
        if (remoteMessage.getNotification() != null) {
            String titulo = remoteMessage.getNotification().getTitle();
            String cuerpo = remoteMessage.getNotification().getBody();
            Log.d(TAG, "Notificación recibida: " + titulo + " - " + cuerpo);

        }
    }

    /**
     * Se invoca automáticamente cuando Firebase genera un nuevo token (identificador único)
     * para esta instancia de la aplicación en este dispositivo físico.
     * Esto ocurre generalmente en la primera instalación, si el usuario borra los datos de la app,
     * o si Firebase decide refrescar el token por motivos de seguridad.
     *
     * @param token El nuevo token alfanumérico generado por FCM.
     */
    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);
        Log.d(TAG, "Nuevo Token de Firebase: " + token);

    }
}

