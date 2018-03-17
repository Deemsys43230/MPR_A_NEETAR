package com.deemsysinc.neetar11biophase1.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;


import com.deemsysinc.neetar11biophase1.R;
import com.deemsysinc.neetar11biophase1.adapters.ModelsAdapter;
import com.deemsysinc.neetar11biophase1.expansion.APKExpansionSupport;
import com.deemsysinc.neetar11biophase1.expansion.ZipResourceFile;
import com.deemsysinc.neetar11biophase1.models.ObjModelss;
import com.deemsysinc.neetar11biophase1.utils.UtilityAr;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

/**
 * Created by Deemsys on 06-Feb-18.
 */

public class ModelsActivity extends AppCompatActivity implements View.OnClickListener {
    RecyclerView modelsList;
    LinearLayoutManager layoutManager;
    Bundle extras;
    int arrayIndex;
    ArrayList<ObjModelss> objModelsses;

    ModelsAdapter modelsAdapter;

    public static View.OnClickListener onClickListener;

    Toolbar toolbar;

    TextView labelHints;

    Typeface textFont,toolbarTitle;

    TextView headModel;

    String obbContentPath=null;

    boolean isMounted=false;

    private ZipResourceFile expansionFile = null;


    int parentIndex=0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        UtilityAr.setStausBarColor(ModelsActivity.this,ModelsActivity.this);
        setContentView(R.layout.activity_models);
        if(getIntent().getExtras()!=null)
        {
            extras=getIntent().getExtras();
            arrayIndex=extras.getInt("index");
            isMounted=extras.getBoolean("isMounted");
            obbContentPath=extras.getString("obbPath");
        }
        toolbar=findViewById(R.id.model_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle("");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent goBack=new Intent(ModelsActivity.this,MenuActivity.class);
                startActivity(goBack);
            }
        });
        headModel=findViewById(R.id.head_models);
        textFont=Typeface.createFromAsset(getAssets(),"fonts/gillsansstd-bold.otf");
        toolbarTitle=Typeface.createFromAsset(getAssets(),"fonts/GillSans-SemiBold.ttf");
        headModel.setTypeface(toolbarTitle);
        modelsList=findViewById(R.id.models_list);
        layoutManager=new LinearLayoutManager(ModelsActivity.this);
        modelsList.setLayoutManager(layoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(modelsList.getContext(),
                layoutManager.getOrientation());
        modelsList.addItemDecoration(dividerItemDecoration);
        onClickListener=new MyClickListner(ModelsActivity.this);
//        labelHints=findViewById(R.id.label_hints);
//        labelHints.setOnClickListener(this);
        objModelsses=new ArrayList<ObjModelss>();
        GetModels(arrayIndex);

    }

    private void GetModels(int arrayIndex) {
        try {
            JSONArray jsonArray=new JSONArray(loadJSONFromAsset());
            JSONObject ParentObject=jsonArray.getJSONObject(arrayIndex);
            JSONArray modelsArray=ParentObject.getJSONArray("models");
            Log.d("ModelArrLen",""+modelsArray.length());
            for(int k=0; k<modelsArray.length(); k++)
            {
                JSONObject jsonObject=modelsArray.getJSONObject(k);
                ObjModelss objModelss=new ObjModelss();
                objModelss.setModelId(jsonObject.getLong("id"));
                objModelss.setModelName(jsonObject.getString("visiblename"));
                objModelss.setImagemodel(jsonObject.getString("modelimage"));
                objModelsses.add(objModelss);
            }
            try {
                expansionFile = APKExpansionSupport.getAPKExpansionZipFile(ModelsActivity.this, 1, 0, obbContentPath);
                Log.d("PrintExpansionFile",""+expansionFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
            modelsAdapter=new ModelsAdapter(ModelsActivity.this,objModelsses,expansionFile);
            modelsList.setAdapter(modelsAdapter);


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
//        if(view==labelHints)
//        {
//            Bundle bundle=new Bundle();
//            Intent goHint=new Intent(ModelsActivity.this,HintsActivity.class);
//            bundle.putInt("index",arrayIndex);
//            goHint.putExtras(bundle);
//            startActivity(goHint);
//            overridePendingTransition(R.anim.slide_in_up,R.anim.slide_out_up);
//        }
    }

    class MyClickListner implements View.OnClickListener
    {
        Context context;
        MyClickListner(Context context){
            this.context=context;
        }
        @Override
        public void onClick(View view) {
            int itemposition=modelsList.getChildLayoutPosition(view);
            Bundle bundle=new Bundle();
            bundle.putInt("parentindex",arrayIndex);
            bundle.putInt("index",itemposition);
            bundle.putBoolean("isMounted",isMounted);
            bundle.putString("obbPath",obbContentPath);
            Intent goQr=new Intent(ModelsActivity.this,ScanArActivity.class);
            goQr.putExtras(bundle);
            startActivity(goQr);


        }
    }

    @Override
    public void onBackPressed() {
        Intent goBack=new Intent(ModelsActivity.this,MenuActivity.class);
        startActivity(goBack);
    }
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
////        getMenuInflater().inflate(R.menu.hints_menu,menu);
//            labelHints = new TextView(this);
//        labelHints.setText("Hints");
//        labelHints.setTextColor(Color.parseColor("#FFFFFF"));
//        labelHints.setOnClickListener(ModelsActivity.this);
//        labelHints.setPadding(5, 0, 30, 0);
//        labelHints.setTypeface(textFont);
//        labelHints.setTextSize(18);
//            //tv.setBackground(ContextCompat.getDrawable(SubscriptionDetailActivity.this,R.drawable.textview_border));
//            menu.add(0, 12, 1, "Hints").setActionView(labelHints).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
////        switch (item.getItemId())
////        {
////            case R.id.hints:
////                Bundle bundle=new Bundle();
////                Intent goHint=new Intent(ModelsActivity.this,HintsActivity.class);
////                bundle.putInt("index",arrayIndex);
////                goHint.putExtras(bundle);
////                startActivity(goHint);
////                overridePendingTransition(R.anim.slide_in_up,R.anim.slide_out_up);
////                break;
////        }
//        return super.onOptionsItemSelected(item);
//    }
}
