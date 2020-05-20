package com.example.pickle.Adapters;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

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

        return  new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        holder.binding.setOrderDetails(ordersList.get(position));
        holder.setImage(ordersList.get(position).getItemCategory(), ordersList.get(position).getItemId());
        holder.setName(ordersList.get(position).getItemCategory(), ordersList.get(position).getItemId());


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
        private ProductViewModel viewModel;
        private ProductModel productModel;

        public OrderViewHolder(@NonNull CardviewOrdersBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            viewModel = new ProductViewModel();
            productModel = new ProductModel();

            viewModel.setProduct(productModel);
            binding.setProductViewModel(viewModel);
        }

        public void setImage(String productCategory, String productId) {
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Products/"+productCategory+"/"+productId+"/itemThumbImage");
            reference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    String imageUrl = dataSnapshot.getValue(String.class);
                    productModel.setItemThumbImage(imageUrl);
                    viewModel.setProduct(productModel);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

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

    }
}
