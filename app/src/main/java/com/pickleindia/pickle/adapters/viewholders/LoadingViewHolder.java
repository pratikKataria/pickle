package com.pickleindia.pickle.adapters.viewholders;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;

import com.pickleindia.pickle.R;
import com.pickleindia.pickle.databinding.CardviewLoadingBinding;
import com.pickleindia.pickle.models.LoadingModel;

public class LoadingViewHolder extends AbstractViewHolder<LoadingModel> {

    private CardviewLoadingBinding cardviewLoadingBinding;

    @LayoutRes
    public static final int LAYOUT = R.layout.cardview_loading;

    public LoadingViewHolder(@NonNull CardviewLoadingBinding cardviewLoadingBinding) {
        super(cardviewLoadingBinding.getRoot());
        this.cardviewLoadingBinding = cardviewLoadingBinding;
    }

    @Override
    public void bind(LoadingModel element) {

    }
}
