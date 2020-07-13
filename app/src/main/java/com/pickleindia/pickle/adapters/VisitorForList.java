package com.pickleindia.pickle.adapters;

import android.view.View;

import com.pickleindia.pickle.adapters.viewholders.AbstractViewHolder;
import com.pickleindia.pickle.adapters.viewholders.LoadingViewHolder;
import com.pickleindia.pickle.adapters.viewholders.OrderHeaderViewHolder;
import com.pickleindia.pickle.adapters.viewholders.OrderedProductDetailsViewHolder;
import com.pickleindia.pickle.adapters.viewholders.OrdersViewHolder;
import com.pickleindia.pickle.databinding.CardviewEmptyOrderBinding;
import com.pickleindia.pickle.databinding.CardviewLoadingBinding;
import com.pickleindia.pickle.databinding.CardviewOrderedProductDetailBinding;
import com.pickleindia.pickle.databinding.CardviewOrdersBinding;
import com.pickleindia.pickle.interfaces.Visitor;
import com.pickleindia.pickle.models.EmptyState;
import com.pickleindia.pickle.models.LoadingModel;
import com.pickleindia.pickle.models.Orders;
import com.pickleindia.pickle.models.OrdersDetails;

public class VisitorForList implements Visitor {
    @Override
    public int type(EmptyState emptyState) {
        return OrderHeaderViewHolder.LAYOUT;
    }

    @Override
    public int type(OrdersDetails ordersDetails) { return OrderedProductDetailsViewHolder.LAYOUT; }

    @Override
    public int type(Orders ordersDetails) {
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
          case OrderedProductDetailsViewHolder.LAYOUT:
              CardviewOrderedProductDetailBinding cardviewOrderedProductDetailBinding = CardviewOrderedProductDetailBinding.bind(parent);
              createViewHolder = new OrderedProductDetailsViewHolder(cardviewOrderedProductDetailBinding);
              break;
      }
      return createViewHolder;
    }
}
