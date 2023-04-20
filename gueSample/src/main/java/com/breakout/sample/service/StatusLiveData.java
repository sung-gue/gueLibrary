package com.breakout.sample.service;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;

import com.breakout.sample.Log;
import com.google.gson.annotations.SerializedName;

public class StatusLiveData extends LiveData<StatusLiveData.Model> {
    private final String TAG = getClass().getSimpleName();
    private static StatusLiveData _instance;

    public static StatusLiveData get() {
        if (_instance == null) _instance = new StatusLiveData();
        return _instance;
    }

    private StatusLiveData() {
        super();
    }

    public void update(Model value) {
        setValue(value);
    }

    @Override
    public void observe(@NonNull LifecycleOwner owner, @NonNull Observer<? super Model> observer) {
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
    protected void onActive() {
        super.onActive();
        Log.d(TAG, "onActive");
    }

    @Override
    protected void onInactive() {
        super.onInactive();
        Log.d(TAG, "onInActive");
    }


    /**
     * streaming data
     * <p>
     * 상태 : TOPIC /topic/status/{androidId}
     * <pre>
     *      {
     *        "id": 1,
     *        "state": "A"
     *      }
     * </pre>
     */
    @SuppressWarnings("unused")
    public static class Model implements Parcelable {
        public static String TOPIC = "/topic/status/%s";

        public enum State {
            A, B, C, NONE,
        }

        @SerializedName("id")
        public int id;

        @SerializedName("state")
        public State state;

        public Model() {
        }

        public Model(Parcel in) {
            id = in.readInt();
            state = State.valueOf(in.readString());
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeInt(id);
            dest.writeString(state != null ? state.name() : null);
        }

        public static final Creator<Model> CREATOR = new Creator<Model>() {
            @Override
            public Model createFromParcel(Parcel in) {
                return new Model(in);
            }

            @Override
            public Model[] newArray(int size) {
                return new Model[size];
            }
        };
    }
}
