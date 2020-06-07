package com.example.pickle.navigation;


import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.example.pickle.BaseFragment;
import com.example.pickle.R;
import com.example.pickle.carousel.CarouselImage;
import com.example.pickle.cart.CartActivity;
import com.example.pickle.databinding.FragmentHomeBinding;
import com.example.pickle.interfaces.IFragmentCb;
import com.example.pickle.main.FirebaseSearchActivity;
import com.example.pickle.main.MainActivity;
import com.example.pickle.models.ProductModel;
import com.example.pickle.orders.OrdersPlacedFragment;
import com.example.pickle.utils.BadgeDrawableUtils;
import com.example.pickle.utils.NotifyRecyclerItems;
import com.example.pickle.utils.SharedPrefsUtils;
import com.google.android.material.transition.MaterialFadeThrough;
import com.google.android.material.transition.MaterialSharedAxis;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.example.pickle.utils.Constant.PRODUCT_BUNDLE;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends BaseFragment implements IFragmentCb {

    private static final int LIST_SIZE = 2;

    private FragmentHomeBinding binding;
    private List<CarouselImage> imageList;
    private ArrayList<ProductModel> productModelArrayList;

    private Map<String, String> saveToMap;
    private int adapterPos;
    private static int itemCount;

    private ChildEventListener carouselImageChildEventListener;
    private DatabaseReference carouselImagesDatabaseReference;

    public HomeFragment() {
        // Required empty public constructor
    }

    private void init_fields(View v) {
        saveToMap = new HashMap<>();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
        productModelArrayList = new ArrayList<>();
        imageList = new ArrayList<>();
        addProduct();
        getImageList();
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.tool_bar_navigation, menu);

        MenuItem item = menu.findItem(R.id.menu_main_cart_btn);
        LayerDrawable icon = (LayerDrawable) item.getIcon();
        if (itemCount > 0) setBadgeCount(getActivity(), icon, itemCount);
        super.onCreateOptionsMenu(menu, inflater);
    }

    public static void setBadgeCount(Context context, LayerDrawable icon, int count) {
        BadgeDrawableUtils badge ;
        Drawable reuse = icon.findDrawableByLayerId(R.id.ic_count);
        if (reuse instanceof BadgeDrawableUtils) {
            badge = (BadgeDrawableUtils) reuse;
        } else {
            badge = new BadgeDrawableUtils(context);
        }

        badge.setCount(String.valueOf(count));
        icon.mutate();
        icon.setDrawableByLayerId(R.id.ic_count, badge);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if (binding == null) binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false);

        setExitTransition(MaterialSharedAxis.create(MaterialSharedAxis.X, false));

        init_fields(binding.getRoot());
        setUpToolbar();

        binding.setProductList(productModelArrayList);
        binding.setCarouselImage(imageList);
        binding.setHomeFragment(HomeFragment.this);

        final Typeface tf = ResourcesCompat.getFont(getContext(), R.font.pacifico_regular);
        binding.setTypeface(tf);

        itemCount = SharedPrefsUtils.getAllProducts(getActivity()).size();
        getActivity().invalidateOptionsMenu();

        return binding.getRoot();
    }

    public void navigateToProductFragment(String type) {
        NavController _navController = NavHostFragment.findNavController(this);
        Bundle bundle = new Bundle();
        bundle.putString(PRODUCT_BUNDLE, type);
        _navController.navigate(R.id.action_homeFragment_to_productsFragment, bundle);
    }

    private void getImageList() {
        carouselImagesDatabaseReference = FirebaseDatabase.getInstance().getReference("CarouselImages");
        carouselImagesDatabaseReference.keepSynced(true);
        carouselImageChildEventListener = carouselImagesDatabaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                imageList.add(new CarouselImage(dataSnapshot.getValue(String.class)));
                if (imageList.size() - 1 >= 0)
                    NotifyRecyclerItems.notifyItemInsertedAt(binding.rvScroll, imageList.size() - 1);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                imageList.add(0, new CarouselImage(dataSnapshot.getValue(String.class)));
                NotifyRecyclerItems.notifyItemInsertedAt(binding.rvScroll, 0);
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void setUpToolbar() {
        ((AppCompatActivity) getActivity()).setSupportActionBar(binding.fragmentOrderToolbar);

        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ((AppCompatActivity)getActivity()).getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu);
    }

    private void addProduct() {
        DatabaseReference keysRef = FirebaseDatabase.getInstance().getReference("ProductCategories");
        Query keyQuery = keysRef.orderByKey();
        keyQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot dataSnapshotChild : dataSnapshot.getChildren()) {
                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Products/" + dataSnapshotChild.getValue(String.class));
                    Query query = reference.limitToFirst(LIST_SIZE);
                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            ProductModel lastProduct = null;
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                lastProduct = snapshot.getValue(ProductModel.class);

                                //update product if available in to cart
                                String savedProductString = SharedPrefsUtils.getStringPreference(getActivity(), lastProduct.getItemId(), 0);
                                ProductModel savedProduct = new Gson().fromJson(savedProductString, ProductModel.class);
                                if (lastProduct.equals(savedProduct)) {
                                    lastProduct.setQuantityCounter(savedProduct.getQuantityCounter());
                                }

                                productModelArrayList.add(lastProduct);

                                notifyItemInsertedAtPos(productModelArrayList.size());
                            }
                            if (lastProduct != null) {
//                                saveToMap.put(lastProduct.getItemCategory(), lastProduct.getItemId());
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getActivity(), "error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addNewProduct() {
        DatabaseReference keysRef = FirebaseDatabase.getInstance().getReference("ProductCategories");
        Query keyQuery = keysRef.orderByKey();
        //todo remove listener
        keyQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot dataSnapshotChild : dataSnapshot.getChildren()) {

                    String lastElem = saveToMap.containsKey(dataSnapshotChild.getValue(String.class)) ? saveToMap.get(dataSnapshotChild.getValue(String.class)) : "Vegetables";
                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Products/" + dataSnapshotChild.getValue(String.class));
                    Query query = reference.orderByKey().startAt(lastElem).limitToFirst(LIST_SIZE);
                    query.keepSynced(true);
                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            ProductModel lastProduct = null;

                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                ProductModel productModel = snapshot.getValue(ProductModel.class);
                                Log.e("Order fragnment ",  " last element name " + productModel.getItemName());
                                if (!productModelArrayList.contains(productModel)) {
                                    lastProduct = productModel;

                                    //update product if available in to cart
                                    String savedProductString = SharedPrefsUtils.getStringPreference(getActivity(), lastProduct.getItemId(), 0);
                                    ProductModel savedProduct = new Gson().fromJson(savedProductString, ProductModel.class);
                                    if (lastProduct.equals(savedProduct)) {
                                        lastProduct.setQuantityCounter(savedProduct.getQuantityCounter());
                                    }

                                    productModelArrayList.add(productModel);
                                    notifyItemInsertedAtPos(productModelArrayList.size() - 1);
//                                    binding.suggestionRecyclerView.getAdapter().notifyItemChanged(productModelArrayList.size()-1);
//                                    notifyChangesAtPosition(Math.max(productModelArrayList.size()-1, 0));
                                }
                            }

                            if (lastProduct != null) {
                                saveToMap.put(lastProduct.getItemCategory(), lastProduct.getItemId());
                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getActivity(), "error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void notifyItemInsertedAtPos(int index) {
        try {
            binding.suggestionRecyclerView.getAdapter().notifyItemChanged(index);
        } catch (NullPointerException npe) {
            Log.e(OrdersPlacedFragment.class.getName(), npe.getMessage());
        }
    }

    private void notifyChangesAtPosition(int pos) {
        try {
            binding.suggestionRecyclerView.getAdapter().notifyItemChanged(pos);
        }catch (NullPointerException npe) {
            Log.e("HomeFragment", npe.getMessage());
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                ((MainActivity) (getActivity())).openDrawer();
                break;
            case R.id.menu_main_cart_btn:
                startActivity(new Intent(getActivity(), CartActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK).addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY));
                break;
            case R.id.menu_main_search_btn:
                startActivity(new Intent(getActivity(), FirebaseSearchActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK).addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY));
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {
        super.onResume();

        ArrayList<ProductModel> refreshList = SharedPrefsUtils.getAllProducts(getActivity());

        //update cart icon on resume
        itemCount = SharedPrefsUtils.getAllProducts(getActivity()).size();
        getActivity().invalidateOptionsMenu();

        for (ProductModel product : productModelArrayList) {
            if (refreshList.contains(product)) {
                ProductModel newProduct = refreshList.get(refreshList.indexOf(product));
                product.setQuantityCounter(newProduct.getQuantityCounter());
                notifyChangesAtPosition(productModelArrayList.indexOf(product));
            } else {
                product.setQuantityCounter(0);
                notifyChangesAtPosition(productModelArrayList.indexOf(product));
            }
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (carouselImageChildEventListener != null && carouselImagesDatabaseReference != null) {
            carouselImagesDatabaseReference.removeEventListener(carouselImageChildEventListener);
        }
    }


    @Override
    public void play() {
    }

    @Override
    public void updateIconItems() {
        try {
            itemCount = SharedPrefsUtils.getAllProducts(getActivity()).size();
            getActivity().invalidateOptionsMenu();
        } catch (Exception xe) {
            Log.e(HomeFragment.class.getName(), "xe: " + xe.getMessage());
        }
    }
}
