package com.breakout.util.date;

import android.content.Context;
import android.text.format.DateUtils;

import com.breakout.util.Log;
import com.breakout.util.Util;
import com.breakout.util.string.StringUtil;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * {@link Util}의 method를 속성에 따라 class로 분리<br/>
 * String Util
 * @author gue
 * @since 2015. 3. 11.
 * @copyright Copyright.2012.gue.All rights reserved.
 * @version 1.0
 * @history <ol>
 * 		<li>변경자/날짜 : 변경사항</li>
 * </ol>
 */
public final class DateUtil {
	private final static String TAG = "DateUtil";
	private final static Locale DEFAULT_LOCALE = Locale.getDefault();

	private DateUtil(){}
	
	
	/**
	 * SimpleDateFormat patterns
	 * <ol>	
	 * 		<li>yy/M/d > 12/8/13</li>
	 * 		<li>yyyy/MM/dd > 2012/08/13</li>
	 * 		<li>yyyy.M.d > 2012.8.13</li>
	 * 		<li>yyyy.MM.dd > 2012.08.13</li>
	 * 		<li>yyyy-M-d > 2012-8-13</li>
	 * 		<li>yyyy-MM-dd > 2012-8-13</li>
	 * 		<li>yyyy.M.d HH:mm > 2012.8.13 02:20</li>
	 * 		<li>yyyy.M.d H:m > 2012.8.13 2:20</li>
	 * 		<li>yyyy.MM.dd HH:mm > 2012.08.13 02:20</li>
	 * 		<li>yyyy.MM.dd HH:mm:ss > 2012.08.13 02:20:15</li>
	 * 		<li>MM/dd > 08/23</li>
	 * 		<li>MM-dd > 08-23</li>
	 * 		<li>MM/dd HH:mm > 08/23 02:20</li>
	 * 		<li>yyyy년 MM월 dd일 E요일 > 2012년 08월 13일 수요일</li>
	 * 		<li>a h:mm > 오전 2:20</li>
	 * 		<li>a hh:mm > 오전 02:20</li>
	 * 		<li>yyyy > 2012</li>
	 * 		<li>yyyy.MM > 2012.08</li>
	 * 	</ol>
	 * @author gue
	 * @since 2012. 8. 13.
	 */
	public final static SimpleDateFormat[] DateFormat= new SimpleDateFormat[]{
			new SimpleDateFormat("0000", DEFAULT_LOCALE),
			new SimpleDateFormat("yy/M/d", DEFAULT_LOCALE),
			new SimpleDateFormat("yyyy/MM/dd", DEFAULT_LOCALE),
			new SimpleDateFormat("yyyy.M.d", DEFAULT_LOCALE),
			new SimpleDateFormat("yyyy.MM.dd", DEFAULT_LOCALE),
			new SimpleDateFormat("yyyy-M-d", DEFAULT_LOCALE),
			new SimpleDateFormat("yyyy-MM-dd", DEFAULT_LOCALE),
			new SimpleDateFormat("yyyy.M.d HH:mm", DEFAULT_LOCALE),
			new SimpleDateFormat("yyyy.M.d H:m", DEFAULT_LOCALE),
			new SimpleDateFormat("yyyy.MM.dd HH:mm", DEFAULT_LOCALE),
			new SimpleDateFormat("yyyy.MM.dd HH:mm:ss", DEFAULT_LOCALE),
			new SimpleDateFormat("MM/dd", DEFAULT_LOCALE),
			new SimpleDateFormat("MM-dd", DEFAULT_LOCALE),
			new SimpleDateFormat("MM/dd HH:mm", DEFAULT_LOCALE),
			new SimpleDateFormat("yyyy년 MM월 dd일 E요일", DEFAULT_LOCALE),
			new SimpleDateFormat("a h:mm", DEFAULT_LOCALE),
			new SimpleDateFormat("a hh:mm", DEFAULT_LOCALE),
			new SimpleDateFormat("yyyy", DEFAULT_LOCALE),
			new SimpleDateFormat("yyyy.MM", DEFAULT_LOCALE),
	};
	
	
	/**
	 * 현재 날짜 
	 * @return 월 일 오전/오후 시간:분 
	 * @author gue
	 * @since 2012. 8. 13.
	 */
	public final static String getNowDate(Context context){
		return DateUtils.formatDateTime(
				context, 
				System.currentTimeMillis(), 
				DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL);
	}
	
	/** 
	 * "yyyyMMddHHmmss" pattern 형식의 날짜를 다른 날짜 형식으로 변환한다.
	 * @param dateStr format의 형식에 맞는 날짜
	 * @param form {@link #DateFormat}
	 * @return dateStr이 형식에 맞지 않는경우 "0000"을 return
	 * @author gue
	 * @since 2012. 8. 13.
	 * @see #dateFormat(String, String, int)
	 */
	public final static String dateFormat(String dateStr, int form) {
		return dateFormat("yyyyMMddHHmmss", dateStr, form);
	}
	
	/**
	 * format에 맞는 형식의 날짜를 다른 날짜 형식으로 변환한다.
	 * @param format date pattern ('yyyyMMddHHmmss')
	 * @param dateStr format의 형식에 맞는 날짜
	 * @param form {@link #DateFormat}
	 * @return dateStr이 형식에 맞지 않는경우 "0000"을 return
	 * @author gue
	 * @since 2012. 8. 13.
	 */
	public final static String dateFormat(String format, String dateStr, int form) {
		String result = "0000";
		if (StringUtil.nullCheckB(dateStr)) {
			try {
				SimpleDateFormat formatter = new SimpleDateFormat(format, DEFAULT_LOCALE);
				Date inputDate = formatter.parse(dateStr);
				result = DateFormat[form].format(inputDate);
			} catch (Exception e) { 
				Log.e(TAG, "Exception - " + e.getMessage(), e);
				result = "0000";
			}
		}
		return result;
	}
	
	/**
	 * Date type을 다른 형식의 문자열로 변환
	 * @param form {@link #DateFormat}
	 * @param date null일 경우 "0000"을 return
	 * @author gue
	 * @since 2012. 11. 22.
	 * @see #dateFormat(String, int)
	 */
	public final static String dateFormat(Date date, int form) {
		String result = "0000";
		if (date != null) {
			SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss", DEFAULT_LOCALE);
			result = dateFormat(formatter.format(date), form);
		}
		return result;
	}
	
	/**
	 * 14자의 string을 날짜로 변환하여준다.<br>
	 * 10분 이전은 "방금전" return<br>
	 * period로 입력받은 시간까지만 경과 기간에 따라서 '시간전', '일전', '달전' 으로 표시된다.
	 * @param dateStr 14글자의 시간 -> "yyyyMMddHHmmss", 형식에 맞지 않는경우 "0000"을 return
	 * @param form {@link #DateFormat}
	 * @param periodHour 시간단위 : 지정한 시간까지만 ago 형식으로 변환한다. 
	 * @author gue
	 * @since 2012. 8. 13.
	 * @see #dateFormat(String, int)
	 */
	public final static String dateAgoFormat(String dateStr, int form, int periodHour) {
		String result = "0000";
		if (StringUtil.nullCheckB(dateStr) && dateStr.length() == 14) {
			try {
				SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss", DEFAULT_LOCALE);
				Date inputDate = formatter.parse(dateStr);
				Date currentDate = new Date();
				
				double minute = (currentDate.getTime() - inputDate.getTime()) / 1000.0 / 60.0 ;
				double hour = minute / 60.0 ;
				double day = hour / 24.0 ; 
				double month = day / 30.0 ; 
				
				if ( minute < 10 && periodHour > 0) {
					result = "방금전";
				} 
				else if ( minute < 60 && periodHour > 0) {
					result = (int) minute + "분전";
				} 
				else if ( hour < 24 && hour <= periodHour) {
					result = (int) hour + "시간전";
				} 
				else if ( day < 30 && day <= periodHour/24.0) {
					result = (int) day + "일전";
				} 
				else if ( month < 4 && month <= periodHour/24.0/30.0) {
					result = (int) month + "달전";
				} 
				else {
					result = dateFormat(dateStr, form);
				}
			} catch (Exception e) {
				Log.e(TAG, "Exception - " + e.getMessage(), e);
				result = "0000";
			}
		}
		return result;
	}
	
	/**
	 * Date type을 다른 형식으로 변환<br>
	 * 10분 이전은 "방금전" return<br>
	 * period로 입력받은 시간까지만 경과 기간에 따라서 '시간전', '일전', '달전' 으로 표시된다.
	 * @param date null일 경우 "0000"을 return
	 * @param form {@link #DateFormat}
	 * @param periodHour 시간단위 : 지정한 시간까지만 ago 형식으로 변환한다.
	 * @author gue
	 * @since 2012. 11. 22.
	 * @see #dateFormat(String, int)
	 */
	public final static String dateAgoFormat(Date date, int form, int periodHour) {
		String result = "0000";
		if (date != null) {
			SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss", DEFAULT_LOCALE);
			result = dateAgoFormat(formatter.format(date), form, periodHour);
		}
		return result;
	}
	
	/**
	 * 두 날짜 차이값 비교 : date1 - date2
	 * @param format 
	 * @param day1 일자1 : 8글자 -> "yyyyMMdd"
	 * @param day2 일자2 : 8글자 -> "yyyyMMdd"
	 * @return long type : 1/1000초단위
	 * @author gue
	 * @since 2012. 8. 13.
	 */
	public final static long getDiffDate (String day1, String day2, String format) {
			long retVal = 0;
			if ( StringUtil.nullCheck(day1) != null && StringUtil.nullCheck(day2) != null ) {
				SimpleDateFormat df = new SimpleDateFormat(format, DEFAULT_LOCALE);
				Date date1 = df.parse(day1, new ParsePosition(0));
				Date date2 = df.parse(day2, new ParsePosition(0));
				retVal = getDiffDate(date1, date2);
			}
			return retVal;
	}
	
	/**
	 * 두 날짜 차이값 비교 : date1 - date2
	 * @return long type의 시간 차이, 단위 : 1/1000초
	 * @author gue
	 * @since 2012. 8. 13.
	 */
	public final static long getDiffDate (Date date1, Date date2) {
		return (date1.getTime() - date2.getTime());
	}

	/**
	 * @param startDateStr 시작시간 -> "yyyyMMddHHmmss"
	 * @param endDateStr 종료시간 -> "yyyyMMddHHmmss"
	 * @return 오늘 날짜가 시작시간과 종료시간 사이에 있다면 true
	 * @author gue
	 * @since 2013. 11. 7.
	 */
	public final static boolean isDateEnable(String startDateStr, String endDateStr) {
		boolean result = false;
		try {
			String format = "yyyyMMddHHmmss";
			SimpleDateFormat formatter = new SimpleDateFormat(format, Locale.getDefault());
			Date currentDate = new Date();
			Date startDate = formatter.parse(startDateStr);
			Date endDate = formatter.parse(endDateStr);
			if ( 	currentDate.getTime() > startDate.getTime()	&&
					currentDate.getTime() < endDate.getTime() 	) {
				result = true;
			}
		} catch (Exception e) {
			Log.e(TAG, "Exception - " + e.getMessage(), e);
		}
		return result;
	}
}