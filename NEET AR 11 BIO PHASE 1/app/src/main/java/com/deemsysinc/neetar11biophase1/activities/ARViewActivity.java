/*
 * Copyright 2017 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.deemsysinc.neetar11biophase1.activities;

import android.app.ActivityManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.os.Debug;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.BaseTransientBottomBar;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;


import com.deemsysinc.neetar11biophase1.R;
import com.deemsysinc.neetar11biophase1.adapters.CameraHintPageAdapter;
import com.deemsysinc.neetar11biophase1.adapters.RelatedModelsAdapter;
import com.deemsysinc.neetar11biophase1.drawers.BackgroundRenderer;
import com.deemsysinc.neetar11biophase1.drawers.ObjectRenderer;
import com.deemsysinc.neetar11biophase1.drawers.PlaneRenderer;
import com.deemsysinc.neetar11biophase1.drawers.PointCloudRenderer;
import com.deemsysinc.neetar11biophase1.drawers.RenderObject;
import com.deemsysinc.neetar11biophase1.expansion.APKExpansionSupport;
import com.deemsysinc.neetar11biophase1.expansion.ZipResourceFile;
import com.deemsysinc.neetar11biophase1.models.ModelProperties;
import com.deemsysinc.neetar11biophase1.models.ModelPropertiesChild;
import com.deemsysinc.neetar11biophase1.utils.CameraPermissionHelper;
import com.deemsysinc.neetar11biophase1.utils.Coordinates;
import com.deemsysinc.neetar11biophase1.utils.DisplayRotationHelper;
import com.deemsysinc.neetar11biophase1.utils.UtilityAr;
import com.deemsysinc.neetar11biophase1.utils.Utils3D;
import com.deemsysinc.neetar11biophase1.utils.VerticalSeekBar;
import com.google.ar.core.Anchor;
import com.google.ar.core.ArCoreApk;
import com.google.ar.core.Camera;
import com.google.ar.core.Config;
import com.google.ar.core.Frame;
import com.google.ar.core.HitResult;
import com.google.ar.core.Plane;
import com.google.ar.core.PointCloud;
import com.google.ar.core.Pose;
import com.google.ar.core.Session;
import com.google.ar.core.Trackable;
import com.google.ar.core.TrackingState;
import com.google.ar.core.exceptions.UnavailableApkTooOldException;
import com.google.ar.core.exceptions.UnavailableArcoreNotInstalledException;
import com.google.ar.core.exceptions.UnavailableSdkTooOldException;
import com.google.ar.core.exceptions.UnavailableUserDeclinedInstallationException;
import com.yarolegovich.discretescrollview.DiscreteScrollView;
import com.yarolegovich.discretescrollview.transform.ScaleTransformer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
import javax.vecmath.Vector2f;
import javax.vecmath.Vector3f;


/**
 * This is a simple example that shows how to create an augmented reality (AR) application using the
 * ARCore API. The application will display any detected planes and will allow the user to tap on a
 * plane to place a 3d model of the Android robot.
 */
public class ARViewActivity extends AppCompatActivity implements GLSurfaceView.Renderer,View.OnClickListener,DiscreteScrollView.OnItemChangedListener<RelatedModelsAdapter.RelatedModelsHolder> {



    AtomicBoolean strokePoint=new AtomicBoolean(false);
    AtomicBoolean touchDown=new AtomicBoolean(false);


    Utils3D utils3D;

    Vector3f vector3f;
    Vector2f vector2f;


    int viewWidth=0;

    int viewHeight=0;

    private final float cubeHitAreaRadius = 0.08f;


    int isPartsClicked=0;

    private float mScreenWidth = 0;
    private float mScreenHeight = 0;


    public float[] viewmtx=new float[16];

    public float[] projmtx=new float[16];

    MotionEvent tap;






    boolean onZoom=false;



    DiscreteScrollView relatedMenuPicker;


    int loaderMsgTemp=0;






    private static final String TAG = ARViewActivity.class.getSimpleName();

    // Rendering. The Renderers are created here, and initialized when the GL surface is created.
    private GLSurfaceView mSurfaceView;

    private Session mSession;
    private GestureDetector mGestureDetector;
    private ScaleGestureDetector mScaleGestureDetector;
    private Snackbar mMessageSnackbar;
    private DisplayRotationHelper mDisplayRotationHelper;

    private final BackgroundRenderer mBackgroundRenderer = new BackgroundRenderer();
    private final ObjectRenderer mVirtualObject = new ObjectRenderer();

    private final RenderObject renderObject =new RenderObject();
    private final RenderObject renderMarkerObject=new RenderObject();
    private final PlaneRenderer mPlaneRenderer = new PlaneRenderer();
    private final PointCloudRenderer mPointCloud = new PointCloudRenderer();










    // Temporary matrix allocated here to reduce number of allocations for each frame.
    private final float[] mAnchorMatrix = new float[16];


    //Camera Look At Position

//    private float[] LookArCamera={0f,0f,6f,
//                                   0f,0f,-1f,
//                                    0f,1f,0f};

    private float mZoomFactor=1.0f;

    float scaleFactor=0f;

    private boolean installRequested;


    private ZipResourceFile expansionFile = null;



    // Tap handling and UI.
    private final ArrayBlockingQueue<MotionEvent> mQueuedSingleTaps = new ArrayBlockingQueue<>(16);
    private final ArrayList<Anchor> mAnchors = new ArrayList<>();


    int frametemp=0;

    int planTemp=0;


    int zoomtemp=0;


    Bundle extras;


    private String str="andy";
    private String strarray;




    LinearLayout progressContainer;

    ProgressDialog progressDialog;

    LinearLayoutManager dialoglayoutManager;


    InputStream modelStram,mtlStream;


    boolean isTouched=false;
    ArrayList<ModelProperties> modelPropertiess;
    int index=0, parentIndex=0;


    LinearLayout zoombarContainer,arStatusContainer;


    ProgressBar indicatorSurface;



    TextView ARStatusLabel;




    ImageView zoomMenu;


    TextView modelHeader;
    android.support.v7.widget.Toolbar toolbar;


    int fromSettings=0;


    int refresh=0;


    float dynamicScale=0f;

    VerticalSeekBar Zoombar;


    float intentPasser=0f;

    AppCompatDialog dialog;


    AppCompatDialog relatedModelDialog;

    RecyclerView relatedModelView;

    RecyclerView.LayoutManager relatedModelManager;


    ImageView closeRelatedModel;


    Button showRelatedModel;


    TextView relatedModelHeader;

    long themodelid=0;




    ImageView helpClose;


    TextView hintsHeader;


    String obbPath;

    float[] centerVertexOf3dObject = {0f, 0f, 0f, 1};



    //-----------------------------Expansion Files Declartions------------------------//


    public static  View.OnClickListener onClickListener;
    private int fromSame=0;
    private long thesettingmodelid=0;


    @Override
    public void onCurrentItemChanged(@Nullable RelatedModelsAdapter.RelatedModelsHolder viewHolder, int adapterPosition) {
//        Log.d("OnItemChanged","True");
    }

    //--------------------------------------------Ends----------------------------------------------------//



    RecyclerView cameraHintsList;
    LinearLayoutManager layoutManager;
    CameraHintPageAdapter cameraHintPageAdapter;

    Typeface hintsFont,toolbarFont,textFont;


    TextView headModelName;




    boolean IsMounted=false;

    float[] vertexResult = new float[4];


    MenuItem eyeOpen,eyeHide;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        UtilityAr.setStausBarColor(ARViewActivity.this,ARViewActivity.this);
         progressDialog=ProgressDialog.show(ARViewActivity.this,"","Drawing please wait..");
        setContentView(R.layout.activity_main);
        toolbar=findViewById(R.id.ar_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setTitle("");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle=new Bundle();
                Intent goBack=new Intent(ARViewActivity.this,ScanArActivity.class);
                bundle.putInt("index",index);
                bundle.putInt("parentindex",parentIndex);
                bundle.putBoolean("isMounted",IsMounted);
                bundle.putString("obbPath",obbPath);
                goBack.putExtras(bundle);
                startActivity(goBack);
            }
        });
        relatedMenuPicker=findViewById(R.id.related_model_list);
        relatedMenuPicker.setSlideOnFling(true);
        headModelName=findViewById(R.id.head_modelname);
        toolbarFont=Typeface.createFromAsset(getAssets(),"fonts/GillSans-SemiBold.ttf");
        textFont=Typeface.createFromAsset(getAssets(),"fonts/gillsansstd.otf");
        headModelName.setTypeface(toolbarFont);
        dialog=new AppCompatDialog(ARViewActivity.this);
        dialog.setContentView(R.layout.camera_hints_page);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        hintsHeader=(dialog).findViewById(R.id.hints_header);
        hintsFont=Typeface.createFromAsset(getAssets(),"fonts/GillSans-SemiBold.ttf");
        hintsHeader.setTypeface(hintsFont);
        cameraHintsList=(dialog).findViewById(R.id.help_list_ar);
        helpClose=(dialog).findViewById(R.id.help_close);
        helpClose.setOnClickListener(this);
        dialoglayoutManager=new LinearLayoutManager(this);
        cameraHintsList.setLayoutManager(dialoglayoutManager);
        showRelatedModel=findViewById(R.id.show_the_related_model);
        showRelatedModel.setOnClickListener(this);
        //InitializeRelateModelDialog();
        mSurfaceView = findViewById(R.id.surfaceview);
        progressContainer=findViewById(R.id.progress_container);
        vector2f=new Vector2f();
        vector3f=new Vector3f(0,0,0);
        zoombarContainer=findViewById(R.id.zoombar_container);
        arStatusContainer=findViewById(R.id.layout_status_explorer);
        indicatorSurface=findViewById(R.id.status_indiator);
        ARStatusLabel=findViewById(R.id.ar_status_text);
        ARStatusLabel.setTypeface(textFont);
        zoombarContainer.setVisibility(View.GONE);
        zoomMenu=findViewById(R.id.zoom_menu);
        zoomMenu.setOnClickListener(this);
        Zoombar=findViewById(R.id.mySeekBar);
        Zoombar.getProgressDrawable().setColorFilter(Color.parseColor("#27ae60"), PorterDuff.Mode.SRC_IN);
        Zoombar.getThumb().setColorFilter(Color.parseColor("#27ae60"), PorterDuff.Mode.SRC_IN);
        Zoombar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                Log.d("PrintII",""+i);
                float temp=0.1f*(float)i;
                Log.d("PrintTemp",""+temp);
                if(temp!=0f) {
                    scaleFactor = temp;
                }
                Log.d("PrintScaleFactorSeek",""+scaleFactor);
                //scaleFactor = Math.max(0.01f, Math.min(scaleFactor, 2.0f));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                zoombarContainer.setVisibility(View.GONE);



            }
        });



        layoutManager=new LinearLayoutManager(this);

//        slideUpDown=findViewById(R.id.slideUpDown);
//        slideUpDown.setOnClickListener(this);
        modelHeader=findViewById(R.id.model_header);

        if(getIntent().getExtras()!=null)
        {
            Log.d("GetStr",str);
            extras=getIntent().getExtras();
            index=extras.getInt("index");
            parentIndex=extras.getInt("parentindex");
            fromSettings=extras.getInt("fromSettings");
            frametemp=extras.getInt("frameKey");
            planTemp=extras.getInt("planKey");
            zoomtemp=extras.getInt("zoomKey");
            str=extras.getString("ImageObject");
            scaleFactor=extras.getFloat("scaleFactor");
            intentPasser=extras.getFloat("scaleFactor");
            dynamicScale=extras.getFloat("dynamicScale");
            IsMounted=extras.getBoolean("isMounted");
            obbPath=extras.getString("obbPath");
            fromSame=extras.getInt("fromWhere");
            themodelid=extras.getLong("id");
            Log.d("MounttingTheObb",""+extras.getBoolean("isMounted"));
            Log.d("GetForScale",""+scaleFactor);
            //Log.d("GetStrr",str);
        }
        modelPropertiess=new ArrayList<>();
        if(fromSame==1)
        {
            LoadRelatedModels(themodelid,index,parentIndex);
        }
        else
        {
            LoadModelProperties(index,parentIndex);
        }
        cameraHintPageAdapter=new CameraHintPageAdapter(ARViewActivity.this,modelPropertiess.get(0).getHints());
        cameraHintsList.setAdapter(cameraHintPageAdapter);

        if(modelPropertiess.get(0).getModelPropertiesChildren()!=null)
        {
            if(modelPropertiess.get(0).getModelPropertiesChildren().size()>0)
            {
                try {
                    expansionFile = APKExpansionSupport.getAPKExpansionZipFile(ARViewActivity.this, 1, 0, obbPath);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                relatedMenuPicker.setAdapter(new RelatedModelsAdapter(this,modelPropertiess.get(0).getModelPropertiesChildren(),expansionFile));
                relatedMenuPicker.setItemTransformer(new ScaleTransformer.Builder()
                        .setMinScale(0.8f)
                        .build());
                relatedMenuPicker.addOnItemChangedListener(this);
                onClickListener=new RelatedMenuClickListner(this);
            }
        }

        mDisplayRotationHelper = new DisplayRotationHelper(/*context=*/ this);

        // Set up tap listener.
        mGestureDetector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onSingleTapUp(MotionEvent event) {
                Log.d("OnSigleTapU",""+event);
                if (fromSettings == 1) {
                    if (planTemp == 0) {
                        isTouched = true;
                        strokePoint.set(true);
                        touchDown.set(false);
                        vector2f.set(event.getX(), event.getY());
                        Log.d("Called=", "" + "Up");
                            Log.d("Called=", "" + "Up");
                    } else if (planTemp == 1) {
                        onSingleTap(event);
                    }
                }
                else
                {
                    if (modelPropertiess.get(0).getIsSurfaceEnabled() == true) {
                        onSingleTap(event);
                    } else {
                        isTouched = true;
                            strokePoint.set(true);
                            touchDown.set(false);
                            vector2f.set(event.getX(), event.getY());
                            Log.d("Called=", "" + "Up");
                    }
                }

                return true;
            }

            @Override
            public boolean onDown(MotionEvent e) {
                Log.d("OnSigleTapD",""+e);
                return true;
            }

        });

        mScaleGestureDetector = new ScaleGestureDetector(this, new ScaleGestureDetector
                            .SimpleOnScaleGestureListener() {
                        @Override
                       public boolean onScale(ScaleGestureDetector detector) {
                                Log.d("GetDetector",""+detector.getScaleFactor());
                                return true;
                            }

            @Override
            public boolean onScaleBegin(ScaleGestureDetector detector) {
                            Log.d("ScaleBegin","True");
                onZoom=true;
                return true;
            }

            @Override
            public void onScaleEnd(ScaleGestureDetector detector) {
                Log.d("ScaleBegin","False");
                onZoom=false;
                super.onScaleEnd(detector);
            }
        });



        mSurfaceView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Log.d("PrintEvent",""+event);
                Log.d("PointerCount",""+event.getPointerCount());
                            if (fromSettings == 1) {
                                if (planTemp == 0) {
                                    isTouched = true;
                                    mGestureDetector.onTouchEvent(event);

                                } else if (planTemp == 1) {
                                    mGestureDetector.onTouchEvent(event);
                                }
                            } else {
                                if (modelPropertiess.get(0).getIsSurfaceEnabled() == true) {
                                    mGestureDetector.onTouchEvent(event);
                                } else {
                                    isTouched = true;
                                    mGestureDetector.onTouchEvent(event);

                                }
                }
                return true;
            }
        });


        // Set up renderer.
        mSurfaceView.setPreserveEGLContextOnPause(true);
        mSurfaceView.setEGLContextClientVersion(2);
        //mSurfaceView.setEGLConfigChooser(false);
        mSurfaceView.setEGLConfigChooser(8, 8, 8, 8, 16, 0); // Alpha used for plane blending.
        mSurfaceView.setRenderer(this);
        mSurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);

        installRequested = false;

    }

    private void LoadRelatedModels(long id,int index,int parentIndex) {
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
                    thesettingmodelid=theModelObject.getLong("id");
                    modelProperties.setModelname(theModelObject.getString("modelname"));
                    modelProperties.setVisiblename(theModelObject.getString("visiblename"));
                    headModelName.setText(theModelObject.getString("visiblename"));
                    modelHeader.setText(theModelObject.getString("visiblename"));
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
                    if(fromSettings==0) {
                        scaleFactor = (float) theModelObject.getDouble("scaleValue");
                        dynamicScale=(float) theModelObject.getDouble("scaleValue");
                        intentPasser=(float)theModelObject.getDouble("scaleValue");
                    }
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
                                thesettingmodelid=relatedObject.getLong("id");
                                modelProperties.setModelname(relatedObject.getString("modelname"));
                                modelProperties.setVisiblename(relatedObject.getString("visiblename"));
                                headModelName.setText(relatedObject.getString("visiblename"));
                                modelHeader.setText(relatedObject.getString("visiblename"));
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
                                if (fromSettings == 0) {
                                    scaleFactor = (float) relatedObject.getDouble("scaleValue");
                                    dynamicScale = (float) relatedObject.getDouble("scaleValue");
                                    intentPasser = (float) relatedObject.getDouble("scaleValue");
                                }

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
                if (fromSettings == 1) {
                    if (zoomtemp != 0) {
                        zoomMenu.setVisibility(View.VISIBLE);
                    }
                    else
                    {
                        zoomMenu.setVisibility(View.GONE);
                    }

                } else {
                    if (modelPropertiess.get(0).getisZoomEnable() == true) {
                        zoomMenu.setVisibility(View.VISIBLE);
                    }
                    else
                    {
                        zoomMenu.setVisibility(View.GONE);
                    }
                }


            }



        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void LoadModelProperties(int index, int parentIndex) {
        try {
            JSONArray jsonArray=new JSONArray(loadJSONFromAsset());
            JSONObject jsonObject=jsonArray.getJSONObject(parentIndex);
            JSONArray modelArray=jsonObject.getJSONArray("models");
            JSONObject modelObject=modelArray.getJSONObject(index);
            ModelProperties modelProperties=new ModelProperties();
            modelProperties.setModelid(modelObject.getLong("id"));
            thesettingmodelid=modelObject.getLong("id");
            modelProperties.setModelname(modelObject.getString("modelname"));
            modelProperties.setVisiblename(modelObject.getString("visiblename"));
            headModelName.setText(modelObject.getString("visiblename"));
            modelHeader.setText(modelObject.getString("visiblename"));
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
            if(fromSettings==0) {
                scaleFactor = (float) modelObject.getDouble("scaleValue");
                dynamicScale=(float) modelObject.getDouble("scaleValue");
                intentPasser=(float)modelObject.getDouble("scaleValue");
            }
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
            if (fromSettings == 1) {
                if (zoomtemp != 0) {
                    zoomMenu.setVisibility(View.VISIBLE);
                }
                else
                {
                    zoomMenu.setVisibility(View.GONE);
                }

            } else {
                if (modelPropertiess.get(0).getisZoomEnable() == true) {
                    zoomMenu.setVisibility(View.VISIBLE);
                }
                else
                {
                    zoomMenu.setVisibility(View.GONE);
                }
            }




        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        // ARCore requires camera permissions to operate. If we did not yet obtain runtime
        // permission on Android M and above, now is a good time to ask the user for it.
           if(mSession==null)
           {
               Exception exception = null;
               String message = null;
               try {
                   switch (ArCoreApk.getInstance().requestInstall(this, !installRequested)) {
                       case INSTALL_REQUESTED:
                           installRequested = true;
                           return;
                       case INSTALLED:
                           break;
                   }
                   if (!CameraPermissionHelper.hasCameraPermission(this)) {
                       CameraPermissionHelper.requestCameraPermission(this);
                       return;
                   }
                   mSession = new Session(/* context= */ this);
               }catch (UnavailableArcoreNotInstalledException
                       | UnavailableUserDeclinedInstallationException e) {
                   message = "Please install ARCore";
                   exception = e;
               } catch (UnavailableApkTooOldException e) {
                   message = "Please update ARCore";
                   exception = e;
               } catch (UnavailableSdkTooOldException e) {
                   message = "Please update this app";
                   exception = e;
               } catch (Exception e) {
                   message = "This device does not support AR";
                   exception = e;
               }
               if (message != null) {
                   showSnackbarMessage(message, true);
                   Log.e(TAG, "Exception creating session", exception);
                   return;
               }

               Config config = new Config(mSession);
               config.setLightEstimationMode(Config.LightEstimationMode.AMBIENT_INTENSITY);
               if (!mSession.isSupported(config)) {
                   showSnackbarMessage("This device does not support AR", true);
               }
               mSession.configure(config);
               if(config.getLightEstimationMode()== Config.LightEstimationMode.DISABLED)
               {
                   Log.d("PrintLigthEsti","Disabled");
               }
           }
                if(fromSettings==1)
                {
                    if(planTemp!=0)
                    {
                        arStatusContainer.setVisibility(View.VISIBLE);
                        indicatorSurface.setVisibility(View.VISIBLE);
                        ARStatusLabel.setText("Move Camera to plane surface and hold on until we detect the plane.");
                    }
                }
                else
                {
                    if(modelPropertiess.get(0).getIsSurfaceEnabled()==true)
                    {
                        arStatusContainer.setVisibility(View.VISIBLE);
                        indicatorSurface.setVisibility(View.VISIBLE);
                        ARStatusLabel.setText("Move Camera to plane surface and hold on until we detect the plane.");
                    }

            }
            mSession.resume();
            mSurfaceView.onResume();
            mDisplayRotationHelper.onResume();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        mScreenHeight = displayMetrics.heightPixels;
        mScreenWidth = displayMetrics.widthPixels;
    }

    @Override
    public void onPause() {
        super.onPause();
        // Note that the order matters - GLSurfaceView is paused first so that it does not try
        // to query the session. If Session is paused before GLSurfaceView, GLSurfaceView may
        // still call mSession.update() and get a SessionPausedException.
        mDisplayRotationHelper.onPause();
        mSurfaceView.onPause();
        if (mSession != null) {
            mSession.pause();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] results) {
        if (!CameraPermissionHelper.hasCameraPermission(this)) {
            Toast.makeText(this,
                "Camera permission is needed to run this application", Toast.LENGTH_LONG).show();
            if (!CameraPermissionHelper.shouldShowRequestPermissionRationale(this)) {
                // Permission denied with checking "Do not ask again".
                CameraPermissionHelper.launchPermissionSettings(this);
            }
            finish();
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            // Standard Android full-screen functionality.
            getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }
    }

    private void onSingleTap(MotionEvent e) {
        // Queue tap if there is space. Tap is lost if queue is full.
        mQueuedSingleTaps.offer(e);
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        GLES20.glClearColor(0.1f, 0.1f, 0.1f, 1.0f);

        RenderTheModel();

    }

    private void RenderTheModel() {
        //imageObject=new ImageObject(ARViewActivity.this);
        mBackgroundRenderer.createOnGlThread(/*context=*/ ARViewActivity.this);
        if (mSession != null) {
            mSession.setCameraTextureName(mBackgroundRenderer.getTextureId());
        }
        try {
            Log.d("CalledWhat","DoinBackground");
            Log.d("PrintStatModPaths",modelPropertiess.get(0).getStaticModelPath());
            String[] parts = modelPropertiess.get(0).getMtlModelPath().split("/");
            Log.d("PrintPArts",parts[0]);
            renderObject.createOnGlThread(IsMounted,obbPath,this,modelPropertiess.get(0).getStaticModelPath(),modelPropertiess.get(0).getMtlModelPath(),"",false,parts[0]);
            if(!modelPropertiess.get(0).getPartsName().equals("null")||!modelPropertiess.get(0).getPartsName().equals(""))
            {
                String[] parts1 = modelPropertiess.get(0).getPartsMtlPath().split("/");
                Log.d("PrintPArts",parts1[0]);
                renderMarkerObject.createOnGlThread(IsMounted,obbPath,this,modelPropertiess.get(0).getPartsModelPath(),modelPropertiess.get(0).getPartsMtlPath(),"",true,parts1[0]);
            }
        } catch (Exception e) {
            Log.e(TAG, "Failed to read obj file");
        }
        try {
            mPlaneRenderer.createOnGlThread(/*context=*/ARViewActivity.this, "trigrid.png");
        } catch (IOException e) {
            Log.e(TAG, "Failed to read plane texture");
        }
        mPointCloud.createOnGlThread(/*context=*/ARViewActivity.this);

        ARViewActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(progressDialog.isShowing()||progressDialog!=null) {
                    progressDialog.dismiss();
                }

            }
        });
        if(fromSettings==1) {
            if (planTemp == 0) {
                ARViewActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        arStatusContainer.setVisibility(View.VISIBLE);
                        indicatorSurface.setVisibility(View.GONE);
                        ARStatusLabel.setText("Tap anywhere to place object");
                    }
                });

            }
        }
        else
        {
            if(modelPropertiess.get(0).getIsSurfaceEnabled()==false)
            {
                ARViewActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        arStatusContainer.setVisibility(View.VISIBLE);
                        indicatorSurface.setVisibility(View.GONE);
                        ARStatusLabel.setText("Tap anywhere to place object");
                    }
                });
            }
        }

    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        mDisplayRotationHelper.onSurfaceChanged(width, height);
        GLES20.glViewport(0, 0, width, height);
        viewWidth=width;
        viewHeight=height;




    }

    @Override
    public void onDrawFrame(GL10 gl) {
        // Clear screen to notify driver it should not load any pixels from previous frame.
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

        Log.d("CalledWhat","OnDrawFrame");
        if (mSession == null) {
            return;
        }
        // Notify ARCore session that the view size changed so that the perspective matrix and
        // the video background can be properly adjusted.
        mDisplayRotationHelper.updateSessionIfNeeded(mSession);





        try {
            // Obtain the current frame from ARSession. When the configuration is set to
            // UpdateMode.BLOCKING (it is by default), this will throttle the rendering to the
            // camera framerate.
            Frame frame = mSession.update();
            Camera camera = frame.getCamera();
            Log.d("PrintRefresh",""+refresh);

                    if (fromSettings == 1) {
                        if (planTemp == 0) {
                            if (isTouched) {
                                if (strokePoint.get()) {
                                    strokePoint.set(false);
                                    addStroke(vector2f);
                                    ARViewActivity.this.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            arStatusContainer.setVisibility(View.GONE);
                                        }
                                    });
                                    Log.d("GetPointx", "" + vector3f.getX());
                                    Pose airRotation= Pose.makeRotation(0f,camera.getPose().qy(),-0.0f,camera.getPose().qw());
                                    final Pose air = Pose.makeTranslation(vector3f.getX(), vector3f.getY(), vector3f.getZ());
                                    final Pose newair=air.compose(airRotation);

                                    Log.d("PrintAir", "" + air);
                                    Log.d("PrintCameraPos", "" + camera.getPose());
                                    float dx = camera.getPose().tx() - newair.tx();
                                    float dy = camera.getPose().ty() - newair.ty();
                                    float dz = camera.getPose().tz() - newair.tz();
                                    float distanceInMeters = (float) Math.sqrt(dx * dx + dy * dy + dz * dz);
                                    Log.d("DistanceInMetersU", "" + distanceInMeters);
                                    if (round(distanceInMeters, 2) >= 1.11) {
                                        ARViewActivity.this.runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                AlertDialog.Builder builder = new AlertDialog.Builder(ARViewActivity.this);
                                                builder.setTitle("Alert");
                                                builder.setMessage("Object is near to the camera.shall we reposition it to recommended distance?");
                                                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialogInterface, int i) {
                                                        dialogInterface.dismiss();
                                                        float x = newair.tx() + 0f;
                                                        float y = newair.ty() + 0.5f;
                                                        float z = newair.tz() + 0f;
                                                        Pose pose = Pose.makeTranslation(x, y, z).extractTranslation();
                                                        Log.d("PrintBoth", "" + newair + " " + "And" + " " + pose);
                                                        if (mAnchors.size() >= 1) {
                                                            mAnchors.get(0).detach();
                                                            mAnchors.remove(0);
                                                        }
                                                        mAnchors.add(mSession.createAnchor(pose));
                                                    }
                                                });
                                                builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialogInterface, int i) {
                                                        dialogInterface.dismiss();
                                                    }
                                                });
                                                builder.show();
                                            }
                                        });
                                    } else {
                                        if (mAnchors.size() >= 1) {
                                            mAnchors.get(0).detach();
                                            mAnchors.remove(0);
                                        }
                                        mAnchors.add(mSession.createAnchor(newair));
                                    }



                                }
                            }


                        } else if (planTemp == 1) {
                            tap = mQueuedSingleTaps.poll();
                            if (tap != null && camera.getTrackingState() == TrackingState.TRACKING) {

                                for (final HitResult hit : frame.hitTest(tap)) {
                                    Log.d("PrintHit", "" + frame.hitTest(tap).get(0));
                                    // Check if any plane was hit, and if it was hit inside the plane polygon
                                    Trackable trackable = hit.getTrackable();
                                    Log.d("FrameHit", "True");
                                    Log.d("GetDistance", "" + hit.getDistance());

                                    if (trackable instanceof Plane
                                            && ((Plane) trackable).isPoseInPolygon(hit.getHitPose())) {
                                        ARViewActivity.this.runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                arStatusContainer.setVisibility(View.GONE);
                                            }
                                        });
                                        Log.d("HittedPolygon", "True");
                                        // Cap the number of objects created. This avoids overloading both the
                                        // rendering system and ARCore.
                                        // Adding an Anchor tells ARCore that it should track this position in
                                        // space. This anchor is created on the Plane to place the 3d model
                                        // in the correct position relative both to the world and to the plane.


                                        //-------------Returns the distance from the camera to the hit loacation in meters

                                        if (round(hit.getDistance(), 1) < 0.5) {
                                            ARViewActivity.this.runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    AlertDialog.Builder builder = new AlertDialog.Builder(ARViewActivity.this);
                                                    builder.setTitle("Alert");
                                                    builder.setMessage("Object is near to the camera.shall we reposition it to recommended distance?");
                                                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialogInterface, int i) {
                                                            if (mAnchors.size() >= 1) {
                                                                mAnchors.get(0).detach();
                                                                mAnchors.remove(0);
                                                            }
                                                            //--------------------------------------------------------------------------------//
                                                            mAnchors.add(hit.getTrackable().createAnchor(hit.getHitPose().compose(Pose.makeTranslation(0f, 0f, -0.5f).extractTranslation())));
                                                            dialogInterface.dismiss();
                                                        }
                                                    });
                                                    builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialogInterface, int i) {
                                                            dialogInterface.dismiss();
                                                        }
                                                    });
                                                    builder.show();

                                                }
                                            });

                                        } else {
                                            Log.d("GetTheHitPose",""+hit.getHitPose());
                                            if (mAnchors.size() >= 1) {
                                                mAnchors.get(0).detach();
                                                mAnchors.remove(0);
                                            }

                                            //--------------------------------------------------------------------------------//
                                            mAnchors.add(hit.createAnchor());
                                        }


                                        break;
                                    }
                                }
                            }
                        }
                    } else {
                        if (modelPropertiess.get(0).getIsSurfaceEnabled() == true) {
                            tap = mQueuedSingleTaps.poll();

                            if (tap != null && camera.getTrackingState() == TrackingState.TRACKING) {

                                for (final HitResult hit : frame.hitTest(tap)) {
                                    Log.d("PrintHit", "" + frame.hitTest(tap).get(0));
                                    // Check if any plane was hit, and if it was hit inside the plane polygon
                                    Trackable trackable = hit.getTrackable();
                                    Log.d("FrameHit", "True");
                                    Log.d("GetDistance", "" + hit.getDistance());

                                    if (trackable instanceof Plane
                                            && ((Plane) trackable).isPoseInPolygon(hit.getHitPose())) {
                                        ARViewActivity.this.runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                arStatusContainer.setVisibility(View.GONE);
                                            }
                                        });
                                        //hideLoadingMessage();
                                        Log.d("HittedPolygon", "True");
                                        // Cap the number of objects created. This avoids overloading both the
                                        // rendering system and ARCore.
                                        // Adding an Anchor tells ARCore that it should track this position in
                                        // space. This anchor is created on the Plane to place the 3d model
                                        // in the correct position relative both to the world and to the plane.


                                        //-------------Returns the distance from the camera to the hit loacation in meters

                                        if (round(hit.getDistance(), 1) < 0.5) {
                                            ARViewActivity.this.runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    AlertDialog.Builder builder = new AlertDialog.Builder(ARViewActivity.this);
                                                    builder.setTitle("Alert");
                                                    builder.setMessage("Object is near to the camera.shall we reposition it to recommended distance?");
                                                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialogInterface, int i) {

                                                            if (mAnchors.size() >= 1) {
                                                                mAnchors.get(0).detach();
                                                                mAnchors.remove(0);
                                                            }

                                                            //--------------------------------------------------------------------------------//
                                                            mAnchors.add(hit.getTrackable().createAnchor(hit.getHitPose().compose(Pose.makeTranslation(0f, 0f, -0.5f).extractTranslation())));
                                                            dialogInterface.dismiss();
                                                        }
                                                    });
                                                    builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialogInterface, int i) {
                                                            dialogInterface.dismiss();
                                                        }
                                                    });
                                                    builder.show();

                                                }
                                            });

                                        } else {
                                            Log.d("GetTheHitPose",""+hit.getHitPose());

                                            if (mAnchors.size() >= 1) {
                                                mAnchors.get(0).detach();
                                                mAnchors.remove(0);
                                            }

                                            //--------------------------------------------------------------------------------//
                                            mAnchors.add(hit.createAnchor());
                                        }

                                        //mAnchors.add(hit.getTrackable().createAnchor(hit.getHitPose().compose(Pose.makeTranslation(0, 0.2f, 0))));

                                        // Hits are sorted by depth. Consider only closest hit on a plane.
                                        break;
                                    }
                                }
                            }
                        } else {
                            if (isTouched) {
                                if (strokePoint.get()) {
                                    strokePoint.set(false);
                                    addStroke(vector2f);
                                    ARViewActivity.this.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            arStatusContainer.setVisibility(View.GONE);
                                        }
                                    });


                                     float sqrtHalf = (float) Math.sqrt(0.5f);
                                     Log.d("PrintSqrtHalf",""+sqrtHalf);
                                   Pose airRotation= Pose.makeRotation(0f,camera.getPose().qy(),-0.0f,camera.getPose().qw());
                                    final Pose air = Pose.makeTranslation(vector3f.getX(), vector3f.getY(), vector3f.getZ()).extractTranslation();
                                    final Pose newair=air.compose(airRotation);
                                    Log.d("PoseTheRotation",""+air.extractRotation());
                                    Log.d("PrintAir", "" + newair);
                                    Log.d("PrintCameraPos", "" + camera.getPose());
                                    float dx = camera.getPose().tx() - newair.tx();
                                    float dy = camera.getPose().ty() - newair.ty();
                                    float dz = camera.getPose().tz() - newair.tz();

                                    float distanceInMeters = (float) Math.sqrt(dx * dx + dy * dy + dz * dz);
                                    Log.d("DistanceInMetersU", "" + distanceInMeters);
                                    if (round(distanceInMeters, 2) >= 1.11) {
                                        ARViewActivity.this.runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                AlertDialog.Builder builder = new AlertDialog.Builder(ARViewActivity.this);
                                                builder.setTitle("Alert");
                                                builder.setMessage("Object is near to the camera.shall we reposition it to recommended distance?");
                                                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialogInterface, int i) {
                                                        dialogInterface.dismiss();
                                                        float x = newair.tx() + 0f;
                                                        float y = newair.ty() + 0.5f;
                                                        float z = newair.tz() + 0f;
                                                        Pose pose = Pose.makeTranslation(x, y, z).extractTranslation();
                                                        Log.d("PrintBoth", "" + newair + " " + "And" + " " + pose);
                                                        if (mAnchors.size() >= 1) {
                                                            mAnchors.get(0).detach();
                                                            mAnchors.remove(0);
                                                        }
                                                        mAnchors.add(mSession.createAnchor(pose));
                                                    }
                                                });
                                                builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialogInterface, int i) {
                                                        dialogInterface.dismiss();
                                                    }
                                                });
                                                builder.show();
                                            }
                                        });
                                    } else {
                                        if (mAnchors.size() >= 1) {
                                            mAnchors.get(0).detach();
                                            mAnchors.remove(0);
                                        }
                                        mAnchors.add(mSession.createAnchor(newair));
                                    }

                                }

                            }

                        }

                    }


            // Draw background.
            mBackgroundRenderer.draw(frame,mZoomFactor);

            // If not tracking, don't draw 3d objects.
            if (camera.getTrackingState() == TrackingState.PAUSED) {
                return;
            }

            // Get projection matrix.
            //float[] projmtx = new float[16];
            camera.getProjectionMatrix(projmtx, 0, 0.1f, 100.0f);





            camera.getViewMatrix(viewmtx, 0);










            // Compute lighting from average intensity of the image.
            final float lightIntensity = frame.getLightEstimate().getPixelIntensity();


            //Log.d("LightIntensity",""+lightIntensity);

            // Visualize tracked points.
            if(fromSettings==1) {
                if (planTemp != 0) {
                    PointCloud pointCloud = frame.acquirePointCloud();
                    mPointCloud.update(pointCloud);
                    mPointCloud.draw(viewmtx, projmtx);
                    pointCloud.release();
                }
            }
            else
            {
                if(modelPropertiess.get(0).getIsSurfaceEnabled()==true)
                {
                    if (planTemp != 0) {
                        PointCloud pointCloud = frame.acquirePointCloud();
                        mPointCloud.update(pointCloud);
                        mPointCloud.draw(viewmtx, projmtx);
                        pointCloud.release();
                    }
                }
            }
            // Application is responsible for releasing the point cloud resources after
            // using it.


            // Check if we detected at least one plane. If so, hide the loading message.
            if(fromSettings==1)
            {
                if(planTemp!=0)
                {
                        for (Plane plane : mSession.getAllTrackables(Plane.class)) {
                            if (plane.getType() == com.google.ar.core.Plane.Type.HORIZONTAL_UPWARD_FACING
                                    && plane.getTrackingState() == TrackingState.TRACKING) {
                                if(loaderMsgTemp==0) {
                                    loaderMsgTemp=1;
                                    ARViewActivity.this.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            indicatorSurface.setVisibility(View.GONE);
                                            ARStatusLabel.setText("Plane Detected,Tap on the plane to place an object");
                                        }
                                    });

                                    //showLoadingMessage("Surface Detected tab on the plane to place object");
                                }
                                break;
                            }
                        }

                    // Visualize planes.
                        mPlaneRenderer.drawPlanes(
                                mSession.getAllTrackables(Plane.class), camera.getDisplayOrientedPose(), projmtx);
                }
            }
            else
            {
                if(modelPropertiess.get(0).getIsSurfaceEnabled()==true)
                {
                        for (Plane plane : mSession.getAllTrackables(Plane.class)) {
                            if (plane.getType() == com.google.ar.core.Plane.Type.HORIZONTAL_UPWARD_FACING
                                    && plane.getTrackingState() == TrackingState.TRACKING) {
                                if(loaderMsgTemp==0) {
                                    loaderMsgTemp=1;
                                    ARViewActivity.this.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            indicatorSurface.setVisibility(View.GONE);
                                            ARStatusLabel.setText("Plane Detected,Tap on the plane to place an object");
                                        }
                                    });
                                    //showLoadingMessage("Surface Detected tab on the plane to place object");
                                }
                                break;
                            }
                        }
                    // Visualize planes.
                    mPlaneRenderer.drawPlanes(
                            mSession.getAllTrackables(Plane.class), camera.getDisplayOrientedPose(), projmtx);
                }
            }

            Log.d("AnchorArrLstSize",""+mAnchors.size());
            if(refresh==1) {
                refresh = 0;
                if (fromSettings == 1) {
                    if (planTemp == 1) {
                        for(Plane plane:mSession.getAllTrackables(Plane.class))
                        {

                        }
                           if(mAnchors!=null||mAnchors.size()>0) {
                               for (int k = 0; k < mAnchors.size(); k++) {
                                   mAnchors.remove(k);
                               }
                           }
                        for (Anchor anchor : mAnchors) {

                            anchor.getPose().toMatrix(mAnchorMatrix, 0);
                            if(isPartsClicked==1)
                            {
                                renderMarkerObject.draw(viewmtx, projmtx, lightIntensity, mAnchorMatrix, scaleFactor,isPartsClicked);
                            }
                            else {
                                renderObject.draw(viewmtx, projmtx, lightIntensity, mAnchorMatrix, scaleFactor,isPartsClicked);
                            }
                        }
                    } else {
                        if(mAnchors!=null||mAnchors.size()>0) {
                            for (int k = 0; k < mAnchors.size(); k++) {
                                mAnchors.remove(k);
                            }
                        }
                        for (Anchor anchor : mAnchors) {
                            anchor.getPose().toMatrix(mAnchorMatrix, 0);
                            if(isPartsClicked==1)
                            {
                                renderMarkerObject.draw(viewmtx, projmtx, lightIntensity, mAnchorMatrix, scaleFactor,isPartsClicked);
                            }
                            else {
                                renderObject.draw(viewmtx, projmtx, lightIntensity, mAnchorMatrix, scaleFactor,isPartsClicked);
                            }
                        }
                    }
                } else {
                    if (modelPropertiess.get(0).getIsSurfaceEnabled() == true) {
                        if(mAnchors!=null || mAnchors.size()>0) {
                            for (int k = 0; k < mAnchors.size(); k++) {
                                mAnchors.remove(k);

                            }
                        }
                        for (Anchor anchor : mAnchors) {
                            anchor.getPose().toMatrix(mAnchorMatrix, 0);
                            if(isPartsClicked==1)
                            {
                                renderMarkerObject.draw(viewmtx, projmtx, lightIntensity, mAnchorMatrix, scaleFactor,isPartsClicked);
                            }
                            else {
                                renderObject.draw(viewmtx, projmtx, lightIntensity, mAnchorMatrix, scaleFactor,isPartsClicked);
                            }
                        }


                    } else {
                           if(mAnchors!=null || mAnchors.size()>0) {
                               for (int k = 0; k < mAnchors.size(); k++) {
                                   mAnchors.remove(k);

                               }
                           }
                        for (Anchor anchor : mAnchors) {
                            anchor.getPose().toMatrix(mAnchorMatrix, 0);
                            if(isPartsClicked==1)
                            {
                                renderMarkerObject.draw(viewmtx, projmtx, lightIntensity, mAnchorMatrix, scaleFactor,isPartsClicked);
                            }
                            else {
                                renderObject.draw(viewmtx, projmtx, lightIntensity, mAnchorMatrix, scaleFactor,isPartsClicked);
                            }
                        }
                    }
                }
            }
            else
            {
                for (Anchor anchor : mAnchors) {
                    anchor.getPose().toMatrix(mAnchorMatrix, 0);
                    Log.d("GetScaleFor",""+scaleFactor);
                    Log.d("PrintLightIntensity",""+lightIntensity);
                    if(isPartsClicked==1)
                    {
                        renderMarkerObject.draw(viewmtx, projmtx, lightIntensity, mAnchorMatrix, scaleFactor,isPartsClicked);
                    }
                    else {
                        renderObject.draw(viewmtx, projmtx, lightIntensity, mAnchorMatrix, scaleFactor,isPartsClicked);
                    }
                }
            }


         }catch (Throwable t) {
            Log.e(TAG, "Exception on the OpenGL thread", t);
        }
    }

    private void showSnackbarMessage(String message, boolean finishOnDismiss) {
        mMessageSnackbar = Snackbar.make(
            ARViewActivity.this.findViewById(android.R.id.content),
            message, Snackbar.LENGTH_INDEFINITE);
        View view=mMessageSnackbar.getView();
        view.setBackgroundColor(Color.parseColor("#80000000"));
        TextView tv = view.findViewById(android.support.design.R.id.snackbar_text);

        tv.setTypeface(textFont);
        tv.setTextSize(18f);
        if (finishOnDismiss) {
            mMessageSnackbar.setAction(
                "Dismiss",
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mMessageSnackbar.dismiss();
                    }
                });
            mMessageSnackbar.addCallback(
                new BaseTransientBottomBar.BaseCallback<Snackbar>() {
                    @Override
                    public void onDismissed(Snackbar transientBottomBar, int event) {
                        super.onDismissed(transientBottomBar, event);
                        finish();
                    }
                });
        }
        mMessageSnackbar.show();
    }


    @Override
    public void onClick(View view) {

        if(view==zoomMenu)
        {
            zoombarContainer.setVisibility(View.VISIBLE);
        }
        if(view==helpClose)
        {
            dialog.dismiss();
        }
        if(view==closeRelatedModel)
        {
            relatedModelDialog.dismiss();
        }




    }

    @Override
    public void onBackPressed() {
        Bundle bundle=new Bundle();
        Intent goBack=new Intent(ARViewActivity.this,ScanArActivity.class);
        bundle.putInt("index",index);
        bundle.putInt("parentindex",parentIndex);
        bundle.putBoolean("isMounted",IsMounted);
        bundle.putString("obbPath",obbPath);
        goBack.putExtras(bundle);
        startActivity(goBack);
    }
    public static float round(float d, int decimalPlace) {
        BigDecimal bd = new BigDecimal(Float.toString(d));
        bd = bd.setScale(decimalPlace, BigDecimal.ROUND_HALF_UP);
        return bd.floatValue();
    }
    private void addPoint(Vector2f touchPoint)
    {
        Vector3f newPoint = Coordinates.GetWorldCoords(touchPoint, mScreenWidth, mScreenHeight, projmtx, viewmtx);
        addPoint(newPoint);
    }

    private void addPoint(Vector3f newPoint) {
        if(Utils3D.distanceCheck(newPoint,vector3f))
        {
            Vector3f p = utils3D.update(newPoint);
            vector3f = new Vector3f(p);
        }

    }

    private void addStroke(Vector2f touchPoint)
    {
        Vector3f newPoint=Coordinates.GetWorldCoords(touchPoint,mScreenWidth,mScreenHeight,projmtx,viewmtx);
        addStroke(newPoint);
    }

    private void addStroke(Vector3f newPoint)
    {
        Log.d("PrintNewPont",""+newPoint);
        utils3D=new Utils3D(0.105);
        for(int i=0; i<1500; i++)
        {
            utils3D.update(newPoint);
        }
        Vector3f p=utils3D.update(newPoint);
        vector3f=new Vector3f(p);
        Log.d("PrintVec3f",""+vector3f);

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
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.ar_menu,menu);
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

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        Log.d("PrintPartsName",modelPropertiess.get(0).getPartsName());
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
            case R.id.ar_settings:
                Bundle bundle=new Bundle();
                bundle.putInt("frameKey",frametemp);
                if(fromSettings==0) {
                    if (modelPropertiess.get(0).getIsSurfaceEnabled() == true) {
                        planTemp = 1;
                    } else {
                        planTemp = 0;
                    }
                    if (modelPropertiess.get(0).getisZoomEnable() == true) {
                        zoomtemp = 1;
                    } else {
                        zoomtemp = 0;
                    }
                }
                bundle.putInt("planKey", planTemp);
                bundle.putInt("zoomKey",zoomtemp);
                bundle.putString("ImageObject",modelPropertiess.get(0).getModelname());
                Log.d("PrintScale",""+scaleFactor);
                bundle.putFloat("scaleFactor",intentPasser);
                bundle.putFloat("dynamicScale",dynamicScale);
                bundle.putInt("index",index);
                bundle.putInt("parentindex",parentIndex);
                bundle.putBoolean("isMounted",IsMounted);
                bundle.putString("obbPath",obbPath);
                bundle.putInt("fromWhere",fromSame);
                bundle.putLong("id",thesettingmodelid);
                bundle.putBoolean("canModifySurface",modelPropertiess.get(0).getCanModifySurface());
                Intent goSettings=new Intent(ARViewActivity.this,SettingsActivity.class);
                goSettings.putExtras(bundle);
                startActivity(goSettings);
                overridePendingTransition(R.anim.slide_in_up,R.anim.slide_out_up);
                break;
            case R.id.ar_home:
                Intent goHome=new Intent(ARViewActivity.this,MenuActivity.class);
                startActivity(goHome);
                break;
            case R.id.ar_refresh:
                 refresh=1;
                 mSession.resume();
                 mSurfaceView.onResume();
                 mDisplayRotationHelper.onResume();
                arStatusContainer.setVisibility(View.VISIBLE);
                indicatorSurface.setVisibility(View.VISIBLE);
                ARStatusLabel.setText("Initializing AR Session...");
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        indicatorSurface.setVisibility(View.GONE);
                        if(fromSettings==1)
                        {
                            if(planTemp!=0)
                            {
                                ARStatusLabel.setText("Tap anywhere in the plane to place object");
                            }
                            else
                            {
                                ARStatusLabel.setText("Tap anywhere to place object");
                            }
                        }
                        else
                        {
                            if(modelPropertiess.get(0).getIsSurfaceEnabled()==true)
                            {
                                ARStatusLabel.setText("Tap anywhere in the plane to place object");
                            }
                            else
                            {

                                ARStatusLabel.setText("Tap anywhere to place object");
                            }
                        }
                    }
                },3000);



                break;
            case R.id.ar_hints:
                dialog.show();
                break;
            case R.id.menu_show_parts:
                isPartsClicked=1;
                invalidateOptionsMenu();
                break;
            case R.id.menu_hide_parts:
                isPartsClicked=0;
                invalidateOptionsMenu();
                break;


        }
        return super.onOptionsItemSelected(item);
    }
    public class RelatedMenuClickListner implements View.OnClickListener
    {
        Context context;
        RelatedMenuClickListner(Context context)
        {
            this.context=context;
        }
        @Override
        public void onClick(View view) {
            int itemposition=relatedMenuPicker.getChildLayoutPosition(view);
            Bundle bundle=new Bundle();
            Intent loadRelatedModel=new Intent(ARViewActivity.this,ARViewActivity.class);
            bundle.putInt("index",index);
            bundle.putInt("parentindex",parentIndex);
            bundle.putLong("id",modelPropertiess.get(0).getModelPropertiesChildren().get(itemposition).getModelid());
            Log.d("TheRelatedId",""+modelPropertiess.get(0).getModelPropertiesChildren().get(itemposition).getModelid());
            bundle.putInt("fromWhere",1);
//            bundle.putString("ImageObject",rawResult.getText().toString());
            bundle.putBoolean("isMounted",IsMounted);
            bundle.putString("obbPath",obbPath);
            loadRelatedModel.putExtras(bundle);
            startActivity(loadRelatedModel);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

    }
    private ActivityManager.MemoryInfo getAvailableMemory() {
        ActivityManager activityManager = (ActivityManager) this.getSystemService(ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
        activityManager.getMemoryInfo(memoryInfo);
        return memoryInfo;
    }
    public static void logHeap() {
        Double allocated = new Double(Debug.getNativeHeapAllocatedSize())/new Double((1048576));
        Double available = new Double(Debug.getNativeHeapSize())/1048576.0;
        Double free = new Double(Debug.getNativeHeapFreeSize())/1048576.0;
        DecimalFormat df = new DecimalFormat();
        df.setMaximumFractionDigits(2);
        df.setMinimumFractionDigits(2);

        Log.d("TheArViewActivity", "debug. =================================");
        Log.d("TheArViewActivity", "debug.heap native: allocated " + df.format(allocated) + "MB of " + df.format(available) + "MB (" + df.format(free) + "MB free)");
        Log.d("TheArViewActivity", "debug.memory: allocated: " + df.format(new Double(Runtime.getRuntime().totalMemory()/1048576)) + "MB of " + df.format(new Double(Runtime.getRuntime().maxMemory()/1048576))+ "MB (" + df.format(new Double(Runtime.getRuntime().freeMemory()/1048576)) +"MB free)");
    }


}
