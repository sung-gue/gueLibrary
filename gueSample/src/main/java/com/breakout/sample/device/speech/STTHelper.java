package com.breakout.sample.device.speech;


import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.OnLifecycleEvent;

import com.breakout.sample.R;
import com.breakout.util.widget.CustomDialog;

import java.util.ArrayList;

public class STTHelper implements LifecycleObserver {
    private final String TAG = getClass().getSimpleName();

    public interface SttListener {
        void onSttComplete(String msg);

        void onSttError(int error, String msg);
    }

    private final AppCompatActivity _activity;
    private final Lifecycle _lifecycle;
    private Intent _sttIntent;
    private SpeechRecognizer _stt;
    private SttListener _sttListener;

    public STTHelper(AppCompatActivity activity) {
        this._activity = activity;
        this._lifecycle = activity.getLifecycle();
        init();
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

    public SpeechRecognizer getSpeechRecognizer() {
        return _stt;
    }

    public void init() {
        _sttIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        _sttIntent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, _activity.getPackageName());
        _sttIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ko-KR");
        _stt = SpeechRecognizer.createSpeechRecognizer(_activity);
        _stt.setRecognitionListener(new RecognitionListener() {
            @Override
            public void onReadyForSpeech(Bundle params) {
                // startListening()호출 후 음성이 입력되기 전 상태
                Log.d(TAG, "RecognitionListener.onReadyForSpeech");
                Toast.makeText(_activity, "음성인식을 시작합니다.", Toast.LENGTH_SHORT).show();
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
                if (_sttListener != null) {
                    _sttListener.onSttError(error, msg);
                } else {
                    new CustomDialog(_activity)
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
                if (_sttListener != null) {
                    _sttListener.onSttComplete(msg);
                } else {
                    new CustomDialog(_activity)
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
        return _stt;
    }

    private SttListener _sttListenerSample = new SttListener() {
        @Override
        public void onSttComplete(String msg) {
            new CustomDialog(_activity)
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
            new CustomDialog(_activity)
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
        _sttListener = sttListener;
        _stt.startListening(_sttIntent);
    }

    public void stopListening() {
        _stt.stopListening();
    }

    public void destroy() {
        if (_stt != null) {
            _stt.stopListening();
            _stt.cancel();
            _stt.destroy();
            _stt = null;
        }
    }
}