package com.breakout.sample.ui;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.RemoteViews;

import com.breakout.sample.BaseActivity;
import com.breakout.sample.R;
import com.breakout.sample.views.AppBar;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class NotificationActivity extends BaseActivity {

    private LinearLayout _bodyView;
    private int notificationId = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        _bodyView = super.setEmptyContentView();
        _bodyView.setBackgroundColor(0x5500FFFF);

        super.initUI();
    }

    @Override
    protected void initTitle(AppBar appBar) {
    }

    @Override
    protected void initFooter() {
    }

    @Override
    protected void initBody() {
        Button(_bodyView, "Add Notification").setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("http://www.naver.com"));
                intent.addCategory(Intent.CATEGORY_BROWSABLE);
                PendingIntent pIntent = PendingIntent.getActivity(_appContext, 0, intent, PendingIntent.FLAG_ONE_SHOT);

                NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

                Notification noti;
                String tickerText = "가자~~";
                String contentTitle = "네이버";
                String contentText = "모바일 브라우저 이동";
                if (Build.VERSION.SDK_INT >= 11) {
                    Notification.Builder builder = new Notification.Builder(_context);
                    builder.setContentIntent(pIntent);
                    builder.setOngoing(true);
                    builder.setAutoCancel(true);
                    builder.setSmallIcon(R.mipmap.ic_launcher);
                    builder.setTicker(tickerText);
                    builder.setContentTitle(contentTitle);
                    builder.setContentText(contentText);
                    builder.setDefaults(Notification.DEFAULT_SOUND);
                    if (Build.VERSION.SDK_INT >= 16) {
                        noti = builder.build();
                    } else {
                        noti = builder.getNotification();
                    }
                } else {
                    noti = new Notification(R.mipmap.ic_launcher, tickerText, System.currentTimeMillis());
                    noti.defaults |= Notification.DEFAULT_SOUND;        // add sound
                    noti.flags = Notification.FLAG_AUTO_CANCEL;            // nm.cancel(id)로 삭제 하지 않아도 click할때 삭제
//	    			long[] vibrate = {100,100,200,300};					// 진동의 설정은 홀수 배열은 진동의 시간, 짝수 배열은 진동이 멈춰있는 시간
//	    			noti.vibrate = vibrate;								// add custom vibration
//	    			noti.defaults = Notification.DEFAULT_ALL;			// sound, vibration, lights 동작
                    try {
                        // noti.setLatestEventInfo(context, context.getString(R.string.nt_flash_title), context.getString(R.string.nt_flash_on), pIntent);
                        Method deprecatedMethod = noti.getClass().getMethod("setLatestEventInfo", Context.class, CharSequence.class, CharSequence.class, PendingIntent.class);
                        deprecatedMethod.invoke(noti, _context, contentTitle, contentTitle, pIntent);
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

                nm.notify(notificationId++, noti);
            }
        });

        Button(_bodyView, "Add Custom Notification").setOnClickListener(new OnClickListener() {
            @SuppressWarnings("deprecation")
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("http://www.naver.com"));
                intent.addCategory(Intent.CATEGORY_BROWSABLE);
                PendingIntent pIntent = PendingIntent.getActivity(_appContext, 0, intent, PendingIntent.FLAG_ONE_SHOT);

                NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

    			/*Notification noti1 = new Notification.Builder(_appContext)
					    	         .setContentTitle("New mail from " + sender.toString())
					    	         .setContentText(subject)
					    	         .setSmallIcon(R.drawable.new_mail)
					    	         .setLargeIcon(aBitmap)
					    	         .build();*/

                RemoteViews notiView = new RemoteViews(getPackageName(), R.layout.notification_custom);
                notiView.setImageViewResource(R.id.ivImage, R.drawable.noti_img);
                notiView.setTextViewText(R.id.tvText, "네이버로 갑니다~~.");

                Notification noti = new Notification(R.mipmap.ic_launcher, "상태바 메세지", System.currentTimeMillis());
                noti.defaults |= Notification.DEFAULT_SOUND;
                noti.flags = Notification.FLAG_AUTO_CANCEL;
                noti.contentView = notiView;
                noti.contentIntent = pIntent;

                nm.notify(notificationId++, noti);
            }
        });


        Button(_bodyView, "ic_noti_48_26").setOnClickListener(new OnClickListener() {
            @SuppressWarnings("deprecation")
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("http://www.naver.com"));
                intent.addCategory(Intent.CATEGORY_BROWSABLE);
                PendingIntent pIntent = PendingIntent.getActivity(_appContext, 0, intent, PendingIntent.FLAG_ONE_SHOT);

                NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                Notification noti;
                String tickerText = "가자~~";
                String contentTitle = "네이버";
                String contentText = "모바일 브라우저 이동";
                if (Build.VERSION.SDK_INT >= 11) {
                    Notification.Builder builder = new Notification.Builder(_context);
                    builder.setContentIntent(pIntent);
                    builder.setOngoing(true);
                    builder.setAutoCancel(true);
                    builder.setSmallIcon(R.drawable.ic_noti_48_26);
                    builder.setTicker(tickerText);
                    builder.setContentTitle(contentTitle);
                    builder.setContentText(contentText);
                    builder.setDefaults(Notification.DEFAULT_SOUND);
                    if (Build.VERSION.SDK_INT >= 16) {
                        noti = builder.build();
                    } else {
                        noti = builder.getNotification();
                    }
                } else {
                    noti = new Notification(R.drawable.ic_noti_48_26, tickerText, System.currentTimeMillis());
                    noti.defaults |= Notification.DEFAULT_SOUND;        // add sound
                    noti.flags = Notification.FLAG_AUTO_CANCEL;            // nm.cancel(id)로 삭제 하지 않아도 click할때 삭제
//	    			long[] vibrate = {100,100,200,300};					// 진동의 설정은 홀수 배열은 진동의 시간, 짝수 배열은 진동이 멈춰있는 시간
//	    			noti.vibrate = vibrate;								// add custom vibration
//	    			noti.defaults = Notification.DEFAULT_ALL;			// sound, vibration, lights 동작
                    try {
                        // noti.setLatestEventInfo(context, context.getString(R.string.nt_flash_title), context.getString(R.string.nt_flash_on), pIntent);
                        Method deprecatedMethod = noti.getClass().getMethod("setLatestEventInfo", Context.class, CharSequence.class, CharSequence.class, PendingIntent.class);
                        deprecatedMethod.invoke(noti, _context, contentTitle, contentTitle, pIntent);
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
                nm.notify(notificationId++, noti);
            }
        });

        Button(_bodyView, "ic_noti_48_30").setOnClickListener(new OnClickListener() {
            @SuppressWarnings("deprecation")
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("http://www.naver.com"));
                intent.addCategory(Intent.CATEGORY_BROWSABLE);
                PendingIntent pIntent = PendingIntent.getActivity(_appContext, 0, intent, PendingIntent.FLAG_ONE_SHOT);

                NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                Notification noti;
                String tickerText = "가자~ ic_noti_48_30";
                String contentTitle = "네이버";
                String contentText = "모바일 브라우저 이동";
                if (Build.VERSION.SDK_INT >= 11) {
                    Notification.Builder builder = new Notification.Builder(_context);
                    builder.setContentIntent(pIntent);
                    builder.setOngoing(true);
                    builder.setAutoCancel(true);
                    builder.setSmallIcon(R.drawable.ic_noti_48_30);
                    builder.setTicker(tickerText);
                    builder.setContentTitle(contentTitle);
                    builder.setContentText(contentText);
                    builder.setDefaults(Notification.DEFAULT_SOUND);
                    if (Build.VERSION.SDK_INT >= 16) {
                        noti = builder.build();
                    } else {
                        noti = builder.getNotification();
                    }
                } else {
                    noti = new Notification(R.drawable.ic_noti_48_30, tickerText, System.currentTimeMillis());
                    noti.defaults |= Notification.DEFAULT_SOUND;        // add sound
                    noti.flags = Notification.FLAG_AUTO_CANCEL;            // nm.cancel(id)로 삭제 하지 않아도 click할때 삭제
//	    			long[] vibrate = {100,100,200,300};					// 진동의 설정은 홀수 배열은 진동의 시간, 짝수 배열은 진동이 멈춰있는 시간
//	    			noti.vibrate = vibrate;								// add custom vibration
//	    			noti.defaults = Notification.DEFAULT_ALL;			// sound, vibration, lights 동작
                    try {
                        // noti.setLatestEventInfo(context, context.getString(R.string.nt_flash_title), context.getString(R.string.nt_flash_on), pIntent);
                        Method deprecatedMethod = noti.getClass().getMethod("setLatestEventInfo", Context.class, CharSequence.class, CharSequence.class, PendingIntent.class);
                        deprecatedMethod.invoke(noti, _context, contentTitle, contentTitle, pIntent);
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

                nm.notify(notificationId++, noti);
            }
        });

        Button(_bodyView, "ic_noti_48_38").setOnClickListener(new OnClickListener() {
            @SuppressWarnings("deprecation")
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("http://www.naver.com"));
                intent.addCategory(Intent.CATEGORY_BROWSABLE);
                PendingIntent pIntent = PendingIntent.getActivity(_appContext, 0, intent, PendingIntent.FLAG_ONE_SHOT);

                NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                Notification noti;
                String tickerText = "가자~ ic_noti_48_38";
                String contentTitle = "네이버";
                String contentText = "모바일 브라우저 이동";
                if (Build.VERSION.SDK_INT >= 11) {
                    Notification.Builder builder = new Notification.Builder(_context);
                    builder.setContentIntent(pIntent);
                    builder.setOngoing(true);
                    builder.setAutoCancel(true);
                    builder.setSmallIcon(R.drawable.ic_noti_48_38);
                    builder.setTicker(tickerText);
                    builder.setContentTitle(contentTitle);
                    builder.setContentText(contentText);
                    builder.setDefaults(Notification.DEFAULT_SOUND);
                    if (Build.VERSION.SDK_INT >= 16) {
                        noti = builder.build();
                    } else {
                        noti = builder.getNotification();
                    }
                } else {
                    noti = new Notification(R.drawable.ic_noti_48_38, tickerText, System.currentTimeMillis());
                    noti.defaults |= Notification.DEFAULT_SOUND;        // add sound
                    noti.flags = Notification.FLAG_AUTO_CANCEL;            // nm.cancel(id)로 삭제 하지 않아도 click할때 삭제
//	    			long[] vibrate = {100,100,200,300};					// 진동의 설정은 홀수 배열은 진동의 시간, 짝수 배열은 진동이 멈춰있는 시간
//	    			noti.vibrate = vibrate;								// add custom vibration
//	    			noti.defaults = Notification.DEFAULT_ALL;			// sound, vibration, lights 동작
                    try {
                        // noti.setLatestEventInfo(context, context.getString(R.string.nt_flash_title), context.getString(R.string.nt_flash_on), pIntent);
                        Method deprecatedMethod = noti.getClass().getMethod("setLatestEventInfo", Context.class, CharSequence.class, CharSequence.class, PendingIntent.class);
                        deprecatedMethod.invoke(noti, _context, contentTitle, contentTitle, pIntent);
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

                nm.notify(notificationId++, noti);
            }
        });

    }

    @Override
    protected void refreshUI() {
    }


}
