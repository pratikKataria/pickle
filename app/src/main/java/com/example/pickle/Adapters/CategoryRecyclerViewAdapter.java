package com.example.pickle.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import com.example.pickle.models.ProductViewModel;
import com.example.pickle.models.ProductModel;
import com.example.pickle.databinding.ActivityEmptyBinding;
import com.example.pickle.databinding.CardViewCategoryProductBinding;
import com.example.pickle.ui.SearchViewBottomSheetDialog;
import java.util.ArrayList;

public class CategoryRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private ArrayList<ProductModel> productModelsList;

    private static final byte EMPTY_VIEW = 32;
    private static final byte CARD_VIEW = 55;

    public CategoryRecyclerViewAdapter(Context context, ArrayList<ProductModel> productModelList) {
        this.context = context;
        this.productModelsList = productModelList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        RecyclerView.ViewHolder holder;
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        if (viewType == EMPTY_VIEW) {
            ActivityEmptyBinding view = ActivityEmptyBinding.inflate(layoutInflater, parent, false);
            holder = new EmptyView(view.getRoot());
        } else {
            CardViewCategoryProductBinding view = CardViewCategoryProductBinding.inflate(layoutInflater, parent, false);
            holder = new ProductCardViewHolder(view);
        }

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        if (holder instanceof ProductCardViewHolder) {
            ProductModel product = productModelsList.get(position);
            ProductCardViewHolder currCardViewHolder = (ProductCardViewHolder) holder;

            ProductViewModel productViewModel = new ProductViewModel();
            productViewModel.setProduct(product);

            currCardViewHolder.bind(productViewModel);

            ((ProductCardViewHolder) holder).binding.cardView.setOnClickListener(
                    n -> ((ProductCardViewHolder) holder).showDialog(productModelsList.get(position))
            );
        } else {
            //empty view
            EmptyView emptyView = (EmptyView) holder;
        }


    }

    @Override
    public int getItemCount() {
        if (productModelsList.size() == 0) {
            return 1;
        } else {
            return productModelsList.size();
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (productModelsList.size() == 0) {
            return EMPTY_VIEW;
        } else {
            return CARD_VIEW;
        }
    }

    static class EmptyView extends  RecyclerView.ViewHolder {
        public EmptyView(@NonNull View itemView) {
            super(itemView);
        }
    }

     class ProductCardViewHolder extends RecyclerView.ViewHolder{

        CardViewCategoryProductBinding binding;

        public ProductCardViewHolder(@NonNull CardViewCategoryProductBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(ProductViewModel viewModel) {
            binding.setProductViewModel(viewModel);
            binding.executePendingBindings();
        }

        private void showDialog(ProductModel product) {
            SearchViewBottomSheetDialog searchViewBottomSheetDialog = new SearchViewBottomSheetDialog(product);
            searchViewBottomSheetDialog.show(((AppCompatActivity)context).getSupportFragmentManager(), "searchViewBottomSheet");
        }

    }

}
