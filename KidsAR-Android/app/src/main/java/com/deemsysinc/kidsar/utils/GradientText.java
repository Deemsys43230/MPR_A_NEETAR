package com.deemsysinc.kidsar.utils;

import android.content.Context;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.support.v4.content.ContextCompat;

import com.deemsysinc.kidsar.R;

public class GradientText {

    public static Shader TextShader(Context context) {
        return new LinearGradient(0, 0, 20, 20,
                new int[]{ContextCompat.getColor(context, R.color.solid_orange), ContextCompat.getColor(context, R.color.solid_orange_light)},
                new float[]{0, 1}, Shader.TileMode.CLAMP);
    }
}
