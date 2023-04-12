package com.breakout.sample.device.speech;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;


public class STTHelperSample extends AppCompatActivity {
    private STTHelper sttHelper;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initSTT();
    }

    private void initSTT() {
        sttHelper = new STTHelper(getLifecycle(), this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        sttStart(new STTHelper.SttListener() {
            @Override
            public void onSttComplete(String msg) {

            }

            @Override
            public void onSttError(int error, String msg) {

            }
        });
    }

    public void sttStart(STTHelper.SttListener sttListener) {
        sttHelper.startListening(sttListener);
    }

    public void sttStop(STTHelper.SttListener sttListener) {
        sttHelper.stopListening();
    }

}