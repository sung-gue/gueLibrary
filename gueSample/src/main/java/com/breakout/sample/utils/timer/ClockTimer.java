package com.breakout.sample.utils.timer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleEventObserver;
import androidx.lifecycle.LifecycleOwner;

import com.breakout.util.date.DateUtil;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

@SuppressWarnings({"unused"})
public class ClockTimer extends Timer implements LifecycleEventObserver {
    private final String TAG = getClass().getSimpleName();

    public interface ClockTimerListener {
        void onNext(String time);
    }

    private final AppCompatActivity activity;
    private final Lifecycle lifecycle;
    private final ClockTimerListener clockTimerListener;

    private final int SCHDULE_DELAY = 0;
    private final int SCHDULE_PERIOD = 1000;

    public ClockTimer(AppCompatActivity activity, @NonNull ClockTimerListener clockTimerListener) {
        super();
        this.activity = activity;
        this.lifecycle = activity.getLifecycle();
        this.clockTimerListener = clockTimerListener;
        lifecycle.addObserver(this);
    }

    @Override
    public void onStateChanged(@NonNull LifecycleOwner source, @NonNull Lifecycle.Event event) {
        if (event == Lifecycle.Event.ON_DESTROY) {
            lifecycle.removeObserver(this);
            cancel();
        }
    }

    public void start() {
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                if (activity == null || activity.isFinishing()) {
                    return;
                }
                activity.runOnUiThread(() -> {
                    if (clockTimerListener != null) {
                        clockTimerListener.onNext(DateUtil.dateFormat(new Date(), 20));
                    }
                });
            }
        };
        schedule(task, SCHDULE_DELAY, SCHDULE_PERIOD);
    }
}