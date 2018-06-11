package com.deemsysinc.kidsar;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.media.AudioManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
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
import android.widget.Toast;
import android.widget.Toolbar;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.SkuDetails;
import com.deemsysinc.kidsar.adapter.AlphapetsAdapter;
import com.deemsysinc.kidsar.models.AlphapetsModel;
import com.deemsysinc.kidsar.models.ParentModel;
import com.deemsysinc.kidsar.models.PurchaseModel;
import com.deemsysinc.kidsar.utils.BillingManager;
import com.deemsysinc.kidsar.utils.Constants;
import com.deemsysinc.kidsar.utils.GridSpacingItemDecoration;
import com.deemsysinc.kidsar.utils.NetworkDetector;
import com.deemsysinc.kidsar.utils.ShowButtons;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.unity3d.player.UnityPlayer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;


public class UnityPlayerActivity extends Activity implements View.OnClickListener, BillingManager.BillingUpdatesListener {
    protected UnityPlayer mUnityPlayer; // don't change the name of this variable; referenced from native code


    FrameLayout sceneUnity;


    Toolbar USceneToolbar;

    Button takeScreenShot, playAudio;

    ArrayList<ParentModel> parentModels;

    RecyclerView alphapetsList;


    RecyclerView.LayoutManager layoutManager;


    Dialog dialog;


    AlphapetsAdapter alphapetsAdapter;


    ImageView showAlphapets, NavigateBack;


    ImageView closeAlphapets;


    public static View.OnClickListener onClickListener;


    Bundle extras;


    int getSelectedPos = 0;


    TextView dialogHeader, mainHeaderName;


    LinearLayout rootDialogLayout, alphabetlayout, animallayout, fruitlayout;

    ImageView imageBackground;

    AudioManager audioManager;

    SharedPreferences prefs;
    Gson gson = new Gson();
    Type type = new TypeToken<List<PurchaseModel>>() {
    }.getType();
    BillingManager billingManager;

    int mediaAlertCount = 0;

    TextView alertTitle, alert_message;
    Button okalert, cancelalert;
    android.support.v7.app.AlertDialog alertDialog;
    int alertrate;

    int itemPosition = -1;
    int temp = 0;

    Dialog pointerDialog;

    Button gotItButton;

    String audionName="";


    ImageView arBackgroundImage;


    boolean isBooleanSelected=false;





    // Setup activity layout
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);

        mUnityPlayer = new UnityPlayer(this);
        //setContentView(mUnityPlayer);
        setContentView(R.layout.activity_arview);
        parentModels = new ArrayList<>();
        if (getIntent().getExtras() != null) {
            extras = getIntent().getExtras();
            getSelectedPos = extras.getInt("selectedPos");
        }
        Log.d("PrintSelectedName", "" + getSelectedPos);

        billingManager = new BillingManager(UnityPlayerActivity.this, this);

        audioManager = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);
        temp = 1;


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
        mainHeaderName = findViewById(R.id.model_name);
        takeScreenShot = findViewById(R.id.take_screenshot);
        showAlphapets = findViewById(R.id.alphapet_collections);
        showAlphapets.setOnClickListener(this);
        NavigateBack = findViewById(R.id.custom_navigation);
        NavigateBack.setOnClickListener(this);
        playAudio = findViewById(R.id.play_music);
        takeScreenShot.setOnClickListener(this);
        playAudio.setOnClickListener(this);
        int animationResource = R.style.DialogAnimation_2;
        pointerDialog=new Dialog(UnityPlayerActivity.this);
        pointerDialog.setContentView(R.layout.ar_dialog);
        pointerDialog.setCancelable(true);
        pointerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        gotItButton=(pointerDialog).findViewById(R.id.got_it_button);
        gotItButton.getBackground().setAlpha(0);
        gotItButton.setOnClickListener(UnityPlayerActivity.this);
        dialog = new Dialog(UnityPlayerActivity.this);
        dialog.setContentView(R.layout.dialog_alphapets);
        dialog.setCancelable(true);
        dialog.getWindow().getAttributes().windowAnimations = animationResource;
        imageBackground = (dialog).findViewById(R.id.image_background);
        alphabetlayout = (dialog).findViewById(R.id.alphapet_transparent);
        animallayout = (dialog).findViewById(R.id.animal_transparent);
        fruitlayout = (dialog).findViewById(R.id.fandv_transparent);
        closeAlphapets = (dialog).findViewById(R.id.alphapets_close);
        closeAlphapets.setOnClickListener(this);
        dialogHeader = (dialog).findViewById(R.id.dialog_header);
        alphapetsList = (dialog).findViewById(R.id.list_alphapets);
        ChangeDialogHeaderName(getSelectedPos);
        onClickListener = new AlphapetsClickListner(this);
        ShowButtons showButtons =new ShowButtons();
        showButtons.setButtons(takeScreenShot,playAudio);
        LoadLevels(getSelectedPos);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                ShowAlert();
            }
        }, 4000);
        UnityPlayer.UnitySendMessage("ARCore Device", "NavigateScene", parentModels.get(0).getLevelName());
        sceneUnity = findViewById(R.id.unity_scene_view);
        sceneUnity.addView(mUnityPlayer.getView(), FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
        mUnityPlayer.requestFocus();
    }

    private void ShowAlert() {

        prefs = getSharedPreferences(Constants.AppPreferences, MODE_PRIVATE);
        final SharedPreferences.Editor editor = prefs.edit();
        if (prefs.getInt("alertMessageShow", 0) == 0) {
//            android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(UnityPlayerActivity.this);
//            LayoutInflater inflater = getLayoutInflater();
//            View dialogView = inflater.inflate(R.layout.alertdialog, null);
//            builder.setView(dialogView);
//            alertTitle = dialogView.findViewById(R.id.alertTitle);
//            alertTitle.setText(R.string.alertStringInfo);
//            alert_message = dialogView.findViewById(R.id.alert_message);
//            alert_message.setText(R.string.plane_message);
//            okalert = dialogView.findViewById(R.id.okalert);
//            okalert.setGravity(Gravity.CENTER_HORIZONTAL);
//            cancelalert = dialogView.findViewById(R.id.cancelalert);
//            cancelalert.setVisibility(View.GONE);
//            okalert.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    alertDialog.dismiss();
//                    editor.putInt("alertMessageShow", 1);
//                    editor.commit();
//                    dialog.show();
//                }
//            });
//            alertDialog = builder.create();
//            alertDialog.setCancelable(false);
//            alertDialog.show();
            pointerDialog.show();
        } else {
            dialog.show();
        }
//        if(prefs.getInt("alertMessageShow",0)==0)
//        {
//            android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(UnityPlayerActivity.this);
//            LayoutInflater inflater = getLayoutInflater();
//            View dialogView = inflater.inflate(R.layout.alertdialog, null);
//            builder.setView(dialogView);
//            alertTitle = (TextView) dialogView.findViewById(R.id.alertTitle);
//            alertTitle.setText(R.string.alertString);
//            alert_message = (TextView) dialogView.findViewById(R.id.alert_message);
//            alert_message.setText(R.string.plane_message);
//            okalert = (Button) dialogView.findViewById(R.id.okalert);
//            okalert.setGravity(Gravity.CENTER_HORIZONTAL);
//            cancelalert = (Button) dialogView.findViewById(R.id.cancelalert);
//            cancelalert.setVisibility(View.GONE);
//
//            okalert.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    alertDialog.dismiss();
//                    editor.putInt("alertMessageShow",1);
//                    editor.commit();
//                }
//            });
//            alertDialog = builder.create();
//            alertDialog.show();
//        }
    }

    private void ChangeDialogHeaderName(int getSelectedPos) {
        switch (getSelectedPos) {
            case 0:
                InputStream bitmap = null;
                try {
                    bitmap = getAssets().open("alphabet_banner.jpg");
//                    bitmap = getAssets().open("alphabets_theme.jpg");
                } catch (IOException e) {
                    e.printStackTrace();
                }
                audionName="audio_a";
                Log.d("PrintBitmap", "" + bitmap);
                Bitmap outImage = BitmapFactory.decodeStream(bitmap);
                closeAlphapets.setImageResource(R.drawable.animal_close);
                imageBackground.setImageBitmap(outImage);
                dialogHeader.setText("ALPHABETS");
                dialogHeader.setTextColor(ContextCompat.getColor(this, R.color.ar_white));
                mainHeaderName.setText("ALPHABETS");
                CustomFontStyle(this, dialogHeader);
                CustomFontStyle(this, mainHeaderName);
                alphabetlayout.setVisibility(View.VISIBLE);
                animallayout.setVisibility(View.GONE);
                fruitlayout.setVisibility(View.GONE);
                alphapetsList.addItemDecoration(new GridSpacingItemDecoration(3, 2, false));
                layoutManager = new GridLayoutManager(UnityPlayerActivity.this, 3);
                alphapetsList.setLayoutManager(layoutManager);
                break;
            case 1:
                InputStream bitmap1 = null;
                try {
                    bitmap1 = getAssets().open("animals_banner.jpg");
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Log.d("PrintBitmap", "" + bitmap1);
                Bitmap outImage1 = BitmapFactory.decodeStream(bitmap1);
                closeAlphapets.setImageResource(R.drawable.animal_close);
                imageBackground.setImageBitmap(outImage1);
                dialogHeader.setText("ANIMALS");
                dialogHeader.setTextColor(ContextCompat.getColor(this, R.color.ar_white));
                mainHeaderName.setText("ANIMALS");
                CustomFontStyle(this, dialogHeader);
                CustomFontStyle(this, mainHeaderName);
                animallayout.setVisibility(View.VISIBLE);
                alphabetlayout.setVisibility(View.GONE);
                fruitlayout.setVisibility(View.GONE);
                alphapetsList.addItemDecoration(new GridSpacingItemDecoration(3, 2, false));
                layoutManager = new GridLayoutManager(UnityPlayerActivity.this, 3);
                alphapetsList.setLayoutManager(layoutManager);
                break;
            case 2:
                InputStream bitmap2 = null;
                try {
                    bitmap2 = getAssets().open("fruitveg_banner.jpg");
                } catch (IOException e) {
                    e.printStackTrace();
                }
                audionName="audio_apple";
                Log.d("PrintBitmap", "" + bitmap2);
                Bitmap outImage2 = BitmapFactory.decodeStream(bitmap2);
                closeAlphapets.setImageResource(R.drawable.animal_close);
                imageBackground.setImageBitmap(outImage2);
                dialogHeader.setText("FRUITS & VEGETABLES");
                mainHeaderName.setText("APPLE");
                CustomFontStyle(this, dialogHeader);
                CustomFontStyle(this, mainHeaderName);
                dialogHeader.setTextColor(ContextCompat.getColor(this, R.color.ar_white));
                fruitlayout.setVisibility(View.VISIBLE);
                animallayout.setVisibility(View.GONE);
                alphabetlayout.setVisibility(View.GONE);
                alphapetsList.addItemDecoration(new GridSpacingItemDecoration(3, 2, false));
                layoutManager = new GridLayoutManager(UnityPlayerActivity.this, 3);
                alphapetsList.setLayoutManager(layoutManager);
                break;
        }
    }

    private void CustomFontStyle(Context context, TextView textView) {
        Typeface font = ResourcesCompat.getFont(context, R.font.gothamrounded_bold);
        if (font != null) {
            textView.setTypeface(font);
        }
    }

    private void LoadLevels(int position) {
        try {
            ArrayList<AlphapetsModel> alphapetsModels = new ArrayList<>();
            JSONArray kidsArray = new JSONArray(loadJSONFromAsset());
            JSONObject levelObject = kidsArray.getJSONObject(position);
            ParentModel parentModel = new ParentModel();
            parentModel.setLevelId(levelObject.getInt("levelid"));
            parentModel.setLevelName(levelObject.getString("levelName"));
            JSONArray jsonArray = levelObject.getJSONArray("models");
            for (int k = 0; k < jsonArray.length(); k++) {
                JSONObject alphapetsObject = jsonArray.getJSONObject(k);
                AlphapetsModel alphapetsModel = new AlphapetsModel();
                alphapetsModel.setModelid(alphapetsObject.getString("modelId"));
                alphapetsModel.setModelName(alphapetsObject.getString("modelName"));
                alphapetsModel.setModelImage(alphapetsObject.getString("modelImage"));
                alphapetsModel.setAudioSource(alphapetsObject.getString("audioName"));
                alphapetsModels.add(alphapetsModel);

            }
            parentModel.setAlphapetsModels(alphapetsModels);
            parentModels.add(parentModel);

            Log.d("ParentModelSize", "" + parentModels.size());
            alphapetsAdapter = new AlphapetsAdapter(UnityPlayerActivity.this, parentModels.get(0).getAlphapetsModels(), getSelectedPos);
            alphapetsList.setAdapter(alphapetsAdapter);
            UpdateList();
            //JSONObject kidsObject=kidsArray.getJSONObject(position);


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    @Override
    protected void onNewIntent(Intent intent) {
        // To support deep linking, we need to make sure that the client can get access to
        // the last sent intent. The clients access this through a JNI api that allows them
        // to get the intent set on launch. To update that after launch we have to manually
        // replace the intent with the one caught here.
        setIntent(intent);
    }

    // Quit Unity
    @Override
    protected void onDestroy() {
        mUnityPlayer.quit();
        super.onDestroy();
    }

    // Pause Unity
    @Override
    protected void onPause() {
        super.onPause();
        mUnityPlayer.pause();
        //((MyApplication) this.getApplication()).startActivityTransitionTimer(this);
    }

    // Resume Unity
    @Override
    protected void onResume() {
        super.onResume();
        mUnityPlayer.resume();
        if (temp == 1) {
            temp = 0;
        } else if (temp == 0) {
            extras = getIntent().getExtras();
            getSelectedPos = extras.getInt("selectedPos");
            parentModels = new ArrayList<>();
            Log.d("PrintGetSelectedPos", "" + getSelectedPos);
            ChangeDialogHeaderName(getSelectedPos);
            LoadLevels(getSelectedPos);
            ShowButtons showButtons =new ShowButtons();
            showButtons.setButtons(takeScreenShot,playAudio);
            takeScreenShot.setVisibility(View.GONE);
            playAudio.setVisibility(View.GONE);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    ShowAlert();
                }
            }, 4000);
            new HandleTheScene().execute();
        }
        //UnityPlayer.UnitySendMessage("ObjectPlacer","ChangeTheScene",parentModels.get(0).getLevelName());
//        MyApplication myApp = (MyApplication) this.getApplication();
//        if (myApp.wasInBackground) {
//            PlayAudioService.onResumePlayer();
//        }
//        myApp.stopActivityTransitionTimer();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mUnityPlayer.start();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mUnityPlayer.stop();
    }

    // Low Memory Unity
    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mUnityPlayer.lowMemory();
    }

    // Trim Memory Unity
    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
        if (level == TRIM_MEMORY_RUNNING_CRITICAL) {
            mUnityPlayer.lowMemory();
        }
    }

    // This ensures the layout will be correct.
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mUnityPlayer.configurationChanged(newConfig);
    }

    // Notify Unity of the focus change.
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        mUnityPlayer.windowFocusChanged(hasFocus);
    }

    // For some reason the multiple keyevent type is not supported by the ndk.
    // Force event injection by overriding dispatchKeyEvent().
    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_MULTIPLE)
            return mUnityPlayer.injectEvent(event);
        return super.dispatchKeyEvent(event);
    }

    // Pass any events not handled by (unfocused) views straight to UnityPlayer
    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        return mUnityPlayer.injectEvent(event);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return mUnityPlayer.injectEvent(event);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return mUnityPlayer.injectEvent(event);
    }

    /*API12*/
    public boolean onGenericMotionEvent(MotionEvent event) {
        return mUnityPlayer.injectEvent(event);
    }

    @Override
    public void onClick(View view) {
        if (view == takeScreenShot) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    UnityPlayer.UnitySendMessage("ObjectPlacer", "SaveImage", "My img {0}.png");
                    UnityPlayer.UnitySendMessage("ObjectPlacer", "DisablePlane", "No");
                }
            }, 2000);
            UnityPlayer.UnitySendMessage("ObjectPlacer", "DisablePlane", "Yes");
            Toast.makeText(this, R.string.screenshot_alert, Toast.LENGTH_LONG).show();
        }
        if (view == playAudio) {
//            AudioManager audioManager = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
//            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 20, 0);
            int mediaVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
//            android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(UnityPlayerActivity.this);
//            LayoutInflater inflater = getLayoutInflater();
//            View dialogView = inflater.inflate(R.layout.alertdialog, null);
//            builder.setView(dialogView);
//            alertTitle =  dialogView.findViewById(R.id.alertTitle);
//            alertTitle.setText(R.string.alertString);
//            alert_message =  dialogView.findViewById(R.id.alert_message);
//            alert_message.setText(R.string.media_mute_alert);
//            okalert =  dialogView.findViewById(R.id.okalert);
//            okalert.setGravity(Gravity.CENTER_HORIZONTAL);
//            cancelalert =  dialogView.findViewById(R.id.cancelalert);
//            cancelalert.setVisibility(View.GONE);
//            alertDialog = builder.create();
//            okalert.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    alertDialog.dismiss();
//                }
//            });
            if (mediaVolume == 0) {
                Toast.makeText(this, R.string.media_mute_alert, Toast.LENGTH_LONG).show();
            }
            else
            {
                Log.d("PrintAudioName",audionName);
                UnityPlayer.UnitySendMessage("ObjectPlacer","PlaySpeak",audionName);

//                switch (audionName)
//                {
//                    case "audio_apple":
//                        UnityPlayer.UnitySendMessage("ObjectPlacer","PlaySpeak",parentModels.get(0).getAlphapetsModels().get(0).getAudioSource());
//                        break;
//                    case "audio_banana":
//                        UnityPlayer.UnitySendMessage("ObjectPlacer","PlaySpeak",parentModels.get(0).getAlphapetsModels().get(1).getAudioSource());
//                        break;
//                }

            }
//            else if(mediaVolume==0&&mediaAlertCount==4)
//            {
//                alertDialog.show();
//
//            }
//            else if(mediaVolume==0&&mediaAlertCount==9)
//            {
//                alertDialog.show();
//
//            }
//            else if(mediaVolume==0&&mediaAlertCount==14)
//            {
//                alertDialog.show();
//
//            }
//                mediaAlertCount++;


            //Log.d("PrintMediaVolume",""+audioManager.getStreamVolume(AudioManager.STREAM_MUSIC));


//
            //UnityPlayer.UnitySendMessage("ObjectPlacer","PlayAudio","Yes");
        }
        if (view == showAlphapets) {
            dialog.show();
        }
        if (view == closeAlphapets) {
            dialog.dismiss();
        }
        if (view == NavigateBack) {
            UnityPlayer.UnitySendMessage("ObjectPlacer", "ClearScene", "Yes");
            Intent goBack = new Intent(UnityPlayerActivity.this, HomeActivity.class);
            //goBack.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(goBack);
        }
        if(view==gotItButton)
        {
            prefs = getSharedPreferences(Constants.AppPreferences, MODE_PRIVATE);
            final SharedPreferences.Editor editor = prefs.edit();
            pointerDialog.dismiss();
            dialog.show();
            editor.putInt("alertMessageShow",1);
            editor.commit();

        }
    }

    @Override
    public void onBillingClientSetupFinished() {

    }

    @Override
    public void onConsumeFinished(String token, int result) {

    }

    @Override
    public void onPurchasesUpdated(List<Purchase> purchases) {
    }

    @Override
    public void onPurchaseHistoryResponse(List<Purchase> purchases) {
    }

    @Override
    public void onPurchaseReponse(String response) {
        if (alphapetsAdapter != null) {
            if (response.equals("You have already purchased this item. Click RESTORE to update your purchase list.") || response.equals("")) {
                String mypurchases = prefs.getString(Constants.purchased_product, "");
                if (itemPosition >= 0) {
                    List<PurchaseModel> fromJson = gson.fromJson(mypurchases, type);
                    List<PurchaseModel> model = new ArrayList<>();
                    if (fromJson != null) {
                        model = new ArrayList<>(fromJson);
                    }
                    String modelid = parentModels.get(0).getAlphapetsModels().get(itemPosition).getModelid();
                    PurchaseModel pmodel = new PurchaseModel(modelid, "", "", "", true);
                    //add the model list
                    model.add(pmodel);
                    for (int i = 0; i < model.size(); i++) {
                        for (int j = i + 1; j < model.size(); j++) {
                            if (model.get(j).getProductid().equals(model.get(i).getProductid())) {
                                model.remove(j);
                            }
                        }
                    }
                    String json = gson.toJson(model, type);
                    Log.d("PurchaseBuy", json);
                    prefs.edit().putString(Constants.purchased_product, json).apply();
                }
                if (!response.equals(""))
                    Toast.makeText(UnityPlayerActivity.this, "You have already purchased this item.", Toast.LENGTH_SHORT).show();
                UpdateList();
            } else {
                Toast.makeText(UnityPlayerActivity.this, response, Toast.LENGTH_SHORT).show();
            }
        }

    }

    @Override
    public void onSkuDetailsResponse(int responseCode, List<SkuDetails> skuDetailsList) {

    }

    class AlphapetsClickListner implements View.OnClickListener {
        Context context;


        public AlphapetsClickListner(Context context) {
            this.context = context;
        }

        @Override
        public void onClick(View view) {
            prefs = getSharedPreferences(Constants.AppPreferences, MODE_PRIVATE);
            itemPosition = alphapetsList.getChildLayoutPosition(view);
            alertrate = prefs.getInt(Constants.alertrate_pref, 0);
            Log.d("Test_Value", "Value:" + alertrate);
            if (parentModels.get(0).getAlphapetsModels().get(itemPosition).getIsPurchased()) {
                UnityPlayer.UnitySendMessage("ObjectPlacer", "DeleteObject", "Yes");

                //int itemPosition=alphapetsList.getChildLayoutPosition(view);
                if(parentModels.get(0).getLevelId()==2||parentModels.get(0).getLevelId()==3)
                {
                    mainHeaderName.setText(parentModels.get(0).getAlphapetsModels().get(itemPosition).getModelName());
                }
                UnityPlayer.UnitySendMessage("ObjectPlacer", "ChangeAlphapet", parentModels.get(0).getAlphapetsModels().get(itemPosition).getModelName());
                //isBooleanSelected=true;
                UnityPlayer.UnitySendMessage("ObjectPlacer","StopPlayAudio","Yes");
                UnityPlayer.UnitySendMessage("ObjectPlacer","whichObjectSelected","Yes");
                audionName=parentModels.get(0).getAlphapetsModels().get(itemPosition).getAudioSource();
                takeScreenShot.setVisibility(View.GONE);
                playAudio.setVisibility(View.GONE);
                dialog.dismiss();
                alertrate = alertrate + 1;
                prefs.edit().putInt(Constants.alertrate_pref, alertrate).apply();
                if (alertrate == 4 || alertrate == 8 || alertrate == 12) {
                    android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(UnityPlayerActivity.this);
                    LayoutInflater inflater = getLayoutInflater();
                    View dialogView = inflater.inflate(R.layout.alertdialog, null);
                    builder.setView(dialogView);
                    alertTitle = dialogView.findViewById(R.id.alertTitle);
                    alertTitle.setText(R.string.alertString);
                    alert_message = dialogView.findViewById(R.id.alert_message);
                    alert_message.setText(R.string.alertmessage);
                    okalert = dialogView.findViewById(R.id.okalert);
                    okalert.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            alertDialog.dismiss();
                            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.supercell.clashofclans"));
                            startActivity(intent);
                            overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up);
                        }
                    });
                    cancelalert = dialogView.findViewById(R.id.cancelalert);
                    cancelalert.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            alertDialog.dismiss();
                        }
                    });
                    alertDialog = builder.create();
                    alertDialog.show();

                }

            } else {
                NetworkDetector networkDetector = new NetworkDetector(UnityPlayerActivity.this);
                if (networkDetector.isConnectingToInternet()) {
                    android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(UnityPlayerActivity.this);
                    LayoutInflater inflater = getLayoutInflater();
                    View dialogView = inflater.inflate(R.layout.alertdialog, null);
                    builder.setView(dialogView);
                    alertTitle = dialogView.findViewById(R.id.alertTitle);
                    alertTitle.setText(R.string.alertpurchase);
                    alert_message = dialogView.findViewById(R.id.alert_message);
                    alert_message.setText(R.string.alertpurchasemessage);
                    okalert = dialogView.findViewById(R.id.okalert);
                    okalert.setText("Yes");
                    okalert.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            alertDialog.dismiss();
                            //dialog.dismiss();
                            String productid = parentModels.get(0).getAlphapetsModels().get(itemPosition).getModelid();
                            Log.d("ProductId", productid);
                            billingManager.initiatePurchaseFlow(productid, BillingClient.SkuType.INAPP);
                        /*PurchaseModel pmodel = new PurchaseModel("com.deemsysinc.kidsar.basicmodels", "", "", "", true);
                        //add the model list
                        List<PurchaseModel> model = new ArrayList<>();
                        model.add(pmodel);
                        String json = gson.toJson(model, type);
                        Log.d("PurchaseRestore", json);
                        prefs.edit().putString(Constants.purchased_product, json).apply();
                        UpdateList();*/
                        }
                    });
                    cancelalert = dialogView.findViewById(R.id.cancelalert);
                    cancelalert.setText("No");
                    cancelalert.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            alertDialog.dismiss();
                            //dialog.dismiss();
                        }
                    });
                    alertDialog = builder.create();
                    alertDialog.show();
                } else {
                    showAlert(R.string.noInternet, "Make sure your device is connected to the internet.");
                }
            }
//            UnityPlayer.UnitySendMessage("ObjectPlacer","DeleteObject","Yes");
//            //int itemPosition=alphapetsList.getChildLayoutPosition(view);
//            UnityPlayer.UnitySendMessage("ObjectPlacer","ChangeAlphapet",parentModels.get(0).getAlphapetsModels().get(itemPosition).getModelName());
//            dialog.dismiss();

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

    private void UpdateList() {
        prefs = getSharedPreferences(Constants.AppPreferences, Context.MODE_PRIVATE);
        String mypurchases = prefs.getString(Constants.purchased_product, "");
        if (!mypurchases.isEmpty()) {
            Log.d("PurchaseOnCreate", mypurchases);
            //Get the json value from Shared Preference and Convert to List in My Purchased values
            List<PurchaseModel> fromJson = gson.fromJson(mypurchases, type);
            for (int i = 0; i < parentModels.get(0).getAlphapetsModels().size(); i++) {
                for (PurchaseModel mylist : fromJson) {
                    if (parentModels.get(0).getAlphapetsModels().get(i).getModelid().equals(mylist.productid)) {
                        parentModels.get(0).getAlphapetsModels().get(i).isPurchased(true);
                    }
                }
            }
            if (alphapetsAdapter != null)
                alphapetsAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onBackPressed() {
        UnityPlayer.UnitySendMessage("ObjectPlacer", "ClearScene", "Yes");
        Intent goBack = new Intent(UnityPlayerActivity.this, HomeActivity.class);
        //goBack.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(goBack);
    }

    private void showAlert(int title, String message) {
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(UnityPlayerActivity.this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.alertdialog, null);
        builder.setView(dialogView);
        TextView alertTitle = dialogView.findViewById(R.id.alertTitle);
        TextView alert_message = dialogView.findViewById(R.id.alert_message);
        alertTitle.setText(title);
        alert_message.setText(message);
        Button okalert = dialogView.findViewById(R.id.okalert);
        okalert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
        Button cancelalert = (Button) dialogView.findViewById(R.id.cancelalert);
        cancelalert.setVisibility(View.GONE);
        alertDialog = builder.create();
        if (alertDialog != null) {
            alertDialog.show();
        }
    }

    class HandleTheScene extends AsyncTask<Integer, Integer, Integer> {
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.d("CalledWhat", "onPreExecute");
//            Toast.makeText(UnityPlayerActivity.this, "Please wait", Toast.LENGTH_SHORT).show();
            //progressDialog=ProgressDialog.show(UnityPlayerActivity.this,"","Loading");
        }

        @Override
        protected Integer doInBackground(Integer... integers) {
            UnityPlayer.UnitySendMessage("ObjectPlacer", "ChangeTheScene", parentModels.get(0).getLevelName());
            return null;
        }

        @Override
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);
            Log.d("CalledWhat", "onPostExecute");
            //progressDialog.dismiss();
        }
    }
    public void ChangeTheVariable()
    {

    }
    public void HideTheButton()
    {
        Log.d("Hidethe","Called");
        UnityPlayerActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                TheCall(1);
            }
        });

//        Toast.makeText(this, "Hello", Toast.LENGTH_SHORT).show();
//        UnityPlayer.currentActivity.runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//
//            }
//        });
//        UnityPlayer.currentActivity.runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                Toast.makeText(UnityPlayerActivity.this, "Hello World", Toast.LENGTH_SHORT).show();
//            }
//        });
    }

    private void TheCall(int i) {
        if(i==1)
        {
            Toast.makeText(this, "Hello", Toast.LENGTH_SHORT).show();
        }
    }

}
