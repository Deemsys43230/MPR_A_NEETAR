package com.deemsysinc.kidsar.utils;

import java.util.ArrayList;
import java.util.List;

public class Products {
    public static final List<String> getModelPurchaseList() {
        List<String> list = new ArrayList<>();
        list.add("android.test.purchased");
        list.add("android.test.canceled");
        list.add("android.test.item_unavailable");
        return list;
    }
}
