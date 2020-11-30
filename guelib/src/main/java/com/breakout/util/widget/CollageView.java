package com.breakout.util.widget;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import it.sephiroth.imagezoom.ImageViewTouchEx;


/**
 * {@link ImageViewTouchEx}를 입력받아 이동이 가능한 ImageView의 집합을 만들기 위해서 사용<br>
 * 사진 합치기, 사진혼합 등에 이용<br>
 * {@link #getAdjustViewSizeFromDisplay(Context, int[])}를 사용하여 display에 맞는 view의 크기를 재계산 하여 사용이 가능하다.
 *
 * @author sung-gue
 * @version 1.1 (2012. 12. 15.)
 */
public class CollageView extends ViewGroup {
    protected final String TAG = "CollageView";

//    private Context _context;

    private float ratio;
    public int gap;


    public CollageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public CollageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CollageView(Context context) {
        super(context);
        init();
    }

    private void init() {
//        _context = getContext();
        ratio = getResources().getDisplayMetrics().widthPixels / 480f;
        gap = (int) (10f * ratio);
    }


    @Override
    protected void onLayout(boolean flag, int left, int top, int right, int bottom) {
        for (int i = 0; i < getChildCount(); i++) {
            View v = getChildAt(i);
            if (v.getVisibility() != View.GONE) {
                Rect rect = ((ImageViewTouchEx) v).getRect();
                v.layout(rect.left, rect.top, rect.right, rect.bottom);
            }
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // 크기가 xml에서 304.0dip로 결정되었기 때문에 MeasureSpec.getMode = MeasureSpec.EXACTLY 이다.
        int specWidthSize = MeasureSpec.getSize(widthMeasureSpec);
        int specHeightSize = MeasureSpec.getSize(heightMeasureSpec);

        setMeasuredDimension(specWidthSize, specHeightSize);

        // mode가 선택되지 않은경우이며 이경우는 소스에서 직접 View를 생성시에 해당 mode가 설정된다.
        if (specWidthSize == MeasureSpec.UNSPECIFIED || specHeightSize == MeasureSpec.UNSPECIFIED) {
            throw new RuntimeException("GridLayout cannot have UNSPECIFIED dimensions");
        }

        // TODO log용 확인 후 삭제 필요, 로직상 불필요 코드
        /*
        for (int i = 0; i < getChildCount(); i++) {
            Rect rect = ((ImageViewTouchEx) getChildAt(i)).getRect();        // 프레임 이미지의 rect정보를 조회한다.
            int calcWidth = MeasureSpec.getSize(MeasureSpec.makeMeasureSpec(rect.width(), MeasureSpec.EXACTLY));
            int calcHeight = MeasureSpec.getSize(MeasureSpec.makeMeasureSpec(rect.height(), MeasureSpec.EXACTLY));
            Log.i(TAG,
                    "xxx onMeasure| " +
                            "save rect " + rect.width() + "x" + rect.height() + " / " +
                            "view size " + getChildAt(i).getWidth() + "x" + getChildAt(i).getHeight() + " / " +
                            "view measure size " + calcWidth + "x" + calcHeight
            );
        }
        Log.i(TAG,
                "xxx onMeasure| remove padding size " +
                        (specWidthSize - getPaddingLeft() - getPaddingRight()) + "x" + (specHeightSize - getPaddingTop() - getPaddingBottom()));
        */
    }

    @Override
    public void removeAllViews() {
        for (int i = 0; i < getChildCount(); i++) {
            if (getChildAt(i) instanceof ImageViewTouchEx)
                ((ImageViewTouchEx) getChildAt(i)).setImageBitmap(null);
        }
        super.removeAllViews();
    }

    public int getConvertPx(int inputPx) {
        return (int) (inputPx * 1f * ratio);
    }

    /**
     * 현재 정사각형 기준으로 pixel에 대한 재계산을 하게 된다. <br>
     * 입력된 View의 사이즈를 여러 device의 screen사이즈에 맞추어 View의 사이즈 재계산, 및 입력된 pixel 위치 재계산<br>
     * 계산시에 기준으로 정한 값은 아래와 같으며, 해당 기준을 사용하여 입력받은 View에 대한 size를 재계산 하여 return한다.
     * <pre>baseDensity = 1.5(240dp), baseResolution 480x800<br>
     * @param context application context
     * @param inputSize 단위 px
     */
    /*
    public static int getAdjustPixelFromScreen(Context context, int inputSize) {
        float currentDensity = context.getResources().getDisplayMetrics().density;
        int deviceWidth = Util.getDisplaySize(context)[0];

        int adjustPixel = 0;

        Log.i(TAG, "---------xxx inputSize : " + inputSize);
        Log.i(TAG, "---------xxx deviceWidth : " + deviceWidth);
        Log.i(TAG, "---------xxx currentDensity : " + currentDensity);

        if (currentDensity == 1.5) {
            if (deviceWidth >= 540 && deviceWidth < 720) {
                adjustPixel = (deviceWidth * inputSize) / 480;
            } else adjustPixel = inputSize;
        } else if (currentDensity == 2.0) {
            if (deviceWidth == 720 || deviceWidth > 800) {
                adjustPixel = (inputSize * 3) / 2;
            } else if (deviceSize > 720 && deviceSize <= 800)
                adjustPixel = (deviceSize * basePixel) / 480;
            else if (deviceWidth == 800) {
                adjustPixel = (deviceWidth * inputSize) / 480;
            } else if (deviceWidth >= 540 && deviceWidth < 720) {
                adjustPixel = (deviceWidth * inputSize) / 480;
            } else if (deviceWidth == 768) {
                adjustPixel = (deviceWidth * inputSize) / 480;
            } else adjustPixel = inputSize;
        } else if (currentDensity == 1.0) {
            if (deviceWidth > 480) {
                adjustPixel = (deviceWidth * inputSize) / 480;
            } else adjustPixel = inputSize;
        } else if (currentDensity > 2.0) {
            adjustPixel = (inputSize * 3) / 2;
        } else adjustPixel = inputSize;

        return adjustPixel;
    }
    */

    /*
    public static int getAdjustPixelFromPhysicalPixel(Context context, int inputSize) {
        float currentDensity = context.getResources().getDisplayMetrics().density;
        // basePixel : inputSize = baseDensity(1.5) : currentDensity  ->  basePixel = inputSize * baseDensity / currentDensity
        int basePixel = (int) ( inputSize * 1.5 / currentDensity );
        
        Log.i(TAG,"xxx inputSize : " + inputSize);
        Log.i(TAG,"xxx basePixel : " + basePixel);
        Log.i(TAG,"xxx currentDensity : " + currentDensity);
        return getAdjustPixelFromScreen(context, basePixel);
    }
    */


    /*
        TODO 작업중...
     */
    /**
     * 현재 정사각형 기준으로 작업을 하여야 한다. 정사각형 이외의 사이즈에 대해서는 예외사항이 많기 때문에 해당 작업에 대해선 고려중이다.<p>
     * 입력된 View의 사이즈를 여러 device의 screen사이즈에 맞추어 View의 사이즈 재계산<br>
     * 계산시에 기준으로 정한 값은 아래와 같으며, 해당 기준을 사용하여 입력받은 View에 대한 size를 재계산 하여 return한다.
     * <pre>baseDensity = 1.5(240dp), baseResolution 480x800<br>
     * @param context application context
     * @param viewSize new int[] {width, height}
     * @return 단위 px, int[] {calcWidth, calcHeight}
     */
    /*
    public static int[] getAdjustViewSizeFromDisplay(Context context, int viewSize[]) {
        int deviceSize[] = Util.getDisplaySize(context);
        double currentDensity = context.getResources().getDisplayMetrics().density;
        
        // baseSize : inputSize = baseDensity(1.5) : currentDensity  ->  baseSize = inputSize * baseDensity / currentDensity
        int[] baseSize = new int[] {
                (int) ( viewSize[0] * 1.5 / currentDensity ),
                (int) ( viewSize[1] * 1.5 / currentDensity )
        };

        int[] adjustSize = new int[2];
        boolean square = baseSize[0] == baseSize[1];
        for (int i=0 ; i < (square ? 1 : 2) ; i++) {
            if (baseSize[i] == 0) {
                adjustSize[i] = baseSize[i];
                continue;
            }
            
            if (currentDensity == 1.5) {
                if (deviceSize[i] >= 540 && deviceSize[i] < 720) {
                    adjustSize[i] = (deviceSize[i] * baseSize[i]) / 480;
                }
                else adjustSize[i] = baseSize[i];
            } 
            else if (currentDensity == 2.0) {
                if (deviceSize[i] == 720 || deviceSize[i] > 800) {
                    adjustSize[i] = (baseSize[i] * 3) / 2;
                }
                else if (deviceSize[i] > 720 && deviceSize[i] <= 800)
                adjustSize[i] = (deviceSize[i] * baseSize[i]) / 480;
                else if (deviceSize[i] == 800) {
                    adjustSize[i] = (deviceSize[i] * baseSize[i]) / 480;
                }
                else if (deviceSize[i] >= 540 && deviceSize[i] < 720) {
                    adjustSize[i] = (deviceSize[i] * baseSize[i]) / 480;
                }
                else if (deviceSize[i] == 768) {
                    adjustSize[i] = (deviceSize[i] * baseSize[i]) / 480;
                }
                else adjustSize[i] = baseSize[i];
            } 
            else if (currentDensity == 1.0) {
                if (deviceSize[i] > 480) {
                    adjustSize[i] = (deviceSize[i] * baseSize[i]) / 480;
                }
                else adjustSize[i] = baseSize[i];
            } 
            else if (currentDensity > 2.0) {
                adjustSize[i] = (baseSize[i] * 3) / 2;
            }
            else adjustSize[i] = baseSize[i];
        }
        
        if (square) adjustSize[1] = adjustSize[0];
        
        return adjustSize;
    }
    */
}