package com.breakout.util.net;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;

import androidx.annotation.NonNull;

import com.breakout.util.CodeAction;
import com.breakout.util.Log;
import com.breakout.util.widget.ViewUtil;

import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.SocketException;
import java.net.URLDecoder;
import java.util.HashMap;


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
 * <li>see : {@link com.breakout.util.sample.BaseControllerEx}</li>
 * </dd>
 * </dl>
 * {@link #_requestMap}을 저장하여 통신 실패시 재전송이 가능하게 구성되어 있으므로
 * 동시에 통신을 시도할 경우 {@link BaseController}의 새 인스턴스를 생성하여 작업하여야 한다.
 *
 * @author sung-gue
 * @version 1.0 (2012. 12. 18.)
 */
public abstract class BaseController<T extends Object> implements Runnable {
    protected final String TAG = getClass().getSimpleName();

    public enum NetState {
        NET_SUCCESS(-1000),
        /**
         * {@link #_currentNetState}의 초기값 설정
         */
        NOT_INIT_CONTROLLER(-9000),
        /**
         * NetWork state가 3g,wifi 상태가 아님, NetWork에 연결할 수 없음
         */
        EXCEPTION_NET_NOT_WAKE(-9001),
        EXCEPTION_PARSE(-9002),
        EXCEPTION_CLIENT_PROTOCOL(-9003),
        EXCEPTION_SOCKET(-9004),
        EXCEPTION_IO(-9005),
        EXCEPTION(-9006),
        EXCEPTION_PARSER(-9007),
        EXCEPTION_NULL_RESPONSE(-9008),
        ;
        public int code;

        NetState(int code) {
            this.code = code;
        }
    }

    /**
     * activity context, network state check와 net exception에 대한 알림 작업을 하기 위해 필요.<br/>
     */
    protected Context _context;

    private HttpMethod _method = HttpMethod.POST;

    /**
     * network 처리 후 data를 담아 controller를 호출한 곳으로 돌려주기 위한 callback handler<br/>
     * {@link BaseController}의 Constructor에서 입력 받는다.
     */
    protected Handler _callBackHandler;
    /**
     * header
     */
    protected final HashMap<String, String> _requestHeaderMap;
    /**
     * parameter map : stringBody for text/plain enctype
     *
     * @see #_currentEnctype
     */
    protected final HashMap<String, String> _requestMap;
    /**
     * parameter map : fileBody for multipart/form-data enctype
     *
     * @see #_currentEnctype
     */
    protected final HashMap<String, String> _requestImageMap;

    protected NetState _netState = NetState.NOT_INIT_CONTROLLER;
    /**
     * {@link Message#what}에 저장하여 {@link #_callBackHandler}로 전달<p>
     * <ul>
     * <li>-1000 : {@link NetState#NET_SUCCESS}</li>
     * <li>-9000 : {@link NetState#NOT_INIT_CONTROLLER} controller not init초기값</li>
     * <li>-9001 : {@link NetState#EXCEPTION_NET_NOT_WAKE}</li>
     * <li>-9002 : {@link NetState#EXCEPTION_PARSE}</li>
     * <li>-9003 : {@link NetState#EXCEPTION_CLIENT_PROTOCOL}</li>
     * <li>-9004 : {@link NetState#EXCEPTION_SOCKET}</li>
     * <li>-9005 : {@link NetState#EXCEPTION_IO}</li>
     * <li>-9006 : {@link NetState#EXCEPTION}</li>
     * <li>-9007 : {@link NetState#EXCEPTION_PARSER}</li>
     * <li>-9008 : {@link NetState#EXCEPTION_NULL_RESPONSE}</li>
     * <li>-9009 : </li>
     * <li>-9010 : </li>
     * </ul>
     * TODO change name
     */
    protected int _currentNetState = NetState.NOT_INIT_CONTROLLER.code;
    /**
     * 디바이스의 net state를 제대로 읽지 못할 경우 false 로 설정
     * <p>
     * ex) android-x86
     */
    private boolean _isCheckNetState = true;
    /**
     * request url
     * TODO change name
     */
    protected String _sendUrl;
    /**
     * true일 경우 {@link #_handler}에 의해 생성되는 dialog가 {@link #_netState}에 정의된 exception을 직접 보여준다.
     */
    protected final boolean _isUseDebugAlert;
    private String _netNotWake = "인터넷에 연결되어 있지 않습니다. wi-fi또는 3g의 연결상태를 확인해 주세요.";
    private String _conFail = "접속이 원할 하지 않습니다. 잠시후에 다시 시도하여 주세요.";
    private String _ok = "확인";
    private String _retry = "재시도";
    /**
     * 기본 제공하는 알림창을 사용하지 않는 경우 true 설정
     */
    private boolean _isUseCustomDialog;

    /**
     * 생성자를 호출후에 see Also에 있는 method를 호출하여 기본 설정을 사용자에 맞게 변경한다.
     *
     * @param context         Activity context {@link #_context}
     * @param handler         {@link #_callBackHandler}
     * @param sendUrl         접속 url {@link #_sendUrl}
     * @param isUseDebugAlert {@link #_isUseDebugAlert}
     * @see #setStringForDialog(String, String, String, String)
     * @see #setStringForDialog(String, String)
     * @see #setUseCustomDialog(boolean)
     */
    protected BaseController(Context context, Handler handler, String sendUrl, boolean isUseDebugAlert) {
        this._sendUrl = sendUrl;
        this._callBackHandler = handler;
        this._context = context;
        this._isUseDebugAlert = isUseDebugAlert;
        _requestHeaderMap = new HashMap<>();
        _requestMap = new HashMap<>();
        _requestImageMap = new HashMap<>();
    }

    /**
     * @see #BaseController(Context, Handler, String, boolean)
     */
    protected BaseController(Context context, Handler handler, String sendUrl, boolean isUseDebugAlert, boolean isUseCustomDialog) {
        this(context, handler, sendUrl, isUseDebugAlert);
        this._isUseCustomDialog = isUseCustomDialog;
    }

    /**
     * 알림창 설정
     *
     * @param useCustomDialog 기본 제공하는 알림창을 사용하지 않는 경우 true 설정
     */
    protected final void setUseCustomDialog(boolean useCustomDialog) {
        _isUseCustomDialog = useCustomDialog;
    }

    /**
     * 네트워크 상태 체크 설정<br/>
     * ex) android-x86
     *
     * @param isCheckNetState 디바이스의 net state를 제대로 읽지 못할 경우 false 로 설정
     */
    protected final void setCheckNetState(boolean isCheckNetState) {
        _isCheckNetState = isCheckNetState;
    }

    /**
     * network exception로 인해 생성되는 dialog의 문자 설정
     *
     * @param netNotWake dialog message, 기본값 : "인터넷에 연결되어 있지 않습니다. wi-fi또는 3g의 연결상태를 확인해 주세요."
     * @param conFail    dialog message, 기본값 : "인터넷 연결이 원할 하지 않습니다. 잠시후에 다시 시도하여 주세요."
     * @param ok         dialog PositiveButton, 기본값 : "확인"
     * @param retry      dialog NegativeButton, 기본값 : "재시도"
     */
    protected final void setStringForDialog(String netNotWake, String conFail, String ok, String retry) {
        _netNotWake = netNotWake;
        _conFail = conFail;
        setStringForDialog(ok, retry);
    }

    /**
     * network exception의 dialog의 버튼에 삽입될 문자를 설정한다.
     *
     * @param ok    dialog PositiveButton, 기본값 : "확인"
     * @param retry dialog NegativeButton, 기본값 : "재시도"
     */
    protected final void setStringForDialog(String ok, String retry) {
        _ok = ok;
        _retry = retry;
    }


    /* ------------------------------------------------------------
        DESC: parameter check & clear
     */

    /**
     * 파라미터로 넘어온 value가 null이거나 ""인경우에는 {@link #_requestHeaderMap}에 입력하지 않는다.
     */
    protected final void setHeaderAfterNullCheck(String key, String value) {
        if (!TextUtils.isEmpty(value)) _requestHeaderMap.put(key, value);
    }

    /**
     * 파라미터로 넘어온 value가 null이거나 ""인경우에는 {@link #_requestHeaderMap}에 입력하지 않는다.
     */
    protected final void removeHeaderAfterNullCheck(String key) {
        if (!TextUtils.isEmpty(key)) _requestHeaderMap.remove(key);
    }

    /**
     * 파라미터로 넘어온 value가 null이거나 ""인경우에는 {@link #_requestMap}에 입력하지 않는다.
     */
    protected final void setParamAfterNullCheck(String param, String value) {
        if (!TextUtils.isEmpty(value)) _requestMap.put(param, value);
    }

    /**
     * 파라미터로 넘어온 path에 null이거나 파일이 존재하지 않는 경우에는 {@link #_requestImageMap}에 입력하지 않는다.
     */
    protected final void setParamAfterFileCheck(String param, String path) {
        if (path != null && new File(path).exists()) _requestImageMap.put(param, path);
    }

    /**
     * {@link #_requestMap},{@link #_requestImageMap}을 clear()하여 주고 {@link #_currentNetState}을 초기화 하여준다.<br/>
     */
    protected final void clearRequestMap() {
        _currentNetState = NetState.NOT_INIT_CONTROLLER.code;
        _netState = NetState.NOT_INIT_CONTROLLER;
        _requestHeaderMap.clear();
        _requestMap.clear();
        _requestImageMap.clear();
    }

    /**
     * 통신에 필요한 파리미터중에 공통적으로 사용되는 파라미터를 {@link #_requestMap}에 입력한다.<br/>
     * 새 인스턴스를 만들지 않고 사용할때는 이전에 전송된 parameter를 담고 있는 {@link #_requestMap}, {@link #_requestImageMap}의 내용을
     * {@link #clearRequestMap()}을 사용하여 초기화 하여주어야 한다.
     *
     * @param values parameter
     */
    protected abstract void setRequiredParam(String... values);


    /* ------------------------------------------------------------
        DESC:   통신 오류처리와 재시도 처리
                버튼 : 확인, 취소, 재시도
     */
    /**
     * true이면 network error시에 생성되는 알림창을 표시하지 않는다.
     */
    private boolean _isErrorDialogSkip;

    /**
     * @param flag {@link #_isErrorDialogSkip}의 값을 설정, true이면 network error시에 자동으로 생성되는 알림창을 표시하지 않는다.
     */
    protected void setErrorDialogSkip(boolean flag) {
        _isErrorDialogSkip = flag;
    }

    /**
     * false일 경우 재시도 버튼 사용안함
     */
    private boolean _isUseRetryButton;

    /**
     * @param flag {@link #_isUseRetryButton}의 값을 설정, false일 경우 재시도에 버튼을 별도로 사용하지 않음
     */
    protected void setUseRetryButton(boolean flag) {
        _isUseRetryButton = flag;
    }

    /**
     * 재시도에 관련된 처리를 숨기고 해당 재시도를 하기 위한 flag<br/>
     * 재시도에 대한 처리를 재시도 버튼 없이 하나의 버튼으로 갈때 AlertDialog에서 'ok'버튼에 재시도 처리를 걸어주고자 할때 true로 변경한다.
     */
    private boolean _isUseOkRetryButton;

    /**
     * @param flag {@link #_isUseOkRetryButton}의 값을 설정
     */
    protected final void setUseOkRetryButton(boolean flag) {
        _isUseOkRetryButton = flag;
    }

    /**
     * {@link #_isUseRetryButton}와 {@link #_isUseOkRetryButton}의 값을 초기화 한다.
     */
    private void initButtonFlag() {
        _isUseRetryButton = false;
        _isUseOkRetryButton = false;
    }

    /**
     * {@link #run()}에서 network작업 후 오류처리 및 재시도 처리를 위한 handler
     * <p>
     * 정해진 처리 완료 후 {@link #onCompleteHttpRequest(Message)}를 호출하여 이후 작업에 대해 작성한다.
     * <p>
     * 만약 network에 대한 오류처리를 직접 하고 싶다면
     * {@link #setErrorDialogSkip(boolean)}을 false로 설정하고 특정 에러에 대한 처리를 작성한다.
     * <p>
     * <li>
     * 에러 메세지 : {@link #_isUseDebugAlert}의 값이 false인 경우 아래와 같은 에러메세지가 출력이 되고
     * true라면 {@link #_currentNetState}에 정의된 exception을 dialog에 그대로 출력하게 된다.<br/>
     * {@link #setErrorDialogSkip(boolean)}을 통하여 dialog를 노출하지 않는다.
     * </li>
     * <li>EXCEPTION_NET_NOT_WAKE : "인터넷에 연결되어 있지 않습니다. wi-fi또는 3g의 연결상태를 확인해 주세요." {@link #_conFail}</li>
     * <li>etc : "인터넷 연결이 원할 하지 않습니다. 잠시후에 다시 시도하여 주세요." {@link #_netNotWake}</li>
     *
     * @see NetState
     */
    private final Handler _handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message msg) {
            if (_isErrorDialogSkip) {
                onCompleteHttpRequest(msg);
            } else {
                if (_isUseDebugAlert) {
                    switch (_netState) {
                        case NOT_INIT_CONTROLLER:
                            createDialog("controller not init", null);
                            break;
                        case EXCEPTION_NET_NOT_WAKE:
                            createDialog(_netNotWake, null);
                            break;
                        case EXCEPTION_PARSE:
                            createDialog("ParseException", null);
                            break;
                        case EXCEPTION_CLIENT_PROTOCOL:
                            createDialog("ClientProtocolException", null);
                            break;
                        case EXCEPTION_SOCKET:
                            createDialog("SocketException", null);
                            break;
                        case EXCEPTION_IO:
                            createDialog("IOException", null);
                            break;
                        case EXCEPTION:
                            createDialog("Net Exception", null);
                            break;
                        case EXCEPTION_PARSER:
                            createDialog("Parser Error", null);
                            break;
                        case EXCEPTION_NULL_RESPONSE:
                            createDialog("Response is null", null);
                            break;
                        // TODO 확인하지 않은 에러일때 코드와 메세지를 그대로 보여준다.
                        default:
                            onCompleteHttpRequest(msg);
                            break;
                    }
                } else {
                    switch (_netState) {
                        case EXCEPTION_NET_NOT_WAKE:
                            createDialog(_netNotWake, null);
                            break;
                        case NOT_INIT_CONTROLLER:
                        case EXCEPTION_PARSE:
                        case EXCEPTION_CLIENT_PROTOCOL:
                        case EXCEPTION_SOCKET:
                        case EXCEPTION_IO:
                        case EXCEPTION:
                        case EXCEPTION_PARSER:
                        case EXCEPTION_NULL_RESPONSE:
                            createDialog(_conFail, null);
                            break;
                        default:
                            onCompleteHttpRequest(msg);
                            break;
                    }
                }
            }
            return true;
        }
    });

    /**
     * {@link #_handler}에서 exception이 처리된 후 호출.
     */
    protected abstract void onCompleteHttpRequest(Message msg);

    /**
     * retry listener
     */
    private final DialogInterface.OnClickListener _retryButtonListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            onRetryButtonClick();
        }
    };

    protected DialogInterface.OnClickListener getRetryButtonListener() {
        return _retryButtonListener;
    }

    /**
     * 오류가 있었을 시에 {@link #_requestImageMap}, {@link #_requestMap}에 담겨진 parameter와 value를 가지고 통신 재시도
     */
    protected final void onRetryButtonClick() {
        initButtonFlag();
        netCheckBeforStart();
    }

    /**
     * ok listener
     */
    private final DialogInterface.OnClickListener _okButtonListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            onOkButtonClick();
        }
    };

    protected DialogInterface.OnClickListener getOkButtonListener() {
        return _okButtonListener;
    }

    /**
     * 오류가 있었을 시에 {@link #_requestImageMap}, {@link #_requestMap}에 담겨진 parameter와 value를 가지고 통신 재시도
     */
    protected final void onOkButtonClick() {
        initButtonFlag();
        onOkButtonListenerComplete();
    }

    /**
     * {@link #_isUseOkRetryButton}, {@link #_isUseRetryButton}에 따른 dialog의 ok 버튼의 실행에 대한 내용 작성<br/>
     */
    protected abstract void onOkButtonListenerComplete();

    /**
     * 기본 알림창을 사용하지 않고 custom dialog를 사용하려면 이부분에 dialog를 대신할 작업을 작성
     * <p>
     * {@link #setUseCustomDialog(boolean)}를 사용하여 custom 알림창 사용 설정
     * <p>
     * override하여 사용할 경우 알림창의 재시도 버튼은 {@link #onRetryButtonClick()}, 확인버튼은 {@link #onOkButtonClick()}을
     * 각 버튼 리스너의 마지막 부분에서 호출해 주어야만 그 후의 작업이 정상적으로 연결 될 수 있다.<br/>
     *
     * @param isUseRetryButton   {@link #_isUseRetryButton}
     * @param isUseOkRetryButton {@link #_isUseOkRetryButton}
     */
    protected abstract Dialog createCustomDialog(String msg, String title, String btOkName, String btRetryName, boolean isUseRetryButton, boolean isUseOkRetryButton);

    /**
     * {@link AlertDialog}를 사용하여 버튼(최대 두개)와 msg, title을 가지고 있는 알림창을 설정한다.<br/>
     */
    protected final void createDialog(String msg, String title) {
        Dialog dialog;
        if (_isUseCustomDialog) {
            dialog = createCustomDialog(msg, title, _ok, _retry, _isUseRetryButton, _isUseOkRetryButton);
        } else {
            View view = ViewUtil.alertViewCenterAlign(msg, title, _context);
            AlertDialog.Builder builder = new AlertDialog.Builder(_context);
            builder.setView(view);
            if (_isUseRetryButton) {
                builder.setPositiveButton(_ok, _okButtonListener);
                builder.setNegativeButton(_retry, _retryButtonListener);
            } else {
                if (_isUseOkRetryButton) builder.setPositiveButton(_ok, _retryButtonListener);
                else builder.setPositiveButton(_ok, _okButtonListener);
            }
            dialog = builder.create();
        }
        dialogShow(dialog);
    }

    private void dialogShow(Dialog dialog) {
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        // 중복된 controller의 사용시에 dialog의 겹침으로 인한 error catch
        try {
            dialog.show();
        } catch (Exception e) {
            Log.e(TAG, "Exception - " + e.getMessage(), e);
        }
    }

    /**
     * URLEncode된 string을 URLDecode 한다.
     */
    protected final String urlDecode(String str) {
        String decodeStr = null;
        try {
            if (str != null) decodeStr = URLDecoder.decode(str, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            Log.e(TAG, "UnsupportedEncodingException - " + e.getMessage(), e);
        } catch (Exception e) {
            Log.e(TAG, "Exception - " + e.getMessage(), e);
        }
        return decodeStr;
    }

    protected final String encryptAES(String value, String key) {
        String returnStr = value;
        try {
            returnStr = CodeAction.EncryptAES(value, key);
        } catch (Exception e) {
            Log.e(e.getMessage());
        }
        return returnStr;
    }

    protected final String decryptAES(String value, String key) {
        String returnStr = value;
        try {
            returnStr = CodeAction.DecryptAES(value, key);
        } catch (Exception e) {
            Log.e(e.getMessage());
        }
        return returnStr;
    }

    protected final String urlDecodeAndDecryptAES(String value, String key) {
        String returnStr = urlDecode(value);
        try {
            returnStr = CodeAction.DecryptAES(value, key);
        } catch (Exception e) {
            Log.e(e.getMessage());
        }
        return returnStr;
    }

    /**
     * response string을 사용가능한 DTO instance로 parsing 한다.<br/>
     * Work Thread 작업이므로 UI Thread를 호출할때 주의하여야 한다.<br/>
     *
     * @param responseStr response data
     * @return DTO instance
     */
    protected abstract T parsing(String responseStr) throws Exception;

    /**
     * response string이 urlEncode가 되어있는 경우 urlDecode의 일괄 작업을 작성한다.<br/>
     * Work Thread 작업이므로 UI Thread를 호출할때 주의하여야 한다.<br/>
     */
    protected abstract void urlDecode(T responseObject);

    /**
     * 통신에 성공하면 호출된다. {@link #parsing(String)}
     * 성공과 실패에 대한 {@link #_currentNetState}의 값을 설정하고 그에 따라 수행되는 작업을 작성한다.<br/>
     * 이부분이 BaseController의 Thread안에서 마지막으로 수행되는 작업이 되기 때문에 UI Thread를 호출하는 경우 Handler등을 이용하여 호출하여야 하며
     * 별도의 handler을 설정하지 않고 BaseController의 {@link #onCompleteHttpRequest(Message)}를 바로 호출하고 싶은 경우에는
     * {@link #callControllerHandler(Object)}를 호출한다.
     * Work Thread 작업이므로 UI Thread를 호출할때 주의하여야 한다.<br/>
     *
     * @param responseObject {@link #parsing(String)}로 생성된 DTO object
     */
    protected abstract void controllerThreadWork(T responseObject);

    protected final void callControllerHandler(Object responseObject) {
        Message msg = _handler.obtainMessage();
        msg.what = _currentNetState;
        msg.obj = responseObject;
        _handler.sendMessage(msg);
    }

    /**
     * true : multipart/form-data<br/>
     * flase : text/plain
     */
    private boolean _currentEnctype;

    /**
     * current thread start()<br/>
     * enctype : text/plain<br/>
     */
    public void startRequest(HttpMethod method) {
        _method = method;
        netCheckBeforStart();
    }

    /**
     * current thread start()<br/>
     * enctype fix : multipart/form-data<br/>
     */
    public void startMultpartRequest(HttpMethod method) {
        _currentEnctype = true;
        startRequest(method);
    }

    /**
     * network thred를 시작하기 전에 통신 가능 여부를 확인한다.<br/>
     * network을 사용할 수 없는 상태일때에는 {@link #connectFail(NetState)}를 호출하여 오류 및 재시도 처리
     */
    private void netCheckBeforStart() {
        _currentNetState = NetState.NOT_INIT_CONTROLLER.code;
        _netState = NetState.NOT_INIT_CONTROLLER;
        if (_isCheckNetState && BaseNet.getInstance().getNetState(_context) < 0) {
            connectFail(NetState.EXCEPTION_NET_NOT_WAKE);
        } else {
            new Thread(this).start();
        }
    }

    /**
     * network을 사용할 수 없는 상태일때 실행될 작업 작성
     *
     * @see BaseController#_currentNetState
     */
//    @Override
    private void connectFail(NetState netState) {
        _currentNetState = netState.code;
        _netState = netState;
        _handler.sendEmptyMessage(_currentNetState);
    }

    @Override
    public void run() {
        // 1. request
        String responseStr = null;
        try {
            responseStr = sendRequest(_method, _sendUrl, _requestHeaderMap, _requestMap, _requestImageMap);
        } catch (ParseException e) {
            Log.e(TAG, "ParseException : " + e.getMessage(), e);
            _netState = NetState.EXCEPTION_PARSE;
        } catch (ClientProtocolException e) {
            Log.e(TAG, "ClientProtocolException : " + e.getMessage(), e);
            _netState = NetState.EXCEPTION_CLIENT_PROTOCOL;
        } catch (SocketException e) {
            Log.e(TAG, "SocketException : " + e.getMessage(), e);
            _netState = NetState.EXCEPTION_SOCKET;
        } catch (IOException e) {
            Log.e(TAG, "IOException : " + e.getMessage(), e);
            _netState = NetState.EXCEPTION_IO;
        } catch (Exception e) {
            Log.e(TAG, "Exception : " + e.getMessage(), e);
            _netState = NetState.EXCEPTION;
        }

        // 2. parsing
        T responseObject = null;
        if (responseStr != null) {
            try {
                responseObject = parsing(responseStr);
                _netState = NetState.NET_SUCCESS;
            } catch (Exception e) {
                Log.e(TAG, "Exception : " + e.getMessage(), e);
                _netState = NetState.EXCEPTION_PARSER;
            }
        } else _netState = NetState.EXCEPTION_NULL_RESPONSE;

        if (responseObject == null) _netState = NetState.EXCEPTION_NULL_RESPONSE;

        // 3. network error & parser error check
        if (_netState.code <= NetState.NOT_INIT_CONTROLLER.code) {
            _currentNetState = _netState.code;
            _handler.sendEmptyMessage(_currentNetState);
        } else if (responseObject != null) {
            urlDecode(responseObject);
            initButtonFlag();
            controllerThreadWork(responseObject);
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
    private String sendRequest(HttpMethod method, String sendUrl, HashMap<String, String> requestHeaderMap, HashMap<String, String> requestMap, HashMap<String, String> requestImageMap) throws ParseException, ClientProtocolException, IOException, Exception {
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
}