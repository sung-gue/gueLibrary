package com.breakout.sample;



/**
 * log switch according to {@link Log#DEBUG }
 * @author gue
 * @since 2012. 10. 4.
 * @copyright Copyright.2011.gue.All rights reserved.
 * @version 
 * @history <ol>
 * 		<li>변경자/날짜 : 변경사항</li>
 * </ol>
 */
public final class Log {
	/**
	 * application tag
	 */
	private final static String TAG = "sample";
	
	/**
	 * <dl><li>true : test</li>
	 * <li>flase : deployment</li>
	 */
	private final static boolean DEBUG = Const.DEBUG;

	/** 
	 * base verbose<br> 
	 * tag name : {@link #TAG}
	 */
	public final static void v(String msg) {
		if (DEBUG) android.util.Log.v(TAG, msg);
	}
	
	/** 
	 * verbose tag<br>
	 */
	public final static void v(String tag, String msg) {
		if (DEBUG) android.util.Log.v(tag, msg);
	}
	
	/** 
	 * base debug<br>
	 * tag name : {@link #TAG}
	 */
	public final static void d(String msg) {
		if (DEBUG) android.util.Log.d(TAG, msg);
	}
	
	/** 
	 * debug tag<br>
	 */
	public final static void d(String tag, String msg) {
		if (DEBUG) android.util.Log.d(tag, msg);
	}

	/** 
	 * base info tag<br>
	 * tag name : {@link #TAG}
	 */
	public final static void i(String msg) {
		if (DEBUG) android.util.Log.i(TAG, msg);
	}
	
	/** 
	 * info tag <br>
	 */
	public static void i(String tag, String msg) {
		if (DEBUG) android.util.Log.i(tag, msg);
	}

	/** 
	 * base warn tag<br>
	 * tag name : {@link #TAG}
	 */
	public static void w(String msg) {
		if (DEBUG) android.util.Log.w(TAG, msg);
	}
	
	/** 
	 * warn tag<br>
	 */ 
	public static void w(String tag, String msg) {
		if (DEBUG) android.util.Log.w(tag, msg);
	}
	
	/** 
	 * base warn tag with throwable<br>
	 * tag name : {@link #TAG}
	 */
	public static void w(String msg, Throwable tr) {
		if (DEBUG) android.util.Log.w(TAG, msg, tr);
	}
	
	/** 
	 * warn tag with throwable<br>
	 */
	public static void w(String tag, String msg, Throwable tr) {
		if (DEBUG) android.util.Log.w(tag, msg, tr);
	}

	/** 
	 * base error tag<br>
	 * tag name : {@link #TAG}
	 */
	public static void e(String msg) {
		if (DEBUG) android.util.Log.e(TAG, msg);
	}
	
	/** 
	 * error tag<br>
	 */
	public static void e(String tag, String msg) {
		if (DEBUG) android.util.Log.e(tag, msg);
	}

	/** 
	 * base error tag with throwable<br>
	 * tag name : {@link #TAG}
	 */
	public static void e(String msg, Throwable tr) {
		if (DEBUG) android.util.Log.e(TAG, msg, tr);
	}
	
	/** error tag with throwable<br>
	 */
	public static void e(String tag, String msg, Throwable tr) {
		if (DEBUG) android.util.Log.e(tag, msg, tr);
	}
	
}