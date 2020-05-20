package com.example.pickle.interfaces;

import com.example.pickle.models.ProductModel;

public interface IMainActivity {
    void updateQuantity(ProductModel productModel, int quantity);

    void removeProduct(ProductModel productModel);
}
