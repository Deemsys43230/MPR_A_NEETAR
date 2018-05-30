package com.deemsysinc.kidsar.models;

import java.util.ArrayList;

public class ParentModel {
    int levelId;

    String levelName;


    ArrayList<AlphapetsModel> alphapetsModels;

    public void setLevelId(int levelId)
    {
        this.levelId=levelId;
    }
    public int getLevelId()
    {
        return levelId;
    }
    public void setLevelName(String levelName)
    {
        this.levelName=levelName;
    }
    public String getLevelName()
    {
        return levelName;
    }
    public void setAlphapetsModels(ArrayList<AlphapetsModel> alphapetsModels)
    {
        this.alphapetsModels=alphapetsModels;
    }

    public ArrayList<AlphapetsModel> getAlphapetsModels() {
        return alphapetsModels;
    }
}
