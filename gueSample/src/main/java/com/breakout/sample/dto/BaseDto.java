package com.breakout.sample.dto;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;


/**
 * {@link Gson}을 사용하기 위한 DTO class이다.
 * intent에 의한 작업이 이루어질수 있도록 {@link Parcelable}을 구현한다.
 *
 * @author sung-gue
 * @version 1.0 (2019-11-07)
 */
public abstract class BaseDto<T extends Parcelable> implements Parcelable {

    /**
     * 호출 결과
     */
    @SerializedName("code")
    public String code;

    /**
     * 호출 메시지
     */
    @SerializedName("message")
    public String message;

    public T data;

    public ArrayList<T> datas;

    public int totalCount;

    /* ------------------------------------------------------------
        DESC: not json
     */
    public String response;


    public BaseDto() {
        datas = new ArrayList<>();
    }

    public BaseDto(Parcel in) {
        code = in.readString();
        message = in.readString();
        data = in.readParcelable(getDataClassLoader());
        datas = new ArrayList<>();
        in.readTypedList(datas, getDataCreator());
        totalCount = in.readInt();

        // not json
        response = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(code);
        dest.writeString(message);
        dest.writeParcelable(data, 0);
        dest.writeTypedList(datas);
        dest.writeInt(totalCount);

        // not json
        dest.writeString(response);
    }

    protected abstract ClassLoader getDataClassLoader();

    protected abstract Creator<T> getDataCreator();

    @Override
    public int describeContents() {
        return 0;
    }

}