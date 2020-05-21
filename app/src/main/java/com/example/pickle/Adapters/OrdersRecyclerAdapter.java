package com.example.pickle.Adapters;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pickle.interfaces.OrderStatus;
import com.example.pickle.models.OrdersDetails;
import com.example.pickle.models.ProductModel;
import com.example.pickle.models.ProductViewModel;
import com.example.pickle.databinding.CardviewOrdersBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.example.pickle.utils.Constant.ORDERS;

public class OrdersRecyclerAdapter extends RecyclerView.Adapter<OrdersRecyclerAdapter.OrderViewHolder> {

    private Context context;
    private ArrayList<OrdersDetails> ordersList;

    public OrdersRecyclerAdapter(Context context, ArrayList<OrdersDetails> ordersList) {
        this.context = context;
        this.ordersList = ordersList;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        CardviewOrdersBinding view = CardviewOrdersBinding.inflate(layoutInflater, parent, false);

        return  new OrderViewHolder(view, context);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        holder.binding.setOrderDetails(ordersList.get(position));
        holder.setName(ordersList.get(position).getItemCategory(), ordersList.get(position).getItemId());

        if (!ordersList.get(position).isPastOrder) {
            holder.binding.cancelButtonMb.setOnClickListener(n -> {
                holder.cancelOrder(ordersList.get(position));
            });
        }
    }


    @Override
    public int getItemCount() {
        return ordersList.size();
    }

    public void updateCartItemsList(ProductModel product) {
        ordersList.remove(product);
        notifyDataSetChanged();
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

        public void setName(String productCategory, String productId) {
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

        public void cancelOrder(OrdersDetails ordersDetails) {
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(ORDERS).child(ordersDetails.orderId);
            Map<String, Object> updateOrder = new HashMap<>();
            updateOrder.put("orderStatus", OrderStatus.CANCEL);
            databaseReference.updateChildren(updateOrder).addOnSuccessListener(task -> {
                Toast.makeText(context, "order cancel successfully", Toast.LENGTH_SHORT).show();
            }).addOnFailureListener(taskFailed -> {
                Toast.makeText(context, "failed to cancel order", Toast.LENGTH_SHORT).show();
            });
        }
    }
}
