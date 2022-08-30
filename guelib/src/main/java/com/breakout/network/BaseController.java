package com.breakout.network;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import com.breakout.network.BaseNet.HttpMethod;
import com.breakout.util.Log;
import com.breakout.util.widget.ViewUtil;

import java.io.File;
import java.io.IOException;
import java.net.SocketException;
import java.util.HashMap;

import cz.msebera.android.httpclient.ParseException;
import cz.msebera.android.httpclient.client.ClientProtocolException;


/**
 * <b>Network Controller</b><p>
 * 통신을 사용하기 위해상속받은 클래스에게 필요한 기능을 정의 하고 있는 추상클래스<br/>
 * <dl>
 * <dt>기능</dt>
 * <dd>
 * <li>통신 가능상황 체크 후 통신 불능 상황일 경우 알림창 팝업 제공</li>
 * <li>통신 실패 시 재시도 처리</li>
 * <li>통신시 필요한 parsing이나 공통 파라미터 입력 작업</li>
 * </dd>
 * <dt>사용</dt>
 * <dd>
 * <li>see : </li>
 * </dd>
 * </dl>
 * {@link #paramMap}을 저장하여 통신 실패시 재전송이 가능하게 구성되어 있으므로
 * 동시에 통신을 시도할 경우 {@link BaseController}의 새 인스턴스를 생성하여 작업하여야 한다.
 *
 * @author sung-gue
 * @version 1.0 (2020. 8. 17.)
 */
@SuppressWarnings({"UnusedReturnValue", "unused", "FieldCanBeLocal", "FieldMayBeFinal"})
public class BaseController implements Runnable {
    protected final String TAG = getClass().getSimpleName();

    public enum NetState {
        NET_SUCCESS(-1000, "success"),
        NOT_INIT_CONTROLLER(-9000, "controller not init"),
        /**
         * NetWork state가 3g,wifi 상태가 아님, NetWork에 연결할 수 없음
         */
        EXCEPTION_NET_NOT_WAKE(-9001, "network not wake"),
        EXCEPTION_PARSE(-9002, "ParseException"),
        EXCEPTION_CLIENT_PROTOCOL(-9003, "ClientProtocolException"),
        EXCEPTION_SOCKET(-9004, "SocketException"),
        EXCEPTION_IO(-9005, "IOException"),
        EXCEPTION(-9006, "Net Exception"),
        EXCEPTION_PARSER(-9007, "Parser Error"),
        EXCEPTION_NULL_RESPONSE(-9008, "Response is null"),
        ;

        public final int code;
        public final String msg;

        NetState(int code, String msg) {
            this.code = code;
            this.msg = msg;
        }

        public static NetState getStateByCode(int code) {
            NetState netState = NetState.NOT_INIT_CONTROLLER;
            for (NetState temp : NetState.values()) {
                if (temp.code == code) {
                    netState = temp;
                    break;
                }
            }
            return netState;
        }
    }

    public interface Listener<DTO> {
        /**
         * call in UI Thread
         *
         * @param okListener    call {@link Listener#onErrorDialogButtonOkClick(NetState)}
         * @param retryListener retry request
         * @return open dialog instance
         */
        Dialog createErrorDialog(
                String msg, String title,
                DialogInterface.OnClickListener okListener, DialogInterface.OnClickListener retryListener
        );

        /**
         * call in UI Thread
         */
        void onErrorDialogButtonOkClick(NetState netState);

        DTO parsing(String url, String responseStr) throws Exception;

        /**
         * call in UI Thread
         */
        void onCompleteRequest(Message msg);
    }

    /**
     * activity context<br/>
     * network check, open ErrorDialog<br/>
     */
    private final Context context;
    private Listener<?> listener;
    private final boolean isDebugMode;

    protected NetState currentNetState = NetState.NOT_INIT_CONTROLLER;
    protected String url;
    protected final HashMap<String, String> headerMap;
    protected String jsonBody = "";
    protected final HashMap<String, String> paramMap;
    protected final HashMap<String, String> fileParamMap;

    /**
     * "인터넷에 연결되어 있지 않습니다. wi-fi또는 3g의 연결상태를 확인해 주세요."
     */
    private String msgNetworkNotWake = "No internet connection. Please check the wi-fi or 3g connection status.\nPress OK to close the app.";
    /**
     * "접속이 원할 하지 않습니다. 잠시후에 다시 시도하여 주세요."
     */
    private String msgConnectionFail = "Connection is not smooth. Please try again after a while.\nClick OK to close the app.";
    private String btNameOk = "OK";
    private String btNameRetry = "RETRY";
    /**
     * 디바이스의 net state를 제대로 읽지 못할 경우 false 로 설정
     * <p>
     * ex) android-x86
     */
    private boolean isCheckNetState = true;

    /**
     * instance 생성
     */
    public BaseController(@NonNull Context context, @NonNull Listener<?> listener, boolean isDebugMode) {
        this.context = context;
        this.listener = listener;
        this.isDebugMode = isDebugMode;
        headerMap = new HashMap<>();
        paramMap = new HashMap<>();
        fileParamMap = new HashMap<>();
    }

    /**
     * turn off : network state check
     */
    public void setNetStateCheckOff() {
        this.isCheckNetState = false;
    }

    /**
     * ErrorDialog ui string setting
     *
     * @param msgNetworkNotWake dialog message, default : {@link #msgNetworkNotWake}
     * @param msgConnectionFail dialog message, default : {@link #msgConnectionFail}
     * @param btNameOk          dialog PositiveButton, default : {@link #btNameOk}
     * @param btNameRetry       dialog NegativeButton, default : {@link #btNameRetry}
     */
    public void setStringForDialog(String msgNetworkNotWake, String msgConnectionFail, String btNameOk, String btNameRetry) {
        this.msgNetworkNotWake = msgNetworkNotWake;
        this.msgConnectionFail = msgConnectionFail;
        setStringForDialog(btNameOk, btNameRetry);
    }

    /**
     * ErrorDialog ui string setting
     *
     * @see #setStringForDialog(String, String, String, String)
     */
    protected final void setStringForDialog(String btNameOk, String btNameRetry) {
        this.btNameOk = btNameOk;
        this.btNameRetry = btNameRetry;
    }


    /* ------------------------------------------------------------
        DESC: parameter check & clear
     */

    public BaseController addHeader(@NonNull String key, String value) {
        if (!TextUtils.isEmpty(value)) headerMap.put(key, value);
        return this;
    }

    public BaseController removeHeader(String key) {
        if (!TextUtils.isEmpty(key)) headerMap.remove(key);
        return this;
    }

    public BaseController setJsonBody(String body) {
        isJsonBody = true;
        jsonBody = body;
        return this;
    }

    public BaseController addParam(@NonNull String key, String value) {
        if (!TextUtils.isEmpty(value)) paramMap.put(key, value);
        return this;
    }

    public BaseController removeParam(String key) {
        if (!TextUtils.isEmpty(key)) paramMap.remove(key);
        return this;
    }

    public BaseController addFileParam(@NonNull String key, String path) {
        if (!TextUtils.isEmpty(path) && new File(path).exists()) fileParamMap.put(key, path);
        return this;
    }


    /* ------------------------------------------------------------
        DESC:   통신 오류처리와 재시도 처리
                버튼 : 확인, 취소, 재시도
     */

    /**
     *
     */
    private final Handler mainHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message msg) {
            NetState currentNetState = NetState.getStateByCode(msg.what);
            if (isSkipErrorDialog) {
                listener.onCompleteRequest(msg);
            } else {
                switch (currentNetState) {
                    case EXCEPTION_NET_NOT_WAKE:
                        openErrorDialog(msgNetworkNotWake, null);
                        break;
                    case NOT_INIT_CONTROLLER:
                    case EXCEPTION_PARSE:
                    case EXCEPTION_CLIENT_PROTOCOL:
                    case EXCEPTION_SOCKET:
                    case EXCEPTION_IO:
                    case EXCEPTION:
                    case EXCEPTION_PARSER:
                    case EXCEPTION_NULL_RESPONSE:
                        if (isDebugMode) {
                            openErrorDialog(currentNetState.msg, null);
                        } else {
                            openErrorDialog(msgConnectionFail, null);
                        }
                        break;
                    // TODO 확인하지 않은 에러일때 코드와 메세지를 그대로 보여준다.
                    default:
                        listener.onCompleteRequest(msg);
                        break;
                }
            }
            return true;
        }
    });


    /*
        INFO: ErrorDialog setting
     */

    /**
     * use custom {@link Listener}
     */
    private boolean isUseCustomErrorDialog = false;
    private boolean isSkipErrorDialog = false;
    /**
     * true : assign retry handling to ok button
     */
    private boolean isUseOkRetryButton;
    private boolean isUseRetryButton = true;

    /**
     * need to implement {@link Listener}
     */
    public void useCustomErrorDialog() {
        isUseCustomErrorDialog = true;
    }

    public void setErrorDialogSkip() {
        isSkipErrorDialog = true;
    }

    /**
     * ErrorDialog ok button listener
     */
    private final DialogInterface.OnClickListener okButtonListener
            = (dialog, which) -> listener.onErrorDialogButtonOkClick(currentNetState);

    /**
     * ErrorDialog retry button listener
     */
    private final DialogInterface.OnClickListener retryButtonListener
            = (dialog, which) -> netCheckBeforStart();

    /**
     * ErrorDialog open (retry request)
     */
    private void openErrorDialog(String msg, String title) {
        Dialog dialog;
        if (isUseCustomErrorDialog) {
            dialog = listener.createErrorDialog(msg, title, okButtonListener, retryButtonListener);
        } else {
            View view = ViewUtil.alertViewCenterAlign(msg, title, context);
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setView(view);
            if (isUseRetryButton) {
                builder.setPositiveButton(btNameOk, okButtonListener);
                builder.setNegativeButton(btNameRetry, retryButtonListener);
            } else {
                if (isUseOkRetryButton) builder.setPositiveButton(btNameOk, retryButtonListener);
                else builder.setPositiveButton(btNameOk, okButtonListener);
            }
            dialog = builder.create();
        }
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        try {
            dialog.show();
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        }
    }

    /*
        INFO: call request
     */
    private HttpMethod httpMethod = HttpMethod.POST;

    /**
     * true : multipart/form-data<br/>
     * flase : text/plain
     */
    private boolean isMultiPartType;

    private boolean isJsonBody;

    /**
     * current thread start()<br/>
     * enctype : text/plain<br/>
     */
    public void startRequest(HttpMethod method, String url) {
        this.url = url;
        httpMethod = method;
        netCheckBeforStart();
    }

    /**
     * current thread start()<br/>
     * enctype fix : multipart/form-data<br/>
     */
    public void startMultpartRequest(HttpMethod method, String url) {
        isMultiPartType = true;
        startRequest(method, url);
    }

    public void retryRequest() {
        netCheckBeforStart();
    }

    /**
     * network thred를 시작하기 전에 통신 가능 여부를 확인한다.<br/>
     * network을 사용할 수 없는 상태일때에는 {@link #connectFail(NetState)}를 호출하여 오류 및 재시도 처리
     */
    private void netCheckBeforStart() {
        currentNetState = NetState.NOT_INIT_CONTROLLER;
        if (isCheckNetState && !CheckNetwork.isConnected(context)) {
            connectFail(NetState.EXCEPTION_NET_NOT_WAKE);
        } else {
            new Thread(this).start();
        }
    }

    /**
     * network을 사용할 수 없는 상태일때 실행될 작업 작성
     */
    private void connectFail(NetState netState) {
        currentNetState = netState;
        mainHandler.sendEmptyMessage(netState.code);
    }

    @Override
    public void run() {
        // 1. request
        String responseStr = null;
        try {
            responseStr = sendRequest(httpMethod, url, headerMap, jsonBody, paramMap, fileParamMap);
        } catch (ParseException e) {
            currentNetState = NetState.EXCEPTION_PARSE;
            Log.e(TAG, String.format("request fail %s : %s", currentNetState.msg, e.getMessage()), e);
        } catch (ClientProtocolException e) {
            currentNetState = NetState.EXCEPTION_CLIENT_PROTOCOL;
            Log.e(TAG, String.format("request fail %s : %s", currentNetState.msg, e.getMessage()), e);
        } catch (SocketException e) {
            currentNetState = NetState.EXCEPTION_SOCKET;
            Log.e(TAG, String.format("request fail %s : %s", currentNetState.msg, e.getMessage()), e);
        } catch (IOException e) {
            currentNetState = NetState.EXCEPTION_IO;
            Log.e(TAG, String.format("request fail %s : %s", currentNetState.msg, e.getMessage()), e);
        } catch (Exception e) {
            currentNetState = NetState.EXCEPTION;
            Log.e(TAG, String.format("request fail %s : %s", currentNetState.msg, e.getMessage()), e);
        }

        // 2. parsing
        Object responseObject = null;
        /*
            TODO: add success > empty string
         */
        if (responseStr != null) {
            try {
                responseObject = listener.parsing(url, responseStr);
                currentNetState = NetState.NET_SUCCESS;
            } catch (Exception e) {
                Log.e(TAG, "parse fail : " + e.getMessage(), e);
                currentNetState = NetState.EXCEPTION_PARSER;
            }
        } else {
            currentNetState = NetState.EXCEPTION_NULL_RESPONSE;
        }

        // 3. network error & parser error check
        if (currentNetState.code <= NetState.NOT_INIT_CONTROLLER.code) {
            mainHandler.sendEmptyMessage(currentNetState.code);
        } else if (responseObject != null) {
            Message msg = mainHandler.obtainMessage();
            msg.what = currentNetState.code;
            msg.obj = responseObject;
            mainHandler.sendMessage(msg);
        }
    }

    /**
     * send request
     *
     * @param method           get, post, delete, put
     * @param sendUrl          target url
     * @param requestHeaderMap header map
     * @param requestMap       parameter map : stringBody for text/plain enctype
     * @param requestImageMap  parameter map : fileBody for multipart/form-data enctype
     * @return response string
     */
    private String sendRequest(
            HttpMethod method, String sendUrl,
            HashMap<String, String> requestHeaderMap,
            String requestBody, HashMap<String, String> requestMap,
            HashMap<String, String> requestImageMap) throws Exception {
        String response = null;
        if (isMultiPartType) {
            switch (method) {
                case POST:
                case PUT:
                    response = BaseNet.getInstance().sendMultiPartRequest(
                            method, sendUrl, requestHeaderMap, requestMap, requestImageMap
                    );
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
                    if (isJsonBody) {
                        response = BaseNet.getInstance().sendRequestJsonBody(
                                method, sendUrl, requestHeaderMap, requestBody
                        );
                    } else {
                        response = BaseNet.getInstance().sendRequest(method, sendUrl, requestHeaderMap, requestMap);
                    }
                    break;
                default:
                    break;
            }
        }
        return response;
    }
}