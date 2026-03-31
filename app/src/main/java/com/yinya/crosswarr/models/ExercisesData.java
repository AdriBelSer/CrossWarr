package com.yinya.crosswarr.models;

import java.util.ArrayList;

public class ExercisesData {
    private String name;
    private String description;
    private String type;
    private String image;
    private String video;
    private ArrayList<String> materials;

    public ExercisesData() {
    }

    public ExercisesData(String name, String description, String type, String image, String video, ArrayList<String> materials) {
        this.name = name;
        this.description = description;
        this.type = type;
        this.image = image;
        this.video = video;
        this.materials = materials;
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


}
