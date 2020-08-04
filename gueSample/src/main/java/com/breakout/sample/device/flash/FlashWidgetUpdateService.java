package com.breakout.sample.device.flash;

import com.breakout.sample.constant.Const;
import com.breakout.sample.R;
import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.widget.RemoteViews;

import com.breakout.util.device.CameraUtil;


/**
 * widget을 그리는 도중 시간 초과로 인한 ANR 방지를 위하여 service로 위젯의 RemoteViews를 설정
 * @author gue
 * @since 2013. 10. 4.
 * @copyright Copyright.2011.gue.All rights reserved.
 * @version 1.0
 * @history <ol>
 * 		<li>변경자/날짜 : 변경사항</li>
 * </ol>
 */
public class FlashWidgetUpdateService extends Service {
	
	@Override
	public void onCreate() {
		super.onCreate();
		registerReceiver(_receiver, new IntentFilter(Const.BR_FLASH_WIDGET_NOTIFICATION));
	}

	private BroadcastReceiver _receiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			FlashWidgetConfigure.flashOff(context);
			context.sendBroadcast(new Intent(Const.BR_FLASH_ACTIVITY));
		}
	};
	
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		
		boolean isFlashOn = CameraUtil.isCameraFlashOn();
		
		AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
		int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(this, FlashWidget2x1Provider.class));
		for (int appWidgetId : appWidgetIds) {
			appWidgetManager.updateAppWidget(appWidgetId, buildRemoteView2x1(this, isFlashOn));
		}
		
		appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(this, FlashWidget1x1Provider.class));
		for (int appWidgetId : appWidgetIds) {
			appWidgetManager.updateAppWidget(appWidgetId, buildRemoteView1x1(this, isFlashOn));
		}
		
		return super.onStartCommand(intent, flags, startId);
	}
	
	private RemoteViews buildRemoteView2x1(Context context, boolean isFlashOn) {
		RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_flash_2x1);
		
		Intent intent = new Intent(Const.WIDGET_2X1_CLICK_ACTION);
		intent.putExtra(Const.EX_FLASH_WIDGET_CLICK, Const.FLASH_WIDGET_FLASH_CLICK);
		PendingIntent pIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
		views.setOnClickPendingIntent(R.id.ivFlash, pIntent);
		
		intent = new Intent(Const.WIDGET_2X1_CLICK_ACTION);
		intent.putExtra(Const.EX_FLASH_WIDGET_CLICK, Const.FLASH_WIDGET_LINK_CLICK);
		pIntent = PendingIntent.getBroadcast(context, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT);
		views.setOnClickPendingIntent(R.id.ivLink, pIntent);
		
		if (isFlashOn) views.setImageViewResource(R.id.ivFlash, R.drawable.bt_flash_on);
		else views.setImageViewResource(R.id.ivFlash, R.drawable.bt_flash_off);
		
		return views;
	}
	
	private RemoteViews buildRemoteView1x1(Context context, boolean isFlashOn) {
		RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_flash_1x1);
		
		Intent intent = new Intent(Const.WIDGET_1X1_CLICK_ACTION);
		intent.putExtra(Const.EX_FLASH_WIDGET_CLICK, Const.FLASH_WIDGET_FLASH_CLICK);
		PendingIntent pIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
		views.setOnClickPendingIntent(R.id.ivFlash, pIntent);
		
		if (isFlashOn) views.setImageViewResource(R.id.ivFlash, R.drawable.bt_flash_on);
		else views.setImageViewResource(R.id.ivFlash, R.drawable.bt_flash_off);
		
		return views;
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	@Override
	public void onDestroy() {
		unregisterReceiver(_receiver);
		super.onDestroy();
	}
	

}
