package com.breakout.sample.fcm;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class FCMData implements Parcelable {

    public enum Type {
        normal(0),
        browser(1),
        alarm(2);
        public int requestCode;

        Type(int requestCode) {
            this.requestCode = requestCode;
        }
    }

    public String title;
    public String body;
    public String subTitle;
    public String subBody;
    public String subImgUrl;
    public String pushSeq;
    public String pushType;
    public String pushAction;
    public String userId;

    public Type type;
    public boolean isBool;
    public ArrayList<FCMData> list;

    public FCMData() {
        list = new ArrayList<>();
    }

    public FCMData(Parcel src) {
        title = src.readString();
        body = src.readString();
        subTitle = src.readString();
        subBody = src.readString();
        subImgUrl = src.readString();
        pushSeq = src.readString();
        pushType = src.readString();
        pushAction = src.readString();
        userId = src.readString();

        type = Type.valueOf(src.readString());
        isBool = src.readInt() == 0;
        list = new ArrayList<>();
        src.readTypedList(list, FCMData.CREATOR);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(body);
        dest.writeString(subTitle);
        dest.writeString(subBody);
        dest.writeString(subImgUrl);
        dest.writeString(pushSeq);
        dest.writeString(pushType);
        dest.writeString(pushAction);
        dest.writeString(userId);

        dest.writeString(type.name());
        dest.writeInt(isBool ? 0 : 1);
        dest.writeTypedList(list);
    }

    public static final Creator<FCMData> CREATOR = new Creator<FCMData>() {
        @Override
        public FCMData createFromParcel(Parcel source) {
            return new FCMData(source);
        }

        @Override
        public FCMData[] newArray(int size) {
            return new FCMData[size];
        }
    };
}
