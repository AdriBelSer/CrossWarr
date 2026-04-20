package com.yinya.crosswarr;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = "FCM_Service";

    // Este método se dispara SOLO cuando recibes una notificación y TIENES LA APP ABIERTA
    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        // Si el mensaje trae una notificación visible
        if (remoteMessage.getNotification() != null) {
            String titulo = remoteMessage.getNotification().getTitle();
            String cuerpo = remoteMessage.getNotification().getBody();
            Log.d(TAG, "Notificación recibida: " + titulo + " - " + cuerpo);

            // TODO: Si quieres que salga el aviso flotante aunque la app esté abierta,
            // tendrías que usar aquí la clase NotificationCompat de Android.
        }
    }

    // Este método se dispara automáticamente si Firebase cambia el Token del dispositivo
    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);
        Log.d(TAG, "Nuevo Token de Firebase: " + token);

        // TODO: Si estás guardando los tokens en UserData,
        // aquí deberías actualizar ese token en tu Firestore.
    }
}

