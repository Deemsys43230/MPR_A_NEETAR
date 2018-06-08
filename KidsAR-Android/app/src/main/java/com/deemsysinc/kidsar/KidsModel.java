package com.deemsysinc.kidsar;

public class KidsModel {
    int id;
    String image, imageunlock;
    String name;
    boolean purchased;

    public KidsModel(int id, String name, String image, String imageunlock, boolean islock) {
        this.id = id;
        this.name = name;
        this.image = image;
        this.imageunlock = imageunlock;
        this.purchased = islock;
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

    public boolean isPurchased() {
        return purchased;
    }

    public void setPurchased(boolean purchased) {
        this.purchased = purchased;
    }

    public String getImageunlock() {
        return imageunlock;
    }

    public void setImageunlock(String imageunlock) {
        this.imageunlock = imageunlock;
    }
}
