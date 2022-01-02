package com.pickleindia.pickle.utils;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.viewpager.widget.ViewPager;

import com.pickleindia.pickle.R;

public class HorizontalFlipTransformation implements ViewPager.PageTransformer {
//    @Override
//    public void transformPage(View page, float position) {
//
//        page.setTranslationX(-position * page.getWidth());
//        page.setCameraDistance(12000);
//
//        if (position < 0.5 && position > -0.5) {
//            page.setVisibility(View.VISIBLE);
//        } else {
//            page.setVisibility(View.INVISIBLE);
//        }
//
//
//        if (position < -1) {     // [-Infinity,-1)
//            page.setAlpha(0);
//
//        } else if (position <= 0) {    // [-1,0]
//            page.setAlpha(1);
//            page.setRotationY(180 * (1 - Math.abs(position) + 1));
//
//        } else if (position <= 1) {    // (0,1]
//            page.setAlpha(1);
//            page.setRotationY(-180 * (1 - Math.abs(position) + 1));
//
//        } else {
//            page.setAlpha(0);
//        }
//    }

    @Override
    public void transformPage(View view, float position) {

/*        TextView heading = view.findViewById(R.id.ob_activity_tv_heading);
        TextView description = view.findViewById(R.id.ob_activity_tv_description);*/
        ImageView imageView = view.findViewById(R.id.ob_activity_iv_image);

        view.setTranslationX(-position * view.getWidth());
        view.setCameraDistance(12000);

        if (position < 0.5 && position > -0.5) {
            view.setVisibility(View.VISIBLE);
        } else {
            view.setVisibility(View.INVISIBLE);
        }


        if (position < -1) {     // [-Infinity,-1)
            view.setAlpha(0);

        } else if (position <= 0) {    // [-1,0]
            view.setAlpha(1);
            view.setRotationY(180 * (1 - Math.abs(position) + 1));

        } else if (position <= 1) {    // (0,1]
            view.setAlpha(1);
            view.setRotationY(-180 * (1 - Math.abs(position) + 1));

        } else {
            view.setAlpha(0);
        }
    }
}
