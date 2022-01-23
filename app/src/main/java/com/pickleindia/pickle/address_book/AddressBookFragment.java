package com.pickleindia.pickle.address_book;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.RadioButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import com.google.firebase.auth.FirebaseAuth;
import com.pickleindia.pickle.R;
import com.pickleindia.pickle.cart.CartViewModel;
import com.pickleindia.pickle.databinding.FragmentAddressBookBinding;
import com.pickleindia.pickle.databinding.LayoutAddressTileBinding;
import com.pickleindia.pickle.models.ProductModel;
import com.pickleindia.pickle.ui.addAddress.CustomerDetailActivity;
import com.pickleindia.pickle.ui.addAddress.model.AddAddressRequest;
import com.pickleindia.pickle.utils.SharedPrefsUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static com.pickleindia.pickle.utils.Constant.OFFER_COMBO;


public class AddressBookFragment extends AppCompatActivity {

    private AddressBookViewModel addressBookViewModel;
    private FragmentAddressBookBinding fragmentAddressBookBinding;

    public AddressBookFragment() {
        // Required empty public constructor
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        fragmentAddressBookBinding = DataBindingUtil.setContentView(
                this,
                R.layout.fragment_address_book
        );
        fragmentAddressBookBinding.setActivity(this);

        addressBookViewModel = new ViewModelProvider(this).get(AddressBookViewModel.class);
//        fragmentAddressBookBinding.includeSlot.cardView.setOnClickListener(n -> startActivity(new Intent(this, CustomerDetailActivity.class).putExtra("UPDATE_ADDRESS", true)));

        /* fragmentAddressBookBinding.swipeToRefresha.setOnRefreshListener(() -> {
            loadAddress();
            if (fragmentAddressBookBinding.swipeToRefreshText.getVisibility() == View.VISIBLE)
                fragmentAddressBookBinding.swipeToRefreshText.setVisibility(View.GONE);
        });*/
        fragmentAddressBookBinding.fabAddAddress.setOnClickListener(v -> {
            startActivity(new Intent(this,
                    CustomerDetailActivity.class)
                    .addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
                    .putExtra("address", addressBookViewModel.getUserAddressLiveData().getValue()));
        });
        getShoppingCart();
    }


    private void loadAddress() {
        addressBookViewModel.getAddressFromFirebaseDatabase();
        Log.e(getClass().getName(), "loadAddress: " + FirebaseAuth.getInstance().getUid());
        addressBookViewModel.getUserAddressLiveData().observe(this, address -> {
            Log.e(getClass().getName(), "loadAddress: " + address.getData());
            fragmentAddressBookBinding.setAddress(address.toString());
            fragmentAddressBookBinding.addressRadioGroup.removeAllViews();
            for (AddAddressRequest.Data data : address.getData()) {
//                RadioButton button = new RadioButton(this);
//                button.setId(new Random().nextInt(1000));
//                button.setBackgroundResource(R.drawable.dashed_rectangle);
//                button.setText(data.toString());
                LayoutAddressTileBinding addressTileBinding = LayoutAddressTileBinding.inflate(this.getLayoutInflater(), fragmentAddressBookBinding.addressRadioGroup, false);
                addressTileBinding.rb.setText(data.toString());
                fragmentAddressBookBinding.addressRadioGroup.addView(addressTileBinding.getRoot());
            }
            fragmentAddressBookBinding.addressRadioGroup.invalidate();
//            fragmentAddressBookBinding.swipeToRefresh.setRefreshing(false);
        });
    }

    private void getShoppingCart() {
        List<ProductModel> cartList = SharedPrefsUtils.getAllProducts(this);
        CartViewModel cartViewModel = new CartViewModel();
        cartViewModel.setCartProducts(cartList);
        fragmentAddressBookBinding.setCartViewModel(cartViewModel);
        ArrayList<ProductModel> offerComboList = getIntent().getParcelableArrayListExtra(OFFER_COMBO);
        if (offerComboList != null)
            cartList.addAll(offerComboList);

        fragmentAddressBookBinding.executePendingBindings();
    }


    @Override
    public void onResume() {
        super.onResume();
        fragmentAddressBookBinding.swipeToRefreshText.setVisibility(View.VISIBLE);
        loadAddress();
        new Handler().postDelayed(() -> {
            if (fragmentAddressBookBinding != null) {
                fragmentAddressBookBinding.swipeToRefreshText.setVisibility(View.GONE);
            }
        }, 8000);
    }

    @Override
    public void onBackPressed() {
        finish();
//        super.onBackPressed();
    }
}