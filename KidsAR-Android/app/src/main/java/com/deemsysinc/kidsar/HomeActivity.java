package com.deemsysinc.kidsar;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.deemsysinc.kidsar.utils.Constants;
import com.deemsysinc.kidsar.utils.MyApplication;
import com.deemsysinc.kidsar.utils.PlayAudioService;
import com.yarolegovich.discretescrollview.DSVOrientation;
import com.yarolegovich.discretescrollview.DiscreteScrollView;
import com.yarolegovich.discretescrollview.InfiniteScrollAdapter;
import com.yarolegovich.discretescrollview.transform.ScaleTransformer;

import java.util.Arrays;
import java.util.List;

public class HomeActivity extends AppCompatActivity implements DiscreteScrollView.OnItemChangedListener<RecyclerView.ViewHolder> {
    List<KidsModel> list;
    InfiniteScrollAdapter<KidsAdapter.MyKidsHolder> adapter;
    ImageView buttonBack;
    TextView kidmodelname;
    Button playmodule;


    private ImageView infosettings;
    private DiscreteScrollView kidrecycler;
    private boolean music_pref;
    private SharedPreferences prefs;
    private boolean firstlogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_home);

        infosettings = findViewById(R.id.info_settings);
        playmodule = findViewById(R.id.playmodule);
        playmodule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(HomeActivity.this, "Button hit", Toast.LENGTH_SHORT).show();
            }
        });

        playmodule.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Log.i("IMAGE", "motion event: " + event.toString());
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        playmodule.setScaleX(0.9f);
                        playmodule.setScaleY(0.9f);
                        break;
                    }
                    case MotionEvent.ACTION_UP: {
                        playmodule.setScaleX(1f);
                        playmodule.setScaleY(1f);
                        break;
                    }
                }
                return false;
            }
        });

        prefs = getSharedPreferences(Constants.AppPreferences, MODE_PRIVATE);
        music_pref = prefs.getBoolean(Constants.music, true);
        String kidname = prefs.getString(Constants.kidname, "");
        if (!kidname.isEmpty()) {
            if (music_pref) {
                if (!isMyServiceRunning(PlayAudioService.class))
                    startService(new Intent(this, PlayAudioService.class));
            } else {
                if (isMyServiceRunning(PlayAudioService.class))
                    stopService(new Intent(HomeActivity.this, PlayAudioService.class));
            }
        }

        infosettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomeActivity.this, KidsSettings_Activity.class));
            }
        });


        list = Arrays.asList(
                new KidsModel(1, "Learn Alphabets", R.drawable.alphabet),
                new KidsModel(2, "Know about Animals", R.drawable.animals),
                new KidsModel(3, "Know about Fruits & Vegetables", R.drawable.fruit_vegetable),
                new KidsModel(4, "Play Puzzles", R.drawable.puzzles));

        kidmodelname = (TextView) findViewById(R.id.kidmodelname);
        kidrecycler = (DiscreteScrollView) findViewById(R.id.kidrecycler);
        kidrecycler.setOrientation(DSVOrientation.HORIZONTAL);
        kidrecycler.addOnItemChangedListener(this);
        adapter = InfiniteScrollAdapter.wrap(new KidsAdapter(this, list));
        kidrecycler.setAdapter(adapter);
        kidrecycler.setItemTransitionTimeMillis(200);
        kidrecycler.setItemTransformer(new ScaleTransformer.Builder()
                .setMinScale(0.8f)
                .build());

        onItemChanged(list.get(0));
    }

    private void onItemChanged(KidsModel kidsModel) {
        kidmodelname.setText(kidsModel.getName());
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

    @Override
    public void onBackPressed() {

    }

    @Override
    public void onCurrentItemChanged(@Nullable RecyclerView.ViewHolder viewHolderkids, int adapterPosition) {
        int positionInDataSet = adapter.getRealPosition(adapterPosition);
        onItemChanged(list.get(positionInDataSet));
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
}
