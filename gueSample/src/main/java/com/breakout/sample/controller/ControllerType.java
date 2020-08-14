package com.breakout.sample.controller;

import com.breakout.sample.Log;
import com.breakout.sample.constant.Const;
import com.breakout.sample.dto.BaseDto;
import com.breakout.sample.dto.DummyDto;
import com.breakout.sample.dto.InitDto;
import com.breakout.sample.dto.NaverImageDto;
import com.breakout.sample.dto.UserDto;
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
 * @author gue
 * @version 1.0
 * @copyright Copyright.2016.breakout.All rights reserved.
 * @since 2016.02.29
 */
enum ControllerType {
    Init("/app/init",
            InitDto.class, ApiType.IN
    ),
    Account("/app/account",
            UserDto.class, ApiType.IN
    ),
    User("/app/user",
            UserDto.class, ApiType.IN
    ),
    NaverImage("https://openapi.naver.com/v1/search/image",
            NaverImageDto.class, ApiType.NAVER
    ),
    Dummy("/app/dummy",
            DummyDto.class, ApiType.IN
    );

    enum ApiType {IN, NAVER}

    final String _controller;
    final Class<?> _class;
    final ApiType _apiType;

    <E extends BaseDto<?>> ControllerType(String controller, Class<E> dto, ApiType apiType) {
        _controller = controller;
        _class = dto;
        _apiType = apiType;
    }

    public String getApiUrl() {
        switch (_apiType) {
            case IN:
            default:
                return Const.API_SERVER + _controller;
            case NAVER:
                return _controller;
        }
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
            log += String.format("\n-- %s decode\n%s", _controller, decodeStr);
        } else {
            log += String.format("\n-- %s decode\n%s", _controller, responseStr);
        }
        Log.d(TAG, log + "\n end resonse decode --");

        Gson gson = new Gson();
        T t = (T) _class.newInstance();
        try {
            Object obj = new JSONTokener(responseStr).nextValue();
            if (obj instanceof JSONObject) {
                t = (T) gson.fromJson(responseStr, _class);
            } else if (obj instanceof JSONArray) {
                Type type = TypeToken.getParameterized(ArrayList.class, _class).getType();
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
            log += String.format("\n-- %s parse\n%s", _controller, gson.toJson(t));
        } catch (Exception e) {
            log += String.format("\n-- %s parse\n  code : %s, msg : %s", _controller, t.code, t.message);
        }
        Log.d(TAG, log + "\n end gson parser --");
        return t;
    }
}