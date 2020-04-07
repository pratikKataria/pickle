package com.example.pickle;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pickle.Adapters.CartRecyclerViewAdapter;
import com.example.pickle.data.CartCalculator;
import com.example.pickle.data.PriceFormatUtils;
import com.example.pickle.data.ProductModel;
import com.example.pickle.data.SharedPrefsUtils;
import com.example.pickle.databinding.ActivityCartTestViewBinding;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

public class CartTestView extends AppCompatActivity {

    RecyclerView _cartRecyclerView;
    private ArrayList<ProductModel> cartList;
    ActivityCartTestViewBinding _activityCartTestViewBinding;
    CartCalculator _cartCalculator;

    TextView _cartAmount;
    TextView _amountToBePaid;
    TextView _deliveryPrice;
    public final int[] anIntCartAmount = new int[1];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        _activityCartTestViewBinding = DataBindingUtil.setContentView(this, R.layout.activity_cart_test_view);

        cartList = new ArrayList<>();

        _cartRecyclerView = _activityCartTestViewBinding.cartRecyclerView;
        _amountToBePaid = _activityCartTestViewBinding.amountToBePaid;
        _deliveryPrice = _activityCartTestViewBinding.deliveryPrice;

        _cartAmount = _activityCartTestViewBinding.cartAmountTextView;

        populateList();
        init_recyclerView();

        _cartCalculator = new CartCalculator();
        _cartCalculator.cartAmount(cartList);
        int amount = _cartCalculator.getCartAmount();

        _cartAmount.setText(amount + "");
        Log.e("price increase listerner", "cart amount:  " + _cartAmount.getText().toString());
    }

    private void init_recyclerView() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        CartRecyclerViewAdapter adapter = new CartRecyclerViewAdapter(this, cartList);
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
}