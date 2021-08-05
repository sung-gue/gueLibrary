package com.breakout.util.constant;

/**
 * constant values in library
 *
 * @author sung-gue
 * @version 1.0 (2012. 10. 4.)
 */
public class CValue {
    /**
     * library의 console log에 대한 설정
     * <ul>
     *     <li>true : development</li>
     *     <li>flase : deployment</li>
     * </ul>
     */
    public static boolean DEBUG = true;


    /*
        INFO: color
     */
    public static final int C_RED = 0xFFFF0000;
    public static final int C_GREEN = 0xFF00FF00;
    public static final int C_BLUE = 0xFF0303FF;
    public static final int C_YELLOW = 0xFFFFFF00;
    public static final int C_SKY_BLUE = 0xFF8CE6F2;
    public static final int C_SKY_BLUE_WEAK = 0xFFDCE6F2;
    public static final int C_PURPLE = 0x77800080;
    public static final int C_GREGE = 0xFF747474;
    public static final int C_GRAY = 0xFF0303FF;


    /*
        INFO: dimen
     */
    public static final int N_01 = 1;
    public static final int N_05 = 5;
    public static final int N_10 = 10;
    public static final int N_20 = 20;
    public static final int N_30 = 30;
    public static final int N_40 = 40;


    /*
        INFO: intent extra key
     */
    public static final String EX_EXIT = "exit";
    public static final String EX_EXIT_SHARE = "exit_share";
    public static final String EX_FORCE_MOVE = "force_move";

}
