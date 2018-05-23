package com.deemsysinc.kidsar;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.deemsysinc.kidsar.utils.Constants;

public class SpalshActivity extends AppCompatActivity {
    TextView arkids;
    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);
   /*     arkids = findViewById(R.id.arkids);
        arkids.getPaint().setShader(GradientText.TextShader(this));*/

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                prefs = getSharedPreferences(Constants.AppPreferences, MODE_PRIVATE);
                String firstlogin = prefs.getString(Constants.kidname, "");
                if (!firstlogin.isEmpty()) {
                    startActivity(new Intent(SpalshActivity.this, HomeActivity.class));
                } else {
                    startActivity(new Intent(SpalshActivity.this, KidsAR_Main.class));
                }
            }
        }, 1000);
    }
}
