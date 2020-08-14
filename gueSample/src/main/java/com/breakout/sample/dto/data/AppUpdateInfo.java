package com.breakout.sample.dto.data;

import android.os.Parcel;
import android.os.Parcelable;

public class AppUpdateInfo implements Parcelable {

    /**
     * 고유 id
     */
    public int id;

    /**
     * 채널 - android, ios, web, etc
     */
    public String channel;

    /**
     * 강제 업데이트 여부 - false:일반, true:강제
     */
    public boolean isForce;

    /**
     * 앱 버전 [n.n.nn] ex) 1.0.0, 1.9.99
     */
    public String version;

    /**
     * 업데이트 제목
     */
    public String title;

    /**
     * 업데이트 내용
     */
    public String contents;

    /**
     * 업데이트 url
     */
    public String updateUrl;

    /**
     * 업데이트 상태 - 1:적용, 2:기록(업데이트 알림 없음), 3:정지, 4:삭제
     */
    public int state;

    public String createdDate;


    public AppUpdateInfo() {
    }

    public AppUpdateInfo(Parcel in) {
        id = in.readInt();
        channel = in.readString();
        isForce = in.readInt() == 0;
        version = in.readString();
        title = in.readString();
        contents = in.readString();
        updateUrl = in.readString();
        state = in.readInt();
        createdDate = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(channel);
        dest.writeInt(isForce ? 0 : 1);
        dest.writeString(version);
        dest.writeString(title);
        dest.writeString(contents);
        dest.writeString(updateUrl);
        dest.writeInt(state);
        dest.writeString(createdDate);
    }

    public static final Creator<AppUpdateInfo> CREATOR = new Creator<AppUpdateInfo>() {
        @Override
        public AppUpdateInfo createFromParcel(Parcel in) {
            return new AppUpdateInfo(in);
        }

        @Override
        public AppUpdateInfo[] newArray(int size) {
            return new AppUpdateInfo[size];
        }
    };
}