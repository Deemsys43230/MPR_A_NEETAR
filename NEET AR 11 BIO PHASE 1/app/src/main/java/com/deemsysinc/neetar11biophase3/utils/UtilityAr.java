package com.deemsysinc.neetar11biophase3.utils;

import android.app.Activity;
import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.Window;
import android.view.WindowManager;

import com.deemsysinc.neetar11biophase3.R;


/**
 * Created by Deemsys on 13-Feb-18.
 */

public class UtilityAr {
    public static void setStausBarColor(Activity activity,Context context)
    {
        Window window = activity.getWindow();

        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

        window.setStatusBarColor(ContextCompat.getColor(context, R.color.ar_neet_theme));
    }
}
