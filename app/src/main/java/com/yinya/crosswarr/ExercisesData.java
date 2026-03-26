package com.yinya.crosswarr;

public class ExercisesData {

    private String name;
    private String description;
    private String type;
    private String image;
    private boolean state;

    public ExercisesData() {
    }

    public ExercisesData(String description, String name, String type, String image, boolean state) {
        this.description = description;
        this.name = name;
        this.type = type;
        this.image = image;
        this.state = state;
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

    public boolean isState() {
        return state;
    }

    public void setState(boolean state) {
        this.state = state;
    }
}
