package com.deemsysinc.kidsar;

import android.app.ActivityManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.deemsysinc.kidsar.utils.Constants;
import com.deemsysinc.kidsar.utils.MyApplication;
import com.deemsysinc.kidsar.utils.PlayAudioService;

public class KidsSettings_Activity extends AppCompatActivity implements DialogInterface.OnDismissListener, DialogInterface.OnCancelListener {
    PopupWindow popupWindow;
    View popup;
    private int height = 300, width = 200;
    ImageView cancelpopup;
    LinearLayout musiccontainer, purchasecontainer, rateuscontainer, namecontainer, helpcontainer, contactcontainer, privacycontainer;
    TextView prefences;
    Animation animationslide;
    ImageView buttonBack;
    private boolean music_pref;
    private SharedPreferences prefs;
    SwitchCompat musicSwitch;
    private String name_pref;
    private TextView kidname;
    private EditText editTextKidAlert;
    private Button savename, cancelname;
    private AlertDialog alertDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.kids_settings_activity);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        height = (int) ((displayMetrics.heightPixels) * 0.88);
        width = (int) ((displayMetrics.widthPixels) * 0.98);

        musiccontainer = findViewById(R.id.musiccontainer);
        purchasecontainer = findViewById(R.id.purchasecontainer);
        rateuscontainer = findViewById(R.id.rateuscontainer);
        helpcontainer = findViewById(R.id.helpcontainer);
        contactcontainer = findViewById(R.id.contactcontainer);
        privacycontainer = findViewById(R.id.privacycontainer);
        namecontainer = findViewById(R.id.namecontainer);
        buttonBack = findViewById(R.id.buttonBack);
        kidname = findViewById(R.id.kidname);
        musicSwitch = findViewById(R.id.musicSwitch);
        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(KidsSettings_Activity.this, HomeActivity.class);
                startActivity(intent);
                finish();
            }
        });
        prefs = getSharedPreferences(Constants.AppPreferences, MODE_PRIVATE);
        music_pref = prefs.getBoolean(Constants.music, true);
        name_pref = prefs.getString(Constants.kidname, "");
        kidname.setText(name_pref);
        if (music_pref) {
            if (!musicSwitch.isChecked())
                musicSwitch.setChecked(true);
        } else {
            if (musicSwitch.isChecked())
                musicSwitch.setChecked(false);
        }
        musicSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    prefs.edit().putBoolean(Constants.music, true).apply();
                    if (isMyServiceRunning(PlayAudioService.class)) {

                    } else {
                        startService(new Intent(KidsSettings_Activity.this, PlayAudioService.class));
                    }
                } else {
                    prefs.edit().putBoolean(Constants.music, false).apply();
                    stopService(new Intent(KidsSettings_Activity.this, PlayAudioService.class));
                }
            }
        });

       /* animationslide = AnimationUtils.loadAnimation(KidsSettings_Activity.this,
                R.anim.fade_in);
        popup = getLayoutInflater().inflate(R.layout.name_popup, null);
        popup.setMinimumWidth(width);
        popup.setMinimumHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        popup.setPadding(10, 10, 10, 10);

        //Get View from Popup Window
        cancelpopup = (ImageView) popup.findViewById(R.id.cancel_popup);
        cancelpopup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });*/

        namecontainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(KidsSettings_Activity.this);
                LayoutInflater inflater = getLayoutInflater();
                View dialogView = inflater.inflate(R.layout.settings_name_layout, null);
                builder.setView(dialogView);

                editTextKidAlert = (EditText) dialogView.findViewById(R.id.kidnameupdate);
                editTextKidAlert.setText(kidname.getText().toString());
                editTextKidAlert.setSelection(editTextKidAlert.getText().length());
                editTextKidAlert.setFilters(new InputFilter[]{new InputFilter.AllCaps()});
                editTextKidAlert.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                        Character c = ' ';
                        if (charSequence.length() >= 1) {
                            if (c.equals(charSequence.charAt(0))) {
                                editTextKidAlert.getText().delete(0, 1);
                                editTextKidAlert.setSelection(editTextKidAlert.getText().length());
                            }
                        }
                    }

                    @Override
                    public void afterTextChanged(Editable et) {
                    }
                });
                savename = (Button) dialogView.findViewById(R.id.savename);
                cancelname = (Button) dialogView.findViewById(R.id.cancelname);

                savename.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (editTextKidAlert.getText().toString().isEmpty()) {
                            Toast.makeText(KidsSettings_Activity.this, "Please Enter Your Name", Toast.LENGTH_SHORT).show();
                        } else {
                            String kidnamefromalter = editTextKidAlert.getText().toString();
                            prefs.edit().putString(Constants.kidname, kidnamefromalter).apply();
                            kidname.setText(kidnamefromalter);
                            alertDialog.cancel();
                        }
                    }
                });
                cancelname.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.cancel();
                    }
                });

                alertDialog = builder.create();
                alertDialog.setOnDismissListener(KidsSettings_Activity.this);
                alertDialog.setOnCancelListener(KidsSettings_Activity.this);
                alertDialog.show();
            }
        });

        musiccontainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*popup.startAnimation(animationslide);
                popupWindow = new PopupWindow(KidsSettings_Activity.this);
                popupWindow.setContentView(popup);
                popupWindow.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
                popupWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
                popupWindow.setBackgroundDrawable(null);
                popupWindow.setFocusable(true);
                popupWindow.setOutsideTouchable(true);
                popupWindow.update();
                popupWindow.showAtLocation(musiccontainer, Gravity.CENTER, 0, 0);*/
            }
        });
        purchasecontainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(KidsSettings_Activity.this, PurchaseActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up);
            }
        });
        rateuscontainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(Constants.KIDS_AR_APP_PLAY_STORE_URL));
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up);
            }
        });
        helpcontainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(KidsSettings_Activity.this, HelpActivity.class);
                intent.putExtra("ActivityString","kidssettings");
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up);
            }
        });
        contactcontainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(KidsSettings_Activity.this, ContactActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up);
            }
        });
        privacycontainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(KidsSettings_Activity.this, PrivacyActivity.class);
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
    public void onDismiss(DialogInterface dialog) {
        getWindow().setSoftInputMode
                (WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        getWindow().setSoftInputMode
                (WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(KidsSettings_Activity.this, HomeActivity.class);
        startActivity(intent);
        finish();
    }
}
