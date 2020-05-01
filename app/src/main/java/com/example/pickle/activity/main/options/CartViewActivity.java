package com.example.pickle.activity.main.options;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.example.pickle.Adapters.CartRecyclerViewAdapter;
import com.example.pickle.R;
import com.example.pickle.activity.main.MainActivity;
import com.example.pickle.binding.IMainActivity;
import com.example.pickle.binding.INavigation;
import com.example.pickle.binding.OrderStatus;
import com.example.pickle.data.CartViewModel;
import com.example.pickle.data.OrderDetails;
import com.example.pickle.data.OrdersData;
import com.example.pickle.data.ProductModel;
import com.example.pickle.databinding.ActivityCartTestViewBinding;
import com.example.pickle.utils.SharedPrefsUtils;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CartViewActivity extends AppCompatActivity implements IMainActivity, INavigation {

    private ActivityCartTestViewBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_cart_test_view);

        getShoppingCart();

        try {
            binding.getCartViewModel().getDatabaseAddress();
        } catch (NullPointerException npe) {
            Log.e(CartViewActivity.class.getName(), npe.getMessage());
        }

        binding.includeLayout.placeOrder.setOnClickListener(n -> placeOrder());

        BottomSheetBehavior behavior = BottomSheetBehavior.from(binding.includeLayout.getRoot());
        behavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) { }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                if (slideOffset < 0.75) {
                    binding.nestedScrollView.setAlpha(Math.abs(1 - slideOffset));
                    binding.nestedScrollView.setBackgroundColor(Color.WHITE);
                } else if (slideOffset > 0.90) {
                    binding.nestedScrollView.setBackgroundColor(0xFFececec);
                }
            }
        });

    }


    private void placeOrder() {
        BottomSheetBehavior bottomSheetBehavior = BottomSheetBehavior.from(binding.includeLayout.getRoot());
        try {
            String deliveryTime = binding.getCartViewModel().getDeliveryTime();
            String deliveryAddress = binding.getCartViewModel().getAddress();
            if (deliveryTime == null || deliveryTime.isEmpty()) {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                Toast.makeText(this, "select delivery time", Toast.LENGTH_SHORT).show();
                return;
            }
            if (deliveryAddress == null || deliveryAddress.isEmpty()) {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                Toast.makeText(this, "select delivery Address", Toast.LENGTH_SHORT).show();
                return;
            }
            List<ProductModel> productCartList = binding.getCartList();
            Map<String, Object> atomicOperation = new HashMap<>();
            for (ProductModel product : productCartList) {
                String key = FirebaseDatabase.getInstance().getReference("Orders").push().getKey();
                atomicOperation.put("OrdersDetails/" + key, new OrdersData(
                        product.getItemId(),
                        new SimpleDateFormat("yyyy.MM.dd 'at' HH:mm:ss").format(new Date()),
                        product.getQuantityCounter(),
                        product.getItemBasePrice(),
                        product.getItemCategory(),
                        deliveryAddress,
                        deliveryTime
                ));
                atomicOperation.put("Orders/" + key, new OrderDetails(
                        "ddEk1gOv0hUFZVinEWzzdZNlBtF3"/*FirebaseAuth.getInstance().getUid()*/,
                        product.getItemId(),
                        OrderStatus.PROCESSING
                ));
            }
            //todo replace with firebase instance
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
            ref.updateChildren(atomicOperation).addOnSuccessListener(aVoid -> Toast.makeText(this, "Thanks For Shopping", Toast.LENGTH_LONG).show())
                    .addOnFailureListener(e -> Toast.makeText(this, "error : " + e.getMessage(), Toast.LENGTH_SHORT).show());

        } catch (Exception xe) {
            Toast.makeText(this, "unable to process request: contact administrator ", Toast.LENGTH_SHORT).show();
            Log.e(CartViewActivity.class.getName(), xe.getMessage());
        }
    }

    private void getShoppingCart() {
        List<ProductModel> cartList = new ArrayList<>();

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        if (preferences != null) {
            Map<String, ?> allEntries = preferences.getAll();
            for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
                String list = SharedPrefsUtils.getStringPreference(this, entry.getKey(), 0);
                ProductModel cartProduct = new Gson().fromJson(list, ProductModel.class);
                if (cartProduct != null) {
                    cartList.add(cartProduct);
                }
            }
        }

        binding.setCartList(cartList);

        CartViewModel cartViewModel = new CartViewModel();
        cartViewModel.setCartProducts(cartList);
        binding.setCartViewModel(cartViewModel);
    }

    @Override
    public void updateQuantity(ProductModel productModel, int quantity) {
        if (productModel != null) {
            productModel.setQuantityCounter(quantity);
            SharedPrefsUtils.setStringPreference(this, productModel.getItemId(), new Gson().toJson(productModel));
            getShoppingCart();
        }
    }

    @Override
    public void removeProduct(ProductModel productModel) {
        SharedPrefsUtils.removeValuePreference(this, productModel.getItemId());
        getShoppingCart();
        try {
            ((CartRecyclerViewAdapter)binding.cartRecyclerView.getAdapter()).updateCartItemsList(productModel);
        } catch (NullPointerException npe) {
            Log.e(CartViewActivity.class.getName(), "cart update item: " + npe.getMessage());
        }
    }

    @Override
    public void navigateTo(int navigationId) {
        startActivity(new Intent(this, MainActivity.class).putExtra("NAVIGATION_ID", navigationId));
        finish();
    }

    @Override
    public void onHomePressed(boolean pressed) {
        onBackPressed();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
