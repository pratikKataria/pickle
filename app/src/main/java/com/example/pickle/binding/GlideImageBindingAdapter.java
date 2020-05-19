package com.example.pickle.binding;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.widget.ImageView;

import androidx.cardview.widget.CardView;
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
    public static void setImageCustom(ImageView _imageView, String imageResource) {
        switch (imageResource) {
            case FRUITS:
                _imageView.setImageDrawable(_imageView.getContext().getDrawable(R.drawable.ic_fruits));
                break;
            case BEVERAGES:
                _imageView.setImageDrawable(_imageView.getContext().getDrawable(R.drawable.ic_beverages));
                break;
            case DAIRY:
                _imageView.setImageDrawable(_imageView.getContext().getDrawable(R.drawable.ic_dairy));
                break;
            case VEGETABLES:
                _imageView.setImageDrawable(_imageView.getContext().getDrawable(R.drawable.ic_vegetable));
                break;

        }
    }

    @BindingAdapter("customBackground")
    public static void setCustomBackground(CardView cardView, String imageResource) {
        switch (imageResource) {
            case FRUITS:
                cardView.setBackgroundColor(cardView.getContext().getColor(R.color.icFruitBg));
                break;
            case BEVERAGES:
                cardView.setBackgroundColor(cardView.getContext().getColor(R.color.icBeveragesBg));
                break;
            case DAIRY:
                cardView.setBackgroundColor(cardView.getContext().getColor(R.color.icDairyBg));
                break;
            case VEGETABLES:
                cardView.setBackgroundColor(cardView.getContext().getColor(R.color.icVegetableBg));
                break;

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
                .into(_imageView);
    }
}
