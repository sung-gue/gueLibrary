package com.breakout.util.listener;


import android.graphics.Color;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

public final class ListenerSuite {
    /**
     * 최초 터치한 x좌표
     */
    private static float fX1;
    /**
     * 최초 터치한 y좌표
     */
    private static float fY1;
    /**
     * imageView click event : press effect
     */
    public final static View.OnTouchListener TouchIVEffect = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            ImageView imagebtn = (ImageView) v;
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    v.setTag(new int[]{(int) event.getX(), (int) event.getY()});
                    fX1 = event.getX();
                    fY1 = event.getY();
//                    imagebtn.setColorFilter(Color.argb(50, 0, 255, 0));
                    imagebtn.setColorFilter(Color.argb(120, 100, 100, 100));
                    break;
                case MotionEvent.ACTION_UP:
                    imagebtn.clearColorFilter();
                    break;
                case MotionEvent.ACTION_MOVE:
                    if (event.getX() < (fX1 - 7) || event.getX() > (fX1 + 7) ||
                            event.getY() < (fY1 - 4) || event.getY() > (fY1 + 4)) {
                        imagebtn.clearColorFilter();
                    }
                    break;
            }
            return false;
        }
    };

}