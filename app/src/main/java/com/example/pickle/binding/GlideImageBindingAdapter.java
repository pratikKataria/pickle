package com.example.pickle.binding;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.os.Build;
import android.view.View;
import android.widget.ImageView;

import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.databinding.BindingAdapter;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.pickle.R;

import static com.example.pickle.utils.Constant.BEVERAGES;
import static com.example.pickle.utils.Constant.DAIRY;
import static com.example.pickle.utils.Constant.FRUITS;
import static com.example.pickle.utils.Constant.VEGETABLES;

public class GlideImageBindingAdapter {

    @BindingAdapter("customImageResource")
    public static void setImageCustom(ImageView imageView, int imageRes) {
            imageView.setImageDrawable(imageView.getContext().getDrawable(imageRes));
    }

    @BindingAdapter("customBackgroundColor")
    public static void setCustomBackground(CardView cardView, int colorRes) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            cardView.setBackgroundColor(cardView.getContext().getColor(colorRes));
        } else {
            cardView.setBackgroundColor(cardView.getContext().getResources().getColor(colorRes));
        }
    }

    @BindingAdapter("customBackground")
    public static void setCustomBackground(View view, int colorRes) {
        view.setBackground(view.getContext().getDrawable(colorRes));
    }

    @BindingAdapter("imageResourceAdapter")
    public static void setImage(ImageView _imageView, String imageUrl) {

        Context context = _imageView.getContext();

        RequestOptions requestOptions = new RequestOptions()
                .placeholder(R.drawable.img_loading)
                .error(R.drawable.img_loading);

        Glide.with(context)
                .setDefaultRequestOptions(requestOptions)
                .load(imageUrl)
                .into(_imageView);
    }
}
