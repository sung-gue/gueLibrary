package com.breakout.util.net.controller;

import android.content.Context;

import com.breakout.util.net.BaseNet;
import com.breakout.util.net.HttpMethod;

import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;

import java.io.IOException;
import java.util.HashMap;


/**
 * {@link BaseController}에서 사용될 Thread를 상속받은 abstract class
 *
 * @author gue
 * @version 1.0
 * @copyright Copyright.2011.gue.All rights reserved.
 * @history <ol>
 * <li>변경자/날짜 : 변경사항</li>
 * </ol>
 * @since 2012. 12. 21.
 */
abstract class ControllerThread extends Thread {
    private Context _context;
    /**
     * enctype mode 에 따른 구분
     * <li>true : multipart/form-data</li>
     * <li>false : text/plain</li>
     */
    boolean _currentEnctype;

    protected ControllerThread(Context context) {
        _context = context;
    }

    /**
     * current thread start()<br>
     * enctype : text/plain<br>
     *
     * @author gue
     * @since 2012. 12. 21.
     */
    public void begin() {
        netCheckBeforStart();
    }

    /**
     * current thread start()<br>
     * enctype fix : multipart/form-data<br>
     *
     * @author gue
     * @since 2012. 12. 21.
     */
    public void beginMultpart() {
        _currentEnctype = true;
        netCheckBeforStart();
    }

    /**
     * network thred를 시작하기 전에 통신 가능 여부를 확인한다.<br/>
     * network을 사용할 수 없는 상태일때에는 {@link #connectFail(int)}를 호출하여 오류 및 재시도 처리
     *
     * @author gue
     * @since 2012. 12. 21.
     */
    void netCheckBeforStart() {
        if (BaseNet.getInstance().getNetState(_context) < 0) {
            connectFail(BaseController.EXCEPTION_NET_NOT_WAKE);
        } else {
            start();
        }
    }

    /**
     * network을 사용할 수 없는 상태일때 실행될 작업 작성
     *
     * @author gue
     * @see {@link BaseController#_currentNetState}
     * @since 2012. 12. 26.
     */
    abstract void connectFail(int currentNetState);

    /**
     * send request
     *
     * @param method           get, post, delete, put
     * @param sendUrl          target url
     * @param requestHeaderMap header map
     * @param requestMap       parameter map : stringBody for text/plain enctype
     * @param requestImageMap  parameter map : fileBody for multipart/form-data enctype
     * @return response string
     * @author gue
     * @since 2012. 12. 21.
     */
    String sendRequest(HttpMethod method, String sendUrl, HashMap<String, String> requestHeaderMap, HashMap<String, String> requestMap, HashMap<String, String> requestImageMap) throws ParseException, ClientProtocolException, IOException, Exception {
        String response = null;
        if (_currentEnctype) {
            switch (method) {
                case POST:
                case PUT:
                    response = BaseNet.getInstance().sendMultiPartRequest(method, sendUrl, requestHeaderMap, requestMap, requestImageMap);
                    break;
                case GET:
                case DELETE:
                default:
                    throw new Exception("multipart not support GET, DELETE ...");
            }
        } else {
            switch (method) {
                case GET:
                case POST:
                case PUT:
                case DELETE:
                    response = BaseNet.getInstance().sendRequest(method, sendUrl, requestHeaderMap, requestMap);
                    break;
                default:
                    break;
            }
        }
        return response;
    }

    /**
     * @see #sendRequest(HttpMethod, String, HashMap, HashMap, HashMap)
     */
    String sendRequest(HttpMethod method, String sendurl, HashMap<String, String> requestMap, HashMap<String, String> requestImageMap) throws ParseException, ClientProtocolException, IOException, Exception {
        return sendRequest(method, sendurl, null, requestMap, requestImageMap);
    }
}