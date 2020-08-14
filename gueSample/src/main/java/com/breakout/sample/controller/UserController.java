package com.breakout.sample.controller;

import android.content.Context;
import android.os.Handler;

import com.breakout.sample.dto.UserDto;


public class UserController extends ControllerEx<UserDto> {

    private static final ControllerType _nettype = ControllerType.User;

    public UserController(Context context, Handler handler) {
        super(context, handler, _nettype.getApiUrl());
    }

    public UserController(Context context, Handler handler, boolean isDialogSkip) {
        this(context, handler);
        setErrorDialogSkip(isDialogSkip);
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
