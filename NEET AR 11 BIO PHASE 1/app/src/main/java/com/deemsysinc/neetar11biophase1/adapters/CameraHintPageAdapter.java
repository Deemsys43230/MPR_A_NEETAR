package com.deemsysinc.neetar11biophase1.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.deemsysinc.neetar11biophase1.R;

import java.util.ArrayList;

/**
 * Created by Deemsys on 12-Feb-18.
 */

public class CameraHintPageAdapter extends RecyclerView.Adapter<CameraHintPageAdapter.CameraHintsPageHolder>  {
    Context context;
    ArrayList<String> cameraHintsList;
    public CameraHintPageAdapter(Context context, ArrayList<String> cameraHintsList)
    {
        this.context=context;
        this.cameraHintsList=cameraHintsList;
    }
    @Override
    public CameraHintsPageHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.camera_hints_item,parent,false);
        CameraHintsPageHolder cameraHintsPageHolder=new CameraHintsPageHolder(view);
        return cameraHintsPageHolder;
    }

    @Override
    public void onBindViewHolder(CameraHintsPageHolder holder, int position) {
        holder.hintsText.setTypeface(holder.textFont);
        holder.hintsText.setText(cameraHintsList.get(position));

    }

    @Override
    public int getItemCount() {
        return cameraHintsList.size();
    }

    class CameraHintsPageHolder extends RecyclerView.ViewHolder
    {
        TextView hintsText;

        Typeface textFont;

        public CameraHintsPageHolder(View itemView) {
            super(itemView);
            hintsText=(itemView).findViewById(R.id.camera_hints_text);
            textFont=Typeface.createFromAsset(context.getAssets(),"fonts/gillsansstd-light.otf");
        }
    }
}
