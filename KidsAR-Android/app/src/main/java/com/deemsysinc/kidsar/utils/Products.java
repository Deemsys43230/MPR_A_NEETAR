package com.deemsysinc.kidsar.utils;

import java.util.ArrayList;
import java.util.List;

public class Products {
    public static List<String> getModelPurchaseList() {
        List<String> list = new ArrayList<>();
        list.add("com.deemsysinc.kidsar.basicmodels");
        list.add("com.deemsysinc.kidsar.premiummodel");
        return list;
    }
}
