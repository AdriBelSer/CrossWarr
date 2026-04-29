package com.yinya.crosswarr.adapters;

import com.yinya.crosswarr.models.ExerciseData;

/**
 * Interfaz que define los eventos de interacción para la lista de ejercicios en el modo administrador.
 * Actúa como un puente de comunicación para delegar las acciones (clics) detectadas en el adaptador
 * hacia el Fragmento o Actividad que lo aloja.
 */
public interface OnExerciseAdminListener {

    /**
     * Se invoca cuando el administrador hace clic sobre la tarjeta de un ejercicio específico en la lista.
     * Generalmente se utiliza para navegar a la pantalla de visualización de los detalles del ejercicio.
     *
     * @param exercise El objeto {@link ExerciseData} que contiene toda la información del ejercicio seleccionado.
     * @param view     La vista (interfaz de usuario) exacta que ha recibido el clic.
     */
    void onExerciseClick(ExerciseData exercise, android.view.View view);

    /**
     * Se invoca cuando el administrador hace clic en el botón de eliminar (papelera)
     * asociado a un ejercicio en la lista.
     *
     * @param exercise El objeto {@link ExerciseData} que el administrador desea eliminar de la base de datos.
     */
    void onDeleteClick(ExerciseData exercise);
}

