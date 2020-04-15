package com.example.pickle.activity.Main.Options;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pickle.Adapters.CartRecyclerViewAdapter;
import com.example.pickle.R;
import com.example.pickle.data.CartCalculator;
import com.example.pickle.data.CustomerOrdersData;
import com.example.pickle.data.ProductModel;
import com.example.pickle.databinding.ActivityCartTestViewBinding;
import com.example.pickle.ui.CustomRadioButton;
import com.example.pickle.utils.PriceFormatUtils;
import com.example.pickle.utils.SharedPrefsUtils;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import io.alterac.blurkit.BlurLayout;


public class CartViewActivity extends AppCompatActivity {

    private RecyclerView _cartRecyclerView;
    private List<ProductModel> cartList;
    private ActivityCartTestViewBinding _activityCartTestViewBinding;
    private CartCalculator _cartCalculator;
    private BottomSheetBehavior _bottomSheetBehavior;

    private TextView _cartAmount;
    private TextView _amountToBePaid;
    private TextView _deliveryPrice;
    private MaterialButton _placeOrderBtn;
    private BlurLayout blurLayout;
    private CustomRadioButton customRadioButton;

    public final int[] anIntCartAmount = new int[1];
    private View _includeView;


    private void initFields() {

        _activityCartTestViewBinding = DataBindingUtil.setContentView(this, R.layout.activity_cart_test_view);


        _cartRecyclerView = _activityCartTestViewBinding.cartRecyclerView;
        _amountToBePaid = _activityCartTestViewBinding.amountToBePaid;
        _deliveryPrice = _activityCartTestViewBinding.deliveryPrice;
        _placeOrderBtn = _activityCartTestViewBinding.placeOrderBtn;
        blurLayout = _activityCartTestViewBinding.blurLayout;

        _includeView = _activityCartTestViewBinding.includeLayout;
        _cartAmount = _activityCartTestViewBinding.cartAmountTextView;

        blurLayout.setAlpha(0);
        cartList = new ArrayList<>();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initFields();

        init_bottomSheet();
        init_customRadioButton();

        populateList();
        init_recyclerView();

        _cartCalculator = new CartCalculator();
        _cartCalculator.cartAmount((ArrayList<ProductModel>) cartList);
        int amount = _cartCalculator.getCartAmount();

        _cartAmount.setText(amount + "");
        Log.e("price increase listerner", "cart amount:  " + _cartAmount.getText().toString());
        _placeOrderBtn.setOnClickListener(n -> {
            for (ProductModel pm : cartList) {
                if (pm != null) {
                    placeOrder(pm);
                }
            }
        });
    }


    private void init_bottomSheet() {
        _bottomSheetBehavior = BottomSheetBehavior.from(_includeView);
        _bottomSheetBehavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (newState == BottomSheetBehavior.STATE_EXPANDED) {
                    blurLayout.startBlur();
                } else {
                    blurLayout.pauseBlur();
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                if (slideOffset > 0 && slideOffset < 0.85) {
                    blurLayout.setAlpha(slideOffset);
                    Log.e("blur cart View Activity", "count " + slideOffset);
                }
            }
        });
    }

    private void init_customRadioButton() {
        RadioGroup radioGroup = _includeView.findViewById(R.id.radioGroup);
        RadioButton address1 = _includeView.findViewById(R.id.address_slot_1);

        radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            Toast.makeText(CartViewActivity.this, "radio group"+ checkedId, Toast.LENGTH_SHORT).show();
            switch (checkedId) {
                case R.id.address_slot_1:
                    Toast.makeText(CartViewActivity.this, "radio group"+ address1.getText(), Toast.LENGTH_SHORT).show();
                    break;
            }
        });
    }

    private void init_recyclerView() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        CartRecyclerViewAdapter adapter = new CartRecyclerViewAdapter(this, (ArrayList<ProductModel>) cartList);
        adapter.setOnPriceChangeListener(new CartRecyclerViewAdapter.PriceChangeListener() {
            @Override
            public void onPriceIncreaseListener(int price) {
                Log.e("price increase listerner" , "cart amount:  " + _cartAmount.getText().toString());
                int currPrice =  PriceFormatUtils.getIntFormattedPrice(_cartAmount.getText().toString()) > 0 ? PriceFormatUtils.getIntFormattedPrice(_cartAmount.getText().toString()) : 0;
                Log.e("price increase listerner" , "cart amount:  "+PriceFormatUtils.getIntFormattedPrice(_cartAmount.getText().toString()) + " price form listner " + price);
                _cartAmount.setText(PriceFormatUtils.getStringFormattedPrice(price + currPrice));
                _amountToBePaid.setText(PriceFormatUtils.getStringFormattedPrice(price + PriceFormatUtils.getIntFormattedPrice(_deliveryPrice.getText().toString())));
            }

            @Override
            public void onPriceDecreaseListener(int price) {
                int currPrice =  PriceFormatUtils.getIntFormattedPrice(_cartAmount.getText().toString()) > 0 ?  PriceFormatUtils.getIntFormattedPrice(_cartAmount.getText().toString()) : 0;

                _cartAmount.setText(PriceFormatUtils.getStringFormattedPrice(Math.abs((price - currPrice))));
                Log.e("price increase listerner" , "cart amount:  "+currPrice + " price form listner " + price);
            }

            @Override
            public void onItemRemovedPriceListener(int price) {
                int currPrice =  PriceFormatUtils.getIntFormattedPrice(_cartAmount.getText().toString()) > 0 ?  PriceFormatUtils.getIntFormattedPrice(_cartAmount.getText().toString()) : 0;
                _cartAmount.setText(PriceFormatUtils.getStringFormattedPrice(Math.abs((price - currPrice))));
            }
        });
        _cartRecyclerView.setLayoutManager(linearLayoutManager);
        _cartRecyclerView.setAdapter(adapter);
    }

    private void placeOrder(ProductModel pm) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Orders").child(FirebaseAuth.getInstance().getUid());

        CustomerOrdersData customerOrdersData = new CustomerOrdersData(
                pm.getQuantityCounter(),
                pm.getItemId(),
                pm.getItemBasePrice(),
                "ordered",
                pm.getItemCategory(),
                "",
                FirebaseAuth.getInstance().getUid(),
                new SimpleDateFormat("yyyy.MM.dd 'at' HH:mm:ss").format(new Date()));

        ref.child(pm.getItemId()).setValue(customerOrdersData).addOnSuccessListener(aVoid -> {
            Toast.makeText(this, "item ordered", Toast.LENGTH_SHORT).show();
        }).addOnFailureListener(e -> {
            Toast.makeText(this, "error : " + e.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }

    private void populateList() {

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        if (preferences != null) {
            Map<String, ?> allEntries = preferences.getAll();
            for (Map.Entry<String, ?> entry : allEntries.entrySet()) {

                String list = SharedPrefsUtils.getStringPreference(this, entry.getKey(), 0);
                ProductModel[] models = new Gson().fromJson(list, ProductModel[].class);

                if (list != null && models != null) {
                    cartList.addAll(Arrays.asList(models));
                }
            }

        }

    }

    private void blurBackground() {

    }

}
