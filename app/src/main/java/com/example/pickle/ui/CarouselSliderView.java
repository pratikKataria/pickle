package com.example.pickle.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;

public class CarouselSliderView extends BaseSliderView {

    public CarouselSliderView(Context context) {
        super(context);
    }

    @Override
    public View getView() {
        View v = LayoutInflater.from(getContext()).inflate(com.daimajia.slider.library.R.layout.render_type_default,null);
        ImageView imageView = (ImageView)v.findViewById(com.daimajia.slider.library.R.id.daimajia_slider_image);
        imageView.setPadding(12, 8, 12, 8);
        v.setOnClickListener(n -> {
            Toast.makeText(mContext, "selected ", Toast.LENGTH_SHORT).show();
        });



        bindEventAndShow(v, imageView);
        return v;
    }
}
