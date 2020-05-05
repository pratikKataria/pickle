package com.example.pickle.activity.main.options;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.example.pickle.Adapters.CartRecyclerViewAdapter;
import com.example.pickle.R;
import com.example.pickle.activity.main.MainActivity;
import com.example.pickle.binding.IMainActivity;
import com.example.pickle.binding.INavigation;
import com.example.pickle.binding.OrderStatus;
import com.example.pickle.data.CartViewModel;
import com.example.pickle.data.ConfirmOrderViewModel;
import com.example.pickle.data.Orders;
import com.example.pickle.data.OrdersDetails;
import com.example.pickle.data.ProductModel;
import com.example.pickle.databinding.ActivityCartViewBinding;
import com.example.pickle.databinding.LayoutConfirmOrderBinding;
import com.example.pickle.utils.PriceFormatUtils;
import com.example.pickle.utils.SharedPrefsUtils;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class CartViewActivity extends AppCompatActivity implements IMainActivity, INavigation {

    private ActivityCartViewBinding binding;
    private AlertDialog alertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_cart_view);

        getShoppingCart();

        try {
            binding.getCartViewModel().getDatabaseAddress();
        } catch (NullPointerException npe) {
            Log.e(CartViewActivity.class.getName(), npe.getMessage());
        }

        binding.setActivity(this);
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
            HashMap<String, Object> atomicOperation = new HashMap<>();
            for (ProductModel product : productCartList) {
                String key = FirebaseDatabase.getInstance().getReference("Orders").push().getKey();
                atomicOperation.put("OrdersDetails/" + key, new OrdersDetails(
                        product.getItemId(),
                        new SimpleDateFormat("yyyy.MM.dd 'at' HH:mm:ss").format(new Date()),
                        product.getQuantityCounter(),
                        product.getItemBasePrice(),
                        product.getItemCategory(),
                        deliveryAddress,
                        deliveryTime
                ));
                atomicOperation.put("Orders/" + key, new Orders(
                        "ddEk1gOv0hUFZVinEWzzdZNlBtF3"/*todo FirebaseAuth.getInstance().getUid()*/,
                        key,
                        OrderStatus.PROCESSING,
                        new SimpleDateFormat("yyyy.MM.dd 'at' HH:mm:ss").format(new Date())
                ));
            }

            showOrderConfirmationDialog(atomicOperation);

        } catch (Exception xe) {
            Toast.makeText(this, "unable to process request: contact administrator ", Toast.LENGTH_SHORT).show();
            Log.e(CartViewActivity.class.getName(), xe.getMessage());
        }
    }

    private void showOrderConfirmationDialog(HashMap<String, Object> atomicOperation) {
        LayoutConfirmOrderBinding confirmOrderBinding = DataBindingUtil.inflate(
                LayoutInflater.from(this),
                R.layout.layout_confirm_order,
                null,
                false);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this, R.style.AlertDialog).setView(confirmOrderBinding.getRoot());
        alertDialog = alertDialogBuilder.create();

        ConfirmOrderViewModel confirmOrderViewModel = new ConfirmOrderViewModel();
        String totalText = binding.amountToBePaid.getText().toString();
        String totalQuantity = binding.getCartViewModel().getProductQuantitiesString();
        confirmOrderViewModel.setTotalPrice(PriceFormatUtils.getIntFormattedPrice(totalText));
        confirmOrderViewModel.setQuantity(totalQuantity);
        confirmOrderBinding.setConfirmModel(confirmOrderViewModel);
        alertDialog.show();

        confirmOrderBinding.successAnimation.setVisibility(View.GONE);
        confirmOrderBinding.confirmBtn.setOnClickListener(n -> {
            confirmOrderBinding.lottieAnimationView2.playAnimation();
            confirmOrderBinding.lottieAnimationView2.loop(true);

            //todo replace with firebase instance
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
            ref.updateChildren(atomicOperation).addOnSuccessListener(aVoid -> {
                binding.getCartList().clear();
                binding.cartRecyclerView.getAdapter().notifyDataSetChanged(); // todo add try catch
                binding.getCartViewModel().setCartVisible(false);
                SharedPrefsUtils.clearCart(this);
                new Handler().postDelayed(() -> {
                    confirmOrderBinding.lottieAnimationView2.setAlpha(0);
                    confirmOrderBinding.successAnimation.setVisibility(View.VISIBLE);
                    confirmOrderBinding.successAnimation.playAnimation();
                    confirmOrderBinding.thankText.animate().alpha(1).setDuration(500).start();
                    confirmOrderBinding.homeBtn.setVisibility(View.VISIBLE);
                    confirmOrderBinding.homeBtn.setOnClickListener(v -> {
                        alertDialog.dismiss();
                        finish();
                    });
                    confirmOrderBinding.backBtn.setVisibility(View.GONE);
                    confirmOrderBinding.confirmBtn.setVisibility(View.GONE);
                    alertDialog.setCancelable(false);

                    Toast.makeText(this, "Thanks For Shopping", Toast.LENGTH_LONG).show();
                }, 1200);
            }).addOnFailureListener(e -> {
                confirmOrderBinding.errorText.setVisibility(View.VISIBLE);
                confirmOrderBinding.errorText.setText(e.getMessage());
                Toast.makeText(this, "error : " + e.getMessage(), Toast.LENGTH_SHORT).show();
            });
        });

        confirmOrderBinding.backBtn.setOnClickListener(n -> {
            alertDialog.dismiss();
        });

    }

    private void getShoppingCart() {
        List<ProductModel> cartList = SharedPrefsUtils.getAllProducts(this);
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
        finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (alertDialog != null) {
            alertDialog.dismiss();
            alertDialog = null;
        }
    }

    public void setTransparentStatusBar() {
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }else {
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
        }
        window.setStatusBarColor(Color.TRANSPARENT);
    }
}
