package com.yinya.crosswarr.network;

import com.yinya.crosswarr.models.ExerciseData;

import java.util.ArrayList;

public interface IExercisesCallback {
    void onSuccess(ArrayList<ExerciseData> exercises);
    void onFailure(Exception e);
}

