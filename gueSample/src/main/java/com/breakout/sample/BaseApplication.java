package com.breakout.sample;

import android.app.Application;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.multidex.MultiDex;

import com.breakout.sample.constant.Const;
import com.breakout.sample.fcm.MyFirebaseMessagingService;
import com.breakout.util.constant.CValue;


public class BaseApplication extends Application {
    private final String TAG = getClass().getSimpleName();
//    private static GoogleAnalytics sAnalytics;
//    private static Tracker sTracker;


    /**
     * 공유할 data나 특정 변수의 초기화를 설정한다.
     */
    @Override
    public void onCreate() {
        Log.i(TAG + " | onCreate");
        CValue.DEBUG = Const.DEBUG;
        MyFirebaseMessagingService.initChannel(this);
        super.onCreate();

//        sAnalytics = GoogleAnalytics.getInstance(this);
    }

    /*
     * Gets the default {@link Tracker} for this {@link Application}.
     *
     * @return tracker
     */
    /*synchronized public Tracker getDefaultTracker() {
        // To enable debug logging use: adb shell setprop log.tag.GAv4 DEBUG
        if (sTracker == null) {
            sTracker = sAnalytics.newTracker(R.xml.global_tracker);
        }

        return sTracker;
    }*/

    /**
     * Application이 종료시에 호출 (강제종료시에는 미호출)
     */
    @Override
    public void onTerminate() {
        Log.i(TAG + " | onTerminate");
        super.onTerminate();
    }

    /**
     * Component의 변화가 발생할 때에 호출 (orientation의 변경)
     */
    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        Log.i(TAG + " | onConfigurationChanged");
        super.onConfigurationChanged(newConfig);
    }

    /**
     * 시스템 메모리가 부족할 때 호출
     */
    public void onLowMemory() {
        Log.i(TAG + " | onLowMemory");
        super.onLowMemory();
        //System.gc();
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        /*
            INFO: 2020-02-14 sdk version 20 이하는 build.gradle 에서
             android:defaultConfig:multiDexEnabled = true 일 경우 아래 값을 추가해 줘야 한다.
         */
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            MultiDex.install(this);
        }
    }

}
