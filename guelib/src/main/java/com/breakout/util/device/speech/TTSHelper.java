package com.breakout.util.device.speech;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.core.os.ConfigurationCompat;
import androidx.core.os.LocaleListCompat;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleEventObserver;
import androidx.lifecycle.LifecycleOwner;

import com.breakout.util.Log;

import java.util.Locale;

/**
 * Google Text to speech
 *
 * @author sung-gue
 * @version 1.0 (2020-08-26)
 */
public class TTSHelper implements LifecycleEventObserver {
    private final String TAG = getClass().getSimpleName();

    public static class Config implements Cloneable {
        public Locale language;
        public float pintch = 1.0f;
        public float speechRate = 1.0f;

        public Config() {
        }

        public Config(Locale language, float pintch, float speechRate) {
            this.language = language;
            this.pintch = pintch;
            this.speechRate = speechRate;
        }

        @Override
        public Config clone() {
            try {
                Config clone = (Config) super.clone();
                // TODO: copy mutable state here, so the clone can't change the internals of the original
                return clone;
            } catch (CloneNotSupportedException e) {
                throw new AssertionError();
            }
        }
    }

    private final Lifecycle lifecycle;
    private TextToSpeech tts;
    private final Config config = new Config();

    public TTSHelper(@NonNull Lifecycle lifecycle, @NonNull Context context) {
        this(lifecycle, context, null);
    }

    public TTSHelper(@NonNull Lifecycle lifecycle, @NonNull Context context, TextToSpeech.OnInitListener listener) {
        this.lifecycle = lifecycle;
        this.lifecycle.addObserver(this);
        init(context, listener);
    }

    @Override
    public void onStateChanged(@NonNull LifecycleOwner source, @NonNull Lifecycle.Event event) {
        if (event == Lifecycle.Event.ON_DESTROY) {
            lifecycle.removeObserver(this);
            destroy();
        }
    }

    private void init(Context context, TextToSpeech.OnInitListener listener) {
        tts = new TextToSpeech(context, status -> {
            if (status == TextToSpeech.SUCCESS) {
                Log.v(TAG, "tts engine initialization success : " + tts);
                setConfig(config);
            } else {
                Log.e(TAG, "tts engine initialization error : " + tts);
            }
            listener.onInit(status);
        });
        tts.setOnUtteranceProgressListener(new UtteranceProgressListener() {
            @Override
            public void onStart(String utteranceId) {
                Log.v(TAG, "tts utterance onStart: " + utteranceId);
            }

            @Override
            public void onDone(String utteranceId) {
                Log.v(TAG, "tts utterance onDone: " + utteranceId);
            }

            @Override
            public void onError(String utteranceId) {
                Log.e(TAG, "tts utterance onError: " + utteranceId);
            }

            @Override
            public void onRangeStart(String utteranceId, int start, int end, int frame) {
                Log.v(TAG, "tts utterance onRangeStart: " + utteranceId);
                super.onRangeStart(utteranceId, start, end, frame);
            }
        });
    }

    public TextToSpeech getTTS() {
        return tts;
    }

    public void setConfig(Config config) {
        if (config.language != null) {
            tts.setLanguage(config.language);
            this.config.language = config.language;
        }
        if (config.pintch > 0) {
            tts.setPitch(config.pintch);
            this.config.pintch = config.pintch;
        }
        if (config.speechRate > 0) {
            tts.setSpeechRate(config.speechRate);
            this.config.speechRate = config.speechRate;
        }
    }

    public void speak(String msg) {
        speak(msg, TextToSpeech.QUEUE_FLUSH, null, "tts-msg-id");
    }

    public void speak(String msg, final int queueMode, final Bundle params, final String utteranceId) {
        if (TextUtils.isEmpty(msg)) return;
        Log.v(TAG, "tts start");
        tts.setPitch(config.pintch);
        tts.setSpeechRate(config.speechRate);
        tts.speak(msg, queueMode, params, utteranceId);
    }

    public void stop() {
        Log.v(TAG, "tts stop");
        tts.stop();
    }

    public void destroy() {
        Log.v(TAG, "tts destroy");
        if (tts != null) {
            tts.stop();
            tts.shutdown();
            tts = null;
        }
    }

    public Locale getCurrentLocale(Context context) {
        if (context == null) return null;
        Resources resources = context.getResources();
        Configuration configuration = resources.getConfiguration();
        LocaleListCompat list = ConfigurationCompat.getLocales(configuration);
        return list.size() > 0 ? list.get(0) : null;
    }

    /**
     * new String[]{"치환1,대상1", "치환2,대상2",}
     */
    public String getReplaceMsg(String msg, String[] replaceArray) {
        if (TextUtils.isEmpty(msg)) return msg;
        for (String replaceStr : replaceArray) {
            String[] temp = replaceStr.split(",");
            msg = msg.replaceAll("(?i)" + temp[0], temp[1]);
        }
        return msg;
    }
}