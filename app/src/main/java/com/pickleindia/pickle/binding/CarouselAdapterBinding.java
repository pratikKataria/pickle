package com.pickleindia.pickle.binding;

import androidx.databinding.BindingAdapter;

import com.pickleindia.pickle.carousel.CarouselAdapter;
import com.pickleindia.pickle.carousel.CarouselImage;
import com.yarolegovich.discretescrollview.DSVOrientation;
import com.yarolegovich.discretescrollview.DiscreteScrollView;
import com.yarolegovich.discretescrollview.InfiniteScrollAdapter;
import com.yarolegovich.discretescrollview.transform.ScaleTransformer;

import java.util.ArrayList;
import java.util.List;

public class CarouselAdapterBinding {

    @BindingAdapter("carouselList")
    public static void carouselBinding(DiscreteScrollView scrollView, List<CarouselImage> imageUrl) {
        if (imageUrl == null) {
            return;
        }

        InfiniteScrollAdapter infiniteScrollAdapter = (InfiniteScrollAdapter) scrollView.getAdapter();
        if (infiniteScrollAdapter == null) {
            infiniteScrollAdapter = InfiniteScrollAdapter.wrap(new CarouselAdapter((ArrayList<CarouselImage>)imageUrl));
            scrollView.setAdapter(infiniteScrollAdapter);
            scrollView.setOrientation(DSVOrientation.HORIZONTAL);
            scrollView.setItemTransitionTimeMillis(150);
            scrollView.setItemTransformer(new ScaleTransformer.Builder().setMinScale(0.8F).build());
        }
    }
}
