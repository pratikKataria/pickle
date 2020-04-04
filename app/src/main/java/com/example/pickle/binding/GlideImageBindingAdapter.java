package com.example.pickle.binding;

import android.content.Context;
import android.widget.ImageView;
import androidx.databinding.BindingAdapter;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.pickle.R;

public class GlideImageBindingAdapter {

    @BindingAdapter("imageResourceAdapter")
    public static void setImage(ImageView _imageView, int imageResource) {

        Context context = _imageView.getContext();

        RequestOptions requestOptions = new RequestOptions()
                .placeholder(R.drawable.ic_launcher_background)
                .error(R.drawable.ic_launcher_background);

        Glide.with(context)
                .setDefaultRequestOptions(requestOptions)
                .load(imageResource)
                .into(_imageView);
    }

    @BindingAdapter("imageResourceAdapter")
    public static void setImage(ImageView _imageView, String imageUrl) {

        Context context = _imageView.getContext();

        RequestOptions requestOptions = new RequestOptions()
                .placeholder(R.drawable.ic_launcher_background)
                .error(R.drawable.ic_launcher_background);

        Glide.with(context)
                .setDefaultRequestOptions(requestOptions)
                .load(imageUrl)
                .into(_imageView);
    }
}
