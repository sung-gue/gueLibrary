package com.breakout.sample.device.speech;

import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.text.TextUtils;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.OnLifecycleEvent;

import java.util.Locale;

/**
 * https://developer.android.com/training/permissions/requesting
 *
 * @author sung-gue
 * @version 1.0 (2020-08-26)
 */
public class TTSHelper implements LifecycleObserver {
    private final String TAG = getClass().getSimpleName();

    private final AppCompatActivity _activity;
    private final Lifecycle _lifecycle;

    private TextToSpeech _tts;

    private final String _replaceStrings[] = new String[]{
            "KDEXc,케이덱스센터",
    };

    public TTSHelper(AppCompatActivity activity) {
        this(activity, null);
    }

    public TTSHelper(AppCompatActivity activity, TextToSpeech.OnInitListener listener) {
        this._activity = activity;
        this._lifecycle = activity.getLifecycle();
        _lifecycle.addObserver(this);

        init(listener);
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    private void onCreate(LifecycleOwner source) {
        Log.d(TAG, "lifecycle : onCreate");
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    private void onStart(LifecycleOwner source) {
        Log.d(TAG, "lifecycle : onStart");
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    private void onDestroy() {
        Log.d(TAG, "lifecycle : onDestroy");
        _lifecycle.removeObserver(this);
        destroy();
    }

    public TextToSpeech getTextToSpeech() {
        return _tts;
    }

    private final TextToSpeech.OnInitListener _onInitListener = new TextToSpeech.OnInitListener() {
        @Override
        public void onInit(int status) {
            if (status != TextToSpeech.ERROR) {
                _tts.setLanguage(Locale.KOREAN);
                Log.d(TAG, "tts initialization success : " + _tts);
            } else {
                Log.d(TAG, "tts initialization fail : " + _tts);
            }
        }
    };

    public void init(TextToSpeech.OnInitListener listener) {
        if (listener == null) {
            listener = _onInitListener;
        }
        _tts = new TextToSpeech(_activity, listener);
        _tts.setOnUtteranceProgressListener(new UtteranceProgressListener() {
            @Override
            public void onStart(String utteranceId) {
                Log.d(TAG, "tts initialization onStart: " + utteranceId);
            }

            @Override
            public void onDone(String utteranceId) {
                Log.d(TAG, "tts initialization onDone: " + utteranceId);
            }

            @Override
            public void onError(String utteranceId) {
                Log.d(TAG, "tts initialization onError: " + utteranceId);
            }

            @Override
            public void onRangeStart(String utteranceId, int start, int end, int frame) {
                Log.d(TAG, "tts initialization onRangeStart: " + utteranceId);
                super.onRangeStart(utteranceId, start, end, frame);
            }
        });
    }

    public TextToSpeech getTTS() {
        return _tts;
    }

    public void speak(int resId) {
        speak(_activity.getString(resId));
    }

    public void speak(String msg) {
        if (TextUtils.isEmpty(msg)) return;
        Log.d(TAG, "tts start");
        for (String replaceStr : _replaceStrings) {
            String[] temp = replaceStr.split(",");
            msg = msg.replaceAll("(?i)" + temp[0], temp[1]);
        }
        // 목소리 톤1.0
        _tts.setPitch(1.0f);
        // 목소리 속도
        _tts.setSpeechRate(1.0f);
        _tts.speak(msg, TextToSpeech.QUEUE_FLUSH, null, "msg");
    }

    public void stop() {
        Log.i(TAG, "tts stop");
        _tts.stop();
    }

    public void destroy() {
        if (_tts != null) {
            _tts.stop();
            _tts.shutdown();
            _tts = null;
        }
    }

}