package com.deemsysinc.kidsar.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.deemsysinc.kidsar.PurchaseActivity;
import com.deemsysinc.kidsar.R;
import com.deemsysinc.kidsar.models.PurchaseModel;

import java.util.List;

public class PurchaseAdapter extends RecyclerView.Adapter<PurchaseAdapter.MyPurchaseHolder> {
    private List<com.deemsysinc.kidsar.models.PurchaseModel> modelList;
    private PurchaseActivity ActPurchase;
    private Context context;

    public PurchaseAdapter(Context context, PurchaseActivity purchaseActivity, List<PurchaseModel> modelList) {
        this.context = context;
        this.ActPurchase = purchaseActivity;
        this.modelList = modelList;
    }

    @NonNull
    @Override
    public MyPurchaseHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View kidsview = LayoutInflater.from(context).inflate(R.layout.purchasemodel_item, parent, false);
        kidsview.setOnClickListener(PurchaseActivity.onClickListener);
        return new MyPurchaseHolder(kidsview);
    }

    @Override
    public void onBindViewHolder(final MyPurchaseHolder holder, final int position) {
        holder.productname.setText(modelList.get(position).product_name);
        holder.productdesc.setText(modelList.get(position).product_desc);
        holder.price.setText(modelList.get(position).product_price);
        if (modelList.get(position).isPurchased()) {
            holder.buybutton.setVisibility(View.GONE);
            holder.alreadyowned.setVisibility(View.VISIBLE);
        } else {
            holder.buybutton.setVisibility(View.VISIBLE);
            holder.alreadyowned.setVisibility(View.GONE);
        }
        holder.buybutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActPurchase.purchaseClickBuy(getItemViewType(position), modelList.get(position).productid);
            }
        });
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return modelList.size();
    }

    public class MyPurchaseHolder extends RecyclerView.ViewHolder {
        TextView productdesc, productname, price;
        ImageView alreadyowned;
        TextView buybutton;

        public MyPurchaseHolder(View itemView) {
            super(itemView);
            productname = itemView.findViewById(R.id.productname);
            productdesc = itemView.findViewById(R.id.productdesc);
            price = itemView.findViewById(R.id.price);
            buybutton = itemView.findViewById(R.id.buybutton);
            alreadyowned = itemView.findViewById(R.id.alreadyowned);
        }
    }
}
