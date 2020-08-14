package com.breakout.sample.controller;

import android.content.Context;
import android.os.Handler;

import com.breakout.sample.dto.NaverImageDto;
import com.breakout.util.net.HttpMethod;


public class NaverImageController extends ControllerEx<NaverImageDto> {

    private static final ControllerType _nettype = ControllerType.NaverImage;

    public NaverImageController(Context context, Handler handler) {
        super(context, handler, _nettype.getApiUrl());
    }

    public NaverImageController(Context context, Handler handler, boolean isDialogSkip) {
        this(context, handler);
        setErrorDialogSkip(isDialogSkip);
    }

    /**
     * GET https://openapi.naver.com/v1/search/image?query=자동차&display=4&start=1000
     */
    private void getImageList(String query, int display, int start, String sort) {
        super.setRequiredParam("");

        setHeaderAfterNullCheck("X-Naver-Client-Id", "sL_j3NvxHna3hCOlXUe3");
        setHeaderAfterNullCheck("X-Naver-Client-Secret", "TEvbs6b0e4");

        setParamAfterNullCheck("query", query);
        setParamAfterNullCheck("display", String.valueOf(display));
        setParamAfterNullCheck("start", String.valueOf(start));
        //정렬 옵션: sim (유사도순), date (날짜순)
        setParamAfterNullCheck("sort", sort);
        //사이즈 필터 옵션: all(전체), large(큰 사이즈), medium(중간 사이즈), small(작은 사이즈)
        setParamAfterNullCheck("filter", "all");

        startRequest(HttpMethod.GET);
    }

    public void getImageListSortBySim(String query, int display, int start) {
        getImageList(query, display, start, "sim");
    }

    public void getImageListSortByDate(String query, int display, int start) {
        getImageList(query, display, start, "date");
    }

    @Override
    protected NaverImageDto initObject() {
        return new NaverImageDto();
    }

    @Override
    protected NaverImageDto parsing(String responseStr) throws Exception {
        return _nettype.getParseObject(responseStr);
    }

    @Override
    protected void urlDecode(NaverImageDto dto) {
        super.urlDecode(dto);
//        dto.welcomeMessage = urlDecoder(dto.welcomeMessage);
    }
}
