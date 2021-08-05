package com.breakout.sample.device.admin;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;


public class DeviceAdminActivity extends AppCompatActivity {
    private DeviceAdmin.DeviceAdminUtil _dau;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initDeviceAdminInstance();
        checkDeviceAdmin();
    }

    @Override
    protected void onResume() {
        super.onResume();
        processDeviceAdmin();
    }


    private void initDeviceAdminInstance() {
        _dau = new DeviceAdmin.DeviceAdminUtil(this);
    }

    private void checkDeviceAdmin() {
        if (!_dau.isActiveAdmin()) {
            _dau.enableAdmin(this, DeviceAdmin.DeviceAdminUtil.REQUEST_CODE_ENABLE_ADMIN);
        } else {
            _dau.controlCamera(true);
            _dau.controlUserRestrictions(true);
            _dau.controlPackagesSuspended(null, true);
            onFinishDeviceAdminInit();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case DeviceAdmin.DeviceAdminUtil.REQUEST_CODE_ENABLE_ADMIN:
                if (resultCode == RESULT_OK) {
                    onFinishDeviceAdminInit();
                } else {
                    Toast.makeText(this, "Allow Device Admin !!", Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;
        }
    }

    private void processDeviceAdmin() {
        if (_dau.isDeviceOwnerApp()) {
            _dau.initCustomUI();
        }
    }

    private void onFinishDeviceAdminInit() {
        Log.d(getClass().getName(), "finish device-admin init !");
    }
}