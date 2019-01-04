package com.deemsysinc.kidsar.utils;

import android.content.Context;
import android.os.Bundle;

import com.google.firebase.analytics.FirebaseAnalytics;

public class EventLoggerFireBase {

    private FirebaseAnalytics firebaseAnalytics;


    public EventLoggerFireBase(Context context)
    {
        firebaseAnalytics=FirebaseAnalytics.getInstance(context);
    }


    public void LogUserEvents(String eventName, Bundle params)
    {
        firebaseAnalytics.logEvent(eventName,params);
    }

}
