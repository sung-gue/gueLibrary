package com.breakout.sample;

import com.breakout.sample.constant.Const;

/**
 * log switch according to {@link Log#DEBUG }
 *
 * @author sung-gue
 * @version 1.0 (2012. 10. 4.)
 */
public final class Log {
    private final static String TAG = Const.APP_TAG;

    private final static boolean DEBUG = Const.DEBUG;

    public static void v(String msg) {
        if (DEBUG) android.util.Log.v(TAG, msg);
    }

    public static void v(String tag, String msg) {
        if (DEBUG) android.util.Log.v(tag, msg);
    }

    public static void d(String msg) {
        if (DEBUG) android.util.Log.d(TAG, msg);
    }

    public static void d(String tag, String msg) {
        if (DEBUG) android.util.Log.d(tag, msg);
    }

    public static void i(String msg) {
        if (DEBUG) android.util.Log.i(TAG, msg);
    }

    public static void i(String tag, String msg) {
        if (DEBUG) android.util.Log.i(tag, msg);
    }

    public static void i(boolean alwaysShow, String tag, String msg) {
        if (DEBUG || alwaysShow) android.util.Log.i(tag, msg);
    }

    public static void w(String msg) {
        if (DEBUG) android.util.Log.w(TAG, msg);
    }

    public static void w(String tag, String msg) {
        if (DEBUG) android.util.Log.w(tag, msg);
    }

    public static void w(boolean alwaysShow, String tag, String msg) {
        if (DEBUG || alwaysShow) android.util.Log.w(tag, msg);
    }

    public static void w(String msg, Throwable tr) {
        if (DEBUG) android.util.Log.w(TAG, msg, tr);
    }

    public static void w(String tag, String msg, Throwable tr) {
        if (DEBUG) android.util.Log.w(tag, msg, tr);
    }

    public static void w(boolean alwaysShow, String tag, String msg, Throwable tr) {
        if (DEBUG || alwaysShow) android.util.Log.e(tag, msg, tr);
    }

    public static void e(String msg) {
        if (DEBUG) android.util.Log.e(TAG, msg);
    }

    public static void e(String tag, String msg) {
        if (DEBUG) android.util.Log.e(tag, msg);
    }

    public static void e(String msg, Throwable tr) {
        if (DEBUG) android.util.Log.e(TAG, msg, tr);
    }

    public static void e(String tag, String msg, Throwable tr) {
        if (DEBUG) android.util.Log.e(tag, msg, tr);
    }

    public static void e(boolean alwaysShow, String tag, String msg, Throwable tr) {
        if (DEBUG || alwaysShow) android.util.Log.e(tag, msg, tr);
    }

}