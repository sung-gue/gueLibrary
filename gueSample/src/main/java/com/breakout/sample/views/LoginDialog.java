package com.breakout.sample.views;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.breakout.sample.R;
import com.breakout.sample.constant.Extra;
import com.breakout.sample.constant.SharedData;
import com.breakout.sample.ui.WebpageActivity;

import java.util.Timer;
import java.util.TimerTask;


public class LoginDialog extends Dialog implements View.OnClickListener {

    public interface OnClickListener {
        /**
         * 로그인 취소
         *
         * @param isCheck 하루다시보지 않기 체크하면 true
         */
        void onCancle(boolean isCheck);

        /**
         * 로그인 요청
         */
        void startLogin();
    }

    private Context _context;
    private LoginDialog.OnClickListener _listener;
    private CheckBox _cbCheck;
    private ViewGroup _llCount;
    private Button _ibLogin;

    private Timer _timer = new Timer();
    private TimerTask _timerTask;

    private boolean _isImmediatelyLogin;

    public LoginDialog(Context context, boolean isVisibleCheckbox, boolean isImmediatelyLogin, LoginDialog.OnClickListener listener) {
        super(context);
        _context = context;
        _listener = listener;
        _isImmediatelyLogin = isImmediatelyLogin;
        init(isVisibleCheckbox);
    }

    private void init(boolean isVisibleCheckbox) {
        setContentView(R.layout.v_login);

        final SharedData shared = SharedData.getInstance(_context);
        TextView tvTermsDesc = findViewById(R.id.tvTermsDesc);
        String termDesc = _context.getString(R.string.terms_desc);
        String link01 = _context.getString(R.string.terms_desc_link01);
        int link01StartIndex = termDesc.indexOf(link01);
        String link02 = _context.getString(R.string.terms_desc_link02);
        int link02StartIndex = termDesc.indexOf(link02);
        SpannableString span = new SpannableString(termDesc);
        span.setSpan(new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                loginTimerCancel();
                Intent intent = new Intent();
                intent.putExtra(Extra.TITLE, _context.getString(R.string.terms_service));
                intent.putExtra(Extra.URL, shared.getTermsOfUseUrl());
                intent.setClass(_context, WebpageActivity.class);
                _context.startActivity(intent);
                ((Activity) _context).overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        }, link01StartIndex, link01StartIndex + link01.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        span.setSpan(new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                loginTimerCancel();
                Intent intent = new Intent();
                intent.putExtra(Extra.TITLE, _context.getString(R.string.terms_privacy));
                intent.putExtra(Extra.URL, shared.getPrivacyUrl());
                intent.setClass(_context, WebpageActivity.class);
                _context.startActivity(intent);
                ((Activity) _context).overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        }, link02StartIndex, link02StartIndex + link02.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        tvTermsDesc.setText(span);
        tvTermsDesc.setMovementMethod(LinkMovementMethod.getInstance());

        _cbCheck = findViewById(R.id.cbCheck);
        if (isVisibleCheckbox) {
            _cbCheck.setVisibility(View.VISIBLE);
        } else {
            _cbCheck.setVisibility(View.GONE);
        }
        findViewById(R.id.ivClose).setVisibility(View.GONE);
        findViewById(R.id.ivClose).setOnClickListener(this);
        _ibLogin = findViewById(R.id.ibLogin);
        _ibLogin.setOnClickListener(this);

        getWindow().setBackgroundDrawableResource(R.color.transparent);
//        getWindow().setLayout(-1, -1);
        setCancelable(false);
        setCanceledOnTouchOutside(false);

        _llCount = findViewById(R.id.llCount);
        /*if (isImmediatelyLogin) {
            _llCount.postDelayed(new Runnable() {
                @Override
                public void run() {
                    _ibLogin.performClick();
                }
            }, 500);
        } else {
        }*/
        loginTimerStart();
    }


    public void loginTimerStart() {
        loginTimerCancel();
        _timerTask = new TimerTask() {
            @Override
            public void run() {
                _llCount.post(new Runnable() {
                    @Override
                    public void run() {
                        if (_llCount.getChildCount() > 0) {
                            _llCount.removeViewAt(0);
                        } else {
                            loginTimerCancel();
                            _ibLogin.performClick();
                        }
                    }
                });
            }
        };
        _timer.schedule(_timerTask, 500, _isImmediatelyLogin ? 300 : 600);
    }

    private void loginTimerCancel() {
        if (_timer != null && _timerTask != null) {
            _timerTask.cancel();
            _timerTask = null;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ivClose:
                _listener.onCancle(_cbCheck.isChecked());
                dismiss();
                break;
            case R.id.ibLogin:
                _listener.startLogin();
                dismiss();
                break;
        }
    }

    @Override
    public void onDetachedFromWindow() {
        if (_timerTask != null) {
            _timer.cancel();
            _timerTask = null;
            _timer = null;
        }
        super.onDetachedFromWindow();
    }
}
