package com.breakout.util.dto.media;

import android.os.Parcel;
import android.os.Parcelable;
import android.provider.BaseColumns;
import android.provider.MediaStore.MediaColumns;

import java.io.Serializable;

/**
 * MediaColumns data
 * {@link MediaColumns} 에 존재하는 media column의 value를 저장하는 DTO
 *
 * @author sung-gue
 * @version 1.0 (2012. 11. 19.)
 */
public class MediaDTO implements Parcelable, Serializable {
    private static final long serialVersionUID = 9152143350048134652L;

    /**
     * {@link BaseColumns#_COUNT}<br>
     * type : int<br>
     * 레코드 개수
     */
    public String _count;
    /**
     * {@link BaseColumns#_ID}<br>
     * type : long<br>
     * 레코드의 pk
     */
    public String _id;
    /**
     * {@link MediaColumns#DATA}<br>
     * type : text<br>
     * 데이터 스트림. 파일의 경로
     */
    public String _data;
    /**
     * {@link MediaColumns#SIZE}<br>
     * type : long<br>
     * 파일 크기
     */
    public String _size;
    /**
     * {@link MediaColumns#DISPLAY_NAME}<br>
     * type : text<br>
     * 파일 표시명
     */
    public String _display_name;
    /**
     * {@link MediaColumns#MIME_TYPE}<br>
     * type : text<br>
     * 마임 타입
     */
    public String mime_type;
    /**
     * {@link MediaColumns#TITLE}<br>
     * type : text<br>
     * 제목
     */
    public String title;
    /**
     * {@link MediaColumns#DATE_ADDED}<br>
     * type : long<br>
     * 추가 날짜. 초단위
     */
    public String date_added;
    /**
     * {@link MediaColumns#DATE_MODIFIED}<br>
     * type : long<br>
     * 최후 갱신 날짜. 초단위
     */
    public String date_modified;

    /* ------------------------------------------------------------
        DESC: 특정 제조사의 정책에 의한 필드
     */
    /**
     * {@link MediaColumns#WIDTH}<br>
     * type : int<br>
     * image width
     * sdk version: 14(4.0) 이상<br>
     * 제조사: samsung<br>
     */
    public String width;
    /**
     * {@link MediaColumns#HEIGHT}<br>
     * type : int<br>
     * image height
     * sdk version: 14(4.0) 이상<br>
     * 제조사: samsung<br>
     */
    public String height;

    public MediaDTO() {
    }

    public MediaDTO(Parcel src) {
        _count = src.readString();
        _id = src.readString();
        _data = src.readString();
        _size = src.readString();
        _display_name = src.readString();
        mime_type = src.readString();
        title = src.readString();
        date_added = src.readString();
        date_modified = src.readString();
        width = src.readString();
        height = src.readString();
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(_count);
        dest.writeString(_id);
        dest.writeString(_data);
        dest.writeString(_size);
        dest.writeString(_display_name);
        dest.writeString(mime_type);
        dest.writeString(title);
        dest.writeString(date_added);
        dest.writeString(date_modified);
        dest.writeString(width);
        dest.writeString(height);
    }

    public static final Parcelable.Creator<MediaDTO> CREATOR = new Creator<MediaDTO>() {
        @Override
        public MediaDTO createFromParcel(Parcel src) {
            return new MediaDTO(src);
        }

        @Override
        public MediaDTO[] newArray(int size) {
            return new MediaDTO[size];
        }
    };
}