package com.example.pickle

import android.content.Intent
import android.os.Bundle
import android.text.Html
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import kotlinx.android.synthetic.main.activity_main.*

class OnBoardingActivity : AppCompatActivity() {

    private var dotes = arrayOfNulls<TextView>(4)
    private var currentPage = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        startActivity(Intent(this@OnBoardingActivity, LoginActivity::class.java))


        val slidingAdapter = SliderAdapter(this)

        ob_activity_vp_page.adapter = slidingAdapter

        dotIndicator(0)

        ob_activity_vp_page.addOnPageChangeListener(viewPagerPageChangeListener)

        ob_activity_mb_next.setOnClickListener {
            ob_activity_vp_page.currentItem = currentPage + 1
//            startActivity(Intent(this@OnBoardingActivity, LoginActivity::class.java))
        }

        ob_activity_mb_back.visibility = GONE

        ob_activity_mb_back.setOnClickListener {
            ob_activity_vp_page.currentItem = currentPage - 1
        }

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
                currentPage = position

                if (position == 0) {
                    ob_activity_mb_next.isEnabled = true
                    ob_activity_mb_back.isEnabled = false
                    ob_activity_mb_back.visibility = GONE

                    ob_activity_mb_next.text = "next"
                    ob_activity_mb_back.text = ""
                } else if (position == dotes.size - 1) {
                    ob_activity_mb_back.isEnabled = true
                    ob_activity_mb_back.visibility = VISIBLE

                    ob_activity_mb_next.setOnClickListener {
                        if (ob_activity_mb_next.text == "finish") {
                            startActivity(
                                Intent(
                                    this@OnBoardingActivity,
                                    LoginActivity::class.java
                                )
                            )
                            finish()
                        } else {
                            ob_activity_vp_page.currentItem = currentPage + 1
                        }
                    }
                    ob_activity_mb_next.text = "finish"
                    ob_activity_mb_back.text = "back"
                } else {
                    ob_activity_mb_next.isEnabled = true
                    ob_activity_mb_back.isEnabled = true
                    ob_activity_mb_back.visibility = VISIBLE

                    ob_activity_mb_back.text = "back"
                    ob_activity_mb_next.text = "next"
                }
            }

            override fun onPageScrollStateChanged(state: Int) {
            }
        }

    fun dotIndicator(position: Int) {
        ob_activity_ll.removeAllViews()
        for (x in dotes.indices) {
            dotes[x] = TextView(this)
            dotes[x]!!.text = Html.fromHtml("&#8226;")
            dotes[x]!!.textSize = 35F
            dotes[x]!!.setTextColor(resources.getColor(R.color.colorBlackTransparent))

            ob_activity_ll.addView(dotes[x])
        }

        if (dotes.size > 0) {
            dotes[position]!!.setTextColor(resources.getColor(R.color.colorPrimary))
        }
    }
}
