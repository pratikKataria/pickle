package com.example.pickle.activity.Main.Options;

import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pickle.Adapters.CartRecyclerViewAdapter;
import com.example.pickle.LocationTracker;
import com.example.pickle.R;
import com.example.pickle.data.Address;
import com.example.pickle.data.CartCalculator;
import com.example.pickle.data.CustomerOrdersData;
import com.example.pickle.data.ProductModel;
import com.example.pickle.databinding.ActivityCartTestViewBinding;
import com.example.pickle.ui.CustomRadioButton;
import com.example.pickle.utils.PriceFormatUtils;
import com.example.pickle.utils.SharedPrefsUtils;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import io.alterac.blurkit.BlurLayout;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;


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
    private View _bottomSheet;

    private String deliveryTime = "";
    private String deliveryAddress = "";

    private Chip _childDeliveryTime1;
    private Chip _childDeliveryTime2;
    private RadioButton address1;
    private Chip _currentLocationBtn;
    private RadioGroup radioGroup;

    private void initFields() {

        _activityCartTestViewBinding = DataBindingUtil.setContentView(this, R.layout.activity_cart_test_view);


        _cartRecyclerView = _activityCartTestViewBinding.cartRecyclerView;
        _amountToBePaid = _activityCartTestViewBinding.amountToBePaid;
        _deliveryPrice = _activityCartTestViewBinding.deliveryPrice;
        _placeOrderBtn = _activityCartTestViewBinding.placeOrderBtn;
        blurLayout = _activityCartTestViewBinding.blurLayout;

        _bottomSheet = _activityCartTestViewBinding.includeLayout;
        _cartAmount = _activityCartTestViewBinding.cartAmountTextView;

        _childDeliveryTime1 = _bottomSheet.findViewById(R.id.chipDeliveryTime1);
        _childDeliveryTime2 = _bottomSheet.findViewById(R.id.chipDeliveryTime2);

        _currentLocationBtn = _bottomSheet.findViewById(R.id.btm_sheet_cip_current_location);

        blurLayout.setAlpha(0);
        cartList = new ArrayList<>();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initFields();

        init_bottomSheet();
        init_customRadioButton();
        init_loadAddress();

        populateList();
        init_recyclerView();

        location_setup();

        _cartCalculator = new CartCalculator();
        _cartCalculator.cartAmount((ArrayList<ProductModel>) cartList);
        int amount = _cartCalculator.getCartAmount();

        _cartAmount.setText(amount + "");
        _placeOrderBtn.setOnClickListener(n -> {
            for (ProductModel pm : cartList) {

                if (deliveryTime.isEmpty()) {
                    Toast.makeText(this, "select delivery time", Toast.LENGTH_SHORT).show();
                    _bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                    return;
                }

                if (deliveryAddress.isEmpty()) {
                    Toast.makeText(this, "select delivery address", Toast.LENGTH_SHORT).show();
                    _bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                    return;
                }

                if (pm != null) {
                    placeOrder(pm);
                }
            }
        });

        _childDeliveryTime1.setOnClickListener(n -> {
            deliveryTime = _childDeliveryTime1.getText().toString();
        });

        _childDeliveryTime2.setOnClickListener(n -> {
            deliveryTime = _childDeliveryTime2.getText().toString();
        });
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
//                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("geo:"+ latitude +"," + longitude));
//                intent.setPackage("com.google.android.apps.maps");
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
        locationTrack.stopListener();
    }

}
