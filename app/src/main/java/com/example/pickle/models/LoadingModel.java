package com.example.pickle.models;

import com.example.pickle.interfaces.Visitable;
import com.example.pickle.interfaces.Visitor;

public class LoadingModel implements Visitable {

    private static LoadingModel loadingModel_singleton = null;

    public static LoadingModel getInstance() {
        if (loadingModel_singleton == null) {
            loadingModel_singleton = new LoadingModel();
        }

        return loadingModel_singleton;
    }

    @Override
    public int accept(Visitor visitor) {
        return visitor.type(this);
    }
}
