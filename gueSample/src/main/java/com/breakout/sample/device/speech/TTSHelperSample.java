package com.breakout.sample.device.speech;

import android.os.Bundle;
import android.speech.tts.TextToSpeech;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.breakout.util.device.speech.TTSHelper;


public class TTSHelperSample extends AppCompatActivity {
    private TTSHelper ttsHelper;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initTTS();

        ttsHelper.getTTS().setLanguage(ttsHelper.getCurrentLocale(this));
        ttsHelper.getTTS().setPitch(1.0f);
        ttsHelper.getTTS().setSpeechRate(1.0f);

        ttsHelper.setConfig(new TTSHelper.Config(null, 1.0f, 1.0f));
    }

    private void initTTS() {
        ttsHelper = new TTSHelper(getLifecycle(), this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        ttsStart1("Hi");
        ttsStart2("Hi 치환1");
    }

    public void ttsStart1(String msg) {
        ttsHelper.speak(msg);
    }

    public void ttsStart2(String msg) {
        String[] replaceArray = new String[]{"치환1,대상1", "치환2,대상2",};
        msg = ttsHelper.getReplaceMsg(msg, replaceArray);
        ttsHelper.speak(msg, TextToSpeech.QUEUE_FLUSH, null, "tts-msg-id");
    }

    public void ttsStop() {
        ttsHelper.stop();
    }

}