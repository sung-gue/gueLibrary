package com.breakout.util.dto.media;

import android.os.Parcel;
import android.os.Parcelable;
import android.provider.MediaStore.Images.ImageColumns;

/**
 * {@link ImageColumns} 에 존재하는 media column의 value를 저장하는 DTO
 *
 * @author sung-gue
 * @version 1.0 (2012. 11. 20.)
 */
public class ImageDTO extends MediaDTO {
    private static final long serialVersionUID = 2099139678051758918L;
    /**
     * {@link ImageColumns#DESCRIPTION}<br>
     * type : text<br>
     * 이미지에 대한 설명
     */
    public String description;
    /**
     * {@link ImageColumns#DESCRIPTION}<br>
     * type : text<br>
     * 피카사에서 매기는 id
     */
    public String picasa_id;
    /**
     * {@link ImageColumns#IS_PRIVATE}<br>
     * type : int<br>
     * 공개 여부
     */
    public String is_private;
    /**
     * {@link ImageColumns#LATITUDE}<br>
     * type : double<br>
     * 위도
     */
    public String latitude;
    /**
     * {@link ImageColumns#LONGITUDE}<br>
     * type : double<br>
     * 경도
     */
    public String longitude;
    /**
     * {@link ImageColumns#DATE_TAKEN}<br>
     * type : int<br>
     * 촬영날짜. 1/1000초 단위
     */
    public String datetaken;
    /**
     * {@link ImageColumns#ORIENTATION}<br>
     * type : int<br>
     * 사진의 방향. 0, 90, 180, 270
     */
    public String orientation;
    /**
     * {@link ImageColumns#MINI_THUMB_MAGIC}<br>
     * type : int<br>
     * 작은 썸네일
     */
    public String mini_thumb_magic;
    /**
     * {@link ImageColumns#BUCKET_ID}<br>
     * type : text<br>
     * 버킷 ID
     */
    public String bucket_id;
    /**
     * {@link ImageColumns#BUCKET_DISPLAY_NAME}<br>
     * type : text<br>
     * 버킷의 이름
     */
    public String bucket_display_name;

    /**
     * bucket의 image 수
     */
    public int bucket_count;

    /**
     * bucket의 대표 image id
     */
    public String bucket_delegate_image_id;

    /**
     * bucket의 대표 image path
     */
    public String bucket_delegate_image_data;

    public final static String[] IMAGE_COLUMN_LIST = new String[]{
            ImageColumns._ID,
            ImageColumns.DATA,
            ImageColumns.SIZE,
            ImageColumns.DISPLAY_NAME,
            ImageColumns.MIME_TYPE,
            ImageColumns.TITLE,
            ImageColumns.DATE_ADDED,
            ImageColumns.DATE_MODIFIED,
            ImageColumns.DESCRIPTION,
            ImageColumns.PICASA_ID,
            ImageColumns.IS_PRIVATE,
            ImageColumns.LATITUDE,
            ImageColumns.LONGITUDE,
            ImageColumns.DATE_TAKEN,
            ImageColumns.ORIENTATION,
            ImageColumns.MINI_THUMB_MAGIC,
            ImageColumns.BUCKET_ID,
            ImageColumns.BUCKET_DISPLAY_NAME,
            ImageColumns.WIDTH,
            ImageColumns.HEIGHT
    };


    public ImageDTO() {
    }

    public ImageDTO(Parcel src) {
        super(src);
        description = src.readString();
        picasa_id = src.readString();
        is_private = src.readString();
        latitude = src.readString();
        longitude = src.readString();
        datetaken = src.readString();
        orientation = src.readString();
        mini_thumb_magic = src.readString();
        bucket_id = src.readString();
        bucket_display_name = src.readString();
        bucket_count = src.readInt();
        bucket_delegate_image_id = src.readString();
        bucket_delegate_image_data = src.readString();
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(description);
        dest.writeString(picasa_id);
        dest.writeString(is_private);
        dest.writeString(latitude);
        dest.writeString(longitude);
        dest.writeString(datetaken);
        dest.writeString(orientation);
        dest.writeString(mini_thumb_magic);
        dest.writeString(bucket_id);
        dest.writeString(bucket_display_name);
        dest.writeInt(bucket_count);
        dest.writeString(bucket_delegate_image_id);
        dest.writeString(bucket_delegate_image_data);
    }

    public static final Parcelable.Creator<ImageDTO> CREATOR = new Creator<ImageDTO>() {
        @Override
        public ImageDTO createFromParcel(Parcel src) {
            return new ImageDTO(src);
        }

        @Override
        public ImageDTO[] newArray(int size) {
            return new ImageDTO[size];
        }
    };
}