package com.breakout.sample.constant;

import com.breakout.sample.BuildConfig;
import com.breakout.util.constant.CValue;

public class Const {
    public static final boolean TEST = BuildConfig.DEBUG;
    public static final boolean DEBUG = CValue.DEBUG = TEST;

    public static final String DOMAIN = "breakout.com";
    public static final String TEST_API_SERVER = "http://tapi." + DOMAIN + ":8080";
    public static final String COMMON_API_SERVER = "https://api." + DOMAIN + ":8080";
    public static final String API_SERVER = (TEST ? TEST_API_SERVER : COMMON_API_SERVER);

    public static final String URL_HOME = (TEST ? "http://twww." : "https://www.") + DOMAIN;
    public static final String URL_MOBILE_HOME = (TEST ? "http://tm." : "https://m.") + DOMAIN;
    public static final String ANALYTICS_TRACKING_ID = (TEST ? "T_ANALYTICS_TRACKING_ID" : "ANALYTICS_TRACKING_ID");


    /* ------------------------------------------------------------
        app info
     */
    /**
     * R.string.app_scheme
     */
    public static final String APP_SCHEME = (TEST ? "tbreakout://" : "breakout://");
    public static final String PACKAGE_NAME = BuildConfig.APPLICATION_ID;
    public static final String APP_TAG = "breakout-s";
    public static final String APP_NAME = "breakout-sample";
    public static final String URL_MARKET = "https://play.google.com/store/apps/details?id=" + PACKAGE_NAME;
    public static final String URI_MARKET = "market://details?id=" + PACKAGE_NAME;
    public static final String URI_APP_STORE = "https://itunes.apple.com/app/[app-id]";
    public static final String TERMS_OF_USE_URL = URL_HOME + "/terms/terms.html";
    public static final String PRIVACY_URL = URL_HOME + "/terms/privacy.html";


    /* ------------------------------------------------------------
        constant value
     */
    public static final String AES_KEY = Const.APP_NAME;

    public static final String YES = "y";
    public static final String NO = "n";
    public static final String ALL = "all";
    public static final String NEW = "N";

    public static final String RESPONSE = "response";
    public static final String HTTP_ERROR = "http_error";
    public static final String PARSER_ERROR = "parser_error";
    public static final String NET_ERROR = "net_error";
    public static final String RESULT = "result";
    public static final String NO_MESSAGE = "no_message";

    public static final String VERSION = "version";
    public static final String ACTIVITY_ID = "activity_id";
    public static final String ERROR_CODE = "error_code";

    public static final String DEFAULT_LIST_CNT = Const.NUM_20;
    public static final int DEFAULT_LIST_CNT_INTEGER = 20;

    public static final String NUM_1 = "1";
    public static final String NUM_2 = "2";
    public static final String NUM_3 = "3";
    public static final String NUM_4 = "4";
    public static final String NUM_5 = "5";
    public static final String NUM_9 = "9";
    public static final String NUM_10 = "10";
    public static final String NUM_20 = "20";
    public static final String NUM_000 = "000";
    public static final String NUM_001 = "001";
    public static final String NUM_004 = "004";
    public static final String NUM_00 = "00";
    public static final String NUM_01 = "01";
    public static final String NUM_0 = "0";

    public static final int CNT_20 = 20;

    /**
     * 몇분전, 몇시간전, 몇일전으로 표시할 제한 시간
     */
    public static final int DATE_AGO_PERIOD = 24 * 30 * 3;
    public static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
    public static final String DATE_FORMAT_YM = "yyyy-MM";
    public static final String NUM_FORMAT = ",###.#";


    /* ------------------------------------------------------------
        test.sample.image_filter_gue
     */
    public final static String OUTPUT_PATH = "output_path";
    public final static String IMAGE_PATH = "image_path";


    /* ------------------------------------------------------------
        widget
     */
    public final static String WIDGET_1X1_CLICK_ACTION = "com.com.breakout.util.flash_widget1x1.CLICK";
    public final static String WIDGET_2X1_CLICK_ACTION = "com.com.breakout.util.flash_widget2x1.CLICK";
    public final static String EX_FLASH_WIDGET_CLICK = "ex_flash_widget_click";
    public final static String FLASH_WIDGET_FLASH_CLICK = "flash_widget_flash_click";
    public final static String FLASH_WIDGET_LINK_CLICK = "flash_widget_link_click";
    public final static String BR_FLASH_WIDGET_NOTIFICATION = "br_flash_widget_notification";
    public final static String BR_FLASH_ACTIVITY = "br_flash_activity";

}
