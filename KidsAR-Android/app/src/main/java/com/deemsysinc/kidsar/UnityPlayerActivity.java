package com.deemsysinc.kidsar;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toolbar;

import com.deemsysinc.kidsar.adapter.AlphapetsAdapter;
import com.deemsysinc.kidsar.models.AlphapetsModel;
import com.deemsysinc.kidsar.models.ParentModel;
import com.deemsysinc.kidsar.utils.Constants;
import com.deemsysinc.kidsar.utils.GridSpacingItemDecoration;
import com.deemsysinc.kidsar.utils.MyApplication;
import com.deemsysinc.kidsar.utils.PlayAudioService;
import com.unity3d.player.UnityPlayer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;



public class UnityPlayerActivity extends Activity implements View.OnClickListener
{
    protected UnityPlayer mUnityPlayer; // don't change the name of this variable; referenced from native code



    FrameLayout sceneUnity;


    Toolbar USceneToolbar;

    Button takeScreenShot,playAudio;

    ArrayList<ParentModel> parentModels;

    RecyclerView alphapetsList;


    RecyclerView.LayoutManager layoutManager;


    Dialog dialog;


    AlphapetsAdapter alphapetsAdapter;


    ImageView showAlphapets,NavigateBack;


    ImageView closeAlphapets;


    public static View.OnClickListener onClickListener;


    Bundle extras;


    int getSelectedPos=0;


    TextView dialogHeader,mainHeaderName;


    LinearLayout rootDialogLayout;

    // Setup activity layout
    @Override protected void onCreate(Bundle savedInstanceState)
    {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);

        mUnityPlayer = new UnityPlayer(this);
        //setContentView(mUnityPlayer);
        setContentView(R.layout.activity_arview);
        parentModels=new ArrayList<>();
        if(getIntent().getExtras()!=null)
        {
            extras=getIntent().getExtras();
            getSelectedPos=extras.getInt("selectedPos");
        }
        Log.d("PrintSelectedName",""+getSelectedPos);

//        alphapets.add("Alligator");
//        alphapets.add("Bear");
//        alphapets.add("Cow");
//        alphapets.add("Dog");
//        alphapets.add("Elephant");
//        alphapets.add("Fox");
//        alphapets.add("Giraffe");
//        alphapets.add("Horse");
//        alphapets.add("Impala");
//        alphapets.add("jaguar");
//        alphapets.add("Kangaroo");
//        alphapets.add("Lion");
//        alphapets.add("Monkey");
//        alphapets.add("Nightingale");
//        alphapets.add("Owl");
//        alphapets.add("Penguin");
//        alphapets.add("Quail");
//        alphapets.add("Rabbit");
//        alphapets.add("Seal");
//        alphapets.add("Tiger");
//        alphapets.add("Urchin");
//        alphapets.add("Vulture");
//        alphapets.add("Wolf");
//        alphapets.add("Xenops bird");
//        alphapets.add("Yak");
//        alphapets.add("Zebra");
        mainHeaderName=findViewById(R.id.model_name);
        takeScreenShot=findViewById(R.id.take_screenshot);
        showAlphapets=findViewById(R.id.alphapet_collections);
        showAlphapets.setOnClickListener(this);
        NavigateBack=findViewById(R.id.custom_navigation);
        NavigateBack.setOnClickListener(this);
        playAudio=findViewById(R.id.play_music);
        takeScreenShot.setOnClickListener(this);
        playAudio.setOnClickListener(this);
        int animationResource=R.style.DialogAnimation_2;
        dialog=new Dialog(UnityPlayerActivity.this);
        dialog.setContentView(R.layout.dialog_alphapets);
        dialog.setCancelable(true);
        dialog.getWindow().getAttributes().windowAnimations=animationResource;
        rootDialogLayout=(dialog).findViewById(R.id.alphapets_dialog_root);
        closeAlphapets=(dialog).findViewById(R.id.alphapets_close);
        closeAlphapets.setOnClickListener(this);
        dialogHeader=(dialog).findViewById(R.id.dialog_header);
        alphapetsList=(dialog).findViewById(R.id.list_alphapets);
        ChangeDialogHeaderName(getSelectedPos);
        onClickListener=new AlphapetsClickListner(this);

        LoadLevels(getSelectedPos);
        mainHeaderName.setText(parentModels.get(0).getLevelName());
        UnityPlayer.UnitySendMessage("ARCore Device","NavigateScene",parentModels.get(0).getLevelName());
//        setActionBar(USceneToolbar);
//        getActionBar().setDisplayHomeAsUpEnabled(true);
//        getActionBar().setTitle("");
        sceneUnity=findViewById(R.id.unity_scene_view);
        sceneUnity.addView(mUnityPlayer.getView(),FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
        mUnityPlayer.requestFocus();
    }

    private void ChangeDialogHeaderName(int getSelectedPos) {
        switch (getSelectedPos)
        {
            case 0:
                rootDialogLayout.setBackgroundResource(R.drawable.alphabet);
                dialogHeader.setText("ALPHABETS");
                alphapetsList.addItemDecoration(new GridSpacingItemDecoration(4, 50, false));
                layoutManager=new GridLayoutManager(UnityPlayerActivity.this,4);
                alphapetsList.setLayoutManager(layoutManager);
                break;
            case 1:
                rootDialogLayout.setBackgroundResource(R.drawable.animals_theme);
                dialogHeader.setText("ANIMALS & BIRDS");
                alphapetsList.addItemDecoration(new GridSpacingItemDecoration(3, 50, false));
                layoutManager=new GridLayoutManager(UnityPlayerActivity.this,3);
                alphapetsList.setLayoutManager(layoutManager);
                break;
            case 2:
                dialogHeader.setText("FRUITS & VEGETABLES");
                alphapetsList.addItemDecoration(new GridSpacingItemDecoration(3, 50, false));
                layoutManager=new GridLayoutManager(UnityPlayerActivity.this,3);
                alphapetsList.setLayoutManager(layoutManager);
                break;
        }
    }

    private void LoadLevels(int position) {
        try {
            ArrayList<AlphapetsModel> alphapetsModels=new ArrayList<>();
            JSONArray kidsArray=new JSONArray(loadJSONFromAsset());
                JSONObject levelObject=kidsArray.getJSONObject(position);
                ParentModel parentModel=new ParentModel();
                parentModel.setLevelId(levelObject.getInt("levelid"));
                parentModel.setLevelName(levelObject.getString("levelName"));
                JSONArray jsonArray=levelObject.getJSONArray("models");
                for(int k=0; k<jsonArray.length(); k++)
                {
                    JSONObject alphapetsObject=jsonArray.getJSONObject(k);
                    AlphapetsModel alphapetsModel=new AlphapetsModel();
                    alphapetsModel.setModelid(alphapetsObject.getInt("modelId"));
                    alphapetsModel.setModelName(alphapetsObject.getString("modelName"));
                    alphapetsModel.setModelImage(alphapetsObject.getString("modelImage"));
                    alphapetsModel.setAudioSource(alphapetsObject.getString("audioName"));
                    alphapetsModels.add(alphapetsModel);

                }
                parentModel.setAlphapetsModels(alphapetsModels);
                parentModels.add(parentModel);

            Log.d("ParentModelSize",""+parentModels.size());
            alphapetsAdapter=new AlphapetsAdapter(UnityPlayerActivity.this,parentModels.get(0).getAlphapetsModels(),getSelectedPos);
            alphapetsList.setAdapter(alphapetsAdapter);
            //JSONObject kidsObject=kidsArray.getJSONObject(position);


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    @Override protected void onNewIntent(Intent intent)
    {
        // To support deep linking, we need to make sure that the client can get access to
        // the last sent intent. The clients access this through a JNI api that allows them
        // to get the intent set on launch. To update that after launch we have to manually
        // replace the intent with the one caught here.
        setIntent(intent);
    }

    // Quit Unity
    @Override protected void onDestroy ()
    {
        mUnityPlayer.quit();
        super.onDestroy();
    }

    // Pause Unity
    @Override protected void onPause()
    {
        super.onPause();
        mUnityPlayer.pause();
        ((MyApplication) this.getApplication()).startActivityTransitionTimer(this);
    }

    // Resume Unity
    @Override protected void onResume()
    {
        super.onResume();
        mUnityPlayer.resume();
        MyApplication myApp = (MyApplication) this.getApplication();
        if (myApp.wasInBackground) {
            PlayAudioService.onResumePlayer();
        }
        myApp.stopActivityTransitionTimer();
    }

    @Override protected void onStart()
    {
        super.onStart();
        mUnityPlayer.start();
    }

    @Override protected void onStop()
    {
        super.onStop();
        mUnityPlayer.stop();
    }

    // Low Memory Unity
    @Override public void onLowMemory()
    {
        super.onLowMemory();
        mUnityPlayer.lowMemory();
    }

    // Trim Memory Unity
    @Override public void onTrimMemory(int level)
    {
        super.onTrimMemory(level);
        if (level == TRIM_MEMORY_RUNNING_CRITICAL)
        {
            mUnityPlayer.lowMemory();
        }
    }

    // This ensures the layout will be correct.
    @Override public void onConfigurationChanged(Configuration newConfig)
    {
        super.onConfigurationChanged(newConfig);
        mUnityPlayer.configurationChanged(newConfig);
    }

    // Notify Unity of the focus change.
    @Override public void onWindowFocusChanged(boolean hasFocus)
    {
        super.onWindowFocusChanged(hasFocus);
        mUnityPlayer.windowFocusChanged(hasFocus);
    }

    // For some reason the multiple keyevent type is not supported by the ndk.
    // Force event injection by overriding dispatchKeyEvent().
    @Override public boolean dispatchKeyEvent(KeyEvent event)
    {
        if (event.getAction() == KeyEvent.ACTION_MULTIPLE)
            return mUnityPlayer.injectEvent(event);
        return super.dispatchKeyEvent(event);
    }

    // Pass any events not handled by (unfocused) views straight to UnityPlayer
    @Override public boolean onKeyUp(int keyCode, KeyEvent event)     { return mUnityPlayer.injectEvent(event); }
    @Override public boolean onKeyDown(int keyCode, KeyEvent event)   { return mUnityPlayer.injectEvent(event); }
    @Override public boolean onTouchEvent(MotionEvent event)          { return mUnityPlayer.injectEvent(event); }
    /*API12*/ public boolean onGenericMotionEvent(MotionEvent event)  { return mUnityPlayer.injectEvent(event); }

    @Override
    public void onClick(View view) {
        if(view==takeScreenShot)
        {
            UnityPlayer.UnitySendMessage("ObjectPlacer","SaveImage","My img {0}.png");
        }
        if(view==playAudio)
        {
            UnityPlayer.UnitySendMessage("ObjectPlacer","PlayAudio","Yes");
        }
        if(view==showAlphapets)
        {
            dialog.show();
        }
        if(view==closeAlphapets)
        {
            dialog.dismiss();
        }
        if(view==NavigateBack)
        {
            mUnityPlayer.quit();
            Intent goBack=new Intent(UnityPlayerActivity.this,HomeActivity.class);
//            goBack.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
//            //goBack.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(goBack);
//            finish();
//            UnityPlayer.currentActivity.startActivity(goBack);
        }
    }
    class AlphapetsClickListner implements View.OnClickListener
    {
        Context context;
        TextView alertTitle, alert_message;
        Button okalert, cancelalert;
        AlertDialog alertDialog;
        int alertrate;
        private SharedPreferences prefs;

        public AlphapetsClickListner(Context context)
        {
            this.context=context;
        }

        @Override
        public void onClick(View view) {
            prefs = getSharedPreferences(Constants.AppPreferences, MODE_PRIVATE);
            int itemPosition = alphapetsList.getChildLayoutPosition(view);
            alertrate = prefs.getInt(Constants.alertrate_pref, 0);
            Log.d("Test_Value", "Value:" + alertrate);
            if (parentModels.get(0).getAlphapetsModels().get(itemPosition).getIsPurchased()) {
                alertrate = alertrate + 1;
                prefs.edit().putInt(Constants.alertrate_pref, alertrate).apply();
                if (alertrate == 4 || alertrate == 8 || alertrate == 12) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(UnityPlayerActivity.this);
                    LayoutInflater inflater = getLayoutInflater();
                    View dialogView = inflater.inflate(R.layout.alertdialog, null);
                    builder.setView(dialogView);
                    alertTitle = (TextView) dialogView.findViewById(R.id.alertTitle);
                    alertTitle.setText(R.string.alertString);
                    alert_message = (TextView) dialogView.findViewById(R.id.alert_message);
                    alert_message.setText(R.string.alertmessage);
                    okalert = (Button) dialogView.findViewById(R.id.okalert);
                    okalert.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            alertDialog.dismiss();
                            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.supercell.clashofclans"));
                            startActivity(intent);
                            overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up);
                        }
                    });
                    cancelalert = (Button) dialogView.findViewById(R.id.cancelalert);
                    cancelalert.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            alertDialog.dismiss();
                        }
                    });
                    alertDialog = builder.create();
                    alertDialog.show();
                }
            }
            UnityPlayer.UnitySendMessage("ObjectPlacer","DeleteObject","Yes");
            //int itemPosition=alphapetsList.getChildLayoutPosition(view);
            UnityPlayer.UnitySendMessage("ObjectPlacer","ChangeAlphapet",parentModels.get(0).getAlphapetsModels().get(itemPosition).getModelName());
            dialog.dismiss();

        }
    }
    public String loadJSONFromAsset() {
        String json = null;
        try {
            InputStream is = getAssets().open("kids.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }

    @Override
    public void onBackPressed() {
        Intent goBack=new Intent(UnityPlayerActivity.this,HomeActivity.class);
        goBack.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(goBack);
    }
}
