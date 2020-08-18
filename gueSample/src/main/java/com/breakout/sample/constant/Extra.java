package com.breakout.sample.constant;


public final class Extra {
    public static final String TITLE = "title";
    public static final String URL = "url";
    public static final String TAB_POSITION = "tab_position";
    public static final String ITEM = "item";

    public static final String FCM_DATA = "fcm_data";

    /**
     * <b>uri -> ://intro?scheme_host=[스키마명]&msg=[약속된메세지]</b><p>
     * 모든 custom scheme의 uri는 UriActivity를 통하여 들어오게 된다.<br>
     * uri query string안의 key값인 scheme의 value를 사용하여
     * 지정된 activity로의 intent를 start하게 된다.<br>
     * ex) Uri.parse("chickenagentuser://" + uri.getQueryParameter(NetParamsKey.EX_URI_SCHEME_HOST));
     */
    public static final String EX_URI_SCHEME_HOST = "scheme_host";
    /**
     * <b>uri -> chickenagentuser://intro?scheme_host=[스키마명]&msg=[약속된메세지]</b><p>
     * 모든 custom scheme의 uri는 UriActivity를 통하여 들어오게 된다.<br>
     * custom scheme로 진입할때 uri query string안의 key값인 msg의 value를 사용하여
     * 이동되는 activity에서 특정 일을 수행할 수 있게 한다.<br>
     * ex) String msg = uri.getQueryParameter(NetParamsKey.EX_URI_MSG);
     */
    public static final String EX_URI_MSG = "msg";
}
