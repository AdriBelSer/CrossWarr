package com.yinya.crosswarr.models;

import com.google.firebase.Timestamp;
import com.yinya.crosswarr.network.models.FirebaseChallengeData;
import com.yinya.crosswarr.network.models.FirebaseUserData;

import java.util.List;
import java.util.Map;

public class ChallengeData {
    private String id;
    private String title;
    private Timestamp creationDate;
    private Timestamp activationDate;
    private int challenteTime;
    private String exerciseSup;
    private String exerciseInf;
    private String exerciseCore;
        private boolean state;
    //TODO: cambiar el estado a activo cuando el usuario lo realice
    private int repetitionSup;
    private int repetitionInf;
    private int repetitionCore;
    private String type;

    public ChallengeData() {
    }

    public ChallengeData(String id,String title, Timestamp creationDate, Timestamp activationDate, int challenteTime, String exerciseSup, String exerciseInf, String exerciseCore, boolean state, String type) {
        this.id = id;
        this.title = title;
        this.creationDate = creationDate;
        this.activationDate = activationDate;
        this.challenteTime = challenteTime;
        this.exerciseSup = exerciseSup;
        this.exerciseInf = exerciseInf;
        this.exerciseCore = exerciseCore;
        this.state = state;
        this.type = type;
    }

    public ChallengeData(String id, String title, Timestamp creationDate, Timestamp activationDate, int challenteTime, String exerciseSup, String exerciseInf, String exerciseCore, boolean state, int repetitionSup, int repetitionInf, int repetitionCore, String type) {
        this.id = id;
        this.title = title;
        this.creationDate = creationDate;
        this.activationDate = activationDate;
        this.challenteTime = challenteTime;
        this.state = state;
        this.exerciseSup = exerciseSup;
        this.exerciseInf = exerciseInf;
        this.exerciseCore = exerciseCore;
        this.repetitionSup = repetitionSup;
        this.repetitionInf = repetitionInf;
        this.repetitionCore = repetitionCore;
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Timestamp getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Timestamp creationDate) {
        this.creationDate = creationDate;
    }

    public Timestamp getActivationDate() {
        return activationDate;
    }

    public void setActivationDate(Timestamp activationDate) {
        this.activationDate = activationDate;
    }

    public int getChallenteTime() {
        return challenteTime;
    }

    public void setChallenteTime(int challenteTime) {
        this.challenteTime = challenteTime;
    }

    public String getExerciseSup() {
        return exerciseSup;
    }

    public void setExerciseSup(String exerciseSup) {
        this.exerciseSup = exerciseSup;
    }

    public String getExerciseInf() {
        return exerciseInf;
    }

    public void setExerciseInf(String exerciseInf) {
        this.exerciseInf = exerciseInf;
    }

    public String getExerciseCore() {
        return exerciseCore;
    }

    public void setExerciseCore(String exerciseCore) {
        this.exerciseCore = exerciseCore;
    }

    public boolean isState() {
        return state;
    }

    public void setState(boolean state) {
        this.state = state;
    }

    public int getRepetitionSup() {
        return repetitionSup;
    }

    public void setRepetitionSup(int repetitionSup) {
        this.repetitionSup = repetitionSup;
    }

    public int getRepetitionInf() {
        return repetitionInf;
    }

    public void setRepetitionInf(int repetitionInf) {
        this.repetitionInf = repetitionInf;
    }

    public int getRepetitionCore() {
        return repetitionCore;
    }

    public void setRepetitionCore(int repetitionCore) {
        this.repetitionCore = repetitionCore;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public FirebaseChallengeData asFirebaseChallengeData() {
        FirebaseChallengeData firebaseChallengeData = new FirebaseChallengeData(
                this.id, this.title, this.creationDate, this.activationDate, this.challenteTime, this.exerciseSup, this.exerciseInf, this.exerciseCore, this.state);
        return firebaseChallengeData;
    }
}
