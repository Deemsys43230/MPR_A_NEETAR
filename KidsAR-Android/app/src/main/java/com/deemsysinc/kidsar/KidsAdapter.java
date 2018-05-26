package com.deemsysinc.kidsar;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class KidsAdapter extends RecyclerView.Adapter<KidsAdapter.MyKidsHolder> {
    private List<KidsModel> modelList;
    private Context context;

    public KidsAdapter(Context context, List<KidsModel> modelList) {
        this.context = context;
        this.modelList = modelList;
    }

    @NonNull
    @Override
    public MyKidsHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View kidsview = LayoutInflater.from(context).inflate(R.layout.kidmodel_item, parent, false);
        return new MyKidsHolder(kidsview, context, modelList);
    }

    @Override
    public void onBindViewHolder(@NonNull MyKidsHolder holder, int position) {
        KidsModel model = modelList.get(position);
        holder.imageView.setImageDrawable(ContextCompat.getDrawable(context, model.getImage()));
    }

    @Override
    public int getItemCount() {
        return modelList.size();
    }

    public class MyKidsHolder extends RecyclerView.ViewHolder {
        private Context context1;
        private List<KidsModel> kidsmodelList;
        ImageView imageView, cancel;
        TextView kidmodelname;


        public MyKidsHolder(View itemView, final Context context, List<KidsModel> modelList) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.kidmodel_image);
            this.context1 = context;
            this.kidsmodelList = modelList;
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getAdapterPosition();


                }
            });
        }
    }
}
