package com.breakout.util.device.permission;

import android.content.pm.PackageManager;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.OnLifecycleEvent;

import com.breakout.util.Log;

import java.util.Map;


/**
 * https://developer.android.com/training/permissions/requesting
 *
 * @author sung-gue
 * @version 1.0 (2020-08-26)
 */
@SuppressWarnings("unused")
public class GrantPermission implements LifecycleObserver {
    private final String TAG = getClass().getSimpleName();

    public interface GrantPermissionsListener {
        void onSuccessGrantPermissions();

        void onCancelGrantPermissions();

        void onFailGrantPermissions(@NonNull Map<String, Boolean> result);

        void onFinishGrantPermissions();
    }

    private final AppCompatActivity _activity;
    private final Lifecycle _lifecycle;

    public GrantPermission(AppCompatActivity activity) {
        this._activity = activity;
        this._lifecycle = activity.getLifecycle();
        _lifecycle.addObserver(this);
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    private void onCreate(LifecycleOwner source) {
        Log.d(TAG, "lifecycle : onCreate");
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    private void onStart(LifecycleOwner source) {
        Log.d(TAG, "lifecycle : onStart");
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    private void onDestroy() {
        Log.d(TAG, "lifecycle : onDestroy");
        _lifecycle.removeObserver(this);
    }

    public boolean hasPermissions(String[] permissions) {
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(_activity, permission) == PackageManager.PERMISSION_DENIED) {
                return false;
            }
        }
        return true;
    }

    public void requestPermission(String permission, GrantPermissionsListener listener) {
        ActivityResultLauncher<String> launcher = _activity.registerForActivityResult(
                new ActivityResultContracts.RequestPermission(), result -> {
                    Log.d(TAG, String.format("onActivityResult: %s / %s", permission, result));
                    if (result) {
                        Log.d(TAG, "onActivityResult: PERMISSION GRANTED");
                    } else {
                        Log.d(TAG, "onActivityResult: PERMISSION DENIED");
                    }
                    onFinishGrantPermissions(listener);
                });
        launcher.launch(permission);
    }

    public void requestPermissions(String[] permissions, GrantPermissionsListener listener) {
        ActivityResultLauncher<String[]> launcher = _activity.registerForActivityResult(
                new ActivityResultContracts.RequestMultiplePermissions(), result -> {
                    boolean isGrantPermissions = true;
                    if (result.size() > 0) {
                        for (String key : result.keySet()) {
                            //noinspection ConstantConditions
                            boolean isGranted = result.get(key);
                            isGrantPermissions = isGrantPermissions && isGranted;
                            Log.d(TAG, String.format("onActivityResult: %s / %s", key, result.get(key)));
                        }
                        if (isGrantPermissions) {
                            onSuccessGrantPermissions(listener);
                        } else {
                            onFailGrantPermissions(listener, result);
                        }
                    } else {
                        onCancelGrantPermissions(listener);
                    }

                    onFinishGrantPermissions(listener);
                });
        launcher.launch(permissions);
    }

    private boolean isActiveLifeCycle(GrantPermissionsListener listener) {
        return _lifecycle != null
                && _lifecycle.getCurrentState() != Lifecycle.State.DESTROYED
                && listener != null;
    }

    private void onSuccessGrantPermissions(GrantPermissionsListener listener) {
        Log.d(TAG, "grant permissions success!");
        if (isActiveLifeCycle(listener)) listener.onSuccessGrantPermissions();
    }

    private void onCancelGrantPermissions(GrantPermissionsListener listener) {
        Log.d(TAG, "grant permissions cancel!");
        if (isActiveLifeCycle(listener)) listener.onCancelGrantPermissions();
    }

    private void onFailGrantPermissions(GrantPermissionsListener listener, @NonNull Map<String, Boolean> result) {
        Log.d(TAG, "grant permissions fail!");
        if (isActiveLifeCycle(listener)) listener.onFailGrantPermissions(result);
    }

    protected void onFinishGrantPermissions(GrantPermissionsListener listener) {
        Log.d(TAG, "grant permissions finish!");
        if (isActiveLifeCycle(listener)) listener.onFinishGrantPermissions();
    }

}