package com.breakout.util.widget;

import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ScrollView;

import com.breakout.util.Log;
import com.breakout.util.Util;
import com.breakout.util.string.StringUtil;

import java.util.Locale;

/**
 * {@link Util}의 method를 속성에 따라 class로 분리<br/>
 * View Util
 *
 * @author gue
 * @version 1.0
 * @copyright Copyright.2012.gue.All rights reserved.
 * @history <ol>
 * <li>변경자/날짜 : 변경사항</li>
 * </ol>
 * @since 2015. 3. 11.
 */
public class ViewUtil {
    private final static String TAG = "ViewUtil";
    private final static Locale DEFAULT_LOCALE = Locale.getDefault();


    private ViewUtil() {
    }

/* ************************************************************************************************
 * INFO view size
 */

    /**
     * px -> dp
     *
     * @author gue
     * @since 2012. 12. 28.
     */
    public final static float px2dp(int px, Context context) {
        /*DisplayMetrics metrics = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(metrics);
        return px / metrics.density;*/
        return px / context.getResources().getDisplayMetrics().density;
    }

    /**
     * dp -> px
     *
     * @author gue
     * @since 2012. 12. 28.
     */
    public final static float dp2px(int dp, Context context) {
//        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5, context.getResources().getDisplayMetrics());
//        return dp * context.getResources().getDisplayMetrics().density;
//        return Math.round(dp * context.getResources().getDisplayMetrics().density);
        return dp * context.getResources().getDisplayMetrics().density + 0.5f;
    }

    /**
     * {@link CompleteGetStatusSizeListener} 를 사용하여 device의 status bar와 title bar의 height를 전달한다.
     *
     * @return <li>int[0] : status bar</li>
     * <li>int[1] : title bar</li>
     * @author gue
     * @since 2014. 1. 8.
     */
    public final static void getStatusBarHeight(final Activity act, final CompleteGetStatusSizeListener listener) {
        new View(act).post(new Runnable() {
            @Override
            public void run() {
                try {
                    Rect rectgle = new Rect();
                    Window window = act.getWindow();
                    window.getDecorView().getWindowVisibleDisplayFrame(rectgle);

                    int statusBarHeight = rectgle.top;
                    int contentViewTop = window.findViewById(Window.ID_ANDROID_CONTENT).getTop();
                    int titleBarHeight = contentViewTop - statusBarHeight;

                    Log.i(TAG, String.format(DEFAULT_LOCALE, "StatusBar height = %d / TitleBar height = %d", statusBarHeight, titleBarHeight));
                    listener.onComplete(statusBarHeight, titleBarHeight);
                } catch (Exception e) {
                    Log.e(TAG, e.getMessage(), e);
                    listener.onComplete(0, 0);
                }
            }
        });
    }

    /**
     * {@link #getStatusBarHeight(Activity, CompleteGetStatusSizeListener)} 에서 title, status bar 크기를 구한뒤
     * 값을 전달 받는 listener
     *
     * @author gue
     * @version 1.0
     * @copyright Copyright.2011.gue.All rights reserved.
     * @since 2014. 1. 8.
     */
    public interface CompleteGetStatusSizeListener {
        public void onComplete(int statusBarHeight, int titleBarHeight);
    }

    
/* ************************************************************************************************
 * INFO view
 */

    /**
     * 키보드를 지정된 시간후에 올리고, 해당 view에 focus를 준다.
     *
     * @param appearTime 실행후 키보드가 올라오는 time지정 1/1000초
     * @author gue
     * @since 2012. 9. 4.
     */
    public final static void appearKeyPad(final Context context, final View v, int appearTime) {
        v.requestFocus();
        new Handler().postDelayed(new Runnable() {
            public void run() {
                InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(v, InputMethodManager.SHOW_IMPLICIT);
            }
        }, appearTime);
    }

    /**
     * 키보드를 지정된 시간후에 키보드를 내린다.
     *
     * @param appearTime 실행후 키보드가 올라오는 time지정 1/1000초
     * @author gue
     * @since 2012. 9. 4.
     */
    public final static void hideKeyPad(final Context context, final View v, int appearTime) {
        new Handler().postDelayed(new Runnable() {
            public void run() {
                InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
//                imm.toggleSoftInput(0, 0);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
            }
        }, appearTime);
    }

    /**
     * ScrollView안에 Scroll이 가능한 View가 있을시에 해당 View가 scroll이 가능하게 해준다.<br/>
     * <li>EditText : android:maxLines="", android:scrollbars="vertical" 이 설정되어 있어야 한다.</li>
     *
     * @author gue
     * @since 2014. 1. 8.
     */
    public final static void setViewScrollEnable(final ScrollView sc, View v) {
        v.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                sc.requestDisallowInterceptTouchEvent(true);
                switch (event.getAction() & MotionEvent.ACTION_MASK) {
                    case MotionEvent.ACTION_UP:
                        sc.requestDisallowInterceptTouchEvent(false);
                        break;
                }
                return false;
            }
        });
    }
    
    
/* ************************************************************************************************
 * INFO android theme effect
 */

    /**
     * background window blur effect <br>
     * 주의 : 항상 setContentView()이전에 실행되어야 한다.
     *
     * @author gue
     * @since 2012. 7. 29.
     */
    @SuppressWarnings("deprecation")
    public final static void setBackgroudBlur(Activity act) {
        if (android.os.Build.VERSION.SDK_INT != 16)
            act.getWindow().setFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND, WindowManager.LayoutParams.FLAG_BLUR_BEHIND);
    }
    
    
/* ************************************************************************************************
 * INFO dialog util
 */

    /**
     * AlertDialog에 사용되는 문구가 가운데로 정렬되어 있는 view를 반환하여 준다.<br>
     * msg,title은 String으로 전달받는다.
     *
     * @author gue
     * @since 2013. 1. 2.
     */
    public final static View alertViewCenterAlign(String msg, String title, Context context) {
        CV_Tv2 view = new CV_Tv2(context);
        view.setMsg(msg);
        if (StringUtil.nullCheckB(title)) view.setTitle(title);
        return view;
    }

    /**
     * AlertDialog에 사용되는 문구가 가운데로 정렬되어 있는 view를 반환하여 준다.<br>
     * msg,title은 resourceId으로 전달받는다.
     *
     * @author gue
     * @since 2013. 1. 2.
     */
    public final static View alertViewCenterAlign(int msg, int title, Context context) {
        CV_Tv2 view = new CV_Tv2(context);
        view.setMsg(msg);
        if (title > 0) view.setTitle(title);
        return view;
    }
    
/* ************************************************************************************************
 * INFO view animation
 */
    /**
     * ui 이동시에 animation을 정의하여 form번호 입력으로 통일 시켜 사용함으로써
     * 추후에 eclipse에서 이동효과에 대한 구조를 볼때 어디서 어떠한 animation을 사용하는지 관리할 수 있다.
     * @param animForm enter / exit
     *     <ol>
     *         <li>faidin / fadeout</li>
     *         <li>popupin / hold</li>
     *         <li>hold / popup_out</li>
     *         <li>hold / fadeout</li>
     *         <li>fadein / hold</li>
     *         <li></li>
     *         <li></li>
     *         <li></li>
     * </ol>
     * @author gue
     * @since 2012. 8. 10.
     */
    /*public final static void uiAnimation(Activity activity, int animForm) {
        switch (animForm) {
            case 1:
                activity.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                break;
            case 2:
                activity.overridePendingTransition(R.anim.zoom_in, R.anim.hold);
                break;
            case 3:
                activity.overridePendingTransition(R.anim.hold, R.anim.zoom_out);
                break;
            case 4:
                activity.overridePendingTransition(R.anim.hold, R.anim.fade_out);
                break;
            case 5:
                activity.overridePendingTransition(R.anim.fade_in, R.anim.hold);
                break;
        }
    }*/

    /**
     * view animation
     * @param animForm
     *     <ol>
     *         <li>faidin</li>
     *         <li>fadeout</li>
     *         <li>popup_in</li>
     *         <li>popup_out</li>
     *         <li>hold</li>
     *         <li>push_left_in</li>
     *         <li>push_left_out</li>
     *         <li>push_right_in</li>
     *         <li>push_right_out</li>
     *         <li></li>
     *         <li></li>
     * </ol>
     * @author gue
     * @since 2012. 8. 10.
     */
    /*public final static void viewAnimation(Context context, View view, int animForm){
        switch (animForm) {
            case 1:
                view.setAnimation(AnimationUtils.loadAnimation(context, R.anim.fade_in));
                break;
            case 2:
                view.setAnimation(AnimationUtils.loadAnimation(context, R.anim.fade_out));
                break;
            case 3:
                view.setAnimation(AnimationUtils.loadAnimation(context, R.anim.popup_in));
                break;
            case 4:
                view.setAnimation(AnimationUtils.loadAnimation(context, R.anim.popup_out));
                break;
            case 5:
                view.setAnimation(AnimationUtils.loadAnimation(context, R.anim.hold));
                break;
            case 6:
                view.setAnimation(AnimationUtils.loadAnimation(context, R.anim.push_left_in));
                break;
            case 7:
                view.setAnimation(AnimationUtils.loadAnimation(context, R.anim.push_left_out));
                break;
            case 8:
                view.setAnimation(AnimationUtils.loadAnimation(context, R.anim.push_right_in));
                break;
            case 9:
                view.setAnimation(AnimationUtils.loadAnimation(context, R.anim.push_right_out));
                break;
            }
    }*/
}