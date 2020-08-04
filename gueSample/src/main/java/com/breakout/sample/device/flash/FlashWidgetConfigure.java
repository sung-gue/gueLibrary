package com.breakout.sample.device.flash;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.widget.RemoteViews;

import com.breakout.sample.constant.Const;
import com.breakout.sample.Log;
import com.breakout.sample.R;
import com.breakout.util.device.CameraUtil;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;


/**
 * Flash Widget Configure
 *
 * @author gue
 * @version 1.0
 * @copyright Copyright.2011.gue.All rights reserved.
 * @history <ol>
 * <li>변경자/날짜 : 변경사항</li>
 * </ol>
 * @since 2013. 10. 2.
 */
public class FlashWidgetConfigure {

    private final static int NOTIFICATION_ID = 5678;


    public static void updateWidgetLayoutForFlash(Context context) {
        Log.d("FlashWidgetConfigure | updateWidgetLayoutForFlash");

        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);

        boolean isFlashOn = CameraUtil.isCameraFlashOn();

        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(context, FlashWidget1x1Provider.class));
        for (int appWidgetId : appWidgetIds) {
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_flash_1x1);
            if (isFlashOn) views.setImageViewResource(R.id.ivFlash, R.drawable.bt_flash_on);
            else views.setImageViewResource(R.id.ivFlash, R.drawable.bt_flash_off);
            appWidgetManager.updateAppWidget(appWidgetId, views);
        }

        appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(context, FlashWidget2x1Provider.class));
        for (int appWidgetId : appWidgetIds) {
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_flash_2x1);
            if (isFlashOn) views.setImageViewResource(R.id.ivFlash, R.drawable.bt_flash_on);
            else views.setImageViewResource(R.id.ivFlash, R.drawable.bt_flash_off);
            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
    }


    public static boolean flashOnOff(Context context) {
        boolean isFlashOn = false;
        if (CameraUtil.checkCameraHardware(context)) {
            isFlashOn = CameraUtil.flashOnOff();
            updateWidgetLayoutForFlash(context);
            setNotification(context, isFlashOn);
            if (!isFlashOn) context.sendBroadcast(new Intent(Const.BR_FLASH_WIDGET_NOTIFICATION));
        }
        return isFlashOn;
    }

    public static void flashOff(Context context) {
        if (CameraUtil.checkCameraHardware(context)) {
            CameraUtil.destroyCameraInstance();
            updateWidgetLayoutForFlash(context);
        }
        setNotification(context, false);
    }

    /**
     * @param showFlag true : flash on
     * @author gue
     * @since 2013. 10. 17.
     */
    public static void setNotification(Context context, boolean showFlag) {
        NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (showFlag) {
            PendingIntent pIntent = PendingIntent.getBroadcast(context, 0, new Intent(Const.BR_FLASH_WIDGET_NOTIFICATION), PendingIntent.FLAG_ONE_SHOT);
            Notification noti;
            if (Build.VERSION.SDK_INT >= 11) {
                Notification.Builder builder = new Notification.Builder(context);
                builder.setContentIntent(pIntent);
                builder.setOngoing(true);
                builder.setAutoCancel(true);
                builder.setSmallIcon(R.mipmap.ic_launcher);
                builder.setTicker(context.getString(R.string.nt_flash_on));
                builder.setContentTitle(context.getString(R.string.nt_flash_title));
                builder.setContentText(context.getString(R.string.nt_flash_on));
                builder.setDefaults(Notification.DEFAULT_SOUND);
                if (Build.VERSION.SDK_INT >= 16) {
                    noti = builder.build();
                } else {
                    noti = builder.getNotification();
                }
            } else {
                noti = new Notification(R.mipmap.ic_launcher, context.getString(R.string.nt_flash_on), System.currentTimeMillis());
                noti.flags = Notification.FLAG_AUTO_CANCEL | Notification.FLAG_ONGOING_EVENT;
                try {
                    // noti.setLatestEventInfo(context, context.getString(R.string.nt_flash_title), context.getString(R.string.nt_flash_on), pIntent);
                    Method deprecatedMethod = noti.getClass().getMethod("setLatestEventInfo", Context.class, CharSequence.class, CharSequence.class, PendingIntent.class);
                    deprecatedMethod.invoke(noti, context, context.getString(R.string.nt_flash_title), context.getString(R.string.nt_flash_on), pIntent);
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            nm.notify(NOTIFICATION_ID, noti);
        } else {
            nm.cancel(NOTIFICATION_ID);
        }
    }
}