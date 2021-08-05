package com.breakout.sample.architecture.lifecycle;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class LifeCycleActivity extends AppCompatActivity {
    private LifecycleObserverEx _lifecycleObserver;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        _lifecycleObserver = new LifecycleObserverEx(getLifecycle());
        getLifecycle().addObserver(_lifecycleObserver);
    }
}
