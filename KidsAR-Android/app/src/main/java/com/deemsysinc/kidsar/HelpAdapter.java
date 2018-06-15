package com.deemsysinc.kidsar;

import android.content.Context;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class HelpAdapter extends RecyclerView.Adapter<HelpAdapter.MyHelpHolder> {
    private List<HelpModel> modelList;
    private Context context;

    public HelpAdapter(Context context, List<HelpModel> modelList) {
        this.context = context;
        this.modelList = modelList;
    }

    @NonNull
    @Override
    public MyHelpHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View kidsview = LayoutInflater.from(context).inflate(R.layout.helpmodel_item, parent, false);
        return new MyHelpHolder(kidsview, context, modelList);
    }

    @Override
    public void onBindViewHolder(MyHelpHolder holder, final int position) {
        holder.helptext.setText(modelList.get(position).name);
        if (modelList.get(position).isIcon()) {
            holder.kidmodel_image.setVisibility(View.VISIBLE);
        } else {
            holder.kidmodel_image.setVisibility(View.GONE);
        }
        if (modelList.get(position).isBold()) {
            if ((position + 1) == modelList.size()) {
                Typeface face = ResourcesCompat.getFont(context, R.font.trebuchet_ms_bold_italic);
                holder.helptext.setTypeface(face);
                holder.helptext.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
            } else {
                Typeface face = ResourcesCompat.getFont(context, R.font.gothamrounded_medium);
                holder.helptext.setTypeface(face);
                holder.helptext.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
            }
        }
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return modelList.size();
    }

    public class MyHelpHolder extends RecyclerView.ViewHolder {
        private Context context1;
        private List<HelpModel> helpmodelList;
        TextView helptext, kidmodel_image;


        public MyHelpHolder(View itemView, Context context, List<HelpModel> modelList) {
            super(itemView);
            this.context1 = context;
            this.helpmodelList = modelList;
            kidmodel_image = itemView.findViewById(R.id.kidmodel_image);
            helptext = itemView.findViewById(R.id.helptext);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getAdapterPosition();
                }
            });
        }
    }
}
