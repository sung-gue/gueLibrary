package com.breakout.sample.device.permission;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;


/**
 * https://developer.android.com/training/permissions/requesting
 */
public class GrantPermissionActivity1 extends AppCompatActivity {
    private final String TAG = getClass().getSimpleName();

    private final int PERMISSIONS_REQUEST_CODE = 1002;
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

        if (checkPermissions()) {
            nextWork();
        }
    }

    private void nextWork() {
        Log.d(TAG, "next work start!");
    }

    private boolean hasPermissions(String[] permissions) {
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_DENIED) {
                return false;
            }
        }
        return true;
    }

    private boolean checkPermissions() {
        String permission = PERMISSIONS[2];
        String[] permissions = new String[]{permission};
        boolean result = hasPermissions(permissions);
        if (!result) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {
                Log.d(TAG, String.format("shouldShowRequestPermissionRationale : %s", permission));
                // In an educational UI, explain to the user why your app requires this
                // permission for a specific feature to behave as expected. In this UI,
                // include a "cancel" or "no thanks" button that allows the user to
                // continue using your app without granting the permission.
            } else {
                // You can directly ask for the permission.
                Log.d(TAG, String.format("requestPermissions : %s", permission));
                ActivityCompat.requestPermissions(this, permissions, PERMISSIONS_REQUEST_CODE);
            }
        }
        return result;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        //noinspection SwitchStatementWithTooFewBranches
        switch (requestCode) {
            case PERMISSIONS_REQUEST_CODE:
                // If request is cancelled, the result arrays are empty.
                boolean isGrantPermissions = true;
                if (grantResults.length > 0) {
                    // Permission is granted. Continue the action or workflow
                    // in your app.
                    for (int grantResult : grantResults) {
                        isGrantPermissions = isGrantPermissions && grantResult == PackageManager.PERMISSION_GRANTED;
                    }
                    if (isGrantPermissions) {
                        onSuccessGrantPermissions();
                    } else {
                        onFailGrantPermissions(permissions, grantResults);
                    }
                } else {
                    // Explain to the user that the feature is unavailable because
                    // the features requires a permission that the user has denied.
                    // At the same time, respect the user's decision. Don't link to
                    // system settings in an effort to convince the user to change
                    // their decision.
                    onCancelGrantPermissions();
                }
                onFinishGrantPermissions();
                break;
        }
    }

    private void onSuccessGrantPermissions() {
        Log.d(TAG, "grant permissions success!");
        nextWork();
    }

    private void onCancelGrantPermissions() {
        Log.d(TAG, "grant permissions cancel!");
        showDialog("please give permission");
    }

    private void onFailGrantPermissions(@NonNull String[] permissions, @NonNull int[] grantResults) {
        String msg = "";
        for (int i = 0; i < permissions.length; i++) {
            if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                msg += "\n - " + permissions[i];
            }
        }
        Log.d(TAG, "grant permissions fail!" + msg);
        showDialog("please give permission\n" + msg);
    }

    private void showDialog(String msg) {
        new AlertDialog.Builder(this)
                .setTitle("warn")
                .setMessage(msg)
                .show();
    }

    protected void onFinishGrantPermissions() {
        Log.d(TAG, "grant permissions finish!");
    }

}