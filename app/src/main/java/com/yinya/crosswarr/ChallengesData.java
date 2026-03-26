package com.yinya.crosswarr;

import com.google.firebase.Timestamp;

import java.util.List;
import java.util.Map;

public class ChallengesData {

    private String title;
    private Timestamp creationDate;
    private Timestamp activationDate;
    private int challenteTime;
    private List<Map<String, Object>> exercises;

    public ChallengesData() {
    }

    public ChallengesData(String title, Timestamp creationDate, Timestamp activationDate, int challenteTime, List<Map<String, Object>> exercises) {
        this.title = title;
        this.creationDate = creationDate;
        this.activationDate = activationDate;
        this.challenteTime = challenteTime;
        this.exercises = exercises;
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

    public List<Map<String, Object>> getExercises() {
        return exercises;
    }

    public void setExercises(List<Map<String, Object>> exercises) {
        this.exercises = exercises;
    }
}
