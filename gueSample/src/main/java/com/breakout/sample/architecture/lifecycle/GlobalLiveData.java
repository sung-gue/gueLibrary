package com.breakout.sample.architecture.lifecycle;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.Transformations;

import com.breakout.sample.Log;

/**
 * <a href="https://developer.android.com/topic/libraries/architecture/livedata">
 * LiveData
 * </a>
 */
public class GlobalLiveData extends LiveData<String> {
    private final String TAG = getClass().getSimpleName();
    private static GlobalLiveData _instance;

    public static GlobalLiveData get() {
        if (_instance == null) _instance = new GlobalLiveData();
        return _instance;
    }

    private GlobalLiveData() {
        super();
    }


    public void update(String str) {
        setValue(str);
    }

    public LiveData<String> map = Transformations.map(this, input -> input + " - map");

    public LiveData<String> switchMap = Transformations.switchMap(
            this,
            input -> new MutableLiveData<>(input + " - switchMap")
    );

    private void testObserve(LifecycleOwner owner) {
        GlobalLiveData.get().map.observe(owner, str -> Log.i(TAG, "observe map | " + str));
        GlobalLiveData.get().switchMap.observe(owner, str -> Log.i(TAG, "observe switchMap | " + str));
    }

    @Override
    public void observe(@NonNull LifecycleOwner owner, @NonNull Observer<? super String> observer) {
        Log.d(
                TAG,
                String.format(
                        "observer regist | %s - %s",
                        owner.getClass().getSimpleName(),
                        owner.getLifecycle().getCurrentState()
                )
        );
        super.observe(owner, observer);
    }

    @Override
    public void observeForever(@NonNull Observer<? super String> observer) {
        super.observeForever(observer);
    }

    @Override
    protected void onActive() {
        super.onActive();
        Log.d(TAG, "onActive");
    }

    @Override
    protected void onInactive() {
        super.onInactive();
        Log.d(TAG, "onInActive");
    }
}
