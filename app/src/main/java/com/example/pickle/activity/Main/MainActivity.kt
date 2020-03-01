package com.example.pickle.activity.Main

import android.annotation.SuppressLint
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.Gravity
import android.widget.Toolbar
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.example.pickle.R
import com.example.pickle.fragment.ExploreFragment
import com.example.pickle.fragment.OfferFragment
import com.example.pickle.fragment.OrderFragment
import com.ismaeldivita.chipnavigation.ChipNavigationBar

class MainActivity : AppCompatActivity() {

    lateinit var orderFragment: OrderFragment
    lateinit var exploreFragment: ExploreFragment
    lateinit var offerFragment: OfferFragment

    lateinit var toolbar: Toolbar
    lateinit var actionBarDrawerToggle: ActionBarDrawerToggle
    public lateinit var drawerLayout: DrawerLayout


    lateinit var bottomNavigationView: ChipNavigationBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        bottomNavigationView = findViewById(R.id.activity_main_cnb_bottom_nav)

        drawerLayout = findViewById(R.id.activity_main_drawer_layout)

        bottomNavigationView.setItemSelected(R.id.bottom_nav_orders)

        val currentFragment = OrderFragment()

        supportFragmentManager.beginTransaction().replace(R.id.activity_main_fl_fragment_loader, currentFragment).commit()

        bottomNavigationView.setOnItemSelectedListener {
            lateinit var fragment : Fragment
            when(bottomNavigationView.getSelectedItemId()) {
                    R.id.bottom_nav_orders -> {
                        fragment = OrderFragment()
                        loadFragment(fragment)
                        return@setOnItemSelectedListener
                    }
                    R.id.bottom_nav_offers -> {
                        fragment = OfferFragment()
                        loadFragment(fragment)
                        return@setOnItemSelectedListener
                    }
                    R.id.bottom_nav_explore -> {
                        fragment = ExploreFragment()
                        loadFragment(fragment)
                        return@setOnItemSelectedListener
                    }
                }
        }



    }

    private fun loadFragment(fragment: Fragment) {
        var fragmentManager : FragmentManager = supportFragmentManager
        fragmentManager.beginTransaction().replace(R.id.activity_main_fl_fragment_loader, fragment).addToBackStack(null).commit()
    }

    @SuppressLint("WrongConstant")
    public fun openDrawer() {
        var drawerLayout : DrawerLayout = findViewById(R.id.activity_main_drawer_layout)
        drawerLayout.openDrawer(Gravity.START)
    }

//    override fun onStart() {
//        super.onStart()
////        val mFirebaseUser = FirebaseAuth.getInstance()
////        if (mFirebaseUser.currentUser == null) {
////            startActivity(Intent(this, LoginActivity::class.java))
////            finish()
////        }
//    }



}
