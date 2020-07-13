package com.pickleindia.pickle.adapters.viewholders;

import android.view.View;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;

import com.pickleindia.pickle.R;
import com.pickleindia.pickle.databinding.CardviewOrderedProductDetailBinding;
import com.pickleindia.pickle.models.OrdersDetails;

public class OrderedProductDetailsViewHolder extends AbstractViewHolder<OrdersDetails> {

    @LayoutRes
    public static final int LAYOUT = R.layout.cardview_ordered_product_detail;

    CardviewOrderedProductDetailBinding binding;

    public OrderedProductDetailsViewHolder(@NonNull CardviewOrderedProductDetailBinding itemView) {
        super(itemView.getRoot());
        binding = itemView;
    }

    @Override
    public void bind(OrdersDetails element) {
        binding.setOrderDetails(element);
        binding.executePendingBindings();
    }
}
