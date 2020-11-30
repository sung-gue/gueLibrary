package com.breakout.sample.controller;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.widget.Toast;

import com.breakout.sample.BaseActivity;
import com.breakout.sample.Log;
import com.breakout.sample.R;
import com.breakout.sample.constant.Const;
import com.breakout.sample.constant.Headers;
import com.breakout.sample.constant.ReceiverName;
import com.breakout.sample.constant.SharedData;
import com.breakout.sample.constant.Values;
import com.breakout.sample.dto.BaseDto;
import com.breakout.sample.ui.IntroActivity;
import com.breakout.sample.util.GetAdidTask;
import com.breakout.util.CodeAction;
import com.breakout.util.Util;
import com.breakout.util.device.DeviceUtil;
import com.breakout.util.net.BaseController;
import com.breakout.util.widget.CustomDialog;

import java.util.Locale;


/**
 * 오류의 일괄처리와 필수코드 정의 및 통신에 따른 알림창 생성
 *
 * @author sung-gue
 * @version 1.0 (2016.02.29)
 */
abstract class ControllerEx<T extends BaseDto> extends BaseController<T> {
    /**
     * server msg (Base)<br/>
     */
    protected String _serverMsg;
    protected T _baseObject;
    protected SharedData _shared;

    protected final String SUCCESS_CODE = "0000";

    private String _adid;

    public ControllerEx(Context context, Handler handler, String sendUrl) {
        super(context, handler, sendUrl, Const.TEST);
        // network exception로 인해 생성되는 dialog의 문자 설정
        setStringForDialog(context.getString(R.string.al_net_not_wake),
                context.getString(R.string.al_conFail),
                context.getString(R.string.ok),
                context.getString(R.string.retry)
        );
        // custom dialog 사용여부 설정
        setUseCustomDialog(true);

        _shared = SharedData.getInstance(context);
        if (_shared.getAndroidAdid() == null) {
            new GetAdidTask(context, new GetAdidTask.OnFinishGetAdidListener() {
                @Override
                public void OnFinishGetAdid(String adid) {
                    _shared.setAndroidAdid(adid);
                    _adid = adid;
                }
            }).execute();
        } else {
            _adid = _shared.getAndroidAdid();
        }
    }

    /**
     * setRequiredParam(new String[]{"[COMMAND]"})
     */
    @Override
    protected void setRequiredParam(String... values) {
        clearRequestMap();
        _sendUrl += values[0];
        super.setHeaderAfterNullCheck(Headers.SESSION, _shared.getUserSession());
        super.setHeaderAfterNullCheck(Headers.CHANNEL, Values.channel);
        super.setHeaderAfterNullCheck(Headers.UUID, _adid);
        super.setHeaderAfterNullCheck(Headers.APP_VERSION, DeviceUtil.getAppVersionName(_context));
        super.setHeaderAfterNullCheck(Headers.COUNTRY_CODE, Locale.getDefault().getCountry());
    }

    public void delUserinfoAndGoIntro() {
        if (!(_context instanceof IntroActivity)) {
            Toast.makeText(_context, _context.getString(R.string.al_expire_session), Toast.LENGTH_LONG).show();
        }
        BaseActivity activity = (BaseActivity) _context;
        activity.onSignOutFinishAndRestartApp();
    }

    @Override
    protected void onCompleteHttpRequest(Message msg) {
        if (_baseObject != null && "SESSION_IS_WRONG".equalsIgnoreCase(_baseObject.code)) {
            delUserinfoAndGoIntro();
            return;
        }

//        if (isDialogSkip()) {
//            sendMessage();
//            return;
//        }
        /*
            TODO code, message 분석하여 앱 강제종료 & 앱 업데이트 처리 추가 해야함
         */
        switch (_currentNetState) {
            case 100:    //로그인 세션키 만료
                // IntroActivity : auth_key에 대한 검증 오류 , 그대로 return
                /*if (_context instanceof IntroActivity) {
                    _handler.sendEmptyMessage(_currentNetState);
                    return;
                } else {
                    super.setNoRetryButton(true);
                    createDialog(_context.getString(R.string.al_expire_auth), null);
                }*/
                break;
            // code를 그대로 activity로 전달 하는 경우
            /*case 110:    //
                Message newMsg = Message.obtain(msg);
                _handler.sendMessage(newMsg);
                break;*/

            case 2000:  //test
//                super.setNoRetryButton(true);
//                createDialog("test" + _serverMsg, null);
                break;
            // server message 그대로 사용하는 code
            case 1:     //파라메터 오류 ($INPUT)
            case 2:     //필수값을 입력해 주세요.


            case 9000:  //올바른 접근이 아닙니다.
            case 9997:  //오류가 발생하였습니다.
            case 9998:  //DB 처리 중 오류가 발견되었습니다.
            case 9999:  //긴급점검

            default:    //확인하지 않은 에러일때 코드와 메세지를 그대로 보여준다.
//                super.setNoRetryButton(true);
//                createDialog(_serverMsg, null);
                sendMessage();
                break;
        }
    }

    private DialogInterface.OnClickListener _okListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            onOkButtonClick();
        }
    };
    private DialogInterface.OnClickListener _retryListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            onRetryButtonClick();
        }
    };

    @Override
    protected Dialog createCustomDialog(String msg, String title, String btOkName, String
            btRetryName, boolean noRetryButton, boolean okRetryButton) {
        CustomDialog dialog = new CustomDialog(_context);
        dialog.setContents(title, msg);
        if (noRetryButton) {
            if (okRetryButton) dialog.setOkBt(btOkName, _retryListener);
            else dialog.setOkBt(btOkName, _okListener);
        } else {
            dialog.setOkBt(btOkName, _okListener);
            dialog.setCancelBt(btRetryName, _retryListener);
        }
        dialog.setCancel(false);
        return dialog;
    }

    @Override
    protected void onOkButtonListenerComplete() {
        if (_currentNetState == 9999) {
            Util.appExit(_context);
        } else if (_currentNetState == 100) {
            SharedData.getInstance(_context).clearUserInfo();
            Util.forceMove(_context);
        } else if (_currentNetState <= NetState.NOT_INIT_CONTROLLER.code) {
            _context.sendBroadcast(new Intent(ReceiverName.FINISH_EXCLUDE_MAIN));
            _context.sendBroadcast(new Intent(ReceiverName.FINISH));
            if (_context instanceof Activity) {
                ((Activity) _context).finish();
            }
        } else {
            sendMessage();
        }
    }

    /**
     * activity로 결과 전달
     */
    private void sendMessage() {
        if (_context != null && _context instanceof Activity && !((Activity) _context).isFinishing()) {
            if (_baseObject == null) _baseObject = initObject();

            Message msg = _callBackHandler.obtainMessage();
            msg.what = _currentNetState;
            msg.obj = _baseObject;
            _callBackHandler.sendMessage(msg);
        }
    }

    /**
     * activity의 handler에서 null check를 위해 DTO object를 초기화한다
     *
     * @return 각 controller에 맞는 초기화된 인스턴스
     */
    protected abstract T initObject();

    @Override
    protected void urlDecode(T dto) {
        dto.message = urlDecode(dto.message);
    }

    public static String encryptAES(String value) {
        String returnStr = value;
        try {
            returnStr = CodeAction.EncryptAES(value, Const.AES_KEY);
        } catch (Exception e) {
            Log.e(e.getMessage());
        }
        return returnStr;
    }

    public static String decryptAES(String value) {
        String returnStr = value;
        try {
            returnStr = CodeAction.DecryptAES(value, Const.AES_KEY);
        } catch (Exception e) {
            Log.e(e.getMessage());
        }
        return returnStr;
    }

    protected String urlDecodeAndDecryptAES(String value) {
        String returnStr = urlDecode(value);
        try {
            returnStr = CodeAction.DecryptAES(value, Const.AES_KEY);
        } catch (Exception e) {
            Log.e(e.getMessage());
        }
        return returnStr;
    }

    @Override
    protected void controllerThreadWork(T responseObject) {
        _baseObject = responseObject;
        // success
        if (!TextUtils.isEmpty(responseObject.code) && SUCCESS_CODE.equals(responseObject.code)) {
            sendMessage();
        } else {
//            sendMessage();
            _serverMsg = responseObject.message;
            if (_serverMsg == null) _serverMsg = "";
            callControllerHandler(responseObject);
        }
    }
}