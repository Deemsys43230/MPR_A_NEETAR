package com.deemsysinc.kidsar;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.deemsysinc.kidsar.models.PurchaseModel;
import com.deemsysinc.kidsar.utils.Constants;
import com.deemsysinc.kidsar.utils.MyApplication;
import com.deemsysinc.kidsar.utils.PlayAudioService;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity {
    List<KidsModel> list;
    KidsAdapter adapter;
    ImageView buttonBack;
    TextView kidmodelname;
    Button playmodule;


    private ImageView infosettings;
    private RecyclerView kidrecycler;
    private boolean music_pref;
    private SharedPreferences prefs;
    private boolean firstlogin;


    int SelectedItemPOs = 0;
    Gson gson = new Gson();
    Type type = new TypeToken<List<PurchaseModel>>() {
    }.getType();
    List<PurchaseModel> mymodel = new ArrayList<PurchaseModel>();
    private LinearLayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_home);

        infosettings = findViewById(R.id.info_settings);
        infosettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomeActivity.this, KidsSettings_Activity.class));
            }
        });
       /* playmodule = findViewById(R.id.playmodule);
        playmodule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(HomeActivity.this, "Button hit", Toast.LENGTH_SHORT).show();
                Bundle bundle = new Bundle();
                bundle.putInt("selectedPos", SelectedItemPOs);
                Intent goPlayer = new Intent(HomeActivity.this, UnityPlayerActivity.class);
                goPlayer.putExtras(bundle);
                //bundle.putInt("position",pos);
                startActivity(goPlayer);
            }
        });*/

       /* playmodule.setOnTouchListener(new View.OnTouchListener() {

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
        });*/

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

        /*list = Arrays.asList(
                new PuzzleModel(1, "com.deemsysinc.kidsar.basicmodels", R.drawable.alphabets_banner_locked, R.drawable.alphabets_banner_unlocked, false),
                new PuzzleModel(2, "com.deemsysinc.kidsar.premiummodel", R.drawable.animals_banner_locked, R.drawable.animals_banner_unlocked, false),
                new PuzzleModel(3, "com.deemsysinc.kidsar.basicmodels", R.drawable.fruits_banner_locked, R.drawable.fruits_banner_unlocked, false),
                new PuzzleModel(4, "", R.drawable.puzzles_banner, R.drawable.puzzles_banner, false));*/


//        kidmodelname = (TextView) findViewById(R.id.kidmodelname);
        kidrecycler = (RecyclerView) findViewById(R.id.kidrecycler);
//        kidrecycler.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getApplicationContext());
        kidrecycler.setLayoutManager(layoutManager);
        /*kidrecycler.setItemViewCacheSize(20);
        kidrecycler.setDrawingCacheEnabled(true);
        kidrecycler.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);*/
        String mypurchases = prefs.getString(Constants.purchased_product, "");
        List<PurchaseModel> fromJson = gson.fromJson(mypurchases, type);
        if (fromJson != null) {
            mymodel = new ArrayList<>(fromJson);
        }

        list = new ArrayList<>();
        list.add(new KidsModel(1, "com.deemsysinc.kidsar.basicmodels", "alphabets_banner_locked.png", "alphabets_banner_unlocked.png", false));
        list.add(new KidsModel(2, "com.deemsysinc.kidsar.premiummodel", "animals_banner_locked.png", "animals_banner_unlocked.png", false));
        list.add(new KidsModel(3, "com.deemsysinc.kidsar.basicmodels", "fruits_banner_locked.png", "fruits_banner_unlocked.png", false));
        list.add(new KidsModel(4, "", "puzzles_banner.png", "puzzles_banner.png", false));

        adapter = new KidsAdapter(getApplicationContext(), list, mymodel);
        kidrecycler.setAdapter(adapter);
        adapter.notifyDataSetChanged();
      /*  kidrecycler.setItemTransitionTimeMillis(200);
        kidrecycler.setItemTransformer(new ScaleTransformer.Builder()
                .setMinScale(0.8f)
                .build());
        onItemChanged(list.get(0));*/
    }

    /*private void onItemChanged(PuzzleModel kidsModel) {
        kidmodelname.setText(kidsModel.getName());
        switch (kidsModel.getName()) {
            case "Learn Alphabets":
                SelectedItemPOs = 0;
                break;
            case "Know about Animals":
                SelectedItemPOs = 1;
                break;
            case "Know about Fruits & Vegetables":
                SelectedItemPOs = 2;
                break;
            case "Play Puzzles":
                SelectedItemPOs = 3;
                break;
        }

    }*/

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

    /*@Override
    public void onCurrentItemChanged(@Nullable RecyclerView.ViewHolder viewHolderkids, int adapterPosition) {
        int positionInDataSet = adapter.getRealPosition(adapterPosition);
        onItemChanged(list.get(positionInDataSet));
    }
*/
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
