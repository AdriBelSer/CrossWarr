package com.yinya.crosswarr.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.yinya.crosswarr.databinding.CardListExercisesEditionItemBinding;
import com.yinya.crosswarr.models.ExerciseData;

import java.util.ArrayList;

public class ExercisesEditionViewAdapter extends RecyclerView.Adapter<ExercisesEditionViewAdapter.ExercisesEditionViewHolder> {
    private final ArrayList<ExerciseData> exercise;
    private final Context context;
    private final OnExerciseAdminListener listener;

    public ExercisesEditionViewAdapter(ArrayList<ExerciseData> exercise, Context context, OnExerciseAdminListener listener) {
        this.exercise = exercise;
        this.context = context;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ExercisesEditionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        CardListExercisesEditionItemBinding binding = CardListExercisesEditionItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ExercisesEditionViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ExercisesEditionViewHolder holder, int position) {
        ExerciseData currentExercise = this.exercise.get(position);
        holder.bind(currentExercise);
        holder.itemView.setOnClickListener(view -> {
            if (listener != null) {
                listener.onExerciseClick(currentExercise, view);
            }
        });
        holder.binding.btnDeleteExercise.setOnClickListener(view -> {
            if (listener != null) {
                listener.onDeleteClick(currentExercise);
            }
        });

    }

    @Override
    public int getItemCount() {
        return exercise.size();

    }


    public static class ExercisesEditionViewHolder extends RecyclerView.ViewHolder {
        private final CardListExercisesEditionItemBinding binding;

        public ExercisesEditionViewHolder(@NonNull CardListExercisesEditionItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(ExerciseData exercise) {
            binding.tvExerciseNameCardListExercisesEditionItem.setText(exercise.getName());

        }
    }
}
