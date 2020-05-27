package com.example.pickle.network;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

import com.example.pickle.Pickle;

public class ConnectivityReceiver extends BroadcastReceiver implements BroadcastRegistration {

//    public static ConnectivityReceiverListener connectivityReceiverListener;

    public ConnectivityReceiver() {
        super();
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        int wifiStateExtra = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, WifiManager.WIFI_STATE_UNKNOWN);
        switch (wifiStateExtra) {
            case WifiManager.WIFI_STATE_ENABLED:
                Log.e("Connectivity Receiver", "WIFI_STATE_ENABLED");
                break;
            case WifiManager.WIFI_STATE_DISABLED:
                Log.e("Connectivity Receiver", "WIFI_STATE_DISABLED");
                break;
        }
    }

    @Override
    public void attach() {

    }

    @Override
    public void detach() {

    }


//    public interface ConnectivityReceiverListener {
//        void onNetworkConnectionChanged(boolean isConnected);
//    }
}
