package com.breakout.sample.utils.timer;

import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleEventObserver;
import androidx.lifecycle.LifecycleOwner;

import com.breakout.sample.Log;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Timer;
import java.util.TimerTask;

@SuppressWarnings("unused")
public class LiveCheckTimer extends Timer implements LifecycleEventObserver {
    private static final String TAG = LiveCheckTimer.class.getSimpleName();

    public final long LIVE_CHECK_SCHDULE_DELAY = 1000 * 20;
    public final long LIVE_CHECK_SCHDULE_PERIOD = 1000 * 20;
    private final Lifecycle lifecycle;

    public LiveCheckTimer(Lifecycle lifecycle) {
        this.lifecycle = lifecycle;
        lifecycle.addObserver(this);
    }

    @Override
    public void onStateChanged(@NonNull LifecycleOwner source, @NonNull Lifecycle.Event event) {
        if (event == Lifecycle.Event.ON_DESTROY) {
            Log.d(TAG, TAG + " cancel");
            lifecycle.removeObserver(this);
            cancel();
        }
    }

    public void start(String url) {
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                new LiveCheck().execute(url);
            }
        };
        schedule(task, LIVE_CHECK_SCHDULE_DELAY, LIVE_CHECK_SCHDULE_PERIOD);
    }

    private static class LiveCheck extends AsyncTask<String, String, Void> {

        public LiveCheck() {
            super();
        }

        @Override
        protected Void doInBackground(String... params) {
            String imageUrl = params[0];
            try {
                URL imgUrl = new URL(imageUrl);
                HttpURLConnection conn = (HttpURLConnection) imgUrl.openConnection();
                conn.setConnectTimeout(1000 * 10);

                InputStream is = conn.getInputStream();
                BufferedReader br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
                StringBuilder builder = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    builder.append(line);
                }
                // Log.i(TAG, "api-server live-check success | " + builder);
                is.close();
                br.close();
                conn.disconnect();
            } catch (Exception e) {
                Log.i(TAG, "api-server live-check fail | " + e.getMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
        }
    }
}
