package com.example.pickle.adapters.viewholders;

import android.view.View;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;

import com.example.pickle.R;
import com.example.pickle.databinding.CardviewLoadingBinding;
import com.example.pickle.empty.EmptyActivity;
import com.example.pickle.models.EmptyState;
import com.example.pickle.models.LoadingModel;

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
