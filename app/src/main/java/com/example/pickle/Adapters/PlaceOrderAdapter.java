package com.example.pickle.Adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pickle.data.OrdersData;
import com.example.pickle.data.ProductModel;
import com.example.pickle.databinding.CardviewOrderPlacedBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class PlaceOrderAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private ArrayList<OrdersData> modelArrayList;
    private ArrayList<ProductModel> productModelArrayList;

    public PlaceOrderAdapter(Context context, ArrayList<OrdersData> modelArrayList, ArrayList<ProductModel> productModelArrayList) {
        this.context = context;
        this.modelArrayList = modelArrayList;
        this.productModelArrayList = productModelArrayList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        RecyclerView.ViewHolder viewHolder;

        CardviewOrderPlacedBinding view = CardviewOrderPlacedBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false);

        viewHolder = new OrdersViewModel(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        OrdersViewModel ordersViewModel = (OrdersViewModel) holder;

        OrdersData ordersData = modelArrayList.get(position);
        ordersViewModel.bindPlaceOrderModel(modelArrayList.get(position));


    }

    @Override
    public int getItemCount() {
        return modelArrayList.size();
    }

    class OrdersViewModel extends RecyclerView.ViewHolder {

        private CardviewOrderPlacedBinding orderPlacedBinding;
        private ImageView imageView;

        public OrdersViewModel(@NonNull CardviewOrderPlacedBinding orderPlacedBinding) {
            super(orderPlacedBinding.getRoot());
            this.orderPlacedBinding = orderPlacedBinding;

            imageView = orderPlacedBinding.imageView;
        }

        public void bindPlaceOrderModel(OrdersData ordersData) {
            orderPlacedBinding.setOrderPlaced(ordersData);
            orderPlacedBinding.executePendingBindings();
            setProductDetails(ordersData.getItemCategory(), ordersData.getItemId());
        }

        public void bindProductModelView(ProductModel model) {
            orderPlacedBinding.setProductDetail(model);
        }

        public void setProductDetails(String category, String productId) {
            DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference("Products/" + category + "/" + productId);
            reference1.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Log.e("placeOrderAdapter", dataSnapshot + "");
//                    bindProductModelView(dataSnapshot.getValue(ProductModel.class));
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }

}
