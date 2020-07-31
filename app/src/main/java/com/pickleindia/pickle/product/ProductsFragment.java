package com.pickleindia.pickle.product;

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
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ObservableBoolean;
import androidx.fragment.app.Fragment;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.pickleindia.pickle.R;
import com.pickleindia.pickle.adapters.CategoryRecyclerViewAdapter;
import com.pickleindia.pickle.cart.CartActivity;
import com.pickleindia.pickle.cart.CartViewModel;
import com.pickleindia.pickle.databinding.FragmentProductsBinding;
import com.pickleindia.pickle.databinding.LayoutProductCategoryChipBinding;
import com.pickleindia.pickle.interfaces.IFragmentCb;
import com.pickleindia.pickle.main.FirebaseSearchActivity;
import com.pickleindia.pickle.models.ProductModel;
import com.pickleindia.pickle.utils.NotifyRecyclerItems;
import com.pickleindia.pickle.utils.PriceFormatUtils;
import com.pickleindia.pickle.utils.RecyclerScrollListener;
import com.pickleindia.pickle.utils.SharedPrefsUtils;
import com.google.android.material.transition.MaterialSharedAxis;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;

import static com.pickleindia.pickle.utils.Constant.PRODUCT;
import static com.pickleindia.pickle.utils.Constant.PRODUCT_TYPE;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProductsFragment extends Fragment implements IFragmentCb, RecyclerScrollListener.LastItemVisibleListener {

    private FragmentProductsBinding productBinding;
    private ArrayList<ProductModel> productsArrayList;

    private int LIMIT = 10;
    private final ObservableBoolean isLoading = new ObservableBoolean(false);

    private DatabaseReference productDatabaseReference;
    private ChildEventListener productChildEventListener;

    private int countItems;

    private final RecyclerScrollListener recyclerScrollListener = new RecyclerScrollListener(this);

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

        final String cat = getCategory();
        productDatabaseReference = FirebaseDatabase.getInstance().getReference(PRODUCT).child(cat);

        productsArrayList = new ArrayList<>();
        new Handler().postDelayed(this::populateList, 400);

        productBinding.setProductList(productsArrayList);
        productBinding.setActivity(getActivity());
        productBinding.setType(cat);
        productBinding.searchCardview.setOnClickListener(n -> startActivity(new Intent(getActivity(), FirebaseSearchActivity.class).addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)));
        productBinding.icCart.setOnClickListener(n -> startActivityForResult(new Intent(getActivity(), CartActivity.class).addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY),1));
        productBinding.recyclerView.addOnScrollListener(recyclerScrollListener);
        productBinding.setIsLoading(isLoading);

        changeStatusBarColor();
        getSublist(cat);

        productBinding.chipGroup.setOnCheckedChangeListener((group, checkedId) -> {
            //if some widget is unchecked it return -1 as id
            if (checkedId != -1) {

                Chip chip = getActivity().findViewById(group.getCheckedChipId());
                if (chip == null) return;

                if (chip.getText().toString().equals("All")) {
                    Log.e("ProductFragment ", checkedId + " checked id " + chip.getText().toString());
                    productsArrayList.clear();
                    productBinding.recyclerView.getAdapter().notifyDataSetChanged();

                    if (productDatabaseReference != null && productChildEventListener != null) {
                        productDatabaseReference.removeEventListener(productChildEventListener);
                        productChildEventListener = null;
                    }

                    populateList();
                    return;
                }

                productBinding.recyclerView.removeOnScrollListener(recyclerScrollListener);
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Products").child(cat);
                Query query = reference.orderByChild("itemSubCategory").equalTo(chip.getText().toString());
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot == null) return;

                        CategoryRecyclerViewAdapter categoryRecyclerViewAdapter = (CategoryRecyclerViewAdapter) productBinding.recyclerView.getAdapter();
                        if (categoryRecyclerViewAdapter == null) return;
                        productsArrayList.clear();
                        categoryRecyclerViewAdapter.notifyDataSetChanged();
                        for (DataSnapshot dataSnapshot : snapshot.getChildren())
                            addProduct(dataSnapshot.getValue(ProductModel.class));
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
                            productsArrayList.sort((o1, o2) -> o1.getItemName().compareTo(o2.getItemName()));

                        categoryRecyclerViewAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }
        });

        return productBinding.getRoot();
    }

    private String getCategory() {
        String cat = "";
        Bundle bundle = getArguments();
        if (bundle != null && bundle.containsKey(PRODUCT_TYPE)) {
            cat = bundle.getString(PRODUCT_TYPE, "");
        }
        return cat;
    }

    private void getSublist(String cat) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("ProductCategories").child(cat).child("subCategory");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String categories = snapshot.getValue(String.class);
                if (categories == null) return;

                for (String category : categories.split(" ")) {
                    LayoutProductCategoryChipBinding bind = DataBindingUtil.inflate(
                            getLayoutInflater(),
                            R.layout.layout_product_category_chip,
                            null,
                            false
                    );
                    bind.setName(category);
                    productBinding.chipGroup.addView(bind.getRoot());
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void populateList() {

        productBinding.recyclerView.clearOnScrollListeners();
        productBinding.recyclerView.addOnScrollListener(recyclerScrollListener);
        Log.e("ProductsFragment", "populate List called");

        Query query = productDatabaseReference.orderByChild("itemName_itemId").limitToFirst(LIMIT);
        productChildEventListener = query.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                Log.e("ProductsFragment", "String key " + s);

                ProductModel product = dataSnapshot.getValue(ProductModel.class);
                if (product != null) {
                    addProduct(product);
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

    public void addProduct(ProductModel product) {
        if (product == null) return;

        String cartProductJson = SharedPrefsUtils.getStringPreference(getContext(), product.getItemId(), 0);
        ProductModel cartProduct = new Gson().fromJson(cartProductJson, ProductModel.class);

        if (product.equals(cartProduct))
            product.setQuantityCounter(cartProduct.getQuantityCounter());

        productsArrayList.add(product);
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
    public void updateIconItems() {
    }

    @Override
    public void loadMoreItems() {
        int currentListSize = productsArrayList.size();

        //if already loading
        if (!isLoading.get()) {
            isLoading.set(true);
            //we use itemName_itemId in order to load the product in ascending order and uniquely
            productDatabaseReference.orderByChild("itemName_itemId")
                    .startAt(productsArrayList.get(Math.max(productsArrayList.size() - 1, 0)).getItemName_itemId())
                    .limitToFirst(LIMIT)
                    .addListenerForSingleValueEvent(
                            new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                    Log.e(ProductsFragment.class.getName(), dataSnapshot.getChildrenCount() + " child count ");

                                    if (dataSnapshot.exists()) {
                                        Iterator<DataSnapshot> iterator = dataSnapshot.getChildren().iterator();

                                        //skip firstItem
                                        if (iterator.hasNext()) {
                                            iterator.next();
                                        }

                                        while (iterator.hasNext()) {
                                            ProductModel currentProduct = iterator.next().getValue(ProductModel.class);

                                            String cartProductString = SharedPrefsUtils.getStringPreference(getActivity(), currentProduct.getItemId(), 0);
                                            ProductModel productModel = new Gson().fromJson(cartProductString, ProductModel.class);

                                            if (currentProduct.equals(productModel)) {
                                                currentProduct.setQuantityCounter(productModel.getQuantityCounter());
                                            }

                                            if (!productsArrayList.contains(currentProduct))
                                                productsArrayList.add(currentProduct);

                                            NotifyRecyclerItems.notifyItemInsertedAt(productBinding.recyclerView, productsArrayList.size());

                                            if (productsArrayList.size() - currentListSize == dataSnapshot.getChildrenCount() - 1) {
                                                isLoading.set(false);
                                            }
                                        }

                                        if (dataSnapshot.getChildrenCount() == 1) {
                                            isLoading.set(false);
                                            Toast.makeText(getActivity(), "you have reached to last", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            }
                    );
        }
    }

    @Override
    public void stopLoading() {
        isLoading.set(false);
    }
}
