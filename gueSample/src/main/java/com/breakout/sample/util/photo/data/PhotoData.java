package com.breakout.sample.util.photo.data;

import android.media.ExifInterface;
import android.os.Parcel;
import android.os.Parcelable;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;


/**
 * 사진 업로드를 위해 필요한 intent extra class
 *
 * @author sung-gue
 * @version 1.0 (2013.11.11)
 */
public class PhotoData implements Parcelable {
    /**
     * 이미지 업로드 위치 결정
     */
    public enum UploadLocation {
        /**
         * 기본 설정
         */
        DEFAULT(1);

        /**
         * 이미지 선택 제한 수량
         */
        public int limitCnt;

        UploadLocation(int limitCnt) {
            this.limitCnt = limitCnt;
        }
    }

    /**
     * 업로드할 이미지 리스트
     */
    public ArrayList<String> imagePathList;

    /**
     * 이미지 업로드 위치 설정
     */
    public UploadLocation uploadLocation;

    /**
     * 이미지 선택 총수량 중 추가 선택 가능한 수량
     *
     * @see UploadLocation#limitCnt
     */
    public int remainSelectCnt = -1;


    public PhotoData() {
        imagePathList = new ArrayList<>();
    }

    public PhotoData(Parcel src) {
        imagePathList = new ArrayList<>();
        src.readStringList(imagePathList);
//        uploadLocation = (UploadLocation) src.readSerializable();
        uploadLocation = UploadLocation.valueOf(src.readString());
        remainSelectCnt = src.readInt();
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringList(imagePathList);
//        dest.writeSerializable(uploadLocation);
        dest.writeString(uploadLocation.name());
        dest.writeInt(remainSelectCnt);
    }

    public static final Creator<PhotoData> CREATOR = new Creator<PhotoData>() {
        @Override
        public PhotoData createFromParcel(Parcel src) {
            return new PhotoData(src);
        }

        @Override
        public PhotoData[] newArray(int size) {
            return new PhotoData[size];
        }
    };


    // TODO ExifInterface.TAG_DATETIME의 형태가 모두 "yyyy:MM:dd HH:mm:ss" 으로 정해져 있는지 확인 필요.

    /**
     * uploadPath에 촬영일을 설정하고, 해당 촬영일을 return <br>
     * 고정이 아닐경우 서버로 촬영일이 서로 약속된 "yyyyMMddHHmmss"의 형식으로 들어가지 않는 문제 생길 수 있음
     */
    public static String setPhotoCaptureTime(String originalPath, String uploadPath) {
        String strDate = null;
        try {
            ExifInterface exif = new ExifInterface(originalPath);
            strDate = exif.getAttribute(ExifInterface.TAG_DATETIME);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (strDate == null) {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy:MM:dd HH:mm:ss", Locale.getDefault());
            strDate = formatter.format(new Date());
        }
        strDate = strDate.trim().replace(":", "").replace(" ", "");

        try {
            ExifInterface exif = new ExifInterface(uploadPath);
            exif.setAttribute(ExifInterface.TAG_DATETIME, strDate);
            exif.saveAttributes();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return strDate;
    }

}