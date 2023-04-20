package com.breakout.util.lifecycle;

import android.os.Handler;

import androidx.lifecycle.Observer;

public abstract class ObserverEx<DATA> implements Observer<DATA> {

    private boolean isStartObserve;
    private final int observerDelaySecond;
    private boolean isSkipFirstChange;

    public ObserverEx(int observerDelaySecond) {
        this(false, observerDelaySecond);
    }

    public ObserverEx(boolean isSkipFirstChange) {
        this(isSkipFirstChange, 0);
    }

    public ObserverEx(boolean isSkipFirstChange, int observerDelaySecond) {
        this.isSkipFirstChange = isSkipFirstChange;
        this.observerDelaySecond = observerDelaySecond;
        if (observerDelaySecond > 0) {
            new Handler().postDelayed(() -> isStartObserve = true, observerDelaySecond * 1000L);
        } else {
            isStartObserve = true;
        }
    }

    @Override
    public void onChanged(DATA data) {
        if (isStartObserve) {
            if (isSkipFirstChange) {
                isSkipFirstChange = false;
                return;
            }
            onChangedEx(data);
        }
    }

    public abstract void onChangedEx(DATA data);
}
