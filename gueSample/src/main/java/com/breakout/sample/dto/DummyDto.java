package com.breakout.sample.dto;

import android.os.Parcel;
import android.os.Parcelable;

import com.breakout.sample.dto.data.DummyData;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;


public class DummyDto extends BaseDto<DummyData> {

    @SerializedName("seqId")
    public String id;

    @SerializedName("seqNum")
    public int num;

    public ArrayList<DummyData> list;


    /* ------------------------------------------------------------
        DESC: not json
     */
    public boolean isNotJson;

    public DummyDto() {
        super();
        list = new ArrayList<>();
    }

    public DummyDto(Parcel in) {
        super(in);
        id = in.readString();
        num = in.readInt();
        list = new ArrayList<>();
        in.readTypedList(list, DummyData.CREATOR);

        // not json
        isNotJson = in.readInt() == 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(id);
        dest.writeInt(num);
        dest.writeTypedList(list);

        // not json
        dest.writeInt(isNotJson ? 0 : 1);
    }

    @Override
    protected ClassLoader getDataClassLoader() {
        return DummyData.class.getClassLoader();
    }

    @Override
    protected Parcelable.Creator<DummyData> getDataCreator() {
        return DummyData.CREATOR;
    }

    public static final Parcelable.Creator<DummyDto> CREATOR = new Parcelable.Creator<DummyDto>() {
        @Override
        public DummyDto createFromParcel(Parcel source) {
            return new DummyDto(source);
        }

        @Override
        public DummyDto[] newArray(int size) {
            return new DummyDto[size];
        }
    };
}
