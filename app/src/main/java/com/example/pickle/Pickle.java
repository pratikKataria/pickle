package com.example.pickle;

import android.app.Application;

import com.example.pickle.network.ConnectivityReceiver;
import com.google.firebase.database.FirebaseDatabase;

public class Pickle extends Application {

//    private static  Pickle pickleInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);

//        pickleInstance = this;
    }

//    public static synchronized  Pickle getInstance(){
//        return pickleInstance;
//    }
//
//    public void setConnectivityListener(ConnectivityReceiver.ConnectivityReceiverListener listener) {
//        ConnectivityReceiver.connectivityReceiverListener = listener;
//    }
}
