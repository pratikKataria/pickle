package com.example.pickle.adapters.viewholders;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.LayoutRes;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.pickle.R;
import com.example.pickle.databinding.CardviewEmptyOrderBinding;
import com.example.pickle.models.EmptyState;

public class OrderHeaderViewHolder extends  AbstractViewHolder<EmptyState>{

    CardviewEmptyOrderBinding binding;

    @LayoutRes
    public static final int LAYOUT = R.layout.cardview_empty_order;

    public OrderHeaderViewHolder(CardviewEmptyOrderBinding binding) {
        super(binding.getRoot());
        this.binding = binding;
    }

    @Override
    public void bind(EmptyState element) {
        binding.setEmptyOrders(element);
        binding.executePendingBindings();
    }
}
