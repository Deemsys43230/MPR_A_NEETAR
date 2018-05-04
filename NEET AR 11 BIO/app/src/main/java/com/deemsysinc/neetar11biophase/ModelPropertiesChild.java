package com.deemsysinc.neetar11biophase;

import java.util.ArrayList;

/**
 * Created by Deemsys on 06-Feb-18.
 */

public class ModelPropertiesChild {
    long modelid;
    String modelname,visiblename,staticModelPath,mtlModelPath,partsName,partsModelPath,partsMtlPath,modelImage;
    boolean isSurfaceEnabled,isZoomEnabled,isPartsAvaliable,canModifySurface;
    float scaleValue;

    ArrayList<Integer> relatedIds;

    ArrayList<String> hints;


    public void setModelid(long modelid)
    {
        this.modelid=modelid;
    }
    public long getModelid()
    {
        return modelid;
    }
    public void setModelname(String modelname)
    {
        this.modelname=modelname;
    }
    public String getModelname()
    {
        return modelname;
    }
    public void setVisiblename(String visiblename)
    {
        this.visiblename=visiblename;
    }
    public String getVisiblename()
    {
        return visiblename;
    }
    public void setStaticModelPath(String staticModelPath)
    {
        this.staticModelPath=staticModelPath;
    }
    public String getStaticModelPath()
    {
        return staticModelPath;
    }
    public void setMtlModelPath(String mtlModelPath)
    {
        this.mtlModelPath=mtlModelPath;
    }
    public String getMtlModelPath()
    {
        return mtlModelPath;
    }
    public void setSurfaceEnabled(boolean isSurfaceEnabled)
    {
        this.isSurfaceEnabled=isSurfaceEnabled;
    }
    public boolean getIsSurfaceEnabled()
    {
        return isSurfaceEnabled;
    }
    public void setZoomEnabled(boolean isZoomEnabled)
    {
        this.isZoomEnabled=isZoomEnabled;
    }
    public boolean getisZoomEnable()
    {
        return isZoomEnabled;
    }
    public void setPartsAvaliable (boolean isPartsAvaliable)
    {
        this.isPartsAvaliable=isPartsAvaliable;
    }
    public boolean getIspartsAvalible()
    {
        return isPartsAvaliable;
    }
    public void setPartsName(String partsName)
    {
        this.partsName=partsName;
    }
    public String getPartsName()
    {
        return partsName;
    }
    public void setPartsModelPath(String partsModelPath)
    {
        this.partsModelPath=partsModelPath;
    }
    public String getPartsModelPath()
    {
        return partsModelPath;
    }
    public void setPartsMtlPath(String partsMtlPath)
    {
        this.partsMtlPath=partsMtlPath;
    }
    public String getPartsMtlPath()
    {
        return partsMtlPath;
    }
    public void setScaleValue(float scaleValue)
    {
        this.scaleValue=scaleValue;
    }
    public float getScaleValue()
    {
        return scaleValue;
    }
    public void setRelatedIds(ArrayList<Integer> relatedIds)
    {
        this.relatedIds=relatedIds;
    }
    public ArrayList<Integer> getRelatedIds()
    {
        return relatedIds;
    }
    public void setModelImage(String modelImage)
    {
        this.modelImage=modelImage;
    }
    public String getModelImage()
    {
        return modelImage;
    }
    public void setCanModifySurface(boolean canModifySurface)
    {
        this.canModifySurface=canModifySurface;
    }
    public boolean getCanModifySurface()
    {
        return canModifySurface;
    }
    public void setHints(ArrayList<String> hints)
    {
        this.hints=hints;
    }
    public ArrayList<String> getHints()
    {
        return hints;
    }
}
