package com.pickleindia.pickle.main;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import com.pickleindia.pickle.Login.LoginActivity;
import com.pickleindia.pickle.R;
import com.pickleindia.pickle.cart.CartActivity;
import com.pickleindia.pickle.interfaces.IFragmentCb;
import com.pickleindia.pickle.interfaces.IMainActivity;
import com.pickleindia.pickle.models.ProductModel;
import com.pickleindia.pickle.navigation.HomeFragment;
import com.pickleindia.pickle.network.NetworkConnectionStateMonitor;
import com.pickleindia.pickle.ui.ExitAppBottomSheetDialog;
import com.pickleindia.pickle.utils.SharedPrefsUtils;
import com.pickleindia.pickle.utils.SmoothActionBarDrawerToggle;
import com.pickleindia.pickle.utils.SnackbarNoSwipeBehavior;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.Gson;

public class MainActivity extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener,
        IMainActivity, IFragmentCb, ExitAppBottomSheetDialog.BottomSheetOnButtonClickListener {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private NavController navController;
    private BottomNavigationView bottomNavigationView;
    private SmoothActionBarDrawerToggle smoothActionBarDrawerToggle;
    private CoordinatorLayout snackBarLayout;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        drawerLayout = findViewById(R.id.activity_main_drawer_layout);
        navigationView = findViewById(R.id.activity_main_nv_side_navigation);
        bottomNavigationView = findViewById(R.id.activity_main_cnb_bottom_nav);
        snackBarLayout = findViewById(R.id.coordinatorLayout);
        bottomNavigationView.setItemIconTintList(null);

        findViewById(R.id.activity_main_fab_add_item).setOnClickListener(n -> {
            startActivity(new Intent(this, AddNewItemActivity.class));
        });

        smoothActionBarDrawerToggle = new SmoothActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(smoothActionBarDrawerToggle);

        drawerLayout.setOnClickListener(n -> {
            startActivity(new Intent(MainActivity.this, CartActivity.class));
        });

        navigationView.setNavigationItemSelectedListener(this);
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.activity_main_nav_host);
        navController = navHostFragment.getNavController();
        NavigationUI.setupWithNavController(bottomNavigationView, navHostFragment.getNavController());


    }

    @Override
    protected void onStart() {
        super.onStart();
        NetworkConnectionStateMonitor networkConnectionStateMonitor = new NetworkConnectionStateMonitor(this);
        Snackbar snackbar = Snackbar.make(snackBarLayout, "cannot connect to internet", Snackbar.LENGTH_INDEFINITE).setAction("Action", null);
        networkConnectionStateMonitor.observe(this, isAvailable -> {
            if (!isAvailable) {
                customSnackBar(snackbar);
                snackbar.setBehavior(new SnackbarNoSwipeBehavior());
                snackbar.show();
            } else {
                snackbar.dismiss();
            }
        });
    }


    private void customSnackBar(Snackbar snackbar) {
        View view = snackbar.getView();
        TextView snackBarTextView = view.findViewById(com.google.android.material.R.id.snackbar_text);
        snackBarTextView.setTextColor(Color.WHITE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            snackBarTextView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        } else {
            snackBarTextView.setGravity(Gravity.CENTER_HORIZONTAL);
        }
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
                Toast.makeText(this, "Logout", Toast.LENGTH_SHORT).show();
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(this, LoginActivity.class));
                finish();
                break;
            case R.id.nav_menu_sub_orders:
                smoothActionBarDrawerToggle.runWhenIdle(() -> checkAuthAndNavigate(R.id.action_homeFragment_to_nav_menu_sub_orders));
                drawerLayout.closeDrawers();
                break;
            case R.id.nav_menu_sub_address_book:
                smoothActionBarDrawerToggle.runWhenIdle(() -> checkAuthAndNavigate(R.id.action_homeFragment_to_addressBookFragment));
                drawerLayout.closeDrawers();
                break;
            case R.id.nav_menu_sub_refer_link:
                Log.e("MainActivity", FirebaseAuth.getInstance().getCurrentUser().isAnonymous() + " anonymous " + FirebaseAuth.getInstance().getUid());
//                createReferLink();
                break;
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private void checkAuthAndNavigate(int id) {
        if (FirebaseAuth.getInstance().getCurrentUser() != null && FirebaseAuth.getInstance().getCurrentUser().isAnonymous()) {
            startActivity(new Intent(this, LoginActivity.class).addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY));
            Toast.makeText(this, "Login First", Toast.LENGTH_LONG).show();
        } else {
            if (id != navController.getCurrentDestination().getId())
                navController.navigate(id);
        }
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

    @Override
    public void onExit() {
       finish();
    }
}
