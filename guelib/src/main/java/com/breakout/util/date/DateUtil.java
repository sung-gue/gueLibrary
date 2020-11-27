package com.breakout.util.date;

import android.content.Context;
import android.text.TextUtils;
import android.text.format.DateUtils;

import com.breakout.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Date Util
 *
 * @author sung-gue
 * @version 1.0 (2015. 3. 11.)
 */
public final class DateUtil {
    private static final String TAG = "DateUtil";
    private static final Locale DEFAULT_LOCALE = Locale.getDefault();

    /**
     * SimpleDateFormat patterns
     * <ol>
     *      <li>yy/M/d > 12/8/13</li>
     *      <li>yyyy/MM/dd > 2012/08/13</li>
     *      <li>yyyy.M.d > 2012.8.13</li>
     *      <li>yyyy.MM.dd > 2012.08.13</li>
     *      <li>yyyy-M-d > 2012-8-13</li>
     *      <li>yyyy-MM-dd > 2012-8-13</li>
     *      <li>yyyy.M.d HH:mm > 2012.8.13 02:20</li>
     *      <li>yyyy.M.d H:m > 2012.8.13 2:20</li>
     *      <li>yyyy.MM.dd HH:mm > 2012.08.13 02:20</li>
     *      <li>yyyy.MM.dd HH:mm:ss > 2012.08.13 02:20:15</li>
     *      <li>MM/dd > 08/23</li>
     *      <li>MM-dd > 08-23</li>
     *      <li>MM/dd HH:mm > 08/23 02:20</li>
     *      <li>yyyy년 MM월 dd일 E요일 > 2012년 08월 13일 수요일</li>
     *      <li>a h:mm > 오전 2:20</li>
     *      <li>a hh:mm > 오전 02:20</li>
     *      <li>yyyy > 2012</li>
     *      <li>yyyy.MM > 2012.08</li>
     *      <li>HH:mm:ss > 02:20:15</li>
     *      <li>yyyy-MM-dd HH:mm:ss > 2012-08-13 02:20:15</li>
     *      <li>yy.MM.dd > 12.08.13</li>
     *      <li>HH:mm > 02:20</li>
     * </ol>
     */
    public static SimpleDateFormat[] DateFormat = new SimpleDateFormat[]{
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
            new SimpleDateFormat("HH:mm:ss", DEFAULT_LOCALE),
            new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", DEFAULT_LOCALE),
            new SimpleDateFormat("yy.MM.dd", DEFAULT_LOCALE),
            new SimpleDateFormat("HH:mm", DEFAULT_LOCALE),
    };

    public static Date getDate(String format, String dateStr) throws Exception {
        SimpleDateFormat formatter = new SimpleDateFormat(format, DEFAULT_LOCALE);
        return formatter.parse(dateStr);
        /*Date date = null;
        try {
            date = formatter.parse(dateStr);
        } catch (ParseException e) {
            Log.e(TAG, e.getMessage(), e);
        }
        return date;*/
    }

    /**
     * Date type을 다른 형식의 문자열로 변환
     *
     * @param inputDate null일 경우 "0000"을 return
     * @param form      {@link #DateFormat}
     */
    public static String dateFormat(Date inputDate, int form) {
        String result = "0000";
        if (inputDate != null) {
            result = DateFormat[form].format(inputDate);
        }
        return result;
    }

    /**
     * format에 맞는 형식의 날짜를 다른 날짜 형식으로 변환
     *
     * @param format  date pattern ex) 'yyyyMMddHHmmss'
     * @param dateStr format의 형식에 맞는 날짜
     * @param form    {@link #DateFormat}
     * @return dateStr이 형식에 맞지 않는경우 "0000"을 return
     */
    public static String dateFormat(String format, String dateStr, int form) {
        Date date = null;
        try {
            date = getDate(format, dateStr);
        } catch (Exception e) {
            Log.w(TAG, e.getMessage(), e);
        }
        return dateFormat(date, form);
    }

    /**
     * Date type을 다른 형식으로 변환<br>
     * 10분 이전은 "방금전" return<br>
     * period로 입력받은 시간까지만 경과 기간에 따라서 '분전', '시간전', '일전', '달전' 으로 표시된다.
     *
     * @param inputDate null일 경우 "0000"을 return
     * @param form      {@link #DateFormat}
     * @param limitHour 시간단위 : 지정한 시간까지만 ago 형식으로 변환한다.
     * @param suffixes  new String[]{"방금전", "%s분전", "%s시간전", "%s일전", "%s달전"}
     * @author gue
     * @see #dateFormat(String, int)
     * @since 2012. 11. 22.
     */
    public static String dateAgoFormat(Date inputDate, int form, int limitHour, String[] suffixes) {
        String result = "0000";
        if (inputDate != null) {
            if (suffixes == null || suffixes.length != 5) {
                suffixes = new String[]{"방금전", "%s분전", "%s시간전", "%s일전", "%s달전"};
            }
            Date currentDate = new Date();

            long minute = (currentDate.getTime() - inputDate.getTime()) / 1000 / 60;
            long hour = minute / 60;
            long day = hour / 24;
            long month = day / 30;

            // "방금전"
            if (minute < 10 && limitHour > 0) {
                result = suffixes[0];
            }
            // "분전"
            else if (minute < 60 && limitHour > 0) {
                result = String.format(suffixes[1], minute);
            }
            // "시간전"
            else if (hour < 24 && hour < limitHour) {
                result = String.format(suffixes[2], hour);
            }
            // "일전"
            else if (day < 30 && day < (limitHour / 24)) {
                result = String.format(suffixes[3], day);
            }
            // "달전"
            else if (month < 4 && month < (limitHour / 24 / 30)) {
                result = String.format(suffixes[4], month);
            } else {
                result = dateFormat(inputDate, form);
            }
        }
        return result;
    }

    /**
     * form 에 해당하는 형식의 날짜로 변환<br>
     * 10분 이전은 "방금전" return<br>
     * period로 입력받은 시간까지만 경과 기간에 따라서 '시간전', '일전', '달전' 으로 표시된다.
     *
     * @param dateStr   14글자의 시간 -> "yyyyMMddHHmmss", 형식에 맞지 않는경우 "0000"을 return
     * @param form      {@link #DateFormat}
     * @param limitHour 시간단위 : 지정한 시간까지만 ago 형식으로 변환한다.
     * @see #dateAgoFormat(Date, int, int, String[])
     */
    @Deprecated
    public static String dateAgoFormat(String dateStr, int form, int limitHour) {
        Date date = null;
        try {
            date = getDate("yyyyMMddHHmmss", dateStr);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        }
        return dateAgoFormat(date, form, limitHour, null);
    }

    /**
     * inputDate까지 남은 시간 계산
     *
     * @return new long[] {day, hour, minute, second, millisecond}, 오류가 있다면 모든 값은 0으로 설정
     */
    public static long[] getLimitDate(Date inputDate) {
        long[] result = new long[]{0, 0, 0, 0, 0};
        if (inputDate != null) {
            try {
                Date currentDate = new Date();

                long millisecond = inputDate.getTime() - currentDate.getTime();
                long second = millisecond / 1000;
                long minute = second / 60;
                long hour = minute / 60;
                long day = hour / 24;
                second = second % 60;
                minute = minute % 60;
                hour = hour % 24;

                result = new long[]{day, hour, minute, second, millisecond};
            } catch (Exception e) {
                Log.e(TAG, e.getMessage(), e);
            }
        }
        return result;
    }

    /**
     * 두 날짜 차이값 비교 : date1 - date2
     *
     * @return long type의 시간 차이, 단위 : 1/1000초
     */
    public static long getDiffDate(Date date1, Date date2) {
        return (date1.getTime() - date2.getTime());
    }

    /**
     * 두 날짜 차이값 비교 : date1 - date2
     *
     * @param day1 일자1 : 8글자 -> "yyyyMMdd"
     * @param day2 일자2 : 8글자 -> "yyyyMMdd"
     * @return long type : 1/1000초단위
     */
    @Deprecated
    public static long getDiffDate(String day1, String day2, String format) {
        long result = 0;
        if (!TextUtils.isEmpty(day1) && !TextUtils.isEmpty(day2)) {
            try {
                result = getDiffDate(getDate(format, day1), getDate(format, day2));
            } catch (Exception e) {
                Log.e(TAG, e.getMessage(), e);
            }
        }
        return result;
    }

    /**
     * @return 오늘 날짜가 시작시간과 종료시간 사이에 있다면 true
     */
    public static boolean isDateEnable(Date startDate, Date endDate) {
        boolean result = false;
        try {
            Date currentDate = new Date();
            if (currentDate.after(startDate) && currentDate.before(endDate)) {
                result = true;
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        }
        return result;
    }

    /**
     * @param startDateStr 시작시간 -> "yyyyMMddHHmmss"
     * @param endDateStr   종료시간 -> "yyyyMMddHHmmss"
     * @return 오늘 날짜가 시작시간과 종료시간 사이에 있다면 true
     */
    @Deprecated
    public static boolean isDateEnable(String startDateStr, String endDateStr) {
        boolean result = false;
        try {
            String format = "yyyyMMddHHmmss";
            result = isDateEnable(getDate(format, startDateStr), getDate(format, endDateStr));
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        }
        return result;
    }

    /**
     * 현재 날짜
     *
     * @return 월 일 오전/오후 시간:분
     */
    @Deprecated
    public static String getNowDate(Context context) {
        return DateUtils.formatDateTime(
                context,
                System.currentTimeMillis(),
                DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL);
    }

    /**
     * "yyyyMMddHHmmss" pattern 형식의 날짜를 다른 날짜 형식으로 변환한다.
     *
     * @param dateStr format의 형식에 맞는 날짜
     * @param form    {@link #DateFormat}
     * @return dateStr이 형식에 맞지 않는경우 "0000"을 return
     */
    @Deprecated
    public static String dateFormat(String dateStr, int form) {
        return dateFormat("yyyyMMddHHmmss", dateStr, form);
    }
}