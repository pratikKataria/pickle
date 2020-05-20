package com.example.pickle.activity.main

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.example.pickle.R
import com.example.pickle.activity.Login.LoginActivity
import com.example.pickle.activity.main.navigation_fragment.OrderFragment
import com.example.pickle.activity.main.options.AddNewItemActivity
import com.example.pickle.binding.IFragmentCb
import com.example.pickle.binding.IMainActivity
import com.example.pickle.binding.NavigationAction.NAVIGATE_TO_PRODUCTS
import com.example.pickle.data.ProductModel
import com.example.pickle.utils.Constant.PRODUCT_BUNDLE
import com.example.pickle.utils.NavigationUtils
import com.example.pickle.utils.SharedPrefsUtils
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener,
    IMainActivity, IFragmentCb {

    lateinit var navigationView: NavigationView
    private lateinit var drawerLayout: DrawerLayout

    lateinit var _navController : NavController;


    lateinit var bottomNavigationView: BottomNavigationView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

//        val mFirebaseUser = FirebaseAuth.getInstance()
//        if (mFirebaseUser.currentUser == null) {
//            startActivity(Intent(this, LoginActivity::class.java))
//            finish()
//        }


        bottomNavigationView = findViewById(R.id.activity_main_cnb_bottom_nav)
        bottomNavigationView.itemIconTintList = null

        drawerLayout = findViewById(R.id.activity_main_drawer_layout)

        navigationView = findViewById(R.id.activity_main_nv_side_navigation);

        activity_main_fab_add_item.setOnClickListener { startActivity(Intent(this@MainActivity, AddNewItemActivity::class.java)) }

        navigationView.setNavigationItemSelectedListener(this)
        var navHostFragment = supportFragmentManager.findFragmentById(R.id.activity_main_nav_host) as NavHostFragment;
        _navController = navHostFragment.navController
        NavigationUI.setupWithNavController(bottomNavigationView, navHostFragment.navController)

        try {
            //todo fix bug here
            val navigationTo: String? = intent.extras?.getString(PRODUCT_BUNDLE)
            if (navigationTo != null) {
                _navController.navigate(NAVIGATE_TO_PRODUCTS, NavigationUtils.getNavBundle(navigationTo))
            }
        } catch (xe : Exception) {
            Log.e(MainActivity::class.java.name, xe.message)
        }
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
            if (fragment is OrderFragment) {
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
        if (fragment is OrderFragment) {
            val iFragmentCb = fragment as IFragmentCb
            iFragmentCb.updateIconItems()
        }
    }

    override fun play() {}

    override fun updateIconItems() {

    }
}
