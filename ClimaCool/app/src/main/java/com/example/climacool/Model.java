package com.example.climacool;

import java.util.function.Consumer;

public class Model {

    private String name;
    private int image;

    // Constructor
    public Model(String name, int image) {
        this.name = name;
        this.image = image;
    }

    // Getter and Setter
    public String getName() {
        return name;
    }

    public void setName(String course_name) {
        this.name = name;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int course_image) {
        this.image = image;
    }
}