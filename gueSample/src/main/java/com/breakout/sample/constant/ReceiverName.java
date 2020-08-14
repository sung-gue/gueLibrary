package com.breakout.sample.constant;

/**
 * define BroadCast filter name
 *
 * @author gue
 * @version 1.0
 * @copyright Copyright.2016.gue.All rights reserved.
 * @since 2016.02.25
 */
public final class ReceiverName {
    /**
     * AppCompatActivityEx.registerfinishReceiver(String filterName)을 사용<br/>
     * <pre>
     * 	regist : registerfinishReceiver(ReceiverName.FINISH_EXCLUDE_MAIN);
     * 	사용 : sendBroadcast(new Intent(ReceiverName.FINISH_EXCLUDE_MAIN));
     * </pre>
     * 등록된 receiver를 예시처럼 실행하여 stack안에서 등록된 모든 activity를 finish()<br/>
     * <pre>sendBroadcast(new Intent(ReceiverName.FINISH_EXCLUDE_MAIN));</pre>
     */
    public static final String FINISH_EXCLUDE_MAIN = "bboom_fnish_exclude_main";
    public static final String FINISH = "bboom_fnish";
    /**
     * start activity from gcm, finish receiver
     */
    public static final String FROM_GCM = "from_gcm";
    /**
     * intro, login, join Activity 에서 로그인을 수행하기전 gcm regId를 가져와야 하는경우
     * broadcast로 gcm regId를 GCMIntentService 에서 broadcast가 등록된 activity로 전달
     */
    public static final String GCM_REGIST_COMPLETE = "gcm_regist_complete";


    public static final String REFRESH = "refresh";

}