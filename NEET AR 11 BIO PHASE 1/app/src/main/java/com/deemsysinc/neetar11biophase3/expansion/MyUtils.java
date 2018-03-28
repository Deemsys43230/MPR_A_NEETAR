package com.deemsysinc.neetar11biophase3.expansion;






import java.io.InputStream;

/**
 * Created by Deemsys on 02-02-2018.
 */

public class MyUtils {
    public String obbContentPath="";
    private InputStream inputStream=null;
    private ZipResourceFile zipFIle=null;

    public String getObbContentPath() {
        return obbContentPath;
    }

    public void setObbContentPath(String obbContentPath) {
        this.obbContentPath = obbContentPath;
    }

    public void setInputStream(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    public InputStream getInputStream() {
        return inputStream;
    }

    public void setZipFIle(ZipResourceFile zipFIle) {
        this.zipFIle = zipFIle;
    }

    public ZipResourceFile getZipFIle() {
        return zipFIle;
    }
}
