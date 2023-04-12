package com.breakout.sample.controller;

import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.provider.Settings;
import android.telephony.TelephonyManager;

import androidx.annotation.NonNull;

import com.breakout.sample.Log;
import com.breakout.sample.constant.Params;
import com.breakout.sample.dto.InitDto;
import com.breakout.sample.utils.GetAdidTask;
import com.breakout.util.net.HttpMethod;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.gson.Gson;

import java.util.HashMap;


public class InitController extends ControllerEx<InitDto> {

    private static final ControllerType _nettype = ControllerType.Init;

    public InitController(Context context, Handler handler) {
        super(context, handler, _nettype.getApiUrl());
    }

    public InitController(Context context, Handler handler, boolean isDialogSkip) {
        this(context, handler);
        setErrorDialogSkip(isDialogSkip);
    }

    /**
     * GET /app/init
     */
    public void init() {
        super.setRequiredParam("");
        new GetAdidTask(_context, new GetAdidTask.OnFinishGetAdidListener() {
            @Override
            public void OnFinishGetAdid(final String adid) {
                FirebaseInstanceId.getInstance()
                        .getInstanceId()
                        .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                            @Override
                            public void onComplete(@NonNull Task<InstanceIdResult> task) {
                                String token = null;
                                if (!task.isSuccessful()) {
                                    Log.w(TAG, "getInstanceId failed ", task.getException());
                                } else {
                                    try {
                                        // Get new Instance ID token
                                        token = task.getResult().getToken();
                                        Log.w(TAG, "getInstanceId success, fcmToken :" + token);
                                    } catch (Exception e) {
                                        Log.e(TAG, e.getMessage(), e);
                                    }
                                }

                                String androidId = null;
                                try {
                                    androidId = Settings.Secure.getString(
                                            _context.getContentResolver(),
                                            Settings.Secure.ANDROID_ID
                                    );
                                } catch (Exception e) {
                                    Log.e(TAG, e.getMessage(), e);
                                }

                                HashMap<String, String> deviceInfoMap = new HashMap<>();
                                try {
                                    deviceInfoMap.put("brand", Build.BRAND);
                                } catch (Exception e) {
                                    Log.e(TAG, e.getMessage(), e);
                                    deviceInfoMap.put("brand", "empty");
                                }
                                try {
                                    deviceInfoMap.put("model", Build.MODEL);
                                } catch (Exception e) {
                                    Log.e(TAG, e.getMessage(), e);
                                    deviceInfoMap.put("model", "empty");
                                }
                                try {
                                    deviceInfoMap.put("os_ver", Build.VERSION.RELEASE);
                                } catch (Exception e) {
                                    Log.e(TAG, e.getMessage(), e);
                                    deviceInfoMap.put("os_ver", "empty");
                                }
                                try {
                                    deviceInfoMap.put("os_ver_int", String.valueOf(Build.VERSION.SDK_INT));
                                } catch (Exception e) {
                                    Log.e(TAG, e.getMessage(), e);
                                    deviceInfoMap.put("os_ver_int", "empty");
                                }
                                try {
                                    TelephonyManager tm = (TelephonyManager) _context.getSystemService(Context.TELEPHONY_SERVICE);
                                    deviceInfoMap.put("operator", tm.getNetworkOperatorName());
                                } catch (Exception e) {
                                    Log.e(TAG, e.getMessage(), e);
                                    deviceInfoMap.put("operator", "empty");
                                }
                                deviceInfoMap.put("referrer1", _shared.getGoogleInstallReferrer());
                                deviceInfoMap.put("referrer2", _shared.getFacebookDeferredAppLinkData());
                                String deviceInfo = new Gson().toJson(deviceInfoMap);

                                setParamAfterNullCheck(Params.fcmToken, token);
                                setParamAfterNullCheck(Params.uuid1, adid);
                                setParamAfterNullCheck(Params.uuid2, androidId);
                                setParamAfterNullCheck(Params.deviceInfo, deviceInfo);

                                startRequest(HttpMethod.GET);
                            }
                        });
            }
        }).execute();
    }

    @Override
    protected InitDto initObject() {
        return new InitDto();
    }

    @Override
    protected InitDto parsing(String responseStr) throws Exception {
        return _nettype.getParseObject(responseStr);
    }

    @Override
    protected void urlDecode(InitDto dto) {
        super.urlDecode(dto);
//        dto.welcomeMessage = urlDecoder(dto.welcomeMessage);
    }
}
