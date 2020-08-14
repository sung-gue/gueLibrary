package com.breakout.sample.controller;

import android.content.Context;
import android.os.Handler;

import com.breakout.sample.constant.Headers;
import com.breakout.sample.constant.Params;
import com.breakout.sample.constant.Values;
import com.breakout.sample.dto.UserDto;
import com.breakout.sample.util.GetAdidTask;
import com.breakout.util.net.HttpMethod;
import com.google.gson.Gson;

import java.util.HashMap;


public class AccountController extends ControllerEx<UserDto> {

    private static final ControllerType _nettype = ControllerType.Account;

    public AccountController(Context context, Handler handler) {
        super(context, handler, _nettype.getApiUrl());
    }

    public AccountController(Context context, Handler handler, boolean isDialogSkip) {
        this(context, handler);
        setErrorDialogSkip(isDialogSkip);
    }

    /**
     * POST /app/accounts/register
     */
    public void register(String ssoId) {
        super.setRequiredParam("/register");
        super.removeHeaderAfterNullCheck(Headers.SESSION);
        setHeaderAfterNullCheck("Content-type", "application/json");

        HashMap<String, Object> map = new HashMap<>();
        map.put(Params.ssoId, ssoId);
        map.put(Params.ssoType, Values.ssoType);
        map.put(Params.name, _shared.getGoogleAccountDisplayName());
        map.put(Params.email, _shared.getGoogleAccountEmail());

        String body = new Gson().toJson(map);
        setParamAfterNullCheck(Params.body, body);
        startRequest(HttpMethod.POST);
    }

    /**
     * POST /app/account/signin
     */
    public void signin() {
        super.setRequiredParam("/signin");
        super.removeHeaderAfterNullCheck(Headers.SESSION);
        setHeaderAfterNullCheck("Content-type", "application/json");

        new GetAdidTask(_context, new GetAdidTask.OnFinishGetAdidListener() {
            @Override
            public void OnFinishGetAdid(String adid) {
                HashMap<String, Object> map = new HashMap<>();
                map.put(Params.ssoId, adid);
                map.put(Params.ssoType, Values.ssoType);

                String body = new Gson().toJson(map);
                setParamAfterNullCheck(Params.body, body);
                startRequest(HttpMethod.POST);
            }
        }).execute();
    }

    @Override
    protected UserDto initObject() {
        return new UserDto();
    }

    @Override
    protected UserDto parsing(String responseStr) throws Exception {
        return _nettype.getParseObject(responseStr);
    }

    @Override
    protected void urlDecode(UserDto dto) {
        super.urlDecode(dto);
//        dto.welcomeMessage = urlDecoder(dto.welcomeMessage);
    }
}
