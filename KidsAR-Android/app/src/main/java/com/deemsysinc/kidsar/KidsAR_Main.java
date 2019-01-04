package com.deemsysinc.kidsar;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.speech.tts.Voice;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.deemsysinc.kidsar.utils.Constants;
import com.deemsysinc.kidsar.utils.EventLoggerFireBase;
import com.deemsysinc.kidsar.utils.MyApplication;
import com.deemsysinc.kidsar.utils.PlayAudioService;
import com.google.firebase.analytics.FirebaseAnalytics;

import java.util.Locale;

public class KidsAR_Main extends AppCompatActivity implements TextToSpeech.OnInitListener {
    private ImageView imageinfoicon, playstopmusicbtn;
    private EditText arkidsname;
    private TextView musictext;
    private Button startkidsAR;
    private MediaPlayer objPlayer;
    private View contentview;
    private SharedPreferences prefs;
    private boolean music_pref;

    TextToSpeech Speech;
    //String settheVoice = "en-us-x-sfg#male_1-local";

    String settheVoice="en-us-x-sfg#female_2-local";

    private boolean speak = false;
    private String navigateintent = "";

    EventLoggerFireBase eventLoggerFireBase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.kidsar_main);
        eventLoggerFireBase=new EventLoggerFireBase(KidsAR_Main.this);
        imageinfoicon = findViewById(R.id.info_image);
        playstopmusicbtn = findViewById(R.id.buttonMusic);
        arkidsname = findViewById(R.id.arkids_name);
        musictext = findViewById(R.id.musictext);
        arkidsname.setVisibility(View.GONE);
        Intent intent = getIntent();
        if (intent != null) {
            navigateintent = intent.getStringExtra("Navigate");
        }
        startkidsAR = findViewById(R.id.start_kidsAR);
        contentview = findViewById(R.id.contentview);
        Speech = new TextToSpeech(KidsAR_Main.this, KidsAR_Main.this,"com.google.android.tts");

        prefs = getSharedPreferences(Constants.AppPreferences, MODE_PRIVATE);
        music_pref = prefs.getBoolean(Constants.music, true);
        arkidsname.setFilters(new InputFilter[]{new InputFilter.AllCaps()});
        arkidsname.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                Character c = ' ';
                if (charSequence.length() >= 1) {
                    if (c.equals(charSequence.charAt(0))) {
                        arkidsname.getText().delete(0, 1);
                        arkidsname.setSelection(arkidsname.getText().length());
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable et) {
            }
        });
        /*String kidname = prefs.getString(Constants.kidname, "");
        arkidsname.setText(kidname);
        if (arkidsname.getText().toString().length() >= 1) {
            startkidsAR.setVisibility(View.VISIBLE);
        } else {
            startkidsAR.setVisibility(View.GONE);
        }*/
        if (music_pref) {
            playstopmusicbtn.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.music_off));
            musictext.setText("Mute");
            if (!isMyServiceRunning(PlayAudioService.class))
                startService(new Intent(this, PlayAudioService.class));
        } else {
            playstopmusicbtn.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.music_on));
            musictext.setText("Unmute");
            if (isMyServiceRunning(PlayAudioService.class))
                stopService(new Intent(KidsAR_Main.this, PlayAudioService.class));
        }
//        KidName,isMusicOn

        playstopmusicbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                music_pref = prefs.getBoolean(Constants.music, true);
                if (music_pref) {
                    playstopmusicbtn.setImageDrawable(ContextCompat.getDrawable(KidsAR_Main.this, R.drawable.music_on));
                    musictext.setText("Unmute");
                    prefs.edit().putBoolean(Constants.music, false).apply();
                    if (isMyServiceRunning(PlayAudioService.class))
                        stopService(new Intent(KidsAR_Main.this, PlayAudioService.class));
                } else {
                    playstopmusicbtn.setImageDrawable(ContextCompat.getDrawable(KidsAR_Main.this, R.drawable.music_off));
                    musictext.setText("Mute");
                    prefs.edit().putBoolean(Constants.music, true).apply();
                    if (!isMyServiceRunning(PlayAudioService.class))
                        startService(new Intent(KidsAR_Main.this, PlayAudioService.class));
                }
            }
        });

        startkidsAR.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Log.i("IMAGE", "motion event: " + event.toString());
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        startkidsAR.setScaleX(0.9f);
                        startkidsAR.setScaleY(0.9f);
                        break;
                    }
                    case MotionEvent.ACTION_UP: {
                        startkidsAR.setScaleX(1f);
                        startkidsAR.setScaleY(1f);
                        break;
                    }
                }
                return false;
            }
        });

        contentview.startAnimation(ViewAnimation.RightAnimation_in());
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                arkidsname.setVisibility(View.VISIBLE);
                arkidsname.startAnimation(ViewAnimation.RightAnimation_in());
                if (navigateintent == null) {
                    Intent intent = new Intent(KidsAR_Main.this, HelpActivity.class);
                    intent.putExtra("ActivityString", "kidsmain");
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up);
                }
            }
        }, 500);

        arkidsname.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() >= 1) {
                    startkidsAR.setVisibility(View.VISIBLE);
                } else {
                    startkidsAR.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        startkidsAR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(arkidsname.getText().toString())) {
                    Toast.makeText(KidsAR_Main.this, "Please Enter your Name", Toast.LENGTH_SHORT).show();
                } else {
                    //Crashlytics.getInstance().crash(); Force Crash For Analytics
                    eventLoggerFireBase.LogUserEvents(Constants.getFireBaseEventName().get(0),null);
                    if (speak) {
                        Speak();
                    }
                    prefs.edit().putString(Constants.kidname, arkidsname.getText().toString()).apply();
                    Intent intent = new Intent(KidsAR_Main.this, HomeActivity.class);
                    intent.putExtra("name", arkidsname.getText().toString());
                    startActivity(intent);
                }
            }
        });

        imageinfoicon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(KidsAR_Main.this, HelpActivity.class);
                intent.putExtra("ActivityString", "kidsmain");
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onResume() {
        super.onResume();
        MyApplication myApp = (MyApplication) this.getApplication();
        if (myApp.wasInBackground) {
            PlayAudioService.onResumePlayer();
        }
        myApp.stopActivityTransitionTimer();
    }

    @Override
    protected void onPause() {
        super.onPause();
        ((MyApplication) this.getApplication()).startActivityTransitionTimer(this);
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void onBackPressed() {

    }

    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            int result = Speech.setLanguage(Locale.US);
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.d("KidsAR", "This Language is not Supported");
            } else {
                speak = true;
            }
        }
    }

    private void Speak() {
        CharSequence getts = "Welcome " + arkidsname.getText();
        Speech.setPitch(1f);
        Speech.setSpeechRate(0.7f);
        for (Voice voice : Speech.getVoices()) {
            if (voice.getName().equals(settheVoice)) {
                Speech.setVoice(voice);
            }
        }

        Speech.speak(getts, TextToSpeech.QUEUE_FLUSH, null, "id1");


    }
}
