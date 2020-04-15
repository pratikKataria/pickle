package com.example.pickle.Adapters;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pickle.R;
import com.example.pickle.data.ProductModel;

import java.util.List;

public class ProductsRecyclerViewAdapter extends RecyclerView.Adapter<ProductsRecyclerViewAdapter.ProductViewHolder> {

    private Context context;
    private List<ProductModel> gridItemList;

    public ProductsRecyclerViewAdapter(Context context, List<ProductModel> gridItemList) {
        this.context = context;
        this.gridItemList = gridItemList;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_card_view, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        holder.setData(gridItemList.get(position).getItemName(), gridItemList.get(position).getItemBasePrice());
    }

    @Override
    public int getItemCount() {
        return gridItemList.size();
    }

    class ProductViewHolder extends RecyclerView.ViewHolder {

        TextView textViewTitle;
        TextView imageView;
        CardView cardView;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);

            textViewTitle = itemView.findViewById(R.id.product_card_view_item_price);
            imageView = itemView.findViewById(R.id.product_card_view_item_name);

        }

        public void setData(String text, int imageResource) {
            textViewTitle.setText(text);
            imageView.setText(imageResource +"");
        }
    }

}