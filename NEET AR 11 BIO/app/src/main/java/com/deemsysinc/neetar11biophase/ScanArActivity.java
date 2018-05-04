package com.deemsysinc.neetar11biophase;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ObbInfo;
import android.content.res.ObbScanner;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.storage.OnObbStateChangeListener;
import android.os.storage.StorageManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.google.zxing.Result;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import me.dm7.barcodescanner.zxing.ZXingScannerView;


/**
 * Created by Deemsys on 14-12-2017.
 */

public class ScanArActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler,View.OnClickListener {
    private static final String LOG_TAG = "Barcode Scanner API";
    private static final int PHOTO_REQUEST = 10;
    private TextView scanResults;

    private Uri imageUri;
    private static final int REQUEST_WRITE_PERMISSION = 20;
    private static final String SAVED_INSTANCE_URI = "uri";
    private static final String SAVED_INSTANCE_RESULT = "result";
    private ZXingScannerView mScannerView;

    Bundle extras;

    int parentIndex=0;
    int index=0;
    ArrayList<ModelProperties> modelPropertiess;

    Toolbar scanToolbar;

    TextView hintesLabel;

    AppCompatDialog dialog;

    RecyclerView hintsList;


    LinearLayoutManager layoutManager;


    ArrayList<String> hints;


    CameraHintPageAdapter cameraHintPageAdapter;


    ImageView skip;

    TextView hintsHeader;

    Typeface hintsFont;




    private final static String EXP_PATH = "/Android/obb/";

    private ObbInfo mObbInfo = null;


    private String obbContentPath = null;
    private StorageManager storage;
    private OnObbStateChangeListener mEventListener = null;


    ImageView scanArHelp;


    boolean isMounted=false;

//    Bundle extras;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        UtilityAr.setStausBarColor(ScanArActivity.this,ScanArActivity.this);
        setContentView(R.layout.activity_scanbar);
//        scanToolbar=findViewById(R.id.scan_toolbar);
//        setSupportActionBar(scanToolbar);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        getSupportActionBar().setDisplayShowTitleEnabled(true);
        scanArHelp=findViewById(R.id.hints_help);
        scanArHelp.setOnClickListener(this);
        dialog=new AppCompatDialog(ScanArActivity.this);
        dialog.setContentView(R.layout.camera_hints_page);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        hintsHeader=(dialog).findViewById(R.id.hints_header);
        hintsFont=Typeface.createFromAsset(getAssets(),"fonts/GillSans-SemiBold.ttf");
        hintsHeader.setTypeface(hintsFont);
        hintsList=(dialog).findViewById(R.id.help_list_ar);
        skip=(dialog).findViewById(R.id.help_close);
        skip.setOnClickListener(this);
        layoutManager=new LinearLayoutManager(ScanArActivity.this);
        hintsList.setLayoutManager(layoutManager);
        hints=new ArrayList<>();
        hints.add("1. Move your phone towards QRCode");
        hints.add("2. Hold on until it detects the code");
        hints.add("3. Make sure you scan the right code that belongs to the chosen model, otherwise you will end up with Invalid Code error");
        hints.add("4. If everything successful, application will navigate to the Augmented View");
        if(getIntent().getExtras()!=null)
        {
            extras=getIntent().getExtras();
            isMounted=extras.getBoolean("isMounted");
            obbContentPath=extras.getString("obbPath");
            parentIndex=extras.getInt("parentindex");
            index=extras.getInt("index");
        }

        cameraHintPageAdapter=new CameraHintPageAdapter(ScanArActivity.this,hints);
        hintsList.setAdapter(cameraHintPageAdapter);
        //dialog.show();
//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                dialog.dismiss();
//                ScanArActivity.this.onResume();
//
//            }
//        },7000);

       /* Button button = (Button) findViewById(R.id.button);
        scanResults = (TextView) findViewById(R.id.scan_results);
        if (savedInstanceState != null) {
            imageUri = Uri.parse(savedInstanceState.getString(SAVED_INSTANCE_URI));
            scanResults.setText(savedInstanceState.getString(SAVED_INSTANCE_RESULT));
        }
        *//*button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {*//*

          *//*  }
        });*//*
        detector = new BarcodeDetector.Builder(getApplicationContext())
                .setBarcodeFormats(Barcode.DATA_MATRIX | Barcode.QR_CODE)
                .build();
        if (!detector.isOperational()) {
            scanResults.setText("Could not set up the detector!");
            return;
        }
        Rescan();*/
        ViewGroup contentFrame = (ViewGroup) findViewById(R.id.content_frame);
        mScannerView = new ZXingScannerView(this);
        contentFrame.addView(mScannerView);
        modelPropertiess=new ArrayList<ModelProperties>();

        try {
            JSONArray jsonArray=new JSONArray(loadJSONFromAsset());
            JSONObject jsonObject=jsonArray.getJSONObject(parentIndex);
            JSONArray modelArray=jsonObject.getJSONArray("models");
            JSONObject modelObject=modelArray.getJSONObject(index);
            ModelProperties modelProperties=new ModelProperties();
            modelProperties.setModelid(modelObject.getLong("id"));
            modelProperties.setModelname(modelObject.getString("modelname"));
            modelProperties.setVisiblename(modelObject.getString("visiblename"));
            //getSupportActionBar().setTitle(modelObject.getString("visiblename")+" "+"-"+" "+"Scan");
            modelProperties.setStaticModelPath(modelObject.getString("staticmodelpath"));
            modelProperties.setMtlModelPath(modelObject.getString("mtlmodelpath"));
            modelProperties.setSurfaceEnabled(modelObject.getBoolean("isSurfaceEnabled"));
            modelProperties.setZoomEnabled(modelObject.getBoolean("isZoomEnabled"));
            modelProperties.setPartsAvaliable(modelObject.getBoolean("isPartsAvailable"));
            modelProperties.setPartsName(modelObject.getString("partsname"));
            modelProperties.setPartsModelPath(modelObject.getString("partsmodelpath"));
            modelProperties.setScaleValue((float)modelObject.getDouble("scaleValue"));
            JSONArray relatedObjectsArray=modelObject.getJSONArray("relatedmodels");
            ArrayList<ModelPropertiesChild> modelPropertiesChildren=new ArrayList<>();
            if(relatedObjectsArray.length()>0)
            {
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
                    modelPropertiesChild.setScaleValue((float)relatedObject.getDouble("scaleValue"));
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
            modelPropertiess.add(modelProperties);


        } catch (JSONException e) {
            e.printStackTrace();
        }


    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

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

   /*  @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PHOTO_REQUEST && resultCode == RESULT_OK) {
            launchMediaScanIntent();
            try {
                Bitmap bitmap = decodeBitmapUri(this, imageUri);
                if (detector.isOperational() && bitmap != null) {
                    Frame frame = new Frame.Builder().setBitmap(bitmap).build();
                    SparseArray<Barcode> barcodes = detector.detect(frame);
                    for (int index = 0; index < barcodes.size(); index++) {
                        Barcode code = barcodes.valueAt(index);
                        scanResults.setText(scanResults.getText() + code.displayValue + "\n");

                        //Required only if you need to extract the type of barcode
                        int type = barcodes.valueAt(index).valueFormat;
                        switch (type) {
                            case Barcode.CONTACT_INFO:
                                Log.i(LOG_TAG, code.contactInfo.title);
                                ActivityStart("cannon");
                                break;
                            case Barcode.EMAIL:
                                Log.i(LOG_TAG, code.email.address);
                                ActivityStart("cannon");
                                break;
                            case Barcode.ISBN:
                                Log.i(LOG_TAG, code.rawValue);
                                ActivityStart("viking");
                                break;
                            case Barcode.PHONE:
                                Log.i(LOG_TAG, code.phone.number);
                                ActivityStart("viking");
                                break;
                            case Barcode.PRODUCT:
                                Log.i(LOG_TAG, code.rawValue);
                                ActivityStart("PenDrive");
                                break;
                            case Barcode.SMS:
                                Log.i(LOG_TAG, code.sms.message);
                                ActivityStart("andy");
                                break;
                            case Barcode.TEXT:
                                Log.i(LOG_TAG, code.rawValue);
                                ActivityStart("andy");
                                break;
                            case Barcode.URL:
                                Log.i(LOG_TAG, "url: " + code.url.url);
                                ActivityStart("PenDrive");
                                break;
                            case Barcode.WIFI:
                                Log.i(LOG_TAG, code.wifi.ssid);
                                ActivityStart("PenDrive");
                                break;
                            case Barcode.GEO:
                                Log.i(LOG_TAG, code.geoPoint.lat + ":" + code.geoPoint.lng);
                                ActivityStart("cannon");
                                break;
                            case Barcode.CALENDAR_EVENT:
                                Log.i(LOG_TAG, code.calendarEvent.description);
                                ActivityStart("viking");
                                break;
                            case Barcode.DRIVER_LICENSE:
                                Log.i(LOG_TAG, code.driverLicense.licenseNumber);
                                ActivityStart("andy");
                                break;
                            default:
                                Log.i(LOG_TAG, code.rawValue);
                                break;
                        }
                    }
                    if (barcodes.size() == 0) {
                        scanResults.setText("Scan Failed: Found nothing to scan");
                        Rescan();
                    } else {
                        scanResults.setText("Scan Success: Found scan");
                    }
                } else {
                    scanResults.setText("Could not set up the detector!");
                }
            } catch (Exception e) {
                Toast.makeText(this, "Failed to load Image", Toast.LENGTH_SHORT)
                        .show();
                Log.e(LOG_TAG, e.toString());
            }
        }
    }

    private void Rescan() {
        ActivityCompat.requestPermissions(ScanArActivity.this, new
                String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_WRITE_PERMISSION);
    }

    private void ActivityStart(String value) {
        Intent intent = new Intent(ScanArActivity.this, ARViewActivity.class);
        intent.putExtra("ImageObject", value);
        startActivity(intent);
    }

    private void takePicture() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File photo = new File(Environment.getExternalStorageDirectory(), "picture.jpg");
        imageUri = FileProvider.getUriForFile(ScanArActivity.this,
                BuildConfig.APPLICATION_ID + ".provider", photo);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(intent, PHOTO_REQUEST);
    }

    private void launchMediaScanIntent() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        mediaScanIntent.setData(imageUri);
        this.sendBroadcast(mediaScanIntent);
    }

    private Bitmap decodeBitmapUri(Context ctx, Uri uri) throws FileNotFoundException {
        int targetW = 600;
        int targetH = 600;
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(ctx.getContentResolver().openInputStream(uri), null, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        int scaleFactor = Math.min(photoW / targetW, photoH / targetH);
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;

        return BitmapFactory.decodeStream(ctx.getContentResolver()
                .openInputStream(uri), null, bmOptions);
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        if (imageUri != null) {
            outState.putString(SAVED_INSTANCE_URI, imageUri.toString());
            outState.putString(SAVED_INSTANCE_RESULT, scanResults.getText().toString());
        }
        super.onSaveInstanceState(outState, outPersistentState);
    }*/

    @Override
    protected void onResume() {
        super.onResume();
        if (CameraPermissionHelper.hasCameraPermission(this)) {
            mScannerView.setResultHandler(this);
            mScannerView.startCamera();
        } else {
            CameraPermissionHelper.requestCameraPermission(this);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        mScannerView.stopCamera();
    }

    @Override
    public void handleResult(final Result rawResult) {
        Log.d("GetImageObject",rawResult.getText());
        final Bundle bundle=new Bundle();
        if(modelPropertiess.get(0).getVisiblename().equals(rawResult.getText().toString()))
        {

            bundle.putInt("index",index);
            bundle.putInt("parentindex",parentIndex);
            bundle.putString("ImageObject",rawResult.getText().toString());
            bundle.putBoolean("isMounted",isMounted);
            bundle.putString("obbPath",obbContentPath);
            //Toast.makeText(this, rawResult.getText().toString(), Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(ScanArActivity.this, UnityPlayerActivity.class);
            intent.putExtras(bundle);
            startActivity(intent);
//            AlertDialog.Builder builder=new AlertDialog.Builder(ScanArActivity.this);
//            builder.setTitle("Alert");
//            builder.setMessage("We have found Heart 3D model.shall we proceed loading?");
////            builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
////                @Override
////                public void onClick(DialogInterface dialogInterface, int i) {
////
////                    dialogInterface.dismiss();
////                    bundle.putInt("index",index);
////                    bundle.putInt("parentindex",parentIndex);
////                    bundle.putString("ImageObject",rawResult.getText().toString());
////                    Intent intent = new Intent(ScanArActivity.this, ARViewActivity.class);
////                    intent.putExtras(bundle);
////                    startActivity(intent);
////                }
////            });
//            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialogInterface, int i) {
//                    ActivityCompat.requestPermissions(ScanArActivity.this, new
//                            String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_WRITE_PERMISSION);
//                }
//            });
//            builder.show();

        }
        else
        {
            AlertDialog.Builder builder=new AlertDialog.Builder(ScanArActivity.this);
            builder.setTitle("Alert");
            builder.setMessage("Invalid QRCode");
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                    mScannerView.resumeCameraPreview(ScanArActivity.this);
//                    ActivityCompat.requestPermissions(ScanArActivity.this, new
//                            String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_WRITE_PERMISSION);


                }
            });
            builder.show();
        }

       /* Toast.makeText(this, "Contents = " + rawResult.getText() +
                ", Format = " + rawResult.getBarcodeFormat().toString(), Toast.LENGTH_SHORT).show();

        // Note:
        // * Wait 2 seconds to resume the preview.
        // * On older devices continuously stopping and resuming camera preview can result in freezing the app.
        // * I don't know why this is the case but I don't have the time to figure out.
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mScannerView.resumeCameraPreview(ScanArActivity.this);
            }
        }, 2000);*/
    }

    @Override
    public void onBackPressed() {
        Bundle bundle=new Bundle();
        bundle.putInt("index",parentIndex);
        bundle.putBoolean("isMounted",isMounted);
        bundle.putString("obbPath",obbContentPath);
        Intent goModels=new Intent(ScanArActivity.this,ModelsActivity.class);
        goModels.putExtras(bundle);
        startActivity(goModels);

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
        hintesLabel = new TextView(this);
        hintesLabel.setText("Hints");
        hintesLabel.setTextColor(Color.parseColor("#FFFFFF"));
        hintesLabel.setOnClickListener(ScanArActivity.this);
        hintesLabel.setPadding(5, 0, 30, 0);
        hintesLabel.setTypeface(null, Typeface.BOLD);
        hintesLabel.setTextSize(18);
        //tv.setBackground(ContextCompat.getDrawable(SubscriptionDetailActivity.this,R.drawable.textview_border));
        menu.add(0, 12, 1, "Hints").setActionView(hintesLabel).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {
        if(view==scanArHelp)
        {
            dialog.show();
        }
        if(view==skip)
        {
            dialog.dismiss();
//            ScanArActivity.this.onResume();
        }


    }






}
