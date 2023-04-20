package com.breakout.sample.service;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.breakout.sample.Log;

public class SampleActivity extends AppCompatActivity {
    private final String TAG = getClass().getSimpleName();

    /*
        INFO: AppService bind
     */
    public interface BindAppServiceConnectedListener {
        void onAppServiceConnected(AppService appService);
    }

    public AppService appService;
    private boolean isAppServiceBinding;
    private final ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.i(TAG, "ServiceConnection onServiceConnected");
            AppService.AppBinder binder = (AppService.AppBinder) service;
            appService = binder.getService();
            isAppServiceBinding = true;
            if (SampleActivity.this instanceof BindAppServiceConnectedListener) {
                ((BindAppServiceConnectedListener) SampleActivity.this).onAppServiceConnected(appService);
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.i(TAG, "ServiceConnection onServiceDisconnected");
            appService = null;
            isAppServiceBinding = false;
        }
    };

    protected void startAppService() {
        Intent serviceIntent = new Intent(this, AppService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(serviceIntent);
        } else {
            startService(serviceIntent);
        }
    }

    protected void bindAppService() {
        Intent serviceIntent = new Intent(this, AppService.class);
        bindService(serviceIntent, serviceConnection, BIND_AUTO_CREATE);
    }

    protected void unbindAppService() {
        if (isAppServiceBinding) {
            unbindService(serviceConnection);
        }
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        startAppService();

        bindAppService();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindAppService();

    }
}
