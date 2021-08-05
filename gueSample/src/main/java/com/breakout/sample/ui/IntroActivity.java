package com.breakout.sample.ui;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Base64;
import android.view.Gravity;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;

import com.android.installreferrer.api.InstallReferrerClient;
import com.android.installreferrer.api.InstallReferrerStateListener;
import com.android.installreferrer.api.ReferrerDetails;
import com.breakout.sample.BaseActivity;
import com.breakout.sample.Log;
import com.breakout.sample.R;
import com.breakout.sample.constant.Const;
import com.breakout.sample.controller.InitController;
import com.breakout.sample.dto.InitDto;
import com.breakout.sample.dto.data.AppUpdateInfo;
import com.breakout.sample.fcm.MyFirebaseMessagingService;
import com.breakout.sample.util.GetAdidTask;
import com.breakout.sample.views.AppBar;
import com.breakout.util.device.DeviceUtil;
import com.breakout.util.storage.SharedStorage;
import com.breakout.util.widget.CustomDialog;
import com.facebook.applinks.AppLinkData;
import com.google.firebase.analytics.FirebaseAnalytics;

import java.security.MessageDigest;

public class IntroActivity extends BaseActivity {

    private InitDto _initdto;
    private AppLinkData _appLinkData;


    @Override
    protected void analyticsRecordScreen(FirebaseAnalytics firebaseAnalytics) {
        firebaseAnalytics.setCurrentScreen(this, "인트로", null);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.ui_base_layout);
        super.setBodyView(R.layout.v_intro);
        super.initUI();

        // get key hash value
        try {
            PackageInfo info = getPackageManager().getPackageInfo(getPackageName(), PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.i(true, TAG, ":::gkhv:::" + Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        }

        /*
            flash 영역 삭제
         */
        _shared.clear(SharedStorage.ClearMode.FLASH_CLEAR);

        // check google play service
        if (DeviceUtil.checkPlayServices(this)) {
            if (Const.TEST) {
                // 서버 및 앱 버전 정보
                try {
                    String toastStr = "Host : " + Uri.parse(Const.API_SERVER).getHost();
                    PackageInfo pi = getPackageManager().getPackageInfo(getPackageName(), 0);
                    toastStr += "\nv" + pi.versionName + ", code" + pi.versionCode;
                    Log.i(TAG, toastStr);
                    Toast t = Toast.makeText(this, toastStr, Toast.LENGTH_LONG);
                    t.setGravity(Gravity.BOTTOM, 0, 200);
                    t.show();
                } catch (Exception e) {
                    Log.e(TAG, e.getMessage(), e);
                }
            } else {
            }
            getInstallReferrer();
            new GetAdidTask(this, new GetAdidTask.OnFinishGetAdidListener() {
                @Override
                public void OnFinishGetAdid(String adid) {
                    _shared.setAndroidAdid(adid);
                    checkAlarmAgree();
                }
            }).execute();
        }
    }

    private void getInstallReferrer() {
        if (_shared.getIsCheckInstallReferrer()) {
            return;
        }
        _shared.setIsCheckInstallReferrer();
        /*
            facebook 지연된 딥 링크
            https://developers.facebook.com/docs/app-ads/deep-linking
         */
        AppLinkData.fetchDeferredAppLinkData(this,
                new AppLinkData.CompletionHandler() {
                    @Override
                    public void onDeferredAppLinkDataFetched(AppLinkData appLinkData) {
                        Log.i(TAG, "facebook appLinkData : " + appLinkData);
                        _appLinkData = appLinkData;
                        if (appLinkData != null && appLinkData.getTargetUri() != null) {
                            try {
                                _shared.setFacebookDeferredAppLinkData(appLinkData.getTargetUri().toString());
                            } catch (Exception e) {
                                Log.e(TAG, e.getMessage(), e);
                            }
                        }
                    }
                }
        );
        /*
            Google Play Install Referrer API
            https://developer.android.com/google/play/installreferrer/library
            adb shell am broadcast -a com.android.vending.INSTALL_REFERRER --es referrer "utm_source%3Dgoogle%26utm_medium%3Dcpc%26anid%3Dadmob"
            adb shell am broadcast -a com.android.vending.INSTALL_REFERRER -n {your package name}/{your package name}.InstallReferrerReceiver --es "referrer" "https%3A%2F%2Fline.me%2Fko%2F"
            adb shell am broadcast -a com.android.vending.INSTALL_REFERRER -n com.breakout.sample.dev/com.breakout.sample.dev.InstallReferrerReceiver --es "referrer" "https%3A%2F%2Fline.me%2Fko%2F"
            adb shell am broadcast -a com.android.vending.INSTALL_REFERRER -n com.breakout.sample.dev/com.breakout.sample.dev.InstallReferrerReceiver --es "referrer" "tbboom://intro?scheme_host=main&msg=60"
            adb shell am broadcast -a com.android.vending.INSTALL_REFERRER -n com.breakout.sample/com.breakout.sample.InstallReferrerReceiver --es "referrer" "bboom://intro?scheme_host=main&msg=733"
            https://play.google.com/store/apps/details?id=com.example.application&referrer=utm_source%3Dgoogle%26utm_medium%3Dcpc%26utm_term%3Drunning%26utm_content%3Da%26utm_campaign%3Dcode%26anid%3Dphunware%26aclid%3D[transaction_id]
         */
        final InstallReferrerClient referrerClient = InstallReferrerClient.newBuilder(this).build();
        referrerClient.startConnection(new InstallReferrerStateListener() {
            @Override
            public void onInstallReferrerSetupFinished(int responseCode) {
                switch (responseCode) {
                    case InstallReferrerClient.InstallReferrerResponse.OK:
                        // Connection established.
                        ReferrerDetails response = null;
                        try {
                            response = referrerClient.getInstallReferrer();
                            String referrerUrl = response.getInstallReferrer();
                            long referrerClickTime = response.getReferrerClickTimestampSeconds();
                            long appInstallTime = response.getInstallBeginTimestampSeconds();
                            boolean instantExperienceLaunched = response.getGooglePlayInstantParam();

                            Log.i(true, TAG, String.format(
                                    "google play onInstallReferrer SetupFinished " +
                                            "\n     referrerUrl : %s" +
                                            "\n     referrerClickTime : %s" +
                                            "\n     appInstallTime : %s" +
                                            "\n     instantExperienceLaunched : %s"
                                    , referrerUrl, referrerClickTime, appInstallTime, instantExperienceLaunched
                            ));
                            if (!TextUtils.isEmpty(referrerUrl)) {
                                _shared.setGoogleInstallReferrer(referrerUrl);
                            }

                            referrerClient.endConnection();
                        } catch (Exception e) {
                            Log.e(TAG, e.getMessage(), e);
                            return;
                        }
                        break;
                    case InstallReferrerClient.InstallReferrerResponse.FEATURE_NOT_SUPPORTED:
                        // API not available on the current Play Store app.
                        Log.i(true, TAG, "google play onInstallReferrer SetupFinished FEATURE_NOT_SUPPORTED");
                        break;
                    case InstallReferrerClient.InstallReferrerResponse.SERVICE_UNAVAILABLE:
                        // Connection couldn't be established.
                        Log.i(true, TAG, "google play onInstallReferrer SetupFinished SERVICE_UNAVAILABLE");
                        break;
                }
            }

            @Override
            public void onInstallReferrerServiceDisconnected() {
                // Try to restart the connection on the next request to
                // Google Play by calling the startConnection() method.
                Log.i(TAG, "google play onInstallReferrer ServiceDisconnected");
            }
        });
    }

    @Override
    protected void initTitle(AppBar appBar) {
    }

    @Override
    protected void initFooter() {
    }

    @Override
    protected void initBody() {
    }

    @Override
    protected void refreshUI() {
    }

    /**
     * 알람 동의
     */
    private void checkAlarmAgree() {
        if (_shared.getAgreeAppArarmYN() == null) {
            new CustomDialog(this)
                    .setContents(0, R.string.al_agree_alarm)
                    .setOkBt(R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            MyFirebaseMessagingService.subscribeTopicAllNotice(true);
                            MyFirebaseMessagingService.subscribeTopicOfflineNotice(true);
                            _shared.setAgreeAppArarmYN(Const.YES);
                            requestInitApp();
                        }
                    })
                    .setCancelBt(R.string.cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            requestInitApp();
                            _shared.setAgreeAppArarmYN(Const.NO);
                        }
                    })
                    .setCancel(false)
                    .show()
            ;
        } else {
            requestInitApp();
        }
    }

    /**
     * 앱 기본정보 분석 : 업데이트, ...
     */
    private void requestInitApp() {
        checkAppUpdate(null);
        if (true) return;
        /*
            TODO: 앱 기본정보 분석 구현
         */
        final Handler handler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(@NonNull Message msg) {
                closeProgress();
                _initdto = (InitDto) msg.obj;
                checkAppUpdate(_initdto);
                return true;
            }
        });
        showProgress();
        new InitController(this, handler).init();
    }

    /**
     * 앱 업데이트 체크
     */
    private void checkAppUpdate(final InitDto dto) {
        afterAppUpdateCheck();
        if (true) return;
        /*
            TODO: 앱 업데이트 구현
         */
        CustomDialog dialog = null;
        try {
            AppUpdateInfo appUpdateInfo = null;
            if (dto.forceUpdate != null) {
                appUpdateInfo = dto.forceUpdate;
            } else if (dto.lastUpdate != null) {
                appUpdateInfo = dto.lastUpdate;
            }
            if (appUpdateInfo != null) {
                final String updateUrl = appUpdateInfo.updateUrl;
                dialog = new CustomDialog(_context);
                dialog.setCancel(false);
                dialog.setOnShowListener(new DialogInterface.OnShowListener() {
                    @Override
                    public void onShow(DialogInterface dialog) {
                        ((AlertDialog) dialog).getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(_context, R.color.gray));
                    }
                });
                DialogInterface.OnClickListener updateListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(updateUrl)));
                        finish();
                    }
                };
                dialog.setContents(appUpdateInfo.title, appUpdateInfo.contents);
                dialog.setOkBt(R.string.update, updateListener);
                if (appUpdateInfo.isForce) {
                    if (TextUtils.isEmpty(appUpdateInfo.contents)) {
                        dialog.setContents(R.string.update_notify, R.string.al_update_force);
                    }
                    dialog.setCancelBt(R.string.exit, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    });
                } else {
                    if (TextUtils.isEmpty(appUpdateInfo.contents)) {
                        dialog.setContents(R.string.update_notify, R.string.al_update_normal);
                    }
                    dialog.setCancelBt(R.string.do_next, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            afterAppUpdateCheck();
                        }
                    });
                }
                dialog.show();
            }
        } catch (Exception e) {
            Log.e(TAG, "checkAppUpdate | " + e.getMessage(), e);
            dialog = null;
        }
        if (dialog == null) {
            afterAppUpdateCheck();
        }
    }

    /**
     * 로그인 정보를 기반으로 이후 행동 정의
     */
    private void afterAppUpdateCheck() {
        // 앱 진입시에 구글 사용자 토큰이 없다면 구글 로그인이 만료
        if (TextUtils.isEmpty(_shared.getUserSsoSerial())) {
            _shared.clearUserInfo();
            if (_shared.isLoginDialogShownToday()) {
                intentMain();
            } else {
                intentMain();
//                startLogin();
            }
        }
        // 세션 정보가 있다면 앱 자동 로그인 사용자
        else if (_shared.isLoginUser()) {
            intentMain();
        } else {
            requestRegist();
        }
    }

    private void startLogin() {
        showLoginDialog(false, false);
    }

    @Override
    protected void onLoginDialogCancel() {
        super.onLoginDialogCancel();
        intentMain();
    }

    @Override
    protected void onLoginFinsih(boolean isLogin) {
        super.onLoginFinsih(isLogin);
        intentMain();
    }

    private void intentMain() {
        showProgress();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!isFinishing() && _context != null) {
                    if (_appLinkData != null && _appLinkData.getTargetUri() != null) {
                        Intent intent = new Intent();
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.setData(_appLinkData.getTargetUri());
                        startActivity(intent);
                        finish();
                    } else {
                        Intent intent = new Intent();
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.setClass(_context, MainActivity.class);
                        startActivity(intent);
                        //overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                        finish();
                    }
                }
                closeProgress();
            }
        }, 1000);
    }
}
