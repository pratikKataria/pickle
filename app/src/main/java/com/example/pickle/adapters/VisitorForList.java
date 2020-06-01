package com.example.pickle.adapters;

import android.view.View;

import com.example.pickle.adapters.viewholders.AbstractViewHolder;
import com.example.pickle.adapters.viewholders.LoadingViewHolder;
import com.example.pickle.adapters.viewholders.OrderHeaderViewHolder;
import com.example.pickle.adapters.viewholders.OrdersViewHolder;
import com.example.pickle.databinding.CardviewEmptyOrderBinding;
import com.example.pickle.databinding.CardviewLoadingBinding;
import com.example.pickle.databinding.CardviewOrdersBinding;
import com.example.pickle.interfaces.Visitor;
import com.example.pickle.models.EmptyState;
import com.example.pickle.models.LoadingModel;
import com.example.pickle.models.OrdersDetails;

public class VisitorForList implements Visitor {
    @Override
    public int type(EmptyState emptyState) {
        return OrderHeaderViewHolder.LAYOUT;
    }

    @Override
    public int type(OrdersDetails ordersDetails) {
        return OrdersViewHolder.LAYOUT;
    }

    @Override
    public int type(LoadingModel loadingModel) {
        return LoadingViewHolder.LAYOUT;
    }

    @Override
    public AbstractViewHolder createViewHolder(View parent, int type) {
      AbstractViewHolder createViewHolder = null;
      switch (type) {
          case OrdersViewHolder.LAYOUT:
              CardviewOrdersBinding cardviewOrdersBinding = CardviewOrdersBinding.bind(parent);
              createViewHolder = new OrdersViewHolder(cardviewOrdersBinding);
              break;
          case OrderHeaderViewHolder.LAYOUT:
              CardviewEmptyOrderBinding cardviewEmptyOrderBinding = CardviewEmptyOrderBinding.bind(parent);
              createViewHolder = new OrderHeaderViewHolder(cardviewEmptyOrderBinding);
              break;
          case LoadingViewHolder.LAYOUT:
              CardviewLoadingBinding cardviewLoadingBinding = CardviewLoadingBinding.bind(parent);
              createViewHolder = new LoadingViewHolder(cardviewLoadingBinding);
              break;
      }
      return createViewHolder;
    }
}
