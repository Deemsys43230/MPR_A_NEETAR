package com.deemsysinc.kidsar.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.deemsysinc.kidsar.KidsModel;
import com.deemsysinc.kidsar.PuzzleDetail;
import com.deemsysinc.kidsar.R;
import com.deemsysinc.kidsar.UnityPlayerActivity;
import com.deemsysinc.kidsar.models.PurchaseModel;
import com.deemsysinc.kidsar.models.PuzzleModel;
import com.google.gson.reflect.TypeToken;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

public class PuzzleAdapter extends RecyclerView.Adapter<PuzzleAdapter.MyPuzzleHolder> {
    int height;
    List<PuzzleModel> modelList;
    Context context;
    private int lastPosition = -1;
    private Animation animation = null;

    public PuzzleAdapter(Context context, List<PuzzleModel> modelList) {
        this.context = context;
        this.modelList = modelList;
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        float width = metrics.widthPixels;
        height = (int) (width * 0.625f);
        animation = AnimationUtils.loadAnimation(context, R.anim.zoom);
    }

    @NonNull
    @Override
    public MyPuzzleHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View kidsview = LayoutInflater.from(context).inflate(R.layout.kidpuzzle_item, parent, false);
        return new MyPuzzleHolder(kidsview);
    }

    @Override
    public void onBindViewHolder(@NonNull MyPuzzleHolder holder, int position) {
        setAnimation(holder.item, position);
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
    }

    @Override
    public int getItemCount() {
        return modelList.size();
    }

    public class MyPuzzleHolder extends RecyclerView.ViewHolder {
        private LinearLayout linearkidpuzzle;
        ImageView imageView;
        View item;

        MyPuzzleHolder(View itemView) {
            super(itemView);
            this.item = itemView;
            linearkidpuzzle = (LinearLayout) itemView.findViewById(R.id.linearkidpuzzle);
            linearkidpuzzle.getLayoutParams().height = height;
            imageView = (ImageView) itemView.findViewById(R.id.kidpuzzle_image);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (animation != null)
                        animation.cancel();
                    int pos = getAdapterPosition();
                    String name = modelList.get(pos).getName();
                    Intent intent = new Intent(context, PuzzleDetail.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("PuzzleHeader", name);
                    intent.putExtra("PuzzleIndex", pos);
                    context.startActivity(intent);
                }
            });
        }
    }

    private void setAnimation(View viewToAnimate, int position) {
        // If the bound view wasn't previously displayed on screen, it's animated
        if (position > lastPosition) {
            viewToAnimate.startAnimation(animation);
            lastPosition = position;
        }
    }
}
