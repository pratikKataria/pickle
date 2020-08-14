package com.pickleindia.pickle.interfaces;

import com.pickleindia.pickle.models.ProductModel;

public interface ProductCheckListener {
    void onReceived(int index, ProductModel productModel);

    void onCompleted();

    void removeListener();
}
