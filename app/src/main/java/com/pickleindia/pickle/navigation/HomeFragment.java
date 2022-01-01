package com.pickleindia.pickle.navigation;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import com.google.android.material.transition.MaterialSharedAxis;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.pickleindia.pickle.auth.Login.LoginActivity;
import com.pickleindia.pickle.R;
import com.pickleindia.pickle.cart.CartActivity;
import com.pickleindia.pickle.databinding.FragmentHomeBinding;
import com.pickleindia.pickle.interfaces.IFragmentCb;
import com.pickleindia.pickle.interfaces.ImageUrlListener;
import com.pickleindia.pickle.interfaces.ReferralBottomSheetListener;
import com.pickleindia.pickle.main.FirebaseSearchActivity;
import com.pickleindia.pickle.main.MainActivity;
import com.pickleindia.pickle.models.OfferCombo;
import com.pickleindia.pickle.models.ProductModel;
import com.pickleindia.pickle.ui.CarouselSliderView;
import com.pickleindia.pickle.ui.ExitAppBottomSheetDialog;
import com.pickleindia.pickle.ui.ReferralRewardBottomSheet;
import com.pickleindia.pickle.utils.BadgeDrawableUtils;
import com.pickleindia.pickle.utils.Constant;
import com.pickleindia.pickle.utils.NotifyRecyclerItems;
import com.pickleindia.pickle.utils.SharedPrefsUtils;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import static com.pickleindia.pickle.utils.Constant.PERMISSION_PREFS_KEY;
import static com.pickleindia.pickle.utils.Constant.PRODUCT_TYPE;

public class HomeFragment extends Fragment implements IFragmentCb, ImageUrlListener, BaseSliderView.OnSliderClickListener, ReferralBottomSheetListener {

    private FragmentHomeBinding binding;
    private ArrayList<OfferCombo> carouselImage;
    private ArrayList<ProductModel> productModelArrayList;
    private HashMap<String, String> paginationProductKeyMap;
    private final ObservableBoolean isLoading = new ObservableBoolean(false);
    private boolean isScrolling;

    private final DatabaseReference carouselImagesDatabaseReference = FirebaseDatabase.getInstance().getReference("Offers");
    private final DatabaseReference productCategoriesDatabaseReference = FirebaseDatabase.getInstance().getReference("ProductCategories");

    public int itemCount;
    private static final int LIMIT = 2;
    private ReferralRewardBottomSheet referralRewardBottomSheet;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
        carouselImage = new ArrayList<>();
        productModelArrayList = new ArrayList<>();
        paginationProductKeyMap = new HashMap<>();
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false);

        setExitTransition(MaterialSharedAxis.create(MaterialSharedAxis.X, false));
        setUpToolbar();

        binding.setProductList(productModelArrayList);
        binding.setHomeFragment(HomeFragment.this);
        binding.setTypeface(ResourcesCompat.getFont(getActivity(), R.font.pacifico_regular));
        itemCount = SharedPrefsUtils.getAllProducts(getActivity()).size();
        getActivity().invalidateOptionsMenu();
        initCarouselView();

        binding.setIsLoading(isLoading);
        initRecyclerView();
        return binding.getRoot();
    }


    int visibleItemCount;
    int totalItemCount;
    int pastVisibleItems;

    private void initRecyclerView() {
        RecyclerView recyclerView = binding.suggestionRecyclerView;
        recyclerView.setItemViewCacheSize(20);
        recyclerView.setDrawingCacheEnabled(true);
        recyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

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
                totalItemCount = layoutManager.getItemCount();
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

    private void initCarouselView() {
        binding.carouselView.setPresetTransformer(SliderLayout.Transformer.Default);
        binding.carouselView.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);
        binding.carouselView.setIndicatorVisibility(PagerIndicator.IndicatorVisibility.Visible);
        binding.carouselView.setDuration(2500);


        //add the default layout only if the carousel images list is empty
        if (carouselImage.isEmpty()) {
            CarouselSliderView textSliderView = new CarouselSliderView(getActivity());
            textSliderView.image(R.drawable.img_loading).setScaleType(BaseSliderView.ScaleType.Fit);
            binding.carouselView.addSlider(textSliderView);
            binding.carouselView.addSlider(textSliderView);
        }

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
        try {
            NavController navController = NavHostFragment.findNavController(this);
            Bundle bundle = new Bundle();
            bundle.putString(PRODUCT_TYPE, type);
            navController.navigate(R.id.action_homeFragment_to_productsFragment, bundle);
            productModelArrayList.clear();
            NotifyRecyclerItems.notifyDataSetChanged(binding.suggestionRecyclerView);
        } catch (Exception xe) {
            Log.e("HomeFragment", "no path defined");
        }

    }


    private ChildEventListener carouselImageChildEventListener;

    private ChildEventListener getImageList() {
        carouselImageChildEventListener = carouselImagesDatabaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                //when carousel has empty layouts then remove them - this happens only on the first load
                if (carouselImage.isEmpty()) {
                    binding.carouselView.removeAllSliders();
                }


                OfferCombo offerCombo = dataSnapshot.getValue(OfferCombo.class);
                Log.e("HomeFragment", offerCombo.toString());
                if (!carouselImage.contains(offerCombo) && offerCombo.getOfferThumb() != null) {
                    carouselImage.add(offerCombo);
                    updateImage(offerCombo.getOfferThumb());
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                OfferCombo offerCombo = dataSnapshot.getValue(OfferCombo.class);
                if (offerCombo.getOfferThumb() != null) {
                    carouselImage.remove(offerCombo);
                    carouselImage.add(offerCombo);
                    int indexOf = carouselImage.indexOf(offerCombo);
                    binding.carouselView.removeSliderAt(indexOf);
                    updateImage(offerCombo.getOfferThumb());
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

            }
        });

        return carouselImageChildEventListener;
    }

    private void setUpToolbar() {
        ((AppCompatActivity) getActivity()).setSupportActionBar(binding.fragmentOrderToolbar);

        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ((AppCompatActivity) getActivity()).getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu);
    }

    private void addProduct() {
        if (productModelArrayList.isEmpty()) {
            productCategoriesDatabaseReference.keepSynced(true);
            productCategoriesDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot dataSnapshotChild : dataSnapshot.getChildren()) {

                        DatabaseReference productDatabaseReference = FirebaseDatabase.getInstance().getReference("Products/" + dataSnapshotChild.child("baseCategory").getValue(String.class));
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

                                    if (!productModelArrayList.contains(currentProduct)) {
                                        productModelArrayList.add(currentProduct);
                                    }

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
    }

    private void addNewProduct() {
        productCategoriesDatabaseReference.keepSynced(true);
        productCategoriesDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot productCategorySnapshot) {
                for (DataSnapshot dataSnapshotChild : productCategorySnapshot.getChildren()) {

                    String productCategory = dataSnapshotChild.child("baseCategory").getValue(String.class);
                    DatabaseReference productDatabaseReference = FirebaseDatabase.getInstance().getReference("Products/" + productCategory);
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

                                    if (!productModelArrayList.contains(currentProduct)) {
                                        productModelArrayList.add(currentProduct);
                                        NotifyRecyclerItems.notifyItemInsertedAt(binding.suggestionRecyclerView, productModelArrayList.size());
                                        isLoading.set(true);
                                    }
                                }

                                if ((productCategory != null && productCategory.equals(Constant.CAT_TWO) && productsSnapshot.getChildrenCount() == 1)) {
                                    Toast.makeText(getActivity(), "No more item", Toast.LENGTH_SHORT).show();
                                    isLoading.set(false);
                                } else if (productCategory != null && productCategory.equals(Constant.CAT_TWO) && productsSnapshot.getChildrenCount() > 1) {
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
                productModelArrayList.clear();
                NotifyRecyclerItems.notifyDataSetChanged(binding.suggestionRecyclerView);
                startActivityForResult(new Intent(getActivity(), CartActivity.class).addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY), REQUEST_CODE);
                break;
            case R.id.menu_main_search_btn:
                productModelArrayList.clear();
                NotifyRecyclerItems.notifyDataSetChanged(binding.suggestionRecyclerView);
                startActivity(new Intent(getActivity(), FirebaseSearchActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK).addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY));
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPause() {
        super.onPause();
        carouselImage.clear();
    }

    @Override
    public void onResume() {
        super.onResume();
//        update cart icon on resume
        ArrayList<ProductModel> refreshList = SharedPrefsUtils.getAllProducts(getActivity());
        if (getActivity() != null) {
            itemCount = refreshList.size();
            getActivity().invalidateOptionsMenu();
        }

        for (OfferCombo offerCombo : carouselImage)
            updateImage(offerCombo.getOfferThumb());

        for (ProductModel productModel : refreshList) {
            int indexOf = productModelArrayList.indexOf(productModel);
            if (indexOf != -1) {
                ProductModel listProduct = productModelArrayList.get(indexOf);
                listProduct.setQuantityCounter(productModel.getQuantityCounter());
                productModelArrayList.remove(indexOf);
                productModelArrayList.add(indexOf, listProduct);
                NotifyRecyclerItems.notifyItemChangedAt(binding.suggestionRecyclerView, indexOf);
            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if (productModelArrayList.size() <= LIMIT + 1) {
            addProduct();
        }

        if (carouselImage.isEmpty()) {
            carouselImageChildEventListener = getImageList();
            carouselImagesDatabaseReference.addChildEventListener(carouselImageChildEventListener);
            return;
        }

        if (carouselImageChildEventListener == null) {
            carouselImageChildEventListener = getImageList();
            carouselImagesDatabaseReference.addChildEventListener(carouselImageChildEventListener);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (carouselImageChildEventListener != null) {
            carouselImagesDatabaseReference.removeEventListener(carouselImageChildEventListener);
        }
    }


    @Override
    public void play() {
    }

    @Override
    public void updateIconItems() {
        if (getActivity() != null) {
            ArrayList<ProductModel> refreshList = SharedPrefsUtils.getAllProducts(getActivity());
            itemCount = refreshList.size();
            getActivity().invalidateOptionsMenu();
        }
    }

//    public void updateIconItems(int itemCount) {
//        if (getActivity() != null) {
//            ArrayList<ProductModel> refreshList = SharedPrefsUtils.getAllProducts(getActivity());
//            itemCount = refreshList.size();
//            getActivity().invalidateOptionsMenu();
//        }
//    }

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
        try {
            int index = binding.carouselView.getCurrentPosition();
            if (index != -1) {
                OfferCombo offerCombo = carouselImage.get(index);
                if (offerCombo != null) {
                    if (offerCombo.isCombo()) {
                        NavController navController = NavHostFragment.findNavController(this);
                        Bundle bundle = new Bundle();
                        bundle.putParcelable("offer_combo", offerCombo);
                        navController.navigate(R.id.action_homeFragment_to_comboOfferFragment, bundle);
                        productModelArrayList.clear();
                    } else {
                        Log.e("HomeFragment", " not a combo ");
                        if (!offerCombo.getProductIds_cat().trim().isEmpty())
                            navigateToProductFragment(offerCombo.getProductIds_cat());
                    }
                }
            }
        } catch (Exception e) {
            Log.e("HomeFragment", "index of bound" + e.toString());
            e.printStackTrace();
        }
    }

    @Override
    public void open(String referredBy) {
        referralRewardBottomSheet = new ReferralRewardBottomSheet(() -> {
            if (FirebaseAuth.getInstance().getCurrentUser() == null && getActivity() != null) {
                SharedPreferences sharedPrefsUtils = getActivity().getSharedPreferences(PERMISSION_PREFS_KEY, 0);
                SharedPreferences.Editor editor = sharedPrefsUtils.edit();
                editor.putString("referredBy", referredBy);
                editor.apply();

                startActivity(new Intent(getActivity(), LoginActivity.class).addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY));
                referralRewardBottomSheet.dismiss();
            } else {
                Toast.makeText(getActivity(), "referral only works for new user", Toast.LENGTH_LONG).show();
                referralRewardBottomSheet.dismiss();
            }
        });
        referralRewardBottomSheet.show(getActivity().getSupportFragmentManager(), "referralRewardBottomSheet");
    }

}
