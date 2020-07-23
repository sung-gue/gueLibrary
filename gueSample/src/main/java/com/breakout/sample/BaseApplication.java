package com.breakout.sample;

import android.app.Application;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Build;

import com.breakout.util.CValue;


public class BaseApplication extends Application {
    private final String TAG = getClass().getSimpleName();

    // 공유할 data나 특정 변수의 초기화를 설정한다.
    @Override
    public void onCreate() {
        Log.i(TAG + " | onCreate");
        CValue.DEBUG = Const.DEBUG;
        MyFirebaseMessagingService.initChannel(this);
        super.onCreate();
    }

    // Application이 종료시에 호출 된다. 하지만 강제종료시에는 호출되지 않는다.
    @Override
    public void onTerminate() {
        Log.i(TAG + " | onTerminate");
        super.onTerminate();
    }

    // Conponent의 변화가 발생할 때에 호출된다.(orientation의 변경)
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        Log.i(TAG + " | onConfigurationChanged");
        super.onConfigurationChanged(newConfig);
    }


    // 시스템 메모리가 부족할 때 호출 된다.
    public void onLowMemory() {
        Log.i(TAG + " | onLowMemory");
        super.onLowMemory();
        System.gc();
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        /*
            INFO: 2020-02-14 sdk version 20 이하는 build.gradle 에서
             android:defaultConfig:multiDexEnabled = true 일 경우 아래 값을 추가해 줘야 한다.
         */
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
//            MultiDex.install(this);
        }
    }


}
