package com.yinya.crosswarr.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;


import com.squareup.picasso.Picasso;
import com.yinya.crosswarr.MainActivity;
import com.yinya.crosswarr.R;
import com.yinya.crosswarr.databinding.CardListExerciseItemBinding;
import com.yinya.crosswarr.models.ExerciseData;

import java.util.ArrayList;

public class ExercisesUserViewAdapter extends RecyclerView.Adapter<ExercisesUserViewAdapter.ExercisesViewHolder>{

    private final ArrayList<ExerciseData> exercise;
    private final Context context;

    public ExercisesUserViewAdapter(ArrayList<ExerciseData> exercise, Context context) {
        this.exercise = exercise;
        this.context = context;
    }

    @NonNull
    @Override
    public ExercisesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        CardListExerciseItemBinding binding = CardListExerciseItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ExercisesViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ExercisesViewHolder holder, int position) {
        ExerciseData currentExercise = this.exercise.get(position);
        holder.bind(currentExercise);
        //Manejar el evento de clic
        holder.itemView.setOnClickListener(view -> {
            if (context instanceof MainActivity) {
                ((MainActivity) context).exerciseUserClicked(currentExercise, view);
            }
        });
    }

    @Override
    public int getItemCount() {
        return exercise.size();
    }

    public static class ExercisesViewHolder extends RecyclerView.ViewHolder {

        private final CardListExerciseItemBinding binding;

        public ExercisesViewHolder(@NonNull CardListExerciseItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(ExerciseData exercise) {

            // Binding del nombre del ejercicio
            binding.tvExerciseNameCardListExerciseItem.setText(exercise.getName());

            // Cargamos la imagen de internet con Picasso
            Picasso.get()
                    .load(exercise.getImage())
                    // Imagen por defecto mientras carga internet
                    .placeholder(R.drawable.ic_hourglass)
                    // Imagen por si el link de internet está roto
                    .error(R.drawable.ic_error)
                    .into(binding.ivExerciseCardListExerciseItem);
        }
    }

}
