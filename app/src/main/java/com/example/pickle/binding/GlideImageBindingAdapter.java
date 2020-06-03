package com.example.pickle.binding;

import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import androidx.cardview.widget.CardView;
import androidx.databinding.BindingAdapter;

import com.bumptech.glide.GenericTransitionOptions;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.pickle.R;

public class GlideImageBindingAdapter {

    @BindingAdapter("customImageResource")
    public static void setImageCustom(ImageView imageView, int imageRes) {
        try {
            imageView.setImageDrawable(imageView.getContext().getDrawable(imageRes));
        } catch (Exception xe) {
            Log.e("GlideImageBindingAdapter", xe.getMessage());
        }
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
        try {
            view.setBackground(view.getContext().getDrawable(colorRes));
        } catch (Exception xe) {
            Log.e("GlideImageBindingAdapter", xe.getMessage());
        }
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
                .transition(GenericTransitionOptions.with(R.anim.fade_in))
                .into(_imageView);
    }
}
