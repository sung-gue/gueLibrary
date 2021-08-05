package com.breakout.sample.test;

import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.breakout.sample.Log;

@SuppressWarnings("unused")
public class ControlScreenActivity extends AppCompatActivity {
    private final String TAG = getClass().getSimpleName();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        keepScreenOn();
        hideNavigationBar();
        hideSystemUI();
        lockTask();
        lockTask2();
    }

    private void keepScreenOn() {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    /*
        https://developer.android.com/training/system-ui/navigation?hl=ko
     */
    private void hideNavigationBar() {
        View decorView = getWindow().getDecorView();
        // Hide both the navigation bar and the status bar.
        // SYSTEM_UI_FLAG_FULLSCREEN is only available on Android 4.1 and higher, but as
        // a general rule, you should design your app to hide the status bar whenever you
        // hide the navigation bar.
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
    }

    private void hideSystemUI() {
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION;
        /*uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN;*/
        /*uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;*/
        /*uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;*/
        View decorView = getWindow().getDecorView();
//        decorView.setSystemUiVisibility(uiOptions);
        /*ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.show();
        }*/
        /*getWindow().getDecorView().setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                getWindow().getDecorView().setSystemUiVisibility(uiOptions);
            }
        });*/
        /*getWindow().getDecorView().setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
            @Override
            public void onSystemUiVisibilityChange(int visibility) {
                // Note that system bars will only be "visible" if none of the LOW_PROFILE, HIDE_NAVIGATION, or FULLSCREEN flags are set.
                if ((visibility & View.SYSTEM_UI_FLAG_HIDE_NAVIGATION) == 0 || (visibility & View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION) == 0) {
                    // TODO: The system bars are visible. Make any desired
                    // adjustments to your UI, such as showing the action bar or other navigational controls.
                    Log.w("test onSystemUiVisibilityChange 1");
                    hideSystemUI();
                } else {
                    // TODO: The system bars are NOT visible. Make any desired
                    // adjustments to your UI, such as hiding the action bar or other navigational controls.
                    Log.w("test onSystemUiVisibilityChange 2");
                    hideSystemUI();
                }
            }
        });*/
    }

    /**
     * https://developer.android.com/work/dpc/dedicated-devices/lock-task-mode
     */
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private void lockTask() {
        Log.d(TAG, "start lock task");
        startLockTask();
    }

    /**
     * https://developer.android.com/work/dpc/dedicated-devices/lock-task-mode
     */
    private void lockTask2() {
        // Allowlist two apps.
        final String KIOSK_PACKAGE = "com.example.kiosk";
        final String PLAYER_PACKAGE = "com.example.player";
        final String[] APP_PACKAGES = {KIOSK_PACKAGE, PLAYER_PACKAGE};

        Context context = getApplicationContext();
        DevicePolicyManager dpm = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
        ComponentName adminName = getComponentName();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            dpm.setLockTaskPackages(adminName, APP_PACKAGES);
        }

        /*
            Enable the Home and Overview buttons so that our custom launcher can respond
            using our custom activities. Implicitly disables all other features.
         */
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            dpm.setLockTaskFeatures(adminName, DevicePolicyManager.LOCK_TASK_FEATURE_NONE);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // First, confirm that this package is allowlisted to run in lock task mode.
            if (dpm.isLockTaskPermitted(context.getPackageName())) {
                startLockTask();
            } else {
                // Because the package isn't allowlisted, calling startLockTask() here
                // would put the activity into screen pinning mode.
            }
        }
    }
}
