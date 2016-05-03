package com.breakout.sample;

import android.app.Application;
import android.content.res.Configuration;


public class BaseApplication extends Application {
    private final String TAG = getClass().getSimpleName();

    // 공유할 data나 특정 변수의 초기화를 설정한다.
    @Override
    public void onCreate() {
        Log.i(TAG + " | onCreate");
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


}
