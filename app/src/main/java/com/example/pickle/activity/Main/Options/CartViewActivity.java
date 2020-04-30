package com.example.pickle.activity.Main.Options;

import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.example.pickle.Adapters.CartRecyclerViewAdapter;
import com.example.pickle.LocationTracker;
import com.example.pickle.R;
import com.example.pickle.activity.Main.MainActivity;
import com.example.pickle.binding.IMainActivity;
import com.example.pickle.data.Address;
import com.example.pickle.data.CartCalculator;
import com.example.pickle.data.CartViewModel;
import com.example.pickle.data.CustomerOrdersData;
import com.example.pickle.data.ProductModel;
import com.example.pickle.databinding.ActivityCartTestViewBinding;
import com.example.pickle.ui.CustomRadioButton;
import com.example.pickle.utils.SharedPrefsUtils;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;


public class CartViewActivity extends AppCompatActivity implements IMainActivity {

    private ActivityCartTestViewBinding binding;
    private BottomSheetBehavior _bottomSheetBehavior;

    private TextView _cartAmount;
    private TextView _amountToBePaid;
    private TextView _deliveryPrice;
//    private ImageButton _placeOrderBtn;
    private CustomRadioButton customRadioButton;

    public final int[] anIntCartAmount = new int[1];
    private View _bottomSheet;

    private String deliveryTime = "";
    private String deliveryAddress = "";

    private Chip _childDeliveryTime1;
    private Chip _childDeliveryTime2;
    private RadioButton address1;
    private Chip _currentLocationBtn;
    private RadioGroup radioGroup;

    private void initFields() {
        _amountToBePaid = binding.amountToBePaid;
        _deliveryPrice = binding.deliveryPrice;
//        _placeOrderBtn = binding.placeOrderBtn;
        _bottomSheet = binding.includeLayout;
        _cartAmount = binding.cartAmountTextView;

        _childDeliveryTime1 = _bottomSheet.findViewById(R.id.chipDeliveryTime1);
        _childDeliveryTime2 = _bottomSheet.findViewById(R.id.chipDeliveryTime2);

        _currentLocationBtn = _bottomSheet.findViewById(R.id.btm_sheet_cip_current_location);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_cart_test_view);


        initFields();
        emptyCart();

        init_bottomSheet();
        init_customRadioButton();
        init_loadAddress();

        getShoppingCart();

        location_setup();

        _childDeliveryTime1.setOnClickListener(n -> {
            deliveryTime = _childDeliveryTime1.getText().toString();
        });

        _childDeliveryTime2.setOnClickListener(n -> {
            deliveryTime = _childDeliveryTime2.getText().toString();
        });
    }

    private void emptyCart() {
        binding.setActivity(this);
        ChipGroup group = binding.chipGroup;
        group.setOnCheckedChangeListener((group1, checkedId) -> {
            Chip chipSelected = findViewById(group1.getCheckedChipId());
            Log.e("CartViewActivity", chipSelected.getText().toString());
            switch (chipSelected.getText().toString()) {
                case "Vegitables":
                    navigateTo(R.id.action_orderFragment_to_vegetableFragment);
                    break;
                case "Fruits":
                    navigateTo(R.id.action_orderFragment_to_fruitsFragment);
                    break;
                case "Diary":
                    navigateTo(R.id.action_orderFragment_to_dairyFragment);
                    break;
                case "Beverages":
                    navigateTo(R.id.action_orderFragment_to_beveragesFragment);
                    break;
            }
        });
    }

    private void navigateTo(int navigationId) {
        startActivity(new Intent(CartViewActivity.this, MainActivity.class).putExtra("NAVIGATION_ID", navigationId));
        finish();
    }

    private void init_loadAddress() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Addresses/" + FirebaseAuth.getInstance().getUid());
        reference.child("slot1").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Address address = dataSnapshot.getValue(Address.class);
                if (address != null)
                    address1.setText(address.toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void init_bottomSheet() {
        _bottomSheetBehavior = BottomSheetBehavior.from(_bottomSheet);
        _bottomSheetBehavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (newState == BottomSheetBehavior.STATE_EXPANDED) {
                //todo do amination here
                } else {
                    //todo do animation here
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                if (slideOffset > 0 && slideOffset < 0.85) {
                    Log.e("blur cart View Activity", "count " + slideOffset);
                }
            }
        });
    }

    private void init_customRadioButton() {
        radioGroup = _bottomSheet.findViewById(R.id.radioGroup);
        address1 = _bottomSheet.findViewById(R.id.address_slot_1);

        radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            Toast.makeText(CartViewActivity.this, "radio group"+ checkedId, Toast.LENGTH_SHORT).show();
            switch (checkedId) {
                case R.id.address_slot_1:
                    deliveryAddress = address1.getText().toString();
                    break;
            }
        });
    }

    private void placeOrder(ProductModel pm) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Orders").child(FirebaseAuth.getInstance().getUid());

        CustomerOrdersData customerOrdersData = new CustomerOrdersData(
                pm.getQuantityCounter(),
                pm.getItemId(),
                pm.getItemBasePrice(),
                "ordered",
                pm.getItemCategory(),
                deliveryAddress,
                FirebaseAuth.getInstance().getUid(),
                new SimpleDateFormat("yyyy.MM.dd 'at' HH:mm:ss").format(new Date()),
                deliveryTime);

        ref.child(pm.getItemId()).setValue(customerOrdersData).addOnSuccessListener(aVoid -> {
            Toast.makeText(this, "item ordered", Toast.LENGTH_SHORT).show();
        }).addOnFailureListener(e -> {
            Toast.makeText(this, "error : " + e.getMessage(), Toast.LENGTH_SHORT).show();
        });
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

    /*
     *  Current location code
     * */

    private ArrayList permissionsToRequest;
    private ArrayList permissionsRejected = new ArrayList();
    private ArrayList permissions = new ArrayList();

    private final static int ALL_PERMISSIONS_RESULT = 101;
    LocationTracker locationTrack;

    private void location_setup() {

        permissions.add(ACCESS_FINE_LOCATION);
        permissions.add(ACCESS_COARSE_LOCATION);

        permissionsToRequest = findUnAskedPermissions(permissions);
        //get the permissions we have asked for before but are not granted..
        //we will store this in a global list to access later.


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (permissionsToRequest.size() > 0)
                requestPermissions((String[]) permissionsToRequest.toArray(new String[permissionsToRequest.size()]), ALL_PERMISSIONS_RESULT);
        }

        _currentLocationBtn.setOnClickListener(n -> {

            radioGroup.clearCheck();
            deliveryAddress = "";
            locationTrack = new LocationTracker(CartViewActivity.this);

            if (locationTrack.canGetLocation()) {

                double longitude = locationTrack.getLongitude();
                double latitude = locationTrack.getLatitude();

                //open google map for longitude and latitude

                //Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("geo:"+ latitude +"," + longitude));
                //intent.setPackage("com.google.android.apps.maps");

                if (latitude > 0 && longitude> 0) {
                    deliveryAddress = latitude +","+longitude;
                }
//                startActivity(intent);
//                Toast.makeText(CartViewActivity.this, deliveryAddress, Toast.LENGTH_SHORT).show();

            }else  {
                locationTrack.showSettingsAlert();
            }
        });
    }

    private ArrayList findUnAskedPermissions(ArrayList wanted) {
        ArrayList result = new ArrayList();

        for (Object perm : wanted) {
            if (!hasPermission((String) perm)) {
                result.add(perm);
            }
        }

        return result;
    }

    private boolean hasPermission(String permission) {
        if (canMakeSmores()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                return (checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED);
            }
        }
        return true;
    }

    private boolean canMakeSmores() {
        return (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1);
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        switch (requestCode) {

            case ALL_PERMISSIONS_RESULT:
                for (Object perms : permissionsToRequest) {
                    if (!hasPermission((String) perms)) {
                        permissionsRejected.add(perms);
                    }
                }

                if (permissionsRejected.size() > 0) {


                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (shouldShowRequestPermissionRationale((String) permissionsRejected.get(0))) {
                            showMessageOKCancel("These permissions are mandatory for the application. Please allow access.",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                requestPermissions((String[]) permissionsRejected.toArray(new String[permissionsRejected.size()]), ALL_PERMISSIONS_RESULT);
                                            }
                                        }
                                    });
                            return;
                        }
                    }

                }

                break;
        }

    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (locationTrack != null) {
            locationTrack.stopListener();
        }
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
        ((CartRecyclerViewAdapter)binding.cartRecyclerView.getAdapter()).updateCartItemsList(productModel);
    }

}
