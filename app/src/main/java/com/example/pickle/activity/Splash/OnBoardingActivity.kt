package com.example.pickle.activity.Splash

import android.R.attr.endX
import android.R.attr.startX
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.text.Html
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import com.example.pickle.HorizontalFlipTransformation
import com.example.pickle.R
import com.example.pickle.SliderAdapter
import com.example.pickle.activity.Login.LoginActivity
import kotlinx.android.synthetic.main.activity_on_boarding.*


class OnBoardingActivity : AppCompatActivity() {

    private var dotes = arrayOfNulls<TextView>(4)
    private var currentPage = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_on_boarding)

//      startActivity(Intent(this@OnBoardingActivity, MainActivity::class.java))


        val slidingAdapter = SliderAdapter(this)

        ob_activity_vp_page.adapter = slidingAdapter

//        if (ob_activity_vp_page.childCount == 3)
//        ob_activity_vp_page_mb_get_started


        dotIndicator(0)

        ob_activity_vp_page.addOnPageChangeListener(viewPagerPageChangeListener)

        ob_activity_mb_next.setOnClickListener {
            ob_activity_vp_page.currentItem = currentPage + 1
//            startActivity(Intent(this@OnBoardingActivity, LoginActivity::class.java))
        }

        ob_activity_mb_back.visibility = GONE

        ob_activity_vp_page.setPageTransformer(true,HorizontalFlipTransformation() )

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
                    ob_activity_vp_page_mb_get_started.visibility = GONE


                    ob_activity_mb_next.text = "next"
                    ob_activity_mb_back.text = ""
                } else if (position == dotes.size - 1) {
                    ob_activity_mb_back.isEnabled = true
                    ob_activity_mb_back.visibility = VISIBLE

//                    var anim : Animation = AnimationUtils.loadAnimation(this@OnBoardingActivity, R.anim.move_up)
//                    anim.duration = 1000
//                    anim.interpolator =  LinearInterpolator()
//                    anim.start()

                    ob_activity_vp_page_mb_get_started.visibility = VISIBLE
                    val objectAnimator: ObjectAnimator = ObjectAnimator.ofFloat(
                        ob_activity_vp_page_mb_get_started,
                        "translationY",
                        0F,
                        -100F
                    )
                    objectAnimator.duration = 1000
                    objectAnimator.start()

//                    ob_activity_vp_page_mb_get_started.animation = anim


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
                    ob_activity_vp_page_mb_get_started.visibility = GONE

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
            dotes[x]!!.textSize = 24F
            dotes[x]!!.setTextColor(resources.getColor(R.color.colorBlackTransparent))

            ob_activity_ll.addView(dotes[x])
        }

        if (dotes.size > 0) {
            dotes[position]!!.setTextColor(resources.getColor(R.color.darkBlue))
        }
    }


}
