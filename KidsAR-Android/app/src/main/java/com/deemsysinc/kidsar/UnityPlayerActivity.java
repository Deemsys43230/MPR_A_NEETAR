package com.deemsysinc.kidsar;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toolbar;

import com.deemsysinc.kidsar.adapter.AlphapetsAdapter;
import com.deemsysinc.kidsar.models.AlphapetsModel;
import com.deemsysinc.kidsar.utils.GridSpacingItemDecoration;
import com.unity3d.player.UnityPlayer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;


public class UnityPlayerActivity extends Activity implements View.OnClickListener
{
    protected UnityPlayer mUnityPlayer; // don't change the name of this variable; referenced from native code



    FrameLayout sceneUnity;


    Toolbar USceneToolbar;

    Button takeScreenShot,playAudio;

    ArrayList<AlphapetsModel> alphapets;

    RecyclerView alphapetsList;


    RecyclerView.LayoutManager layoutManager;


    Dialog dialog;


    AlphapetsAdapter alphapetsAdapter;


    ImageView showAlphapets,NavigateBack;


    ImageView closeAlphapets;


    public static View.OnClickListener onClickListener;

    // Setup activity layout
    @Override protected void onCreate(Bundle savedInstanceState)
    {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);

        mUnityPlayer = new UnityPlayer(this);
        //setContentView(mUnityPlayer);
        setContentView(R.layout.activity_arview);
        alphapets=new ArrayList<>();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                UnityPlayer.UnitySendMessage("ARCore Device","NavigateScene","LEARN ALPHABETS");
            }
        },500);

        LoadLevels(0);
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
        closeAlphapets=(dialog).findViewById(R.id.alphapets_close);
        closeAlphapets.setOnClickListener(this);
        alphapetsList=(dialog).findViewById(R.id.list_alphapets);
        onClickListener=new AlphapetsClickListner(this);
        alphapetsList.addItemDecoration(new GridSpacingItemDecoration(4, 50, false));
        layoutManager=new GridLayoutManager(UnityPlayerActivity.this,4);
        alphapetsList.setLayoutManager(layoutManager);
        alphapetsAdapter=new AlphapetsAdapter(UnityPlayerActivity.this,alphapets);
        alphapetsList.setAdapter(alphapetsAdapter);
//        setActionBar(USceneToolbar);
//        getActionBar().setDisplayHomeAsUpEnabled(true);
//        getActionBar().setTitle("");
        sceneUnity=findViewById(R.id.unity_scene_view);
        sceneUnity.addView(mUnityPlayer.getView(),FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
        mUnityPlayer.requestFocus();
    }

    private void LoadLevels(int position) {
        try {
            JSONArray kidsArray=new JSONArray(loadJSONFromAsset());
            JSONObject kidsObject=kidsArray.getJSONObject(position);
            JSONArray jsonArray=kidsObject.getJSONArray("models");
            for(int k=0; k<jsonArray.length(); k++)
            {
                JSONObject alphapetsObject=jsonArray.getJSONObject(k);
                AlphapetsModel alphapetsModel=new AlphapetsModel();
                alphapetsModel.setModelid(alphapetsObject.getInt("modelId"));
                alphapetsModel.setModelName(alphapetsObject.getString("modelName"));
                alphapetsModel.setModelImage(alphapetsObject.getString("modelImage"));
                alphapetsModel.setAudioSource(alphapetsObject.getString("audioName"));
                alphapets.add(alphapetsModel);

            }
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
    }

    // Resume Unity
    @Override protected void onResume()
    {
        super.onResume();
        mUnityPlayer.resume();
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
            Intent goBack=new Intent(UnityPlayerActivity.this,HomeActivity.class);
            goBack.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(goBack);
        }
    }
    class AlphapetsClickListner implements View.OnClickListener
    {
        Context context;

        public AlphapetsClickListner(Context context)
        {
            this.context=context;
        }

        @Override
        public void onClick(View view) {
            UnityPlayer.UnitySendMessage("ObjectPlacer","DeleteObject","Yes");
            int itemPosition=alphapetsList.getChildLayoutPosition(view);
            UnityPlayer.UnitySendMessage("ObjectPlacer","ChangeAlphapet",alphapets.get(itemPosition).getModelName());
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
