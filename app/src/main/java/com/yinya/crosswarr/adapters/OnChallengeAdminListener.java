package com.yinya.crosswarr.adapters;

import com.yinya.crosswarr.models.ChallengeData;

/**
 * Interfaz que define los eventos de interacción para la lista de desafíos en el modo administrador.
 * Sirve como puente de comunicación para enviar las acciones (clics) desde el adaptador
 * hacia el Fragmento o Actividad que lo está alojando.
 */
public interface OnChallengeAdminListener {

    /**
     * Se invoca cuando el administrador hace clic sobre cualquier parte de la tarjeta de un desafío.
     * Generalmente se utiliza para abrir la vista de detalles de dicho desafío.
     *
     * @param challenge El objeto {@link ChallengeData} que contiene toda la información del desafío seleccionado.
     * @param view      La vista (UI) exacta que ha recibido el clic, útil para animaciones o transiciones compartidas.
     */
    void onChallengeClick(ChallengeData challenge, android.view.View view);

    /**
     * Se invoca específicamente cuando el administrador hace clic en el botón de eliminar (papelera)
     * asociado a un desafío en la lista.
     *
     * @param challenge El objeto {@link ChallengeData} que el administrador ha solicitado eliminar de la base de datos.
     */
    void onDeleteClick(ChallengeData challenge);
}
