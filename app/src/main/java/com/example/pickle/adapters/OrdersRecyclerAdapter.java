package com.example.pickle.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pickle.adapters.viewholders.AbstractViewHolder;
import com.example.pickle.databinding.CardviewOrdersBinding;
import com.example.pickle.interfaces.OrderStatus;
import com.example.pickle.interfaces.Visitable;
import com.example.pickle.interfaces.Visitor;
import com.example.pickle.models.OrdersDetails;
import com.example.pickle.models.ProductModel;
import com.example.pickle.models.ProductViewModel;
import com.example.pickle.utils.DateUtils;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.example.pickle.utils.Constant.ORDERS;
import static com.example.pickle.utils.Constant.ORDERS_CANCELLED;

public class OrdersRecyclerAdapter extends RecyclerView.Adapter<AbstractViewHolder> {

    private Context context;
    private ArrayList<Visitable> visitableArrayList;
    private Visitor visitor;

    public OrdersRecyclerAdapter(Context context, ArrayList<Visitable> visitableArrayList, Visitor visitor) {
        this.context = context;
        this.visitableArrayList = visitableArrayList;
        this.visitor = visitor;
    }

    @NonNull
    @Override
    public AbstractViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

//        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
//        CardviewOrdersBinding view = CardviewOrdersBinding.inflate(layoutInflater, parent, false);
//
//        return  new OrderViewHolder(view, context);

        View view = LayoutInflater.from(parent.getContext()).inflate(viewType, parent, false);
        return visitor.createViewHolder(view, viewType);
    }

    @Override
    public void onBindViewHolder(@NonNull AbstractViewHolder holder, int position) {
//        OrdersDetails orders = ordersList.get(position);
//        holder.binding.setOrderDetails(orders);
//        holder.setName(orders.getItemCategory(), orders.getItemId());
//
//        if (!orders.isPastOrder) {
//            holder.binding.cancelButtonMb.setOnClickListener(n -> {
//                holder.cancelOrder(ordersList.get(position));
//            });
//        }
//
//        Log.e("is past order", orders.isPastOrder + "");
//        if (ordersList.get(position).isPastOrder) {
//            holder.setDate(ordersList.get(position));
//        }

        holder.bind(visitableArrayList.get(position));

    }


    @Override
    public int getItemCount() {
        return visitableArrayList.size();
    }

    @Override
    public int getItemViewType(int position) {
        Log.e("OrdersRecyclerview", visitableArrayList.get(position).accept(visitor) +" ");
        return visitableArrayList.get(position).accept(visitor);
    }

    public static class OrderViewHolder extends RecyclerView.ViewHolder {

        CardviewOrdersBinding binding;
        private Context context;
        private ProductViewModel viewModel;
        private ProductModel productModel;

        public OrderViewHolder(@NonNull CardviewOrdersBinding binding, Context context) {
            super(binding.getRoot());
            this.binding = binding;
            this.context = context;
            viewModel = new ProductViewModel();
            productModel = new ProductModel();

            viewModel.setProduct(productModel);
            binding.setProductViewModel(viewModel);

        }

        void setName(String productCategory, String productId) {
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Products/"+productCategory+"/"+productId+"/itemName");
            reference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    String name = dataSnapshot.getValue(String.class);
                    productModel.setItemName(name);
                    viewModel.setProduct(productModel);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

        void cancelOrder(OrdersDetails ordersDetails) {
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

            Map<String, Object> atomicUpdate = new HashMap<>();

            atomicUpdate.put(ORDERS + "/" + ordersDetails.orderId + "/" + "orderStatus", OrderStatus.CANCEL);
            atomicUpdate.put(ORDERS_CANCELLED + "/" + ordersDetails.orderId + "/" + "orderId", ordersDetails.orderId);
            atomicUpdate.put(ORDERS_CANCELLED + "/" + ordersDetails.orderId + "/" + "date", ServerValue.TIMESTAMP);

            databaseReference.updateChildren(atomicUpdate).addOnSuccessListener(task -> {
                Toast.makeText(context, "order cancel successfully", Toast.LENGTH_SHORT).show();
            }).addOnFailureListener(taskFailed -> {
                Toast.makeText(context, "failed to cancel order", Toast.LENGTH_SHORT).show();
            });
        }

        void setDate(OrdersDetails ordersDetails) {
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
            Log.e("references ```~", ordersDetails.status + "");
            switch (ordersDetails.status) {
//                case OrderStatus.PROCESSING:
//                    binding.cardViewTvDate.setText("Processing");
//                    break;
                case OrderStatus.CANCEL:
                    reference.child(ORDERS_CANCELLED).child(ordersDetails.orderId);
                    reference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            Log.e("datasnapshot", dataSnapshot + " --------------------------------------------------------------------------------");
                            String date = DateUtils.getServerDate(dataSnapshot.child("date").getValue(Long.class));
                            binding.cardViewTvDate.setText("Date: " + date);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                    break;
//                case OrderStatus.DELIVERED:
//                    binding.cardViewTvDate.setText("Delivered");
//                    break;
//                case OrderStatus.ORDERED:
//                    binding.cardViewTvDate.setText("Ordered");
//                    break;
            }
        }
    }
}
