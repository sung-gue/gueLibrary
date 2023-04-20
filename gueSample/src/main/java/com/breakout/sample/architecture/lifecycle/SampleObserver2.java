package com.breakout.sample.architecture.lifecycle;


import androidx.annotation.NonNull;
import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;

import com.breakout.sample.Log;

/**
 * DefaultLifecycleObserver
 */
public class SampleObserver2 implements DefaultLifecycleObserver {
    private final String TAG = getClass().getSimpleName();
    private final Lifecycle lifecycle;

    public SampleObserver2(Lifecycle lifecycle) {
        this.lifecycle = lifecycle;
        lifecycle.addObserver(this);
    }

    @Override
    public void onCreate(@NonNull LifecycleOwner owner) {
        DefaultLifecycleObserver.super.onCreate(owner);
        Log.d(TAG, "onCreate");
    }

    @Override
    public void onStart(@NonNull LifecycleOwner owner) {
        DefaultLifecycleObserver.super.onStart(owner);
        Log.d(TAG, "onStart");
        if (lifecycle.getCurrentState().isAtLeast(Lifecycle.State.INITIALIZED)) {
            Log.d(TAG, "currentState is greater or equal to INITIALIZED");
        }
        if (lifecycle.getCurrentState().isAtLeast(Lifecycle.State.CREATED)) {
            Log.d(TAG, "currentState is greater or equal to CREATED");
        }
        if (lifecycle.getCurrentState().isAtLeast(Lifecycle.State.STARTED)) {
            Log.d(TAG, "currentState is greater or equal to STARTED");
        }
        if (lifecycle.getCurrentState().isAtLeast(Lifecycle.State.RESUMED)) {
            Log.d(TAG, "currentState is greater or equal to RESUMED");
        }
        if (lifecycle.getCurrentState().isAtLeast(Lifecycle.State.DESTROYED)) {
            Log.d(TAG, "currentState is greater or equal to DESTROYED");
        }
    }

    @Override
    public void onResume(@NonNull LifecycleOwner owner) {
        DefaultLifecycleObserver.super.onResume(owner);
        Log.d(TAG, "onResume");
    }

    @Override
    public void onPause(@NonNull LifecycleOwner owner) {
        DefaultLifecycleObserver.super.onPause(owner);
        Log.d(TAG, "onPause");
    }

    @Override
    public void onStop(@NonNull LifecycleOwner owner) {
        DefaultLifecycleObserver.super.onStop(owner);
        Log.d(TAG, "onStop");
    }

    @Override
    public void onDestroy(@NonNull LifecycleOwner owner) {
        DefaultLifecycleObserver.super.onDestroy(owner);
        Log.d(TAG, "onDestroy");
        lifecycle.removeObserver(this);
        // owner.getLifecycle().removeObserver(this);
    }

}