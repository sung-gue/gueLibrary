package com.breakout.sample.dto;

import android.os.Parcel;
import android.os.Parcelable;

import com.breakout.sample.dto.data.AppUpdateInfo;


public class InitDto extends BaseDto {

    public String termsOfUseUrl;

    public String privacyUrl;

    public AppUpdateInfo forceUpdate;

    public AppUpdateInfo lastUpdate;


    public InitDto() {
        super();
    }

    public InitDto(Parcel in) {
        super(in);
        termsOfUseUrl = in.readString();
        privacyUrl = in.readString();
        forceUpdate = in.readParcelable(AppUpdateInfo.class.getClassLoader());
        lastUpdate = in.readParcelable(AppUpdateInfo.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(termsOfUseUrl);
        dest.writeString(privacyUrl);
        dest.writeParcelable(forceUpdate, 0);
        dest.writeParcelable(lastUpdate, 0);
    }

    @Override
    protected ClassLoader getDataClassLoader() {
        return null;
    }

    @Override
    protected Parcelable.Creator<Object> getDataCreator() {
        return null;
    }

    public static final Parcelable.Creator<InitDto> CREATOR = new Parcelable.Creator<InitDto>() {
        @Override
        public InitDto createFromParcel(Parcel in) {
            return new InitDto(in);
        }

        @Override
        public InitDto[] newArray(int size) {
            return new InitDto[size];
        }
    };
}
