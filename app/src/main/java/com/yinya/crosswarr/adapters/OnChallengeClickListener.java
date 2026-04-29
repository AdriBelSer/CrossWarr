package com.yinya.crosswarr.adapters;

import android.view.View;

import com.yinya.crosswarr.models.ChallengeData;

/**
 * Interfaz que define los eventos de interacción para la lista de desafíos en la vista del usuario estándar.
 * Actúa como puente de comunicación para delegar las acciones de clic desde el adaptador
 * hacia el Fragmento o Actividad responsable de la navegación.
 */
public interface OnChallengeClickListener {

    /**
     * Se invoca cuando el usuario estándar hace clic sobre la tarjeta de un desafío.
     * Este evento suele utilizarse para navegar a la pantalla de detalles del desafío seleccionado,
     * donde el usuario podrá ver las instrucciones e iniciar el temporizador.
     *
     * @param challengeData El objeto {@link ChallengeData} que contiene toda la información del desafío en el que se hizo clic.
     * @param view          La vista de la interfaz de usuario que ha recibido el evento (la tarjeta o elemento de la lista).
     */
    void onChallengeClick(ChallengeData challengeData, View view);
}
