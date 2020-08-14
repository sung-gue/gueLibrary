package com.breakout.sample.dto.data;

import android.os.Parcel;
import android.os.Parcelable;


public class User implements Parcelable {

    /**
     * 회원 고유 id
     */
    public int id;

    /**
     * 회원 여부 false: 비회원, true: 회원
     */
    public boolean isMember;

    /**
     * 회원 상태 코드 - 1:정상, 2:사용정지, 8:탈퇴요청, 9:탈퇴
     */
    public int state;

    public String name;

    public String createdDate;

    public String session;

    public boolean isFirstLoign;

    public User() {
    }

    public User(Parcel in) {
        id = in.readInt();
        isMember = in.readInt() == 0;
        state = in.readInt();
        name = in.readString();
        createdDate = in.readString();
        session = in.readString();
        isFirstLoign = in.readInt() == 0;

    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeInt(isMember ? 0 : 1);
        dest.writeInt(state);
        dest.writeString(name);
        dest.writeString(createdDate);
        dest.writeString(session);
        dest.writeInt(isFirstLoign ? 0 : 1);
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };
}