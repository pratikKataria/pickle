package com.pickleindia.pickle.main;

import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.view.GravityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.UpdateAvailability;
import com.google.android.play.core.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.dynamiclinks.DynamicLink;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.gson.Gson;
import com.pickleindia.pickle.DeveloperProfile;
import com.pickleindia.pickle.auth.Login.LoginActivity;
import com.pickleindia.pickle.R;
import com.pickleindia.pickle.cart.CartActivity;
import com.pickleindia.pickle.databinding.LayoutFollowUsAlertDialogBinding;
import com.pickleindia.pickle.interfaces.IFragmentCb;
import com.pickleindia.pickle.interfaces.IMainActivity;
import com.pickleindia.pickle.models.ProductModel;
import com.pickleindia.pickle.navigation.HomeFragment;
import com.pickleindia.pickle.network.NetworkConnectionStateMonitor;
import com.pickleindia.pickle.product.ProductsFragment;
import com.pickleindia.pickle.ui.ExitAppBottomSheetDialog;
import com.pickleindia.pickle.utils.SharedPrefsUtils;
import com.pickleindia.pickle.utils.SmoothActionBarDrawerToggle;
import com.pickleindia.pickle.utils.SnackbarNoSwipeBehavior;

import static com.pickleindia.pickle.utils.Constant.PRODUCT_TYPE;

public class MainActivity extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener,
        IMainActivity, IFragmentCb, ExitAppBottomSheetDialog.BottomSheetOnButtonClickListener {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private NavController navController;
    private BottomNavigationView bottomNavigationView;
    private SmoothActionBarDrawerToggle smoothActionBarDrawerToggle;
    private CoordinatorLayout snackBarLayout;

    public static final int UPDATE_STATUS_CODE = 432;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        drawerLayout = findViewById(R.id.activity_main_drawer_layout);
        navigationView = findViewById(R.id.activity_main_nv_side_navigation);
        bottomNavigationView = findViewById(R.id.activity_main_cnb_bottom_nav);
        snackBarLayout = findViewById(R.id.coordinatorLayout);
        bottomNavigationView.setItemIconTintList(null);

//        findViewById(R.id.activity_main_fab_add_item).setOnClickListener(n -> {
//            startActivity(new Intent(this, AddNewItemActivity.class));
//        });

        smoothActionBarDrawerToggle = new SmoothActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(smoothActionBarDrawerToggle);

        drawerLayout.setOnClickListener(n -> {
            startActivity(new Intent(MainActivity.this, CartActivity.class));
        });

        navigationView.setNavigationItemSelectedListener(this);
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.activity_main_nav_host);
        navController = navHostFragment.getNavController();
        NavigationUI.setupWithNavController(bottomNavigationView, navHostFragment.getNavController());

        LinearLayout companyInfo = findViewById(R.id.linearLayout_comp_info);
        companyInfo.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, DeveloperProfile.class));
        });

        AppUpdateManager appUpdateManager = AppUpdateManagerFactory.create(MainActivity.this);
        Task<AppUpdateInfo> appUpdateManagerTask = appUpdateManager.getAppUpdateInfo();
        appUpdateManagerTask.addOnSuccessListener(appUpdateInfo -> {
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE &&
                    appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)) {
                try {
                    appUpdateManager.startUpdateFlowForResult(
                            appUpdateInfo,
                            AppUpdateType.IMMEDIATE,
                            this,
                            UPDATE_STATUS_CODE
                    );
                } catch (IntentSender.SendIntentException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void checkForDynamicLink() {
        FirebaseDynamicLinks.getInstance().getDynamicLink(getIntent()).addOnSuccessListener(pendingDynamicLinkData -> {
            Log.e("MainActivity ", "we have dynamic link ");

            Uri deepLink = null;
            if (pendingDynamicLinkData != null) {
                deepLink = pendingDynamicLinkData.getLink();
            }

            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user == null && deepLink != null && deepLink.getBooleanQueryParameter("referredBy", false) && deepLink.getBooleanQueryParameter("value", false)) {
                Log.e("MainActivity ", "Here's the deep link URL:\n" + deepLink.toString());
                String referredBy = deepLink.getQueryParameter("referredBy");
//                String offerValue = deepLink.getQueryParameter("value");
//                editor.putString("offerValue", offerValue);

                NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.activity_main_nav_host);
                if (navHostFragment != null) {
                    Fragment fragment = navHostFragment.getChildFragmentManager().getFragments().get(0);
                    if (fragment instanceof HomeFragment) {
                        ((HomeFragment) fragment).open(referredBy);
                    }
                }
            }
        }).addOnFailureListener(e -> Toast.makeText(this, "Oops, we couldn't fetch details", Toast.LENGTH_SHORT).show());
    }

    @Override
    protected void onStart() {
        super.onStart();
        checkForDynamicLink();

        NetworkConnectionStateMonitor networkConnectionStateMonitor = new NetworkConnectionStateMonitor(this);
        Snackbar snackbar = Snackbar.make(snackBarLayout, "cannot connect to internet", Snackbar.LENGTH_INDEFINITE).setAction("Action", null);
        networkConnectionStateMonitor.observe(this, isAvailable -> {
            if (!isAvailable) {
                customSnackBar(snackbar);
                snackbar.setBehavior(new SnackbarNoSwipeBehavior());
                snackbar.show();
            } else {
                checkForDynamicLink();
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
                if (FirebaseAuth.getInstance().getUid() == null)
                    Toast.makeText(this, "login first", Toast.LENGTH_SHORT).show();
                else
                    showLogoutDialog();
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
                createReferLink();
                break;
            case R.id.nav_menu_sub_reward:
                smoothActionBarDrawerToggle.runWhenIdle(() -> checkAuthAndNavigate(R.id.action_homeFragment_to_rewardFragment));
                drawerLayout.closeDrawers();
                break;
            case R.id.navigation_menu_login:
                smoothActionBarDrawerToggle.runWhenIdle(() -> {
                    if (FirebaseAuth.getInstance().getUid() == null) {
                        startActivity(new Intent(this, LoginActivity.class));
                    } else {
                        Toast.makeText(this, "already logged in", Toast.LENGTH_SHORT).show();
                    }
                });
                drawerLayout.closeDrawers();
                break;
            case R.id.nav_menu_follow_us:
                showFollowusDialog();
                break;
            case R.id.nav_menu_sub_about:
                checkAuthAndNavigate(R.id.action_homeFragment_to_aboutFragment);
                break;
            case R.id.nav_menu_sub_tutorial:
                showTutorialAlertDialog();
                break;
            case R.id.navigation_menu_rate_us:
                showPlaystoreRateUs();
                break;
            case R.id.nav_menu_sub_order_on_phone:
            case R.id.nav_menu_sub_help:
                smoothActionBarDrawerToggle.runWhenIdle(() -> checkAuthAndNavigate(R.id.action_homeFragment_to_orderOnPhone));
                drawerLayout.closeDrawers();
                break;
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private void showPlaystoreRateUs() {
        Uri uri = Uri.parse("market://details?id=com.pickleindia.pickle");
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);

        try {
            startActivity(intent);
        }catch (Exception xe) {
            startActivity(new Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse(
                            "http://play.google.com/store/apps/details?id=$com.pickleindia.pickle"
                    )
            ));
        }

    }

    private void showLogoutDialog() {
        MaterialAlertDialogBuilder materialAlertDialogBuilder = new MaterialAlertDialogBuilder(this);
        materialAlertDialogBuilder.setTitle("Logout");
        materialAlertDialogBuilder.setMessage("Would you like to logout?");
        materialAlertDialogBuilder.setPositiveButton("Yes", ((dialog, which) -> {
            FirebaseAuth.getInstance().signOut();
            SharedPrefsUtils.clearCart(this);
            updateIconItems();
            startActivity(new Intent(this, LoginActivity.class));
        })).setNegativeButton("No", ((dialog, which) -> {

        }));
        materialAlertDialogBuilder.show();
    }

    private void showTutorialAlertDialog() {
        MaterialAlertDialogBuilder materialAlertDialogBuilder = new MaterialAlertDialogBuilder(this);
        materialAlertDialogBuilder.setTitle("Tutorial");
        materialAlertDialogBuilder.setMessage("Would you like to open Youtube? To watch the tutorials on how to use the app");
        materialAlertDialogBuilder.setPositiveButton("Open", (dialog, which) -> {
            Intent youtube = new Intent(Intent.ACTION_VIEW);
            youtube.setData(Uri.parse("https://www.youtube.com/channel/UCggJVmXwRCRnGJ3nsn8Vtog"));
            youtube.setPackage("com.google.android.youtube");
            startActivity(youtube);
        }).setNegativeButton("back", ((dialog, which) -> {
        }));
        materialAlertDialogBuilder.show();
    }

    private void showFollowusDialog() {
        MaterialAlertDialogBuilder materialAlertDialogBuilder = new MaterialAlertDialogBuilder(this);
        LayoutFollowUsAlertDialogBinding layoutFollowUsAlertDialogBinding = DataBindingUtil.inflate(
                getLayoutInflater(),
                R.layout.layout_follow_us_alert_dialog,
                null,
                false
        );
        layoutFollowUsAlertDialogBinding.setActivity(this);
        layoutFollowUsAlertDialogBinding.facebookButton.setOnClickListener(n -> openFacebookIntent());
        layoutFollowUsAlertDialogBinding.instaButton.setOnClickListener(n -> openInstagramIntent());
        layoutFollowUsAlertDialogBinding.twitterButton.setOnClickListener(n -> openTwitterIntent());
        materialAlertDialogBuilder.setView(layoutFollowUsAlertDialogBinding.getRoot());
        materialAlertDialogBuilder.show();
    }

    private void openFacebookIntent() {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        try {
            int versionCode = this.getPackageManager().getPackageInfo("com.facebook.katana", 0).versionCode;
            String uri = "";
            if (versionCode >= 3002850) {
                uri = "fb://facewebmodal/f?href=" + "https://www.facebook.com/pickleindia.mart/";
            } else {
                uri = "fb://page/" + "pickleindia.mart";
            }
            intent.setData(Uri.parse(uri));
            startActivity(intent);

        } catch (PackageManager.NameNotFoundException | ActivityNotFoundException e) {
            intent.setData(Uri.parse("https://www.facebook.com/pickleindia.mart/"));
            startActivity(intent);
        }
    }

    public void openInstagramIntent() {
        Uri uri = Uri.parse("https://www.instagram.com/pickleindia.mart/?igshid=gy5umm9paa8t");
        try {
            Intent intagram = new Intent(Intent.ACTION_VIEW);
            intagram.setData(uri);
            intagram.setPackage("com.instagram.android");
            startActivity(intagram);
        } catch (ActivityNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW).setData(uri));
        }
    }

    private void openTwitterIntent() {
        Uri uri = Uri.parse("https://twitter.com/pickle_india?s=09");

        try {
            Intent twitter = new Intent(Intent.ACTION_VIEW);
            twitter.setData(uri);
            twitter.setPackage("com.twitter.android");
            startActivity(twitter);
        } catch (ActivityNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW).setData(uri));
        }
    }

    private void checkAuthAndNavigate(int id) {
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            startActivity(new Intent(this, LoginActivity.class));
            Toast.makeText(this, "Login First", Toast.LENGTH_LONG).show();
        } else {

            NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.activity_main_nav_host);
            Fragment fragment = navHostFragment.getChildFragmentManager().getFragments().get(0);

            if (fragment instanceof HomeFragment) {
                navController.navigate(id);
            }
        }
    }

    public void createReferLink() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            startActivity(new Intent(this, LoginActivity.class).addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY));
            Toast.makeText(this, "Login First", Toast.LENGTH_LONG).show();
        } else {
            String uid = user.getUid();
            String link = "https://officialpickleindia.com/?referredBy=" + uid + "&value=" + "50 ";
            DynamicLink dynamicLink = FirebaseDynamicLinks.getInstance().createDynamicLink()
                    .setLink(Uri.parse(link))
                    .setDomainUriPrefix("https://officialpickleindia.page.link")
                    .setAndroidParameters(new DynamicLink.AndroidParameters.Builder("com.pickleindia.pickle").build())
                    .buildDynamicLink();

            Uri dynamicLinkUri = dynamicLink.getUri();
            buildInvitation(dynamicLinkUri);
        }
    }

    public void buildInvitation(Uri dynamicLinkUri) {
        String referrerUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String msg = String.format("Download PickleIndia via link %s to get exciting offer \n link: %s", referrerUid, dynamicLinkUri.toString());
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, msg);
        startActivity(Intent.createChooser(intent, "Share using"));

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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (data != null && data.getExtras() != null) {
            NavHostFragment navHostFragment = (NavHostFragment) this.getSupportFragmentManager().findFragmentById(R.id.activity_main_nav_host);
            Fragment fragment = navHostFragment.getChildFragmentManager().getFragments().get(0);
            if (fragment instanceof ProductsFragment) {
                Bundle bundle = new Bundle();
                bundle.putString(PRODUCT_TYPE, data.getExtras().getString(PRODUCT_TYPE));
                navController.popBackStack();
                navController.navigate(R.id.action_homeFragment_to_productsFragment, bundle);
            }
        }
    }


}
