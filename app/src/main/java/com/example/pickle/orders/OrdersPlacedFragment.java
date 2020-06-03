package com.example.pickle.orders;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.core.widget.NestedScrollView;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.pickle.R;
import com.example.pickle.adapters.VisitorForList;
import com.example.pickle.databinding.FragmentOrdersBinding;
import com.example.pickle.interfaces.Visitable;
import com.example.pickle.main.FirebaseSearchActivity;
import com.example.pickle.models.EmptyState;
import com.example.pickle.models.LoadingModel;
import com.example.pickle.models.Operation;
import com.example.pickle.models.Orders;
import com.example.pickle.models.OrdersDetails;
import com.google.android.material.transition.MaterialFadeThrough;

import java.util.ArrayList;

import static com.example.pickle.interfaces.OrderStatus.CANCEL;
import static com.example.pickle.utils.Constant.ADD;
import static com.example.pickle.utils.Constant.MODIFIED;
import static com.example.pickle.utils.Constant.REMOVE;


/**
 * A simple {@link Fragment} subclass.
 */
public class OrdersPlacedFragment extends Fragment {

    private FragmentOrdersBinding binding;
    private ArrayList<Visitable> ordersList;
    private ArrayList<Visitable> pastOrdersList;


    public OrdersPlacedFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        binding = DataBindingUtil.inflate(
                inflater,
                R.layout.fragment_orders,
                container,
                false);

        MaterialFadeThrough materialFadeThrough = MaterialFadeThrough.create();
        setEnterTransition(materialFadeThrough);

        ordersList = new ArrayList<>();
        pastOrdersList = new ArrayList<>();
        binding.setOrdersList(ordersList);
        binding.setPastOrdersList(pastOrdersList);
        binding.setVisitor(new VisitorForList());
        binding.setActivity(getActivity());
        binding.setIntent(new Intent(getActivity(), FirebaseSearchActivity.class).addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY));

        pastOrdersList.add(new EmptyState(R.drawable.crd_empty_past_order_bg, R.drawable.empty_past_orders_img, "Your past order's", "shows the history of order's past 6 month's"));

        initRecyclerViewScrollListener();

        OrdersViewModel ordersViewModel = new ViewModelProvider(this).get(OrdersViewModel.class);
        LiveData<Operation> firebaseQueryLiveData =  ordersViewModel.getLiveData();

        firebaseQueryLiveData.observe(getViewLifecycleOwner(), operation -> {
            updateHeaderView();
            switch (operation.mode) {
                case ADD:
                    OrdersDetails newOrdersDetails = (OrdersDetails) operation.aClass;
                    if (newOrdersDetails.isPastOrder) {
                        addPastOrders(newOrdersDetails);
                    } else {
                        addProduct(newOrdersDetails);
                    }
                    break;
                case MODIFIED:
                    Orders orderModified = (Orders) operation.aClass;
                    removeProduct(orderModified.getOrderId());
                    break;
                case REMOVE:
                    Log.e("ORDER PLACED FRAGMENT", "removed");
                    break;
            }

        });

        updateHeaderView();
        return binding.getRoot();
    }

    private void initRecyclerViewScrollListener() {
        binding.nestedScrollView.setOnScrollChangeListener((NestedScrollView.OnScrollChangeListener) (v, scrollX, scrollY, oldScrollX, oldScrollY) -> {

            Log.e("past ordersList ", "last item" + oldScrollY);

            if (scrollY < (oldScrollY-60) && pastOrdersList.get(pastOrdersList.size()-1) instanceof LoadingModel) {
                pastOrdersList.remove(pastOrdersList.size()-1);
                binding.recyclerViewPastOrders.getAdapter().notifyItemRemoved(pastOrdersList.size()-1);
            }

            if (v.getChildAt(v.getChildCount() - 1) != null) {
                if((scrollY >= (v.getChildAt(v.getChildCount() - 1).getMeasuredHeight() - v.getMeasuredHeight())) && scrollY > oldScrollY) {
                    LinearLayoutManager linearLayoutManager = (LinearLayoutManager) binding.recyclerViewPastOrders.getLayoutManager();
                    Log.e("past ordersList ", "last item" + (pastOrdersList.get(pastOrdersList.size() -1) instanceof LoadingModel));
                    if (linearLayoutManager != null) {
                        int firstVisibleProductPosition = linearLayoutManager.findFirstVisibleItemPosition();
                        int visibleProductCount = linearLayoutManager.getChildCount();
                        int totalProductCount = linearLayoutManager.getItemCount();
                        int lastIndex = pastOrdersList.size() - 1;
                        Log.e("before order", "orders placed fragment" + firstVisibleProductPosition + visibleProductCount );
                        if (firstVisibleProductPosition + visibleProductCount == totalProductCount) {
                            if (!(pastOrdersList.get(lastIndex) instanceof LoadingModel)) {
                                pastOrdersList.add(new LoadingModel());
                                notifyItemInsertedAtPastOrdersList(lastIndex);
                            }
                        }
                    }
                }
            }
        });
    }


    private void addProduct(OrdersDetails addedOrdersDetails) {
        ordersList.add(addedOrdersDetails);
        notifyItemInsertedAtOrdersList(ordersList.size());
    }

    private void addPastOrders(OrdersDetails pastOrderDetails) {
        pastOrdersList.add(pastOrderDetails);
        notifyItemInsertedAtPastOrdersList(ordersList.size());
    }

    private void removeProduct(String orderId) {
        //start from 1 because at 0 index there is empty state card holder
        for (int i = 1; i < ordersList.size(); i++) {
            OrdersDetails currentOrdersDetails = (OrdersDetails) ordersList.get(i);
            if (orderId.equals(currentOrdersDetails.orderId)) {
                short removeIndex = (short) ordersList.indexOf(currentOrdersDetails);
                ordersList.remove(currentOrdersDetails);
                currentOrdersDetails.status = CANCEL;
                ordersList.add(currentOrdersDetails);
                binding.recyclerView.getAdapter().notifyItemMoved(removeIndex, ordersList.size() - 1);
                notifyItemInsertedAtOrdersList(removeIndex);
                Log.e("OrdersPlacedFragment", currentOrdersDetails.toString());
                updateHeaderView();
            }
        }
    }

    private void notifyItemInsertedAtOrdersList(int index) {
        try {
            binding.recyclerView.getAdapter().notifyItemChanged(index);
        } catch (NullPointerException npe) {
            Log.e(OrdersPlacedFragment.class.getName(), npe.getMessage());
        }
    }

    private void notifyItemChangedAtOrdersList(int index) {
        try {
            binding.recyclerView.getAdapter().notifyItemChanged(index);
        } catch (NullPointerException npe) {
            Log.e(OrdersPlacedFragment.class.getName(), npe.getMessage());
        }
    }

    private void notifyItemInsertedAtPastOrdersList(int index) {
        try {
            binding.recyclerViewPastOrders.getAdapter().notifyItemChanged(index);
        } catch (NullPointerException npe) {
            Log.e(OrdersPlacedFragment.class.getName(), npe.getMessage());
        }
    }

    private void updateHeaderView() {
        if (ordersList.size() == 0) {
            ordersList.add(new EmptyState(R.drawable.crd_empty_order_bg, R.drawable.empty_cart_img, "Whoops", "its look like that no ongoing orders"));
            notifyItemInsertedAtOrdersList(0);
            notifyItemInsertedAtPastOrdersList(0);
        } else if (ordersList.size() == 2) {
            ordersList.remove(0);
            ordersList.add(0, new EmptyState(R.drawable.crd_order_bg, R.drawable.pablo_delivery_transparent, "Today's Orders", "your orders will be delivered as soon as possible"));
            notifyItemInsertedAtOrdersList(0);
        }
    }
}
