package com.deemsysinc.kidsar;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.deemsysinc.kidsar.models.PurchaseModel;
import com.google.gson.reflect.TypeToken;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

public class KidsAdapter extends RecyclerView.Adapter<KidsAdapter.MyKidsHolder> {
    int height;
    List<KidsModel> modelList;
    Context context;
    Type type = new TypeToken<List<PurchaseModel>>() {
    }.getType();
    List<PurchaseModel> mymodel;


    public KidsAdapter(Context context, List<KidsModel> modelList, List<PurchaseModel> mymodel) {
        this.context = context;
        this.modelList = modelList;
        this.mymodel = mymodel;
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        float width = metrics.widthPixels;
        height = (int) (width * 0.625f);
    }

    @NonNull
    @Override
    public MyKidsHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View kidsview = LayoutInflater.from(context).inflate(R.layout.kidmodel_item, parent, false);
        return new MyKidsHolder(kidsview);
    }

    @Override
    public void onBindViewHolder(@NonNull MyKidsHolder holder, int position) {
//        KidsModel model = modelList.get(position);
        Bitmap decoded = null;
        try {
            Bitmap bitmap1 = BitmapFactory.decodeStream(context.getAssets().open(modelList.get(position).getImage()));
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            bitmap1.compress(Bitmap.CompressFormat.PNG, 100, out);
            decoded = BitmapFactory.decodeStream(new ByteArrayInputStream(out.toByteArray()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        holder.imageView.setImageBitmap(decoded);
        if (!modelList.get(position).getName().isEmpty()) {
            for (int i = 0; i < mymodel.size(); i++) {
                if (mymodel.get(i).getProductid().equals(modelList.get(position).getName())) {
                    Bitmap newunlock = null;
                    try {
                        Bitmap bitmap = BitmapFactory.decodeStream(context.getAssets().open(modelList.get(position).getImageunlock()));
                        ByteArrayOutputStream out = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
                        newunlock = BitmapFactory.decodeStream(new ByteArrayInputStream(out.toByteArray()));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    holder.imageView.setImageBitmap(newunlock);
                }
            }
        }
    }

    @Override
    public int getItemCount() {
        return modelList.size();
    }

    public class MyKidsHolder extends RecyclerView.ViewHolder {
        private LinearLayout linearkid;
        ImageView imageView;

        MyKidsHolder(View itemView) {
            super(itemView);
            linearkid = (LinearLayout) itemView.findViewById(R.id.linearkid);
            linearkid.getLayoutParams().height = height;
            imageView = (ImageView) itemView.findViewById(R.id.kidmodel_image);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getAdapterPosition();
                    Bundle bundle = new Bundle();
                    bundle.putInt("selectedPos", pos);
                    Intent goPlayer = new Intent(context, UnityPlayerActivity.class);
                    goPlayer.putExtras(bundle);
                    context.startActivity(goPlayer);
                }
            });
        }
    }
}
