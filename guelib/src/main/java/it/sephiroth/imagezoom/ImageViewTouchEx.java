package it.sephiroth.imagezoom;

import android.content.Context;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;

import com.breakout.util.widget.CollageView;


/**
 * {@link ImageViewTouch}를 상속 받아 {@link CollageView}를 사용시에 view의 사이즈와 이미지의 정보를 기록
 *
 * @author sung-gue
 * @version 1.0 (2013. 2. 6.)
 */
public class ImageViewTouchEx extends ImageViewTouch {
    /**
     * view의 size로 입력된 rect
     */
    private Rect rect;
    /**
     * 이미지의 orientation값에 해당하는 rotate degree
     */
    private int degree;
    /**
     * 원본 파일과 이미지 디코드시의 sampleSize 값
     */
    private int sampleSize;


    public ImageViewTouchEx(Context context, AttributeSet attr) {
        super(context, attr);
    }

    public ImageViewTouchEx(Context context) {
        this(context, null);
    }

    public RectF getBitmapRect() {
        return super.getBitmapRect(mSuppMatrix);
    }

    /**
     * @param rect view의 size로 입력된 rect
     */
    public void setRect(Rect rect) {
        this.rect = rect;
    }

    /**
     * @return view의 size로 입력된 rect
     */
    public Rect getRect() {
        return rect;
    }

    /**
     * @param degree 이미지의 orientation값에 해당하는 rotate degree
     */
    public void setDegree(int degree) {
        this.degree = degree;
    }

    /**
     * @return 이미지의 orientation값에 해당하는 rotate degree
     */
    public int getDegree() {
        return degree;
    }

    /**
     * @param sampleSize 원본 파일과 이미지 디코드시의 sampleSize 값
     */
    public void setSampleSize(int sampleSize) {
        this.sampleSize = sampleSize;
    }

    /**
     * @return 원본 파일과 이미지 디코드시의 sampleSize 값
     */
    public int getSampleSize() {
        return sampleSize;
    }
}