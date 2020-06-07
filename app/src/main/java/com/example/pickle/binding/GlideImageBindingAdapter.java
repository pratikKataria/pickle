package com.example.pickle.binding;

import android.content.Context;
import android.media.Image;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import androidx.cardview.widget.CardView;
import androidx.databinding.BindingAdapter;

import com.bumptech.glide.GenericTransitionOptions;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.pickle.R;
import com.example.pickle.product.ProductsFragment;
import com.example.pickle.utils.ResourceUtils;

import static com.example.pickle.utils.Constant.PRODUCT_BUNDLE;

public class GlideImageBindingAdapter {

    @BindingAdapter("customBackgroundColor")
    public static void productFragmentCustomCardViewBackground(CardView cardView, String type) {
        Log.e(GlideImageBindingAdapter.class.getName(), "custom backgound " + type);
        if (type == null) {
            return;
        }

        if (ResourceUtils.backgroundColors.containsKey(type)) {
            cardView.setCardBackgroundColor(
                    cardView.getContext()
                            .getResources()
                            .getColor(
                                    ResourceUtils.backgroundColors.get(type)
                            )
            );
        }
    }

    @BindingAdapter("customImageBackgroundS")
    public static void productFragmentCustomImageBackground(ImageView imageview, String type) {
        Log.e(GlideImageBindingAdapter.class.getName(), "custom  img backgound " + type);
        if (type == null) {
            return;
        }

        if (ResourceUtils.backgroundColors.containsKey(type)) {
            imageview.setImageDrawable(
                    imageview.getContext()
                            .getResources()
                            .getDrawable(
                                    ResourceUtils.foregroundImages.get(type)
                            )
            );
        }
    }

    @BindingAdapter("customLayoutBackground")
    public static void setCustomBackground(View view, int colorRes) {
        Log.e(GlideImageBindingAdapter.class.getName(), "custom  lay backgound " + colorRes);
        try {
            view.setBackground(view.getContext().getResources().getDrawable(colorRes));
        } catch (Exception xe) {
            Log.e("GlideBindingAdapter", xe.getMessage());
        }
    }

    @BindingAdapter("customBackgroundColor")
    public static void productFragmentCustomCardViewBackground(CardView cardView, int resource) {
        Log.e(GlideImageBindingAdapter.class.getName(), "custom int backgound " + resource);
        cardView.setCardBackgroundColor(
                    cardView.getContext()
                            .getResources()
                            .getColor(
                                    resource
                            )
            );
    }

    @BindingAdapter("customImageBackgroundI")
    public static void productFragmentCustomImageBackground(ImageView imageview, int resource) {
        Log.e(GlideImageBindingAdapter.class.getName(), "custom int  img backgound " + resource);
        if (resource == 0)
                return;

            imageview.setImageDrawable(
                    imageview.getContext()
                            .getResources()
                            .getDrawable(
                                   resource
                            )
            );
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
