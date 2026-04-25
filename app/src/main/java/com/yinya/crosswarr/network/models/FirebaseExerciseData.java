package com.yinya.crosswarr.network.models;

import com.yinya.crosswarr.models.ExerciseData;
import com.yinya.crosswarr.models.UserData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class FirebaseExerciseData {
    private String id;
    private String name;
    private String description;
    private String type;
    private String image;
    private String video;
    private ArrayList<String> materials;
    private boolean isUsed;

    public FirebaseExerciseData() {
    }

    public FirebaseExerciseData(String id, String name, String description, String type, String image, String video, boolean isUsed) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.type = type;
        this.image = image;
        this.video = video;
        this.isUsed = isUsed;
    }

    public FirebaseExerciseData(String id, String name, String description, String type, String image, String video, ArrayList<String> materials, boolean isUsed) {
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

    public ExerciseData asExerciseData() {
        ExerciseData exerciseData = new ExerciseData(
                this.id, this.name, this.description, this.type, this.image, this.video, this.materials, this.isUsed);
        return exerciseData;
    }

    public HashMap<String, Object> asHashMap() {
        HashMap<String, Object> hashMap = new HashMap<String, Object>();
        hashMap.put("id", this.id);
        hashMap.put("name", this.name);
        hashMap.put("description", this.description);
        hashMap.put("type", this.type);
        if (this.image != null) {
            hashMap.put("image", this.image);
        } else {
            hashMap.put("image", "");
        }
        if (this.video != null) {
            hashMap.put("video", this.video);
        } else {
            hashMap.put("video", "");
        }
        hashMap.put("materials", this.materials);
        hashMap.put("isUsed", this.isUsed);

        return hashMap;
    }
}
