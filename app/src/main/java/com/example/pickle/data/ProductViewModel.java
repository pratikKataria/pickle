package com.example.pickle.data;

import android.content.Context;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

import com.example.pickle.BR;
import com.example.pickle.binding.IMainActivity;

public class ProductViewModel extends BaseObservable {
    private ProductModel product;

    @Bindable
    public ProductModel getProduct() {
        return product;
    }

    public void setProduct(ProductModel product) {
        this.product = product;
        notifyPropertyChanged(BR.product);
    }

    public void addToCart(Context context) {
        ProductModel newProduct = getProduct();

        IMainActivity iMainActivity = (IMainActivity) context;
        iMainActivity.updateQuantity(newProduct, 1);

        newProduct.setQuantityCounter(1);
        setProduct(newProduct);
    }

    public void increaseQuantity(Context context) {
        ProductModel newProduct = getProduct();
        int currentQuantity = newProduct.getQuantityCounter();
        int maxQuantity = newProduct.getItemMaxQtyPerUser();

        if (currentQuantity < maxQuantity) {
            IMainActivity iMainActivity = (IMainActivity) context;
            iMainActivity.updateQuantity(newProduct, currentQuantity + 1);

            newProduct.setQuantityCounter(currentQuantity + 1);
            setProduct(newProduct);
        }
    }

    public void decreaseQuantity(Context context) {
        ProductModel newProduct = getProduct();
        int currentQuantity = newProduct.getQuantityCounter();

        if (currentQuantity > 0) {
            IMainActivity iMainActivity = (IMainActivity) context;
            iMainActivity.updateQuantity(newProduct, currentQuantity - 1);

            newProduct.setQuantityCounter(currentQuantity - 1);
            setProduct(newProduct);
        }
    }

}
