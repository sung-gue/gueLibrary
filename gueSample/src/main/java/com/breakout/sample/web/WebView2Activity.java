package com.breakout.sample.web;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.breakout.sample.BaseActivity;
import com.breakout.sample.Log;
import com.breakout.sample.views.AppBar;


public class WebView2Activity extends BaseActivity {

    private LinearLayout _bodyView;
    private WebView _wv;
    private CookieManager _cookieManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        _bodyView = super.setEmptyContentView();

        CookieSyncManager.createInstance(this);        // 쿠키의 동기화를 위해 인스턴스 생성

        // cookie 삭제
        _cookieManager = CookieManager.getInstance();
//		_cookieManager.removeAllCookie();

        super.initUI();
    }

    @Override
    protected void initTitle(AppBar appBar) {
    }

    @Override
    protected void initTitle() {
        LinearLayout llBtBox = new LinearLayout(this);
        llBtBox.setOrientation(LinearLayout.HORIZONTAL);
        llBtBox.setPadding(10, 10, 10, 10);
        llBtBox.setLayoutParams(new LayoutParams(-1, -2));
        Button btExit = Button(llBtBox, "닫기");
        btExit.setLayoutParams(new LinearLayout.LayoutParams(-1, -2, 1));
        btExit.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        Button btBack = Button(llBtBox, "뒤로");
        btBack.setLayoutParams(new LinearLayout.LayoutParams(-1, -2, 1));
        btBack.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (_wv.canGoBack()) _wv.goBack();
                else Toast.makeText(_appContext, "뒤로 갈수 있는 페이지가 없습니다.", Toast.LENGTH_SHORT).show();
            }
        });
        Button btFoward = Button(llBtBox, "앞으로");
        btFoward.setLayoutParams(new LinearLayout.LayoutParams(-1, -2, 1));
        btFoward.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (_wv.canGoForward()) _wv.goForward();
                else Toast.makeText(_appContext, "앞으로 갈수 있는 페이지가 없습니다.", Toast.LENGTH_SHORT).show();
            }
        });
        _bodyView.addView(llBtBox);
    }

    @Override
    protected void initFooter() {
    }

    @SuppressWarnings("deprecation")
    @Override
    protected void initBody() {
        _wv = new WebView(this);
        _bodyView.setPadding(0, 0, 0, 0);
        _bodyView.addView(_wv, new LayoutParams(-1, -1));

        _wv.clearCache(true);
        WebSettings settings = _wv.getSettings();
        settings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        settings.setSupportZoom(true);
        settings.setBuiltInZoomControls(true);
        settings.setJavaScriptEnabled(true);
        settings.setLoadWithOverviewMode(true);
        settings.setUseWideViewPort(true);
        settings.setPluginState(WebSettings.PluginState.ON);
        _wv.setHorizontalScrollBarEnabled(false);
        _wv.setVerticalScrollBarEnabled(false);

        _wv.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                Log.i(TAG, " | shouldOverrideUrlLoading : " + url);
                view.loadUrl(url);
                return false;
//        		return super.shouldOverrideUrlLoading(view, url);
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                Log.i(TAG, " | onPageStarted : " + url);
                super.onPageStarted(view, url, favicon);
                showProgress();
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                CookieSyncManager.getInstance().sync();        // 페이지 로드가 끝났을 경우 쿠키 분석
                Log.i(TAG, " | onPageFinished : " + url);
                super.onPageFinished(view, url);
                closeProgress();
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                super.onReceivedError(view, errorCode, description, failingUrl);
                closeProgress();
                Toast.makeText(_context, description, Toast.LENGTH_SHORT).show();
            }
        });
        _wv.loadUrl("http://www.naver.com");
        System.out.println("cookie : " + _cookieManager.getCookie("http://www.naver.com"));
    }

    @Override
    protected void refreshUI() {
    }

    @Override
    protected void onResume() {
        super.onResume();
        CookieSyncManager.getInstance().startSync();    // 쿠키 동기화 시작
    }

    @Override
    protected void onPause() {
        super.onPause();
        CookieSyncManager.getInstance().stopSync();        // 쿠키 동기화 정지
    }

    @Override
    public void onBackPressed() {
        if (_wv.canGoBack()) _wv.goBack();
        else super.onBackPressed();
    }

}
