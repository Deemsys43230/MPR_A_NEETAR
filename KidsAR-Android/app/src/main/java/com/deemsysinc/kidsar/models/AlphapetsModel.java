package com.deemsysinc.kidsar.models;

public class AlphapetsModel {



    int modelid;

    String modelImage,modelName,audioSource;

    boolean isPurchased;
    public void setModelid(int modelid)
    {
        this.modelid=modelid;
    }
    public int getModelid()
    {
        return modelid;
    }
    public void setModelImage(String modelImage)
    {
        this.modelImage=modelImage;
    }
    public String getModelImage()
    {
        return modelImage;
    }
    public void setModelName(String modelName)
    {
        this.modelName=modelName;
    }
    public String getModelName()
    {
        return modelName;
    }
    public void setAudioSource(String audioSource)
    {
        this.audioSource=audioSource;
    }
    public String getAudioSource()
    {
        return audioSource;
    }
    public void isPurchased(boolean isPurchased)
    {
        this.isPurchased=isPurchased;
    }
    public boolean getIsPurchased()
    {
        return isPurchased;
    }

}
