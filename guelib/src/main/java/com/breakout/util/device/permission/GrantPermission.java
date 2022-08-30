package com.breakout.util.device.permission;

import android.content.pm.PackageManager;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleEventObserver;
import androidx.lifecycle.LifecycleOwner;

import com.breakout.util.Log;

import java.util.Arrays;
import java.util.Map;


/**
 * https://developer.android.com/training/permissions/requesting
 *
 * @author sung-gue
 * @version 1.0 (2020-08-26)
 */
@SuppressWarnings("unused")
public class GrantPermission implements LifecycleEventObserver {
    private final String TAG = getClass().getSimpleName();

    public interface Listener {
        void onSuccessGrantPermissions();

        void onCancelGrantPermissions();

        void onFailGrantPermissions(@NonNull Map<String, Boolean> result);

        void onFinishGrantPermissions();
    }

    private final AppCompatActivity activity;
    private final Lifecycle lifecycle;
    private final Listener listener;

    private ActivityResultLauncher<String> requestPermissionLauncher;
    private ActivityResultLauncher<String[]> requestMultiplePermissionsLauncher;

    /**
     * @throws Exception must be init in Activity.onCreate()
     */
    public GrantPermission(AppCompatActivity activity, @NonNull Listener listener) throws Exception {
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
        requestPermissionLauncher = activity.registerForActivityResult(
                new ActivityResultContracts.RequestPermission(), isGranted -> {
                    Log.d(TAG, String.format("RequestPermission result: %s ", isGranted));
                    if (isGranted) {
                        onSuccessGrantPermissions();
                    } else {
                        onCancelGrantPermissions();
                    }
                    onFinishGrantPermissions();
                });
        requestMultiplePermissionsLauncher = activity.registerForActivityResult(
                new ActivityResultContracts.RequestMultiplePermissions(), result -> {
                    boolean isGrantPermissions = true;
                    if (result.size() > 0) {
                        Log.d(TAG, String.format("RequestMultiplePermissions result: %s ", result));
                        for (String key : result.keySet()) {
                            // Log.d(TAG, String.format("RequestMultiplePermissions result: %s / %s", key, result.get(key)));
                            isGrantPermissions = result.get(key);
                            if (!isGrantPermissions) break;
                        }
                        if (isGrantPermissions) {
                            onSuccessGrantPermissions();
                        } else {
                            onFailGrantPermissions(result);
                        }
                    } else {
                        onCancelGrantPermissions();
                    }

                    onFinishGrantPermissions();
                });
    }

    public void checkPermissions(String[] permissions) {
        if (hasPermissions(permissions)) {
            onSuccessGrantPermissions();
        } else {
            requestPermissions(permissions);
        }
    }

    public boolean hasPermissions(String[] permissions) {
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(
                    activity,
                    permission
            ) == PackageManager.PERMISSION_DENIED) {
                return false;
            }
        }
        return true;
    }

    public void requestPermission(String permission) {
        Log.d(TAG, String.format("RequestPermission : %s ", permission));
        requestPermissionLauncher.launch(permission);
    }

    public void requestPermissions(String[] permissions) {
        Log.d(TAG, String.format("RequestMultiplePermissions : %s ", Arrays.toString(permissions)));
        requestMultiplePermissionsLauncher.launch(permissions);
    }

    private boolean isActiveLifeCycle() {
        return lifecycle != null
               && lifecycle.getCurrentState() != Lifecycle.State.DESTROYED
               && listener != null;
    }

    private void onSuccessGrantPermissions() {
        Log.d(TAG, "grant permissions success!");
        if (isActiveLifeCycle()) listener.onSuccessGrantPermissions();
    }

    private void onCancelGrantPermissions() {
        Log.d(TAG, "grant permissions cancel!");
        if (isActiveLifeCycle()) listener.onCancelGrantPermissions();
    }

    private void onFailGrantPermissions(@NonNull Map<String, Boolean> result) {
        Log.d(TAG, "grant permissions fail!");
        if (isActiveLifeCycle()) listener.onFailGrantPermissions(result);
    }

    protected void onFinishGrantPermissions() {
        Log.d(TAG, "grant permissions finish!");
        if (isActiveLifeCycle()) listener.onFinishGrantPermissions();
    }

}