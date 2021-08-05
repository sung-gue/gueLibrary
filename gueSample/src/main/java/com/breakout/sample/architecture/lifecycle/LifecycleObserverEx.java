package com.breakout.sample.architecture.lifecycle;

import android.util.Log;

import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.OnLifecycleEvent;

/**
 * https://developer.android.com/topic/libraries/architecture/lifecycle
 * <p>
 * https://developer.android.com/jetpack/androidx/releases/lifecycle
 */
public class LifecycleObserverEx implements LifecycleObserver {
    private final String TAG = getClass().getSimpleName();

    private final Lifecycle _lifecycle;


    public LifecycleObserverEx(Lifecycle lifecycle) {
        this._lifecycle = lifecycle;
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    private void onCreate(LifecycleOwner source) {
        Log.d(TAG, "onCreate");
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    private void onStart() {
        Log.d(TAG, "onStart");
        if (_lifecycle.getCurrentState().isAtLeast(Lifecycle.State.INITIALIZED)) {
            Log.d(TAG, "currentState is greater or equal to INITIALIZED");
        }
        if (_lifecycle.getCurrentState().isAtLeast(Lifecycle.State.CREATED)) {
            Log.d(TAG, "currentState is greater or equal to CREATED");
        }
        if (_lifecycle.getCurrentState().isAtLeast(Lifecycle.State.STARTED)) {
            Log.d(TAG, "currentState is greater or equal to STARTED");
        }
        if (_lifecycle.getCurrentState().isAtLeast(Lifecycle.State.RESUMED)) {
            Log.d(TAG, "currentState is greater or equal to RESUMED");
        }
        if (_lifecycle.getCurrentState().isAtLeast(Lifecycle.State.DESTROYED)) {
            Log.d(TAG, "currentState is greater or equal to DESTROYED");
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    private void onResume() {
        Log.d(TAG, "onResume");
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    private void onPause() {
        Log.d(TAG, "onPause");
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    private void onStop() {
        Log.d(TAG, "onStop");
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    private void onDestroy() {
        Log.d(TAG, "onDestroy");
        _lifecycle.removeObserver(this);
    }

}