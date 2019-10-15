package com.breakout.util.net.controller;

import java.io.IOException;
import java.util.HashMap;

import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;

import android.content.Context;

import com.breakout.util.net.BaseNet;


/**
 * {@link BaseController} 의 thread에서 사용하는 method가 정의된 abstract class
 * @author gue
 * @since 2012. 12. 21.
 * @copyright Copyright.2011.gue.All rights reserved.
 * @version 1.0
 * @history <ol>
 * 		<li>변경자/날짜 : 변경사항</li>
 * </ol>
 */
public abstract class CopyOfControllerThread extends Thread {
	private Context _context;
	
	
	protected CopyOfControllerThread(Context context) {
		_context = context;
	}
	
	/**
	 * current thread start()<br>
	 * enctype fix - text/plain<br>
	 * {@link BaseController#_currentEnctype} = false<br>
	 * @param singleDataMode response가 json일경우 true-> data{} : single, false-> data[{}] : array<br>{@link #_singleDataMode}
	 * @author gue
	 * @since 2012. 12. 21.
	 */
	public void begin(boolean singleDataMode) {
		setMode(singleDataMode, false);
		netCheckBeforStart();
	}
	
	/**
	 * current thread start()<br>
	 * enctype - text/plain<br>
	 * {@link BaseController#_singleDataMode} = true, {@link BaseController#_currentEnctype} = false<br>
	 * @author gue
	 * @since 2012. 12. 21.
	 */
	public void begin() {
		begin(true);
	}
	
	/**
	 * current thread start()<br>
	 * json일경우 - data[{}] : array<br>
	 * enctype - text/plain<br>
	 * {@link BaseController#_singleDataMode} = false, {@link BaseController#_currentEnctype} = false<br>
	 * @author gue
	 * @since 2012. 8. 2.
	 */
	public void beginArrayMode() {
		begin(false);
	}

	
	/**
	 * current thread start()<br>
	 * enctype fix - multipart/form-data<br>
	 * {@link BaseController#_singleDataMode} = true, {@link BaseController#_currentEnctype} = true<br>
	 * @author gue
	 * @since 2012. 12. 21.
	 */
	public void beginMultpart() {
		beginMultpart(true);
	}
	
	/**
	 * current thread start()<br>
	 * enctype fix - multipart/form-data<br>
	 * {@link BaseController#_currentEnctype} = true<br>
	 * @param singleDataMode see {@link BaseController#_singleDataMode} 
	 * @author gue
	 * @since 2012. 12. 21.
	 */
	public void beginMultpart(boolean singleDataMode) {
		setMode(singleDataMode, true);
		netCheckBeforStart();
	}
	
	/**
	 * network thred를 시작하기 전에 network이 가능한 상태인지를 check하고 
	 * network을 사용할 수 없는 상태일때에는 {@link #_controllerThreadHandler}를 callback하여 오류 및 재시도에 대한 처리를 하여 준다. 
	 * @author gue
	 * @since 2012. 12. 21.
	 */
	protected void netCheckBeforStart(){
		if (BaseNet.getInstance().getNetState(_context) < 0 ) netInitFail(CopyOfBaseController.EXCEPTION_NET_NOT_WAKE);
		else start();
	}
	
	/**
	 * network 초기 오류 일때 실행될 작업 작성, 해당시에는 {@link BaseController#_what} = {@link BaseController#EXCEPTION_NET_NOT_WAKE}이 기본 설정으로 입력된다.
	 * @param see {@link BaseController#_what}
	 * @author gue
	 * @since 2012. 12. 26.
	 */
	protected abstract void netInitFail(int what);
	
	/**
	 * {@link BaseController#_singleDataMode}, {@link BaseController#_currentEnctype} 설정
	 * @param singleDataMode see {@link BaseController#_singleDataMode}
	 * @param enctypeMode see {@link BaseController#_singleDataMode}
	 * @author gue
	 * @since 2012. 12. 26.
	 */
	protected abstract void setMode(boolean singleDataMode, boolean enctypeMode);
	
	/**
	 * send request
	 * @param sendURL 접속 url
	 * @param requestMap parameter map : stringBody for text/plain enctype
	 * @param requestImageMap parameter map : fileBody for multipart/form-data enctype
	 * @return response string
	 * @author gue
	 * @since 2012. 12. 21.
	 */
	protected String sendRequest(boolean enctypeMode, String sendURL, HashMap<String,String> requestMap, HashMap<String,String> requestImageMap) 
			throws ParseException, ClientProtocolException, IOException, Exception {
		String response = null;
		// enctype에 따른 전송 방식 결정
		if (enctypeMode) {
			response = BaseNet.getInstance().sendMultiPart(sendURL, null, requestMap, requestImageMap);
		} else {
			response = BaseNet.getInstance().sendPost(sendURL, null, requestMap);
		}
		return response;
	}

}
