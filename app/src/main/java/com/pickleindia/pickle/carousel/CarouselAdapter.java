package com.pickleindia.pickle.carousel;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.pickleindia.pickle.databinding.CardviewCarouselBinding;

import java.util.ArrayList;

public class CarouselAdapter extends RecyclerView.Adapter<CarouselAdapter.ViewHolder> {

    private ArrayList<CarouselImage> images;

    public CarouselAdapter(ArrayList<CarouselImage> images) {
        this.images = images;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        CardviewCarouselBinding view = CardviewCarouselBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.setBinding(images.get(position));
    }

    @Override
    public int getItemCount() {
        return images.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        private CardviewCarouselBinding binding;

        public ViewHolder(@NonNull CardviewCarouselBinding itemView) {
            super(itemView.getRoot());
            this.binding = itemView;
        }

        void setBinding(CarouselImage image) {

        }
    }
}
