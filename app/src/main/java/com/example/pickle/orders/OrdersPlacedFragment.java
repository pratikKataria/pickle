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
import com.example.pickle.utils.NotifyRecyclerItems;
import com.google.android.material.transition.MaterialSharedAxis;

import java.util.ArrayList;

import static com.example.pickle.utils.Constant.ADD;
import static com.example.pickle.utils.Constant.LIMIT;
import static com.example.pickle.utils.Constant.MODIFIED;
import static com.example.pickle.utils.Constant.REMOVE;
import static com.example.pickle.utils.OrderStatus.CANCEL;


/**
 * A simple {@link Fragment} subclass.
 */
public class OrdersPlacedFragment extends Fragment {

    private FragmentOrdersBinding binding;
    private ArrayList<Visitable> ordersList;
    private ArrayList<Visitable> pastOrdersList;
    private OrdersViewModel ordersViewModel;

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

        setEnterTransition(MaterialSharedAxis.create(MaterialSharedAxis.X, true));

        ordersList = new ArrayList<>();
        pastOrdersList = new ArrayList<>();
        binding.setOrdersList(ordersList);
        binding.setPastOrdersList(pastOrdersList);
        binding.setVisitor(new VisitorForList());
        binding.setActivity(getActivity());
        binding.setIntent(new Intent(getActivity(), FirebaseSearchActivity.class).addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY));

        pastOrdersList.add(new EmptyState(R.drawable.crd_empty_past_order_bg, R.drawable.empty_past_orders_img, "Your past order's", "shows the history of order's past 6 month's"));

        initRecyclerViewScrollListener();

        ordersViewModel = new ViewModelProvider(this).get(OrdersViewModel.class);
        LiveData<Operation> firebaseQueryLiveData = ordersViewModel.orders();

        firebaseQueryLiveData.observe(getViewLifecycleOwner(), operation -> {
            updateHeaderView();
            addOrdersToType(operation);
        });

        updateHeaderView();
        return binding.getRoot();
    }

    private void initRecyclerViewScrollListener() {
        binding.nestedScrollView.setOnScrollChangeListener((NestedScrollView.OnScrollChangeListener) (v, scrollX, scrollY, oldScrollX, oldScrollY) -> {

            if (scrollY < (oldScrollY-60) && pastOrdersList.contains(LoadingModel.getInstance())) {
                pastOrdersList.remove(LoadingModel.getInstance());
                NotifyRecyclerItems.notifyItemRemovedAt(binding.recyclerViewPastOrders, pastOrdersList.size() - 1);
            }

            if (v.getChildAt(v.getChildCount() - 1) != null) {
                if ((scrollY >= (v.getChildAt(v.getChildCount() - 1).getMeasuredHeight() - v.getMeasuredHeight())) && scrollY > oldScrollY) {
                    LinearLayoutManager linearLayoutManager = (LinearLayoutManager) binding.recyclerViewPastOrders.getLayoutManager();
                    if (linearLayoutManager != null) {
                        int firstVisibleProductPosition = linearLayoutManager.findFirstVisibleItemPosition();
                        int visibleProductCount = linearLayoutManager.getChildCount();
                        int totalProductCount = linearLayoutManager.getItemCount();
                        if (firstVisibleProductPosition + visibleProductCount == totalProductCount) {
                            LoadingModel loadingModel = LoadingModel.getInstance();
                            if (!pastOrdersList.contains(loadingModel) && pastOrdersList.size() >= 2) {
                                addLoadingView();

                                OrdersDetails ordersDateOrderId = (OrdersDetails) pastOrdersList.get(pastOrdersList.size() - 2);
                                ordersViewModel.loadMoreOrders(ordersDateOrderId.date + "_" + ordersDateOrderId.orderId).observe(getViewLifecycleOwner(), operation -> {
                                    addOrdersToType(operation);
                                    if ((pastOrdersList.size() - pastOrdersList.indexOf(loadingModel)) - 1 == LIMIT - 1) {
                                        removeLoadingView();
                                    }
                                });
                            }
                        }
                    }
                }
            }
        });
    }

    private void addLoadingView() {
        LoadingModel loadingModel = LoadingModel.getInstance();
        pastOrdersList.add(loadingModel);
        NotifyRecyclerItems.notifyItemInsertedAt(binding.recyclerViewPastOrders, pastOrdersList.size() - 1);
    }

    private void removeLoadingView() {
        LoadingModel loadingModel = LoadingModel.getInstance();
        int index = pastOrdersList.indexOf(loadingModel);
        pastOrdersList.remove(loadingModel);
        NotifyRecyclerItems.notifyItemRemovedAt(binding.recyclerViewPastOrders, index);
    }

    private void addOrdersToType(Operation operation) {
        switch (operation.mode) {
            case ADD:
                OrdersDetails newOrdersDetails = (OrdersDetails) operation.aClass;
                if (newOrdersDetails.isPastOrder) addPastOrders(newOrdersDetails);
                else addOrders(newOrdersDetails);
                break;
            case MODIFIED:
                Orders orderModified = (Orders) operation.aClass;
                removeProduct(orderModified.getOrderId());
                break;
            case REMOVE:
                Log.e("ORDER PLACED FRAGMENT", "removed");
                break;
        }
    }

    private void addOrders(OrdersDetails addedOrdersDetails) {
        if (!ordersList.contains(addedOrdersDetails)) {
            ordersList.add(addedOrdersDetails);
            NotifyRecyclerItems.notifyItemInsertedAt(binding.recyclerView, ordersList.size());
        }
    }

    private void addPastOrders(OrdersDetails pastOrderDetails) {
        if (!pastOrdersList.contains(pastOrderDetails)) {
            pastOrdersList.add(pastOrderDetails);
            NotifyRecyclerItems.notifyItemInsertedAt(binding.recyclerViewPastOrders, pastOrdersList.size());
        }
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
                NotifyRecyclerItems.notifyItemInsertedAt(binding.recyclerView, 0);
                updateHeaderView();
            }
        }
    }

    private void updateHeaderView() {
        if (ordersList.size() == 0) {
            ordersList.add(new EmptyState(R.drawable.crd_empty_order_bg, R.drawable.empty_cart_img, "Whoops", "its look like that no ongoing orders"));
            NotifyRecyclerItems.notifyItemInsertedAt(binding.recyclerView, 0);
            ordersList.add(LoadingModel.getInstance());
            NotifyRecyclerItems.notifyItemInsertedAt(binding.recyclerView, ordersList.indexOf(LoadingModel.getInstance()));
        } else if (ordersList.size() == 3) {
            LoadingModel loadingModel = LoadingModel.getInstance();
            int indexOfLoadingModel = ordersList.indexOf(loadingModel);
            ordersList.remove(loadingModel);
            NotifyRecyclerItems.notifyItemRemovedAt(binding.recyclerView, indexOfLoadingModel);
            ordersList.remove(0);
            ordersList.add(0, new EmptyState(R.drawable.crd_order_bg, R.drawable.pablo_delivery_transparent, "Today's Orders", "your orders will be delivered as soon as possible"));
            NotifyRecyclerItems.notifyItemInsertedAt(binding.recyclerView, 0);
        }
    }
}
