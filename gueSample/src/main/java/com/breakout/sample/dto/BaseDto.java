package com.breakout.sample.dto;

import android.os.Parcel;
import android.os.Parcelable;

import com.breakout.network.EncodeUtil;
import com.breakout.sample.Log;
import com.breakout.sample.constant.Const;
import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.lang.reflect.Type;
import java.net.URLDecoder;
import java.util.ArrayList;


/**
 * {@link Gson}을 사용하기 위한 DTO class
 * <p>
 * intent data 를 위한 {@link Parcelable} 구현
 *
 * @author sung-gue
 * @version 1.0 (2014. 8. 17.)
 */
public abstract class BaseDto<DATA extends Parcelable> implements Parcelable {
    protected static final String TAG = BaseDto.class.getSimpleName();

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

    public DATA data;

    public ArrayList<DATA> datas;

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
    public int describeContents() {
        return 0;
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

    protected abstract Creator<DATA> getDataCreator();

    /*
        INFO: response parsing
     */
    public static class Parser<T extends BaseDto<?>> {
        private static final String TAG = "BaseDto.Parser";

        public Class<T> clazz;

        public Parser(Class<T> clazz) {
            this.clazz = clazz;
        }

        public T parsing(String url, String responseStr) throws Exception {
            T dto = BaseDto.getParseObject(clazz, responseStr, url);
            if (dto != null) {
                dto.urlDecode();
            }
            return dto;
        }

        public T getNewDtoInstance() {
            T instance = null;
            try {
                instance = clazz.newInstance();
            } catch (Exception e) {
                Log.e(TAG, String.format("new instance fail | %s", e.getMessage()));
            }
            return instance;
        }
    }

    public void urlDecode() {
        this.message = EncodeUtil.urlDecode(this.message);
    }

    public static <T extends BaseDto<?>> T getParseObject(Class<T> _class, String responseStr, String url) throws Exception {
        final String TAG = "BaseDto.parse";
        String log = "* start response decode ...";
        String decodeStr = null;
        try {
            decodeStr = URLDecoder.decode(responseStr, "UTF-8");
        } catch (Exception e) {
            Log.e(TAG, String.format("url decode fail | %s", e.getMessage()));
        }
        if (decodeStr != null) {
            log += String.format("\n-- %s decode\n%s", url, decodeStr);
        } else {
            log += String.format("\n-- %s decode\n%s", url, responseStr);
        }
        if (Const.IS_RESPONSE_PARSE_LOG) Log.d(TAG, log + "\n end resonse decode --");

        Gson gson = new Gson();
        T instance = _class.newInstance();
        try {
            Object obj = new JSONTokener(responseStr).nextValue();
            if (obj instanceof JSONObject) {
                instance = gson.fromJson(responseStr, _class);
            } else if (obj instanceof JSONArray) {
                Type type = TypeToken.getParameterized(ArrayList.class, _class).getType();
                // Type type = new TypeToken<ArrayList<T>>() {}.getType();
                instance.datas = gson.fromJson(responseStr, type);
                if (instance.datas == null) {
                    instance.datas = new ArrayList<>();
                }
            }
        } catch (Exception e) {
            Log.e(TAG, String.format("parse from response-str fail | %s", e.getMessage()));
        }
        if (instance == null) {
            instance = _class.newInstance();
        }
        instance.response = responseStr;
        log = "* gson toJson ... ";
        try {
            log += String.format("success | %s", url);
        } catch (Exception e) {
            log += String.format("fail | %s\n  code : %s, msg : %s", url, instance.code, instance.message);
        }
        if (Const.IS_RESPONSE_PARSE_LOG) Log.d(TAG, log);
        return instance;
    }

    protected String encryptAES(String value) {
        return EncodeUtil.encryptAES(value, Const.AES_KEY);
    }

    protected String decryptAES(String value) {
        return EncodeUtil.decryptAES(value, Const.AES_KEY);
    }

    protected String urlDecodeAndDecryptAES(String value) {
        return EncodeUtil.urlDecodeAndDecryptAES(value, Const.AES_KEY);
    }

}