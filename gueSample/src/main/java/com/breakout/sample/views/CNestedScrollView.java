package com.breakout.sample.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;

import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;

import com.breakout.sample.R;


public class CNestedScrollView extends NestedScrollView {
    public CNestedScrollView(Context context) {
        super(context);
    }

    public CNestedScrollView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public CNestedScrollView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    private Path path;

    @Override
    protected void dispatchDraw(Canvas canvas) {
        // add round conner
        if (true || path == null) {
            path = new Path();
            float rx = getContext().getResources().getDimension(R.dimen.radius_corner);
            float ry = rx;
//            path.addRoundRect(new RectF(0, 0, canvas.getWidth(), canvas.getHeight()), rx, ry, Path.Direction.CW);
            float[] radius = new float[]{
                    0, 0,
                    0, 0,
                    rx, ry,
                    rx, ry
            };
            path.addRoundRect(new RectF(canvas.getClipBounds()), radius, Path.Direction.CW);
        }
        canvas.clipPath(path);
        super.dispatchDraw(canvas);
    }

    /*@Override
    protected void onDraw(Canvas canvas) {
        // add round conner
        if (path == null) {
            path = new Path();
            float rx = getContext().getResources().getDimension(R.dimen.boxMainRadius);
            float ry = rx;
//            path.addRoundRect(new RectF(0, 0, canvas.getWidth(), canvas.getHeight()), rx, ry, Path.Direction.CW);
            float[] radius = new float[]{
                    0, 0,
                    0, 0,
                    rx, ry,
                    rx, ry
            };
            path.addRoundRect(new RectF(canvas.getClipBounds()), radius, Path.Direction.CW);
        }
        canvas.clipPath(path);
        super.onDraw(canvas);
    }*/

}
