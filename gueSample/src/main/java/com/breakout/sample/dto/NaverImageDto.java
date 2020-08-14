package com.breakout.sample.dto;

import android.os.Parcel;
import android.os.Parcelable;

import com.breakout.sample.Log;
import com.google.gson.annotations.SerializedName;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;


public class NaverImageDto extends BaseDto<NaverImageDto.Item> {

    /**
     * datetime 검색 결과를 생성한 시간이다.
     */
    public String lastBuildDate;

    public Date lastUpdateDate;
    /**
     * integer 검색 결과 문서의 총 개수를 의미한다.
     */
    public int total;
    /**
     * integer 검색 결과 문서 중, 문서의 시작점을 의미한다.
     */
    public int start;
    /**
     * integer 검색된 검색 결과의 개수이다.
     */
    public int display;

    @SerializedName("items")
    public ArrayList<Item> list;

    /**
     * "Wed, 12 Aug 2020 17:00:34 +0900"
     */
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss Z", Locale.getDefault());

    public NaverImageDto() {
        super();
        list = new ArrayList<>();
    }

    public NaverImageDto(Parcel in) {
        super(in);
        lastBuildDate = in.readString();
        try {
            lastUpdateDate = simpleDateFormat.parse(lastBuildDate);
        } catch (ParseException e) {
            Log.e(e.getMessage(), e);
        }
        total = in.readInt();
        start = in.readInt();
        display = in.readInt();
        list = new ArrayList<>();
        in.readTypedList(list, Item.CREATOR);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        try {
            lastBuildDate = simpleDateFormat.format(lastUpdateDate);
        } catch (Exception e) {
            Log.e(e.getMessage(), e);
        }
        dest.writeString(lastBuildDate);
        dest.writeInt(total);
        dest.writeInt(start);
        dest.writeInt(display);
        dest.writeTypedList(list);
    }

    @Override
    protected ClassLoader getDataClassLoader() {
        return Item.class.getClassLoader();
    }

    @Override
    protected Creator<Item> getDataCreator() {
        return Item.CREATOR;
    }

    public static final Creator<NaverImageDto> CREATOR = new Creator<NaverImageDto>() {
        @Override
        public NaverImageDto createFromParcel(Parcel source) {
            return new NaverImageDto(source);
        }

        @Override
        public NaverImageDto[] newArray(int size) {
            return new NaverImageDto[size];
        }
    };

    public static class Item implements Parcelable {

        /**
         * 검색 결과 이미지의 제목을 나타낸다.
         */
        @SerializedName("title")
        public String title;
        /**
         * 검색 결과 이미지의 하이퍼텍스트 link를 나타낸다.
         */
        @SerializedName("link")
        public String imageUrl;
        /**
         * 검색 결과 이미지의 썸네일 link를 나타낸다.
         */
        @SerializedName("thumbnail")
        public String thumbnailUrl;
        /**
         * 검색 결과 이미지의 썸네일 높이를 나타낸다.
         */
        @SerializedName("sizeheight")
        public String height;
        /**
         * 검색 결과 이미지의 너비를 나타낸다. 단위는 pixel이다.
         */
        @SerializedName("sizewidth")
        public String width;


        public Item() {
        }

        public Item(Parcel in) {
            title = in.readString();
            imageUrl = in.readString();
            thumbnailUrl = in.readString();
            height = in.readString();
            width = in.readString();
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(title);
            dest.writeString(imageUrl);
            dest.writeString(thumbnailUrl);
            dest.writeString(height);
            dest.writeString(width);
        }

        public static final Creator<Item> CREATOR = new Creator<Item>() {
            @Override
            public Item createFromParcel(Parcel in) {
                return new Item(in);
            }

            @Override
            public Item[] newArray(int size) {
                return new Item[size];
            }
        };
    }
}
