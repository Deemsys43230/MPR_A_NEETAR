package com.deemsysinc.kidsar.models;

public class PuzzleModel {
    int id;
    String image;
    String name;

    public PuzzleModel(int id, String name, String image) {
        this.id = id;
        this.name = name;
        this.image = image;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
