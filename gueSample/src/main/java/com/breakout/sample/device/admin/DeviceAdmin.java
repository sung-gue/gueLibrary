package com.breakout.sample.device.admin;

import android.app.Activity;
import android.app.admin.DeviceAdminReceiver;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.UserManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.breakout.sample.Log;

import java.util.ArrayList;
import java.util.Arrays;


/**
 * https://developer.android.com/guide/topics/admin/device-admin
 * <p>
 * https://github.com/android/enterprise-samples/tree/main/DeviceOwner
 * <p>
 *
 * @author sung-gue
 * @version 1.0 (2020-12-18)
 */
/*
    AndroidManifest.xml
        <receiver
            android:name=".device.admin.DeviceAdmin"
            android:label="${app_name}"
            android:permission="android.permission.BIND_DEVICE_ADMIN">
            <meta-data
                android:name="android.app.device_admin"
                android:resource="@xml/device_admin" />

            <intent-filter>
                <action android:name="android.app.action.DEVICE_ADMIN_ENABLED" />
                <action android:name="android.app.action.DEVICE_ADMIN_DISABLE_REQUESTED" />
                <action android:name="android.app.action.DEVICE_ADMIN_DISABLED" />
            </intent-filter>
        </receiver>
 */
public class DeviceAdmin extends DeviceAdminReceiver {
    private final String TAG = getClass().getSimpleName();

    @Override
    public void onReceive(@NonNull Context context, @NonNull Intent intent) {
        super.onReceive(context, intent);
        Log.d(TAG, "onReceive: " + intent.getAction());
    }

    @Override
    public void onEnabled(@NonNull Context context, @NonNull Intent intent) {
        super.onEnabled(context, intent);
        showToast(context, "DeviceAdmin enabled");
    }

    @Nullable
    @Override
    public CharSequence onDisableRequested(@NonNull Context context, @NonNull Intent intent) {
        return super.onDisableRequested(context, intent);
    }

    @Override
    public void onDisabled(@NonNull Context context, @NonNull Intent intent) {
        super.onDisabled(context, intent);
        showToast(context, "DeviceAdmin disabled");
    }

    @Override
    public void onLockTaskModeEntering(@NonNull Context context, @NonNull Intent intent, @NonNull String pkg) {
        super.onLockTaskModeEntering(context, intent, pkg);
        Log.d(TAG, "onLockTaskModeEntering: " + pkg);
    }

    @Override
    public void onLockTaskModeExiting(@NonNull Context context, @NonNull Intent intent) {
        super.onLockTaskModeExiting(context, intent);
        Log.d(TAG, "onLockTaskModeExiting");
    }

    void showToast(Context context, String msg) {
        Log.d(TAG, msg);
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }

    public static class DeviceAdminUtil {
        private final String TAG = getClass().getSimpleName();
        public static final int REQUEST_CODE_ENABLE_ADMIN = 1001;
        private final Context _context;
        private final ComponentName _deviceAdmin;
        private final DevicePolicyManager _dpm;

        public DeviceAdminUtil(Context context) {
            _context = context;
            _deviceAdmin = new ComponentName(context.getApplicationContext(), DeviceAdmin.class);
            _dpm = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
        }

        public boolean isDeviceOwnerApp() {
            boolean isOwner = false;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                isOwner = _dpm.isDeviceOwnerApp(_context.getPackageName());
            }
            Log.d(TAG, "isDeviceOwnerApp: " + isOwner);
            return isOwner;
        }

        public boolean isActiveAdmin() {
            boolean isAdmin = _dpm.isAdminActive(_deviceAdmin);
            Log.d(TAG, "isAdminActive: " + isAdmin);
            return isAdmin;
        }

        public void enableAdmin(Activity activity, int requestCode) {
            if (!isActiveAdmin()) {
                // Launch the activity to have the user enable our admin.
                Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
                intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, _deviceAdmin);
                // intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, activity.getString(R.string.add_admin_extra_app_text));
                activity.startActivityForResult(intent, requestCode);
            }
        }

        public void disableAdmin(Context context) {
            if (isActiveAdmin()) {
                _dpm.removeActiveAdmin(_deviceAdmin);
            }
        }

        /**
         * https://developer.android.com/work/dpc/dedicated-devices/lock-task-mode#customize-ui
         */
        public void initCustomUI() {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                _dpm.setLockTaskFeatures(_deviceAdmin, DevicePolicyManager.LOCK_TASK_FEATURE_NONE);
            }
        }

        /**
         * 카메라 제한
         */
        public void controlCamera(boolean isDisabled) {
            _dpm.setCameraDisabled(_deviceAdmin, isDisabled);
        }

        /**
         * 사용자의 기능 제한
         */
        public void controlUserRestrictions(boolean isRestricted) {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                return;
            }
            ArrayList<String> userRestrictions = new ArrayList<>(
                    Arrays.asList(
                            UserManager.DISALLOW_SMS,
                            UserManager.DISALLOW_MOUNT_PHYSICAL_MEDIA,
                            UserManager.DISALLOW_USB_FILE_TRANSFER,
                            UserManager.DISALLOW_BLUETOOTH
                    )
            );
            for (String restriction : userRestrictions) {
                if (isRestricted) {
                    _dpm.addUserRestriction(_deviceAdmin, restriction);
                } else {
                    _dpm.clearUserRestriction(_deviceAdmin, restriction);
                }
            }
        }

        /**
         * 앱 사용 제한
         */
        public void controlPackagesSuspended(String[] packageNames, boolean suspended) {
            if (packageNames == null) {
                packageNames = new String[]{
                        "com.twitter.android", "com.facebook.katana", "com.google.android.apps.nbu.files"
                };
            }
            if (Build.VERSION.SDK_INT >= 24) {
                _dpm.setPackagesSuspended(_deviceAdmin, packageNames, suspended);
            }
        }
    }
}