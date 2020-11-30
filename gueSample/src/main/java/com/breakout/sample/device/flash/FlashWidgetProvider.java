package com.breakout.sample.device.flash;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;

import com.breakout.sample.Log;


/**
 * Flash Widget Provider
 *
 * @author sung-gue
 * @version 1.0 (2013. 10. 7.)
 */
public abstract class FlashWidgetProvider extends AppWidgetProvider {
    protected final String TAG = getClass().getSimpleName();

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        Log.d(TAG + " | onUpdate");
        super.onUpdate(context, appWidgetManager, appWidgetIds);

        context.startService(new Intent(context, FlashWidgetUpdateService.class));
    }

    @Override
    public void onEnabled(Context context) {
        Log.d(TAG + " | onEnabled");
        super.onEnabled(context);
    }

    @Override
    public void onDisabled(Context context) {
        Log.d(TAG + " | onDisabled");
        super.onDisabled(context);
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        Log.d(TAG + " | onDeleted");
        super.onDeleted(context, appWidgetIds);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG + " | onReceive");
        super.onReceive(context, intent);
    }

    /*
    @Override
    public void onAppWidgetOptionsChanged(Context context, AppWidgetManager appWidgetManager, int appWidgetId, Bundle newOptions) {
        Log.d(TAG, "");
        super.onAppWidgetOptionsChanged(context, appWidgetManager, appWidgetId, newOptions);
    }
    */
}