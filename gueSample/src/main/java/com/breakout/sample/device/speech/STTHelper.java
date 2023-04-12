package com.breakout.sample.device.speech;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleEventObserver;
import androidx.lifecycle.LifecycleOwner;

import com.breakout.sample.R;
import com.breakout.util.Log;
import com.breakout.util.widget.CustomDialog;

import java.util.ArrayList;

/**
 * Google Speech to text (speech recognizer)
 *
 * @author sung-gue
 * @version 1.0 (2020-08-26)
 */
public class STTHelper implements LifecycleEventObserver {
    private final String TAG = getClass().getSimpleName();

    public interface SttListener {
        void onSttComplete(String msg);

        void onSttError(int error, String msg);
    }

    private final Context context;
    private final Lifecycle lifecycle;
    private Intent sttIntent;
    private SpeechRecognizer stt;
    private SttListener sttListener;

    public STTHelper(@NonNull Lifecycle lifecycle, @NonNull Context context) {
        this.lifecycle = lifecycle;
        this.context = context;
        this.lifecycle.addObserver(this);
        init();
    }

    @Override
    public void onStateChanged(@NonNull LifecycleOwner source, @NonNull Lifecycle.Event event) {
        if (event == Lifecycle.Event.ON_DESTROY) {
            lifecycle.removeObserver(this);
            destroy();
        }
    }

    public void init() {
        sttIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        sttIntent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, context.getPackageName());
        sttIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ko-KR");
        stt = SpeechRecognizer.createSpeechRecognizer(context);
        stt.setRecognitionListener(new RecognitionListener() {
            @Override
            public void onReadyForSpeech(Bundle params) {
                // startListening()호출 후 음성이 입력되기 전 상태
                Log.d(TAG, "RecognitionListener.onReadyForSpeech");
                Toast.makeText(context, "음성인식을 시작합니다.", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onBeginningOfSpeech() {
                // 음성이 입력되고 있는 상태
                Log.d(TAG, "RecognitionListener.onBeginningOfSpeech");
            }

            @Override
            public void onRmsChanged(float rmsdB) {
                // 사운드 레벨이 변경된 상태
                // 입력받는 소리의 크기를 알려줍니다.
                Log.d(TAG, "RecognitionListener.onRmsChanged");
            }

            @Override
            public void onBufferReceived(byte[] buffer) {
                // 많은 소리가 수신된 상태
                // 사용자가 말을 시작하고 인식이 된 단어를 buffer에 담습니다.
                Log.d(TAG, "RecognitionListener.onBufferReceived");
            }

            @Override
            public void onEndOfSpeech() {
                // 음성 인식을 마친 상태
                Log.d(TAG, "RecognitionListener.onEndOfSpeech");
            }

            @Override
            public void onError(int error) {
                String msg;
                switch (error) {
                    case SpeechRecognizer.ERROR_AUDIO:
                        msg = "음성인식 오디오 오류입니다.";
                        break;
                    case SpeechRecognizer.ERROR_CLIENT:
                        msg = "음성인식 클라이언트 오류입니다.";
                        break;
                    case SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS:
                        msg = "음성인식 사용에 대한 권한이 없습니다.";
                        break;
                    // 네트워크 에러
                    case SpeechRecognizer.ERROR_NETWORK:
                        msg = "네트워크 오류가 발생했습니다.";
                        break;
                    // 네트워크 타임아웃
                    case SpeechRecognizer.ERROR_NETWORK_TIMEOUT:
                        msg = "네트웍 타임아웃이 발생했습니다.";
                        break;
                    // 결과 없음
                    case SpeechRecognizer.ERROR_NO_MATCH:
                        msg = "음성인식 결과가 없습니다.";
                        break;
                    case SpeechRecognizer.ERROR_RECOGNIZER_BUSY:
                        msg = "음성인식 결과를 받지 못했습니다.";
                        break;
                    // 서버오류
                    case SpeechRecognizer.ERROR_SERVER:
                        msg = "음성인식 결과를 구글로부터 받지 못했습니다.";
                        break;
                    // 인식 제한시간 초과
                    case SpeechRecognizer.ERROR_SPEECH_TIMEOUT:
                        msg = "음성인식 제한시간이 초과되었습니다.";
                        break;
                    default:
                        msg = "음성인식이 정상적으로 작동하지 않습니다.";
                        break;
                }
                if (sttListener != null) {
                    sttListener.onSttError(error, msg);
                } else {
                    new CustomDialog(context)
                            .setCancel(false)
                            .setContents("음성인식 오류", msg)
                            .setOkBt(R.string.ok, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            })
                            .show();
                }
            }

            @Override
            public void onResults(Bundle results) {
                // 인식 결과가 준비되면 호출
                ArrayList<String> matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                String msg = "";
                for (int i = 0; i < matches.size(); i++) {
                    Log.d(TAG, "RecognitionListener.onResults : " + matches.get(i));
                    msg += matches.get(i);
                }
                Log.d(TAG, "RecognitionListener.onResults complete : " + msg);
                if (sttListener != null) {
                    sttListener.onSttComplete(msg);
                } else {
                    new CustomDialog(context)
                            .setCancel(false)
                            .setContents("음성인식 결과", msg)
                            .setOkBt(R.string.yes, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            })
                            .setCancelBt(R.string.no, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            })
                            .show();
                }
            }

            @Override
            public void onPartialResults(Bundle partialResults) {
                // 부분적으로 인식 결과를 사용하기 위한 상태
            }

            @Override
            public void onEvent(int eventType, Bundle params) {
                // 향후 이벤트를 추가하기 위해 예약된 상태
            }
        });
    }

    public SpeechRecognizer getSTT() {
        return stt;
    }

    public SpeechRecognizer getSpeechRecognizer() {
        return stt;
    }

    private SttListener sttListenerSample = new SttListener() {
        @Override
        public void onSttComplete(String msg) {
            new CustomDialog(context)
                    .setCancel(false)
                    .setContents("음성인식 결과", msg)
                    .setOkBt(R.string.yes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    })
                    .setCancelBt(R.string.no, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    })
                    .show();
        }

        @Override
        public void onSttError(int error, String msg) {
            new CustomDialog(context)
                    .setCancel(false)
                    .setContents("음성인식 결과", msg)
                    .setOkBt(R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    })
                    .show();
        }
    };

    public void startListening(SttListener sttListener) {
        Log.v(TAG, "stt startListening");
        this.sttListener = sttListener;
        stt.startListening(sttIntent);
    }

    public void stopListening() {
        Log.v(TAG, "stt stopListening");
        stt.stopListening();
    }

    public void destroy() {
        Log.v(TAG, "stt destroy");
        if (stt != null) {
            stt.stopListening();
            stt.cancel();
            stt.destroy();
            stt = null;
        }
    }
}