package com.breakout.util;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import java.util.List;

/**
 * intent & task log
 *
 * @author sung-gue
 * @version 1.0 (2016. 2. 3.)
 */
public class LogUtil {

    public static String getIntentCheckLog(Intent intent, String title) {
        StringBuilder builder = new StringBuilder();
        if (intent != null) {
            builder.append(String.format(
                    "\n| intent      | %s" +
                    "\n| component   | %s" +
                    "\n| uri(data)   | %s",
                    intent, intent.getComponent(), intent.getData()
            ));
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                for (String key : bundle.keySet()) {
                    builder.append(String.format(
                            "\n| extra       | %s : %s",
                            key, bundle.get(key)
                    ));
                }
            }
        } else {
            builder.append("\n| intent is null !!");
        }
        return String.format(
                "\n----- check intent -----" +
                "\n| %s %s" +
                "\n------------------------",
                title, builder
        );
    }

    public static String getActivityTaskLog(Activity activity, String title) {
        return getActivityTaskLog(activity, title, false);
    }

    public static String getActivityTaskLog(Activity activity, String title, boolean isDebug) {
        if (isDebug) getActivityTaskDebugLog(activity);

        // get activity task
        ActivityManager am = (ActivityManager) activity.getSystemService(Context.ACTIVITY_SERVICE);

        StringBuilder builder = new StringBuilder();
        try {
            List<ActivityManager.RunningTaskInfo> info = am.getRunningTasks(1);
            builder.append(String.format(
                    "\n| baseActivity    | %s" +
                    "\n| topActivity     | %s" +
                    "\n| numActivities   | %s" +
                    "\n| numRunning      | %s" +
                    title,
                    info.get(0).baseActivity.getClassName(),
                    info.get(0).topActivity.getClassName(),
                    info.get(0).numActivities,
                    info.get(0).numRunning
            ));
        } catch (Exception e) {
            builder.append(String.format(
                    "\n| error get task info!!" +
                    "\n|    error msg    | %s",
                    e.getMessage()
            ));
        }
        return String.format(
                "\n----- task info -----" +
                "\n| %s %s" +
                "\n---------------------",
                title, builder
        );
    }

    private static void getActivityTaskDebugLog(Activity activity) {
        // get activity task
        ActivityManager am = (ActivityManager) activity.getSystemService(Context.ACTIVITY_SERVICE);

        // process & task check
        List<ActivityManager.RunningAppProcessInfo> processList = am.getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo runningAppProcessInfo : processList) {
            if (activity.getPackageName().equals(runningAppProcessInfo.processName))
                Log.e("getActivityTaskLog | runningAppProcessInfo : " +
                      runningAppProcessInfo.pid + " / " + runningAppProcessInfo.processName);
            else
                Log.e("getActivityTaskLog | runningAppProcessInfo : " +
                      runningAppProcessInfo.pid + " / " + runningAppProcessInfo.processName);
        }
        List<ActivityManager.RunningTaskInfo> taskList = am.getRunningTasks(processList.size());
        for (ActivityManager.RunningTaskInfo runningTaskInfo : taskList) {
            if (activity.getPackageName().equals(runningTaskInfo.topActivity.getPackageName()))
                Log.e("getActivityTaskLog | runningTaskInfo : " +
                      runningTaskInfo.topActivity.getPackageName() + " / " + runningTaskInfo.topActivity);
            else
                Log.e("getActivityTaskLog | runningTaskInfo : " +
                      runningTaskInfo.topActivity.getPackageName() + " / " + runningTaskInfo.topActivity);
        }
    }

}
