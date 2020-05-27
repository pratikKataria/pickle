package com.example.pickle.network;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.net.NetworkRequest;
import android.net.wifi.WifiManager;
import android.util.Log;

import androidx.annotation.NonNull;

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

    public static void registerNetworkCallback(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkRequest.Builder builder = new NetworkRequest.Builder();

        connectivityManager.registerDefaultNetworkCallback(new ConnectivityManager.NetworkCallback() {
            @Override
            public void onAvailable(@NonNull Network network) {
                Log.e("connectivity receiver", "onAvailable");
            }

            @Override
            public void onLost(@NonNull Network network) {
                Log.e("connectivity receiver", "onLost");
            }

            @Override
            public void onUnavailable() {
                super.onUnavailable();
            }


        }) ;
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
