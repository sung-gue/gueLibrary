package com.breakout.sample.device.permission;

import android.Manifest;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.breakout.util.device.permission.GrantPermission;

import java.util.Map;

public class GrantPermissionActivity extends AppCompatActivity {
    private final String TAG = getClass().getSimpleName();

    private GrantPermission _grantPermission;
    private final String[] PERMISSIONS = {
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initGrantPermission();
    }

    private void initGrantPermission() {
        try {
            _grantPermission = new GrantPermission(this, new GrantPermission.Listener() {
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
            });
        } catch (Exception ignored) {
        }
        checkPermissions();
    }

    private void checkPermissions() {
        _grantPermission.checkPermissions(PERMISSIONS);
    }

}