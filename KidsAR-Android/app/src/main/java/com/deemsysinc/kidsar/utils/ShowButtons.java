package com.deemsysinc.kidsar.utils;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.deemsysinc.kidsar.UnityPlayerActivity;

import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Handler;

public class ShowButtons {
    private Context context;
    private static ShowButtons instance;

    public Context context1;

    Timer timer;

    Button takeScreenShot,volume,animation;

    android.os.Handler handler;


    public UnityPlayerActivity unityPlayerActivity;


    public int isAnimationPlayingCompleted=0;

    public ShowButtons() {
        this.instance = this;
        handler=new android.os.Handler();
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

    public void setButtons(Button takeScreenShot,Button volume,Button animation)
    {
        this.takeScreenShot=takeScreenShot;
        this.volume=volume;
        this.animation=animation;
    }

    public void ShowButtons(String message) {

        //Toast.makeText(this.context, message, Toast.LENGTH_SHORT).show();
        Log.d("MethodCalled","True");
        takeScreenShot.setVisibility(View.VISIBLE);
        volume.setVisibility(View.VISIBLE);
        if(message.equals("AnimalPlacerController"))
        {
            animation.setText("Stop");
            animation.setVisibility(View.VISIBLE);
//        unityPlayerActivity.RemovesCallbacks();
//        handler.removeCallbacks(runnable);
//        handler.postDelayed(runnable,14000);
        }
        else if(message.equals("URCHIN"))
        {
            animation.setVisibility(View.INVISIBLE);
        }

    }
    public void HideButtons(String message)
    {
        takeScreenShot.setVisibility(View.GONE);
        volume.setVisibility(View.GONE);
        animation.setVisibility(View.GONE);
    }
    public void SetContext(Context context)
    {
        this.context1=context;
    }
    public void RemoveCallBacks()
    {
        handler.removeCallbacks(runnable);
    }
    public void ResumeAnimation(String message)
    {
        Log.d("ResumeAnimation","Called"+" "+message);

        if(message.equals("Playing"))
        {
            animation.setText("Stop");
        }
        else if(message.equals("Stopped"))
        {
            animation.setText("Start");
        }
//        if(isAnimationPlayingCompleted==1)
//        {
//            unityPlayerActivity.ResumeAnimation();
//
//        }
    }
    public void setIsAnimationPlayingCompleted(int value)
    {
        this.isAnimationPlayingCompleted=value;
    }
    class SheduledTimer extends TimerTask
    {

        @Override
        public void run() {
            animation.setText("Start");
        }
    }
    public void setActivity(UnityPlayerActivity unityPlayerActivity)
    {
        this.unityPlayerActivity=unityPlayerActivity;
    }
    Runnable runnable=new Runnable() {
        @Override
        public void run() {
            animation.setText("Start");
        }
    };





}
