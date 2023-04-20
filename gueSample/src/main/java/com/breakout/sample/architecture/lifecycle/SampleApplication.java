package com.breakout.sample.architecture.lifecycle;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleEventObserver;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.lifecycle.ProcessLifecycleOwner;

import com.breakout.sample.Log;


@SuppressWarnings("unused")
public class SampleApplication extends Application {

    @Deprecated
    static class ApplicationObserverOld implements LifecycleObserver {
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

    static class ApplicationObserver implements LifecycleEventObserver {
        private final String TAG = getClass().getName();

        @Override
        public void onStateChanged(@NonNull LifecycleOwner source, @NonNull Lifecycle.Event event) {
            Log.d(TAG, event.name());
            switch (event) {
                default:
                case ON_ANY:
                case ON_CREATE:
                case ON_RESUME:
                case ON_START:
                case ON_PAUSE:
                case ON_STOP:
                case ON_DESTROY:
                    break;
            }
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        ProcessLifecycleOwner.get().getLifecycle().addObserver(new ApplicationObserver());
    }
}