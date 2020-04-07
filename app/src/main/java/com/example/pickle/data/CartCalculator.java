package com.example.pickle.data;

import android.util.Log;

import com.airbnb.lottie.L;

import java.util.ArrayList;

public class CartCalculator{

    private int cartAmount;

    public void cartAmount(ArrayList<ProductModel> productModelArrayList) {
        int cartAmount = 0;
        for (ProductModel productModel : productModelArrayList) {
            if (productModel.getQuantityCounter() > 0 ) {
                cartAmount += (productModel.getItemBasePrice() * productModel.getQuantityCounter());
            }
        }
        this.cartAmount = cartAmount;
    }

    public int getCartAmount() {
        return cartAmount;
    }
    public void setCartAmount(int amount) { cartAmount = amount;}

}
