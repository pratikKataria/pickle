package com.example.pickle.product;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.pickle.BR;
import com.example.pickle.R;
import com.example.pickle.interfaces.IFragmentCb;
import com.example.pickle.interfaces.IMainActivity;
import com.example.pickle.models.ProductModel;

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

        try {
            NavHostFragment navFragment = (NavHostFragment) ((AppCompatActivity) context).getSupportFragmentManager().findFragmentById(R.id.activity_main_nav_host);
            Fragment fragment = navFragment.getChildFragmentManager().getFragments().get(0);
            IFragmentCb iFragmentCb = (IFragmentCb) fragment;
            iFragmentCb.play();
        } catch (Exception xe) {
            Log.e(ProductViewModel.class.getName(), xe.getMessage());
        }

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

            try {
                NavHostFragment navFragment = (NavHostFragment) ((AppCompatActivity) context).getSupportFragmentManager().findFragmentById(R.id.activity_main_nav_host);
                Fragment fragment = navFragment.getChildFragmentManager().getFragments().get(0);
                IFragmentCb iFragmentCb = (IFragmentCb) fragment;
                iFragmentCb.play();
            } catch (Exception xe) {
                Log.e(ProductViewModel.class.getName(), xe.getMessage());
            }


        }else {
            Toast.makeText(context, "maximum quantity reached", Toast.LENGTH_SHORT).show();
        }
    }

    public void decreaseQuantity(Context context) {
        ProductModel newProduct = getProduct();
        IMainActivity iMainActivity = (IMainActivity) context;
        int currentQuantity = newProduct.getQuantityCounter();

        if (currentQuantity > 1) {
            iMainActivity.updateQuantity(newProduct, currentQuantity - 1);
            newProduct.setQuantityCounter(currentQuantity - 1);
            setProduct(newProduct);
        }

        else if (currentQuantity == 1) {
            newProduct.setQuantityCounter(newProduct.getQuantityCounter() - 1);
            setProduct(newProduct);
            iMainActivity.removeProduct(newProduct);
        }

    }

    public void deleteFromCart(Context context) {
        ProductModel newProduct = getProduct();
        IMainActivity iMainActivity = (IMainActivity) context;
        newProduct.setQuantityCounter(0);
        setProduct(newProduct);
        iMainActivity.removeProduct(newProduct);
    }

}
