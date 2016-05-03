package com.breakout.sample;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.breakout.util.AppCompatActivityEx;
import com.breakout.util.string.StringUtil;


/**
 * 상속받은 class는 onCreate()에 {@link #setContentView(String, boolean)}을 구현하면 {@link #_vParent}의 사용이 가능하다.<br/>
 * {@link #_vParent}를 사용하면 xml을 사용하지 앟고 이부분에 특정 layout을 add하여 사용이 가능하다.
 *
 * @author gue
 * @version 1.0
 * @copyright Copyright.2011.gue.All rights reserved.
 * @history <ol>
 * <li>변경자/날짜 : 변경사항</li>
 * </ol>
 * @since 2013. 11. 27.
 */
public abstract class BaseActivity extends AppCompatActivityEx {
    /**
     * top view of current activity<br/>
     * orientation=vertical<br/>
     */
    protected LinearLayout _vParent;
    /**
     * {@link android.util.DisplayMetrics#density}
     */
    private float density;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        density = getResources().getDisplayMetrics().density;
        super.onCreate(savedInstanceState);
    }

    /**
     * set width, height
     *
     * @param params
     * @return params
     * @author gue
     * @since 2013. 10. 1.
     */
    protected final <Params extends ViewGroup.LayoutParams> Params getLayoutParams(Params params) {
        if (params.width > 0) params.width = (int) (params.width * density);
        if (params.height > 0) params.height = (int) (params.height * density);
        return params;
    }

    /**
     * set margin params, size : dp unit
     *
     * @param params
     * @param left   margin left
     * @param top    margin top
     * @param right  margin right
     * @param bottom margin bottom
     * @return params
     * @author gue
     */
    protected final <Params extends ViewGroup.LayoutParams> Params getMarginLayoutParams(Params params, int left, int top, int right, int bottom) {
        params = getLayoutParams(params);
        if (params instanceof ViewGroup.MarginLayoutParams) {
            ((ViewGroup.MarginLayoutParams) params).setMargins((int) (left * density),
                    (int) (top * density),
                    (int) (right * density),
                    (int) (bottom * density));
        }
        return params;
    }

    /**
     * size : dp unit
     *
     * @author gue
     */
    protected final void setPadding(View view, int left, int top, int right, int bottom) {
        view.setPadding((int) (left * density),
                (int) (top * density),
                (int) (right * density),
                (int) (bottom * density));
    }

    protected final LinearLayout setContentView(String title, boolean scroll) {
        LinearLayout contentView = new LinearLayout(this);
        contentView.setOrientation(LinearLayout.VERTICAL);
        contentView.setLayoutParams(getLayoutParams(new ViewGroup.LayoutParams(-1, -1)));

        LinearLayout child = new LinearLayout(this);
        child.setOrientation(LinearLayout.VERTICAL);
        child.setLayoutParams(getLayoutParams(new LinearLayout.LayoutParams(-1, -1)));
        setPadding(child, 10, 10, 10, 10);
        _vParent = child;
        _vParent.setBackgroundColor(Color.RED);


        if (StringUtil.nullCheckB(title)) setTitleBar(contentView, title);

        if (scroll) {
            ScrollView sv = new ScrollView(this);
            sv.setLayoutParams(getLayoutParams(new ViewGroup.LayoutParams(-1, -1)));
            sv.setFillViewport(true);

            sv.addView(child);

            contentView.addView(sv);
        } else {
            contentView.addView(child);
        }

        super.setContentView(contentView, contentView.getLayoutParams());
        return contentView;
    }

    protected final void setTitleBar(ViewGroup parent, String title) {
        TextView tv = new TextView(this);
        tv.setText(title);
        tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
        tv.setTextColor(Color.BLACK);
        tv.setTypeface(Typeface.DEFAULT_BOLD);
        tv.setGravity(Gravity.CENTER);
        tv.setBackgroundResource(R.color.colorPrimary);
        tv.setLayoutParams(getLayoutParams(new ViewGroup.LayoutParams(-1, -2)));
        setPadding(tv, 5, 5, 5, 5);

        parent.addView(tv, 0);
    }

    protected final Button Button(ViewGroup parent, String btName) {
        Button bt = new Button(this);
        bt.setText(btName);
        bt.setPadding(0, 0, 0, 0);
        bt.setIncludeFontPadding(false);
        bt.setLayoutParams(getMarginLayoutParams(new LinearLayout.LayoutParams(-1, -2), 5, 5, 5, 5));

        if (parent != null) parent.addView(bt);
        return bt;
    }

    protected final TextView TextView(ViewGroup parent, String contents) {
        TextView tv = new TextView(this);
        tv.setText(contents);
        tv.setTextColor(Color.WHITE);
        tv.setPadding(5, 0, 5, 0);
        tv.setLayoutParams(getMarginLayoutParams(new LinearLayout.LayoutParams(-1, -2), 0, 5, 0, 0));

        if (parent != null) parent.addView(tv);
        return tv;
    }

    protected final EditText EditText(ViewGroup parent, String hint) {
        EditText et = new EditText(this);
        et.setHint(hint);
        et.setPadding(10, 10, 10, 10);
        et.setLayoutParams(getMarginLayoutParams(new LinearLayout.LayoutParams(-1, -2), 0, 0, 0, 0));

        if (parent != null) parent.addView(et);
        return et;
    }

    protected final OnClickListener getMoveActivityListener(final Class<?> cls) {
        OnClickListener listener = new OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(_appContext, cls));
            }
        };
        return listener;
    }

    protected final OnClickListener getMoveActivityListener(final Intent intent) {
        OnClickListener listener = new OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(intent);
            }
        };
        return listener;
    }


}