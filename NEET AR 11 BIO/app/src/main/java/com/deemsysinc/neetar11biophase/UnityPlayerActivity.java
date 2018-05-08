package com.deemsysinc.neetar11biophase;

import com.unity3d.player.*;
import com.yarolegovich.discretescrollview.DiscreteScrollView;
import com.yarolegovich.discretescrollview.transform.ScaleTransformer;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class UnityPlayerActivity extends Activity implements DiscreteScrollView.OnItemChangedListener<RelatedModelsAdapter.RelatedModelsHolder>,View.OnClickListener,CompoundButton.OnCheckedChangeListener
{


    protected UnityPlayer mUnityPlayer; // don't change the name of this variable; referenced from native code

    Bundle extras;

    ArrayList<ModelProperties> modelPropertiess;


    String modelName="";

    int index=0, parentIndex=0;

    FrameLayout FrameAr;


    DiscreteScrollView relatedMenuPicker;


    static View.OnClickListener onClickListener;

    android.widget.Toolbar arToolbar;

    TextView headModelName,SDynamicScale;

    Typeface toolbarFont;


    int fromsame=0;

    MenuItem eyeOpen,eyeHide;


    int isPartsClicked=0;


    Button zoomOnOff,settingsApply,settingsCancel;


    Switch planeOnOff,szoomOnOff,AZoom;

    SeekBar scale;


    Dialog settingsDialog,dialog;

    RecyclerView cameraHintsList;
    LinearLayoutManager layoutManager;
    CameraHintPageAdapter cameraHintPageAdapter;


    LinearLayoutManager dialoglayoutManager;


    Typeface textfont;


    ImageView helpClose;


    int settingsScale=0;


    TextView dummyZoom;





    // Setup activity layout
    @Override protected void onCreate(Bundle savedInstanceState)
    {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        mUnityPlayer = new UnityPlayer(this);
        //setContentView(mUnityPlayer);
        setContentView(R.layout.activity_arview);
        arToolbar=findViewById(R.id.ar_toolbar);
        setActionBar(arToolbar);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setTitle("");
        arToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle=new Bundle();
                Intent goBack=new Intent(UnityPlayerActivity.this,ScanArActivity.class);
                goBack.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                bundle.putInt("index",index);
                bundle.putInt("parentindex",parentIndex);
                goBack.putExtras(bundle);
                startActivity(goBack);
            }
        });
        AZoom=findViewById(R.id.switch_zoom);
        dummyZoom=findViewById(R.id.zoom_dummy);
        AZoom.setOnCheckedChangeListener(this);
        settingsDialog=new Dialog(UnityPlayerActivity.this);
        settingsDialog.setContentView(R.layout.settings_ar);
        settingsDialog.setCancelable(true);
        settingsDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog=new Dialog(UnityPlayerActivity.this);
        dialog.setContentView(R.layout.camera_hints_page);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        cameraHintsList=(dialog).findViewById(R.id.help_list_ar);
        dialoglayoutManager=new LinearLayoutManager(this);
        cameraHintsList.setLayoutManager(dialoglayoutManager);
        SDynamicScale=(settingsDialog).findViewById(R.id.dynamic_scale);
        helpClose=(dialog).findViewById(R.id.help_close);
        helpClose.setOnClickListener(this);
        settingsApply=(settingsDialog).findViewById(R.id.settings_apply);
        settingsApply.setOnClickListener(this);
        settingsCancel=(settingsDialog).findViewById(R.id.settings_cancel);
        settingsCancel.setOnClickListener(this);
        planeOnOff=(settingsDialog).findViewById(R.id.plane_on_off);
        planeOnOff.setOnCheckedChangeListener(this);
        szoomOnOff=(settingsDialog).findViewById(R.id.zoom_on_off);
        szoomOnOff.setOnCheckedChangeListener(this);
        scale=(settingsDialog).findViewById(R.id.get_scale);
        scale.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                SDynamicScale.setText(""+i);
                settingsScale=i;

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        settingsApply.setOnClickListener(this);
        settingsCancel.setOnClickListener(this);
        zoomOnOff=findViewById(R.id.zoom_on_off);
        zoomOnOff.setOnClickListener(this);
        headModelName=findViewById(R.id.head_modelname);
        toolbarFont= Typeface.createFromAsset(getAssets(),"fonts/GillSans-SemiBold.ttf");
        dummyZoom.setTypeface(toolbarFont);
        headModelName.setTypeface(toolbarFont);
        FrameAr=findViewById(R.id.ar_frame);
        relatedMenuPicker=findViewById(R.id.related_model_list);
        FrameAr.addView(mUnityPlayer.getView(),FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
        extras=getIntent().getExtras();
        index=extras.getInt("index");
        parentIndex=extras.getInt("parentindex");
        modelPropertiess=new ArrayList<>();
        LoadModelProperties(index,parentIndex);
        cameraHintPageAdapter=new CameraHintPageAdapter(UnityPlayerActivity.this,modelPropertiess.get(0).getHints());
        cameraHintsList.setAdapter(cameraHintPageAdapter);
        //UnityPlayer.UnitySendMessage("Cube","willLoadArScene","Yes");
        UnityPlayer.UnitySendMessage("Cube","NavigateSceneModel",modelPropertiess.get(0).getVisiblename());
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                UnityPlayer.UnitySendMessage("ObjectPlacer","ChangeModel",modelPropertiess.get(0).getModelname());
            }
        },5000);

        if(modelPropertiess.get(0).getIsSurfaceEnabled()==true)
        {
            planeOnOff.setChecked(false);
            UnityPlayer.UnitySendMessage("ObjectPlacer","EnablePlane","true");

        }
        else
        {
            planeOnOff.setChecked(true);
            UnityPlayer.UnitySendMessage("ObjectPlacer","EnablePlane","false");

        }

        if(modelPropertiess.get(0).getModelPropertiesChildren()!=null)
        {
            if(modelPropertiess.get(0).getModelPropertiesChildren().size()>0)
            {
//                try {
//                    expansionFile = APKExpansionSupport.getAPKExpansionZipFile(ARViewActivity.this, 1, 0, obbPath);
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }

                relatedMenuPicker.setAdapter(new RelatedModelsAdapter(this,modelPropertiess.get(0).getModelPropertiesChildren()));
                relatedMenuPicker.setItemTransformer(new ScaleTransformer.Builder()
                        .setMinScale(0.8f)
                        .build());
                relatedMenuPicker.addOnItemChangedListener(this);
                onClickListener=new RelatedMenuListner(this);
            }
        }

        mUnityPlayer.requestFocus();
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
        //UnityPlayer.UnitySendMessage("ObjectPlacer","ChangeModel",modelPropertiess.get(0).getModelname());
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
    private void LoadModelProperties(int index, int parentIndex) {
        try {
            JSONArray jsonArray=new JSONArray(loadJSONFromAsset());
            JSONObject jsonObject=jsonArray.getJSONObject(parentIndex);
            JSONArray modelArray=jsonObject.getJSONArray("models");
            JSONObject modelObject=modelArray.getJSONObject(index);
            ModelProperties modelProperties=new ModelProperties();
            modelProperties.setModelid(modelObject.getLong("id"));
            //thesettingmodelid=modelObject.getLong("id");
            modelProperties.setModelname(modelObject.getString("modelname"));
            modelProperties.setVisiblename(modelObject.getString("visiblename"));
            headModelName.setText(modelObject.getString("visiblename"));
            //modelHeader.setText(modelObject.getString("visiblename"));
            modelProperties.setStaticModelPath(modelObject.getString("staticmodelpath"));
            modelProperties.setMtlModelPath(modelObject.getString("mtlmodelpath"));
            modelProperties.setSurfaceEnabled(modelObject.getBoolean("isSurfaceEnabled"));
            modelProperties.setZoomEnabled(modelObject.getBoolean("isZoomEnabled"));
            modelProperties.setPartsAvaliable(modelObject.getBoolean("isPartsAvailable"));
            modelProperties.setPartsName(modelObject.getString("partsname"));
            modelProperties.setPartsModelPath(modelObject.getString("partsmodelpath"));
            modelProperties.setPartsMtlPath(modelObject.getString("partsmtlpath"));
            modelProperties.setScaleValue((float)modelObject.getDouble("scaleValue"));
            modelProperties.setCanModifySurface(modelObject.getBoolean("canModifySurface"));
            modelProperties.setModelImage(modelObject.getString("modelimage"));
            JSONArray arrayHints=modelObject.getJSONArray("hints");
            ArrayList<String> HintsArray=new ArrayList<>();
            for(int l=0; l<arrayHints.length(); l++)
            {
                HintsArray.add(arrayHints.getString(l));
            }
            modelProperties.setHints(HintsArray);
//            if(fromSettings==0) {
//                scaleFactor = (float) modelObject.getDouble("scaleValue");
//                dynamicScale=(float) modelObject.getDouble("scaleValue");
//                intentPasser=(float)modelObject.getDouble("scaleValue");
//            }
            JSONArray relatedObjectsArray=modelObject.getJSONArray("relatedmodels");
            ArrayList<ModelPropertiesChild> modelPropertiesChildren=new ArrayList<>();
            if(relatedObjectsArray.length()>0)
            {
                relatedMenuPicker.setVisibility(View.VISIBLE);
                for(int k=0; k<relatedObjectsArray.length(); k++)
                {
                    JSONObject relatedObject=relatedObjectsArray.getJSONObject(k);
                    ModelPropertiesChild modelPropertiesChild=new ModelPropertiesChild();
                    modelPropertiesChild.setModelid(relatedObject.getLong("id"));
                    modelPropertiesChild.setModelname(relatedObject.getString("modelname"));
                    modelPropertiesChild.setVisiblename(relatedObject.getString("visiblename"));
                    modelPropertiesChild.setStaticModelPath(relatedObject.getString("staticmodelpath"));
                    modelPropertiesChild.setMtlModelPath(relatedObject.getString("mtlmodelpath"));
                    modelPropertiesChild.setSurfaceEnabled(relatedObject.getBoolean("isSurfaceEnabled"));
                    modelPropertiesChild.setZoomEnabled(relatedObject.getBoolean("isZoomEnabled"));
                    modelPropertiesChild.setPartsAvaliable(relatedObject.getBoolean("isPartsAvailable"));
                    modelPropertiesChild.setPartsName(relatedObject.getString("partsname"));
                    modelPropertiesChild.setPartsModelPath(relatedObject.getString("partsmodelpath"));
                    modelPropertiesChild.setPartsMtlPath(modelObject.getString("partsmtlpath"));
                    modelPropertiesChild.setScaleValue((float)relatedObject.getDouble("scaleValue"));
                    modelPropertiesChild.setCanModifySurface(relatedObject.getBoolean("canModifySurface"));
                    modelPropertiesChild.setModelImage(relatedObject.getString("modelimage"));
                    JSONArray RelatedArrayHints=modelObject.getJSONArray("hints");
                    ArrayList<String> RelatedListHints=new ArrayList<>();
                    for(int m=0; m<RelatedArrayHints.length(); m++)
                    {
                        RelatedListHints.add(RelatedArrayHints.getString(m));
                    }
                    modelPropertiesChild.setHints(RelatedListHints);
                    JSONArray relatedIds=relatedObject.getJSONArray("relatedids");
                    ArrayList<Integer> storeRelatedIds=new ArrayList<>();
                    for(int j=0; j<relatedIds.length(); j++)
                    {
                        storeRelatedIds.add(relatedIds.getInt(j));
                    }
                    modelPropertiesChild.setRelatedIds(storeRelatedIds);
                    modelPropertiesChildren.add(modelPropertiesChild);
                    Log.d("StoreddRe",""+storeRelatedIds.size());
                }
                modelProperties.setModelPropertiesChildren(modelPropertiesChildren);

            }
            else
            {
                relatedMenuPicker.setVisibility(View.VISIBLE);
            }
            modelPropertiess.add(modelProperties);
//            if (fromSettings == 1) {
//                if (zoomtemp != 0) {
//                    zoomMenu.setVisibility(View.VISIBLE);
//                }
//                else
//                {
//                    zoomMenu.setVisibility(View.GONE);
//                }
//
//            } else {
//                if (modelPropertiess.get(0).getisZoomEnable() == true) {
//                    zoomMenu.setVisibility(View.VISIBLE);
//                }
//                else
//                {
//                    zoomMenu.setVisibility(View.GONE);
//                }
//            }




        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
    public String loadJSONFromAsset() {
        String json = null;
        try {
            InputStream is = getAssets().open("3dmodels.json");
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
        Bundle bundle=new Bundle();
        Intent goBack=new Intent(UnityPlayerActivity.this,ScanArActivity.class);
        goBack.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        bundle.putInt("index",index);
        bundle.putInt("parentindex",parentIndex);
        goBack.putExtras(bundle);
        startActivity(goBack);

    }

    // Pass any events not handled by (unfocused) views straight to UnityPlayer
    @Override public boolean onKeyUp(int keyCode, KeyEvent event)     { return mUnityPlayer.injectEvent(event); }
    @Override public boolean onKeyDown(int keyCode, KeyEvent event)   { return mUnityPlayer.injectEvent(event); }
    @Override public boolean onTouchEvent(MotionEvent event)          { return mUnityPlayer.injectEvent(event); }
    /*API12*/ public boolean onGenericMotionEvent(MotionEvent event)  { return mUnityPlayer.injectEvent(event); }

    @Override
    public void onCurrentItemChanged(@Nullable RelatedModelsAdapter.RelatedModelsHolder viewHolder, int adapterPosition) {

    }

    @Override
    public void onClick(View view) {
        if(view==zoomOnOff)
        {
            if(zoomOnOff.getText().toString().equals("Zoom-On"))
            {
                zoomOnOff.setText("Zoom-Off");
                UnityPlayer.UnitySendMessage("ObjectPlacer","ZoomObject","Yes");
            }
            else if(zoomOnOff.getText().toString().equals("Zoom-Off"))
            {
                zoomOnOff.setText("Zoom-On");
                UnityPlayer.UnitySendMessage("ObjectPlacer","ZoomObject","No");
            }
        }
        if(view==settingsApply)
        {
            settingsDialog.dismiss();
            if(planeOnOff.isChecked()==true)
            {
                //UnityPlayer.UnitySendMessage("ObjectPlacer","DeleteObject","Yes");
                UnityPlayer.UnitySendMessage("ObjectPlacer","EnablePlane","false");
            }
            else
            {
                //UnityPlayer.UnitySendMessage("ObjectPlacer","DeleteObject","Yes");
                UnityPlayer.UnitySendMessage("ObjectPlacer","EnablePlane","true");
            }
            if(szoomOnOff.isChecked()==true)
            {
                UnityPlayer.UnitySendMessage("ObjectPlacer","ZoomObject","Yes");
                zoomOnOff.setVisibility(View.VISIBLE);
            }
            else
            {
                UnityPlayer.UnitySendMessage("ObjectPlacer","ZoomObject","No");
                zoomOnOff.setVisibility(View.GONE);
            }
            if(settingsScale!=0)
            {
                Log.d("UnityScale",""+settingsScale);
                UnityPlayer.UnitySendMessage("ObjectPlacer","ChangeScale",String.valueOf(settingsScale));
            }
        }
        if(view==settingsCancel)
        {
            settingsDialog.dismiss();
        }
        if(view==helpClose)
        {
            dialog.dismiss();
        }

    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        if(compoundButton==AZoom)
        {
            if(b==true)
            {
                UnityPlayer.UnitySendMessage("ObjectPlacer","ZoomObject","Yes");
            }
            else
            {
                UnityPlayer.UnitySendMessage("ObjectPlacer","ZoomObject","No");
            }
        }

    }

    public class RelatedMenuListner implements View.OnClickListener
    {
        Context context;
        public RelatedMenuListner(Context context)
        {
            this.context=context;
        }

        @Override
        public void onClick(View view) {
            //Toast.makeText(context, "Toast Clicked", Toast.LENGTH_SHORT).show();

            UnityPlayer.UnitySendMessage("ObjectPlacer","DeleteObject","Yes");
            int itemposition=relatedMenuPicker.getChildLayoutPosition(view);
            LoadRelatedModels(modelPropertiess.get(0).getModelPropertiesChildren().get(itemposition).getModelid(),index,parentIndex);
            if(!modelPropertiess.get(0).getPartsName().equals("null"))
            {
                ShowOrHideParts();
            }
            else
            {
                eyeOpen.setVisible(false);
                eyeHide.setVisible(false);
            }
            cameraHintPageAdapter=new CameraHintPageAdapter(UnityPlayerActivity.this,modelPropertiess.get(0).getHints());
            cameraHintsList.setAdapter(cameraHintPageAdapter);
            UnityPlayer.UnitySendMessage("ObjectPlacer","ChangeModel",modelPropertiess.get(0).getModelname());
            if(modelPropertiess.get(0).getModelPropertiesChildren()!=null)
            {
                if(modelPropertiess.get(0).getModelPropertiesChildren().size()>0)
                {
//                try {
//                    expansionFile = APKExpansionSupport.getAPKExpansionZipFile(ARViewActivity.this, 1, 0, obbPath);
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }

                    relatedMenuPicker.setAdapter(new RelatedModelsAdapter(UnityPlayerActivity.this,modelPropertiess.get(0).getModelPropertiesChildren()));
                    relatedMenuPicker.setItemTransformer(new ScaleTransformer.Builder()
                            .setMinScale(0.8f)
                            .build());
                    relatedMenuPicker.addOnItemChangedListener(UnityPlayerActivity.this);
                    onClickListener=new RelatedMenuListner(UnityPlayerActivity.this);
                }
            }
        }
    }
    private void LoadRelatedModels(long id,int index,int parentIndex) {
        modelPropertiess=new ArrayList<>();
        try {
            JSONArray relatedModelArray=new JSONArray(loadJSONFromAsset());
            JSONObject relatedModelObject=relatedModelArray.getJSONObject(parentIndex);
            JSONArray modelArray=relatedModelObject.getJSONArray("models");
            if(modelArray.length()>0)
            {
                ArrayList<ModelPropertiesChild> modelPropertiesChildren = new ArrayList<>();
                ModelProperties modelProperties=new ModelProperties();
                JSONObject theModelObject=modelArray.getJSONObject(index);
                if(id==theModelObject.getLong("id"))
                {
                    modelProperties.setModelid(theModelObject.getLong("id"));
                    //thesettingmodelid=theModelObject.getLong("id");
                    modelProperties.setModelname(theModelObject.getString("modelname"));
                    modelProperties.setVisiblename(theModelObject.getString("visiblename"));
                    headModelName.setText(theModelObject.getString("visiblename"));
                    //modelHeader.setText(theModelObject.getString("visiblename"));
                    modelProperties.setStaticModelPath(theModelObject.getString("staticmodelpath"));
                    modelProperties.setMtlModelPath(theModelObject.getString("mtlmodelpath"));
                    modelProperties.setSurfaceEnabled(theModelObject.getBoolean("isSurfaceEnabled"));
                    modelProperties.setZoomEnabled(theModelObject.getBoolean("isZoomEnabled"));
                    modelProperties.setPartsAvaliable(theModelObject.getBoolean("isPartsAvailable"));
                    modelProperties.setPartsName(theModelObject.getString("partsname"));
                    modelProperties.setPartsModelPath(theModelObject.getString("partsmodelpath"));
                    modelProperties.setPartsMtlPath(theModelObject.getString("partsmtlpath"));
                    modelProperties.setScaleValue((float)theModelObject.getDouble("scaleValue"));
                    modelProperties.setCanModifySurface(theModelObject.getBoolean("canModifySurface"));
                    modelProperties.setModelImage(theModelObject.getString("modelimage"));
                    JSONArray arrayHints=theModelObject.getJSONArray("hints");
                    ArrayList<String> HintsArray=new ArrayList<>();
                    for(int l=0; l<arrayHints.length(); l++)
                    {
                        HintsArray.add(arrayHints.getString(l));
                    }
                    modelProperties.setHints(HintsArray);
//                    if(fromSettings==0) {
//                        scaleFactor = (float) theModelObject.getDouble("scaleValue");
//                        dynamicScale=(float) theModelObject.getDouble("scaleValue");
//                        intentPasser=(float)theModelObject.getDouble("scaleValue");
//                    }
                    JSONArray relatedObjectsArray=theModelObject.getJSONArray("relatedmodels");

                    if(relatedObjectsArray.length()>0)
                    {
                        relatedMenuPicker.setVisibility(View.VISIBLE);
                        for(int k=0; k<relatedObjectsArray.length(); k++)
                        {
                            JSONObject relatedObject=relatedObjectsArray.getJSONObject(k);
                            ModelPropertiesChild modelPropertiesChild=new ModelPropertiesChild();
                            modelPropertiesChild.setModelid(relatedObject.getLong("id"));
                            modelPropertiesChild.setModelname(relatedObject.getString("modelname"));
                            modelPropertiesChild.setVisiblename(relatedObject.getString("visiblename"));
                            modelPropertiesChild.setStaticModelPath(relatedObject.getString("staticmodelpath"));
                            modelPropertiesChild.setMtlModelPath(relatedObject.getString("mtlmodelpath"));
                            modelPropertiesChild.setSurfaceEnabled(relatedObject.getBoolean("isSurfaceEnabled"));
                            modelPropertiesChild.setZoomEnabled(relatedObject.getBoolean("isZoomEnabled"));
                            modelPropertiesChild.setPartsAvaliable(relatedObject.getBoolean("isPartsAvailable"));
                            modelPropertiesChild.setPartsName(relatedObject.getString("partsname"));
                            modelPropertiesChild.setPartsModelPath(relatedObject.getString("partsmodelpath"));
                            modelPropertiesChild.setPartsMtlPath(relatedObject.getString("partsmtlpath"));
                            modelPropertiesChild.setScaleValue((float)relatedObject.getDouble("scaleValue"));
                            modelPropertiesChild.setCanModifySurface(relatedObject.getBoolean("canModifySurface"));
                            modelPropertiesChild.setModelImage(relatedObject.getString("modelimage"));
                            JSONArray ReHints=relatedObject.getJSONArray("hints");
                            ArrayList<String> ReHintsArray=new ArrayList<>();
                            for(int l=0; l<ReHints.length(); l++)
                            {
                                ReHintsArray.add(ReHints.getString(l));
                            }
                            modelPropertiesChild.setHints(ReHintsArray);
                            JSONArray relatedIds=relatedObject.getJSONArray("relatedids");
                            ArrayList<Integer> storeRelatedIds=new ArrayList<>();
                            for(int j=0; j<relatedIds.length(); j++)
                            {
                                storeRelatedIds.add(relatedIds.getInt(j));
                            }
                            modelPropertiesChild.setRelatedIds(storeRelatedIds);
                            modelPropertiesChildren.add(modelPropertiesChild);
                            Log.d("StoreddRe",""+storeRelatedIds.size());
                        }
                        modelProperties.setModelPropertiesChildren(modelPropertiesChildren);

                    }
                    else
                    {
                        relatedMenuPicker.setVisibility(View.GONE);
                    }


                }
                else {
                    JSONArray relatedmodels = theModelObject.getJSONArray("relatedmodels");
                    if (relatedmodels.length() > 0) {
                        relatedMenuPicker.setVisibility(View.VISIBLE);
                        JSONArray relatedIds = null;
                        Log.d("RelateModelsLength",""+relatedmodels.length());
                        for (int i = 0; i < relatedmodels.length(); i++) {
                            JSONObject relatedObject = relatedmodels.getJSONObject(i);
                            if (id == relatedObject.getLong("id")) {
                                modelProperties.setModelid(relatedObject.getLong("id"));
                                //thesettingmodelid=relatedObject.getLong("id");
                                modelProperties.setModelname(relatedObject.getString("modelname"));
                                modelProperties.setVisiblename(relatedObject.getString("visiblename"));
                                headModelName.setText(relatedObject.getString("visiblename"));
                                //modelHeader.setText(relatedObject.getString("visiblename"));
                                modelProperties.setStaticModelPath(relatedObject.getString("staticmodelpath"));
                                modelProperties.setMtlModelPath(relatedObject.getString("mtlmodelpath"));
                                modelProperties.setSurfaceEnabled(relatedObject.getBoolean("isSurfaceEnabled"));
                                modelProperties.setZoomEnabled(relatedObject.getBoolean("isZoomEnabled"));
                                modelProperties.setPartsAvaliable(relatedObject.getBoolean("isPartsAvailable"));
                                modelProperties.setPartsName(relatedObject.getString("partsname"));
                                modelProperties.setPartsModelPath(relatedObject.getString("partsmodelpath"));
                                modelProperties.setPartsMtlPath(relatedObject.getString("partsmtlpath"));
                                modelProperties.setScaleValue((float) relatedObject.getDouble("scaleValue"));
                                modelProperties.setCanModifySurface(relatedObject.getBoolean("canModifySurface"));
                                modelProperties.setModelImage(relatedObject.getString("modelimage"));
                                JSONArray ReHints=relatedObject.getJSONArray("hints");
                                ArrayList<String> ReHintsArray=new ArrayList<>();
                                for(int l=0; l<ReHints.length(); l++)
                                {
                                    ReHintsArray.add(ReHints.getString(l));
                                }
                                modelProperties.setHints(ReHintsArray);
                                relatedIds = relatedObject.getJSONArray("relatedids");
//                                if (fromSettings == 0) {
//                                    scaleFactor = (float) relatedObject.getDouble("scaleValue");
//                                    dynamicScale = (float) relatedObject.getDouble("scaleValue");
//                                    intentPasser = (float) relatedObject.getDouble("scaleValue");
//                                }

                            }
                        }

                        if (relatedIds.length() > 0) {
                            int addStatus=0;
                            for (int j = 0; j < relatedIds.length(); j++) {
                                for(int m=0;m<relatedmodels.length(); m++) {
                                    JSONObject relatedObject = relatedmodels.getJSONObject(m);
                                    ModelPropertiesChild modelPropertiesChild = new ModelPropertiesChild();
                                    if (relatedIds.getInt(j) == relatedObject.getLong("id")) {
                                        Log.d("RelatedObjectVis", relatedObject.getString("visiblename"));
                                        modelPropertiesChild.setModelid(relatedObject.getLong("id"));
                                        modelPropertiesChild.setModelname(relatedObject.getString("modelname"));
                                        modelPropertiesChild.setVisiblename(relatedObject.getString("visiblename"));
                                        modelPropertiesChild.setStaticModelPath(relatedObject.getString("staticmodelpath"));
                                        modelPropertiesChild.setMtlModelPath(relatedObject.getString("mtlmodelpath"));
                                        modelPropertiesChild.setSurfaceEnabled(relatedObject.getBoolean("isSurfaceEnabled"));
                                        modelPropertiesChild.setZoomEnabled(relatedObject.getBoolean("isZoomEnabled"));
                                        modelPropertiesChild.setPartsAvaliable(relatedObject.getBoolean("isPartsAvailable"));
                                        modelPropertiesChild.setPartsName(relatedObject.getString("partsname"));
                                        modelPropertiesChild.setPartsModelPath(relatedObject.getString("partsmodelpath"));
                                        modelPropertiesChild.setPartsMtlPath(relatedObject.getString("partsmtlpath"));
                                        modelPropertiesChild.setScaleValue((float) relatedObject.getDouble("scaleValue"));
                                        modelPropertiesChild.setCanModifySurface(relatedObject.getBoolean("canModifySurface"));
                                        modelPropertiesChild.setModelImage(relatedObject.getString("modelimage"));
                                        JSONArray ReHints=relatedObject.getJSONArray("hints");
                                        ArrayList<String> ReHintsArray=new ArrayList<>();
                                        for(int l=0; l<ReHints.length(); l++)
                                        {
                                            ReHintsArray.add(ReHints.getString(l));
                                        }
                                        modelPropertiesChild.setHints(ReHintsArray);
                                        JSONArray reIds = relatedObject.getJSONArray("relatedids");
                                        ArrayList<Integer> storeRelatedIds = new ArrayList<>();
                                        for (int r = 0; r < reIds.length(); r++) {
                                            storeRelatedIds.add(reIds.getInt(r));
                                        }
                                        modelPropertiesChild.setRelatedIds(storeRelatedIds);
                                        modelPropertiesChildren.add(modelPropertiesChild);

                                    }
                                    else if(addStatus==0) {
                                        if (relatedIds.getInt(j) == theModelObject.getLong("id")) {
                                            addStatus = 1;
                                            Log.d("TheModelObjectName", theModelObject.getString("visiblename"));
                                            modelPropertiesChild.setModelid(theModelObject.getLong("id"));
                                            modelPropertiesChild.setModelname(theModelObject.getString("modelname"));
                                            modelPropertiesChild.setVisiblename(theModelObject.getString("visiblename"));
                                            modelPropertiesChild.setStaticModelPath(theModelObject.getString("staticmodelpath"));
                                            modelPropertiesChild.setMtlModelPath(theModelObject.getString("mtlmodelpath"));
                                            modelPropertiesChild.setSurfaceEnabled(theModelObject.getBoolean("isSurfaceEnabled"));
                                            modelPropertiesChild.setZoomEnabled(theModelObject.getBoolean("isZoomEnabled"));
                                            modelPropertiesChild.setPartsAvaliable(theModelObject.getBoolean("isPartsAvailable"));
                                            modelPropertiesChild.setPartsName(theModelObject.getString("partsname"));
                                            modelPropertiesChild.setPartsModelPath(theModelObject.getString("partsmodelpath"));
                                            modelPropertiesChild.setPartsMtlPath(theModelObject.getString("partsmtlpath"));
                                            modelPropertiesChild.setScaleValue((float) theModelObject.getDouble("scaleValue"));
                                            modelPropertiesChild.setCanModifySurface(theModelObject.getBoolean("canModifySurface"));
                                            modelPropertiesChild.setModelImage(theModelObject.getString("modelimage"));
                                            JSONArray ReHints=theModelObject.getJSONArray("hints");
                                            ArrayList<String> ReHintsArray=new ArrayList<>();
                                            for(int l=0; l<ReHints.length(); l++)
                                            {
                                                ReHintsArray.add(ReHints.getString(l));
                                            }
                                            modelPropertiesChild.setHints(ReHintsArray);
                                            modelPropertiesChildren.add(modelPropertiesChild);
                                        }
                                    }
                                }




                            }
                            Log.d("ModelProbChiSize",""+modelPropertiesChildren.size());
                            modelProperties.setModelPropertiesChildren(modelPropertiesChildren);
                        }





                    }
                    else
                    {
                        relatedMenuPicker.setVisibility(View.GONE);
                    }
                }
                modelPropertiess.add(modelProperties);
//                if (fromSettings == 1) {
//                    if (zoomtemp != 0) {
//                        zoomMenu.setVisibility(View.VISIBLE);
//                    }
//                    else
//                    {
//                        zoomMenu.setVisibility(View.GONE);
//                    }
//
//                } else {
//                    if (modelPropertiess.get(0).getisZoomEnable() == true) {
//                        zoomMenu.setVisibility(View.VISIBLE);
//                    }
//                    else
//                    {
//                        zoomMenu.setVisibility(View.GONE);
//                    }
//                }


            }



        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.ar_menu,menu);
        eyeOpen=menu.findItem(R.id.menu_show_parts);
        eyeHide=menu.findItem(R.id.menu_hide_parts);
        if(!modelPropertiess.get(0).getPartsName().equals("null"))
        {
            ShowOrHideParts();
        }
        else
        {
            eyeOpen.setVisible(false);
            eyeHide.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.menu_show_parts:
                isPartsClicked=1;
                invalidateOptionsMenu();
                UnityPlayer.UnitySendMessage("ObjectPlacer","ShowParts","true");
                break;
            case R.id.menu_hide_parts:
                isPartsClicked=0;
                invalidateOptionsMenu();
                UnityPlayer.UnitySendMessage("ObjectPlacer","ShowParts","false");
                break;
            case R.id.ar_refresh:
                DeleteObject();
                break;
            case R.id.ar_settings:
                settingsDialog.show();
                break;
            case R.id.ar_home:
                Intent goHome=new Intent(UnityPlayerActivity.this,MenuActivity.class);
                goHome.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(goHome);
                break;
            case R.id.ar_hints:
                dialog.show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
    private void ShowOrHideParts() {
        if(isPartsClicked==0)
        {
            eyeOpen.setVisible(true);
            eyeHide.setVisible(false);
        }
        else if(isPartsClicked==1)
        {
            eyeOpen.setVisible(false);
            eyeHide.setVisible(true);
        }

    }
    public void DeleteObject()
    {
        UnityPlayer.UnitySendMessage("ObjectPlacer","DeleteObject","Yes");
    }

}
