package com.example.pickle.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.pickle.data.ProductModel;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CartHandler {

    private Context context;
    private Map<String, ProductModel> cartMap;

    public CartHandler(Context context) {
        this.context = context;
        cartMap = new HashMap<>();
    }

    public CartHandler(Context context, @NonNull Map<String, ProductModel> cartMap) {
        this.context = context;
        this.cartMap = cartMap;
    }

    public int add(ProductModel product) {
        product.setQuantityCounter(1);
        cartMap.put(product.getItemId(), product);
        save(product.getItemCategory());
        return 1;
    }

    public int increaseQty(String productKey) {
        int initQty = 0;
        if (!cartMap.isEmpty() && cartMap.containsKey(productKey)) {
            ProductModel product = cartMap.get(productKey);
            initQty = product.getQuantityCounter() + 1;
            product.setQuantityCounter(initQty);
            cartMap.put(product.getItemId(), product);
            save(product.getItemCategory());
        }
        return initQty;
    }

    public int decreaseQty(String productKey) {
        int initQty = 0;
        if (!cartMap.isEmpty() && cartMap.containsKey(productKey)) {
            ProductModel product = cartMap.get(productKey);
            initQty = product.getQuantityCounter();
            if (initQty > 1) {
                initQty = product.getQuantityCounter() - 1;
                product.setQuantityCounter(initQty);
                cartMap.put(product.getItemId(), product);
                save(product.getItemCategory());
            } else if (initQty == 1) {
                initQty = product.getQuantityCounter() - 1;
                product.setQuantityCounter(initQty);
                cartMap.remove(product.getItemId());
                save(product.getItemCategory());
            }
        }
        return initQty;
    }

    public Map<String, ProductModel> getCachedProductsMap() {
        Map<String, ProductModel> map = new HashMap<>();
        try {
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
            Map<String, ?> allEntries = preferences.getAll();
            Log.e("CartHandler", allEntries.size() +" : size :");
//            for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
//                Log.e("CartHandler", entry+"");

//                String list = SharedPrefsUtils.getStringPreference(context, entry.getKey(), 0);
//                ProductModel[] models = new Gson().fromJson(list, ProductModel[].class);
//
//                if (list != null && models != null) {
//                    for (ProductModel productModel : models) {
//                        map.put(productModel.getItemId(), productModel);
//                    }
//                }
//            }
        } catch (Exception xe) {
            Log.e("CartHandler", xe.getMessage());
        }

        return map;
    }

    public Map<String, ProductModel> getCartMap() {
        return cartMap;
    }

    public List<ProductModel> getCachedProductList() {
        return (List<ProductModel>) getCachedProductsMap().values();
    }

    public List<ProductModel> getProductLis() {
        return (List<ProductModel>) cartMap.values();
    }

    public int getCartSize() {
        return getCachedProductsMap().size();
    }

    private void save(String key) {
        SharedPrefsUtils.setStringPreference(context, key, new Gson().toJson(new ArrayList<>(cartMap.values())));
    }

}
