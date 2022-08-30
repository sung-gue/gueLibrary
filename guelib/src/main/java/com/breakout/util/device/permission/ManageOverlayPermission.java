package com.breakout.util.device.permission;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleEventObserver;
import androidx.lifecycle.LifecycleOwner;

import com.breakout.util.Log;


/**
 * 다른창 위에 표시 설정<br/>
 * 안드로이드 Q버전의 디바이스 시작 시 해당 설정이 등록되지 않으면 자동으로 앱 시작 불가
 * <p>
 * AndroidManifest.xml 권한 추가 필요<br/>
 * &lt;uses-permission android:name="android.permission.SYSTEM_ALERT_WINDOW">
 *
 * @author sung-gue
 * @version 1.0 (2020-08-28)
 */
public class ManageOverlayPermission implements LifecycleEventObserver {
    private final String TAG = getClass().getSimpleName();

    public interface Listener {
        void onSuccess();

        void onFail();
    }

    // private static int ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE = 2323;

    private final AppCompatActivity activity;
    private final Lifecycle lifecycle;
    private final Listener listener;
    private ActivityResultLauncher<Intent> launcher;

    public ManageOverlayPermission(AppCompatActivity activity, @NonNull Listener listener) throws Exception {
        this.activity = activity;
        this.lifecycle = activity.getLifecycle();
        this.listener = listener;
        lifecycle.addObserver(this);
        if (lifecycle.getCurrentState() != Lifecycle.State.INITIALIZED) {
            throw new Exception("must be init in onCreate()");
        }
        initPermissionLauncher();
    }

    @Override
    public void onStateChanged(@NonNull LifecycleOwner source, @NonNull Lifecycle.Event event) {
        if (event == Lifecycle.Event.ON_DESTROY) {
            lifecycle.removeObserver(this);
        }
    }

    private void initPermissionLauncher() {
        launcher = activity.registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (isNotRegist()) {
                        Log.d(TAG, "grant permissions fail!");
                        onFail();
                    } else {
                        // Permission Granted-System will work
                        Log.d(TAG, "grant permissions success!");
                        onSuccess();
                    }
                }
        );
    }

    public void requestPermission() {
        // Check if Android M or higher
        if (isNotRegist()) {
            // Show alert dialog to the user saying a separate permission is needed
            // Launch the settings activity if the user prefers
            Intent intent = new Intent(
                    Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:" + activity.getPackageName())
            );
            launcher.launch(intent);
        } else {
            Log.d(TAG, "no need to obtain permission");
            onSuccess();
        }
    }

    private boolean isNotRegist() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q && !Settings.canDrawOverlays(activity);
    }

    private boolean isActiveLifeCycle() {
        return lifecycle != null
               && lifecycle.getCurrentState() != Lifecycle.State.DESTROYED
               && listener != null;
    }

    private void onSuccess() {
        if (isActiveLifeCycle()) listener.onSuccess();
    }

    private void onFail() {
        if (isActiveLifeCycle()) listener.onFail();
    }

}
