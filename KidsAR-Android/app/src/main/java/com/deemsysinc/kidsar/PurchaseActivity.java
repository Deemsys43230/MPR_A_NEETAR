package com.deemsysinc.kidsar;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.SkuDetails;
import com.deemsysinc.kidsar.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.deemsysinc.kidsar.adapter.PurchaseAdapter;
import com.deemsysinc.kidsar.models.PurchaseModel;
import com.deemsysinc.kidsar.utils.BillingManager;
import com.deemsysinc.kidsar.utils.Constants;
import com.deemsysinc.kidsar.utils.MyApplication;
import com.deemsysinc.kidsar.utils.NetworkDetector;

import com.deemsysinc.kidsar.utils.PlayAudioService;
import com.deemsysinc.kidsar.utils.Products;

public class PurchaseActivity extends AppCompatActivity implements BillingManager.BillingUpdatesListener {
    public static View.OnClickListener onClickListener;
    ImageView close;
    private RecyclerView listrecycler;
    private RecyclerView.LayoutManager layoutManager;
    private List<PurchaseModel> modellist = new ArrayList<>();
    private PurchaseAdapter purchaseAdapter;
    private BillingManager billingManager;
    private TextView restore_button;
    SharedPreferences prefs;
    Gson gson = new Gson();
    Type type = new TypeToken<List<PurchaseModel>>() {
    }.getType();
    private int listposition = -1;
    private AlertDialog alertDialog;
    private TextView alertTitle, alert_message;
    private Button okalert, cancelalert;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_purchase);
        prefs = getSharedPreferences(Constants.AppPreferences, Context.MODE_PRIVATE);
        onClickListener = new PurchaseClick(this);
        NetworkDetector networkDetector = new NetworkDetector(this);
        if (networkDetector.isConnectingToInternet()) {
            billingManager = new BillingManager(PurchaseActivity.this, this);
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(PurchaseActivity.this);
            LayoutInflater inflater = getLayoutInflater();
            View dialogView = inflater.inflate(R.layout.alertdialog, null);
            builder.setView(dialogView);
            alertTitle = (TextView) dialogView.findViewById(R.id.alertTitle);
            alertTitle.setText(R.string.noInternet);
            alert_message = (TextView) dialogView.findViewById(R.id.alert_message);
            alert_message.setText("Make sure your device is connected to the internet.");
            okalert = (Button) dialogView.findViewById(R.id.okalert);
            okalert.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    alertDialog.dismiss();
                }
            });
            cancelalert = (Button) dialogView.findViewById(R.id.cancelalert);
            cancelalert.setVisibility(View.GONE);
            alertDialog = builder.create();
            alertDialog.show();
        }

        listrecycler = findViewById(R.id.purchase_list_recycler);
        restore_button = findViewById(R.id.restore_button);
        layoutManager = new LinearLayoutManager(this);
        purchaseAdapter = new PurchaseAdapter(this, this, modellist);
        listrecycler.setLayoutManager(layoutManager);
        close = findViewById(R.id.buttonclose);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(R.anim.slide_in_down, R.anim.slide_out_down);
            }
        });
        restore_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listposition = -1;
                if (billingManager != null) {
                    billingManager.queryPurchases();
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onResume() {
        super.onResume();
        MyApplication myApp = (MyApplication) this.getApplication();
        if (myApp.wasInBackground) {
            PlayAudioService.onResumePlayer();
        }
        myApp.stopActivityTransitionTimer();
    }

    @Override
    protected void onPause() {
        super.onPause();
        ((MyApplication) this.getApplication()).startActivityTransitionTimer(this);
    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.slide_in_down, R.anim.slide_out_down);
    }

    @Override
    public void onBillingClientSetupFinished() {
        List<String> list = Products.getModelPurchaseList();
        billingManager.querySkuDetailsAsync(BillingClient.SkuType.INAPP, list);
    }

    @Override
    public void onConsumeFinished(String token, int result) {
        if (result == BillingClient.BillingResponse.OK) {
            Toast.makeText(PurchaseActivity.this, "Ok Response", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onPurchasesUpdated(List<Purchase> purchases) {
        List<PurchaseModel> model = new ArrayList<PurchaseModel>();
       /*for (int i = 0; i < modellist.size(); i++) {
            for (int j = 0; j < purchases.size(); j++) {
                if (modellist.get(i).getProductid().equals(purchases.get(j).getSku())) {
                    modellist.get(i).setPurchased(true);
                    PurchaseModel pmodel = modellist.get(i);
                    //add the model list
                    model.add(pmodel);
                    String json = gson.toJson(model, type);
                    Log.d("PurchaseRestore", json);
                    prefs.edit().putString(Constants.purchased_product, json).apply();
                }
            }
            if (purchaseAdapter != null)
                purchaseAdapter.notifyDataSetChanged();
        }*/
        for (int i = 0; i < purchases.size(); i++) {
            for (int j = 0; j < modellist.size(); j++) {
                if (modellist.get(j).getProductid().equals(purchases.get(i).getSku())) {
                    modellist.get(j).setPurchased(true);
                }
            }
            PurchaseModel pmodel = new PurchaseModel(purchases.get(i).getSku(), "", "", "", true);
            //add the model list
            model.add(pmodel);
        }
        String json = gson.toJson(model, type);
        Log.d("PurchaseRestore", json);
        prefs.edit().putString(Constants.purchased_product, json).apply();
        if (purchaseAdapter != null)
            purchaseAdapter.notifyDataSetChanged();
    }

    @Override
    public void onPurchaseHistoryResponse(List<Purchase> purchases) {
        Toast.makeText(PurchaseActivity.this, "" + purchases.size(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPurchaseReponse(String response) {
        if (purchaseAdapter != null) {
            if (response.equals("You have already purchased this item. Click RESTORE to update your purchase list.") || response.equals("")) {
                String mypurchases = prefs.getString(Constants.purchased_product, "");
                if (listposition >= 0) {
                    modellist.get(listposition).setPurchased(true);
                    List<PurchaseModel> fromJson = gson.fromJson(mypurchases, type);
                    List<PurchaseModel> model = new ArrayList<>();
                    if (fromJson != null) {
                        model = new ArrayList<>(fromJson);
                    }
                    PurchaseModel pmodel = modellist.get(listposition);
                    //add the model list
                    model.add(pmodel);
                    for (int i = 0; i < model.size(); i++) {
                        for (int j = 1; j < model.size() - 1; j++) {
                            if (model.get(i).productid.equals(model.get(j).productid)) {
                                model.remove(j);
                            }
                        }
                    }
                    String json = gson.toJson(model, type);
                    Log.d("PurchaseBuy", json);
                    prefs.edit().putString(Constants.purchased_product, json).apply();
                    purchaseAdapter.notifyDataSetChanged();
                }
                if (!response.equals(""))
                    Toast.makeText(PurchaseActivity.this, response, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(PurchaseActivity.this, response, Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onSkuDetailsResponse(int responseCode, List<SkuDetails> skuDetailsList) {
        for (int i = 0; i < skuDetailsList.size(); i++) {
            String productid = skuDetailsList.get(i).getSku();
            String productname = skuDetailsList.get(i).getTitle();
            String productdesc = skuDetailsList.get(i).getDescription();
            String productprice = skuDetailsList.get(i).getPrice();
            modellist.add(new PurchaseModel(productid, productname, productdesc, productprice, false));
        }
        listrecycler.setAdapter(purchaseAdapter);
        String mypurchases = prefs.getString(Constants.purchased_product, "");
        if (!mypurchases.isEmpty()) {
            Log.d("PurchaseOnCreate", mypurchases);
            //Get the json value from Shared Preference and Convert to List in My Purchased values
            List<PurchaseModel> fromJson = gson.fromJson(mypurchases, type);
            for (int i = 0; i < modellist.size(); i++) {
                for (PurchaseModel mylist : fromJson) {
                    if (modellist.get(i).productid.equals(mylist.productid)) {
                        modellist.get(i).setPurchased(true);
                    }
                }
            }
            if (purchaseAdapter != null)
                purchaseAdapter.notifyDataSetChanged();
        }
    }

    public void purchaseClickBuy(int position, String productid) {
        listposition = position;
        billingManager.initiatePurchaseFlow(productid, BillingClient.SkuType.INAPP);
    }

    public class PurchaseClick implements View.OnClickListener {
        Context context;

        public PurchaseClick(Context context) {
            this.context = context;
        }

        @Override
        public void onClick(View v) {
            int itemPosition = listrecycler.getChildLayoutPosition(v);
            if (!modellist.get(itemPosition).isPurchased()) {

            }
        }
    }
}
