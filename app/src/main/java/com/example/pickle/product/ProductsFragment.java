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
import com.example.pickle.interfaces.IFragmentCb;
import com.example.pickle.cart.CartViewModel;
import com.example.pickle.main.FirebaseSearchActivity;
import com.example.pickle.models.ProductModel;
import com.example.pickle.databinding.FragmentProductsBinding;
import com.example.pickle.utils.BundleUtils;
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
import java.util.Iterator;

import static com.example.pickle.utils.Constant.PRODUCT;
import static com.example.pickle.utils.Constant.PRODUCT_BUNDLE;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProductsFragment extends Fragment implements IFragmentCb {

    private DatabaseReference reference;
    private ChildEventListener childEventListener;

    private ArrayList<ProductModel> fruitList;
    private FragmentProductsBinding productBinding;
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
        if (bundle != null && bundle.containsKey(PRODUCT_BUNDLE)) {
            reference = FirebaseDatabase.getInstance().getReference(PRODUCT).child(bundle.getString(PRODUCT_BUNDLE));
        }

        fruitList = new ArrayList<>();
        new Handler().postDelayed(this::populateList,1200);

        productBinding.setProductList(fruitList);
        productBinding.setActivity(getActivity());
        productBinding.setBundle(BundleUtils.setNavigationBundle(bundle.getString(PRODUCT_BUNDLE)));
        productBinding.searchCardview.setOnClickListener(n -> startActivity(new Intent(getActivity(), FirebaseSearchActivity.class).addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)));
        productBinding.ibOverlay.setOnClickListener(n -> startActivity(new Intent(getActivity(), CartActivity.class).addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)));

        changeStatusBarColor();

        return productBinding.getRoot();
    }

    private void populateList() {

        if (reference == null) {
            reference = FirebaseDatabase.getInstance().getReference(PRODUCT);
        }

        Query query = reference.orderByChild("itemName").limitToLast(15);

        childEventListener = query.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    if (dataSnapshot.exists()) {
                        ProductModel product = dataSnapshot.getValue(ProductModel.class);
                        try {
                            String cartProductJson = SharedPrefsUtils.getStringPreference(getContext(), product.getItemId(), 0);
                            ProductModel cartProduct = new Gson().fromJson(cartProductJson, ProductModel.class);
                            if (product.equals(cartProduct))
                                product.setQuantityCounter(cartProduct.getQuantityCounter());

                            countItems += 1;
                            if (countItems > 0)
                                productBinding.countFruits.setText(countItems + " items");
                            else
                                productBinding.countFruits.setText(countItems + " item");


                            fruitList.add(product);
                            notifyChanges();
                        } catch (NullPointerException npe) {
                            Log.e(ProductsFragment.class.getName(), "npe " + npe);
                        }
                    }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                int position = 0;
                Iterator<ProductModel> iterator = fruitList.iterator();
                while (iterator.hasNext()) {
                    position++;
                    ProductModel product = iterator.next();
                    ProductModel newProduct = dataSnapshot.getValue(ProductModel.class);
                    if (product.equals(newProduct)) {
                        iterator.remove();
                        break;
                    }
                }

                ProductModel newProduct = dataSnapshot.getValue(ProductModel.class);

                if (newProduct != null) {

                    //on item changed get default saved product and set saved quantity counted to the new product
                    String cartProductJson = SharedPrefsUtils.getStringPreference(getContext(), newProduct.getItemId(), 0);
                    ProductModel cartProduct = new Gson().fromJson(cartProductJson, ProductModel.class);

                    if (newProduct.equals(cartProduct))
                            newProduct.setQuantityCounter(cartProduct.getQuantityCounter());
                    //closed

                    if ((position - 1) <= fruitList.size()) {
                        fruitList.add(position - 1, newProduct);
                        notifyChanges();
                    } else {
                        fruitList.add(newProduct);
                        notifyChanges();
                    }
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
                Log.e("OrdersFirebaseQata", databaseError.getCode() +" ");
            }
        });
    }

    private void notifyChanges() {
        try {
            productBinding.fruitsRecyclerView.getAdapter().notifyDataSetChanged();
        } catch (NullPointerException npe) {
            Log.e("FruitFragment", "npe exception " + npe.getMessage());
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        ArrayList<ProductModel> refreshList = SharedPrefsUtils.getAllProducts(getActivity());
        for (ProductModel product : fruitList) {
            if (refreshList.contains(product)) {
                ProductModel newProduct = refreshList.get(refreshList.indexOf(product));
                product.setQuantityCounter(newProduct.getQuantityCounter());
                notifyChanges();
            } else {
                product.setQuantityCounter(0);
                notifyChanges();
            }
        }

        CartViewModel cartViewModel = new CartViewModel();
        cartViewModel.setCartProducts(fruitList);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (childEventListener != null) {
            reference.removeEventListener(childEventListener);
        }
    }

    private void changeStatusBarColor() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                Window window = getActivity().getWindow();
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.setStatusBarColor(getActivity().getResources().getColor(R.color.colorGrey50));
            }
        } catch (Exception xe) {
            Log.e(ProductsFragment.class.getName(), "error "+xe.getMessage());
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
