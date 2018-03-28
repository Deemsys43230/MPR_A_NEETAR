package com.deemsysinc.neetar11biophase3.activities;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.text.style.RelativeSizeSpan;
import android.view.View;
import android.widget.TextView;


import com.deemsysinc.neetar11biophase3.R;
import com.deemsysinc.neetar11biophase3.adapters.HintsAdapter;
import com.deemsysinc.neetar11biophase3.utils.UtilityAr;

import java.util.ArrayList;

/**
 * Created by Deemsys on 08-Feb-18.
 */

public class HintsActivity extends AppCompatActivity {
    RecyclerView hintsList;
    Toolbar toolbar;
    LinearLayoutManager layoutManager;
    ArrayList<String> hints;
    HintsAdapter hintsAdapter;
    Bundle extras;
    int index=0;
    String seventh="";

    int whereFrom;

    Typeface toolbarFont;

    TextView hintsHeader;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hints);
        UtilityAr.setStausBarColor(HintsActivity.this,HintsActivity.this);
        toolbar=findViewById(R.id.hints_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setTitle("");
        hintsHeader=findViewById(R.id.head_hints);
        toolbarFont= Typeface.createFromAsset(getAssets(),"fonts/GillSans-SemiBold.ttf");
        hintsHeader.setTypeface(toolbarFont);
        if(getIntent().getExtras()!=null)
        {
            extras=getIntent().getExtras();
            index=extras.getInt("index");
            whereFrom=extras.getInt("whereFrom");
        }
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(whereFrom==1)
                {
                    Intent goChapters=new Intent(HintsActivity.this,MenuActivity.class);
                    startActivity(goChapters);
                    overridePendingTransition(R.anim.slide_in_down,R.anim.slide_out_dwon);
                }
                else if(whereFrom==2)
                {
                    finish();
                    overridePendingTransition(R.anim.slide_in_down,R.anim.slide_out_dwon);
                }
//                Bundle bundle=new Bundle();
//                bundle.putInt("index",index);
//                Intent goModels=new Intent(HintsActivity.this,ModelsActivity.class);
//                goModels.putExtras(bundle);
//                startActivity(goModels);
//                overridePendingTransition(R.anim.slide_in_down,R.anim.slide_out_dwon);


            }
        });
        hintsList=findViewById(R.id.hints_list);
        layoutManager=new LinearLayoutManager(this);
        hintsList.setLayoutManager(layoutManager);
        seventh="Use &#x21bb to clear everything";
        SpannableString ss1=  new SpannableString(seventh);
        ss1.setSpan(new RelativeSizeSpan(1f), 6,10, 0); // set size
        if(whereFrom==1)
        {
            hints=new ArrayList<>();
            hints.add("1. Select a chapter from the list");
            hints.add("2. You will navigate to the models list. Choose the model to augment");
            hints.add("3. Wait until application loads camera view. Scan QR code of an object");
            hints.add("4. Augment 3d model using camera");
            hints.add("5. Use ⚙️ button to change 3D model settings");
            hints.add("6. Use ↻ button to clear models");
            hints.add("7. Use \uD83C\uDFE0 button to go to Chapters");
            hints.add("8. For instructions use help button");
        }
        else if(whereFrom==2)
        {
            hints=new ArrayList<>();
            hints.add("1. Set Place on air to ON to display object on Air");
            hints.add("2. Set Place on air to OFF to display object on Plane");
            hints.add("3. Use Scale to set object's zoom size");
            hints.add("4. Use Zoom option to Enable/Disable zoom of object");
        }
        hintsAdapter=new HintsAdapter(this,hints);
        hintsList.setAdapter(hintsAdapter);

    }

    @Override
    public void onBackPressed() {
        if(whereFrom==1)
        {
            Intent goChapters=new Intent(HintsActivity.this,MenuActivity.class);
            startActivity(goChapters);
            overridePendingTransition(R.anim.slide_in_down,R.anim.slide_out_dwon);
        }
        else if(whereFrom==2)
        {
            finish();
            overridePendingTransition(R.anim.slide_in_down,R.anim.slide_out_dwon);
        }
//                Bundle bundle=new Bundle();
//                bundle.putInt("index",index);
//                Intent goModels=new Intent(HintsActivity.this,ModelsActivity.class);
//                goModels.putExtras(bundle);
//                startActivity(goModels);
//                overridePendingTransition(R.anim.slide_in_down,R.anim.slide_out_dwon);
    }
}
