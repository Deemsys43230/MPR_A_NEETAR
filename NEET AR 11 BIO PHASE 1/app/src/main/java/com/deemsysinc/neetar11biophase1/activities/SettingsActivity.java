package com.deemsysinc.neetar11biophase1.activities;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

import com.deemsysinc.neetar11biophase1.R;
import com.deemsysinc.neetar11biophase1.utils.UtilityAr;


/**
 * Created by Deemsys on 05-Jan-18.
 */

public class SettingsActivity extends AppCompatActivity implements View.OnClickListener,CompoundButton.OnCheckedChangeListener {
    private Switch frameSwitch,planeSwitch,zoomSwitch;

    private LinearLayout resetOption;

    int framtemp=0;

    int plantemp=0;

    int zoomtemp=0;


    Bundle extras;

    android.support.v7.widget.Toolbar settingsToolbar;
    private Button doneButton;


    private SeekBar seekBar;

    TextView dymaicScale;


    String imageObject="";

    float scaleFactor=0f;


    int index=0,parentindex=0;

    float dynamiscale=0f;

    boolean isTracking=false;

    TextView planeLabel,Scalelabel,zoomLabel,settingsHeader;

    Typeface textFont,toolbarFont;

    boolean IsMounted=false;

    String obbPath;

    TextView labelHints;

    int fromsame;


    long modelid;

    boolean canModifySurface;


    LinearLayout placeOntheAirContainer;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        UtilityAr.setStausBarColor(SettingsActivity.this,SettingsActivity.this);
        setContentView(R.layout.activity_settings);
        if(getIntent().getExtras()!=null)
        {
            extras=getIntent().getExtras();
            framtemp=extras.getInt("frameKey");
            plantemp=extras.getInt("planKey");
            zoomtemp=extras.getInt("zoomKey");
            imageObject=extras.getString("ImageObject");
            scaleFactor=extras.getFloat("scaleFactor");
            dynamiscale=extras.getFloat("dynamicScale");
            index=extras.getInt("index");
            parentindex=extras.getInt("parentindex");
            IsMounted=extras.getBoolean("isMounted");
            obbPath=extras.getString("obbPath");
            modelid=extras.getLong("id");
            fromsame=extras.getInt("fromWhere");
            canModifySurface=extras.getBoolean("canModifySurface");
        }
        settingsToolbar=findViewById(R.id.settings_toolbar);
        setSupportActionBar(settingsToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setTitle("");
        settingsHeader=findViewById(R.id.head_settings);
        toolbarFont=Typeface.createFromAsset(getAssets(),"fonts/GillSans-SemiBold.ttf");
        settingsHeader.setTypeface(toolbarFont);
        settingsToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle=new Bundle();
                bundle.putInt("frameKey",framtemp);
                bundle.putInt("planKey",plantemp);
                bundle.putInt("zoomKey",zoomtemp);
                bundle.putString("ImageObject",imageObject);
                bundle.putFloat("scaleFactor",scaleFactor);
                bundle.putFloat("dynamicScale",dynamiscale);
                bundle.putInt("fromSettings",1);
                bundle.putInt("index",index);
                bundle.putInt("parentindex",parentindex);
                bundle.putBoolean("isMounted",IsMounted);
                bundle.putString("obbPath",obbPath);
                bundle.putInt("fromWhere",fromsame);
                bundle.putLong("id",modelid);
                bundle.putBoolean("canModifySurface",canModifySurface);
                Intent goBack=new Intent(SettingsActivity.this,ARViewActivity.class);
                goBack.putExtras(bundle);
                startActivity(goBack);
                overridePendingTransition(R.anim.slide_in_down,R.anim.slide_out_dwon);
            }
        });
        frameSwitch=findViewById(R.id.frame_on_off);
        planeSwitch=findViewById(R.id.plane_on_off);
        zoomSwitch=findViewById(R.id.zoom_on_off);
        resetOption=findViewById(R.id.reset_option);
        placeOntheAirContainer=findViewById(R.id.place_on_the_air_container);
        resetOption.setOnClickListener(this);
        frameSwitch.setOnCheckedChangeListener(this);
        planeSwitch.setOnCheckedChangeListener(this);
        if(canModifySurface==true)
        {
            placeOntheAirContainer.setBackgroundColor(ContextCompat.getColor(SettingsActivity.this,R.color.ar_white));
            planeSwitch.setClickable(true);
        }
        else
        {
            placeOntheAirContainer.setBackgroundColor(Color.parseColor("#4D909090"));
            planeSwitch.setClickable(false);
        }
        zoomSwitch.setOnCheckedChangeListener(this);
        doneButton=findViewById(R.id.doneButton);
        doneButton.setOnClickListener(this);
        seekBar=findViewById(R.id.get_scale);
        dymaicScale=findViewById(R.id.dynamic_scale);
        dymaicScale.setText(""+dynamiscale);
        planeLabel=findViewById(R.id.status_detection);
        Scalelabel=findViewById(R.id.text_scale);
        zoomLabel=findViewById(R.id.zoom_detection);
        textFont=Typeface.createFromAsset(getAssets(),"fonts/gillsansstd.otf");
        planeLabel.setTypeface(textFont);
        Scalelabel.setTypeface(textFont);
        zoomLabel.setTypeface(textFont);
        dymaicScale.setTypeface(textFont);
        doneButton.setTypeface(textFont);
        float temp;
        temp=dynamiscale*10;
        Log.d("GetTemp",""+temp);
        seekBar.getProgressDrawable().setColorFilter(Color.parseColor("#80909090"), PorterDuff.Mode.SRC_IN);

        seekBar.setProgress((int)temp);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                scaleFactor=0.1f*(float)i;
                Log.d("DynamicScale",""+dynamiscale);
                if(isTracking==true) {
                    dynamiscale = 0.1f * (float) i;
                }
                dymaicScale.setText(""+dynamiscale);
                Log.d("GetScaleFactor",""+scaleFactor);
                Log.d("SeekbarValue",""+scaleFactor);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                isTracking=true;

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                isTracking=false;

            }
        });
        if(framtemp==0)
        {
            frameSwitch.setChecked(false);
        }
        else
        {
            frameSwitch.setChecked(true);
        }
        if(plantemp==0)
        {
            planeSwitch.setChecked(false);
        }
        else
        {
            planeSwitch.setChecked(true);
        }
        if(zoomtemp==0)
        {
            zoomSwitch.setChecked(false);
        }
        else
        {
            zoomSwitch.setChecked(true);
        }

    }

    @Override
    public void onClick(View view) {
        if(view==resetOption)
        {
            finish();
        }
        if(view==doneButton)
        {
            doneButton.setText("Please wait...");
            doneButton.setEnabled(false);
            Bundle bundle=new Bundle();
            bundle.putInt("frameKey",framtemp);
            bundle.putInt("planKey",plantemp);
            bundle.putInt("zoomKey",zoomtemp);
            bundle.putString("ImageObject",imageObject);
            bundle.putFloat("scaleFactor",scaleFactor);
            bundle.putFloat("dynamicScale",dynamiscale);
            bundle.putInt("fromSettings",1);
            bundle.putInt("index",index);
            bundle.putInt("parentindex",parentindex);
            bundle.putBoolean("isMounted",IsMounted);
            bundle.putString("obbPath",obbPath);
            bundle.putInt("fromWhere",fromsame);
            bundle.putLong("id",modelid);
            Intent goAr=new Intent(SettingsActivity.this,ARViewActivity.class);
            goAr.putExtras(bundle);
            startActivity(goAr);
        }
//        if(view==labelHints)
//        {
//            Bundle bundle=new Bundle();
//            Intent goHint=new Intent(SettingsActivity.this,HintsActivity.class);
//            bundle.putInt("whereFrom",2);
////            bundle.putInt("index",arrayIndex);
//            goHint.putExtras(bundle);
//            startActivity(goHint);
//            overridePendingTransition(R.anim.slide_in_up,R.anim.slide_out_up);
//        }
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        if(compoundButton==frameSwitch)
        {
            if(b==true)
            {
                framtemp=1;
//                Bundle bundle=new Bundle();
//                bundle.putInt("frameKey",framtemp);
//                Intent goAr=new Intent(SettingsActivity.this,ARViewActivity.class);
//                goAr.putExtras(bundle);
//                startActivity(goAr);
            }
            else
            {
                framtemp=0;
//                Bundle bundle=new Bundle();
//                bundle.putInt("frameKey",framtemp);
//                Intent goAr=new Intent(SettingsActivity.this,ARViewActivity.class);
//                goAr.putExtras(bundle);
//                startActivity(goAr);
            }
        }
        if(compoundButton==planeSwitch)
        {
            if(b==true)
            {
                plantemp=1;
//                Bundle bundle=new Bundle();
//                bundle.putInt("planKey",plantemp);
//                Intent goAr=new Intent(SettingsActivity.this,ARViewActivity.class);
//                goAr.putExtras(bundle);
//                startActivity(goAr);

            }
            else
            {
                plantemp=0;
//                Bundle bundle=new Bundle();
//                bundle.putInt("planKey",plantemp);
//                Intent goAr=new Intent(SettingsActivity.this,ARViewActivity.class);
//                goAr.putExtras(bundle);
//                startActivity(goAr);
            }
        }
        if(compoundButton==zoomSwitch)
        {
            if(b==true)
            {
                zoomtemp=1;
            }
            else
            {
                zoomtemp=0;
            }
        }

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.hints_menu,menu);
//        labelHints = new TextView(this);
//        labelHints.setText("Hints");
//        labelHints.setTextColor(Color.parseColor("#FFFFFF"));
//        labelHints.setOnClickListener(SettingsActivity.this);
//        labelHints.setPadding(5, 0, 30, 0);
//        labelHints.setTypeface(textFont);
//        labelHints.setTextSize(18);
//        //tv.setBackground(ContextCompat.getDrawable(SubscriptionDetailActivity.this,R.drawable.textview_border));
//        menu.add(0, 12, 1, "Hints").setActionView(labelHints).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.hints:
                Bundle bundle=new Bundle();
                Intent goHint=new Intent(SettingsActivity.this,HintsActivity.class);
                bundle.putInt("whereFrom",2);
//            bundle.putInt("index",arrayIndex);
                goHint.putExtras(bundle);
                startActivity(goHint);
                overridePendingTransition(R.anim.slide_in_up,R.anim.slide_out_up);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
