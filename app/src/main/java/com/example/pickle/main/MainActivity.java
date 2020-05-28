package com.example.pickle.main;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;
import com.example.pickle.Login.LoginActivity;
import com.example.pickle.R;
import com.example.pickle.SmoothActionBarDrawerToggle;
import com.example.pickle.cart.CartActivity;
import com.example.pickle.interfaces.IFragmentCb;
import com.example.pickle.interfaces.IMainActivity;
import com.example.pickle.models.ProductModel;
import com.example.pickle.navigation.HomeFragment;
import com.example.pickle.network.NetworkConnectionStateMonitor;
import com.example.pickle.utils.BundleUtils;
import com.example.pickle.utils.SharedPrefsUtils;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.Gson;

import static com.example.pickle.interfaces.NavigationAction.NAVIGATE_TO_PRODUCTS;
import static com.example.pickle.utils.Constant.PRODUCT_BUNDLE;

public class MainActivity extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener,
        IMainActivity, IFragmentCb {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private NavController navController;
    private BottomNavigationView bottomNavigationView;
    private SmoothActionBarDrawerToggle smoothActionBarDrawerToggle;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        drawerLayout = findViewById(R.id.activity_main_drawer_layout);
        navigationView = findViewById(R.id.activity_main_nv_side_navigation);
        bottomNavigationView = findViewById(R.id.activity_main_cnb_bottom_nav);
        bottomNavigationView.setItemIconTintList(null);

        smoothActionBarDrawerToggle = new SmoothActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(smoothActionBarDrawerToggle);

        drawerLayout.setOnClickListener( n -> {
            startActivity(new Intent(MainActivity.this, CartActivity.class));
        });

        navigationView.setNavigationItemSelectedListener(this);
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.activity_main_nav_host);
        navController = navHostFragment.getNavController();
        NavigationUI.setupWithNavController(bottomNavigationView, navHostFragment.getNavController());
        navController.navigate(R.id.action_orderFragment_to_nav_menu_sub_orders);

        try {
            String navigateTo = getIntent().getStringExtra(PRODUCT_BUNDLE);
            if (navigateTo != null) {
                navController.navigate(NAVIGATE_TO_PRODUCTS, BundleUtils.setNavigationBundle(navigateTo));
            }
        } catch (Exception xe) {
            Log.e(MainActivity.class.getName(), xe.getMessage());
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        NetworkConnectionStateMonitor networkConnectionStateMonitor = new NetworkConnectionStateMonitor(this) {
            @Override
            public void state(String type, boolean status) {
                Log.e("connectivity receiver", "type " + type);

            }
        };
        networkConnectionStateMonitor.observe(this, aBoolean -> {
            Log.e("connectivity receiver", "network " + aBoolean);
        });
    }

    @SuppressLint("WrongConstant")
    public void openDrawer() {
        DrawerLayout drawerLayout = findViewById(R.id.activity_main_drawer_layout);
        drawerLayout.openDrawer(Gravity.START);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.navigation_menu_logout:
                Toast.makeText(this, "logout", Toast.LENGTH_SHORT).show();
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(this, LoginActivity.class));
                finish();
                break;
            case R.id.nav_menu_sub_orders:
                try {
                    smoothActionBarDrawerToggle.runWhenIdle(() -> navController.navigate(R.id.action_orderFragment_to_nav_menu_sub_orders));
                    drawerLayout.closeDrawers();
                } catch (Exception xe) {
                    Log.e("MainActivity", xe.getMessage());
                }
                break;
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void play() {

    }

    @Override
    public void updateIconItems() {

    }

    @Override
    public void updateQuantity(ProductModel productModel, int quantity) {
        if (productModel != null) {
            productModel.setQuantityCounter(quantity);
            SharedPrefsUtils.setStringPreference(this, productModel.getItemId(), new Gson().toJson(productModel));

            //send update message to fragment order
            NavHostFragment navHostFragment = (NavHostFragment) this.getSupportFragmentManager().findFragmentById(R.id.activity_main_nav_host);
            Fragment fragment = navHostFragment.getChildFragmentManager().getFragments().get(0);
            if (fragment instanceof HomeFragment) {
                IFragmentCb iFragmentCb = (IFragmentCb) fragment;
                iFragmentCb.updateIconItems();
            }
        }
    }

    @Override
    public void removeProduct(ProductModel productModel) {
        SharedPrefsUtils.removeValuePreference(this, productModel.getItemId());

        //send update message to fragment order
        NavHostFragment navHostFragment = (NavHostFragment) this.getSupportFragmentManager().findFragmentById(R.id.activity_main_nav_host);
        Fragment fragment = navHostFragment.getChildFragmentManager().getFragments().get(0);
        if (fragment instanceof HomeFragment) {
            IFragmentCb iFragmentCb = (IFragmentCb) fragment;
            iFragmentCb.updateIconItems();
        }
    }
}
