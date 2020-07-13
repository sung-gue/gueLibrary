package com.breakout.util.string;

import android.text.InputFilter;
import android.text.Spanned;

import com.breakout.util.Log;
import com.breakout.util.Util;

import java.text.DecimalFormat;
import java.util.Locale;
import java.util.regex.Pattern;


/**
 * {@link Util}의 method를 속성에 따라 class로 분리<br/>
 * String Util
 *
 * @author gue
 * @version 1.0
 * @copyright Copyright.2012.gue.All rights reserved.
 * @history <ol>
 * <li>변경자/날짜 : 변경사항</li>
 * </ol>
 * @since 2015. 3. 11.
 */
public final class StringUtil {
    private final static String TAG = "StringUtil";
    private final static Locale DEFAULT_LOCALE = Locale.getDefault();

    private StringUtil() {
    }

    /**
     * ""값을 null 로 교체, ""가 아니고 null이 아니라면 값을 그대로 return
     *
     * @author gue
     * @since 2011. 10. 16.
     */
    @Deprecated
    public final static String nullCheck(String str) {
        if (str != null && str.length() != 0) return str;
        else return null;
    }

    /**
     * ""나 null 일경우 false return
     *
     * @author gue
     * @since 2011. 10. 16.
     */
    @Deprecated
    public final static boolean nullCheckB(String str) {
        return str != null && str.length() != 0;
    }

    /**
     * 해당 object가 특정 class의 instance인지 check
     *
     * @author gue
     * @since 2011. 10. 16.
     */
    public static boolean instanceCheck(Object obj, Class<?> instance) {
        return instance.isInstance(obj);
    }

    /**
     * String 로 된 정수를 특정 형식으로 변환한다.
     * 4자리 까지는 그대로 표현, 만단위를 넘어가면 000K, 백만단위는 000M, 십억단위는 000B로 변환하여 준다.
     *
     * @param count string number
     * @param form  <ol>
     *              <li>천 - 000K, 백만 - 000M, 십억 - 000B</li>
     *              <li>천 - 000.0K, 백만 - 000.0M, 십억 - 000.0B</li>
     *              </ol>
     * @return 숫자로 이루어진 값이 아니거나 null값일 경우 :  '0'으로 설정
     * @author gue
     * @since 2012. 7. 19.
     */
    public static String convertNumberNotation(String count, int form) {
        String result = "0";
        if (isValidNumber_1(count)) {
            try {
                double cnt = Double.parseDouble(count);
                String suffix = "";
                if (cnt > 9999) {
                    suffix = "K";
                    cnt /= 1000.0;
                    if (cnt > 999) {
                        suffix = "M";
                        cnt /= 1000.0;
                        if (cnt > 999) {
                            suffix = "B";
                            cnt /= 1000.0;
                        }
                    }
                }

                DecimalFormat df = new DecimalFormat();
                switch (form) {
                    case 1:
                        df.applyPattern("0" + suffix);
                        break;
                    case 2:
                        df.applyPattern("0.#" + suffix);
                        break;
                }
                result = df.format(cnt);

            } catch (Exception e) {
                Log.e(TAG, "Exception : " + e.fillInStackTrace());
            }
        }
        return result;
    }

    /**
     * 파일의 사이즈를 byte 길이로 입력받아 kb,mb,gb,tb 의 단위로 변환후 단위를 붙혀 String으로 return한다.<br>
     * {@link DecimalFormat}의 pattern은 ",###.#"을 사요한다. pattern의 변경을 위해서는 {@link #convertFileSizeFormat(double, String)}을 사용한다.
     *
     * @author gue
     * @since 2012. 10. 5.
     */
    public static String convertFileSizeFormat(double byteSize) {
        return convertFileSizeFormat(byteSize, ",###.#");
    }

    /**
     * see {@link #convertFileSizeFormat(double)}
     *
     * @author gue
     * @since 2013. 1. 23.
     */
    public static String convertFileSizeFormat(double byteSize, String pattern) {
        String suffix = "B";
        if (byteSize >= 1024) {
            suffix = "KB";
            byteSize /= 1024;
            if (byteSize >= 1024) {
                suffix = "MB";
                byteSize /= 1024;
                if (byteSize >= 1024) {
                    suffix = "GB";
                    byteSize /= 1024;
                    if (byteSize >= 1024) {
                        suffix = "TB";
                        byteSize /= 1024;
                    }
                }
            }
        }
        return new DecimalFormat(pattern + suffix).format(byteSize);
    }

    /**
     * 금액 표현
     *
     * @param price             금액
     * @param moneyUnit         화폐 단위
     * @param isMoneyUnitSuffix 화폐단위가 금액 뒤에 붙는다면 true
     * @author gue
     * @since 2014. 1. 14.
     */
    public static String changeMoneyFormat(int price, String moneyUnit, boolean isMoneyUnitSuffix) {
        String result = null;
        DecimalFormat format = null;
        try {
            if (isMoneyUnitSuffix) format = new DecimalFormat(",###.#" + moneyUnit);
            else format = new DecimalFormat(moneyUnit + ",###.#");
            result = format.format(price);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        }
        return result;
    }

    /**
     * 금액 표현
     *
     * @param price             금액
     * @param moneyUnit         화폐 단위
     * @param isMoneyUnitSuffix 화폐단위가 금액 뒤에 붙는다면 true
     * @author gue
     * @since 2014. 1. 14.
     */
    public static String changeMoneyFormat(float price, String moneyUnit, boolean isMoneyUnitSuffix) {
        String result = null;
        DecimalFormat format = null;
        try {
            if (isMoneyUnitSuffix) format = new DecimalFormat(",###.#" + moneyUnit);
            else format = new DecimalFormat(moneyUnit + ",###.#");
            result = format.format(price);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        }
        return result;
    }

    /* ************************************************************************************************
     * INFO pattern
     */
    /**
     * Pattern ","
     */
    public final static Pattern COMMA_PATTERN = Pattern.compile(",");
    /**
     * Pattern "&"
     */
    public final static Pattern AND_PATTERN = Pattern.compile("&");
    /**
     * Pattern "+"
     */
    public final static Pattern EQUAL_PATTERN = Pattern.compile("=");
    /**
     * Pattern "/"
     */
    public final static Pattern SLASH_PATTERN = Pattern.compile("/");
    /**
     * Pattern "|"
     */
    public final static Pattern BAR_PATTERN = Pattern.compile("[|]");

    /**
     * 1. id validation : 최소 최대 자릿수 설정 : 한글, 영소문자, 숫자
     *
     * @author gue
     * @since 2012. 9. 1.
     */
    public static boolean isValid_1(String value, int minLenth, int maxLenth) {
        if (nullCheck(value) != null)
            return Pattern.compile(String.format(DEFAULT_LOCALE, "^[가-힣a-z0-9]{%d,%d}$", minLenth, maxLenth)).matcher(value).matches();
        else return false;
    }

    /**
     * 2. id validation : 최소 최대 자릿수 설정 : 한글, 영대소문자, 숫자
     *
     * @author gue
     * @since 2012. 9. 1.
     */
    public static boolean isValid_2(String value, int minLenth, int maxLenth) {
        if (nullCheck(value) != null)
            return Pattern.compile(String.format(DEFAULT_LOCALE, "^[가-힣a-zA-Z0-9]{%d,%d}$", minLenth, maxLenth)).matcher(value).matches();
        else return false;
    }

    /**
     * 3. password validation : 최소 최대 자릿수 설정 : 영문소문자, 숫자
     *
     * @author gue
     * @since 2012. 9. 1.
     */
    public static boolean isValid_3(String value, int minLenth, int maxLenth) {
        if (nullCheck(value) != null)
            return Pattern.compile(String.format(DEFAULT_LOCALE, "^[a-z0-9]{%d,%d}$", minLenth, maxLenth)).matcher(value).matches();
        else return false;
    }

    /**
     * 4. password validation : 최소 최대 자릿수 설정 : 영문소문자, 숫자 조합
     *
     * @author gue
     * @since 2012. 10. 2.
     */
    public static boolean isValid_4(String value, int minLenth, int maxLenth) {
        boolean result = false;
        if (nullCheck(value) != null) {
            if (value.matches("^\\p{Alnum}{" + minLenth + "," + maxLenth + "}")) {
                if (!value.matches("^\\p{Alpha}{6,12}") && !value.matches("^\\p{Digit}{6,12}")) {
                    result = true;
                }
            }
        }
        return result;
    }

    /**
     * 5. password validation : 자릿수 고정 : 영문 소문자, 숫자
     *
     * @author gue
     * @since 2012. 9. 1.
     */
    public static boolean isValid_5(String value, int fixLenth) {
        if (nullCheck(value) != null)
            return Pattern.compile(String.format(DEFAULT_LOCALE, "^[a-z0-9]{%d}$", fixLenth)).matcher(value).matches();
        else return false;
    }

    /**
     * email validation
     *
     * @author gue
     * @since 2012. 9. 1.
     */
    public static boolean isValidEmail(String value) {
        if (nullCheck(value) != null)
            return Pattern.compile("^[\\w-_]+.*[\\w-_]*@(\\w+\\.)+\\w+$").matcher(value).matches();
        else return false;
    }

    /**
     * 1. 숫자로만 이루어지는 pattern
     *
     * @author gue
     * @since 2012. 9. 1.
     */
    public static boolean isValidNumber_1(String value) {
        if (nullCheck(value) != null) return Pattern.compile("^[0-9]*$").matcher(value).matches();
        else return false;
    }

    /**
     * 2. 숫자로만 이루어지고 중간에 '.'이 들어가는 pattern
     *
     * @author gue
     * @since 2012. 9. 1.
     */
    public static boolean isValidNumber_2(String value) {
        if (nullCheck(value) != null)
            return Pattern.compile("^[0-9]+\\.([0-9]+)$").matcher(value).matches();
        else return false;
    }

    /**
     * 전화번호 형식 체크
     *
     * @param value 숫자로만 이루어진 전화번호
     * @author gue
     * @since 2015. 4. 18.
     */
    public static boolean isValidPhoneNumber(String value) {
        if (nullCheckB(value)) {
            return Pattern.compile("^(010|011|016|017|018|019)[\\d]*$").matcher(value).matches();
        } else {
            return false;
        }
    }

    /**
     * 숫자와 문자로 이루어진(전화번호등)문자에서 숫자를 제외한 문자 제거
     *
     * @author gue
     * @since 2015. 4. 18.
     */
    public static String getNumber(String value) {
        if (nullCheckB(value)) {
            return Pattern.compile("\\D").matcher(value).replaceAll("");
        } else {
            return null;
        }
    }


    /* ************************************************************************************************
     * INFO InputFilter
     */
    /**
     * {@link InputFilter} : no space
     *
     * @author gue
     * @since 2012. 9. 15.
     */
    public static InputFilter IF_NoSpace = new InputFilter() {
        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
            if (Pattern.compile("^ +$").matcher(source).matches()) {
                return "";
            }
            return null;
        }
    };

    /**
     * {@link InputFilter} : no enter
     *
     * @author gue
     * @since 2012. 9. 17.
     */
    public static InputFilter IF_NoEnter = new InputFilter() {
        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
            if (Pattern.compile("^\n+$").matcher(source).matches()) {
                return "";
            }
            return null;
        }
    };

}