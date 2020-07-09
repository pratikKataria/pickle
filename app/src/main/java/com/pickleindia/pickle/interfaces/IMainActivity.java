package com.pickleindia.pickle.interfaces;

import com.pickleindia.pickle.models.ProductModel;

public interface IMainActivity {
    void updateQuantity(ProductModel productModel, int quantity);

    void removeProduct(ProductModel productModel);
}
