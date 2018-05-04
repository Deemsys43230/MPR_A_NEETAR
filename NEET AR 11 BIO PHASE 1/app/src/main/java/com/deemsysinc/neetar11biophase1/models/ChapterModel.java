package com.deemsysinc.neetar11biophase1.models;

/**
 * Created by Deemsys on 06-Feb-18.
 */

public class ChapterModel {
    String chaptername;
    int noOfModels;

    String phase1,phase2;
    public void setChaptername(String chaptername)
    {
        this.chaptername=chaptername;
    }
    public String getChaptername()
    {
        return chaptername;
    }
    public void setNoOfModels(int noOfModels)
    {
        this.noOfModels=noOfModels;
    }
    public int getNoOfModels()
    {
        return noOfModels;
    }

    public void  setPhase1(String phase1)
    {
        this.phase1=phase1;
    }
    public  String getPhase1()
    {
        return phase1;
    }
    public void  setPhase2(String phase2)
    {
        this.phase2=phase2;
    }
    public String getPhase2()
    {
        return phase2;
    }
}
