package com.deemsysinc.kidsar.utils;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class ShowButtons {
    private Context context;
    private static ShowButtons instance;




    Button takeScreenShot,volume;

    public ShowButtons() {
        this.instance = this;
    }

    public static ShowButtons instance() {
        if(instance == null) {
            instance = new ShowButtons();
        }
        return instance;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public void setButtons(Button takeScreenShot,Button volume)
    {
        this.takeScreenShot=takeScreenShot;
        this.volume=volume;
    }

    public void ShowButtons(String message) {

        //Toast.makeText(this.context, message, Toast.LENGTH_SHORT).show();
        Log.d("MethodCalled","True");

        takeScreenShot.setVisibility(View.VISIBLE);
        volume.setVisibility(View.VISIBLE);
    }
    public void HideButtons(String message)
    {
        takeScreenShot.setVisibility(View.GONE);
        volume.setVisibility(View.GONE);
    }




}
