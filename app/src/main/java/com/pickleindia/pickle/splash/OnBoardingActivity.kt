package com.pickleindia.pickle.splash

import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.text.Html
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import com.pickleindia.pickle.utils.HorizontalFlipTransformation
import com.pickleindia.pickle.R
import com.pickleindia.pickle.adapters.SliderAdapter
import com.pickleindia.pickle.Login.LoginActivity
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.FirebaseAuth


class OnBoardingActivity : AppCompatActivity() {

    private lateinit var viewPager: ViewPager
    private lateinit var dotLayout : LinearLayout
    private lateinit var getStartedBtn : MaterialButton
    private lateinit var skipBtn : ImageButton
    private var dotes = arrayOfNulls<TextView>(4)
    private var currentPage = 0

    private fun init_fields() {
        getStartedBtn = findViewById(R.id.ob_activity_mb_get_started)
        viewPager = findViewById(R.id.ob_activity_vp_page)
        dotLayout = findViewById(R.id.ob_activity_ll)
        skipBtn = findViewById(R.id.ob_activity_mb_skip)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_on_boarding)

        init_fields()

        val slidingAdapter = SliderAdapter(this)

        viewPager.adapter = slidingAdapter

        dotIndicator(0)

        viewPager.addOnPageChangeListener(viewPagerPageChangeListener)

        viewPager.setPageTransformer(true,
            HorizontalFlipTransformation()
        )

        skipBtn.setOnClickListener { startActivity(Intent(this@OnBoardingActivity, LoginActivity::class.java)) }
    }




    private val viewPagerPageChangeListener: ViewPager.OnPageChangeListener =
        object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
            }

            override fun onPageSelected(position: Int) {
                dotIndicator(position)
                if (position == 0) {
                    getStartedBtn.visibility = GONE
                } else if (position == dotes.size - 1) {

                    getStartedBtn.visibility = VISIBLE
                    val objectAnimator: ObjectAnimator = ObjectAnimator.ofFloat(
                        getStartedBtn,
                        "translationY",
                        0F,
                        -100F
                    )
                    objectAnimator.duration = 1000
                    objectAnimator.start()

                    getStartedBtn.setOnClickListener {
                        FirebaseAuth.getInstance().signInAnonymously().addOnCompleteListener {
                            if (it.isSuccessful) {
                                startActivity(Intent(this@OnBoardingActivity, SplashActivity::class.java))
                                finish()
                            } else {
                                Toast.makeText(this@OnBoardingActivity, it.exception!!.message, Toast.LENGTH_SHORT).show();
                            }
                        }
                    }

                } else {
                    getStartedBtn.visibility = GONE
                }
            }

            override fun onPageScrollStateChanged(state: Int) {
            }
        }

    fun dotIndicator(position: Int) {
        dotLayout.removeAllViews()
        for (x in dotes.indices) {
            dotes[x] = TextView(this)
            dotes[x]!!.text = Html.fromHtml("&#8226;")
            dotes[x]!!.textSize = 24F
            dotes[x]!!.setTextColor(resources.getColor(R.color.colorBlackTransparent))

            dotLayout.addView(dotes[x])
        }

        if (dotes.size > 0) {
            dotes[position]!!.setTextColor(resources.getColor(R.color.darkBlue))
        }
    }


}
