package com.example.pickle.activity.Main.NavigationFragment;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
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
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pickle.Adapters.FirebaseSearchRecyclerAdapter;
import com.example.pickle.R;
import com.example.pickle.activity.Main.FirebaseSearchActivity;
import com.example.pickle.activity.Main.MainActivity;
import com.example.pickle.activity.Main.Options.CartViewActivity;
import com.example.pickle.activity.carousel.CarouselAdapter;
import com.example.pickle.activity.carousel.CarouselImage;
import com.example.pickle.data.ProductModel;
import com.example.pickle.databinding.FragmentOrderBinding;
import com.example.pickle.utils.BadgeDrawableUtils;
import com.example.pickle.utils.SharedPrefsUtils;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.yarolegovich.discretescrollview.DSVOrientation;
import com.yarolegovich.discretescrollview.DiscreteScrollView;
import com.yarolegovich.discretescrollview.InfiniteScrollAdapter;
import com.yarolegovich.discretescrollview.transform.ScaleTransformer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class OrderFragment extends Fragment{

    private static final int LIST_SIZE = 2;
    private Toolbar _toolbar;

    private List<CarouselImage> imageList;
    private DiscreteScrollView carouselScrollView;
    private InfiniteScrollAdapter infiniteAdapter;
    private NavController _navController;

    private FragmentOrderBinding binding;
    private ArrayList<ProductModel> productModelArrayList;

    private Map<String, String> lastElemId;
    private int adapterPos;

    public OrderFragment() {
        // Required empty public constructor
    }

    private void init_fields(View v) {
        _toolbar = v.findViewById(R.id.fragment_order_toolbar);
        productModelArrayList = new ArrayList<>();
        lastElemId = new HashMap<>();
        //final Typeface tf = ResourcesCompat.getFont(getContext(), R.font.pacifico_regular);
    }

    private void init_carousel(View v) {
        imageList = new ArrayList<>();
        carouselScrollView = binding.rvScroll;
        carouselScrollView.setOrientation(DSVOrientation.HORIZONTAL);
        infiniteAdapter = InfiniteScrollAdapter.wrap(new CarouselAdapter(imageList));
        carouselScrollView.setAdapter(infiniteAdapter);
        carouselScrollView.setItemTransitionTimeMillis(150);
        carouselScrollView.setItemTransformer(new ScaleTransformer.Builder()
        .setMinScale(0.8F)
        .build());
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        _navController = Navigation.findNavController(view);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu_main, menu);

        MenuItem item = menu.findItem(R.id.menu_main_cart_btn);
        LayerDrawable icon = (LayerDrawable) item.getIcon();

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
//        if (preferences != null) {
//            Map<String, ?> allEntries = preferences.getAll();
//            int count = 0;
//            for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
//
//                String list = SharedPrefsUtils.getStringPreference(getContext(), entry.getKey(), 0);
//                ProductModel[] models = new Gson().fromJson(list, ProductModel[].class);
//
//                if (list != null && models != null) {
//                    count += models.length;
//                }
//
//                Log.e("map values", entry.getKey() + ": " + entry.getValue().toString());
//                Log.e("map values", " ------------------------  " );
//            }
//            if (count > 0) {
//                    setBadgeCount(getActivity(), icon, count);
//            }
//        }
        super.onCreateOptionsMenu(menu, inflater);
    }

    public static void setBadgeCount(Context context, LayerDrawable icon, int count) {
        BadgeDrawableUtils badge ;
        Drawable reuse = icon.findDrawableByLayerId(R.id.ic_count);
        if (reuse != null && reuse instanceof BadgeDrawableUtils) {
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

        binding = DataBindingUtil.inflate(
                inflater,
                R.layout.fragment_order,
                container,
                false
        );

        init_fields(binding.getRoot());
        binding.setProductList(productModelArrayList);
        setUpToolbar();
        getImageList();
        init_carousel(binding.getRoot());

        new Handler().postDelayed(this::addProduct, 2000);

        binding.cardViewFruits.setOnClickListener(n -> _navController.navigate(R.id.action_orderFragment_to_fruitsFragment));
        binding.cardViewVegetables.setOnClickListener(n -> _navController.navigate(R.id.action_orderFragment_to_vegetableFragment));
        binding.cardViewBeverages.setOnClickListener(n -> _navController.navigate(R.id.action_orderFragment_to_beveragesFragment));
        binding.cardViewDairy.setOnClickListener(n -> _navController.navigate(R.id.action_orderFragment_to_dairyFragment));

        binding.recomRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                LinearLayoutManager layoutManager = (LinearLayoutManager) binding.recomRecyclerView.getLayoutManager();
                FirebaseSearchRecyclerAdapter firebaseSearchRecyclerAdapter = (FirebaseSearchRecyclerAdapter) binding.recomRecyclerView.getAdapter();
                if (layoutManager != null && firebaseSearchRecyclerAdapter != null) {
                    int id = layoutManager.findFirstCompletelyVisibleItemPosition();
                    adapterPos = firebaseSearchRecyclerAdapter.getItemCount();
//                    Log.e("OrderFragment ", adapterListSize + " adapter List Size ");
//                    Log.e("Order Fragment ", id + " adapter id size ");
                    if (id >= adapterPos - 1) {
                        Log.e("OrderFragment ", " add new item ");
                        addNewProduct();
                    }
                }

            }
        });

        return binding.getRoot();
    }


    private void getImageList() {
        //todo remove listener
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("CarouselImages");
        reference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    imageList.add(new CarouselImage(dataSnapshot.getValue(String.class)));
                    infiniteAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    imageList.add(new CarouselImage(dataSnapshot.getValue(String.class)));
                    infiniteAdapter.notifyDataSetChanged();
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
        ((AppCompatActivity)getActivity()).setSupportActionBar(_toolbar);

        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("Pickle India");

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
                                productModelArrayList.add(snapshot.getValue(ProductModel.class));
                                Collections.shuffle(productModelArrayList);
                                if (binding.recomRecyclerView.getAdapter() != null && adapterPos > 2)
                                    binding.recomRecyclerView.getAdapter().notifyItemRangeChanged(adapterPos-2, adapterPos);
                            }
                            if (lastProduct != null) {
                                lastElemId.put(lastProduct.getItemCategory(), lastProduct.getItemId());
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
        Log.e("Order Fragment ", " map values "+ lastElemId);
        DatabaseReference keysRef = FirebaseDatabase.getInstance().getReference("ProductCategories");
        Query keyQuery = keysRef.orderByKey();
        //todo remove listener
        keyQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot dataSnapshotChild : dataSnapshot.getChildren()) {

                    String lastElem = lastElemId.containsKey(dataSnapshotChild.getValue(String.class)) ? lastElemId.get(dataSnapshotChild.getValue(String.class)) : "Vegetables";
                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Products/" + dataSnapshotChild.getValue(String.class));
                    Query query = reference.orderByKey().startAt(lastElem).limitToFirst(LIST_SIZE);
                    query.keepSynced(true);
                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            ProductModel updateId = null;

                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                ProductModel productModel = snapshot.getValue(ProductModel.class);
                                Log.e("Order fragnment ",  " last element name " + productModel.getItemName());
                                if (productModel != null && !productModelArrayList.contains(productModel)) {
                                    updateId = productModel;
                                    productModelArrayList.add(productModel);
                                    if (binding.recomRecyclerView.getAdapter() != null)
                                        binding.recomRecyclerView.getAdapter().notifyDataSetChanged();
                                }
                            }

                            if (updateId != null) {
                                lastElemId.put(updateId.getItemCategory(), updateId.getItemId());
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



    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                ((MainActivity)(getActivity())).openDrawer();
                break;
            case R.id.menu_main_cart_btn:
                startActivity(new Intent(getActivity(), CartViewActivity.class));
                break;
            case R.id.menu_orders:
                _navController.navigate(R.id.action_orderFragment_to_ordersPlacedFragment);
                break;

            case R.id.menu_main_search_btn:
                startActivity(new Intent(getActivity(), FirebaseSearchActivity.class));
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
