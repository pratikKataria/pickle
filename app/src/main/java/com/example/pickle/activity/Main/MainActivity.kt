package com.example.pickle.activity.Main

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
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

    lateinit var bottomNavigationView: ChipNavigationBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bottomNavigationView = findViewById(R.id.activity_main_cnb_bottom_nav)

        bottomNavigationView.setItemSelected(R.id.bottom_nav_orders)

        val fragmentx = OrderFragment()

        supportFragmentManager.beginTransaction().replace(R.id.activity_main_fl_fragment_loader, fragmentx).commit()

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

    override fun onStart() {
        super.onStart()

//        val mFirebaseUser = FirebaseAuth.getInstance()
//        if (mFirebaseUser.currentUser == null) {
//            startActivity(Intent(this, LoginActivity::class.java))
//            finish()
//        }
    }



}
