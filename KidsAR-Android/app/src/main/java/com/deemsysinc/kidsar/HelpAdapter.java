package com.deemsysinc.kidsar;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.VideoView;

import java.util.List;
import java.util.logging.Handler;

public class HelpAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<HelpModel> modelList;
    private Context context;
    Activity activity;

    public HelpAdapter(Context context, Activity activity, List<HelpModel> modelList) {
        this.context = context;
        this.modelList = modelList;
        this.activity = activity;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == 1) {
            View kidsview = LayoutInflater.from(context).inflate(R.layout.helpmodel_item_video, parent, false);
            return new MyHelpVideoHolder(kidsview);
        } else {
            View kidsview = LayoutInflater.from(context).inflate(R.layout.helpmodel_item, parent, false);
            return new MyHelpHolder(kidsview);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder.getItemViewType() != 1) {
            MyHelpHolder textholder = (MyHelpHolder) holder;
            textholder.helptext.setText(modelList.get(position).name);
            if (modelList.get(position).isIcon()) {
                textholder.kidmodel_image.setVisibility(View.VISIBLE);
            } else {
                textholder.kidmodel_image.setVisibility(View.GONE);
            }
            if (modelList.get(position).isBold()) {
                if ((position + 1) == modelList.size()) {
                    Typeface face = ResourcesCompat.getFont(context, R.font.trebuchet_ms_bold_italic);
                    textholder.helptext.setPadding(0, 10, 0, 0);
                    textholder.helptext.setTypeface(face);
                    textholder.helptext.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
                } else {
                    Typeface face = ResourcesCompat.getFont(context, R.font.gothamrounded_medium);
                    textholder.helptext.setTypeface(face);
                    textholder.helptext.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
                }
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

    class MyHelpHolder extends RecyclerView.ViewHolder {
        TextView helptext, kidmodel_image;

        public MyHelpHolder(View itemView) {
            super(itemView);
            kidmodel_image = itemView.findViewById(R.id.kidmodel_image);
            helptext = itemView.findViewById(R.id.helptext);
        }
    }

    public class MyHelpVideoHolder extends RecyclerView.ViewHolder {

        public MyHelpVideoHolder(View kidsview) {
            super(kidsview);
            kidsview.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    /*activity.videoItemClickInitialize(itemView);*/
//                    activity.videoItemClick(itemView);
                    context.startActivity(new Intent(context, VideoPlayerActivity.class));
                    activity.overridePendingTransition(R.anim.zoom_in, R.anim.zoom_out);
                   /* Uri uri = Uri.parse("android.resource://" + context.getPackageName() + "/" + R.raw.video);
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_VIEW);
                    intent.setDataAndType(uri, "video/*");
                    context.startActivity(Intent.createChooser(intent,"Play using"));*/
                }
            });
        }
    }
}
