/**
 * Copyright Google Inc. All Rights Reserved.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.breakout.sample.fcm;

import android.app.NotificationChannel;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.breakout.sample.R;
import com.breakout.util.string.StringUtil;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;


public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static MyFirebaseMessagingService _instance;

    private static final String TAG = MyFirebaseMessagingService.class.getSimpleName();

    public static class NotificationChannelData {
        public static NotificationChannelData instance;

        public final String id;
        public final String name;
        public final String description;
        public final int importance;
        public final boolean vibration;
        public final int lockscreenVisibility;

        public NotificationChannelData(Context context) {
            id = context.getString(R.string.notification_channel_01_id);
            name = context.getString(R.string.notification_channel_01_name);
            description = context.getString(R.string.notification_channel_01_desc);
            importance = NotificationManagerCompat.IMPORTANCE_HIGH;
            vibration = true;
            lockscreenVisibility = NotificationCompat.VISIBILITY_PRIVATE;
        }

        public static synchronized NotificationChannelData getInstance(Context context) {
            if (instance == null & context != null) {
                instance = new NotificationChannelData(context);
            }
            return instance;
        }
    }

    public static String initChannel(Context context) {
        String channelId = null;
        // NotificationChannels are required for Notifications on O (API 26) and above.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannelData channelData = NotificationChannelData.getInstance(context);
            channelId = channelData.id;

            NotificationChannel channel = new NotificationChannel(channelData.id, channelData.name, channelData.importance);
            channel.setDescription(channelData.description);
            channel.enableVibration(channelData.vibration);
            channel.setLockscreenVisibility(channelData.lockscreenVisibility);

//            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
            notificationManager.createNotificationChannel(channel);
        }
        return channelId;
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // [START_EXCLUDE]
        // There are two types of messages data messages and notification messages. Data messages are handled
        // here in onMessageReceived whether the app is in the foreground or background. Data messages are the type
        // traditionally used with GCM. Notification messages are only received here in onMessageReceived when the app
        // is in the foreground. When the app is in the background an automatically generated notification is displayed.
        // When the user taps on the notification they are returned to the app. Messages containing both notification
        // and data payloads are treated as notification messages. The Firebase console always sends notification
        // messages. For more see: https://firebase.google.com/docs/cloud-messaging/concept-options
        // [END_EXCLUDE]


        // Handle data payload of FCM messages.
        Log.d(TAG, "FCM Message Id: " + remoteMessage.getMessageId());


        // (developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.d(TAG, "FCM From: " + remoteMessage.getFrom());


        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "FCM Notification Title: " + remoteMessage.getNotification().getTitle());
            Log.d(TAG, "FCM Notification Body: " + remoteMessage.getNotification().getBody());
        }

        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See initNotification method below.

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "FCM Message data payload: " + remoteMessage.getData());
            initNotification(remoteMessage);
        }
    }

    private FCMData.Type getPushType(String type) {
        FCMData.Type[] types = FCMData.Type.values();
        FCMData.Type returnType = FCMData.Type.normal;
        for (FCMData.Type temp : types) {
            if (temp.name().equalsIgnoreCase(type)) {
                returnType = temp;
                break;
            }
        }
        return returnType;
    }

    /**
     * Create and show a simple notification containing the received FCM message.
     * {
     * title:         string       알림 제목
     * body:          string       알림 내용
     * subTitle:      string       android : 알림을 펼쳤을때의 알림 제목
     * subBody:       string       android : 알림을 펼쳤을때의 알림 내용
     * subImgUrl:     string       android : 알림을 펼쳤을때 노출될 이미지
     * pushSeq:       int          android : 알림에 고유번호를 부여하여 같은 번호의 알림일경우 최근것만 표시
     * pushType:      string       pushType 에 따라 알림을 클릭했을때의 행동 정의
     * pushAction:    string       pushType 에 따라 미리 정의된 값
     * userId:        string       fcm_token은 device에 유일하므로, 개인 알림의 경우 현재 로그인된 userId와 알림으로 받은 userId가 일치하지 않으면 알림 비노출
     * }
     *
     * @param remoteMessage FCM message body received.
     */
    private void initNotification(RemoteMessage remoteMessage) {
        SharedData shared = SharedData.getInstance(this);
        Map<String, String> remoteData = remoteMessage.getData();

        FCMData data = new FCMData();
        data.userId = remoteData.get(Params.userId);
        data.title = remoteData.get(Params.title);
        data.body = remoteData.get(Params.body);
        data.subTitle = remoteData.get(Params.subTitle);
        data.subBody = remoteData.get(Params.subBody);
        data.subImgUrl = remoteData.get(Params.subImgUrl);
        data.pushSeq = remoteData.get(Params.pushSeq);
        data.pushType = remoteData.get(Params.pushType);
        data.pushAction = remoteData.get(Params.pushAction);
        if (TextUtils.isEmpty(data.subTitle)) data.subTitle = data.title;
        if (TextUtils.isEmpty(data.subBody)) data.subBody = data.body;

        /*
            check member
         */
        int saveUserId = shared.getUserId();
        if (saveUserId > 0 && !TextUtils.isEmpty(data.userId) && !data.userId.equalsIgnoreCase(String.valueOf(saveUserId))) {
            Log.i(TAG, "initNotification | mismatch user");
            return;
        } else if (TextUtils.isEmpty(data.title) || TextUtils.isEmpty(data.body)) {
            Log.i(TAG, "initNotification | not exist base data");
            return;
        }

        /*
            check push type
         */
        data.type = getPushType(data.pushType);
        int pushRequestcode = data.type.requestCode;

        /*
            check noti seq
         */
        /*try {
            int savedNotiSeq = shared.getNotiSeq();
            int currentNotiSeq = Integer.parseInt(pushSeq);
            if (currentNotiSeq > savedNotiSeq) {
                Intent alarmIntent = new Intent(Navigation.ALARM_CHANGE);
                alarmIntent.putExtra(Navigation.ALARM_CHANGE, true);
                sendBroadcast(alarmIntent);
            }
        } catch (NumberFormatException e) {
            Log.e(TAG, e.getMessage());
        }*/

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        // check call_type
        if (FCMData.Type.browser == data.type) {
            try {
                intent.setData(Uri.parse(data.pushAction));
            } catch (Exception e) {
                Log.w(TAG, e.getMessage(), e);
                return;
            }
        } else {
            intent.setClass(this, IntroActivity.class);
            intent.putExtra(Extra.FCM_DATA, data);
        }

        PendingIntent pendingIntent = PendingIntent.getActivity(this, pushRequestcode /* Request code */, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationChannelData channelData = NotificationChannelData.getInstance(this);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, channelData.id)
                .setSmallIcon(R.mipmap.ic_launcher)
                //.setSmallIcon(R.drawable.ic_ticker)
                //.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher))
                //.setColor(Color.parseColor("#ffff9ea9"))
                //.setColor(ContextCompat.getColor(this, R.color.intro_background))
                //.setColor(Color.argb(255, 255, 255, 255))
                .setContentTitle(data.title)
                .setContentText(data.body)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent);

        Bitmap bitmap = getBitmapFromUrl(data.subImgUrl);
        if (bitmap != null) {
            NotificationCompat.BigPictureStyle style = new NotificationCompat.BigPictureStyle(notificationBuilder);
            style.setBigContentTitle(data.subTitle);
            style.setSummaryText(data.subBody);
            style.bigPicture(bitmap);
            notificationBuilder.setStyle(style);
        }

//        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(pushRequestcode /* ID of notification */, notificationBuilder.build());
    }

    public Bitmap getBitmapFromUrl(String imageUrl) {
        Bitmap bitmap = null;
        try {
            if (!StringUtil.nullCheckB(imageUrl)) {
                return bitmap;
            }
            URL url = new URL(imageUrl);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setDoInput(true);
            con.connect();
            InputStream is = con.getInputStream();
            bitmap = BitmapFactory.decodeStream(is);
            /*
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            bitmap = BitmapFactory.decodeStream(is, null, options);
            */
        } catch (IOException e) {
            Log.e(TAG, e.getMessage(), e);
        }
        Log.i(TAG, "getBitmapFromUrl | " + bitmap);
        return bitmap;
    }


    /**
     * Called if InstanceID token is updated. This may occur if the security of
     * the previous token had been compromised. Note that this is called when the InstanceID token
     * is initially generated so this is where you would retrieve the token.
     */
    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);
        Log.d(TAG, "onNewToken Refreshed token: " + token);

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        sendRegistrationToServer(token);

        //subscribeTopicAllNotice(true);
    }

    /**
     * Persist token to third-party servers.
     * Modify this method to associate the user's FCM InstanceID token with any server-side account
     * maintained by your application.
     *
     * @param token The new token.
     */
    private void sendRegistrationToServer(String token) {
        Log.d(TAG, "sendRegistrationToServer: " + token);
        /*
            TODO 2019-11-21 서버에 키 변경 알림 (Implement this method to send token to your app server.)
         */
    }


    private static final String TOPIC_ALL_NOTICE = "ALL_NOTICE";
    private static final String TOPIC_ONLINE_NOTICE = "ONLINE_NOTICE";
    private static final String TOPIC_OFFLINE_NOTICE = "OFFLINE_NOTICE";
    private static final String TOPIC_ANDROID_ALL_NOTICE = "ANDROID_ALL_NOTICE";
    private static final String TOPIC_ANDROID_ONLINE_NOTICE = "ANDROID_ONLINE_NOTICE";
    private static final String TOPIC_ANDROID_OFFLINE_NOTICE = "ANDROID_OFFLINE_NOTICE";

    /**
     * Subscribe to any FCM topics of interest, as defined by the TOPICS constant.
     */
    public static void subscribeTopicAllNotice(boolean toSubscribe) {
        Log.d(TAG, "subscribeTopicAllNotice: " + toSubscribe);
        // Once a token is generated, we subscribe to topic.
        if (toSubscribe) {
            FirebaseMessaging.getInstance().subscribeToTopic(TOPIC_ALL_NOTICE);
            FirebaseMessaging.getInstance().subscribeToTopic(TOPIC_ANDROID_ALL_NOTICE);
        } else {
            FirebaseMessaging.getInstance().unsubscribeFromTopic(TOPIC_ALL_NOTICE);
            FirebaseMessaging.getInstance().unsubscribeFromTopic(TOPIC_ANDROID_ALL_NOTICE);
        }
    }

    public static void subscribeTopicOnlineNotice(boolean toSubscribe) {
        Log.d(TAG, "subscribeTopicMemberNotice: " + toSubscribe);
        if (toSubscribe) {
            FirebaseMessaging.getInstance().subscribeToTopic(TOPIC_ONLINE_NOTICE);
            FirebaseMessaging.getInstance().subscribeToTopic(TOPIC_ANDROID_ONLINE_NOTICE);
        } else {
            FirebaseMessaging.getInstance().unsubscribeFromTopic(TOPIC_ONLINE_NOTICE);
            FirebaseMessaging.getInstance().unsubscribeFromTopic(TOPIC_ANDROID_ONLINE_NOTICE);
        }
    }

    public static void subscribeTopicOfflineNotice(boolean toSubscribe) {
        Log.d(TAG, "subscribeTopicNonmemberNotice: " + toSubscribe);
        if (toSubscribe) {
            FirebaseMessaging.getInstance().subscribeToTopic(TOPIC_OFFLINE_NOTICE);
            FirebaseMessaging.getInstance().subscribeToTopic(TOPIC_ANDROID_OFFLINE_NOTICE);
        } else {
            FirebaseMessaging.getInstance().unsubscribeFromTopic(TOPIC_OFFLINE_NOTICE);
            FirebaseMessaging.getInstance().unsubscribeFromTopic(TOPIC_ANDROID_OFFLINE_NOTICE);
        }
    }
}
