package com.deemsysinc.neetar11biophase1.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.deemsysinc.neetar11biophase1.R;
import com.deemsysinc.neetar11biophase1.activities.MenuActivity;
import com.deemsysinc.neetar11biophase1.models.ChapterModel;

import java.util.ArrayList;

/**
 * Created by Deemsys on 06-Feb-18.
 */

public class ChaptersAdapter extends RecyclerView.Adapter<ChaptersAdapter.ChapterHolder>  {
    Context context;
    ArrayList<ChapterModel> chapterModels;
    public ChaptersAdapter(Context context, ArrayList<ChapterModel> chapterModels)
    {
        this.context=context;
        this.chapterModels=chapterModels;
    }
    @Override
    public ChapterHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.chapter_items,parent,false);
        view.setOnClickListener(MenuActivity.onClickListener);
        ChapterHolder chapterHolder=new ChapterHolder(view);
        return chapterHolder;
    }

    @Override
    public void onBindViewHolder(ChapterHolder holder, int position) {
        holder.chapterName.setTypeface(holder.textFont);
        holder.chapterName.setText(chapterModels.get(position).getChaptername());
        holder.navigationIcon.setTypeface(holder.font);
        if(position+1==chapterModels.size())
        {
            holder.containerPlay.setVisibility(View.VISIBLE);
            holder.playOne.setTypeface(holder.textFont1);
            holder.playTwo.setTypeface(holder.textFont1);
            holder.playNavigation1.setTypeface(holder.font);
            holder.playNavigation2.setTypeface(holder.font);

            holder.playoneClick.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String url = "https://play.google.com/store/apps/details?id=com.deemsysinc.neetar11biophase2";
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(url));
                    context.startActivity(i);

                }
            });
            holder.playTwoClick.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String url = "https://play.google.com/store/apps/details?id=com.deemsysinc.neetar11biophase3";
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(url));
                    context.startActivity(i);
                }
            });

        }
        Log.d("PrintPos",""+position);

    }

    @Override
    public int getItemCount() {
        return chapterModels.size();
    }

    class ChapterHolder extends RecyclerView.ViewHolder
    {
        TextView chapterName,navigationIcon,playOne,playTwo,playNavigation1,playNavigation2;

        Typeface font,textFont,textFont1;

        LinearLayout containerPlay;

        RelativeLayout playoneClick,playTwoClick;



        public ChapterHolder(View itemView) {
            super(itemView);
            chapterName=(itemView).findViewById(R.id.chapter_name);
            navigationIcon=(itemView).findViewById(R.id.chapter_navigation);
            containerPlay=(itemView).findViewById(R.id.container_play);
            playOne=(itemView).findViewById(R.id.play_one);
            playTwo=(itemView).findViewById(R.id.play_two);
            playNavigation1=(itemView).findViewById(R.id.play_navigation);
            playNavigation2=(itemView).findViewById(R.id.play_navigation_two);
            playoneClick=(itemView).findViewById(R.id.playone_click);
            playTwoClick=(itemView).findViewById(R.id.playtwo_click);
            font=Typeface.createFromAsset(context.getAssets(),"fontawesome-webfont.ttf");
            textFont=Typeface.createFromAsset(context.getAssets(),"fonts/gillsansstd.otf");
            textFont1=Typeface.createFromAsset(context.getAssets(),"fonts/gillsansstd-bold.otf");
        }
    }
}
