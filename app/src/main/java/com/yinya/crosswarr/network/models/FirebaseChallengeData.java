package com.yinya.crosswarr.network.models;

import com.google.firebase.Timestamp;
import com.yinya.crosswarr.models.ChallengeData;

import java.util.HashMap;

public class FirebaseChallengeData {

    private String id;
    private String title;
    private Timestamp creationDate;
    private Timestamp activationDate;
    private int challengeTime;
    private String exerciseSup;
    private String exerciseInf;
    private String exerciseCore;
    private boolean state;
    private int repetitionSup;
    private int repetitionInf;
    private int repetitionCore;
    private String type;
    private boolean requiresEquipment;

    public FirebaseChallengeData() {
    }

    public FirebaseChallengeData(String id, String title, Timestamp creationDate, Timestamp activationDate, int challengeTime, String exerciseSup, String exerciseInf, String exerciseCore, boolean state) {
        this.id = id;
        this.title = title;
        this.creationDate = creationDate;
        this.activationDate = activationDate;
        this.challengeTime = challengeTime;
        this.exerciseSup = exerciseSup;
        this.exerciseInf = exerciseInf;
        this.exerciseCore = exerciseCore;
        this.state = state;
    }

    public FirebaseChallengeData(String id, String title, Timestamp creationDate, Timestamp activationDate, int challengeTime, String exerciseSup, String exerciseInf, String exerciseCore, boolean state, int repetitionSup, int repetitionInf, int repetitionCore, String type, boolean requiresEquipment) {
        this.id = id;
        this.title = title;
        this.creationDate = creationDate;
        this.activationDate = activationDate;
        this.challengeTime = challengeTime;
        this.exerciseSup = exerciseSup;
        this.exerciseInf = exerciseInf;
        this.exerciseCore = exerciseCore;
        this.state = state;
        this.repetitionSup = repetitionSup;
        this.repetitionInf = repetitionInf;
        this.repetitionCore = repetitionCore;
        this.type = type;
        this.requiresEquipment = requiresEquipment;
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
        return challengeTime;
    }

    public void setChallenteTime(int challenteTime) {
        this.challengeTime = challenteTime;
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

    public int getChallengeTime() {
        return challengeTime;
    }

    public void setChallengeTime(int challengeTime) {
        this.challengeTime = challengeTime;
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

    public boolean isRequiresEquipment() {
        return requiresEquipment;
    }

    public void setRequiresEquipment(boolean requiresEquipment) {
        this.requiresEquipment = requiresEquipment;
    }

    public ChallengeData asChallengeData() {
        ChallengeData challengeData = new ChallengeData(
                this.id, this.title, this.creationDate, this.activationDate, this.challengeTime, this.exerciseSup, this.exerciseInf,
                this.exerciseCore, this.state, this.repetitionSup, this.repetitionInf, this.repetitionCore, this.type, this.requiresEquipment);
        return challengeData;
    }

    public HashMap<String, Object> asHashMap() {
        HashMap<String, Object> hashMap = new HashMap<String, Object>();
        hashMap.put("id", this.id);
        hashMap.put("title", this.title);
        hashMap.put("creationDate", this.creationDate);
        hashMap.put("activationDate", this.activationDate);
        hashMap.put("challengeTime", this.challengeTime);
        hashMap.put("exerciseSup", this.exerciseSup);
        hashMap.put("exerciseInf", this.exerciseInf);
        hashMap.put("exerciseCore", this.exerciseCore);
        hashMap.put("state", this.state);
        hashMap.put("repetitionSup", this.repetitionSup);
        hashMap.put("repetitionInf", this.repetitionInf);
        hashMap.put("repetitionCore", this.repetitionCore);
        hashMap.put("type", this.type);
        hashMap.put("requiresEquipment", this.requiresEquipment);
        return hashMap;
    }
}
