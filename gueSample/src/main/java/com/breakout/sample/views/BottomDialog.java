package com.breakout.sample.views;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.breakout.sample.R;
import com.breakout.util.widget.ViewUtil;


/**
 * 하단에서 슬라이드업 되는 커스텀 다이얼로그
 *
 * @author sung-gue
 * @version 1.0 (2019-12-21)
 */
public class BottomDialog extends Dialog implements View.OnClickListener {

    private Context _context;

    private TextView _title;
    private TextView _msg;
    private FrameLayout _rootView;
    private TextView _okBt;
    private TextView _cancelBt;
    private OnClickListener _okListener;
    private OnClickListener _cancleListener;
    private View _btLine;
    private boolean _isSlideUp = true;

    public BottomDialog(@NonNull Context context) {
        super(context);
        _context = context;
        init();
    }

    public BottomDialog(@NonNull Context context, boolean isSlideUp) {
        super(context);
        _context = context;
        _isSlideUp = isSlideUp;
        init();
    }


    public BottomDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
        _context = context;
        init();
    }

    protected BottomDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        _context = context;
        init();
    }

    private void init() {
        setContentView(R.layout.ui_bottom_dialog);
        _title = findViewById(R.id.bottomDialogTitle);
        _msg = findViewById(R.id.bottomDialogMsg);
        _rootView = findViewById(R.id.bottomDialogRoot);
        _okBt = findViewById(R.id.bottomDialogOkBt);
        _cancelBt = findViewById(R.id.bottomDialogCancelBt);
        _btLine = findViewById(R.id.bottomDialogBtLine);


        _okBt.setOnClickListener(this);
        _cancelBt.setOnClickListener(this);
    }

    @Override
    public void setContentView(int layoutResID) {
//        super.setContentView(layoutResID);
        View view = LayoutInflater.from(getContext()).inflate(layoutResID, null);
        setContentView(view);
    }

    @Override
    public void setContentView(@NonNull View view) {
//        super.setContentView(view);
        Context context = getContext();
        int margin = (int) ViewUtil.dp2px(10, context);
        ViewGroup.MarginLayoutParams marginParams = new ViewGroup.MarginLayoutParams(-1, -2);
        marginParams.setMargins(margin, 0, margin, margin);
        setContentView(view, marginParams);
    }

    @Override
    public void setContentView(@NonNull View view, @Nullable ViewGroup.LayoutParams params) {
        super.setContentView(view, params);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window window = getWindow();
        window.setLayout(-1, -2);
        window.setBackgroundDrawableResource(R.color.transparent);
        WindowManager.LayoutParams windowParams = window.getAttributes();
        if (_isSlideUp) {
            windowParams.windowAnimations = R.style.Animations_SlideUp;
            windowParams.gravity = Gravity.BOTTOM;
        }
//        windowParams.verticalMargin = 0.02f;
//        windowParams.horizontalMargin = 0.1f;
    }

    public void setView(int layoutResID) {
        View view = LayoutInflater.from(getContext()).inflate(layoutResID, null);
        setView(view);
    }

    public void setView(@NonNull View view) {
        ViewGroup.LayoutParams marginParams = new ViewGroup.LayoutParams(-1, -2);
        _rootView.removeAllViews();
        _rootView.addView(view, marginParams);
    }

    public void setView(@NonNull View view, ViewGroup.LayoutParams params) {
        _rootView.removeAllViews();
        _rootView.addView(view, params);
    }

    @Override
    public void setTitle(int titleId) {
        setTitle(_context.getString(titleId));
    }

    @Override
    public void setTitle(@Nullable CharSequence title) {
        _title.setText(title);
        _title.setVisibility(View.VISIBLE);
    }

    public void setMessage(int msgId) {
        setMessage(_context.getString(msgId));
    }

    public void setMessage(CharSequence msg) {
        _msg.setText(msg);
        _msg.setVisibility(View.VISIBLE);
    }

    public void setOkBt(OnClickListener listener) {
        setOkBt(R.string.ok, listener);

    }

    public void setOkBt(int resourceId, OnClickListener listener) {
        setOkBt(_context.getString(resourceId), listener);

    }

    public void setOkBt(String str, OnClickListener listener) {
        _okBt.setText(str);
        _okBt.setVisibility(View.VISIBLE);
        ((View) _okBt.getParent()).setVisibility(View.VISIBLE);
        _okListener = listener;
        initButtonArea();
    }

    public void setOkBt(CharSequence str, OnClickListener listener) {
        _okBt.setText(str);
        _okBt.setVisibility(View.VISIBLE);
        ((View) _okBt.getParent()).setVisibility(View.VISIBLE);
        _okListener = listener;
        initButtonArea();
    }

    public void setCancelBt(OnClickListener listener) {
        setCancelBt(R.string.cancel, listener);

    }

    public void setCancelBt(int resourceId, OnClickListener listener) {
        setCancelBt(_context.getString(resourceId), listener);

    }

    public void setCancelBt(String str, OnClickListener listener) {
        _cancelBt.setText(str);
        _cancelBt.setVisibility(View.VISIBLE);
        ((View) _cancelBt.getParent()).setVisibility(View.VISIBLE);
        _cancleListener = listener;
        initButtonArea();
    }

    public void setCancelBt(CharSequence str, OnClickListener listener) {
        _cancelBt.setText(str);
        _cancelBt.setVisibility(View.VISIBLE);
        ((View) _cancelBt.getParent()).setVisibility(View.VISIBLE);
        _cancleListener = listener;
        initButtonArea();
    }

    private void initButtonArea() {
        int btCnt = 0;
        if (((View) _okBt.getParent()).getVisibility() == View.VISIBLE) {
            btCnt++;
        }
        if (((View) _cancelBt.getParent()).getVisibility() == View.VISIBLE) {
            btCnt++;
        }
        if (btCnt > 1) {
            _btLine.setVisibility(View.VISIBLE);
        } else {
            _btLine.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bottomDialogOkBt:
                dismiss();
                if (_okListener != null) {
                    _okListener.onClick(this, DialogInterface.BUTTON_POSITIVE);
                }
                break;
            case R.id.bottomDialogCancelBt:
                cancel();
                if (_cancleListener != null) {
                    _cancleListener.onClick(this, DialogInterface.BUTTON_NEGATIVE);
                }
                break;
        }
    }

    @Override
    public void setCancelable(boolean flag) {
        super.setCancelable(flag);
        super.setCanceledOnTouchOutside(flag);
    }

    @Override
    public void setOnDismissListener(@Nullable OnDismissListener listener) {
        super.setOnDismissListener(listener);
    }

    @Override
    public void setOnCancelListener(@Nullable OnCancelListener listener) {
        super.setOnCancelListener(listener);
    }

}
