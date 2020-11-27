package com.breakout.util.listener;

import android.graphics.Color;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ImageView;

/**
 * ImageView로 되어있는 버튼을 클릭했을 때 알파값과 전체색농도를 낮춤으로써 눌리는 효과를 주게 되는 TouchListener
 *
 * @author sung-gue
 * @version 1.0 (2010. 6. 25.)
 */
public final class IVTouchEffectListener implements OnTouchListener {
    /**
     * 최초 터치한 x좌표
     */
    private float fX;
    /**
     * 최초 터치한 y좌표
     */
    private float fY;

    public IVTouchEffectListener() {
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        ImageView imagebtn = (ImageView) v;
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
//                v.setTag(new int[]{(int) event.getX(),(int) event.getY()});
                fX = event.getX();
                fY = event.getY();
//                imagebtn.setColorFilter(Color.argb(50, 0, 255, 0));
                imagebtn.setColorFilter(Color.argb(100, 0, 0, 0));
                break;
            case MotionEvent.ACTION_UP:
                imagebtn.clearColorFilter();
                fX = 0;
                fY = 0;
                break;
            case MotionEvent.ACTION_MOVE:
                if (event.getX() < (fX - 7) || event.getX() > (fX + 7) ||
                        event.getY() < (fY - 4) || event.getY() > (fY + 4)) {
                    imagebtn.clearColorFilter();
                }
                break;
        }
        return false;
    }

}