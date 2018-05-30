package com.deemsysinc.kidsar.models;

public class PurchaseModel {
    public String productid, product_name, product_desc, product_price;
    public boolean purchased;

    public PurchaseModel(String productid, String product_name, String product_desc, String product_price, boolean purchased) {
        this.productid = productid;
        this.product_name = product_name;
        this.product_desc = product_desc;
        this.product_price = product_price;
        this.purchased = purchased;
    }

    public String getProductid() {
        return productid;
    }

    public void setProductid(String productid) {
        this.productid = productid;
    }

    public String getProduct_name() {
        return product_name;
    }

    public void setProduct_name(String product_name) {
        this.product_name = product_name;
    }

    public String getProduct_desc() {
        return product_desc;
    }

    public void setProduct_desc(String product_desc) {
        this.product_desc = product_desc;
    }

    public String getProduct_price() {
        return product_price;
    }

    public void setProduct_price(String product_price) {
        this.product_price = product_price;
    }

    public boolean isPurchased() {
        return purchased;
    }

    public void setPurchased(boolean purchased) {
        this.purchased = purchased;
    }
}
