package com.deemsysinc.neetar11biophase;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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

    }

    @Override
    public int getItemCount() {
        return chapterModels.size();
    }

    class ChapterHolder extends RecyclerView.ViewHolder
    {
        TextView chapterName,navigationIcon;

        Typeface font,textFont;



        public ChapterHolder(View itemView) {
            super(itemView);
            chapterName=(itemView).findViewById(R.id.chapter_name);
            navigationIcon=(itemView).findViewById(R.id.chapter_navigation);
            font=Typeface.createFromAsset(context.getAssets(),"fontawesome-webfont.ttf");
            textFont=Typeface.createFromAsset(context.getAssets(),"fonts/gillsansstd.otf");
        }
    }
}
