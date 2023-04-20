package com.breakout.sample.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Binder;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Process;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LifecycleService;

import com.breakout.sample.Log;

import ua.naiksoftware.stomp.dto.StompMessage;


/**
 * <uses-permission android:name="android.permission.FOREGROUND_SERVICE" />
 */
public class AppService extends LifecycleService {
    private final String TAG = getClass().getSimpleName();

    public class AppBinder extends Binder {
        public AppService getService() {
            return AppService.this;
        }
    }

    private Context appContext;
    public StatusLiveData.Model statusModel;

    private final IBinder iBinder = new AppBinder();
    private Looper serviceLooper;
    private ServiceHandler serviceHandler;

    public StompController stompController;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "Service onCreate");
        initVariable();
        initServiceHandler();
        initStomp();

        /*HandlerThread thread = new HandlerThread("LiveCheckService", Process.THREAD_PRIORITY_FOREGROUND);
        thread.start();
        Looper mainLooper = thread.getLooper();
        Handler handler = new Handler(mainLooper) {
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                new LiveCheckTimer(getApplicationContext(), getLifecycle()).start();
            }
        };
        Message msg = handler.obtainMessage();
        handler.sendMessage(msg);*/
    }

    private void initVariable() {
        appContext = getApplicationContext();
        statusModel = new StatusLiveData.Model();
    }

    private void initServiceHandler() {
        // 서비스는 메인스레드에서 동작하므로 서비스의 작업은 따로 스레드를 선언
        // CPU priority를 background로 선언하여 ui의 버벅임을 방지
        HandlerThread thread = new HandlerThread("ServiceStartArguments", Process.THREAD_PRIORITY_BACKGROUND);
        thread.start();

        // Get the HandlerThread's Looper and use it for our Handler
        serviceLooper = thread.getLooper();
        serviceHandler = new ServiceHandler(serviceLooper);
    }


    public static String STOMP_SCHEME = "ws";
    public static String STOMP_HOST = "192.168.0.2";
    public static String STOMP_PORT = "8000";
    public static String STOMP_PATH = "stream";
    public static String STOMP_TOKEN = "authToken";

    private void initStomp() {
        String connectUrl = StompController.getStompUrl(STOMP_SCHEME, STOMP_HOST, STOMP_PORT, STOMP_PATH);
        // Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);
        stompController = new StompController(appContext, getLifecycle(), connectUrl, STOMP_TOKEN);
    }

    /**
     * Background Thread Handler
     */
    private final class ServiceHandler extends Handler {
        public ServiceHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            int cnt = 1;
            while (true) {
                try {
                    Thread.sleep(1000);
                    Log.e(TAG, "ServiceHandler : " + cnt);
                    cnt++;
                    if (cnt > 10) {
                        break;
                    }
                } catch (InterruptedException e) {
                    // Restore interrupt status.
                    Thread.currentThread().interrupt();
                }
            }
            /*
                서비스를 사용하였다면 서비스를 종료해 주어야 함.
                아래 메소드는 작업 startId가 가장 최신일때만 서비스를 stop하게 함
                이렇게 하면 동시에 여러 작업할 때, 모든작업이 끝나야 stop이 된다
                이게뭔지는 이후 설명에서 나옴
             */
            // stopSelf(msg.arg1);
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        Log.i(TAG, "Service onStartCommand : " + startId);

        // startId를 전달하여 작업을 중단 할 수 있도록 전송
        if (false) {
            Message msg = serviceHandler.obtainMessage();
            msg.arg1 = startId;
            serviceHandler.sendMessage(msg);
        }

        if (!stompController.isConnected()) {
            connectStomp();
            // connectStompSFM();
        }

        String channelId = createNotificationChannel("app-service-1", "stomp");
        Notification.Builder builder;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            builder = new Notification.Builder(this, channelId);
        } else {
            builder = new Notification.Builder(this);
        }
        builder.setContentTitle("App service");
        builder.setContentText("App service runnung...");
        startForeground(123, builder.build());

        return START_STICKY; // 마지막 intent를 전달하지 않고 null 인텐트로 onStartCommand() 호촐
        // return START_REDELIVER_INTENT; // 서비스에 마지막 전돨된 intent로 inStartCommand() 호출
        // return START_NOT_STICKY; // 서비스 재시작 하지 않음
        // return super.onStartCommand(intent, flags, startId);
    }

    private String createNotificationChannel(String channelId, String channelName) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId,
                    channelName,
                    NotificationManager.IMPORTANCE_NONE
            );
            channel.setLightColor(Color.BLUE);
            channel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(channel);
        }
        return channelId;
    }

    private void connectStomp() {
        stompController.connect();
        subTopic();
    }

    public void subTopic() {
        stompController.subscribe(String.format(StatusLiveData.Model.TOPIC, STOMP_TOKEN), statusTopicListener);
    }

    StompController.TopicListener<StatusLiveData.Model> statusTopicListener = new StompController.TopicListener<StatusLiveData.Model>() {
        @Override
        public void onNext(StompMessage stompMessage, StatusLiveData.Model data) {
            statusModel = data;
            StatusLiveData.get().update(data);
        }

        @Override
        public void onError(Throwable throwable) {
        }

        @Override
        public Class<StatusLiveData.Model> getDataClass() {
            return StatusLiveData.Model.class;
        }
    };

    @Nullable
    @Override
    public IBinder onBind(@NonNull Intent intent) {
        super.onBind(intent);
        Log.i(TAG, "Service onBind");
        return iBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.i(TAG, "Service onUnbind");
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        Log.i(TAG, "Service onDestroy");
        super.onDestroy();
    }
}
