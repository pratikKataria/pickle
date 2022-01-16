package com.pickleindia.pickle.address_book;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import com.pickleindia.pickle.R;
import com.pickleindia.pickle.auth.Login.CustomerDetailActivity;
import com.pickleindia.pickle.cart.CartViewModel;
import com.pickleindia.pickle.databinding.FragmentAddressBookBinding;
import com.pickleindia.pickle.models.ProductModel;
import com.pickleindia.pickle.utils.SharedPrefsUtils;

import java.util.ArrayList;
import java.util.List;

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
        loadAddress();

//        fragmentAddressBookBinding.includeSlot.cardView.setOnClickListener(n -> startActivity(new Intent(this, CustomerDetailActivity.class).putExtra("UPDATE_ADDRESS", true)));

        fragmentAddressBookBinding.swipeToRefresh.setOnRefreshListener(() -> {
            loadAddress();
            if (fragmentAddressBookBinding.swipeToRefreshText.getVisibility() == View.VISIBLE)
                fragmentAddressBookBinding.swipeToRefreshText.setVisibility(View.GONE);
        });
        getShoppingCart();
    }


    private void loadAddress() {
        addressBookViewModel.getAddressFromFirebaseDatabase();
        addressBookViewModel.getUserAddressLiveData().observe(this, address -> {
            fragmentAddressBookBinding.setAddress(address.toString());
            fragmentAddressBookBinding.swipeToRefresh.setRefreshing(false);
        });
    }

    private void getShoppingCart() {
        List<ProductModel> cartList = SharedPrefsUtils.getAllProducts(this);
//        fragmentAddressBookBinding.(cartList);
        CartViewModel cartViewModel = new CartViewModel();
        cartViewModel.setCartProducts(cartList);
        fragmentAddressBookBinding.setCartViewModel(cartViewModel);
//        cartViewModel.setComboValue(getComboPrice());
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