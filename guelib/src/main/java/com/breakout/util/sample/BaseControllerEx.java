package com.breakout.util.sample;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Message;
import android.os.Parcel;
import android.os.Parcelable;

import com.breakout.util.Log;
import com.breakout.util.net.controller.BaseController;
import com.google.gson.Gson;

import java.net.URLDecoder;
import java.util.ArrayList;


/**
 * {@link BaseController}를 상속받아 사용할수 있는 Controller 예제<br/>
 * <ol>
 * </ol>
 * {@link BaseController}를 상속받아 현재 class를 상속받는 다른 Controller class들과 함께 처리될수 있는 오류의 일괄처리와 필수 코드 정의 및 통신에 따른 알림창 생성
 *
 * @author sung-gue
 * @version 1.0 (2014. 8. 17.)
 */
public abstract class BaseControllerEx extends BaseController {
    private static final boolean DEBUG = true;
    protected String _serverMsg;
    protected Object _baseObject;

    public BaseControllerEx(Context context, Handler handler, String sendUrl) {
        super(context, handler, sendUrl, DEBUG);
        // network exception로 인해 생성되는 dialog의 문자 설정
        setStringForDialog("인터넷에 연결되어 있지 않습니다. wi-fi또는 3g의 연결상태를 확인해 주세요.",
                "인터넷 연결이 원할 하지 않습니다. 잠시후에 다시 시도하여 주세요.",
                "확인",
                "재시도"
        );
        // custom dialog 사용여부 설정
        useCustomDialog(true);
    }

    @Override
    protected void setRequiredParam(String[] value) {
        clearRequestMap();
        setParamAfterNullCheck("channel", "android");
    }

    @Override
    protected void handlerProcess2(Message msg) {
        if (isDialogSkip()) {
            sendMessage();
            return;
        }
        Message newMsg = Message.obtain(msg);
        switch (_currentNetState) {
            case 100:    // 입력 파라메터 오류
                super.setNoRetryButton(true);
                createDialog(_serverMsg, null);
                break;

            // code를 그대로 activity로 전달 하는 경우
            case 110:    // 오류1
            case 120:    // 오류2
                _handler.sendMessage(newMsg);
                break;

            // server message 그대로 사용하는 code
            case 130:    // 오류3
            case 140:    // 오류4
            case 9998:    // 데이터베이스 오류
                super.setNoRetryButton(true);
                createDialog("DB 점검중", null);
                break;
            case 9999:    // 긴급점검
                super.setNoRetryButton(true);
                createDialog(_serverMsg, null);
                break;
            case 9000:    // 지원하지 않는 웹서비스 코드입니다.
                sendMessage();
                break;
            default:    // 확인하지 않은 에러일때 코드와 메세지를 그대로 보여준다.
                super.setNoRetryButton(true);
                createDialog(_serverMsg, null);
                break;
        }
    }

    private DialogInterface.OnClickListener _okListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            okClick();
        }
    };
    private DialogInterface.OnClickListener _retryListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            retryClick();
        }
    };

    @Override
    protected Dialog createCustomDialog(String msg, String title, String btOkName, String btRetryName, boolean noRetryButton, boolean okRetryButton) {
        AlertDialog.Builder builder = new AlertDialog.Builder(_context);
        builder.setMessage(msg);
        builder.setTitle(title);
        if (noRetryButton) {
            if (okRetryButton) builder.setPositiveButton(btOkName, _retryListener);
            else builder.setPositiveButton(btOkName, _okListener);
        } else {
            builder.setPositiveButton(btOkName, _okListener);
            builder.setNegativeButton(btRetryName, _retryListener);
        }
        Dialog dialog = builder.create();
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        return dialog;
    }

    @Override
    protected void okListenerWork() {
        if (_currentNetState == 9999) {
            // 앱종료
        } else if (_currentNetState == 100) {
            // 초기화면 이동
        } else {
            sendMessage();
        }
    }

    /**
     * activity로 결과 전달
     */
    private void sendMessage() {
        if (_baseObject == null) initObject();

        Message msg = _handler.obtainMessage();
        msg.what = _currentNetState;
        msg.obj = _baseObject;
        _handler.sendMessage(msg);
    }

    /**
     * activity의 handler에서 null check위해 DTO를 초기화한다.
     */
    protected abstract void initObject();

    @Override
    protected void controllerThreadWork(Object responseObject) {
        // success
        if (((BaseDTO) responseObject).code != null && "0000".equals(((BaseDTO) responseObject).code)) {
            Message msg = _handler.obtainMessage();
            msg.what = _currentNetState;
            msg.obj = responseObject;
            _handler.sendMessage(msg);
        }
        // server code error
        else {
            try {
                _baseObject = responseObject;
                _currentNetState = Integer.parseInt(((BaseDTO) responseObject).code);
                _serverMsg = ((BaseDTO) responseObject).message;
                if (_serverMsg == null) _serverMsg = "";

                callControllerHandler(responseObject);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Controller의 종류에 대한 enum class<br>
     * 서버의 url 정보와 parsing을 주 목적으로 한다<br/>
     */
    public enum ControllerType {
        USER("/UserInterface.jsp", UserDTO.class, UserDTOs.class),
        /*NOTICE("/NoticeInterface.jsp", NoticeDTO.class, NoticeDTOs.class),*/;
        private final String SERVER_URL = "http://www.serverurl.com";
        private final String serverUrl;
        private final Class<?> parsingClassSingle;
        private final Class<?> parsingClassArray;

        private ControllerType(String url, Class<?> parsingClassSingle, Class<?> parsingClassArray) {
            this.serverUrl = url;
            this.parsingClassSingle = parsingClassSingle;
            this.parsingClassArray = parsingClassArray;
        }

        public Class<?> getParsingClassSingle(boolean mode) {
            return parsingClassSingle;
        }

        public Class<?> getParsingClassArray() {
            return parsingClassArray;
        }

        public String getServerUrl() {
            return SERVER_URL + serverUrl;
        }

        public Object getParseObject(String responseJson, boolean singleMode) throws Exception {
            String log = "[response json parser] start----------------------------\n";
            String decodeResponseJson = null;
            try {
                decodeResponseJson = URLDecoder.decode(responseJson, "UTF-8");
            } catch (Exception e) {
                decodeResponseJson = null;
            }
            log += "url : " + getServerUrl() + "\n";
            if (decodeResponseJson != null) log += decodeResponseJson;
            else log += responseJson;
            Log.d(getClass().getSimpleName(), log + "\n[response json parser] end------------------------------");

            if (singleMode) return new Gson().fromJson(responseJson, parsingClassSingle);
            else return new Gson().fromJson(responseJson, parsingClassArray);
        }
    }

    /**
     * {@link Gson}을 사용하기 위한 DTO class이다.
     * intent에 의한 작업이 이루어질수 있도록 {@link Parcelable}을 구현한다.<br/>
     * 서버와 통신 규약에 따라 다르게 정의 될수 있음
     */
    public static class BaseDTO implements Parcelable {
        public String code;
        public String message;

        public BaseDTO() {
        }

        public BaseDTO(Parcel src) {
            code = src.readString();
            message = src.readString();
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(code);
            dest.writeString(message);
        }

        public static final Parcelable.Creator<BaseDTO> CREATOR = new Creator<BaseDTO>() {
            @Override
            public BaseDTO createFromParcel(Parcel src) {
                return new BaseDTO(src);
            }

            @Override
            public BaseDTO[] newArray(int size) {
                return new BaseDTO[size];
            }
        };
    }

    public static class UserDTO extends BaseDTO implements Parcelable {
        public UserData data;

        public UserDTO() {
        }

        public UserDTO(Parcel src) {
            data = src.readParcelable(UserData.class.getClassLoader());
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeParcelable(data, 0);
        }

        public static final Parcelable.Creator<UserDTO> CREATOR = new Creator<UserDTO>() {
            @Override
            public UserDTO createFromParcel(Parcel src) {
                return new UserDTO(src);
            }

            @Override
            public UserDTO[] newArray(int size) {
                return new UserDTO[size];
            }
        };

    }

    public static class UserDTOs extends BaseDTO implements Parcelable {
        public ArrayList<UserData> data;

        public UserDTOs() {
        }

        public UserDTOs(Parcel src) {
            data = new ArrayList<UserData>();
            src.readTypedList(data, UserData.CREATOR);
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeTypedList(data);
        }

        public static final Parcelable.Creator<UserDTOs> CREATOR = new Creator<UserDTOs>() {
            @Override
            public UserDTOs createFromParcel(Parcel src) {
                return new UserDTOs(src);
            }

            @Override
            public UserDTOs[] newArray(int size) {
                return new UserDTOs[size];
            }
        };
    }

    public static class UserData implements Parcelable {
        public String name;
        public String tel;

        public UserData(Parcel src) {
            name = src.readString();
            tel = src.readString();
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(name);
            dest.writeString(tel);
        }

        public static final Parcelable.Creator<UserData> CREATOR = new Creator<UserData>() {
            @Override
            public UserData createFromParcel(Parcel src) {
                return new UserData(src);
            }

            @Override
            public UserData[] newArray(int size) {
                return new UserData[size];
            }
        };
    }
}