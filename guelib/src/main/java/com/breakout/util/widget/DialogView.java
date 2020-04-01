package com.breakout.util.widget;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.breakout.util.device.DeviceUtil;
import com.breakout.util.res.AnimationSuite;

/**
 * 기본으로 반투명한 라운드 박스에 원형 {@link ProgressBar}를 삽입한 view를 생성하여 기존 progressBar를 대체할수 있다.
 * 제공하는 함수를 사용하여 배경과 삽입될 view를 변경하여 사용하거나 constructor에서 최초 설정하면 기존 Dialog의 View도 교체 할 수 있다.
 * {@link #getDialog()}, {@link #getDialog(boolean)}, {@link #getDialog(boolean, boolean)}를 사용하여 현재 class의 view를 적용한 {@link Dialog}를 얻을 수 있다.<p>
 * base padding : setPaddings(density * 25, density * 12, density * 25, density * 12);<br>
 * base margin : setMargins(density * 10, density * 10, density * 10, density * 10);<br>
 *
 * @author gue
 * @version 1.0
 * @copyright Copyright.2011.gue.All rights reserved.
 * @extend {@link LinearLayout}
 * @history <ol>
 * <li>변경자/날짜 : 변경사항</li>
 * </ol>
 * @since 2013. 1. 3.
 */
public class DialogView extends LinearLayout {
    /*private final String TAG = "DialogView";*/
    private final String TAG = getClass().getSimpleName();
    private float _density;
    private int[] _deviceSize;
    private Rect _padding;
    private Rect _margin;
    private Rect _tempRect = new Rect();
    private LinearLayout _parent;
    private Drawable _backGround;
    private View _view;
    private Dialog _dialog;
    private boolean _isUserBackground;

    /**
     * enum of {@link ProgressBar} Size
     */
    public enum Size {
        small(android.R.attr.progressBarStyleSmall),
        medium(android.R.attr.progressBarStyle),
        large(android.R.attr.progressBarStyleLarge);
        public int defStyle;

        Size(int defStyle) {
            this.defStyle = defStyle;
        }
    }

    /**
     * 기본 progress를 Dialog로 사용한다.
     * 배경은 {@link GradientDrawable}을 사용반투명의 라운드 박스에 원형 {@link ProgressBar}를 사용한다.
     * <ul>
     * <li>background : see {@link #setBackGround(Drawable)}</li>
     * <li>progress : {@link android.R.attr#progressBarStyleSmall}</li>
     * </ul>
     */
    public DialogView(Context context, Size size) {
        super(context);
        init(new ProgressBar(getContext(), null, size.defStyle));
    }

    /**
     * {@link AnimationDrawable}이나 xml로 animation이 가능하게 만들어진 drawable(ex 참고), 일반 drawable을 입력받아 ImageView에 담아
     * 해당 drawable을 회전시킨다., {@link AnimationDrawable}일 경우 frame을 start 한다.<p>
     * <pre>
     * ex) animation-list xml
     * &lt;animation-list xmlns:android="http://schemas.android.com/apk/res/android" android:oneshot="false"&gt;
     *     &lt;item android:drawable="@drawable/이미지명" android:duration="200" /&gt;
     *     &lt;item android:drawable="@drawable/이미지명" android:duration="200" /&gt;
     * &lt;/animation-list&gt;
     *
     * ex) shape xml : ring
     * &lt;shape xmlns:android="http://schemas.android.com/apk/res/android"
     *     android:innerRadiusRatio="4"
     *     android:shape="ring"
     *     android:thicknessRatio="5"
     *     android:useLevel="false"
     *     &gt;
     * &lt;size
     *     android:height="35dp"
     *     android:width="35dp"
     *     /&gt;
     * &lt;gradient
     *     android:centerColor="#886688cc"
     *     android:centerY="0.50"
     *     android:endColor="#ff6688cc"
     *     android:startColor="#006688cc"
     *     android:type="sweep"
     *     android:useLevel="false"
     *     /&gt;
     * &lt;/shape&gt;
     * </pre>
     * ex) animated-rotate xml : 해당 xml은 일반 drawable를 삽입하는것과 같은 결과를 가져오므로 리소스의 drawable을 그대로 삽입하여야 한다.
     * drawable이 android.graphics.drawable.AnimatedRotateDrawable의 성격을 가지고 있지만
     * 해당 package안에 AnimatedRotateDrawable가 존재하지 않기 때문에 해당 drawable에 대한 예외를 적용할 수가 없다.
     * <pre>
     * &lt;animated-rotate xmlns:android="http://schemas.android.com/apk/res/android"
     *     android:drawable="@drawable/이미지명"
     *     android:pivotX="50%"
     *     android:pivotY="50%"
     * &lt;/animated-rotate&gt;
     * </pre>
     * <p/>
     * see {@link #DialogView(Context, Size)}
     *
     * @param drawable animation이 없는 이미지의 경우 사이즈는 정사각형이어야 한다. 직사각형일 경우 입력받는 ImageView의 크기는 큰쪽에 맞추어진다.
     */
    public DialogView(Context context, Drawable drawable) {
        super(context);
        init(getImageView(drawable));
    }

    /**
     * 이 생성자를 사용할 경우 {@link #setBackGround(Drawable)}를 사용하여 배경을 변경할 수 있다. <br>
     * 배경부분의 표시를 하지 않으려면 {@link #DialogView(Context, View, Drawable)}의 drawable 인자에 null 을 입력한다.
     * see {@link #DialogView(Context, Size)}
     *
     * @param view dialog에 사용될 view, 어떠한 view도 삽입이 가능하다.
     */
    public DialogView(Context context, View view) {
        super(context);
        init(view);
    }

    /**
     * see {@link #DialogView(Context, Size)}
     *
     * @param view       dialog에 사용될 view, 어떠한 view도 삽입이 가능하다.
     * @param backGround 기본 제공되는 background를 변경할 drawable을 입력한다. null일 경우 기본 background를 사용하지 않는다.
     */
    public DialogView(Context context, View view, Drawable backGround) {
        super(context);
        _isUserBackground = true;
        _backGround = backGround;
        init(view);
    }

    private View getImageView(Drawable drawable) {
        /*if (drawable instanceof AnimationDrawable) {
            ProgressBar pb = new ProgressBar(getContext());
            pb.setIndeterminate(true);
            pb.setIndeterminateDrawable(drawable);
            return pb;
        }*/

        ImageView iv = new ImageView(getContext());
        iv.setImageDrawable(drawable);
        iv.setAdjustViewBounds(true);
        iv.setScaleType(ScaleType.FIT_CENTER);

        if (drawable instanceof AnimationDrawable) {
            _ad = (AnimationDrawable) drawable;
            int frameNum = ((AnimationDrawable) drawable).getNumberOfFrames();
            int ivSize = 0;
            int tempSize;
            Drawable tempD;
            Bitmap tempB;
            for (int i = 0; i < frameNum; i++) {
                tempD = ((AnimationDrawable) drawable).getFrame(i);
                if (tempD instanceof BitmapDrawable) {
                    tempB = ((BitmapDrawable) tempD).getBitmap();
                    tempSize = tempB.getWidth() > tempB.getHeight() ? tempB.getWidth() : tempB.getHeight();
                } else {
                    tempSize = tempD.getIntrinsicWidth() > tempD.getIntrinsicHeight() ? tempD.getIntrinsicWidth() : tempD.getIntrinsicHeight();
                }
                ivSize = ivSize > tempSize ? ivSize : tempSize;
            }
            if (ivSize > 0) iv.setLayoutParams(new LayoutParams(ivSize, ivSize));
        } else {
            iv.startAnimation(AnimationSuite.rotate(0, 360, 1500, new LinearInterpolator()));
            iv.getAnimation().setRepeatCount(Animation.INFINITE);
        }

        return iv;
    }

    private void init(View view) {
        _density = getResources().getDisplayMetrics().density;
        _deviceSize = DeviceUtil.getDisplaySize(getContext());

        // 1. set root view
        setLayoutParams(new LayoutParams(-2, -2));
        setGravity(Gravity.CENTER);

        // 2. set parent view
        _parent = new LinearLayout(getContext());
        addView(_parent, new LayoutParams(-2, -2));
        _parent.setGravity(Gravity.CENTER);
        setPaddingDp(25, 12, 25, 12);
        setMarginDp(10, 10, 10, 10);
        setBackGround(_backGround);

        // 3. set child view
        setView(view);
    }

    /**
     * 배경을 설정한다. null이 입력되면 기본 배경을 사용한다.<br>
     * 기본 배경 정보
     * <ul>
     * <li>{@link GradientDrawable#setColor(int)} : 0x55000000</li>
     * <li>{@link GradientDrawable#setCornerRadius(float)} : density * 10</li>
     * <li>{@link GradientDrawable#setAlpha(int)} : 200</li>
     * </ul>
     */
    public void setBackGround(Drawable backGround) {
        if (!_isUserBackground && backGround == null) {
            GradientDrawable gd = new GradientDrawable();
            gd.setColor(0x55000000);
            gd.setCornerRadius(_density * 10);
            gd.setAlpha(200);
            backGround = gd;
        }
        _backGround = backGround;
        setBack(_parent, _backGround);
    }

    public void clearBackGround() {
        _backGround = null;
        setBack(_parent, null);
    }

    /**
     * 배경안에 삽입될 view를 설정한다. 입력된 view의 크기는 ProgressView의 padding과 margin의 값을 고려하고 넣어야 하며,
     * setView이전에 입력된 view의 LayoutParams을 설정하여 넣었다면 ProgressView의 padding과 marginr값을 제거하여 width와 height를 재설정한다.
     * 만약 view의 LayoutParams을 정의하지 않았다면 width,height는 {@link ViewGroup.LayoutParams#MATCH_PARENT}로 설정된다.
     *
     * @author gue
     * @see DialogView
     * @since 2012. 12. 28.
     */
    public void setView(View view) {
        _view = view;
        _parent.removeAllViews();
        if (_view.getLayoutParams() != null) {
            _parent.addView(_view);
        } else {
            _parent.addView(_view, new LayoutParams(-2, -2));
        }
    }

    /**
     * 배경과의 설정된 view와의 여백을 의미한다.
     * 입력 사이즈는 dp이며 device의 density에 맞추어 dp를 px로 변환하여 padding을 설정한다.
     */
    public void setPaddingDp(int left, int top, int right, int bottom) {
        setPaddingPx(getPx(left), getPx(top), getPx(right), getPx(bottom));
    }

    /**
     * 배경과의 설정된 view와의 여백을 의미한다. 입력 사이즈의 단위는 px이다
     */
    public void setPaddingPx(int left, int top, int right, int bottom) {
        _parent.setPadding(left, top, right, bottom);
        _padding = new Rect(left, top, right, bottom);
    }

    /**
     * device와 배경과의 여백을 의미한다.
     * 입력 사이즈는 dp이며 device의 density에 맞추어 dp를 px로 변환하여 margin을 설정한다.
     */
    public void setMarginDp(int left, int top, int right, int bottom) {
        setMarginPx(getPx(left), getPx(top), getPx(right), getPx(bottom));
    }

    /**
     * device와 배경과의 여백을 의미한다. 입력 사이즈의 단위는 px이다
     */
    public void setMarginPx(int left, int top, int right, int bottom) {
        super.setPadding(left, top, right, bottom);
        _margin = new Rect(left, top, right, bottom);
    }

    /**
     * 현재 View에서는 적용되지 않는 method
     */
    @Override
    public void setPadding(int left, int top, int right, int bottom) {
        //super.setPadding(left, top, right, bottom);
    }

    /**
     * device의 orientation이 변경되면 dialog는 사라진다.
     *
     * @param cancel    {@link Dialog#setCancelable(boolean)}의 값을 설정, {@link Dialog#setCanceledOnTouchOutside(boolean)}는 false 고정
     * @param dimBehind true - set dim, false : clear dim
     * @return {@link DialogView}로 내부 View가 설정된 Dialog를 반환
     * @author gue
     * @since 2012. 12. 31.
     */
    public Dialog getDialog(boolean cancel, boolean dimBehind) {
        _dialog = new Dialog(getContext());

        _dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        _dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        if (!dimBehind) _dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
//        dialog.getWindow().getAttributes().windowAnimations = android.R.style.Animation_InputMethod;
//        pDialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL, WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL);

        _dialog.setContentView(this);

        _dialog.setCancelable(cancel);
        _dialog.setCanceledOnTouchOutside(false);
        return _dialog;
    }

    /**
     * {@link #getDialog(boolean, boolean)} 에서 dimBehind값을 false로 고정
     *
     * @author gue
     * @since 2012. 12. 31.
     */
    public Dialog getDialog(boolean cancel) {
        return getDialog(cancel, false);
    }

    /**
     * {@link #getDialog(boolean, boolean)} 에서 cancel과 dimBehind값을 false로 고정
     *
     * @author gue
     * @since 2012. 12. 31.
     */
    public Dialog getDialog() {
        return getDialog(false, false);
    }

    /* ************************************************************************************************
     * INFO Override
     */
    /**
     * {@link #getImageView(Drawable)}에서 Drawable가 {@link AnimationDrawable}일 경우 해당 frame을 start하기 위한 member value
     */
    private AnimationDrawable _ad;

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        if (_ad != null) _ad.start();
        super.onWindowFocusChanged(hasFocus);
    }

    /*@Override
    public void setLayoutParams(android.view.ViewGroup.LayoutParams params) {
        Log.v(TAG, "setLayoutParams 1 | " + params);
        super.setLayoutParams(params);
        Log.v(TAG, "setLayoutParams 2 | ");
    }*/

    /*@Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        Log.v(TAG, "onMeasure 1 | " + widthMeasureSpec + " / " + heightMeasureSpec);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        Log.v(TAG, "onMeasure 2 | ");
    }*/

    /*@Override
    protected void measureChild(View child, int parentWidthMeasureSpec, int parentHeightMeasureSpec) {
        Log.v(TAG, "measureChild 1 | " + child + " / " + parentWidthMeasureSpec + " / " + parentHeightMeasureSpec);
        super.measureChild(child, parentWidthMeasureSpec, parentHeightMeasureSpec);
        Log.v(TAG, "measureChild 2 | ");
    }*/
    
    /*@Override
    protected void measureChildren(int widthMeasureSpec, int heightMeasureSpec) {
        Log.v(TAG, "measureChildren 1 | " + widthMeasureSpec + " / " + heightMeasureSpec);
        super.measureChildren(widthMeasureSpec, heightMeasureSpec);
        Log.v(TAG, "measureChildren 2 | ");
    }*/
    
    /*@Override
    protected void measureChildWithMargins(View child, int parentWidthMeasureSpec, int widthUsed, int parentHeightMeasureSpec, int heightUsed) {
        Log.v(TAG, "measureChildWithMargins 1 | " + child + " / " + parentWidthMeasureSpec + " / " + widthUsed + " / "  + parentHeightMeasureSpec + " / " + heightUsed);
        super.measureChildWithMargins(child, parentWidthMeasureSpec, widthUsed, parentHeightMeasureSpec, heightUsed);
        Log.v(TAG, "measureChildWithMargins 2 | ");
    }*/

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        Window window = ((Activity) getContext()).getWindow();
        _tempRect.setEmpty();
        window.getDecorView().getWindowVisibleDisplayFrame(_tempRect);
        int statusBarHeight = _tempRect.top;
        /*int contentTopHeight= window.findViewById(Window.ID_ANDROID_CONTENT).getTop();
        int titleBarHeight = contentTopHeight - statusBarHeight;*/

        // _view 에 입력된 LayoutParams가 device의 사이즈보다 클 경우 현재 View의 padding과 margin을 제거한 크기로 재설정 하여준다.
        int childMaxWidth = _deviceSize[0] - _padding.left - _padding.right - _margin.left - _margin.right;
        int childMaxHeight = _deviceSize[1] - _padding.top - _padding.bottom - _margin.top - _margin.bottom - statusBarHeight;
        LayoutParams childParams = (LayoutParams) _view.getLayoutParams();
        
        /*if (com.com.breakout.util.CValue.DEBUG) {
            String info = String.format
                    (    "--------- DialogView onLayout -------------------------------------------------------" +
                        "\n|  onLayout changed=%s, left=%d, top=%d, right=%d, bottom=%d" +
                        "\n|  margin left=%d, top=%d, right=%d, bottom=%d" +
                        "\n|  padding left=%d, top=%d, right=%d, bottom=%d" +
                        "\n|  DialogView (%s) param size= %d x %d / view size= %d x %d" +
                        "\n|  _parent (%s) param size= %d x %d / view size= %d x %d" +
                        "\n|  child (%s) param size= %d x %d / view size= %d x %d / max size = %d x %d" +
                        
                        "\n|  DialogView parent (%s) param size= %d x %d / view size= %d x %d" +
                        "\n|  DialogView parent parent (%s) param size= %d x %d / view size= %d x %d" +
//                        "\n|  " +
                        "\n-------------------------------------------------------------------------------------" ,
                        changed, left, top, right, bottom,
                        _margin.left, _margin.top, _margin.right, _margin.bottom,
                        _padding.left, _padding.top, _padding.right, _padding.bottom,
                        this, getLayoutParams().width, getLayoutParams().height, getWidth(), getHeight(),
                        _parent, _parent.getLayoutParams().width, _parent.getLayoutParams().height, _parent.getWidth(), _parent.getHeight(),
                        _view, _view.getLayoutParams().width, _view.getLayoutParams().height, _view.getWidth(), _view.getHeight(), childMaxWidth, childMaxHeight
                        
                        ,this.getParent(), ((View) this.getParent()).getLayoutParams().width, ((View) this.getParent()).getLayoutParams().height,
                        ((View) this.getParent()).getWidth(), ((View) this.getParent()).getHeight()
                        
                        ,((View) this.getParent()).getParent(), ((View) ((View) this.getParent()).getParent()).getLayoutParams().width, ((View) ((View) this.getParent()).getParent()).getLayoutParams().height,
                        ((View) ((View) this.getParent()).getParent()).getWidth(), ((View) ((View) this.getParent()).getParent()).getHeight()
                    );
            System.out.println(String.format("_parent (%s) param size= %d x %d / view size= %d x %d", 
                    _parent.getParent(), ((View) _parent.getParent()).getLayoutParams().width, ((View) _parent.getParent()).getLayoutParams().height,
                        ((View) _parent.getParent()).getWidth(), ((View) _parent.getParent()).getHeight()));
            Log.v(TAG,     info);
        }*/

        if (childParams.width > childMaxWidth) childParams.width = childMaxWidth;
        if (childParams.height > childMaxHeight) childParams.height = childMaxHeight;

        _view.setLayoutParams(childParams);

        super.onLayout(changed, left, top, right, bottom);
    }

    @Override
    protected void onConfigurationChanged(Configuration newConfig) {
        if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            _deviceSize = DeviceUtil.getDisplaySize(getContext());
        } else if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            _deviceSize = DeviceUtil.getDisplaySize(getContext());
        }
        super.onConfigurationChanged(newConfig);

    }
    
    /*@Override
    protected void onDraw(Canvas canvas) {
        Log.v(TAG, "onDraw 1 | ");
        super.onDraw(canvas);
        Log.v(TAG, "onDraw 2 | ");
    }*/


    /* ************************************************************************************************
     * INFO  util
     */
    @SuppressWarnings("deprecation")
    private void setBack(View view, Drawable drawable) {
        if (android.os.Build.VERSION.SDK_INT < 16) view.setBackgroundDrawable(drawable);
        else view.setBackground(drawable);
    }

    private int getPx(int dp) {
        return (int) (_density * dp);
    }
}
