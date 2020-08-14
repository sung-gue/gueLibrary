package com.breakout.sample.dto.data;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class DummyData implements Parcelable {

    @SerializedName("seqId")
    public String id;

    @SerializedName("seqNum")
    public int num;

    public ArrayList<DummyData> list;


    /* ------------------------------------------------------------
        DESC: not json
     */
    public boolean isNotJson;

    public DummyData() {
        list = new ArrayList<>();
    }

    public DummyData(Parcel in) {
        id = in.readString();
        num = in.readInt();
        list = new ArrayList<>();
        in.readTypedList(list, DummyData.CREATOR);

        // not json
        isNotJson = in.readInt() == 0;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeInt(num);
        dest.writeTypedList(list);

        // not json
        dest.writeInt(isNotJson ? 0 : 1);
    }

    public static final Creator<DummyData> CREATOR = new Creator<DummyData>() {
        @Override
        public DummyData createFromParcel(Parcel in) {
            return new DummyData(in);
        }

        @Override
        public DummyData[] newArray(int size) {
            return new DummyData[size];
        }
    };
}
