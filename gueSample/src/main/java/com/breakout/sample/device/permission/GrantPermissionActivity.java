package com.breakout.sample.device.permission;

import android.Manifest;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.breakout.util.device.permission.GrantPermission;

import java.util.Map;

public class GrantPermissionActivity extends AppCompatActivity implements GrantPermission.GrantPermissionsListener {
    private final String TAG = getClass().getSimpleName();

    private final String[] PERMISSIONS = {
            // <uses-permission android:name="android.permission.CAMERA" />
            Manifest.permission.CAMERA,
            // <uses-permission android:name="android.permission.RECORD_AUDIO" />
            Manifest.permission.RECORD_AUDIO,
            //<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        GrantPermission grantPermission = new GrantPermission(this);
        boolean isGranted = grantPermission.hasPermissions(PERMISSIONS);
        if (!isGranted) {
            grantPermission.requestPermissions(PERMISSIONS, this);
        }
    }

    @Override
    public void onSuccessGrantPermissions() {

    }

    @Override
    public void onCancelGrantPermissions() {

    }

    @Override
    public void onFailGrantPermissions(@NonNull Map<String, Boolean> result) {
        StringBuilder msg = new StringBuilder();
        for (String key : result.keySet()) {
            //noinspection ConstantConditions
            boolean isGranted = result.get(key);
            if (!isGranted) {
                msg.append("\n - ").append(key);
            }
        }
        Log.d(TAG, "grant permissions fail!" + msg);
    }

    @Override
    public void onFinishGrantPermissions() {

    }
}