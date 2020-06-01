package com.example.pickle.models;

import com.example.pickle.interfaces.Visitable;
import com.example.pickle.interfaces.Visitor;

public class LoadingModel implements Visitable {

    public LoadingModel() {

    }

    @Override
    public int accept(Visitor visitor) {
        return visitor.type(this);
    }
}
