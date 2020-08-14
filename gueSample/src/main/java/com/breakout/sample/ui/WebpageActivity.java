package com.breakout.sample.ui;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import androidx.core.content.ContextCompat;

import com.breakout.sample.BaseActivity;
import com.breakout.sample.R;
import com.breakout.sample.constant.Extra;
import com.breakout.sample.constant.ReceiverName;
import com.breakout.sample.util.CustomWebViewClient;
import com.breakout.sample.views.AppBar;
import com.breakout.util.widget.ViewUtil;
import com.google.firebase.analytics.FirebaseAnalytics;

public class WebpageActivity extends BaseActivity implements CustomWebViewClient.CustomWebViewClientListenerEx {

    private WebView _webView;

    @Override
    protected void analyticsRecordScreen(FirebaseAnalytics firebaseAnalytics) {
        String title = getIntent().getStringExtra(Extra.TITLE);
        firebaseAnalytics.setCurrentScreen(this, title, null);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.registerfinishReceiver(ReceiverName.FINISH_EXCLUDE_MAIN);
        super.setContentView(R.layout.ui_base_layout);
        super.setBodyView(R.layout.v_webpage);

        super.initUI();
    }

    @Override
    protected void initTitle(AppBar appBar) {
        appBar.setTabLayout(false)
                .setHomeIcon(true)
                .setTitle(getIntent().getStringExtra(Extra.TITLE))
                .fixAppBarLocation(true)
        ;
    }

    @Override
    protected void initFooter() {

    }

    @Override
    protected void initBody() {
        Intent intent = getIntent();
        String url = intent.getStringExtra(Extra.URL);

        _webView = findViewById(R.id.wv);
        CustomWebViewClient webClient = new CustomWebViewClient(this, this, _webView, WebSettings.LOAD_DEFAULT);
        _webView.getSettings().setUseWideViewPort(false);
        webClient.setShowProgress(true);
        webClient.setProgressCancel(true);
        _webView.loadUrl(url);
        if (_progressBar == null) {
            FrameLayout flWeb = findViewById(R.id.flWeb);
            _progressBar = new ProgressBar(this, null, android.R.attr.progressBarStyleHorizontal);
            _progressBar.setVisibility(View.GONE);
            _progressBar.setMax(100);
            flWeb.addView(_progressBar);
            _progressBar.getLayoutParams().width = FrameLayout.LayoutParams.MATCH_PARENT;
            _progressBar.getLayoutParams().height = (int) ViewUtil.dp2px(3, this);
            _progressBar.getProgressDrawable().setColorFilter(ContextCompat.getColor(this, R.color.webPage_topProgressBar), PorterDuff.Mode.SRC_IN);
        }

    }

    @Override
    protected void refreshUI() {

    }

    private ProgressBar _progressBar;

    @Override
    public void onWebViewLoadStart(WebView wv) {

    }

    @Override
    public void onWebViewProgressUpdate(WebView wv, int newProgress) {
        if (_progressBar != null) {
            _progressBar.setProgress(newProgress);
            if (newProgress != 100) {
                _progressBar.setVisibility(View.VISIBLE);
            } else {
                _progressBar.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void onWebViewLoadFinish(WebView wv) {

    }

    @Override
    public void onWebViewLoadError(WebView wv) {

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void finishAfterTransition() {
        super.finishAfterTransition();
//        overridePendingTransition(R.anim.hold, R.anim.slide_down_out);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    @Override
    public void finish() {
        super.finish();
//        overridePendingTransition(R.anim.hold, R.anim.slide_down_out);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }
}
