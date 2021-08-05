package com.breakout.sample.storage.data;

import android.os.Parcel;
import android.os.Parcelable;
import android.provider.BaseColumns;
import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;

public class TrainTimeTable implements Parcelable {
    public static class Entry implements BaseColumns {
        public static final String TABLE_NAME = "TrainTimetable";

        public static final String id = "id";
        public static final String type = "type";
        public static final String trainNum = "trainNum";
        public static final String time = "time";
        public static final String currentStation = "currentStation";
        public static final String arrivalStation = "arrivalStation";

    }

    public enum Type {
        WEEKDAY_UP("평일상행"),
        WEEKDAY_DOWN("평일하행"),
        WEEKEND_UP("휴일상행"),
        WEEKEND_DOWN("휴일하행");

        public String name;

        Type(String name) {
            this.name = name;
        }

        public static Type getType(String name) {
            Type type = WEEKDAY_UP;
            for (Type temp : Type.values()) {
                if (TextUtils.equals(name, temp.name)) {
                    type = temp;
                    break;
                }
            }
            return type;
        }
    }

    @SerializedName("id")
    public int id;

    @SerializedName("type")
    public Type type;

    @SerializedName("trainNum")
    public String trainNum;

    @SerializedName("time")
    public String time;

    @SerializedName("currentStation")
    public String currentStation;

    @SerializedName("arrivalStation")
    public String arrivalStation;

    @SerializedName("position")
    public int position;

    @SerializedName("timeType")
    public String timeType;

//    public boolean isSelect = false;


    public TrainTimeTable() {
    }

    public TrainTimeTable(Parcel in) {
        id = in.readInt();
        type = Type.valueOf(in.readString());
        trainNum = in.readString();
        time = in.readString();
        currentStation = in.readString();
        arrivalStation = in.readString();
        position = in.readInt();
        timeType = in.readString();
//        isSelect = in.readInt() == 1;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(type.name());
        dest.writeString(trainNum);
        dest.writeString(time);
        dest.writeString(currentStation);
        dest.writeString(arrivalStation);
        dest.writeInt(position);
        dest.writeString(timeType);
//        dest.writeInt(isSelect ? 1 : 0);
    }

    public static final Creator<TrainTimeTable> CREATOR = new Creator<TrainTimeTable>() {
        @Override
        public TrainTimeTable createFromParcel(Parcel in) {
            return new TrainTimeTable(in);
        }

        @Override
        public TrainTimeTable[] newArray(int size) {
            return new TrainTimeTable[size];
        }
    };
}
