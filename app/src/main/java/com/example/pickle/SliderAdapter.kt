package com.example.pickle

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.viewpager.widget.PagerAdapter
import kotlinx.android.synthetic.main.sliding_layout.view.*

class SliderAdapter : PagerAdapter {

    lateinit var context: Context;
    lateinit var layoutInflater: LayoutInflater

    constructor(context: Context) {
        this.context = context
    }

    private val drawable = arrayOf(
        R.drawable.ic_ob_screen_i,
        R.drawable.ic_ob_screen_ii,
        R.drawable.ic_ob_screen_iii,
        R.drawable.ic_ob_screen_iv
        )

    private val textHeading = arrayOf(
        "Fruits, Vegetables, Beverages ",
        "Exact Delivery Location",
        "Cash On Delivery",
        "Faster Delivery"
    )

    private val description = arrayOf(
        "Fresh Vegetables, fruits and Beverages available at your Door with quality products",
        "Delivery with fine and exact location all over indore",
        "No need to pay before, Pay When you get it, COD available at indore",
        "Delivery within 30 min of order, with no extra coast"
    )

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view == `object`
    }

    override fun getCount(): Int {
        return textHeading.size
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {

        layoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view : View = layoutInflater.inflate(R.layout.sliding_layout, container, false)

        view.ob_activity_iv_image.setImageDrawable(view.resources.getDrawable(drawable[position]))
        view.ob_activity_tv_heading.text = textHeading[position]
        view.ob_activity_tv_description.text = description[position]

        container.addView(view)

        return view
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as ConstraintLayout)
    }
}