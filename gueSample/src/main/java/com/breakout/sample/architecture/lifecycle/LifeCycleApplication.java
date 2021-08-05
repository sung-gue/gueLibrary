package com.breakout.sample.architecture.lifecycle;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.lifecycle.ProcessLifecycleOwner;


@SuppressWarnings("unused")
public class LifeCycleApplication extends Application {

    static class ApplicationLifecycleObserver implements LifecycleObserver {
        private final String TAG = getClass().getName();

        @OnLifecycleEvent(Lifecycle.Event.ON_START)
        private void onForeground() {
            Log.d(TAG, "onForeground");
        }

        @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
        private void onBackground() {
            Log.d(TAG, "onBackground");
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        ProcessLifecycleOwner.get().getLifecycle().addObserver(new ApplicationLifecycleObserver());
    }
}