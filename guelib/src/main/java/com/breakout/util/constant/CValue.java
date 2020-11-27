package com.breakout.util.constant;

/**
 * constant values
 *
 * @author sung-gue
 * @version 1.0 (2012. 10. 4.)
 */
public class CValue {
    /**
     * library의 console log에 대한 설정
     * <dl><li>true : test</li>
     * <li>flase : deployment</li>
     */
    public static boolean DEBUG = true;

    public static final String DB_WRITE = "write";
    public static final String DB_READ = "read";


    /* ------------------------------------------------------------
        color
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


    /* ------------------------------------------------------------
        dimen
     */
    public static final int N01 = 1;
    public static final int N05 = 5;
    public static final int N10 = 10;
    public static final int N20 = 20;
    public static final int N30 = 30;
    public static final int N40 = 40;


    /* ------------------------------------------------------------
        intent extra key
     */
    public static final String EX_EXIT = "exit";
    public static final String EX_EXIT_SHARE = "exit_share";
    public static final String EX_FORCE_MOVE = "force_move";

}
