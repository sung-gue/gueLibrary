package com.breakout.sample.architecture.lifecycle;

import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.OnLifecycleEvent;

import com.breakout.sample.Log;

/**
 * LifecycleObserver
 */
@Deprecated
public class SampleObserver3 implements LifecycleObserver {
    private final String TAG = getClass().getSimpleName();
    private final Lifecycle lifecycle;

    public SampleObserver3(Lifecycle lifecycle) {
        this.lifecycle = lifecycle;
        lifecycle.addObserver(this);
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    private void onCreate(LifecycleOwner source) {
        Log.d(TAG, "onCreate");
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    private void onStart(LifecycleOwner source) {
        Log.d(TAG, "onStart");
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    private void onResume(LifecycleOwner source) {
        Log.d(TAG, "onResume");
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    private void onPause(LifecycleOwner source) {
        Log.d(TAG, "onPause");
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    private void onStop(LifecycleOwner source) {
        Log.d(TAG, "onStop");
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    private void onDestroy(LifecycleOwner source) {
        Log.d(TAG, "onDestroy");
        lifecycle.removeObserver(this);
    }

}