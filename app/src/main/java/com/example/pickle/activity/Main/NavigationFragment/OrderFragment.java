package com.example.pickle.activity.Main.NavigationFragment;


import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pickle.Adapters.GridRecyclerViewAdapter;
import com.example.pickle.Adapters.ProductsRecyclerViewAdapter;
import com.example.pickle.R;
import com.example.pickle.SpacesItemDecoration;
import com.example.pickle.Utility;
import com.example.pickle.activity.Main.MainActivity;
import com.example.pickle.data.BadgeDrawable;
import com.example.pickle.data.GridItem;
import com.example.pickle.data.Product;
import com.example.pickle.data.ProductModel;
import com.example.pickle.data.SharedPrefsUtils;
import com.google.gson.Gson;
import com.synnapps.carouselview.CarouselView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class OrderFragment extends Fragment{

    private CarouselView _carouselView;

    private Toolbar _toolbar;
    private RecyclerView _gridViewRecyclerView;
    private List<GridItem> gridItemCategoryList;

    private RecyclerView recyclerViewProduct;
    private List<Product> productsList;

    private NavController _navController;


    private int [] sampleImages = {
            R.drawable.sale_one,
            R.drawable.sale_two,
            R.drawable.sale_three,
            R.drawable.seal_four
    };

    private final static HashMap<Integer, Integer> navigationMap = new HashMap<>();


    public OrderFragment() {
        // Required empty public constructor
    }

    private void init_fields(View v) {
        _carouselView = v.findViewById(R.id.carouselView);
        _toolbar = v.findViewById(R.id.fragment_order_toolbar);
        _gridViewRecyclerView = v.findViewById(R.id.recyclerView);
        recyclerViewProduct = v.findViewById(R.id.recyclerView1);

        gridItemCategoryList = new ArrayList<>();
        productsList = new ArrayList<>();

        //final Typeface tf = ResourcesCompat.getFont(getContext(), R.font.pacifico_regular);

        navigationMap.put(0, R.id.action_orderFragment_to_fruitsFragment);
        navigationMap.put(1, R.id.action_orderFragment_to_vegetableFragment);
        navigationMap.put(2, R.id.action_orderFragment_to_beveragesFragment);
        navigationMap.put(3, R.id.action_orderFragment_to_dairyFragment);

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
        if (preferences != null) {
            Map<String, ?> allEntries = preferences.getAll();
            int count = 0;
            for (Map.Entry<String, ?> entry : allEntries.entrySet()) {

                String list = SharedPrefsUtils.getStringPreference(getContext(), entry.getKey(), 0);
                ProductModel[] models = new Gson().fromJson(list, ProductModel[].class);

                if (list != null && models != null) {
                    count += models.length;
                }

                Log.e("map values", entry.getKey() + ": " + entry.getValue().toString());

            }
            if (count > 0) {
                    setBadgeCount(getActivity(), icon, count);
            }
        }
        super.onCreateOptionsMenu(menu, inflater);
    }

    public static void setBadgeCount(Context context, LayerDrawable icon, int count) {
        BadgeDrawable badge ;
        Drawable reuse = icon.findDrawableByLayerId(R.id.ic_count);
        if (reuse != null && reuse instanceof BadgeDrawable) {
            badge = (BadgeDrawable) reuse;
        } else {
            badge = new BadgeDrawable(context);
        }

        badge.setCount(String.valueOf(count));
        icon.mutate();
        icon.setDrawableByLayerId(R.id.ic_count, badge);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_order, container, false);

        init_fields(view);

        setUpToolbar();

        _carouselView.setPageCount(sampleImages.length);

        _carouselView.setImageListener((position, imageView) -> {
            imageView.setImageResource(sampleImages[position]);
        });

        populateList();

        initGridRecyclerView();


        ProductsRecyclerViewAdapter adapter = new ProductsRecyclerViewAdapter(getContext(), productsList);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false);
        recyclerViewProduct.setLayoutManager(layoutManager);
        recyclerViewProduct.addItemDecoration(new SpacesItemDecoration(16, 9));
        recyclerViewProduct.setAdapter(adapter);

        return view;
    }

    private void initGridRecyclerView() {
        int mNoOfColumns = Utility.calculateNoOfColumns(getContext(), 120 );
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), mNoOfColumns);
        _gridViewRecyclerView.setLayoutManager(gridLayoutManager);
        GridRecyclerViewAdapter gridRecyclerViewAdapter = new GridRecyclerViewAdapter(getActivity(), gridItemCategoryList);
        _gridViewRecyclerView.addItemDecoration(new SpacesItemDecoration(5));
        _gridViewRecyclerView.setAdapter(gridRecyclerViewAdapter);
        gridRecyclerViewAdapter.setOnItemClickListener(position -> {
            if (navigationMap.containsKey(position)) {
                _navController.navigate(navigationMap.get(position));
            }
        });
    }

    private void populateList() {

        gridItemCategoryList.add(new GridItem("Fruits", R.drawable.ic_fruit));
        gridItemCategoryList.add(new GridItem("Vegetables", R.drawable.ic_vegetables));
        gridItemCategoryList.add(new GridItem("Beverages", R.drawable.ic_beverages));
        gridItemCategoryList.add(new GridItem("Dairy", R.drawable.ic_dairy));
        gridItemCategoryList.add(new GridItem("Dairy", R.drawable.ic_dairy));


        productsList.add(new Product("item1", 2110));
        productsList.add(new Product("item1", 2110));
        productsList.add(new Product("item1", 2110));
        productsList.add(new Product("item1", 2110));
        productsList.add(new Product("item1", 2110));
        productsList.add(new Product("item1", 2110));
        productsList.add(new Product("item1", 2110));

    }

    private void setUpToolbar() {
        ((AppCompatActivity)getActivity()).setSupportActionBar(_toolbar);

        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("Pickle India");

        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ((AppCompatActivity)getActivity()).getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu);
    }



    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                ((MainActivity)(getActivity())).openDrawer();
                break;
            case R.id.menu_main_cart_btn:
                _navController.navigate(R.id.action_orderFragment_to_cartFragment);
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
