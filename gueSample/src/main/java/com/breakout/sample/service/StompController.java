package com.breakout.sample.service;

import android.content.Context;
import android.os.Handler;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleEventObserver;
import androidx.lifecycle.LifecycleOwner;

import com.breakout.sample.Log;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.reactivestreams.Subscription;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import ua.naiksoftware.stomp.Stomp;
import ua.naiksoftware.stomp.StompClient;
import ua.naiksoftware.stomp.dto.StompHeader;
import ua.naiksoftware.stomp.dto.StompMessage;

/**
 * STOMP protocol
 * <p>
 * https://github.com/NaikSoftware/StompProtocolAndroid
 * <p/>
 * write build.gradle
 * <pre>
 *     // Stomp | https://github.com/NaikSoftware/StompProtocolAndroid
 *     implementation 'com.github.NaikSoftware:StompProtocolAndroid:1.6.6'
 *
 *     // rx java | stomp dependencies
 *     implementation 'io.reactivex.rxjava2:rxjava:2.2.5'
 *     implementation 'io.reactivex.rxjava2:rxandroid:2.1.0'
 *
 *     // Gson
 *     implementation "com.google.code.gson:gson:2.8.9"
 * </pre>
 *
 * @author sung-gue
 * @version 1.0 (2022-08-29)
 */
public class StompController implements LifecycleEventObserver {
    private final String TAG = getClass().getSimpleName();

    public interface TopicListener<DATA> {
        void onNext(StompMessage stompMessage, DATA data);

        void onError(Throwable throwable);

        Class<DATA> getDataClass();
    }

    public interface TopicDetailListener<DATA> extends TopicListener<DATA> {
        void onComplete();

        void onSubscribe(Subscription subscription);
    }

    private static class TopicInfo<DATA> {
        String path;
        List<StompHeader> headers;
        TopicListener<DATA> listener;

        public TopicInfo(String path, List<StompHeader> headers, TopicListener<DATA> listener) {
            this.path = path;
            this.headers = headers;
            this.listener = listener;
        }
    }

    private final Context context;
    private final Lifecycle lifecycle;
    private final String connectUrl;
    private final String authToken;

    private List<StompHeader> connectHeaders;
    private final ArrayList<TopicInfo<?>> topicList = new ArrayList<>();

    private final int HEART_BEAT_SEC = 15;
    private StompClient stompClient;
    private CompositeDisposable compositeDisposable;

    private final Gson gson = new GsonBuilder().create();

    /**
     * @return [scheme]://[host]:[port]/[path]/websocket
     */
    public static String getStompUrl(String scheme, String host, String port, String path) {
        return String.format("%s://%s:%s/%s/websocket", scheme, host, port, path);
    }


    public StompController(@NonNull Context context, @NonNull String connectUrl) {
        this(context, null, connectUrl, null);
    }

    public StompController(@NonNull Context context, Lifecycle lifecycle, @NonNull String connectUrl) {
        this(context, lifecycle, connectUrl, null);
    }

    public StompController(@NonNull Context context, Lifecycle lifecycle, @NonNull String connectUrl, String authToken) {
        this.context = context;
        this.lifecycle = lifecycle;
        this.connectUrl = connectUrl;
        this.authToken = authToken;
        if (lifecycle != null) {
            lifecycle.addObserver(this);
        }
        init();
    }

    @Override
    public void onStateChanged(@NonNull LifecycleOwner source, @NonNull Lifecycle.Event event) {
        Log.d(TAG, String.format(
                "onStateChanged | %s - %s / %s",
                source.getClass().getSimpleName(),
                source.getLifecycle().getCurrentState(),
                event.name()
        ));
        switch (event) {
            case ON_DESTROY:
                if (lifecycle != null) lifecycle.removeObserver(this);
                // source.getLifecycle().removeObserver(this);
                disconnect();
                break;
        }
    }

    private void init() {
        stompClient = Stomp.over(Stomp.ConnectionProvider.OKHTTP, connectUrl);
        stompClient.withClientHeartbeat(1000 * HEART_BEAT_SEC).withServerHeartbeat(1000 * HEART_BEAT_SEC);
    }

    public boolean isConnected() {
        return stompClient != null && stompClient.isConnected();
    }

    public List<StompHeader> getAuthorizationHeader() {
        List<StompHeader> headers = new ArrayList<>();
        if (!TextUtils.isEmpty(authToken)) {
            headers.add(new StompHeader("Authorization", authToken));
        }
        return headers;
    }

    public void connect() {
        this.connect(getAuthorizationHeader());
    }

    public void connect(List<StompHeader> headers) {
        this.connectHeaders = headers;
        resetSubscriptions();
        Disposable dispLifecycle = stompClient.lifecycle()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(lifecycleEvent -> {
                    switch (lifecycleEvent.getType()) {
                        case OPENED:
                            Log.i(TAG, "Stomp connection opened");
                            break;
                        case ERROR:
                            Log.e(TAG, "Stomp connection error", lifecycleEvent.getException());
                            break;
                        case CLOSED:
                            Log.w(TAG, "Stomp connection closed");
                            resetSubscriptions();
                            new Handler().postDelayed(() -> {
                                if (!stompClient.isConnected()) {
                                    Log.i(TAG, "Stomp try reconnect");
                                    // stompClient.reconnect();
                                    connect(connectHeaders);
                                    ArrayList<TopicInfo<?>> list = new ArrayList<>(topicList);
                                    topicList.clear();
                                    for (TopicInfo<?> info : list) {
                                        subscribe(info.path, info.headers, info.listener);
                                    }
                                }
                            }, 10 * 1000);
                            break;
                        case FAILED_SERVER_HEARTBEAT:
                            Log.e(TAG, "Stomp failed server heartbeat");
                            break;
                    }
                });
        compositeDisposable.add(dispLifecycle);
        stompClient.connect(headers);
    }

    private void resetSubscriptions() {
        if (compositeDisposable != null) {
            compositeDisposable.dispose();
        }
        compositeDisposable = new CompositeDisposable();
    }

    public void disconnect() {
        if (stompClient != null) {
            stompClient.disconnect();
        }
        if (compositeDisposable != null) {
            compositeDisposable.dispose();
        }
    }

    public <DATA> StompController subscribe(@NonNull String path, @NonNull TopicListener<DATA> listener) {
        return this.subscribe(path, null, listener);
    }

    public <DATA> StompController subscribe(@NonNull String path, List<StompHeader> headers, @NonNull TopicListener<DATA> listener) {
        topicList.add(new TopicInfo<>(path, headers, listener));
        Disposable dispTopic = stompClient.topic(path, headers)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                // .filter(new Predicate<StompMessage>() {
                //     @Override
                //     public boolean test(StompMessage stompMessage) throws Exception {
                //         String dest = stompMessage.findHeader(StompHeader.DESTINATION);
                //         Log.e(TAG, "!!! ++ " + dest);
                //         return true;
                //     }
                //
                // })
                .subscribe(stompMessage -> {
                    String msg = stompMessage.getPayload();
                    // Log.i(TAG, String.format("Topic [%s] | onNext(Received) : %s", path, msg));
                    DATA instance = parsing(msg, listener.getDataClass());
                    // DATA instance = mGson.fromJson(msg, listener.getDataClass());
                    listener.onNext(stompMessage, instance);
                }, throwable -> {
                    listener.onError(throwable);
                    Log.e(TAG, String.format("Topic [%s] | onError : %s", path, throwable.getMessage()), throwable);
                });
        compositeDisposable.add(dispTopic);
        return this;
    }

    private <DATA> DATA parsing(String msg, Class<DATA> clazz) {
        DATA instance = null;
        try {
            instance = clazz.newInstance();
            instance = gson.fromJson(msg, clazz);
        } catch (Exception e) {
            Log.e(TAG, "parsing error : " + e.getMessage(), e);
        }
        return instance;
    }

    // Receive greetings
    private void subTopicExample(String path, List<StompHeader> headers) {
        //noinspection Convert2Lambda,RedundantThrows
        Disposable dispTopic = stompClient.topic(path, headers)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<StompMessage>() {
                    @Override
                    public void accept(StompMessage stompMessage) throws Exception {
                        String msg = stompMessage.getPayload();
                        Log.i(TAG, String.format("Topic [%s] | onNext(Received) : %s", path, msg));
                        Object object = gson.fromJson(msg, Object.class);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Log.e(TAG, String.format("Topic [%s] | onError : %s", path, throwable.getMessage()), throwable);
                    }
                });
        compositeDisposable.add(dispTopic);
        //noinspection CodeBlock2Expr
        dispTopic = stompClient.topic(path, headers)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(stompMessage -> {
                    String msg = stompMessage.getPayload();
                    Log.i(TAG, String.format("Topic [%s] | onNext(Received) : %s", path, msg));
                    Object object = gson.fromJson(msg, Object.class);
                }, throwable -> {
                    Log.e(TAG, String.format("Topic [%s] | onError : %s", path, throwable.getMessage()), throwable);
                });
        compositeDisposable.add(dispTopic);
    }
}
