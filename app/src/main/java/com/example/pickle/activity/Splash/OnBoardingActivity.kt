package com.example.pickle.activity.Splash

import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.text.Html
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import com.example.pickle.utils.HorizontalFlipTransformation
import com.example.pickle.R
import com.example.pickle.adapters.SliderAdapter
import com.example.pickle.activity.Login.LoginActivity
import com.google.android.material.button.MaterialButton


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
                        startActivity(Intent(this@OnBoardingActivity, SplashActivity::class.java))
                        finish()
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
