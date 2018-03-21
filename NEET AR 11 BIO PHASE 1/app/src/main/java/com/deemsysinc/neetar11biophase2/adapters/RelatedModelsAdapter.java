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
import android.widget.LinearLayout;
import android.widget.TextView;


import com.deemsysinc.neetar11biophase2.R;
import com.deemsysinc.neetar11biophase2.activities.ARViewActivity;
import com.deemsysinc.neetar11biophase2.expansion.ZipResourceFile;
import com.deemsysinc.neetar11biophase2.models.ModelPropertiesChild;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

/**
 * Created by Deemsys on 08-Feb-18.
 */

public class RelatedModelsAdapter extends RecyclerView.Adapter<RelatedModelsAdapter.RelatedModelsHolder> {
    Context context;
    ArrayList<ModelPropertiesChild> modelPropertiesChildren;
    ZipResourceFile expansionFile;
    public RelatedModelsAdapter(Context context, ArrayList<ModelPropertiesChild> modelPropertiesChildren, ZipResourceFile expansionFile)
    {
        this.context=context;
        this.modelPropertiesChildren=modelPropertiesChildren;
        this.expansionFile=expansionFile;
    }
    @Override
    public RelatedModelsHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.related_model_item,parent,false);
        view.setOnClickListener(ARViewActivity.onClickListener);
        RelatedModelsHolder relatedModelsHolder=new RelatedModelsHolder(view);
        return relatedModelsHolder;
    }

    @Override
    public void onBindViewHolder(RelatedModelsHolder holder, int position) {
        holder.relatedModelName.setTypeface(holder.textFont);
        holder.relatedModelName.setText(modelPropertiesChildren.get(position).getVisiblename());
        ZipResourceFile.ZipEntryRO[] entriesarray = expansionFile.getAllEntries();
        for(ZipResourceFile.ZipEntryRO zipEntryRO:entriesarray)
        {
            String theFileName=zipEntryRO.mFileName.substring(40,zipEntryRO.mFileName.length());
            Log.d("ModProbChild",modelPropertiesChildren.get(position).getModelImage());
            if(theFileName.equals(modelPropertiesChildren.get(position).getModelImage()))
            {

                try {
                    InputStream inputStream = expansionFile.getInputStream(zipEntryRO.mFileName);
                    Bitmap modelImageBitmap= BitmapFactory.decodeStream(inputStream);
                    holder.relatedModelImage.setImageBitmap(modelImageBitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }

//        holder.cellLayout.getw

    }

    @Override
    public int getItemCount() {
        return modelPropertiesChildren.size();
    }

    public class RelatedModelsHolder extends RecyclerView.ViewHolder
    {
        TextView relatedModelName;
        ImageView relatedModelImage;
        LinearLayout cellLayout;
        Typeface textFont;
        public RelatedModelsHolder(View itemView) {
            super(itemView);
            cellLayout=(itemView).findViewById(R.id.cell_related_item);
            relatedModelName=(itemView).findViewById(R.id.realated_model_name);
            relatedModelImage=(itemView).findViewById(R.id.related_model_image);
            textFont= Typeface.createFromAsset(context.getAssets(),"fonts/gillsansstd.otf");

        }
    }
}
