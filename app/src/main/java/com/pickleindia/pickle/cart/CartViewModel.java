package com.pickleindia.pickle.cart;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

import com.pickleindia.pickle.BR;
import com.pickleindia.pickle.models.Address;
import com.pickleindia.pickle.models.ProductModel;
import com.pickleindia.pickle.utils.PriceFormatUtils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class CartViewModel extends BaseObservable {
    private List<ProductModel> cartProducts = new ArrayList<>();
    private boolean isCartVisible;

    @Bindable
    public List<ProductModel> getCartProducts() {
        return cartProducts;
    }

    @Bindable
    public void setCartProducts(List<ProductModel> cartProducts) {
        this.cartProducts = cartProducts;
        notifyPropertyChanged(BR.cartProducts);
    }

    @Bindable
    public void setCartVisible(boolean cartVisible) {
        isCartVisible = cartVisible;
        notifyPropertyChanged(BR.cartVisible);
    }

    @Bindable
    public boolean getCartVisible() {
        return isCartVisible;
    }


    @Bindable
    public boolean isCartVisible() {
        return isCartVisible;
    }


    public String getProductQuantitiesString() {
        int totalItems = 0;

        for (ProductModel product : cartProducts) {
            totalItems += 1;
        }

        String s;
        if (totalItems > 1) {
            s = "items";
        } else {
            s = "item";
        }

        if (totalItems > 0)
            setCartVisible(true);
        else
            setCartVisible(false);

        return ("(" + String.valueOf(totalItems) + " " + s + ")");

    }

    public String getTotalCostString() {
        int totalCost = 0;

        for(ProductModel product : cartProducts) {
            int productQuantity = product.getQuantityCounter();
            int cost;
            if (product.getItemSellPrice() > 0) {
               cost = productQuantity * product.getItemSellPrice();
            } else {
                cost = productQuantity * product.getItemBasePrice();
            }
            totalCost += cost;
        }
        return PriceFormatUtils.getStringFormattedPrice(totalCost);
    }

    public int getTotalCostInt() {
        int totalCost = 0;

        for(ProductModel product : cartProducts) {
            int productQuantity = product.getQuantityCounter();
            int cost;
            if (product.getItemSellPrice() > 0) {
                cost = productQuantity * product.getItemSellPrice();
            } else {
                cost = productQuantity * product.getItemBasePrice();
            }
            totalCost += cost;
        }
        return totalCost;
    }
}
