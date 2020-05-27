package com.example.pickle.main;


import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.wifi.WifiManager
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.example.pickle.Login.LoginActivity
import com.example.pickle.R
import com.example.pickle.SmoothActionBarDrawerToggle
import com.example.pickle.cart.CartActivity
import com.example.pickle.interfaces.IFragmentCb
import com.example.pickle.interfaces.IMainActivity
import com.example.pickle.interfaces.NavigationAction.NAVIGATE_TO_PRODUCTS
import com.example.pickle.models.ProductModel
import com.example.pickle.navigation.HomeFragment
import com.example.pickle.network.ConnectivityReceiver
import com.example.pickle.network.NetworkConnectionStateMonitor
import com.example.pickle.utils.BundleUtils
import com.example.pickle.utils.Constant.PRODUCT_BUNDLE
import com.example.pickle.utils.SharedPrefsUtils
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener,
    IMainActivity, IFragmentCb {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    private lateinit var _navController: NavController;
    private lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var drawerToggle: SmoothActionBarDrawerToggle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        drawerLayout = findViewById(R.id.activity_main_drawer_layout)
        navigationView = findViewById(R.id.activity_main_nv_side_navigation);
        bottomNavigationView = findViewById(R.id.activity_main_cnb_bottom_nav)
        bottomNavigationView.itemIconTintList = null;

        drawerToggle = SmoothActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(drawerToggle);


        drawerLayout.setOnClickListener {
           startActivity(Intent(this@MainActivity, CartActivity::class.java))
        }

        activity_main_fab_add_item.setOnClickListener { startActivity(Intent(this@MainActivity, AddNewItemActivity::class.java)) }

        navigationView.setNavigationItemSelectedListener(this)
        var navHostFragment = supportFragmentManager.findFragmentById(R.id.activity_main_nav_host) as NavHostFragment;
        _navController = navHostFragment.navController
        NavigationUI.setupWithNavController(bottomNavigationView, navHostFragment.navController)
        _navController.navigate(R.id.action_orderFragment_to_nav_menu_sub_orders) //todo throws an  exception 001

        try {
            //todo fix bug here
            val navigationTo: String? = intent.extras?.getString(PRODUCT_BUNDLE)
            if (navigationTo != null) {
                _navController.navigate(NAVIGATE_TO_PRODUCTS, BundleUtils.setNavigationBundle(navigationTo))
            }
        } catch (xe : Exception) {
            Log.e(MainActivity::class.java.name, xe.message)
        }


    }

    var connectivityReceiver = ConnectivityReceiver();

    override fun onStart() {
        super.onStart()

        var connectionMonitor = NetworkConnectionStateMonitor(this);
        connectionMonitor.observe(this, Observer {
            Log.e("connectivity receiver", "network $it");
        } )
//        val intentFilter = IntentFilter(WifiManager.WIFI_STATE_CHANGED_ACTION);
//        registerReceiver(connectivityReceiver, intentFilter);
    }

    override fun onStop() {
        super.onStop()
//        unregisterReceiver(connectivityReceiver)
//        ConnectivityReceiver.registerNetworkCallback(applicationContext);
    }


    @SuppressLint("WrongConstant")
    public fun openDrawer() {
        var drawerLayout : DrawerLayout = findViewById(R.id.activity_main_drawer_layout)
        drawerLayout.openDrawer(Gravity.START)
    }

    override fun onNavigationItemSelected(p0: MenuItem): Boolean {
        when(p0.itemId) {
            R.id.navigation_menu_logout -> {
                Toast.makeText(this, "logout", Toast.LENGTH_SHORT).show()
                FirebaseAuth.getInstance().signOut()
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }
            R.id.nav_menu_sub_orders -> {
                try {
                    drawerToggle.runWhenIdle {
                        _navController.navigate(R.id.action_orderFragment_to_nav_menu_sub_orders)
                    }
                    drawerLayout.closeDrawers()
                } catch (xe : Exception) {
                    Log.e("MainActivity", xe.message);
                }
            }
        }
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    override fun updateQuantity(productModel: ProductModel?, quantity: Int) {
        if (productModel != null) {
            productModel.quantityCounter = quantity
            SharedPrefsUtils.setStringPreference(this, productModel.itemId, Gson().toJson(productModel))

            //send update message to fragment order
            val navHostFragment : NavHostFragment = this.supportFragmentManager.findFragmentById(R.id.activity_main_nav_host) as NavHostFragment;
            val fragment = navHostFragment.childFragmentManager.fragments[0]
            if (fragment is HomeFragment) {
                val iFragmentCb = fragment as IFragmentCb
                iFragmentCb.updateIconItems()
            }
        }
    }

    override fun removeProduct(productModel: ProductModel?) {
        SharedPrefsUtils.removeValuePreference(this, productModel!!.itemId)

        //send update message to fragment order
        val navHostFragment : NavHostFragment = this.supportFragmentManager.findFragmentById(R.id.activity_main_nav_host) as NavHostFragment;
        val fragment = navHostFragment.childFragmentManager.fragments[0]
        if (fragment is HomeFragment) {
            val iFragmentCb = fragment as IFragmentCb
            iFragmentCb.updateIconItems()
        }
    }

    override fun play() {}

    override fun updateIconItems() {

    }
}
