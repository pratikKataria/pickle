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
    private boolean currentLocationFound;
    private String displayAddress;
    private String firebaseDatabaseAddress;
    private String deliveryTime;

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
    public void setDisplayAddress(String displayAddress) {
        this.displayAddress = displayAddress;
        Log.e(CartActivity.class.getName(), "address: " + displayAddress);
        notifyPropertyChanged(BR.displayAddress);
    }

    @Bindable
    public String getDisplayAddress() {
        return displayAddress;
    }

    @Bindable
    public boolean isCartVisible() {
        return isCartVisible;
    }

    @Bindable
    public void  setCurrentLocationFound(boolean currentLocationFound) {
        this.currentLocationFound = currentLocationFound;
        Log.e(CartViewModel.class.getName(), "current found " + currentLocationFound);
        notifyPropertyChanged(BR.currentLocationFound);
    }

    @Bindable
    public boolean isCurrentLocationFound() {
        return currentLocationFound;
    }

    @Bindable
    public void setDeliveryTime(String deliveryTime) {
        this.deliveryTime = deliveryTime;
        notifyPropertyChanged(BR.deliveryTime);
    }

    @Bindable
    public String getDeliveryTime() {return deliveryTime;}

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

    public void getDatabaseAddress() {
        if (FirebaseAuth.getInstance().getCurrentUser() != null && !FirebaseAuth.getInstance().getCurrentUser().isAnonymous()) {
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Addresses/" + FirebaseAuth.getInstance().getUid());
            reference.keepSynced(true);
            reference.child("slot1").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Address newAddress = dataSnapshot.getValue(Address.class);
                    if (newAddress != null && newAddress.getGpsLocation() != null) {
                        setDisplayAddress(newAddress.toString());
                        setFirebaseDatabaseAddress(newAddress.getGpsLocation());
                        return;
                    }

                    if (newAddress != null) {
                        setDisplayAddress(newAddress.toString());
                        setFirebaseDatabaseAddress(newAddress.toString());
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    setDisplayAddress(databaseError.getMessage());
                }
            });
        }
    }

    public String getFirebaseDatabaseAddress() {
        return firebaseDatabaseAddress;
    }

    public void setFirebaseDatabaseAddress(String firebaseDatabaseAddressString) {
        this.firebaseDatabaseAddress = firebaseDatabaseAddressString;
    }

    public void setCurrentLocation(String currentLocationString) {
        setDisplayAddress("Current Location set");
        setFirebaseDatabaseAddress(currentLocationString);
    }

}
