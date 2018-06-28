package com.deemsysinc.kidsar.utils;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.MediaController;

import com.deemsysinc.kidsar.R;

import static android.content.res.Configuration.ORIENTATION_LANDSCAPE;
import static android.content.res.Configuration.ORIENTATION_PORTRAIT;

public class FullScreenMediaPlayer extends MediaController {

    private ImageView fullScreen;

    public FullScreenMediaPlayer(Context context) {
        super(context);
    }

    @Override
    public void setAnchorView(View view) {
        super.setAnchorView(view);
        fullScreen = new ImageView(super.getContext());
        fullScreen.setBackground(new ColorDrawable(Color.TRANSPARENT));
        fullScreen.setPadding(10, 10, 10, 10);
        LayoutParams params =
                new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.END;
        params.rightMargin = 50;
        params.topMargin = 40;
        addView(fullScreen, params);

        int orientation = getResources().getConfiguration().orientation;
        switch (orientation) {
            case ORIENTATION_PORTRAIT:
                fullScreen.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.fit_to_screen));
                break;
            case ORIENTATION_LANDSCAPE:
                fullScreen.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.fit_normal_screen));
                break;
        }
        fullScreen.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                int orientation = getResources().getConfiguration().orientation;
                switch (orientation) {
                    case ORIENTATION_PORTRAIT:
                        fullScreen.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.fit_normal_screen));
                        ((Activity) getContext()).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                        break;
                    case ORIENTATION_LANDSCAPE:
                        fullScreen.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.fit_to_screen));
                        ((Activity) getContext()).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                        break;
                }
            }
        });
    }

    @Override
    public void show(int timeout) {
        super.show(timeout);
    }
}
