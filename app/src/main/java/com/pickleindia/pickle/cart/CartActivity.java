package com.pickleindia.pickle.cart;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.BackgroundColorSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.Observable;
import androidx.databinding.ObservableField;
import androidx.databinding.ObservableInt;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.chip.Chip;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.pickleindia.pickle.Login.CustomerDetailActivity;
import com.pickleindia.pickle.Login.LoginActivity;
import com.pickleindia.pickle.R;
import com.pickleindia.pickle.adapters.CartRecyclerViewAdapter;
import com.pickleindia.pickle.databinding.ActivityCartViewBinding;
import com.pickleindia.pickle.databinding.LayoutConfirmOrderBinding;
import com.pickleindia.pickle.interfaces.IMainActivity;
import com.pickleindia.pickle.models.Address;
import com.pickleindia.pickle.models.OrdersDetails;
import com.pickleindia.pickle.models.ProductModel;
import com.pickleindia.pickle.utils.DateUtils;
import com.pickleindia.pickle.utils.NotifyRecyclerItems;
import com.pickleindia.pickle.utils.OrderStatus;
import com.pickleindia.pickle.utils.SharedPrefsUtils;
import com.pickleindia.pickle.utils.SnackbarNoSwipeBehavior;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mumayank.com.airlocationlibrary.AirLocation;

import static com.pickleindia.pickle.utils.Constant.GPS_CORD_RE;
import static com.pickleindia.pickle.utils.Constant.PRODUCT_TYPE;

public class CartActivity extends AppCompatActivity implements IMainActivity {

    private ActivityCartViewBinding binding;
    private AlertDialog alertDialog;
    private final ObservableField<String> observableAddress = new ObservableField<>("");
    private final ObservableField<String> displayAddress = new ObservableField<>("");

    private BottomSheetBehavior.BottomSheetCallback bottomSheetCallback = new BottomSheetBehavior.BottomSheetCallback() {
        @Override
        public void onStateChanged(@NonNull View bottomSheet, int newState) {
            if (newState == BottomSheetBehavior.STATE_EXPANDED) {
                if (binding != null && binding.includeLayout.chipDeliveryTime3.isChecked()) {
                    setDeliveryChargeAlert();
                }
            }
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

        binding.setActivity(this);
        binding.setObservableAddress(displayAddress);

        BottomSheetBehavior<View> behavior = BottomSheetBehavior.from(binding.includeLayout.getRoot());
        behavior.addBottomSheetCallback(bottomSheetCallback);

        binding.includeLayout.placeOrder.setOnClickListener(n -> {
            if (!checkFirebaseAuth())
                return;

            //check if user address is present
            checkAddress();
        });

        binding.includeLayout.radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == binding.includeLayout.addressSlot1.getId()) {
                if (observableAddress.get().matches(GPS_CORD_RE) && displayAddress.get().isEmpty()) {
                    observableAddress.set("");
                    checkAddress();
                } else {
                    observableAddress.set(displayAddress.get());
                }
            }
        });

        observableAddress.addOnPropertyChangedCallback(new Observable.OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable sender, int propertyId) {
                if (!observableAddress.get().isEmpty() && observableAddress.get().matches(GPS_CORD_RE)) {
                    binding.includeLayout.currentAddressAlert.setVisibility(View.VISIBLE);
                } else {
                    binding.includeLayout.btmSheetCipCurrentLocation.setText("CURRENT LOCATION");
                    binding.includeLayout.currentAddressAlert.setVisibility(View.GONE);
                }
                Log.e("CartActivity ", sender.toString() + " " + propertyId);
            }
        });

        binding.includeLayout.chipGroup2.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == binding.includeLayout.chipDeliveryTime3.getId()) {
                setDeliveryChargeAlert();
            } else {
                binding.includeLayout.deliveryChargeAlert.setVisibility(View.GONE);
            }
        });
    }

    private void setDeliveryChargeAlert() {
        if (binding.getCartViewModel().getTotalCostInt() >= 500) {
            binding.includeLayout.deliveryChargeAlert.setText(getString(R.string.delivery_charge_alert, "0", "above", "500"));
            binding.includeLayout.deliveryChargeAlert.setTextColor(getResources().getColor(R.color.chartIdealBar));
        } else {
            binding.includeLayout.deliveryChargeAlert.setText(getString(R.string.delivery_charge_alert, "39", " below ", "500"));
            binding.includeLayout.deliveryChargeAlert.setTextColor(Color.RED);
        }
        binding.includeLayout.deliveryChargeAlert.setVisibility(View.VISIBLE);
    }

    public void refreshAddress() {
        if (displayAddress.get().isEmpty()) {
            checkAddress();
        } else {
            observableAddress.set(displayAddress.get());
        }
    }

    private void getShoppingCart() {
        List<ProductModel> cartList = SharedPrefsUtils.getAllProducts(this);
        binding.setCartList(cartList);
        CartViewModel cartViewModel = new CartViewModel();
        cartViewModel.setCartProducts(cartList);
        binding.setCartViewModel(cartViewModel);
        binding.executePendingBindings();
    }

    //check if user is login or not
    private boolean checkFirebaseAuth() {
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            startActivity(new Intent(this, LoginActivity.class).putExtra(CartActivity.class.getName(), "cart"));
            Toast.makeText(this, "login first", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            return true;
        }
    }

    //check address in database and place order
    private void checkAddress() {

        if (FirebaseAuth.getInstance().getUid() == null)
            return;

        binding.includeLayout.progressCircular.setVisibility(View.VISIBLE);
        if (observableAddress.get().isEmpty()) {
            Toast.makeText(this, "checking address", Toast.LENGTH_SHORT).show();
            DatabaseReference userAddressDatabaseReference = FirebaseDatabase.getInstance().getReference("Addresses");
            userAddressDatabaseReference.child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .child("slot1")
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                Address address = dataSnapshot.getValue(Address.class);
                                if (address != null) {
                                    if (address.getGpsLocation() != null) {
                                        observableAddress.set(address.getGpsLocation());
                                        displayAddress.set(address.getGpsLocation());
                                    } else {
                                        observableAddress.set(address.toString());
                                        displayAddress.set(address.toString());
                                    }
                                    isAllProductsValid();
                                }
                            } else {
                                Toast.makeText(CartActivity.this, "fill address details", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(CartActivity.this, CustomerDetailActivity.class).addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY));
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                        }
                    });
        } else {
            isAllProductsValid();
        }
    }

    private void isAllProductsValid() {
        Log.e("CartActivity ", "isAllProductsValid();");
        final List<ProductModel> cartProductList = SharedPrefsUtils.getAllProducts(this);
        List<ProductModel> newProductsList = new ArrayList<>();
        check(new ProductCheckListener() {
            @Override
            public void onReceived(int index, ProductModel productModel) {

                if (productModel != null)
                    newProductsList.add(productModel);

                if (index < cartProductList.size()) {
                    ProductModel product = cartProductList.get(index);
                    getNext(product.getItemCategory(), product.getItemId(), index);
                }
            }

            @Override
            public void onCompleted() {
                binding.includeLayout.progressCircular.setVisibility(View.GONE);
                boolean isOutOfStock = true;
                boolean isPriceSame = true;
                for (ProductModel newProduct : newProductsList) {
                    int cartItemIndex = cartProductList.indexOf(newProduct);
                    if (cartItemIndex != -1) {
                        newProduct.setQuantityCounter(cartProductList.get(cartItemIndex).getQuantityCounter());
                        String newProductModel = new Gson().toJson(newProduct);
                        SharedPrefsUtils.setStringPreference(CartActivity.this, newProduct.getItemId(), newProductModel);

                        if (binding.cartRecyclerView.getAdapter() != null) {
                            CartRecyclerViewAdapter cartRecyclerViewAdapter = (CartRecyclerViewAdapter) binding.cartRecyclerView.getAdapter();
                            cartRecyclerViewAdapter.updateItemInCart(newProduct);
                        }

                        if (!newProduct.isItemAvailability()) {
                            isOutOfStock = false;
                        }

                        Log.e("Cartactivity ", cartProductList.get(cartItemIndex).getItemBasePrice() + " ");
                        if (!newProduct.isPriceSame(cartProductList.get(cartItemIndex))) {
                            isPriceSame = false;
                        }

                    }//if new index
                } //for loop
                getShoppingCart();

                final boolean finalIsPriceSame = isPriceSame;
                Snackbar outOfStockAlert = Snackbar.make(binding.cartView, "Some products are out of stock, remove to proceed", Snackbar.LENGTH_INDEFINITE);
                outOfStockAlert.setBehavior(new SnackbarNoSwipeBehavior());
                outOfStockAlert.setActionTextColor(getResources().getColor(R.color.chartIdealBar));
                outOfStockAlert.setAction("remove", n -> {
                    Snackbar removeMessage = Snackbar.make(binding.cartView, "removing products", Snackbar.LENGTH_SHORT);
                    removeMessage.show();

                    for (ProductModel productModel : cartProductList) {
                        CartRecyclerViewAdapter cartRecyclerViewAdapter = (CartRecyclerViewAdapter) binding.cartRecyclerView.getAdapter();
                        if (cartRecyclerViewAdapter != null && !productModel.isItemAvailability()) {
                            cartRecyclerViewAdapter.deleteItemFromCart(productModel);
                            SharedPrefsUtils.removeValuePreference(CartActivity.this, productModel.getItemId());
                        }
                    }

                    getShoppingCart();

                    if (!finalIsPriceSame) {
                        showPriceChangeDialog();
                    } else {
                        checkDeliveryTimeAndAddress();
                    }

                });

                if (!isOutOfStock) {
                    outOfStockAlert.show();
                } else if (!isPriceSame) {
                    showPriceChangeDialog();
                } else {
                    checkDeliveryTimeAndAddress();
                }

                getShoppingCart();
            }

            private void getNext(String cat, String itemId, int index) {
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Products").child(cat).child(itemId);
                reference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists() && snapshot.getValue() != null) {
                            ProductModel productModel = snapshot.getValue(ProductModel.class);
                            onReceived(index + 1, productModel);
                            if (index + 1 == cartProductList.size()) {
                                onCompleted();
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });
    }

    private void showPriceChangeDialog() {
        MaterialAlertDialogBuilder materialAlertDialogBuilder = new MaterialAlertDialogBuilder(CartActivity.this);
        materialAlertDialogBuilder.setTitle("Product Price Alert");
        materialAlertDialogBuilder.setCancelable(false);
        materialAlertDialogBuilder.setMessage("Its look like that prices of products in your cart has been update, Please recheck the price before proceeding");
        materialAlertDialogBuilder.setPositiveButton("Next", (dialog, which) -> {
            checkDeliveryTimeAndAddress();
        });
        materialAlertDialogBuilder.setNegativeButton("back", (dialog, which) -> {

        });
        materialAlertDialogBuilder.show();
    }

    private void check(ProductCheckListener productCheckListener) {
        productCheckListener.onReceived(0, null);
    }

    private interface ProductCheckListener {
        void onReceived(int index, ProductModel productModel);

        void onCompleted();
    }

    private void checkDeliveryTimeAndAddress() {
        BottomSheetBehavior<View> bottomSheetBehavior = BottomSheetBehavior.from(binding.includeLayout.getRoot());
        String deliveryTime = null;
        Chip chip = findViewById(binding.includeLayout.chipGroup2.getCheckedChipId());

        if (chip != null) {
            deliveryTime = chip.getText().toString();
        }

        if (deliveryTime == null || deliveryTime.isEmpty()) {
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            Toast.makeText(this, "select delivery time", Toast.LENGTH_SHORT).show();
            return;
        }
        if (observableAddress.get().isEmpty()) {
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            Toast.makeText(this, "Refresh delivery address", Toast.LENGTH_SHORT).show();
            checkAddress();
            return;
        }

        showOrderConfirmationDialog();
    }

    final ObservableInt pcoinsUsed = new ObservableInt(0);
    final ObservableInt totalPcoins = new ObservableInt(0);

    private void showOrderConfirmationDialog() {
        LayoutConfirmOrderBinding confirmOrderBinding = DataBindingUtil.inflate(
                LayoutInflater.from(this),
                R.layout.layout_confirm_order,
                null,
                false);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this, R.style.AlertDialog).setView(confirmOrderBinding.getRoot());
        alertDialogBuilder.setCancelable(false);
        alertDialog = alertDialogBuilder.create();

        int total = getTotalCost();

        int deliveryCharge = getDeliveryCharge();
        if (deliveryCharge > 0) {
            total += deliveryCharge;
            confirmOrderBinding.totalPriceTextView.setText(String.format("Subtotal %s + %s (delivery charge) = %s", binding.amountToBePaid.getText().toString(), deliveryCharge+"", total));
        }else {
            confirmOrderBinding.totalPriceTextView.setText(String.format("Total price %s", binding.amountToBePaid.getText().toString()));
        }

        confirmOrderBinding.quantityTextView.setText(String.format("Total quantity %s", binding.getCartViewModel().getProductQuantitiesString()));
        alertDialog.show();

        confirmOrderBinding.successAnimation.setVisibility(View.GONE);

        final int finalTotal = total;
        confirmOrderBinding.applyPcoins.setOnClickListener(v -> {

            if (finalTotal < 100) {
                Toast.makeText(this, "pcoins or reward points used only on order 100 or above", Toast.LENGTH_SHORT).show();
                return;
            }

            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Customers").child(FirebaseAuth.getInstance().getUid()).child("referralReward").child("pcoins");
            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists() && snapshot.getValue() != null) {
                        int pcoins = snapshot.getValue(Long.class).intValue();
                        if (pcoins > 0) {
                            totalPcoins.set(pcoins);
                            int priceToBeCutOff = (finalTotal / 10);

                            confirmOrderBinding.pcoinsAlertText.setVisibility(View.VISIBLE);

                            if (finalTotal > 100) {
                                if (pcoins < priceToBeCutOff) {
                                    pcoinsUsed.set(pcoins);
                                    confirmOrderBinding.priceAfterPcoin.setText("final price: " + finalTotal + " - " + pcoins + " = " + (finalTotal - pcoins));
                                    confirmOrderBinding.priceAfterPcoin.setVisibility(View.VISIBLE);
                                } else if (pcoins > priceToBeCutOff) {
                                    pcoinsUsed.set(priceToBeCutOff);
                                    confirmOrderBinding.priceAfterPcoin.setText("final price: " + finalTotal + " - " + priceToBeCutOff + " = " + (finalTotal - priceToBeCutOff));
                                    confirmOrderBinding.priceAfterPcoin.setVisibility(View.VISIBLE);
                                }
                            }
                        } else {
                            Toast.makeText(CartActivity.this, "not have enough pcoins", Toast.LENGTH_SHORT);
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        });

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

        confirmOrderBinding.backBtn.setOnClickListener(n -> {
            alertDialog.dismiss();
            binding.includeLayout.chipGroup2.clearCheck();
            pcoinsUsed.set(0);
        });

        Chip chip = findViewById(binding.includeLayout.chipGroup2.getCheckedChipId());

        if (chip == null) {
            return;
        }
        String selectedDeliveryTime = chip.getText().toString();
        if (selectedDeliveryTime.equals("7:00 AM ~ 9:00AM")) {
            if (DateUtils.currentTimeIsAfter("7:00 AM")) {
                String text = "You have selected morning slot (" + selectedDeliveryTime + ") this order will be deliver next morning (" + DateUtils.getNextDate() + ")";
                SpannableString spannableString = new SpannableString(text);
                int startIndexOf = text.indexOf("(");
                int endIndexOf = text.indexOf(")");
                spannableString.setSpan(new BackgroundColorSpan(Color.YELLOW), startIndexOf, endIndexOf, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                spannableString.setSpan(new BackgroundColorSpan(Color.YELLOW), text.indexOf("(", startIndexOf + 1), text.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                confirmOrderBinding.deliveryNote.setText(spannableString);
            } else {
                confirmOrderBinding.deliveryNote.setText("Your order will be delivered by today");
                confirmOrderBinding.deliveryNote.setGravity(Gravity.CENTER);
            }
        } else if (selectedDeliveryTime.equals("4:00 PM ~ 6:00PM")) {
            if (DateUtils.currentTimeIsAfter("4:00 PM")) {
                String text = "You have selected evening slot (" + selectedDeliveryTime + ") this order will be deliver next evening (" + DateUtils.getNextDate() + ")";
                SpannableString spannableString = new SpannableString(text);
                int startIndexOf = text.indexOf("(");
                int endIndexOf = text.indexOf(")");
                spannableString.setSpan(new BackgroundColorSpan(Color.YELLOW), startIndexOf, endIndexOf, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                spannableString.setSpan(new BackgroundColorSpan(Color.YELLOW), text.indexOf("(", startIndexOf + 1), text.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                confirmOrderBinding.deliveryNote.setText(spannableString);
            } else {
                confirmOrderBinding.deliveryNote.setText("Your order will be delivered by today evening");
                confirmOrderBinding.deliveryNote.setGravity(Gravity.CENTER);
            }
        }
        binding.executePendingBindings();
        confirmOrderBinding.executePendingBindings();
    }

    private int getDeliveryCharge() {
        int deliveryCharge = 0;
        if (binding != null && binding.includeLayout.chipGroup2.getCheckedChipId() == R.id.chipDeliveryTime3)
            deliveryCharge = 39;

        return deliveryCharge;
    }

    private int getTotalCost() {
        int total = 0;
        for (ProductModel productModel : binding.getCartList()) {
            if (productModel.getItemSellPrice() > 0) {
                total += productModel.getItemSellPrice() * productModel.getQuantityCounter();
            } else {
                total += productModel.getItemBasePrice() * productModel.getQuantityCounter();
            }
        }
        return total;
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
            int calcTotal = 0;
            String key = FirebaseDatabase.getInstance().getReference("Orders").push().getKey();
            StringBuilder orderDetailsIds = new StringBuilder();
            for (ProductModel product : productCartList) {
                atomicOperation.put("OrdersDetails/" + key + "/" + product.getItemId(), new OrdersDetails(
                        product.getItemId(),
                        product.getItemThumbImage(),
                        product.getQuantityCounter(),
                        product.getItemBasePrice(),
                        product.getItemSellPrice(),
                        product.getItemCategory(),
                        product.getItemName()
                ));
                orderDetailsIds.append(product.getItemId()).append(" ");
                if (product.getItemSellPrice() > 0) {
                    calcTotal += (product.getItemSellPrice() * product.getQuantityCounter());
                } else {
                    calcTotal += (product.getItemBasePrice() * product.getQuantityCounter());
                }
            }

            long localTimestamp = System.currentTimeMillis();

            atomicOperation.put("Orders/" + key + "/userId", FirebaseAuth.getInstance().getUid());
            atomicOperation.put("Orders/" + key + "/orderId", key);
            atomicOperation.put("Orders/" + key + "/orderStatus", OrderStatus.ORDERED);
            atomicOperation.put("Orders/" + key + "/date", localTimestamp);
            atomicOperation.put("Orders/" + key + "/orderDetailsIds", orderDetailsIds.toString());
            atomicOperation.put("Orders/" + key + "/pcoinsSpent", pcoinsUsed.get());
            atomicOperation.put("Orders/" + key + "/address", observableAddress.get());

            int shipping = 0;
            Chip chip = findViewById(binding.includeLayout.chipGroup2.getCheckedChipId());
            if (chip != null) {
                atomicOperation.put("Orders/" + key + "/deliveryTime", chip.getText().toString());

                if (chip.getId() == binding.includeLayout.chipDeliveryTime3.getId()) {
                    if (calcTotal < 500) {
                        shipping += 39;
                    }
                }
            }

            atomicOperation.put("Orders/" + key + "/shipping", shipping);
            atomicOperation.put("Orders/" + key + "/subTotal", calcTotal);

            atomicOperation.put("UserOrders/" + FirebaseAuth.getInstance().getUid() + "/" + key + "/date", ServerValue.TIMESTAMP);
            atomicOperation.put("UserOrders/" + FirebaseAuth.getInstance().getUid() + "/" + key + "/date_orderId", localTimestamp + "_" + key);

            if (pcoinsUsed.get() > 0)
                atomicOperation.put("Customers/" + FirebaseAuth.getInstance().getUid() + "/" + "referralReward" + "/" + "pcoins", totalPcoins.get() - pcoinsUsed.get());
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
            cartRecyclerViewAdapter.deleteItemFromCart(productModel);
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
            } else {
                window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
            }
            window.setStatusBarColor(Color.TRANSPARENT);
        }
    }

    AirLocation airLocation;

    public void getCurrentLocation() {
        binding.includeLayout.radioGroup.clearCheck();
        airLocation = new AirLocation(CartActivity.this, new AirLocation.Callback() {
            @Override
            public void onSuccess(@NotNull ArrayList<Location> arrayList) {
                for (Location location : arrayList) {
                    if (location.getLatitude() != 0 && location.getLatitude() != 0) {
                        binding.includeLayout.btmSheetCipCurrentLocation.setText("Location Found");
                        Toast.makeText(CartActivity.this, "location found", Toast.LENGTH_SHORT).show();
                        if (binding.getCartViewModel() != null) {
                            observableAddress.set(location.getLatitude() + "," + location.getLongitude());
                        }
                    }
                }
            }

            @Override
            public void onFailure(@NotNull AirLocation.LocationFailedEnum locationFailedEnum) {
            }
        }, true, 500, "permission is required to get the current location");
        airLocation.start();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 1243 && permissions[0].equals(Manifest.permission.ACCESS_FINE_LOCATION)) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                binding.includeLayout.btmSheetCipCurrentLocation.setText("Request Location");
            } else {
                binding.includeLayout.btmSheetCipCurrentLocation.setText("Request Permission");
            }
        }

        if (requestCode == 1243 && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)) {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(this)
                    .setTitle("Location Permission")
                    .setMessage("this Permission required for deliver at the right location, you can enable it by going to the Setting -> permission")
                    .setPositiveButton("Open", (dialog, which) -> {
                        Intent intent = new Intent();
                        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        intent.setData(Uri.fromParts("package", getPackageName(), null));
                        startActivity(intent);
                    });
            alertDialog.show();
        }


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        airLocation.onActivityResult(requestCode, resultCode, data);
    }


    @Override
    public void onStart() {
        super.onStart();
    }
}
