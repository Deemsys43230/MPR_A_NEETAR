package com.deemsysinc.kidsar.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.deemsysinc.kidsar.R;
import com.deemsysinc.kidsar.UnityPlayerActivity;
import com.deemsysinc.kidsar.models.AlphapetsModel;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class AlphapetsAdapter extends RecyclerView.Adapter<AlphapetsAdapter.AlphapetsHolder>  {
    Context context;

    ArrayList<AlphapetsModel> alphapetsList;

    int getposition;

    public AlphapetsAdapter(Context context, ArrayList<AlphapetsModel> alphapetsList,int getposition)
    {
        this.context=context;
        this.alphapetsList=alphapetsList;
        this.getposition=getposition;
    }

    @Override
    public AlphapetsHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view=null;
        if(getposition==0)
        {
             view= LayoutInflater.from(parent.getContext()).inflate(R.layout.alphapets_item,parent,false);

        }
        else if(getposition==1||getposition==2)
        {
            view= LayoutInflater.from(parent.getContext()).inflate(R.layout.animals_item,parent,false);

        }

        view.setOnClickListener(UnityPlayerActivity.onClickListener);
        AlphapetsHolder alphapetsHolder=new AlphapetsHolder(view);
        return alphapetsHolder;
    }

    @Override
    public void onBindViewHolder(AlphapetsHolder holder, int position) {
        holder.alphapetLabel.setText(alphapetsList.get(position).getModelName());
//        if(alphapetsList.get(position).getModelid()==1||alphapetsList.get(position).getModelid()==2)
//        {
//            holder.purchaseLocker.setVisibility(View.GONE);
//        }
//        else
//        {
//            holder.purchaseLocker.setVisibility(View.VISIBLE);
//        }
        if (position == 0 || position == 1) {
            alphapetsList.get(position).isPurchased(true);
            holder.purchaseLocker.setVisibility(View.GONE);
        } else if (alphapetsList.get(position).getIsPurchased()) {
            holder.purchaseLocker.setVisibility(View.GONE);
        } else {
            holder.purchaseLocker.setVisibility(View.VISIBLE);
        }
        InputStream bitmap= null;
        try {
            bitmap = context.getAssets().open(alphapetsList.get(position).getModelImage());
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.d("PrintBitmap",""+bitmap);
        Bitmap outImage= BitmapFactory.decodeStream(bitmap);
        holder.theAlphapet.setImageBitmap(outImage);
    }

    @Override
    public int getItemCount() {
        return alphapetsList.size();
    }

    class AlphapetsHolder extends RecyclerView.ViewHolder
    {
        ImageView purchaseLocker;
        ImageView theAlphapet;
        TextView alphapetLabel;
        public AlphapetsHolder(View itemView) {
            super(itemView);
            theAlphapet=(itemView).findViewById(R.id.alphapets);
            purchaseLocker=(itemView).findViewById(R.id.lock);
            alphapetLabel=(itemView).findViewById(R.id.alphapet_label);
        }
    }
}
