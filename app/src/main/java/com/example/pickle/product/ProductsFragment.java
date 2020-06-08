package com.example.pickle.product;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.airbnb.lottie.LottieAnimationView;
import com.example.pickle.R;
import com.example.pickle.cart.CartActivity;
import com.example.pickle.cart.CartViewModel;
import com.example.pickle.databinding.FragmentProductsBinding;
import com.example.pickle.interfaces.IFragmentCb;
import com.example.pickle.main.FirebaseSearchActivity;
import com.example.pickle.models.ProductModel;
import com.example.pickle.utils.NotifyRecyclerItems;
import com.example.pickle.utils.SharedPrefsUtils;
import com.google.android.material.transition.MaterialSharedAxis;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import static com.example.pickle.utils.Constant.PRODUCT;
import static com.example.pickle.utils.Constant.PRODUCT_TYPE;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProductsFragment extends Fragment implements IFragmentCb {

    private FragmentProductsBinding productBinding;
    private ArrayList<ProductModel> productsArrayList;

    private DatabaseReference productDatabaseReference;
    private ChildEventListener productChildEventListener;

    private int countItems;

    public ProductsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        productBinding = DataBindingUtil.inflate(
                inflater,
                R.layout.fragment_products,
                container,
                false
        );

        setEnterTransition(MaterialSharedAxis.create(MaterialSharedAxis.X, true));

        Bundle bundle = getArguments();
        String childReference = bundle != null && bundle.containsKey(PRODUCT_TYPE) ? bundle.getString(PRODUCT_TYPE) : " ";
        productDatabaseReference = FirebaseDatabase.getInstance().getReference(PRODUCT).child(childReference != null ? childReference : " ");

        productsArrayList = new ArrayList<>();
        new Handler().postDelayed(this::populateList, 800);

        productBinding.setProductList(productsArrayList);
        productBinding.setActivity(getActivity());
        productBinding.setType(childReference);
        productBinding.searchCardview.setOnClickListener(n -> startActivity(new Intent(getActivity(), FirebaseSearchActivity.class).addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)));
        productBinding.icCart.setOnClickListener(n -> startActivity(new Intent(getActivity(), CartActivity.class).addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)));

        changeStatusBarColor();

        return productBinding.getRoot();
    }

    private void populateList() {
        if (productDatabaseReference == null)
            productDatabaseReference = FirebaseDatabase.getInstance().getReference(PRODUCT);

        Query query = productDatabaseReference.orderByChild("itemName").limitToLast(15);
        productChildEventListener = query.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                ProductModel product = dataSnapshot.getValue(ProductModel.class);
                if (product != null) {
                    String cartProductJson = SharedPrefsUtils.getStringPreference(getContext(), product.getItemId(), 0);
                    ProductModel cartProduct = new Gson().fromJson(cartProductJson, ProductModel.class);

                    if (product.equals(cartProduct))
                        product.setQuantityCounter(cartProduct.getQuantityCounter());

                    productsArrayList.add(product);
                    NotifyRecyclerItems.notifyItemInsertedAt(productBinding.recyclerView, productsArrayList.size());

                    countItems += 1;
                    String itemsCountedText;
                    if (countItems > 0) itemsCountedText = countItems + " items";
                    else itemsCountedText = countItems + " item";
                    productBinding.countFruits.setText(itemsCountedText);
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                //new implementation /2 june 2020
                ProductModel newProduct = dataSnapshot.getValue(ProductModel.class);
                if (productsArrayList.contains(newProduct)) {
                    int indexOfOldProduct = productsArrayList.indexOf(newProduct);
                    ProductModel oldProduct = productsArrayList.get(indexOfOldProduct);
                    productsArrayList.remove(oldProduct);
                    productsArrayList.add(indexOfOldProduct, newProduct);
                    NotifyRecyclerItems.notifyItemChangedAt(productBinding.recyclerView, indexOfOldProduct);
                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(ProductsFragment.class.getName(), databaseError.getCode() + " ");
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        // This method work when user navigate to the CartActivity and
        // changes qty in the cart from CartActivity and come back to
        // product fragment then this fragment is resume and update the
        // changed product
        ArrayList<ProductModel> cartArrayList = SharedPrefsUtils.getAllProducts(getActivity());
        for (ProductModel product : productsArrayList) {
            if (cartArrayList.contains(product)) {
                ProductModel newProduct = cartArrayList.get(cartArrayList.indexOf(product));
                product.setQuantityCounter(newProduct.getQuantityCounter());
            } else
                product.setQuantityCounter(0);

            NotifyRecyclerItems.notifyItemChangedAt(productBinding.recyclerView, productsArrayList.indexOf(product));
        }

        CartViewModel cartViewModel = new CartViewModel();
        cartViewModel.setCartProducts(productsArrayList);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (productChildEventListener != null) {
            productDatabaseReference.removeEventListener(productChildEventListener);
        }
    }

    private void changeStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && getActivity() != null) {
            Window window = getActivity().getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getActivity().getResources().getColor(R.color.colorGrey50));
        }
    }

    @Override
    public void play() {
        LottieAnimationView lottieCart = productBinding.cartAnim;
        lottieCart.setMinAndMaxProgress(.20f, 1f);
        lottieCart.playAnimation();
    }

    @Override
    public void updateIconItems() {}
}
