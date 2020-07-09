package com.pickleindia.pickle.models;

public class Operation<T> {
    public T aClass;
    public int mode;

    public Operation(T aClass, int mode) {
        this.aClass = aClass;
        this.mode = mode;
    }
}
