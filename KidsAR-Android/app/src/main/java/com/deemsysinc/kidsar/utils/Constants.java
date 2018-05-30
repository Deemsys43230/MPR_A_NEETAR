package com.deemsysinc.kidsar.utils;

import android.os.Build;

import com.deemsysinc.kidsar.HelpModel;

import java.util.ArrayList;
import java.util.List;

public class Constants {
    public static final int FIXED_JOB_ID = 10001;
    public static final String START_JOB = "Start";
    public static final String PAUSE_JOB = "Pause";
    public static final String RESUME_JOB = "Resume";
    public static final String STOP_JOB = "Stop";

    // APP Shared Preference Name
    public static final String AppPreferences = "ARKids_Preferences";
    public static final String alertrate_pref = "Alert_Rating";
    public static final String purchased_product = "Purchased_Product";
    public static final String music = "isMusicOn";
    public static final String kidname = "KidName";

    //For Contact Page Send User Feedback

    public static final String osName = "Android" + " " + Build.VERSION.RELEASE;
    public static final String DeviceName = Build.MANUFACTURER + " " + Build.MODEL;
    public static final String contactfromemail = "deemmobtest@gmail.com";
    public static final String contactfrompassword = "deemsys@123";
    public static final String contacttoemail = "deemmobtest@gmail.com";

    //    public static final String contacttoemail = "incdeemsys@gmail.com";
    public static final String contacthost = "smtp.gmail.com";
    public static final String contactemailsubject = "Kids AR - User query";
    public static final String contactemailbody = "Hello,\n\n User from Kids AR Android application has sent a query to you.\n\n USER DETAILS : \n\n";
    public static final String contactemailbody2 = "\n\n DEVICE DETAILS : \n\n Device Name : " + DeviceName + "\n OS Version : " + osName + "\n App Version : ";


    public static final List<HelpModel> getModelList() {
        List<HelpModel> modelList = new ArrayList<>();
        modelList.add(new HelpModel(1, "Welcome to our augmented reality kids AR app.", false, true));
        modelList.add(new HelpModel(2, "To get started give your name on welcome screen to explore the fun of augmented realities.", false, true));
        modelList.add(new HelpModel(3, "Choose a category on your home screen and press start.", false, true));
        modelList.add(new HelpModel(4, "You will be given few models for free and you need to purchase locked models for using it.", false, true));
        modelList.add(new HelpModel(5, "You can customise your app under settings button.", false, true));
        modelList.add(new HelpModel(6, "For unlocking all models go to Settings->Purchase-> click buy.", false, true));
        modelList.add(new HelpModel(7, "For restoring your purchases on new device or reinstalling app goto Settings -> Purchase -> click restore.", false, true));
        modelList.add(new HelpModel(8, "Any Queries ? Please send us through Settings->Contact us", false, true));
        modelList.add(new HelpModel(9, "", false, false));
        modelList.add(new HelpModel(10, " How to use 3d models ?", true, false));
        modelList.add(new HelpModel(11, "To simulate a 3d model, press the menu button on top of your camera screen to choose a model from the list.", false, true));
        modelList.add(new HelpModel(12, "Hold your camera still while we detect flat surfaces for placing models.", false, true));
        modelList.add(new HelpModel(13, "Once you see surface detected, Tap on the surface to place your models.", false, true));
        modelList.add(new HelpModel(14, "To zoom your models use two finger pinch gesture.", false, true));
        modelList.add(new HelpModel(15, "To rotate your models use single finger and slide left or right.", false, true));
        modelList.add(new HelpModel(16, "", false, false));
        modelList.add(new HelpModel(17, "Happy Learning !", false, false));
        return modelList;
    }
}
