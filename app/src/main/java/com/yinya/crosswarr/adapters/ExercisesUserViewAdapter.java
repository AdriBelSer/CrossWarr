package com.yinya.crosswarr.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;
import com.yinya.crosswarr.R;
import com.yinya.crosswarr.databinding.CardListExerciseItemBinding;
import com.yinya.crosswarr.models.ExerciseData;

import java.util.ArrayList;

/**
 * Adaptador para el RecyclerView que muestra la lista de ejercicios a los usuarios estándar de la aplicación.
 * Se encarga de inflar las tarjetas de los ejercicios, mostrar sus nombres, cargar las imágenes
 * correspondientes de forma asíncrona usando Picasso y gestionar los eventos de clic para ver los detalles.
 */
public class ExercisesUserViewAdapter extends RecyclerView.Adapter<ExercisesUserViewAdapter.ExercisesViewHolder> {

    private final ArrayList<ExerciseData> exercise;
    private final Context context;
    private final OnExerciseClickListener listener;

    /**
     * Constructor del adaptador para la vista de usuario de ejercicios.
     *
     * @param exercise Lista de objetos {@link ExerciseData} con la información de los ejercicios a mostrar.
     * @param context  Contexto de la aplicación o actividad actual.
     * @param listener Interfaz para capturar y gestionar el evento de clic sobre la tarjeta de un ejercicio.
     */
    public ExercisesUserViewAdapter(ArrayList<ExerciseData> exercise, Context context, OnExerciseClickListener listener) {
        this.exercise = exercise;
        this.context = context;
        this.listener = listener;
    }

    /**
     * Invocado por el RecyclerView cuando necesita crear un nuevo {@link ExercisesViewHolder}.
     * Infla el diseño XML de la tarjeta de ejercicio para el usuario utilizando View Binding.
     *
     * @param parent   El ViewGroup al que se adjuntará la nueva vista.
     * @param viewType El tipo de vista del nuevo elemento.
     * @return Un nuevo ExercisesViewHolder que contiene la vista inflada de la tarjeta.
     */
    @NonNull
    @Override
    public ExercisesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        CardListExerciseItemBinding binding = CardListExerciseItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ExercisesViewHolder(binding);
    }

    /**
     * Invocado por el RecyclerView para mostrar los datos en una posición específica de la lista.
     * Vincula la información del ejercicio actual al ViewHolder y configura el listener
     * para reaccionar a los toques del usuario en la tarjeta.
     *
     * @param holder   El ViewHolder que debe actualizarse con el contenido del elemento.
     * @param position La posición del ejercicio dentro de la lista de datos.
     */
    @Override
    public void onBindViewHolder(@NonNull ExercisesViewHolder holder, int position) {
        ExerciseData currentExercise = this.exercise.get(position);
        holder.bind(currentExercise);

        //Manejar el evento de clic
        holder.itemView.setOnClickListener(view -> {
            if (listener != null) {

                // Llamamos al método de la interfaz externa
                listener.onExerciseClick(currentExercise, view);
            }
        });
    }

    /**
     * Devuelve el número total de ejercicios disponibles para mostrar.
     *
     * @return El tamaño de la lista de ejercicios.
     */
    @Override
    public int getItemCount() {
        return exercise.size();
    }

    /**
     * Clase interna estática que actúa como ViewHolder para los elementos del RecyclerView de ejercicios del usuario.
     * Mantiene las referencias a las vistas (nombre e imagen) para un acceso rápido y eficiente.
     */
    public static class ExercisesViewHolder extends RecyclerView.ViewHolder {

        private final CardListExerciseItemBinding binding;

        /**
         * Constructor del ViewHolder.
         *
         * @param binding El objeto de View Binding con las referencias a los elementos visuales de la tarjeta.
         */
        public ExercisesViewHolder(@NonNull CardListExerciseItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        /**
         * Asigna los datos de un objeto {@link ExerciseData} a las vistas de la interfaz.
         * Muestra el nombre en el texto y utiliza la librería Picasso para descargar y mostrar
         * la imagen del ejercicio desde una URL, configurando imágenes de carga y error.
         *
         * @param exercise El objeto de datos que contiene la información y URLs del ejercicio.
         */
        public void bind(ExerciseData exercise) {

            // Binding del nombre del ejercicio
            binding.tvExerciseNameCardListExerciseItem.setText(exercise.getName());

            // Cargamos la imagen de internet con Picasso
            Picasso.get().load(exercise.getImage())

                    // Imagen por defecto mientras carga internet
                    .placeholder(R.drawable.ic_hourglass)

                    // Imagen por si el link de internet está roto
                    .error(R.drawable.ic_error).into(binding.ivExerciseCardListExerciseItem);
        }
    }

}
