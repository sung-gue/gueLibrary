package com.breakout.sample.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.breakout.sample.R;


public class CRoundRecyclerView extends RecyclerView {
    public CRoundRecyclerView(Context context) {
        super(context);
    }

    public CRoundRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public CRoundRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    private Path path;

    @Override
    protected void dispatchDraw(Canvas canvas) {
        /**
         * add round conner
         */
        if (path == null) {
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
    public void onDraw(Canvas canvas) {
        *//**
     * add round conner
     *//*
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
