package com.pickleindia.pickle.adapters.viewholders;

import androidx.annotation.LayoutRes;

import com.pickleindia.pickle.R;
import com.pickleindia.pickle.databinding.CardviewEmptyOrderBinding;
import com.pickleindia.pickle.models.EmptyState;

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
