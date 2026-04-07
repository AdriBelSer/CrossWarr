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
    private List<Map<String, Object>> exercises;
    private boolean state;

    public ChallengeData() {
    }

    public ChallengeData(String id,String title, Timestamp creationDate, Timestamp activationDate, int challenteTime, List<Map<String, Object>> exercises, boolean state) {
        this.id = id;
        this.title = title;
        this.creationDate = creationDate;
        this.activationDate = activationDate;
        this.challenteTime = challenteTime;
        this.exercises = exercises;
        this.state = state;
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

    public List<Map<String, Object>> getExercises() {
        return exercises;
    }

    public void setExercises(List<Map<String, Object>> exercises) {
        this.exercises = exercises;
    }

    public boolean isState() {
        return state;
    }

    public void setState(boolean state) {
        this.state = state;
    }

    public FirebaseChallengeData asFirebaseChallengeData() {
        FirebaseChallengeData firebaseChallengeData = new FirebaseChallengeData(
                this.id, this.title, this.creationDate, this.activationDate, this.challenteTime, this.exercises, this.state);
        return firebaseChallengeData;
    }
}
