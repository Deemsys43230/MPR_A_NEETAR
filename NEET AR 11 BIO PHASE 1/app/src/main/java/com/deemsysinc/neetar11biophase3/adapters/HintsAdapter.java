package com.deemsysinc.neetar11biophase3.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.SpannableString;
import android.text.style.RelativeSizeSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.deemsysinc.neetar11biophase3.R;

import java.util.ArrayList;

/**
 * Created by Deemsys on 08-Feb-18.
 */

public class HintsAdapter extends RecyclerView.Adapter<HintsAdapter.HintsHolder> {
    Context context;
    ArrayList<String> hints;
    public HintsAdapter(Context context, ArrayList<String> hints)
    {
        this.context=context;
        this.hints=hints;

    }
    @Override
    public HintsHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.hints_item,parent,false);
        HintsHolder hintsHolder=new HintsHolder(view);
        return hintsHolder;
    }

    @Override
    public void onBindViewHolder(HintsHolder holder, int position) {
        holder.labelHint.setTypeface(holder.textFont);
        if(position==6)
        {
            SpannableString ss1=  new SpannableString(Html.fromHtml(hints.get(position)));

            ss1.setSpan(new RelativeSizeSpan(2f), 6,7, 0); // set size
            holder.labelHint.setText(ss1);
        }
        else {
            holder.labelHint.setText(Html.fromHtml(hints.get(position)));
        }

    }

    @Override
    public int getItemCount() {
        return hints.size();
    }

    class HintsHolder extends RecyclerView.ViewHolder
    {
        TextView labelHint;
        Typeface textFont;
        public HintsHolder(View itemView) {
            super(itemView);
            labelHint=(itemView).findViewById(R.id.hints_text);
            textFont=Typeface.createFromAsset(context.getAssets(),"fonts/gillsansstd-light.otf");
        }
    }
}
