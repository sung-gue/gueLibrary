package com.breakout.sample.architecture.lifecycle;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;

import com.breakout.util.lifecycle.ObserverEx;

public class SampleActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addObserver();
    }

    private SampleObserver sampleObserver;

    private void addObserver() {
        sampleObserver = new SampleObserver();
        getLifecycle().addObserver(sampleObserver);

        SampleObserver sampleObserver = new SampleObserver(getLifecycle());
        SampleObserver2 sampleObserver2 = new SampleObserver2(getLifecycle());
        SampleObserver3 sampleObserver3 = new SampleObserver3(getLifecycle());
    }

    private void removeObserver() {
        if (sampleObserver != null) getLifecycle().removeObserver(sampleObserver);
    }

    private void observeLiveData() {
        GlobalLiveData.get().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {

            }
        });
        GlobalLiveData.get().observe(this, new ObserverEx<String>(5) {
            @Override
            public void onChangedEx(String s) {

            }
        });
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        removeObserver();
    }
}
