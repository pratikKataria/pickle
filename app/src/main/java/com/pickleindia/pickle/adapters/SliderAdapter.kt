package com.pickleindia.pickle.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.viewpager.widget.PagerAdapter
import com.pickleindia.pickle.R
import kotlinx.android.synthetic.main.sliding_layout.view.*

class SliderAdapter : PagerAdapter {

    lateinit var context: Context;
    lateinit var layoutInflater: LayoutInflater

    constructor(context: Context) {
        this.context = context
    }

    private val drawable = arrayOf(
        R.drawable.onboarding_screen_one,
        R.drawable.onboarding_screen_two,
        R.drawable.onboarding_screen_three,
        R.drawable.onboarding_screen_four
        )

    private val textHeading = arrayOf(
        "Fruits, Vegetables, Beverages ",
        "Exact Delivery Location",
        "Cash On Delivery",
        "Faster Delivery"
    )

    private val description = arrayOf(
        "Fresh Vegetables, Fruits, Grocery, Households, Personal Care and many more available at your door",
        "Delivery with exact location all over Indore",
        "No need to pay before, Pay When you get it, COD available in Indore",
        "Fast delivery, with no extra cost "
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
     /*   view.ob_activity_tv_heading.text = textHeading[position]
        view.ob_activity_tv_description.text = description[position]

*/

        container.addView(view)

        return view
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as ConstraintLayout)
    }
}