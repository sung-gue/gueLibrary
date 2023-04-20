package com.breakout.sample.architecture.lifecycle;

import androidx.annotation.NonNull;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleEventObserver;
import androidx.lifecycle.LifecycleOwner;

import com.breakout.sample.Log;

/**
 * LifecycleEventObserver
 * <p>
 * <a href="https://developer.android.com/guide/components/activities/activity-lifecycle">
 * - 활동 수명 주기에 관한 이해
 * </a>
 * <p>
 * <a href="https://developer.android.com/topic/libraries/architecture/lifecycle">
 * - 수명 주기 인식 구성요소로 수명 주기 처리
 * </a>
 * <p>
 * <a href="https://developer.android.com/jetpack/androidx/releases/lifecycle">
 * - Lifecycle
 * </a>
 */
public class SampleObserver implements LifecycleEventObserver {
    private final String TAG = getClass().getSimpleName();
    private Lifecycle lifecycle;


    public SampleObserver() {
    }

    public SampleObserver(Lifecycle lifecycle) {
        this.lifecycle = lifecycle;
    }

    @Override
    public void onStateChanged(@NonNull LifecycleOwner source, @NonNull Lifecycle.Event event) {
        Log.d(TAG, String.format(
                "onStateChanged | %s - %s / %s",
                source.getClass().getSimpleName(),
                source.getLifecycle().getCurrentState(),
                event.name()
        ));
        switch (event) {
            case ON_ANY:
                // break;
            case ON_CREATE:
                // break;
            case ON_RESUME:
                // break;
            case ON_START:
                // break;
            case ON_PAUSE:
                // break;
            case ON_STOP:
                break;
            case ON_DESTROY:
                if (lifecycle != null) lifecycle.removeObserver(this);
                // source.getLifecycle().removeObserver(this);
                break;
        }
    }

    private void checkCurrentState() {
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

}