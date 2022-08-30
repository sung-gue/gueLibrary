package com.breakout.sample.controller;

import com.breakout.sample.Log;
import com.breakout.sample.constant.Const;
import com.breakout.sample.dto.BaseDto;
import com.breakout.sample.dto.DummyDto;
import com.breakout.sample.dto.InitDto;
import com.breakout.sample.dto.NaverImageDto;
import com.breakout.sample.dto.UserDto;
import com.breakout.sample.openapi.openweathermap.WeatherDto;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.lang.reflect.Type;
import java.net.URLDecoder;
import java.util.ArrayList;


/**
 * Controller의 종류에 대한 enum class<br>
 * 서버의 url 정보와 reponse json string을 {@link Gson}library를 사용하여 Object로 return한다.
 *
 * @author sung-gue
 * @version 1.0 (2016.02.29)
 */
public enum ControllerType {
    Init("/app/init", InitDto.class),
    Account("/app/account", UserDto.class),
    User("/app/user", UserDto.class),
    NaverImage("https://openapi.naver.com/v1/search/image", NaverImageDto.class, false),
    Weather("https://api.openweathermap.org", WeatherDto.class, false),
    Dummy("/app/dummy", DummyDto.class);

    private final String apiUrl;
    private final Class<?> dtoClass;
    private final boolean isThirdParty;

    <E extends BaseDto<?>> ControllerType(String apiUrl, Class<E> dtoClass) {
        this(apiUrl, dtoClass, false);
    }

    <E extends BaseDto<?>> ControllerType(String apiUrl, Class<E> dtoClass, boolean isThirdParty) {
        this.apiUrl = apiUrl;
        this.dtoClass = dtoClass;
        this.isThirdParty = isThirdParty;
    }

    public String getApiUrl() {
        String url;
        if (isThirdParty) {
            url = apiUrl;
        } else {
            url = Const.API_SERVER + this.apiUrl;
        }
        return url;
    }

    @SuppressWarnings("unchecked")
    public <T extends BaseDto<?>> T getParseObject(String responseStr) throws Exception {
        final String TAG = "ControllerType";
        String log = "start response decode ...";
        String decodeStr = null;
        try {
            decodeStr = URLDecoder.decode(responseStr, "UTF-8");
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        }
        if (decodeStr != null) {
            log += String.format("\n-- %s decode\n%s", apiUrl, decodeStr);
        } else {
            log += String.format("\n-- %s decode fail\n%s", apiUrl, responseStr);
        }
        Log.d(TAG, log + "\n-- end resonse decode");

        Gson gson = new Gson();
        T t = (T) dtoClass.newInstance();
        try {
            Object obj = new JSONTokener(responseStr).nextValue();
            if (obj instanceof JSONObject) {
                t = (T) gson.fromJson(responseStr, dtoClass);
            } else if (obj instanceof JSONArray) {
                Type type = TypeToken.getParameterized(ArrayList.class, dtoClass).getType();
                //Type type = new TypeToken<ArrayList<T>>() {}.getType();
                t.datas = gson.fromJson(responseStr, type);
            } else {
                t.message = responseStr;
            }
            t.response = responseStr;
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
            t.message = responseStr;
            t.response = responseStr;
        }
        log = "start gson parser ...";
        try {
            log += String.format("\n-- %s parse\n%s", apiUrl, gson.toJson(t));
        } catch (Exception e) {
            log += String.format("\n-- %s parse fail\n  code : %s, msg : %s", apiUrl, t.code, t.message);
        }
        Log.d(TAG, log + "\n-- end gson parser");
        return t;
    }
}