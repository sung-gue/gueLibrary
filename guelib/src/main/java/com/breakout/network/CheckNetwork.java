package com.breakout.network;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.breakout.util.Log;

public class CheckNetwork {
    private final static String TAG = CheckNetwork.class.getName();

    public static class Info {
        public enum Type {
            UNCHECKED, NOT_CONNECTED, CONNECTED
        }

        NetworkInfo netWorkInfo;
        /**
         * true if large transfers should be avoided, otherwise false.
         * <p>
         * https://developer.android.com/reference/android/net/ConnectivityManager?hl=ko#isActiveNetworkMetered()
         */
        boolean isActiveNetworkMetered = true;
        Type type = Type.UNCHECKED;

        public void init(ConnectivityManager cm) {
            if (cm != null) {
                netWorkInfo = cm.getActiveNetworkInfo();
                isActiveNetworkMetered = cm.isActiveNetworkMetered();
                if (netWorkInfo != null) {
                    type = Type.CONNECTED;
                } else {
                    type = Type.NOT_CONNECTED;
                }
            }
        }

        @Nullable
        public NetworkInfo getNetWorkInfo() {
            return netWorkInfo;
        }

        public Type getType() {
            return type;
        }

        public boolean isConnected() {
            return type == Type.CONNECTED && netWorkInfo != null && netWorkInfo.isConnected() && netWorkInfo.isAvailable();
        }

        public boolean isFreeNetwokrk() {
            return !isActiveNetworkMetered;
        }

        public boolean isConnectedMobile() {
            return netWorkInfo != null && netWorkInfo.getType() == ConnectivityManager.TYPE_MOBILE;
        }

        public boolean isConnectedWifi() {
            return netWorkInfo != null && netWorkInfo.getType() == ConnectivityManager.TYPE_WIFI;
        }
    }

    /**
     * check network info
     */
    public static Info getInfo(@NonNull Context context) {
        Info info = new Info();
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        info.init(cm);
        if (info.netWorkInfo != null) {
            Log.d(TAG, "NetworkInfo : " + info.netWorkInfo.getTypeName());

        } else {
            Log.d(TAG, "NetworkInfo : fail");
        }
        return info;
    }

    /**
     * network connected
     */
    public static boolean isConnected(@NonNull Context context) {
        Info info = getInfo(context);
        return info.isConnected();
    }
}
