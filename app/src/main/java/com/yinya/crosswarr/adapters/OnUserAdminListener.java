package com.yinya.crosswarr.adapters;

import com.yinya.crosswarr.models.UserData;

/**
 * Interfaz que define los eventos de interacción para la lista de usuarios en el modo administrador.
 * Actúa como puente de comunicación para delegar las acciones (clics) detectadas en el adaptador
 * hacia el Fragmento o Actividad responsable de gestionar la edición o eliminación de las cuentas.
 */
public interface OnUserAdminListener {

    /**
     * Se invoca cuando el administrador hace clic sobre la tarjeta de un usuario específico en la lista.
     * Habitualmente se utiliza para navegar a la pantalla de edición de perfil,
     * donde el administrador puede modificar el rol, nombre u otros ajustes de ese usuario.
     *
     * @param user El objeto {@link UserData} que contiene toda la información del usuario seleccionado.
     * @param view La vista de la interfaz de usuario que ha recibido el evento (la tarjeta completa).
     */
    void onUserClick(UserData user, android.view.View view);

    /**
     * Se invoca cuando el administrador hace clic explícitamente en el botón de eliminar (papelera)
     * asociado a un usuario de la lista.
     *
     * @param user El objeto {@link UserData} que representa la cuenta que el administrador desea eliminar.
     */
    void onDeleteClick(UserData user);
}
