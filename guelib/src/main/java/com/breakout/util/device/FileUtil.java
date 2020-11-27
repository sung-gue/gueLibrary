package com.breakout.util.device;

import android.content.Context;


/**
 * Android File Util
 *
 * @author sung-gue
 * @version 1.0 (2012. 10. 15.)
 */
public class FileUtil {

    public static String getCacheDir(Context context) {
        String cacheDir = null;
        if (context != null) {
            if (context.getExternalCacheDir() != null) {
                cacheDir = context.getExternalCacheDir().getAbsolutePath();
            } else {
                cacheDir = context.getCacheDir().getAbsolutePath();
            }
        }
        return cacheDir;
    }
}