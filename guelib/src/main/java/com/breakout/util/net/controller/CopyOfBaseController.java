package com.breakout.util.net.controller;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;

import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Message;
import android.view.View;

import com.breakout.util.Log;
import com.breakout.util.string.StringUtil;
import com.breakout.util.widget.ViewUtil;


/**
 * 
 * 현재 클래스를 상속받은 자식 클래스에게 필요한 기능을 정의 하고 있는 추상클래스 이다.<br/>
 * <ol>기능
 * 	<li>통신 가능상황 체크 후 통신 불능 상황일 경우 알림창 팝업 제공</li>
 * 	<li>통신 실패 시 재시도 처리</li>
 * 	<li>통신시 필요한 parsing이나 공통파라미터 입력 작업</li>
 * </ol>
 * {@link #_requestMap}을 저장하여 통신 실패시 재전송이 가능하게 구성되어 있으므로 
 * 동시에 여러개의 통신을 원할 경우 {@link CopyOfBaseController}의 새 인스턴스를 생성하여 작업하여야 한다. 
 * @author gue
 * @since 2012. 12. 18.
 * @copyright Copyright.2011.gue.All rights reserved.
 * @version 1.0
 * @history <ol>
 * 		<li>변경자/날짜 : 변경사항</li>
 * </ol>
 */
public abstract class CopyOfBaseController {
	protected final String TAG = getClass().getSimpleName();
//	/**
//	 * network 성공, - single data
//	 */
//	public static final int NET_SUCCESS_SINGLE_MODE = -1001;
//	/**
//	 * network 성공, - array data
//	 */
//	public static final int NET_SUCCESS_ARRAY_MODE = -1002;
	/**
	 * network 성공
	 */
	public static final int NET_SUCCESS = -1001;
	/**
	 * {@link #_currentNetState}의 초기값 설정
	 */
	public static final int NOT_INIT_CONTROLLER = -1000;
	/**
	 * NetWork state가 3g,wifi 상태가 아님, NetWork에 연결할 수 없음
	 */
	public static final int EXCEPTION_NET_NOT_WAKE = -9001;
	/**
	 * ParseException
	 */
	public static final int EXCEPTION_PARSE = -9002;
	/**
	 * ClientProtocolException
	 */
	public static final int EXCEPTION_CLIENT_PROTOCOL = -9003;
	/**
	 * IOException ex) socketException -> 서버가 응답하지 않을 경우
	 */
	public static final int EXCEPTION_IO = -9004;
	/**
	 * exception
	 */
	public static final int EXCEPTION = -9005;
	/**
	 * jsonParser error
	 */
	public static final int EXCEPTION_JSON_PARSER = -9006;
	/**
	 * responseJson is null<br>
	 * ResponseJson이 원인모를 결과로 인해 null값으로 넘어올 경우이다. dialog안에 재시도에 대한 코드를 작성
	 */
	public static final int EXCEPTION_NULL_RESPONSE = -9007;
	/**
	 * activity context, network state check와 net exception에 대한 알림 작업을 하기 위해 필요.<br>
	 */
	protected Context _context;
	/**
	 * network 처리 후 data를 담아 controller를 호출한 activity로 돌려주기 위한 callback handler<br>
	 * {@link CopyOfBaseController}의 Constructor에서 입력 받는다.
	 */
	protected Handler _handler;
	/**
	 * parameter map : stringBody for text/plain enctype
	 * @see ControllerThread#_currentEnctype
	 */
	protected final HashMap<String,String> _requestMap;
	/**
	 * parameter map : fileBody for multipart/form-data enctype
	 * @see ControllerThread#_currentEnctype
	 */
	protected final HashMap<String,String> _requestImageMap;
//	* <li>-1001 : {@link #NET_SUCCESS_SINGLE_MODE}</li>
//	* <li>-1002 : {@link #NET_SUCCESS_ARRAY_MODE}</li>
	/**
	 * Handler의 Message에 삽입된 what에 관한 정보<p>
	 * 서버와의 통신후 data가 싱글인지에 대한 체크값과 서버와 약속된 error가 있다면 처리하여 activity에서 해당 에러에 대한 체크를 할수 있게 하여 준다.
	 * <li>-1001 : {@link #NET_SUCCESS}</li>
	 * <li>-1000 : {@link #NOT_INIT_CONTROLLER} controller not init초기값</li>
	 * <li>-9001 : {@link #EXCEPTION_NET_NOT_WAKE}</li>
	 * <li>-9002 : {@link #EXCEPTION_PARSE}</li>
	 * <li>-9003 : {@link #EXCEPTION_CLIENT_PROTOCOL}</li>
	 * <li>-9004 : {@link #EXCEPTION_IO}</li>
	 * <li>-9005 : {@link #EXCEPTION}</li>
	 * <li>-9006 : {@link #EXCEPTION_JSON_PARSER}</li>
	 * <li>-9007 : {@link #EXCEPTION_NULL_RESPONSE}</li>
	 * <li>-9008 : </li>
	 * <li>-9009 : </li>
	 * <li>-9010 : </li>
	 * @see ControllerThread#_what
	 */
	protected int _currentNetState = NOT_INIT_CONTROLLER;
	/**
	 * 접속 url
	 */
	protected String _sendUrl;
	/**
	 * response json data에서 특정 output parameter 부분이 배열([{}])일때와 아닐때({})의 구분<br>
	 * response가 json이 아닐경우 true로 설정한다.
	 * <li>true : single type</li>
	 * <li>false : array type</li>
	 */
	protected boolean _singleDataMode;
	/**
	 *  enctype mode 에 따른 구분
	 * <li>true : multipart/form-data</li>
	 * <li>false : text/plain</li>
	 */
	protected boolean _enctypeMode;
	/**
	 * true일 경우 {@link #handlerProcess1(Message)}에 의해 생성되는 alert가 {@link #_currentNetState}에 정의된 exception을 직접 보여준다.
	 */
	protected final boolean ALERT_DEBUG;
	
	private String _netNotWake = "인터넷에 연결되어 있지 않습니다. wi-fi또는 3g의 연결상태를 확인해 주세요.";
	private String _conFail = "인터넷 연결이 원할 하지 않습니다. 잠시후에 다시 시도하여 주세요.";
	private String _ok = "확인";
	private String _retry = "재시도";
	
	/**
	 * 기본 알림창을 사용하지 않는 경우에 true
	 */
	private boolean _useCustomDialog;
	
	/**
	 * 생성자를 호출후에 see Also에 있는 method를 호출하여 기본 설정을 사용자에 맞게 변경한다.
	 * @param context Activity context {@link #_context}
	 * @param handler {@link #_handler}
	 * @param sendUrl 접속 url {@link #_sendUrl}
	 * @param ALERT_DEBUG {@link #ALERT_DEBUG}
	 * @see #setStringForDialog(String, String)
	 * @see #setStringForDialog(String, String, String, String)
	 * @see #useCustomDialog()
	 */
	protected CopyOfBaseController(Context context, Handler handler, String sendUrl, boolean ALERT_DEBUG) {
		this._sendUrl = sendUrl;
		this._handler = handler;
		this._context = context;
		this.ALERT_DEBUG = ALERT_DEBUG;
		_requestMap = new HashMap<String,String>();
		_requestImageMap = new HashMap<String,String>();
		_controllerThreadHandler = new ControllerHandler(this);
	}
	
	/**
	 * network exception로 인해 생성되는 dialog의 문자 설정
	 * @param netNotWake dialog message, 기본값 : "인터넷에 연결되어 있지 않습니다. wi-fi또는 3g의 연결상태를 확인해 주세요." 
	 * @param conFail dialog message, 기본값 : "인터넷 연결이 원할 하지 않습니다. 잠시후에 다시 시도하여 주세요."
	 * @param ok dialog PositiveButton, 기본값 : "확인"
	 * @param retry dialog NegativeButton, 기본값 : "재시도"
	 * @author gue
	 * @since 2012. 12. 21.
	 */
	protected final void setStringForDialog(String netNotWake, String conFail, String ok, String retry) {
		_netNotWake = netNotWake;
		_conFail = conFail;
		setStringForDialog(ok, retry);
	}
	
	/**
	 * network exception의 dialog의 버튼에 삽입될 문자를 설정한다.
	 * @param ok dialog PositiveButton, 기본값 : "확인"
	 * @param retry dialog NegativeButton, 기본값 : "재시도"
	 * @author gue
	 * @since 2012. 12. 21.
	 */
	protected final void setStringForDialog(String ok, String retry) {
		_ok = ok;
		_retry = retry;
	}
	
	/**
	 * BaseController에서 제공되는 기본 알림창을 사용하지 않는 경우 설정
	 * @author gue
	 * @since 2013. 6. 21.
	 */
	protected final void useCustomDialog(boolean useCustom) {
		_useCustomDialog = useCustom;
	}

	
/* ************************************************************************************************
 * INFO parameter check & clear
 */
	/**
	 * 파라미터로 넘어온 value가 null이거나 ""인경우에는 {@link #_requestMap}에 입력하지 않는다. 
	 * @author gue
	 */
	protected final void setParamAfterNullCheck(String param, String value){
		if (StringUtil.nullCheckB(value)) _requestMap.put(param, value);
	}
	
	/**
	 * 파라미터로 넘어온 path에 null이거나 파일이 존재하지 않는 경우에는 {@link #_requestImageMap}에 입력하지 않는다. 
	 * @author gue
	 */
	protected final void setParamAfterFileCheck(String param, String path){
		if (path!= null && new File(path).exists())  _requestImageMap.put(param, path);
	}
	
	/**
	 * {@link #_requestMap},{@link #_requestImageMap}을 clear()하여 주고 {@link #_currentNetState}을 초기화 하여준다.<br>
	 * @author gue
	 */
	protected final void clearRequestMap() {
		_currentNetState = NOT_INIT_CONTROLLER;
		_requestMap.clear();
		_requestImageMap.clear();
	}
	
	/**
	 * 서버에 전송하여야할 parameter중에 공통적으로 적용되는 parameter를 {@link #_requestMap}에 삽입하는 부분을 작성한다.<br>
	 * 작성할때 필수로 이전에 전송된 parameter를 담고 있는 {@link #_requestMap}, {@link #_requestImageMap}의 내용을 
	 * {@link #clearRequestMap()}을 사용하여 초기화 하여주어야 한다.
	 * @param value 추가로 담을 parameter의 array 값
	 * @author gue
	 * @since 2012. 12. 21.
	 */
	protected abstract void setRequiredParam(String[] value);
	
/* ************************************************************************************************
 * INFO 통신 오류처리와 재시도 처리
 */
	/**
	 * true이면 network error시에 생성되는 알림창을 표시하지 않는다.
	 */
	private boolean _isDialogSkip;
	/**
	 * @param flag {@link #_isDialogSkip}의 값을 설정, true이면 network error시에 자동으로 생성되는 알림창을 표시하지 않는다.
	 * @author gue
	 * @since 2013. 6. 21.
	 */
	protected void setDialogSkip(boolean flag) {
		_isDialogSkip = flag;
	}
	protected boolean isDialogSkip() {
		return _isDialogSkip;
	}
	/**
	 * true일 경우 재시도 버튼 사용안함
	 */
	private boolean _noRetryButton = false;
	/**
	 * @param flag {@link #_noRetryButton}의 값을 설정, true일 경우 재시도에 버튼을 별도로 사용하지 않음
	 * @author gue
	 * @since 2013. 6. 21.
	 */
	protected void setNoRetryButton(boolean flag) {
		_noRetryButton = flag;
	}
	/**
	 * 재시도에 관련된 처리를 숨기고 해당 재시도를 하기 위한 boolean flag<br>
	 * 재시도에 대한 처리를 재시도 버튼 없이 하나의 버튼으로 갈때 AlertDialog에서 'ok'버튼에 재시도 처리를 걸어주고자 할때 true로 변경한다.
	 */
	private boolean _okRetryButton = false;
	/**
	 * @param flag {@link #_okRetryButton}의 값을 설정
	 * @author gue
	 * @since 2013. 6. 21.
	 */
	protected final void setOkRetryButton(boolean flag) {
		_okRetryButton = flag;
	}
	/**
	 * {@link #_noRetryButton}와 {@link #_okRetryButton}의 값을 초기화 한다.
	 * @author gue
	 * @since 2013. 6. 21.
	 * @history <ol>
	 * 		<li>변경자/날짜 : 변경사항</li>
	 * </ol>
	 */
	private final void initButtonFlag(){
		_noRetryButton = false;
		_okRetryButton = false;
	}
	/**
	 * {@link BaseControllerThread}에서 작업을 마친후 오류 처리와 재시도를 위한 handler
	 */
	private final Handler _controllerThreadHandler;
	
	
	/**
	 * {@link #_controllerThreadHandler}에 의하여 호출되며 network에대한 오류 처리를 한 후 abstract method인 {@link #handlerProcess2(Message)}에서 
	 * 해당 ntework작업에 대한 개별 작업에 대해 작성한다.<br>
	 * {@link #_currentNetState}에 서버에서 정의된 특정 code가 삽입 되지만 -1~-7과 1,2 값은 현재 controller에서 관리하는 값이다.
	 * 따라서 만약 서버의 코드값에 1과 2가 포함된다면 1 > -100, 2 > -200 으로 변환하여 {@link #_currentNetState}에 저장한다.<br>
	 * 만약 network에 대한 오류처리를 직접 하고 싶다면 현재 method를 Override하여 특정 에러에 대한 처리를 작성한다.<p>
	 * 에러 메세지 : {@link #ALERT_DEBUG}의 값이 false인 경우 아래와 같은 에러메세지가 출력이 되고 
	 * true라면 {@link #_currentNetState}에 정의된 exception을 dialog에 그대로 출력하게 된다.
	 * <dl>
	 * 		<li>-2 ~ -7 : "인터넷에 연결되어 있지 않습니다. wi-fi또는 3g의 연결상태를 확인해 주세요." {@link #_conFail}</li>
	 * 		<li>-1 : "인터넷 연결이 원할 하지 않습니다. 잠시후에 다시 시도하여 주세요." {@link #_netNotWake}</li>
	 * </dl>
	 * @param msg
	 * @author gue
	 * @see #_currentNetState
	 */
	private final void handlerProcess1(Message msg) {
		if (_isDialogSkip) {
			handlerProcess2(msg); 
		}
		else {
			if (ALERT_DEBUG) {
				switch (_currentNetState) {
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
					case EXCEPTION_IO:
						createDialog("IOException ex) socketException->서버가 응답하지 않을 경우", null);
						break;
					case EXCEPTION:
						createDialog("Net Exception", null);
						break;
					case EXCEPTION_JSON_PARSER:
						createDialog("jsonParser error", null);
						break;
					case EXCEPTION_NULL_RESPONSE:
						createDialog("responseJson is null", null);
						break;
						// TODO 확인하지 않은 에러일때 코드와 메세지를 그대로 보여준다.
					default :
						handlerProcess2(msg);
						break;
				}
			} else {
				switch (_currentNetState) {
					case EXCEPTION_NET_NOT_WAKE: 
						createDialog(_netNotWake, null);
						break;
					case NOT_INIT_CONTROLLER:
					case EXCEPTION_PARSE: 
					case EXCEPTION_CLIENT_PROTOCOL:
					case EXCEPTION_IO:
					case EXCEPTION:
					case EXCEPTION_JSON_PARSER:
					case EXCEPTION_NULL_RESPONSE:
						createDialog(_conFail, null);
						break;
					default :
						handlerProcess2(msg);
						break;
				}
			}
		}
	}
	
	/**
	 * {@link #handlerProcess1(Message)}에서 network에대한 exception이 처리된 뒤에 상속받은 class는 그 이후 작업에 관해 작성한다.
	 * @param msg
	 * @author gue
	 * @since 2012. 12. 21.
	 */
	protected abstract void handlerProcess2(Message msg); 
	
	private final DialogInterface.OnClickListener _retryListener = new DialogInterface.OnClickListener() {
		@Override
		public void onClick(DialogInterface dialog, int which) {
			retryClick();
		}
	};
	
	/**
	 * 오류가 있었을 시에 {@link #_requestImageMap}, {@link #_requestMap}에 담겨진 parameter와 value를 가지고 재시도를 시도
	 * @author gue
	 */
	protected final void retryClick() {
		initButtonFlag();
		new BaseControllerThread().netCheckBeforStart();
	}
	
	private final DialogInterface.OnClickListener _okListener = new DialogInterface.OnClickListener() {
		@Override
		public void onClick(DialogInterface dialog, int which) {
			okClick();
		}
	};
	
	/**
	 * 오류가 있었을 시에 {@link #_requestImageMap}, {@link #_requestMap}에 담겨진 parameter와 value를 가지고 재시도를 시도
	 * @author gue
	 */
	protected final void okClick() {
		initButtonFlag();
		okListenerWork();
	}
	
	/**
	 * {@link #_okRetryButton}, {@link #_noRetryButton}에 따른 dialog의 ok 버튼의 실행에 대한 내용 작성<br>
	 * 
	 * @author gue
	 * @since 2012. 12. 21.
	 */
	protected abstract void okListenerWork();
	
	/*protected void createAlert(int msg, int title) {
		createAlert( msg > 0 ? _context.getString(msg) : null, title > 0 ? _context.getString(title) : null);
	}*/
	
	/**
	 * BaseController에서 제공되는 기본 알림창을 사용하지 않고 custom 알림창 등을 사용하려고 한다면 이부분에 해당 작업을 작성하며 
	 * {@link #useCustomDialog()}를 자식 클래스의 생성자에서 사용하여 custom 알림창을 사용하는지에 대한 여부를 알려주어야 한다.<br>
	 * 알림창의 재시도 버튼은 {@link #retryClick()}, 확인버튼은 {@link #okClick()}을 각 버튼의 리스너에서 작업을 마친후 마지막 부분에서 필수로 호출해 주어야만 
	 * 그 후의 작업이 정상적으로 연결 될 수 있다.<br>
	 * @param noRetryButton {@link #_noRetryButton}
	 * @param okRetryButton {@link #_okRetryButton}
	 * @author gue
	 * @since 2013. 6. 21.
	 */
	protected abstract Dialog createCustomDialog(String msg, String title, String btOkName, String btRetryName, boolean noRetryButton, boolean okRetryButton);
	
	/**
	 * {@link AlertDialog}를 사용하여 버튼(최대 두개)와 msg, title을 가지고 있는 알림창을 설정한다.<br>
	 * @author gue
	 * @since 2012. 12. 21.
	 */
	protected final void createDialog(String msg, String title) {
		Dialog dialog = null;
		if (_useCustomDialog) {
			dialog = createCustomDialog(msg, title, _ok, _retry, _noRetryButton, _okRetryButton);
		}
		else {
			View view = ViewUtil.alertViewCenterAlign(msg, title, _context);
			AlertDialog.Builder builder = new AlertDialog.Builder(_context);
			builder.setView(view);
			if (_noRetryButton) {
				if (_okRetryButton) builder.setPositiveButton(_ok, _retryListener);
				else builder.setPositiveButton(_ok, _okListener);
			} 
			else {
				builder.setPositiveButton(_ok, _okListener);
				builder.setNegativeButton(_retry, _retryListener);
			}
			dialog = builder.create();
		}
		dialogShow(dialog);
	}
	
	private final void dialogShow(Dialog dialog){
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
	 * @author gue
	 * @since 2012. 12. 21.
	 */
	protected final String urlDecoder(String str) {
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
	
	/**
	 * response string 을 호출한 곳에서 사용할수 있는 형태의 DTO Object로 생성하여 return
	 * @param responsString response data
	 * @return DTO Object
	 * @author gue
	 * @since 2012. 12. 21.
	 */
	protected abstract Object parsing(String responsString, boolean singleDataMode) throws Exception;
	
	/**
	 * response data 안에 있는 string이 urlEncode가 되어있는 경우 urlDecode의 일괄 작업을 작성한다. 
	 * @param object
	 * @author gue
	 * @since 2012. 12. 21.
	 */
	protected abstract void urlDecode(Object object);
	
	/**
	 * 성공과 실패에 대한 {@link #_currentNetState}의 값을 설정하고 그에 따라 수행되는 작업을 작성한다.<br>
	 * 이부분이 BaseController의 Thread안에서 마지막으로 수행되는 작업이 되기 때문에 UI Thread를 호출하는 경우 Handler등을 이용하여 호출하여야 하며 
	 * 별도의 handler을 설정하지 않고 BaseController의 {@link #handlerProcess2(Message)}를 바로 호출하고 싶은 경우에는 
	 * {@link #callControllerHandler(int, Object)}를 호출한다. 
	 * @param returnObject {@link #parsing(String)}로 생성된 DTO object 
	 * @author gue
	 * @since 2012. 12. 21.
	 */
	protected abstract void controllerThreadWork(Object returnObject);
	
	/**
	 * @param what 
	 * @param returnObj
	 * @author gue
	 * @since 2013. 6. 21.
	 */
	protected final void callControllerHandler(int what, Object returnObj){
		Message msg = _controllerThreadHandler.obtainMessage();
		msg.what = _currentNetState;
		msg.obj = returnObj;
		_controllerThreadHandler.sendMessage(msg);
	}
	

/* ************************************************************************************************
 * INFO handler
 */
	private static class ControllerHandler extends Handler {
//		private WeakReference<BaseController> _controller;
		private CopyOfBaseController _controller;
		
		private ControllerHandler(CopyOfBaseController controller) {
			super();
//			_controller = new WeakReference<BaseController>(controller);
			_controller = controller;
		}

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			/*BaseController controller = _controller.get();
			if (controller != null) {
				controller.handlerProcess1(msg);
			}*/
			_controller.handlerProcess1(msg);
		}
	}
	
	
/* ************************************************************************************************
 * INFO BaseControllerThread
 */
	protected class BaseControllerThread extends CopyOfControllerThread {
		
		public BaseControllerThread() {
			super(_context);
			_currentNetState = NOT_INIT_CONTROLLER;
		}
		
		@Override
		protected void setMode(boolean singleDataMode, boolean enctypeMode) {
//			_singleDataMode = singleDataMode;
			_enctypeMode = enctypeMode;
		}
		
		@Override
		protected void netInitFail(int what) {
			_currentNetState = what;
			_controllerThreadHandler.sendEmptyMessage(what);
		}

		@Override
		public void run() {
			super.run();
			// 1. request 전송
			String responseStr = null;
			try {
				responseStr = sendRequest(_enctypeMode, _sendUrl, _requestMap, _requestImageMap);
			} catch (ParseException e) {
				Log.e(TAG, "ParseException : " + e.getMessage(), e);
				_currentNetState = EXCEPTION_PARSE;
			} catch (ClientProtocolException e) {
				Log.e(TAG, "ClientProtocolException : " + e.getMessage(), e);
				_currentNetState = EXCEPTION_CLIENT_PROTOCOL;
			} catch (IOException e) {
				Log.e(TAG, "IOException : " + e.getMessage(), e);
				_currentNetState = EXCEPTION_IO;
			} catch (Exception e) {
				Log.e(TAG, "Exception : " + e.getMessage(), e);
				_currentNetState = EXCEPTION;
			}
			
			// 2. response data parsing
			Object returnObject = null;
			if (responseStr != null){
				// response가 json일 경우 data의 구조가 배열, single 값인지에 대해 paser에 대한 결과값을 다르게 가져온다.
				try {
					returnObject = parsing(responseStr, _singleDataMode);
					_currentNetState = NET_SUCCESS;
//					returnObject = parsing(responseStr, _singleDataMode);
//					if (_singleDataMode) _currentNetState = NET_SUCCESS_SINGLE_MODE;
//					else _currentNetState = NET_SUCCESS_ARRAY_MODE;
				} catch (Exception e) {
					Log.e(TAG, "Exception : " + e.getMessage(), e);
					_currentNetState = EXCEPTION_JSON_PARSER;
				}
			} 
			else _currentNetState = EXCEPTION_NULL_RESPONSE;
			
			if (returnObject == null) _currentNetState = EXCEPTION_NULL_RESPONSE;
			
			// 3. 상태에 따른 _currentNetStatet값 설정 및 activity로 handler callback 
			if ( _currentNetState <= NOT_INIT_CONTROLLER ){
				// network error & parser error
				_controllerThreadHandler.sendEmptyMessage(_currentNetState);
			} 
			else if (returnObject != null) {
				urlDecode(returnObject);
				initButtonFlag();
				controllerThreadWork(returnObject);
			}
		} // run()
	} // BaseControllerThread
	
}