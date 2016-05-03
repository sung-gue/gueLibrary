package com.breakout.util.net;

import java.io.InputStream;

import android.content.Context;


/**
 * 
 * @author gue
 * @since 2012. 6. 17.
 * @copyright Copyright.2012.gue.All rights reserved.
 * @version 1.0
 * @history <ol>
 * 		<li>변경자/날짜 : 변경사항</li>
 * </ol>
 */
public abstract class Parsing {
	protected final String TAG = getClass().getSimpleName();
	
	/**
	 * instance 
	 */
	protected Parsing _parsing;
	
	protected Context _context;
	
	protected InputStream _responseXml;
	
	
	protected Parsing() {
	}
	
	
	/**
	 * parsing을 위하여 actiontype에따라 분류하여 작업
	 * @param responseXml HttpEntity.getContent()
	 */
	public abstract void responseParsing(InputStream responseXml) throws Exception;
	
}