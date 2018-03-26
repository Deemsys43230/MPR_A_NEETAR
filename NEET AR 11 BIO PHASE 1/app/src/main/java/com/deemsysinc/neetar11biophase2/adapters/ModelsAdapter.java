package com.deemsysinc.neetar11biophase2.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import com.deemsysinc.neetar11biophase2.R;
import com.deemsysinc.neetar11biophase2.activities.ModelsActivity;
import com.deemsysinc.neetar11biophase2.expansion.ZipResourceFile;
import com.deemsysinc.neetar11biophase2.models.ObjModelss;


import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

/**
 * Created by Deemsys on 06-Feb-18.
 */

public class ModelsAdapter extends RecyclerView.Adapter<ModelsAdapter.ModelsHolder> {
    Context context;
    ArrayList<ObjModelss> objModelsses;
     ZipResourceFile expansionFile = null;

    public ModelsAdapter(Context context, ArrayList<ObjModelss> objModelsses, ZipResourceFile expansionFile)
    {
        this.context=context;
        this.objModelsses=objModelsses;
        this.expansionFile=expansionFile;
    }
    @Override
    public ModelsHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.modellistitem,parent,false);
        view.setOnClickListener(ModelsActivity.onClickListener);
        ModelsHolder modelsHolder=new ModelsHolder(view);
        return modelsHolder;
    }

    @Override
    public void onBindViewHolder(ModelsHolder holder, int position) {
        holder.modelName.setTypeface(holder.textFont);
        if(objModelsses.get(position).getModelName().equals("Muscular tissue"))
        {
            holder.modelName.setText("Skeletal muscle");
        }
        else if(objModelsses.get(position).getModelName().equals("Epithelial tissue"))
        {
            holder.modelName.setText("Simple squamous");
        }
        else {
            holder.modelName.setText(objModelsses.get(position).getModelName());
        }
        holder.modelNavigation.setTypeface(holder.font);
         ZipResourceFile.ZipEntryRO[] entriesarray = expansionFile.getAllEntries();
        for(ZipResourceFile.ZipEntryRO zipEntryRO:entriesarray)
        {
            String theFileName=zipEntryRO.mFileName.substring(40,zipEntryRO.mFileName.length());
            Log.d("PrintTheNew",theFileName);
            if(theFileName.equals(objModelsses.get(position).getImagemodel()))
            {
                Log.d("TheModelIma",theFileName);
                try {
                    InputStream inputStream=expansionFile.getInputStream(zipEntryRO.mFileName);
                    Bitmap modelImageBitmap= BitmapFactory.decodeStream(inputStream);
                    holder.modelsImage.setImageBitmap(modelImageBitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public int getItemCount() {
        return objModelsses.size();
    }

    class ModelsHolder extends RecyclerView.ViewHolder
    {
        TextView modelName,modelNavigation;

        ImageView modelsImage;

        Typeface font,textFont;

        public ModelsHolder(View itemView) {
            super(itemView);
            modelName=(itemView).findViewById(R.id.model_name);
            modelNavigation=(itemView).findViewById(R.id.model_navigation);
            modelsImage=(itemView).findViewById(R.id.models_image);
            font=Typeface.createFromAsset(context.getAssets(),"fontawesome-webfont.ttf");
            textFont=Typeface.createFromAsset(context.getAssets(),"fonts/gillsansstd.otf");
        }
    }
}
