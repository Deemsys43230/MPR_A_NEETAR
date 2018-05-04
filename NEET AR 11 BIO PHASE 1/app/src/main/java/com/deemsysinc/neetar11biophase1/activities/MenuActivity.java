package com.deemsysinc.neetar11biophase1.activities;

import android.app.PendingIntent;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.ObbInfo;
import android.content.res.ObbScanner;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Environment;
import android.os.StatFs;
import android.os.storage.OnObbStateChangeListener;
import android.os.storage.StorageManager;
import android.support.annotation.Nullable;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDialog;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;


import com.deemsysinc.neetar11biophase1.R;
import com.deemsysinc.neetar11biophase1.adapters.ChaptersAdapter;
import com.deemsysinc.neetar11biophase1.downloader.DownloadProgressInfo;
import com.deemsysinc.neetar11biophase1.downloader.DownloaderClientMarshaller;
import com.deemsysinc.neetar11biophase1.downloader.Helpers;
import com.deemsysinc.neetar11biophase1.downloader.impl.DownloaderService;
import com.deemsysinc.neetar11biophase1.expansion.MyUtils;
import com.deemsysinc.neetar11biophase1.models.ChapterModel;
import com.deemsysinc.neetar11biophase1.utils.UtilityAr;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

/**
 * Created by Deemsys on 06-Feb-18.
 */

public class MenuActivity extends AppCompatActivity implements View.OnClickListener {
    RecyclerView chapterList;
    LinearLayoutManager layoutManager;
    ArrayList<ChapterModel> chapterModels;
    ChaptersAdapter chaptersAdapter;

    public static View.OnClickListener onClickListener;

    Toolbar toolbar;

    TextView hintesLabel;

    TextView headChapters;


    Typeface hintsFont, toolbarTitle,textFont;

    MyUtils utils = new MyUtils();

    private final static String EXP_PATH = "/Android/obb/";

    private ObbInfo mObbInfo = null;

    private String obbContentPath = null;
    private StorageManager storage;
    private OnObbStateChangeListener mEventListener = null;


    public static AppCompatDialog dialog;

    public static Button TheMount;
    //public static long fsize = 103194684L;

    // With Folder
    //public static long fsize = 214188092L;
    //Without Folder
    public static long fsize = 1244135488L;

    private static TextView textpercent, underdownloadtext;
    private static ProgressBar mProgressBar;


    boolean isMounted=false;

    private static class XAPKFile {
        public final boolean mIsMain;
        public final int mFileVersion;
        public final long mFileSize;

        XAPKFile(boolean isMain, int fileVersion, long fileSize) {
            mIsMain = isMain;
            mFileVersion = fileVersion;
            mFileSize = fileSize;
        }
    }
    private static final MenuActivity.XAPKFile[] xAPKS = {
            new MenuActivity.XAPKFile(
                    true, // true signifies a main file
                    1, // the version of the APK that the file was uploaded against
                    fsize // the length of the file in bytes
            )
    };


     String AppPrefernce = "ScanAR_Ref";

    static  SharedPreferences prefs;

    static  SharedPreferences.Editor editor;






    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        prefs=getSharedPreferences(AppPrefernce, MODE_PRIVATE);
        editor=prefs.edit();
        storage = (StorageManager) getSystemService(Context.STORAGE_SERVICE);
        mEventListener = new OnObbStateChangeListener() {
            @Override
            public void onObbStateChange(String path, int state) {
                if (state == OnObbStateChangeListener.MOUNTED) {
                    Log.d("CalledWho","onObbStateChange");
                    obbContentPath = storage.getMountedObbPath(path);
                    Log.d("Mount_State", "Mounted : Delay ");
                    utils.setObbContentPath(obbContentPath);
                    isMounted=true;

//                    initializePath(context,objpath,mtlPath);
                } else {
                    Log.d("Mount_State", "UnMounted");
                }
            }
        };
        for (XAPKFile xf : xAPKS) {
            String fileName = Helpers.getExpansionAPKFileName(this, xf.mIsMain, xf.mFileVersion);
            Log.d("PrintOnCreateFileName",fileName);
            File file=new File(Helpers.generateSaveFileName(MenuActivity.this,fileName));
            if(file.exists()) {
                MountObb();
            }
        }

        UtilityAr.setStausBarColor(MenuActivity.this,MenuActivity.this);
        toolbar=findViewById(R.id.menu_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setTitle("");
        headChapters=findViewById(R.id.head_chapters);
        toolbarTitle=Typeface.createFromAsset(getAssets(),"fonts/GillSans-SemiBold.ttf");
        hintsFont=Typeface.createFromAsset(getAssets(),"fonts/gillsansstd-bold.otf");
        textFont=Typeface.createFromAsset(getAssets(),"fonts/gillsansstd.otf");
        headChapters.setTypeface(toolbarTitle);
        chapterList=findViewById(R.id.main_menus);
        layoutManager=new LinearLayoutManager(MenuActivity.this);
        chapterList.setLayoutManager(layoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(chapterList.getContext(),
                layoutManager.getOrientation());
        chapterList.addItemDecoration(dividerItemDecoration);
        chapterModels=new ArrayList<ChapterModel>();
        onClickListener=new MyOnclickListner(MenuActivity.this);
        GetChapters();
    }

    private void setStausBarColor() {

    }

    private void GetChapters() {
        try {
            JSONArray jsonArray=new JSONArray(loadJSONFromAsset());
            Log.d("PrintJsonArrayLength",""+jsonArray.length());
            for (int k=0; k<jsonArray.length(); k++)
            {
                JSONObject jsonObject=jsonArray.getJSONObject(k);
                ChapterModel chapterModel=new ChapterModel();
                chapterModel.setChaptername(jsonObject.getString("name"));
                chapterModels.add(chapterModel);
            }
//            ChapterModel chapterModel=new ChapterModel();
//            chapterModel.setPhase1("GET NEET AR 11th Biology PHASE II");
//            chapterModel.setPhase2("GET NEET AR 11th Biology PHASE III");
//            chapterModels.add(chapterModel);
            chaptersAdapter=new ChaptersAdapter(MenuActivity.this,chapterModels);
            chapterList.setAdapter(chaptersAdapter);


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
    public void onClick(View view) {
        if(view==TheMount)
        {
            if(TheMount.getText().toString().equals("Close"))
            {
                dialog.dismiss();
                MountObb();
            }

        }

    }

    class MyOnclickListner implements View.OnClickListener
    {
        Context context;
        MyOnclickListner(Context context)
        {
            this.context=context;
        }
        @Override
        public void onClick(View view) {
            int itemposition=chapterList.getChildLayoutPosition(view);
            Log.d("IsMountedCheck",""+isMounted);
            if(isMounted&&obbContentPath!=null) {
                Bundle bundle = new Bundle();
                bundle.putInt("index", itemposition);
                bundle.putBoolean("isMounted", isMounted);
                bundle.putString("obbPath", obbContentPath);
                Intent goModels = new Intent(MenuActivity.this, ModelsActivity.class);
                goModels.putExtras(bundle);
                startActivity(goModels);
            }
            else
            {

                InitializeObbStreams();
            }

        }
    }

    @Override
    public void onBackPressed() {
        Intent startMain = new Intent(Intent.ACTION_MAIN);
        startMain.addCategory(Intent.CATEGORY_HOME);
        startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(startMain);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.hints_menu,menu);
//        hintesLabel = new TextView(this);
//        hintesLabel.setText("Hints");
//        hintesLabel.setTextColor(Color.parseColor("#FFFFFF"));
//        hintesLabel.setOnClickListener(MenuActivity.this);
//        hintesLabel.setPadding(5, 0, 30, 0);
//        hintesLabel.setTypeface(hintsFont);
//        hintesLabel.setTextSize(18);
//        //tv.setBackground(ContextCompat.getDrawable(SubscriptionDetailActivity.this,R.drawable.textview_border));
//        menu.add(0, 12, 1, "Hints").setActionView(hintesLabel).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.hints:
                Bundle bundle=new Bundle();
                Intent goHint=new Intent(MenuActivity.this,HintsActivity.class);
                bundle.putInt("whereFrom",1);
                goHint.putExtras(bundle);
                startActivity(goHint);
                overridePendingTransition(R.anim.slide_in_up,R.anim.slide_out_up);
                break;
        }

        return super.onOptionsItemSelected(item);
    }
    private void InitializeObbStreams() {
        Log.d("TheDelevered",""+isExpansionFilePartiallyDelivered());

        if ((isExpansionFilePartiallyDelivered()==true)) {
            Log.d("PrintMessage","True");
            if (isConnectingToInternet()) {
                if(prefs.contains("IsObbDownloaded")) {
                    if (prefs.getInt("IsObbDownloaded", 0) == 1) {
                        int downloaded = prefs.getInt("Downloaded", 0);
                        AlertDialog.Builder dialogbuilder = new AlertDialog.Builder(this, android.R.style.Theme_Material_Dialog_Alert)
                                .setTitle("ARDemoProject")
                                .setMessage(downloaded + "%" + " " + "downloaded is over! Shall we resume downloading")
                                .setCancelable(true)
                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        DownloadobbFile();
                                    }
                                })
                                .setNegativeButton("No", null);
                        AlertDialog dialog = dialogbuilder.create();
                        dialog.show();
                    }

                }
            } else {
                AlertDialog.Builder dialogbuilder = new AlertDialog.Builder(this, android.R.style.Theme_Material_Dialog_Alert)
                        .setTitle("ARDemoProject")
                        .setMessage("Please check your internet connection.")
                        .setCancelable(true)
                        .setPositiveButton("Okay", null);
                AlertDialog dialog = dialogbuilder.create();
                dialog.show();
//                Toast.makeText(MenuActivity.this, "Please check your internet connection.", Toast.LENGTH_SHORT).show();
            }
        }
        else if(!expansionFilesDelivered())
        {
            AlertDialog.Builder dialogbuilder = new AlertDialog.Builder(this, android.R.style.Theme_Material_Dialog_Alert)
                    .setTitle("ARDemoProject")
                    .setMessage("3D Models are not available! Shall we download from the server?")
                    .setCancelable(true)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            DownloadobbFile();
                        }
                    })
                    .setNegativeButton("No", null);
            AlertDialog dialog = dialogbuilder.create();
            dialog.show();
        }
        else
        {
            MountObb();

        }
//        else {
//            File root = Environment.getExternalStorageDirectory();
//            String path = root.getPath();
//            Log.d("PrintRoot", "" + root);
//            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
//                String zipkey = "mobileteam";
//                String expandfilepath = root + EXP_PATH + getPackageName() + "/main.2." + getPackageName() + ".obb";
//                Log.d("ExpandFilePath", expandfilepath + "," + root.getPath());
//                File expPath = new File(expandfilepath);
//                if (expPath.exists()) {
//                    try {
//                        mObbInfo = ObbScanner.getObbInfo(expandfilepath);
//                        if (mObbInfo != null) {
//                            Log.d("OBB_FileState", "File Name : " + mObbInfo.filename + " Package Name: " + mObbInfo.packageName);
//                        }
//                    } catch (IllegalArgumentException e) {
//                        Log.d("OBB_FileState", "The OBB file could not be found!");
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                        Log.d("OBB_FileState", "The OBB file could not be read!");
//                    }
//                    if (storage != null) {
//                        Log.d("ExpPath", "" + expPath.getAbsolutePath());
//                        try {
//                            storage.mountObb(expPath.getAbsolutePath(), zipkey, mEventListener);
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                            Log.d("NotMountExcep", e.toString());
//                        }
//                        boolean connected = storage.isObbMounted(expPath.getAbsolutePath());
//                        if (!connected) {
//                            Log.d("Mount_State", "Unmounted");
//                        } else {
//                            obbContentPath = storage.getMountedObbPath(expPath.getAbsolutePath());
//                            utils.setObbContentPath(obbContentPath);
//                            isMounted = true;
//                        }
//                    }
//                } else {
//                    Log.d("ExpandFilePath", "" + "Not Exists");
////                    Toast.makeText(this, "Not Exists", Toast.LENGTH_SHORT).show();
//                }
//            }
//        }
    }

    private void MountObb() {
        File root = Environment.getExternalStorageDirectory();
        String path = root.getPath();
        Log.d("PrintRoot", "" + root);
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            String zipkey = "mobileteam";
            String expandfilepath = root + EXP_PATH + getPackageName() + "/main.1." + getPackageName() + ".obb";
            Log.d("ExpandFilePath", expandfilepath + "," + root.getPath());
            File expPath = new File(expandfilepath);
            if (expPath.exists()) {
                try {
                    mObbInfo = ObbScanner.getObbInfo(expandfilepath);
                    if (mObbInfo != null) {
                        Log.d("OBB_FileState", "File Name : " + mObbInfo.filename + " Package Name: " + mObbInfo.packageName);
                    }
                } catch (IllegalArgumentException e) {
                    Log.d("OBB_FileState", "The OBB file could not be found!");
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.d("OBB_FileState", "The OBB file could not be read!");
                }
                if (storage != null) {
                    Log.d("ExpPath", "" + expPath.getAbsolutePath());
                    try {
                        storage.mountObb(expPath.getAbsolutePath(), zipkey, mEventListener);
                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.d("NotMountExcep", e.toString());
                    }
                    boolean connected = storage.isObbMounted(expPath.getAbsolutePath());
                    if (!connected) {
                        Log.d("Mount_State", "Unmounted");
                    } else {
                        obbContentPath = storage.getMountedObbPath(expPath.getAbsolutePath());
                        utils.setObbContentPath(obbContentPath);
                        isMounted = true;
                    }
                }
            } else {
                Log.d("ExpandFilePath", "" + "Not Exists");
//                    Toast.makeText(this, "Not Exists", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public boolean isConnectingToInternet() {
        ConnectivityManager connectivity = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo info = connectivity.getActiveNetworkInfo();
            if (info != null) {
                if (info.getState() == NetworkInfo.State.CONNECTED) {
                    return true;
                }
            }
        }
        return false;
    }
    boolean expansionFilesDelivered() {
        for (XAPKFile xf : xAPKS) {
            String fileName = Helpers.getExpansionAPKFileName(this, xf.mIsMain, xf.mFileVersion);
            Log.d("Filename", fileName);
            if (!Helpers.doesFileExist(this, fileName, xf.mFileSize, false)) {
                return false;
            }

        }
        return true;
    }
    boolean isExpansionFilePartiallyDelivered() {
        for (XAPKFile xf : xAPKS) {
            String fileName = Helpers.getPartialName(this, xf.mIsMain, xf.mFileVersion);
            File file=new File(Helpers.generateSaveFileName(MenuActivity.this,fileName));
            Log.d("PrinttFileNAme",""+file);
            if(!file.exists()) {
                return false;
            }
        }
        return true;
    }
    private void DownloadobbFile() {
        try {
            Intent launchIntent = MenuActivity.this.getIntent();
            Intent intentToLaunchThisActivityFromNotification = new Intent(MenuActivity.this, MenuActivity.this.getClass());
            intentToLaunchThisActivityFromNotification.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intentToLaunchThisActivityFromNotification.setAction(launchIntent.getAction());
            if (launchIntent.getCategories() != null) {
                for (String category : launchIntent.getCategories()) {
                    intentToLaunchThisActivityFromNotification.addCategory(category);
                }
            }
            PendingIntent pendingIntent = PendingIntent.getActivity(MenuActivity.this, 0, intentToLaunchThisActivityFromNotification, PendingIntent.FLAG_UPDATE_CURRENT);
            // Request to start the download
            int startResult = DownloaderService.startDownloadServiceIfRequired(this, pendingIntent, DownloaderService.class);
            if (startResult != DownloaderClientMarshaller.NO_DOWNLOAD_REQUIRED) {
                StatFs stat = new StatFs(Environment.getExternalStorageDirectory().getPath());
                long bytesAvailable = stat.getBlockSizeLong() * stat.getAvailableBlocksLong();
                long megAvailable = bytesAvailable / (1024 * 1024);
                Log.e("Memory_External", "Available MB : " + megAvailable);
                if (bytesAvailable > fsize) {
//                            mDownloaderClientStub = DownloaderClientMarshaller.CreateStub(this, DownloaderService.class);
                    dialog = new AppCompatDialog(this);
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    dialog.setCancelable(false);
                    dialog.setContentView(R.layout.progressdialog);
                    Typeface tf = ResourcesCompat.getFont(this, R.font.bodini_old);
                    TheMount=(dialog).findViewById(R.id.dialog_mount_button);
                    TheMount.setOnClickListener(this);
                    TheMount.setTypeface(textFont);
                    textpercent = (TextView) dialog.findViewById(R.id.textpercent);
                    underdownloadtext = (TextView) dialog.findViewById(R.id.underdownloadtext);
                    textpercent.setTypeface(tf, Typeface.NORMAL);
                    underdownloadtext.setTypeface(tf, Typeface.NORMAL);
                    mProgressBar = dialog.findViewById(R.id.downloaddialog);
                    Drawable customDrawable = getResources().getDrawable(R.drawable.custom_progress);
                    mProgressBar.setProgressDrawable(customDrawable);
                    dialog.show();
                    ComponentName mServiceComponent = new ComponentName(this, DownloaderService.class);
                    JobInfo.Builder builder = new JobInfo.Builder(1008, mServiceComponent);
                    builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY);
                    JobScheduler tm = (JobScheduler) getSystemService(Context.JOB_SCHEDULER_SERVICE);
                    tm.schedule(builder.build());

                } else {
                    AlertDialog.Builder dialogbuilder = new AlertDialog.Builder(this, android.R.style.Theme_Material_Dialog_Alert)
                            .setTitle("ARDemoProject")
                            .setMessage("Insufficient Storage.")
                            .setCancelable(true)
                            .setPositiveButton("Okay", null);
                    AlertDialog dialog = dialogbuilder.create();
                    dialog.show();
//                    Toast.makeText(this, "Insufficient Storage.", Toast.LENGTH_SHORT).show();
                }
            } else {

            }
        } catch (PackageManager.NameNotFoundException e) {
            Log.e("Test", "Cannot find package!", e);
//            Toast.makeText(MenuActivity.this, "Cannot find package!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(dialog!=null)
        {
            if(dialog.isShowing())
            {
                dialog.dismiss();
            }
        }
    }
    public static  void setDownloadProgress(DownloadProgressInfo progress) {
        float percents = (float) ((progress.mOverallProgress * 100) / progress.mOverallTotal);
        Log.d("PrintProgress",""+progress.mOverallProgress+" ,"+percents+" ,"+progress.mOverallTotal);
        Log.d("PrintPercents",""+percents);
        if (percents < 99) {
            Log.d("Values", "percent" + percents + "   Over all Process" + progress.mOverallProgress + " Over all Total" + progress.mOverallTotal);
            mProgressBar.setProgress((int) percents);
            textpercent.setText((int) percents + "%");
            editor.putInt("IsObbDownloaded",1);
            editor.putInt("Downloaded",(int)percents);
            editor.commit();

        } else {
//            try {
                mProgressBar.setProgress((int) percents);
                textpercent.setText("" + 100 + "%");
                //Thread.sleep(15000);
                 TheMount.setText("Close");
                //dialog.dismiss();
                editor.putInt("IsObbDownloaded",2);
                editor.commit();

//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
        }
    }
}
