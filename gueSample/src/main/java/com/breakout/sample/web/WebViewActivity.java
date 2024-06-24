package com.breakout.sample.web;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.webkit.CookieSyncManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import com.breakout.sample.BaseActivity;
import com.breakout.sample.Log;
import com.breakout.sample.R;
import com.breakout.sample.views.AppBar;
import com.breakout.util.string.StringUtil;
import com.breakout.util.widget.ViewUtil;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class WebViewActivity extends BaseActivity implements OnClickListener {
    private String _url = "http://www.daum.net";
    private WebView _wv;
    private ImageButton _btBack;
    private ImageButton _btNext;
    private ImageButton _btMove;
    private EditText _etUrl;
    private static int i = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.webview);

        Intent intent = getIntent();
        String action = intent.getAction();
        // share text
        if (action != null && action.equalsIgnoreCase(Intent.ACTION_SEND) && intent.hasExtra(Intent.EXTRA_TEXT)) {
            String text = intent.getStringExtra(Intent.EXTRA_TEXT);
            //String subject = intent.getStringExtra(Intent.EXTRA_SUBJECT);

            if (StringUtil.nullCheckB(text)) {
                Pattern pattern = Pattern.compile("(https|http)://.*\\s?");
                Matcher match = pattern.matcher(text);
                if (match.find()) {
                    _url = match.group(0);
                }
            }
        }


        super.initUI();
        setData();
    }

    @Override
    protected void initTitle(AppBar appBar) {
    }

    @Override
    protected void initFooter() {
    }

    @Override
    protected void initBody() {
        _wv = (WebView) findViewById(R.id.wv);
        _btBack = (ImageButton) findViewById(R.id.btBack);
        _btNext = (ImageButton) findViewById(R.id.btNext);
        _btMove = (ImageButton) findViewById(R.id.btMove);
        _etUrl = (EditText) findViewById(R.id.etUrl);

        _btBack.setOnClickListener(this);
        _btNext.setOnClickListener(this);
        _btMove.setOnClickListener(this);
        _etUrl.setOnEditorActionListener(new OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_GO) {
                    _btMove.performClick();
                    return true;
                } else return false;
            }
        });
    }

    @Override
    protected void refreshUI() {
    }

    private void setData() {
        // 쿠키의 동기화를 위해 인스턴스 생성
        CookieSyncManager.createInstance(this);
        // cookie 삭제
        /*CookieManager cookieManager = CookieManager.getInstance();
        System.out.println("cookie : " + cookieManager.getCookie("http://www.naver.com") );
        cookieManager.removeAllCookie();
        System.out.println("cookie : " + cookieManager.getCookie("http://www.naver.com") );*/

        new CustomWebViewClient(_wv, WebSettings.LOAD_NO_CACHE);

        _wv.loadUrl(_url);
        _etUrl.setText(_url);
    }

    private class CustomWebViewClient extends WebViewClient {

        public CustomWebViewClient(WebView wv, int mode) {
            webViewSetting(wv, mode);
        }

        @SuppressLint("SetJavaScriptEnabled")
        private void webViewSetting(WebView wv, int mode) {
//            wv.clearCache(true);
            wv.setHorizontalScrollBarEnabled(false);
            wv.setVerticalScrollBarEnabled(false);
            wv.setWebViewClient(this);

            WebSettings settings = wv.getSettings();
            settings.setCacheMode(mode);
            settings.setSupportZoom(true);
            settings.setBuiltInZoomControls(true);
            settings.setLoadWithOverviewMode(true);
            settings.setUseWideViewPort(true);
            settings.setDomStorageEnabled(true);
            // settings.setAppCacheEnabled(true);
            settings.setJavaScriptCanOpenWindowsAutomatically(false);
            try {
                settings.setJavaScriptEnabled(true);
            } catch (Exception e) {
                Log.e(TAG, e.getMessage(), e);
            }
            try {
                settings.setPluginState(WebSettings.PluginState.ON);
            } catch (Exception e) {
                Log.e(TAG, e.getMessage(), e);
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                WebView.setWebContentsDebuggingEnabled(true);
            }

            // INFO gue/2014. 1. 28. : 4.4 에서 userAgent의 chrome 문자열 제거
            String userAgentCustom = settings.getUserAgentString();
            if (StringUtil.nullCheckB(userAgentCustom)) {
                userAgentCustom = userAgentCustom.replaceAll("(?i)chrome", "");
                settings.setUserAgentString(userAgentCustom);
            }
            settings.setSupportMultipleWindows(true);       // WebChromeClient 를 같이 사용할 경우 설정
            wv.setWebChromeClient(new WebChromeClient());
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            Log.i(TAG, " | shouldOverrideUrlLoading : " + url);
            return super.shouldOverrideUrlLoading(view, url);
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            Log.i(TAG, " | onPageStarted : " + url);
            super.onPageStarted(view, url, favicon);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            CookieSyncManager.getInstance().sync();        // 페이지 로드가 끝났을 경우 쿠키 분석
            Log.i(TAG, " | onPageFinished : " + url);
            super.onPageFinished(view, url);
        }

        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            super.onReceivedError(view, errorCode, description, failingUrl);
            Toast.makeText(_context, description, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btBack:
                _wv.goBack();
                break;
            case R.id.btNext:
                _wv.goForward();
                break;
            case R.id.btMove:
                ViewUtil.hideKeyPad(this, _etUrl, 500);
                String url = _etUrl.getText().toString();
                _etUrl.setSelection(0);
                if (url != null && url.length() > 0) _wv.loadUrl(url);
                break;
        }

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
