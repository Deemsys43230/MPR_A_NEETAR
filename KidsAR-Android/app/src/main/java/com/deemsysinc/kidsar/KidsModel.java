package com.deemsysinc.kidsar;

public class KidsModel {
    public int id;
    public int image;
    public String name;

    public KidsModel(int id, String name, int image) {
        this.id = id;
        this.name = name;
        this.image = image;
    }

    public KidsModel(int image, String name) {
        this.image = image;
        this.name = name;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
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
