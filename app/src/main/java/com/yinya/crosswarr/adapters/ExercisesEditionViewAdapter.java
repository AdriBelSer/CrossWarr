package com.yinya.crosswarr.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.yinya.crosswarr.databinding.CardListExercisesEditionItemBinding;
import com.yinya.crosswarr.models.ExerciseData;

import java.util.ArrayList;

/**
 * Adaptador para el RecyclerView que gestiona la lista de ejercicios en la vista de edición (modo administrador).
 * Se encarga de inflar las tarjetas de los ejercicios, mostrar sus nombres y capturar
 * las interacciones del administrador (seleccionar para visualizar detalles o pulsar para eliminar).
 */
public class ExercisesEditionViewAdapter extends RecyclerView.Adapter<ExercisesEditionViewAdapter.ExercisesEditionViewHolder> {
    private final ArrayList<ExerciseData> exercise;
    private final Context context;
    private final OnExerciseAdminListener listener;

    /**
     * Constructor del adaptador de edición de ejercicios.
     *
     * @param exercise Lista de objetos {@link ExerciseData} con la información de los ejercicios disponibles.
     * @param context  Contexto de la aplicación o la actividad que aloja el RecyclerView.
     * @param listener Interfaz para capturar y gestionar los eventos de clic en los elementos (vista y borrado).
     */
    public ExercisesEditionViewAdapter(ArrayList<ExerciseData> exercise, Context context, OnExerciseAdminListener listener) {
        this.exercise = exercise;
        this.context = context;
        this.listener = listener;
    }

    /**
     * Invocado por el RecyclerView cuando necesita crear un nuevo {@link ExercisesEditionViewHolder}.
     * Infla el diseño XML de la tarjeta de edición de ejercicios utilizando View Binding.
     *
     * @param parent   El ViewGroup al que se adjuntará la nueva vista.
     * @param viewType El tipo de vista del nuevo elemento.
     * @return Un nuevo ExercisesEditionViewHolder que contiene la vista de la tarjeta.
     */
    @NonNull
    @Override
    public ExercisesEditionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        CardListExercisesEditionItemBinding binding = CardListExercisesEditionItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ExercisesEditionViewHolder(binding);
    }

    /**
     * Invocado por el RecyclerView para mostrar los datos en una posición específica de la lista.
     * Vincula la información del ejercicio al ViewHolder y establece los eventos de clic para
     * la tarjeta entera (ver detalles) y para el botón de la papelera (eliminar).
     *
     * @param holder   El ViewHolder que debe actualizarse con el contenido del elemento.
     * @param position La posición del ejercicio dentro de la lista de datos.
     */
    @Override
    public void onBindViewHolder(@NonNull ExercisesEditionViewHolder holder, int position) {
        ExerciseData currentExercise = this.exercise.get(position);
        holder.bind(currentExercise);

        // Listener para detectar el clic en toda la tarjeta (abrir detalles/edición)
        holder.itemView.setOnClickListener(view -> {
            if (listener != null) {
                listener.onExerciseClick(currentExercise, view);
            }
        });

        // Listener específico para el botón de eliminar ejercicio
        holder.binding.btnDeleteExercise.setOnClickListener(view -> {
            if (listener != null) {
                listener.onDeleteClick(currentExercise);
            }
        });
    }

    /**
     * Devuelve el número total de ejercicios que el adaptador está gestionando.
     *
     * @return El tamaño de la lista de ejercicios.
     */
    @Override
    public int getItemCount() {
        return exercise.size();
    }

    /**
     * Clase interna estática que actúa como ViewHolder para los elementos del RecyclerView.
     * Mantiene las referencias a las vistas de la tarjeta para evitar búsquedas repetitivas (findViewById)
     * y mejorar el rendimiento al hacer scroll.
     */
    public static class ExercisesEditionViewHolder extends RecyclerView.ViewHolder {
        private final CardListExercisesEditionItemBinding binding;

        /**
         * Constructor del ViewHolder.
         *
         * @param binding El objeto de View Binding con las referencias a los elementos visuales de la tarjeta.
         */
        public ExercisesEditionViewHolder(@NonNull CardListExercisesEditionItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        /**
         * Asigna los datos de un objeto {@link ExerciseData} a las vistas de la interfaz.
         * En este caso, establece el nombre del ejercicio en el TextView correspondiente.
         *
         * @param exercise El objeto de datos que contiene la información del ejercicio a mostrar.
         */
        public void bind(ExerciseData exercise) {
            binding.tvExerciseNameCardListExercisesEditionItem.setText(exercise.getName());
        }
    }
}
