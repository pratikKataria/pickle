package com.example.pickle.navigation;


import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
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
import com.example.pickle.R;
import com.example.pickle.carousel.CarouselImage;
import com.example.pickle.cart.CartActivity;
import com.example.pickle.databinding.FragmentHomeBinding;
import com.example.pickle.interfaces.IFragmentCb;
import com.example.pickle.main.FirebaseSearchActivity;
import com.example.pickle.main.MainActivity;
import com.example.pickle.models.ProductModel;
import com.example.pickle.utils.BadgeDrawableUtils;
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
import java.util.List;

import static com.example.pickle.utils.Constant.PRODUCT_TYPE;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment implements IFragmentCb {

    private FragmentHomeBinding binding;
    private List<CarouselImage> imageList;
    private ArrayList<ProductModel> productModelArrayList;

    private final DatabaseReference carouselImagesDatabaseReference = FirebaseDatabase.getInstance().getReference("CarouselImages");
    private ChildEventListener carouselImageChildEventListener;

    private final DatabaseReference productCategoriesDatabaseReference = FirebaseDatabase.getInstance().getReference("ProductCategories");
    private ValueEventListener productCategoriesValueEventListener;

    private static int itemCount;
    private static final int LIMIT = 2;

    private final int REQUEST_CODE = 0x88;

    public HomeFragment() {
        // Required empty public constructor
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

        return binding.getRoot();
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
        if (carouselImageChildEventListener != null) {
            carouselImagesDatabaseReference.removeEventListener(carouselImageChildEventListener);
        }

        if (productCategoriesValueEventListener != null) {
            productCategoriesDatabaseReference.removeEventListener(productCategoriesValueEventListener);
        }
    }


    @Override
    public void play() {
    }

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
}
