package com.example.pickle.adapters.viewholders;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.LayoutRes;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.pickle.R;
import com.example.pickle.databinding.CardviewEmptyOrderBinding;
import com.example.pickle.models.EmptyState;

public class EmptyViewHolder extends  AbstractViewHolder<EmptyState>{

    private Context context;
    private ImageView imageView;
    private ConstraintLayout constraintLayout;

    CardviewEmptyOrderBinding binding;

    @LayoutRes
    public static final int LAYOUT = R.layout.cardview_empty_order;

    public EmptyViewHolder(CardviewEmptyOrderBinding binding) {
        super(binding.getRoot());
        this.binding = binding;
    }

    @Override
    public void bind(EmptyState element) {
        binding.setEmptyOrders(element);
    }
}
