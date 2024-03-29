package com.pickleindia.pickle.network;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.NetworkRequest;
import android.os.Build;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

public class NetworkConnectionStateMonitor extends LiveData<Boolean> implements INetworkState {
    private Context context;
    private ConnectivityManager.NetworkCallback networkCallback;
    private ConnectivityManager connectivityManager;
    private NetworkReceiver networkReceiver;

    public NetworkConnectionStateMonitor(Context context) {
        this.context = context;
        connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            networkCallback = new NetworkCallback(this);
        } else {
            networkReceiver = new NetworkReceiver();
        }
    }

    @Override
    protected void onActive() {
        super.onActive();
        updateConnection();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            connectivityManager.registerDefaultNetworkCallback(networkCallback);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            NetworkRequest networkRequest = new NetworkRequest.Builder()
                    .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
                    .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
                    .build();
            connectivityManager.registerNetworkCallback(networkRequest, networkCallback);
        } else {
            context.registerReceiver(networkReceiver, new IntentFilter(Context.CONNECTIVITY_SERVICE));
        }
    }

    @Override
    protected void onInactive() {
        super.onInactive();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            connectivityManager.unregisterNetworkCallback(networkCallback);
        } else {
            context.unregisterReceiver(networkReceiver);
        }
    }

    private void updateConnection() {
        if (connectivityManager != null) {
            NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
            if (activeNetwork != null && activeNetwork.isConnectedOrConnecting()) {
                postValue(true);
            } else {
                postValue(false);
            }
        }
    }

    @Override
    public void state(String type, boolean status) {

    }

    class NetworkCallback extends ConnectivityManager.NetworkCallback {
        private NetworkConnectionStateMonitor connectionStateMonitor;

        public NetworkCallback(NetworkConnectionStateMonitor connectionStateMonitor) {
            this.connectionStateMonitor = connectionStateMonitor;
        }

        @Override
        public void onAvailable(@NonNull Network network) {
            Log.e("connectivity receiver", "onAvailable");
            state("onAvailable", true);
            connectionStateMonitor.postValue(true);
        }

        @Override
        public void onLost(@NonNull Network network) {
            Log.e("connectivity receiver", "onLost");
            state("onLost", true);
            connectionStateMonitor.postValue(false);
        }

        @Override
        public void onUnavailable() {
            Log.e("connectivity receiver", "onUnavailable");
            connectionStateMonitor.postValue(false);
            state("onUnavailable", true);
        }

        @Override
        public void onLosing(@NonNull Network network, int maxMsToLive) {
            Log.e("connectivity receiver", "onLosing"+maxMsToLive);
            state("onLosing", true);

        }


    }

    class NetworkReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Context.CONNECTIVITY_SERVICE)) {
//                updateConnection();
            }
        }
    }
}
