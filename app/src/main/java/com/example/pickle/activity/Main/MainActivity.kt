package com.example.pickle.activity.Main

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.MenuItem
import android.widget.Toast
import android.widget.Toolbar
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.example.pickle.R
import com.example.pickle.activity.Login.LoginActivity
import com.example.pickle.activity.Main.NavigationFragment.ExploreFragment
import com.example.pickle.activity.Main.NavigationFragment.OfferFragment
import com.example.pickle.activity.Main.NavigationFragment.OrderFragment
import com.example.pickle.activity.Main.Options.AddNewItemActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    lateinit var navigationView: NavigationView
    private lateinit var drawerLayout: DrawerLayout

    lateinit var _navController : NavController;


    lateinit var bottomNavigationView: BottomNavigationView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        bottomNavigationView = findViewById(R.id.activity_main_cnb_bottom_nav)

        drawerLayout = findViewById(R.id.activity_main_drawer_layout)

        navigationView = findViewById(R.id.activity_main_nv_side_navigation);

        activity_main_fab_add_item.setOnClickListener { startActivity(Intent(this@MainActivity, AddNewItemActivity::class.java)) }

        navigationView.setNavigationItemSelectedListener(this)

        var navHostFragment = supportFragmentManager.findFragmentById(R.id.activity_main_nav_host) as NavHostFragment;
        if (navHostFragment != null) {
            _navController = navHostFragment.navController
            NavigationUI.setupWithNavController(bottomNavigationView, navHostFragment.navController);
        }

    }

    @SuppressLint("WrongConstant")
    public fun openDrawer() {
        var drawerLayout : DrawerLayout = findViewById(R.id.activity_main_drawer_layout)
        drawerLayout.openDrawer(Gravity.START)
    }

    override fun onStart() {
        super.onStart()
        val mFirebaseUser = FirebaseAuth.getInstance()
        if (mFirebaseUser.currentUser == null) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
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

}
