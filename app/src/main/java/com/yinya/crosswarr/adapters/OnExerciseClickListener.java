package com.yinya.crosswarr.adapters;

import android.view.View;

import com.yinya.crosswarr.models.ExerciseData;

/**
 * Interfaz que define los eventos de interacción para la lista de ejercicios en la vista del usuario estándar.
 * Actúa como un puente de comunicación para delegar las acciones (clics) detectadas en el adaptador
 * hacia el Fragmento o Actividad responsable de manejar la navegación.
 */
public interface OnExerciseClickListener {

    /**
     * Se invoca cuando el usuario estándar hace clic sobre la tarjeta de un ejercicio en la lista.
     * Este evento suele utilizarse para navegar a la pantalla de detalles del ejercicio,
     * donde el usuario podrá leer la descripción y ver el vídeo demostrativo.
     *
     * @param exercise El objeto {@link ExerciseData} que contiene toda la información del ejercicio en el que se hizo clic.
     * @param view     La vista de la interfaz de usuario que ha recibido el evento (la tarjeta completa del ejercicio).
     */
    void onExerciseClick(ExerciseData exercise, View view);
}
