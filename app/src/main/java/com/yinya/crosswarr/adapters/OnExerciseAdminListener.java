package com.yinya.crosswarr.adapters;

import com.yinya.crosswarr.models.ExerciseData;

public interface OnExerciseAdminListener {
    void onExerciseClick(ExerciseData exercise, android.view.View view);
    void onDeleteClick(ExerciseData exercise);
}

