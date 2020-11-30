package com.breakout.util.widget;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * alert dialog 를 사용할때 msg와 title을 center align을 적용하여 표현하기 위한 View
 *
 * @author sung-gue
 * @version 1.0 (2012. 12. 18.)
 */
public class CV_Tv2 extends LinearLayout {
    public TextView tvTitle;
    public TextView tvMsg;

    private float _density;

    /**
     * Call requires API level 11 (current min is 8)
     */
    public CV_Tv2(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        init();
    }

    public CV_Tv2(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CV_Tv2(Context context) {
        super(context);
        init();
    }

    private void init() {
        setLayoutParams(new LayoutParams(-1, -1));
        setOrientation(VERTICAL);
        _density = getResources().getDisplayMetrics().density;

        tvTitle = new TextView(getContext());
        tvTitle.setPadding(getPx(10), getPx(10), getPx(10), getPx(10));
        tvTitle.setGravity(Gravity.CENTER);
        tvTitle.setSingleLine(false);
        tvTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
        tvTitle.setTextColor(Color.WHITE);
        tvTitle.setVisibility(View.GONE);

        tvMsg = new TextView(getContext());
        tvMsg.setPadding(getPx(10), getPx(10), getPx(10), getPx(10));
        tvMsg.setGravity(Gravity.CENTER);
        tvMsg.setSingleLine(false);
        tvMsg.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        tvMsg.setTextColor(Color.WHITE);
        tvMsg.setLineSpacing(0, 1.2f);

        addView(tvTitle, new LayoutParams(-1, -2));
        addView(tvMsg, new LayoutParams(-1, -2));
    }

    public void setTitle(String title) {
        tvTitle.setText(title);
        tvTitle.setVisibility(View.VISIBLE);
    }

    public void setTitle(int title) {
        tvTitle.setText(title);
        tvTitle.setVisibility(View.VISIBLE);
    }

    public void setMsg(String msg) {
        tvMsg.setText(msg);
    }

    public void setMsg(int msg) {
        tvMsg.setText(msg);
    }

    @Override
    public void setLayoutParams(android.view.ViewGroup.LayoutParams params) {
        super.setLayoutParams(params);
    }

    private int getPx(int dp) {
        return (int) (_density * dp);
    }
}