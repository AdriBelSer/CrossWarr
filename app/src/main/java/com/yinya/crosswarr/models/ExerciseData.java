package com.yinya.crosswarr.models;

import com.yinya.crosswarr.network.models.FirebaseExerciseData;
import com.yinya.crosswarr.network.models.FirebaseUserData;

import java.util.ArrayList;

public class ExerciseData {
    private String id;
    private String name;
    private String description;
    private String type;
    private String image;
    private String video;
    private ArrayList<String> materials;
    private boolean isUsed;

    public ExerciseData() {
    }

    public ExerciseData(String id, String name, String description, String type, String image, String video, boolean isUsed) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.type = type;
        this.image = image;
        this.video = video;
        this.isUsed = isUsed;
    }

    public ExerciseData(String id, String name, String description, String type, String image, String video, ArrayList<String> materials, boolean isUsed) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.type = type;
        this.image = image;
        this.video = video;
        this.materials = materials;
        this.isUsed = isUsed;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getVideo() {
        return video;
    }

    public void setVideo(String video) {
        this.video = video;
    }

    public ArrayList<String> getMaterials() {
        return materials;
    }

    public void setMaterials(ArrayList<String> materials) {
        this.materials = materials;
    }

    public boolean isUsed() {
        return isUsed;
    }

    public void setUsed(boolean used) {
        isUsed = used;
    }

    public FirebaseExerciseData asFirebaseExerciseData() {
        FirebaseExerciseData firebaseExerciseData = new FirebaseExerciseData(
                this.id, this.name, this.description, this.type, this.image, this.video, this.materials, this.isUsed);
        return firebaseExerciseData;
    }
}
