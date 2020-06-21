package com.example.pickle.cart;

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
import com.example.pickle.Login.CustomerDetailActivity;
import com.example.pickle.Login.LoginActivity;
import com.example.pickle.R;
import com.example.pickle.adapters.CartRecyclerViewAdapter;
import com.example.pickle.databinding.ActivityCartViewBinding;
import com.example.pickle.databinding.LayoutConfirmOrderBinding;
import com.example.pickle.interfaces.IMainActivity;
import com.example.pickle.models.OrdersDetails;
import com.example.pickle.models.ProductModel;
import com.example.pickle.utils.NotifyRecyclerItems;
import com.example.pickle.utils.OrderStatus;
import com.example.pickle.utils.SharedPrefsUtils;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.example.pickle.utils.Constant.PRODUCT_TYPE;

public class CartActivity extends AppCompatActivity implements IMainActivity {

    private ActivityCartViewBinding binding;
    private AlertDialog alertDialog;

    private BottomSheetBehavior.BottomSheetCallback bottomSheetCallback = new BottomSheetBehavior.BottomSheetCallback() {
        @Override
        public void onStateChanged(@NonNull View bottomSheet, int newState) {
        }

        @Override
        public void onSlide(@NonNull View bottomSheet, float slideOffset) {
            if (slideOffset < 0.75) {
                binding.nestedScrollView.setAlpha(Math.abs(1 - slideOffset));
                binding.nestedScrollView.setBackgroundColor(Color.WHITE);
            } else if (slideOffset > 0.90) {
                binding.nestedScrollView.setBackgroundColor(0xFFececec);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_cart_view);
        getShoppingCart();
        setTransparentStatusBar();

        binding.setActivity(this);
        binding.getCartViewModel().getDatabaseAddress();

        BottomSheetBehavior<View> behavior = BottomSheetBehavior.from(binding.includeLayout.getRoot());
        behavior.addBottomSheetCallback(bottomSheetCallback);

        binding.includeLayout.placeOrder.setOnClickListener(n -> {
            if (!checkFirebaseAuth()) return;

            //check if user address is present
            checkAddress();
        });
    }

    private void getShoppingCart() {
        List<ProductModel> cartList = SharedPrefsUtils.getAllProducts(this);
        binding.setCartList(cartList);
        CartViewModel cartViewModel = new CartViewModel();
        cartViewModel.setCartProducts(cartList);
        binding.setCartViewModel(cartViewModel);
    }

    //check if user is login or not
    private boolean checkFirebaseAuth() {
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            startActivity(new Intent(this, LoginActivity.class).addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY));
            Toast.makeText(this, "login first", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            return true;
        }
    }

    //check address in database and place order
    private void checkAddress() {
        Toast.makeText(this, "checking address", Toast.LENGTH_SHORT).show();
        DatabaseReference userAddressDatabaseReference = FirebaseDatabase.getInstance().getReference("Addresses");
        if (FirebaseAuth.getInstance().getUid() != null) {
            userAddressDatabaseReference.child(FirebaseAuth.getInstance().getUid())
                    .child("slot1")
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                checkDeliveryTimeAndAddress();
                            } else {
                                Toast.makeText(CartActivity.this, "fill address details", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(CartActivity.this, CustomerDetailActivity.class).addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY));
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                        }
                    });
        }
    }

    private void checkDeliveryTimeAndAddress() {
        BottomSheetBehavior<View> bottomSheetBehavior = BottomSheetBehavior.from(binding.includeLayout.getRoot());
        String deliveryTime = binding.getCartViewModel().getDeliveryTime();
        String deliveryAddress = binding.getCartViewModel().getFirebaseDatabaseAddress();

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

        showOrderConfirmationDialog();
    }

    private void showOrderConfirmationDialog() {
        LayoutConfirmOrderBinding confirmOrderBinding = DataBindingUtil.inflate(
                LayoutInflater.from(this),
                R.layout.layout_confirm_order,
                null,
                false);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this, R.style.AlertDialog).setView(confirmOrderBinding.getRoot());
        alertDialogBuilder.setCancelable(false);
        alertDialog = alertDialogBuilder.create();

        confirmOrderBinding.totalPriceTextView.setText(String.format("total quantity %s", binding.amountToBePaid.getText().toString()));
        confirmOrderBinding.quantityTextView.setText(String.format("total price %s", binding.getCartViewModel().getProductQuantitiesString()));
        alertDialog.show();

        confirmOrderBinding.successAnimation.setVisibility(View.GONE);

        confirmOrderBinding.confirmBtn.setOnClickListener(n -> {
            confirmOrderBinding.lottieAnimationView2.playAnimation();
            confirmOrderBinding.lottieAnimationView2.loop(true);
            sendOrdersToDatabase(isUploaded -> {
                if (isUploaded) {
                    // do animation stuff here
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
                } else {
                    Log.e(CartActivity.class.getName(), "uploading........ else ");
                    Toast.makeText(this, "error : " + "something went wrong please try again", Toast.LENGTH_SHORT).show();
                }
            });
        });

        confirmOrderBinding.backBtn.setOnClickListener(n -> alertDialog.dismiss());
    }

    private void sendOrdersToDatabase(UploadResult result) {
        Map<String, Object> atomicOperation = buildOrders();
        DatabaseReference ordersDatabaseReference = FirebaseDatabase.getInstance().getReference();
        ordersDatabaseReference.updateChildren(atomicOperation).addOnSuccessListener(aVoid -> {
            // clean up the local data base after the orders
            // sent to the database
            binding.getCartList().clear();
            NotifyRecyclerItems.notifyDataSetChanged(binding.cartRecyclerView);
            binding.getCartViewModel().setCartVisible(false);
            SharedPrefsUtils.clearCart(this);
            result.uploaded(true);
        }).addOnFailureListener(e -> result.uploaded(false));
    }

    private Map<String, Object> buildOrders() {
        HashMap<String, Object> atomicOperation = new HashMap<>();
        try {
            List<ProductModel> productCartList = binding.getCartList();
            for (ProductModel product : productCartList) {
                String key = FirebaseDatabase.getInstance().getReference("Orders").push().getKey();
                atomicOperation.put("OrdersDetails/" + key, new OrdersDetails(
                        product.getItemId(),
                        product.getItemThumbImage(),
                        product.getQuantityCounter(),
                        product.getItemBasePrice(),
                        product.getItemSellPrice(),
                        product.getItemCategory(),
                        binding.getCartViewModel().getFirebaseDatabaseAddress(),
                        binding.getCartViewModel().getDeliveryTime()
                ));

                long localTimestamp = System.currentTimeMillis();

                atomicOperation.put("Orders/" + key + "/userId", FirebaseAuth.getInstance().getUid());
                atomicOperation.put("Orders/" + key + "/orderId", key);
                atomicOperation.put("Orders/" + key + "/orderStatus", OrderStatus.PROCESSING);
                atomicOperation.put("Orders/" + key + "/date", localTimestamp);

                atomicOperation.put("UserOrders/" + FirebaseAuth.getInstance().getUid() + "/" + key + "/date", ServerValue.TIMESTAMP);
                atomicOperation.put("UserOrders/" + FirebaseAuth.getInstance().getUid() + "/" + key + "/date_orderId", localTimestamp + "_" + key);
            }
        } catch (Exception xe) {
            Toast.makeText(this, "unable to process request: contact administrator ", Toast.LENGTH_SHORT).show();
            Log.e(CartActivity.class.getName(), xe.getMessage() + "");
        }

        return atomicOperation;
    }

    interface UploadResult {
        void uploaded(boolean isUploaded);
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
        setTransparentStatusBar();
        CartRecyclerViewAdapter cartRecyclerViewAdapter = (CartRecyclerViewAdapter) binding.cartRecyclerView.getAdapter();
        if (cartRecyclerViewAdapter != null)
            cartRecyclerViewAdapter.updateCartItemsList(productModel);
    }

    public void navigateTo(String navigationTo) {
        Intent intent = new Intent();
        intent.putExtra(PRODUCT_TYPE, navigationTo);
        setResult(1, intent);
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
        if (binding.getCartList().size() <= 0) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            }else {
                window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
            }
            window.setStatusBarColor(Color.TRANSPARENT);
        }
    }
}
