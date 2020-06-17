package com.example.pickle.navigation;


import android.content.Context;
import android.content.Intent;
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
import android.view.animation.LinearInterpolator;
import android.widget.AbsListView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ObservableBoolean;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.daimajia.slider.library.Indicators.PagerIndicator;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.example.pickle.R;
import com.example.pickle.carousel.CarouselImage;
import com.example.pickle.cart.CartActivity;
import com.example.pickle.databinding.FragmentHomeBinding;
import com.example.pickle.interfaces.IFragmentCb;
import com.example.pickle.interfaces.ImageUrlListener;
import com.example.pickle.main.FirebaseSearchActivity;
import com.example.pickle.main.MainActivity;
import com.example.pickle.models.ProductModel;
import com.example.pickle.ui.CarouselSliderView;
import com.example.pickle.ui.ExitAppBottomSheetDialog;
import com.example.pickle.utils.BadgeDrawableUtils;
import com.example.pickle.utils.Constant;
import com.example.pickle.utils.NotifyRecyclerItems;
import com.example.pickle.utils.SharedPrefsUtils;
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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import static com.example.pickle.utils.Constant.PRODUCT_CAT_COUNT;
import static com.example.pickle.utils.Constant.PRODUCT_TYPE;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment implements IFragmentCb, ImageUrlListener, BaseSliderView.OnSliderClickListener {

    private FragmentHomeBinding binding;
    private List<CarouselImage> imageList;
    private ArrayList<String> carouselImage;
    private ArrayList<ProductModel> productModelArrayList;
    private HashMap<String, String> paginationProductKeyMap;
    private ObservableBoolean isLoading = new ObservableBoolean(false);
    private boolean isScrolling;

    private final DatabaseReference carouselImagesDatabaseReference = FirebaseDatabase.getInstance().getReference("CarouselImages");
    private ChildEventListener carouselImageChildEventListener;

    private final DatabaseReference productCategoriesDatabaseReference = FirebaseDatabase.getInstance().getReference("ProductCategories");
    private ValueEventListener productCategoriesValueEventListener;

    private static int itemCount;
    private static final int LIMIT = 2;
    private int currentIndex = 1;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
        productModelArrayList = new ArrayList<>();
        imageList = new ArrayList<>();
        carouselImage = new ArrayList<>();
        paginationProductKeyMap = new HashMap<>();
        addProduct();
        getImageList();
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if (binding == null)
            binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false);

        setExitTransition(MaterialSharedAxis.create(MaterialSharedAxis.X, false));
        setUpToolbar();

        binding.setProductList(productModelArrayList);
        binding.setCarouselImage(imageList);
        binding.setHomeFragment(HomeFragment.this);
        if (getActivity() != null)
            binding.setTypeface(ResourcesCompat.getFont(getActivity(), R.font.pacifico_regular));
        updateToolbarCartIconCounter();

        binding.carouselView.setPresetTransformer(SliderLayout.Transformer.Default);
        binding.carouselView.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);
        binding.carouselView.setIndicatorVisibility(PagerIndicator.IndicatorVisibility.Visible);
        binding.carouselView.setSliderTransformDuration(800, new LinearInterpolator());
        binding.carouselView.setDuration(3000);
        binding.setIsLoading(isLoading);
        initRecyclerView();
        return binding.getRoot();
    }
    int visibleItemCount;
    int totalItemCount;
    int pastVisibleItems;
    private void initRecyclerView() {
        binding.suggestionRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                    isScrolling = true;
                }
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                LinearLayoutManager layoutManager = (LinearLayoutManager) binding.suggestionRecyclerView.getLayoutManager();

                visibleItemCount = layoutManager.getChildCount();
                totalItemCount   = layoutManager.getItemCount();
                pastVisibleItems = layoutManager.findFirstCompletelyVisibleItemPosition();
                if (isScrolling && !isLoading.get() && ((visibleItemCount + pastVisibleItems) == totalItemCount)) {
                    isScrolling = false;
                    if (dx > 0) {
                        isLoading.set(true);
                        addNewProduct();
                    }
                }

                if (dx < 0) isLoading.set(false);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 1 && data != null && data.getExtras() != null) {
            navigateToProductFragment(data.getExtras().getString(PRODUCT_TYPE));
        }
    }

    public void navigateToProductFragment(@Nullable String type) {
        if (type == null) return;
        NavController navController = NavHostFragment.findNavController(this);
        Bundle bundle = new Bundle();
        bundle.putString(PRODUCT_TYPE, type);
        navController.navigate(R.id.action_homeFragment_to_productsFragment, bundle);
    }

    private void getImageList() {
        carouselImagesDatabaseReference.keepSynced(true);
        carouselImageChildEventListener = carouselImagesDatabaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                carouselImage.add(dataSnapshot.getValue(String.class));
                updateImage(dataSnapshot.getValue(String.class));
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

        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ((AppCompatActivity) getActivity()).getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu);
    }

    private void addProduct() {
        Query productCategoryQuery = productCategoriesDatabaseReference.orderByKey();
        productCategoriesValueEventListener = productCategoryQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot dataSnapshotChild : dataSnapshot.getChildren()) {
                    DatabaseReference productDatabaseReference = FirebaseDatabase.getInstance().getReference("Products/" + dataSnapshotChild.getValue(String.class));
                    Query query = productDatabaseReference.limitToFirst(LIMIT);
                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            ProductModel currentProduct;
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                currentProduct = snapshot.getValue(ProductModel.class);

                                //update product if available in to cart
                                String savedProductString = SharedPrefsUtils.getStringPreference(getActivity(), currentProduct.getItemId(), 0);
                                ProductModel cartProduct = new Gson().fromJson(savedProductString, ProductModel.class);
                                if (currentProduct.equals(cartProduct))
                                    currentProduct.setQuantityCounter(cartProduct.getQuantityCounter());

                                productModelArrayList.add(currentProduct);
                                NotifyRecyclerItems.notifyItemInsertedAt(binding.suggestionRecyclerView, productModelArrayList.size());

                                paginationProductKeyMap.put(currentProduct.getItemCategory(), currentProduct.getItemId());
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
        productCategoriesDatabaseReference.keepSynced(true);
        productCategoriesDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot productCategorySnapshot) {
                for (DataSnapshot dataSnapshotChild : productCategorySnapshot.getChildren()) {

                    DatabaseReference productDatabaseReference = FirebaseDatabase.getInstance().getReference("Products/" + dataSnapshotChild.getValue(String.class));
                    String productCategory = dataSnapshotChild.getValue(String.class);
                    String productStartKey = paginationProductKeyMap.containsKey(productCategory) ? paginationProductKeyMap.get(productCategory) : " ";
                    Query query = productDatabaseReference.orderByKey().startAt(productStartKey).limitToFirst(3);

                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot productsSnapshot) {
                            if (productsSnapshot.exists()) {

                                Iterator<DataSnapshot> iterator = productsSnapshot.getChildren().iterator();

                                //skip First item
                                if (iterator.hasNext()) {
                                    iterator.next();
                                }

                                while (iterator.hasNext()) {
                                    DataSnapshot snapshot = iterator.next();
                                    ProductModel currentProduct = snapshot.getValue(ProductModel.class);
                                    paginationProductKeyMap.put(currentProduct.getItemCategory(), currentProduct.getItemId());

                                    //update product quantity if available in to cart
                                    String savedProductString = SharedPrefsUtils.getStringPreference(getActivity(), currentProduct.getItemId(), 0);
                                    ProductModel cartProduct = new Gson().fromJson(savedProductString, ProductModel.class);
                                    if (currentProduct.equals(cartProduct))
                                        currentProduct.setQuantityCounter(cartProduct.getQuantityCounter());

                                    productModelArrayList.add(currentProduct);
                                    NotifyRecyclerItems.notifyItemInsertedAt(binding.suggestionRecyclerView, productModelArrayList.size());
                                    isLoading.set(true);
                                }

                                if ((productCategory != null && productCategory.equals(Constant.VEGETABLES) && productsSnapshot.getChildrenCount() == 1)){
                                    Toast.makeText(getActivity(), "No more item", Toast.LENGTH_SHORT).show();
                                    isLoading.set(false);
                                } else if (productCategory != null && productCategory.equals(Constant.VEGETABLES) && productsSnapshot.getChildrenCount() > 1) {
                                    isLoading.set(false);
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            isLoading.set(false);
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
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.tool_bar_navigation, menu);

        MenuItem item = menu.findItem(R.id.menu_main_cart_btn);
        LayerDrawable icon = (LayerDrawable) item.getIcon();
        if (itemCount > 0)
            setBadgeCount(getActivity(), icon, itemCount);
        super.onCreateOptionsMenu(menu, inflater);
    }

    public static void setBadgeCount(Context context, LayerDrawable icon, int count) {
        BadgeDrawableUtils badge;
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
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int REQUEST_CODE = 0x88;
        switch (item.getItemId()) {
            case android.R.id.home:
                if (getActivity() != null)
                    ((MainActivity) (getActivity())).openDrawer();
                break;
            case R.id.menu_main_cart_btn:
                startActivityForResult(new Intent(getActivity(), CartActivity.class).addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY), REQUEST_CODE);
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

        //update cart icon on resume
        updateToolbarCartIconCounter();

        ArrayList<ProductModel> refreshList = SharedPrefsUtils.getAllProducts(getActivity());
        for (ProductModel product : productModelArrayList) {
            if (refreshList.contains(product)) {
                ProductModel newProduct = refreshList.get(refreshList.indexOf(product));
                product.setQuantityCounter(newProduct.getQuantityCounter());
            } else
                product.setQuantityCounter(0);
            NotifyRecyclerItems.notifyItemChangedAt(binding.suggestionRecyclerView, productModelArrayList.indexOf(product));
        }
    }

    @Override
    public void onStop() {
        super.onStop();
//        if (carouselImageChildEventListener != null) {
//            carouselImagesDatabaseReference.removeEventListener(carouselImageChildEventListener);
//        }
//
//        if (productCategoriesValueEventListener != null) {
//            productCategoriesDatabaseReference.removeEventListener(productCategoriesValueEventListener);
//        }
    }


    @Override
    public void play() { }

    @Override
    public void updateIconItems() {
        updateToolbarCartIconCounter();
    }

    private void updateToolbarCartIconCounter() {
        if (getActivity() != null) {
            itemCount = SharedPrefsUtils.getAllProducts(getActivity()).size();
            getActivity().invalidateOptionsMenu();
        }
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        requireActivity().getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                ExitAppBottomSheetDialog exitAppBottomSheetDialog = new ExitAppBottomSheetDialog();
                exitAppBottomSheetDialog.show(getChildFragmentManager(), null);
                return;
            }
        });
    }

    @Override
    public void updateImage(String url) {
        CarouselSliderView textSliderView = new CarouselSliderView(getActivity());
        textSliderView.image(url)
                .setScaleType(BaseSliderView.ScaleType.Fit)
                .setOnSliderClickListener(this);

        binding.carouselView.addSlider(textSliderView);
    }

    @Override
    public void onSliderClick(BaseSliderView slider) {
        Toast.makeText(getActivity(), "Offers will be shown", Toast.LENGTH_SHORT).show();
    }
}
