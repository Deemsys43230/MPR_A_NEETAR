package com.deemsysinc.neetar11biophase;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;



public class SplashActivity extends AppCompatActivity {
    TextView SAR,SNEET;
    Typeface primary,secondary;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        SAR=findViewById(R.id.s_Ar);
        SNEET=findViewById(R.id.s_neet);
        primary=Typeface.createFromAsset(getAssets(),"fonts/gillsansstd-bold.otf");
        secondary=Typeface.createFromAsset(getAssets(),"fonts/gillsansstd-italic.otf");
        SAR.setTypeface(primary);
        SNEET.setTypeface(secondary);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent goMenu=new Intent(SplashActivity.this,MenuActivity.class);
                startActivity(goMenu);
            }
        },3000);
    }
}
