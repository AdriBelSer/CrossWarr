package com.yinya.crosswarr.network;

import com.yinya.crosswarr.models.ExerciseData;
import java.util.ArrayList;

    public interface IExercisesCallback {
        // Fíjate en la diferencia: aquí devolvemos la lista preparada, no un Map
        void onSuccess(ArrayList<ExerciseData> exercises);
        void onFailure(Exception e);
    }

