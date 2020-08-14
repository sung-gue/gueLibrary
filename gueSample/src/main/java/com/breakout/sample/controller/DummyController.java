package com.breakout.sample.controller;

import android.content.Context;
import android.os.Handler;

import com.breakout.sample.constant.Params;
import com.breakout.sample.constant.Values;
import com.breakout.sample.dto.DummyDto;
import com.breakout.util.net.HttpMethod;


public class DummyController extends ControllerEx<DummyDto> {

    private static final ControllerType _nettype = ControllerType.Dummy;

    public DummyController(Context context, Handler handler) {
        super(context, handler, _nettype.getApiUrl());
    }

    public DummyController(Context context, Handler handler, boolean isDialogSkip) {
        this(context, handler);
        setErrorDialogSkip(isDialogSkip);
    }

    /**
     * GET /app/dummy/test
     */
    public void dummy() {
        super.setRequiredParam("/test");

        setParamAfterNullCheck(Params.channel, Values.channel);

        startRequest(HttpMethod.GET);
        startMultpartRequest(HttpMethod.POST);
    }

    @Override
    protected DummyDto initObject() {
        return new DummyDto();
    }

    @Override
    protected DummyDto parsing(String responseStr) throws Exception {
        return _nettype.getParseObject(responseStr);
    }

    @Override
    protected void urlDecode(DummyDto dto) {
        super.urlDecode(dto);
//        dto.welcomeMessage = urlDecoder(dto.welcomeMessage);
    }
}
