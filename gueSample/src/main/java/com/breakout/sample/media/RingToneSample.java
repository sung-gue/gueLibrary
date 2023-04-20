package com.breakout.sample.media;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.breakout.sample.Log;

/**
 * play RingTone
 *
 * @author sung-gue
 * @version 1.0 (2023-06-23)
 */
public class RingToneSample extends AppCompatActivity {
    private final String TAG = getClass().getSimpleName();

    private MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        playRingTone();
    }

    private void playRingTone() {
        // playRingTone1(null);
        playRingTone2(null);
        // playRingTone3(null);
    }

    private void stopRingTone() {
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
            }
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    private void playRingTone1(View view) {
        // Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
        // Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);
        mediaPlayer.setLooping(true);
        mediaPlayer.start();
        if (view != null) {
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                        mediaPlayer.stop();
                        mediaPlayer.release();
                        mediaPlayer = null;
                    } else {
                        mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);
                        mediaPlayer.setLooping(true);
                        mediaPlayer.start();
                    }
                }
            });
        }
    }

    private void playRingTone2(View view) {
        // Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
        // Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setOnPreparedListener(mp -> {
            Log.i(TAG, String.format("MediaPlayer onPrepared file: %s", uri));
            mediaPlayer.start();
        });
        mediaPlayer.setOnCompletionListener(mp -> {
            Log.i(TAG, String.format("MediaPlayer onCompletion file: %s", uri));
        });
        mediaPlayer.setOnErrorListener((mp, what, extra) -> {
            Log.e(TAG, String.format("MediaPlayer onError  what: %s, extra: %s", what, extra));
            return true;
        });
        try {
            mediaPlayer.setDataSource(this, uri);
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_RING);
            mediaPlayer.setLooping(true);
            mediaPlayer.prepare();
            // mediaPlayer.prepareAsync();
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        }
        if (view != null) {
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mediaPlayer.isPlaying()) {
                        mediaPlayer.stop();
                        // mediaPlayer.release();
                        // mediaPlayer = null;
                    } else {
                        try {
                            mediaPlayer.prepare();
                            // mediaPlayer.prepareAsync();
                        } catch (Exception e) {
                            Log.e(TAG, e.getMessage(), e);
                        }
                    }
                }
            });
        }
    }

    private void playRingTone3(View view) {
        // Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
        // Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Ringtone ringtone = RingtoneManager.getRingtone(this, uri);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            ringtone.setLooping(true);
        }
        ringtone.play();
        if (view != null) {
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (ringtone.isPlaying()) {
                        ringtone.stop();
                    } else {
                        ringtone.play();
                    }
                }
            });
        }
    }

}