package com.breakout.util.web;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.breakout.util.ActivityEx;
import com.breakout.util.Log;
import com.breakout.util.string.StringUtil;

import java.net.URLEncoder;


/**
 * 기본적인 웹뷰 설정과 ovrride한 함수들의 로그를 제공한다.<br>
 * 생성자인 {@link #CustomWebViewClient(ActivityEx, WebView, int)} 를 사용하여 기본적인 웹뷰의 설정을 할수 있다.
 * 또한 {@link #addQuery(String)}를 사용하여 서버와의 약속된 쿼리를 추가하는 작업을 한다.<br>
 * 기본으로 페이지 이동시에 progress를 생성하여 주지만 이것을 사용하지 않을 시에는 {@link #setShowProgress(boolean)}를 사용하여
 * dialog를 제거할수 있다.
 *
 * @author gue
 * @copyright Copyright.2011.gue.All rights reserved.
 * @history <ol>
 * <li>변경자/날짜 : 변경사항</li>
 * </ol>
 * @since 2013. 3. 21.
 */
public class CustomWebViewClient extends WebViewClient {
    private final String TAG;
    private final ActivityEx _baseAct;
    private WebView _wv;

    private final String HTTP = "http";
    private final String HTTPS = "https";
    private final String EXTERNAL = "external";
    private final String EXTERNALS = "externals";

    private boolean _isProgress = true;
    private boolean _isProgressCancel = true;

    public CustomWebViewClient(ActivityEx baseAct) {
        this.TAG = baseAct.getClass().getSimpleName();
        this._baseAct = baseAct;
    }

    /**
     * WebView을 전달받아 기본 쓰이는 설정을 하고 {@link WebViewClient}를 상속받은 현재클래스로 생성하여 준다.
     *
     * @param wv   사용되는 웹뷰
     * @param mode {@link WebSettings#setCacheMode(int)} 로 사용할 인자를 가져온다.
     */
    public CustomWebViewClient(ActivityEx baseAct, WebView wv, int mode) {
        this.TAG = baseAct.getClass().getSimpleName();
        this._baseAct = baseAct;
        this._wv = wv;
        webViewSetting(wv, mode);
    }

    /**
     * WebView을 전달받아 기본 쓰이는 설정을 하고 {@link WebViewClient}를 상속받은 현재클래스로 생성하여 준다.
     *
     * @param wv   사용되는 웹뷰
     * @param mode {@link WebSettings#setCacheMode(int)} 로 사용할 인자를 가져온다.
     * @author gue
     */
    @SuppressLint("SetJavaScriptEnabled")
    private void webViewSetting(WebView wv, int mode) {
        wv.clearCache(true);
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
        settings.setAppCacheEnabled(true);
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
        /*settings.setSupportMultipleWindows(true);    // WebChromeClient 를 같이 사용할 경우 설정
        wv.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onJsAlert(WebView view, String url, String message, final JsResult result) {
                new CDialog(_baseAct)
                        .setMsg(message)
                        .setCancel(false)
                        .setBtOk(R.string.ok, new CDialogBtClickListener() {
                            @Override
                            public void onClick() {
                                result.confirm();
                            }
                        }).show();
                return true;
                //return super.onJsAlert(view, url, message, result);
            }

            @Override
            public boolean onJsConfirm(WebView view, String url, String message, final JsResult result) {
                new CDialog(_baseAct)
                        .setMsg(message)
                        .setCancel(false)
                        .setBtOk(R.string.ok, new CDialogBtClickListener() {
                            @Override
                            public void onClick() {
                                result.confirm();
                            }
                        })
                        .setBtCancel(R.string.cancel, new CDialogBtClickListener() {
                            @Override
                            public void onClick() {
                                result.cancel();
                            }
                        })
                        .show();
                return true;
                //return super.onJsConfirm(view, url, message, result);
            }
        });*/

        // sdk 2.3 이하버전에서 키보드 올라오지 않음
        if (Build.VERSION.SDK_INT < 11) {
            wv.requestFocusFromTouch();
             /*wv.setFocusable(true);
             wv.setFocusableInTouchMode(true);
             wv.requestFocus(android.view.View.FOCUS_DOWN);
             wv.setOnTouchListener(new android.view.View.OnTouchListener() {
                 @Override
                 public boolean onTouch(android.view.View v, android.view.MotionEvent event) {
                     switch (event.getAction()) {
                         case android.view.MotionEvent.ACTION_DOWN:
                         case android.view.MotionEvent.ACTION_UP:
                             if (!v.hasFocus()) {
                                 v.requestFocus();
                             }
                             break;
                     }
                     return false;
                 }
             });*/
        }
    }

    /**
     * @param context Activity context
     * @author gue
     */
    public final static void clearCookie(Context context) {
        CookieSyncManager cookieSyncManager = CookieSyncManager.createInstance(context);
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.setAcceptCookie(true);
        cookieManager.removeAllCookie();
        cookieSyncManager.sync();
    }

    /**
     * @param context Activity context
     * @author gue
     */
    public final static void clearSession(Context context) {
        CookieSyncManager cookieSyncManager = CookieSyncManager.createInstance(context);
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.removeSessionCookie();
        cookieSyncManager.sync();
    }

    /**
     * @return 서버에 전송될 쿼리 파라미터를 연결한 url
     * @author gue
     */
    public String addQuery(String url) {
        String resultUrl = url;
        try {
            if (resultUrl.contains("atype=user") && !resultUrl.contains("&auth_key=")) {
                Log.i(TAG, "CWVC | addQuery key url encode : " + url);
                resultUrl = url +
                        "&auth_key=" + URLEncoder.encode(url, "UTF-8");
            }
        } catch (Exception e) {
            resultUrl = url;
            Log.e(TAG, e.getMessage(), e);
        }
        return resultUrl;
    }

    public void setSupportZoom(boolean flag) {
        WebSettings settings = _wv.getSettings();
        settings.setSupportZoom(flag);
        settings.setBuiltInZoomControls(flag);
    }

    public void setShowProgress(boolean flag) {
        _isProgress = flag;
    }

    public void setProgressCancel(boolean flag) {
        _isProgressCancel = flag;
    }

    private void showProgress() {
        if (_baseAct != null && !_baseAct.isFinishing() && _isProgress) {
            _baseAct.showProgress();
            try {
                Dialog dialog = _baseAct.showProgress();
                if (dialog != null && _isProgressCancel) dialog.setCancelable(true);
            } catch (Exception e) {
                Log.w(TAG, e.getMessage(), e);
            }
        }
    }

    private void closeProgress() {
        if (_baseAct != null && !_baseAct.isFinishing() && _isProgress) {
            try {
                _baseAct.closeProgress();
            } catch (Exception e) {
                Log.w(TAG, e.getMessage(), e);
            }
        }
    }

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        Log.i(TAG, "CWVC | shouldOverrideUrlLoading : " + url);

        Uri uri = Uri.parse(url);

        if (HTTP.equalsIgnoreCase(uri.getScheme()) || HTTPS.equalsIgnoreCase(uri.getScheme())) {
            url = addQuery(url);
            view.loadUrl(url);
        } else {
            if (EXTERNAL.equalsIgnoreCase(uri.getScheme())) {
                url = url.replace(EXTERNAL + ":", HTTP + ":");
            } else if (EXTERNALS.equalsIgnoreCase(uri.getScheme())) {
                url = url.replace(EXTERNALS + ":", HTTPS + ":");
            }
//            else if (url.indexOf("www.youtube.com/watch?v=") > 0) {
//                url = url.replace("http://www.youtube.com/watch?v=", "vnd.youtube:");
//            }
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));

            try {
                _baseAct.startActivity(intent);
            } catch (ActivityNotFoundException ex) {
                Toast.makeText(_baseAct, "설치되어 있지 않은 앱", Toast.LENGTH_LONG).show();
            }
        }
        return true;
//        return super.shouldOverrideUrlLoading(view, url);
    }

    @Override
    public void onPageStarted(WebView view, String url, Bitmap favicon) {
        Log.i(TAG, "CWVC | onPageStarted : " + url);
        super.onPageStarted(view, url, favicon);
        showProgress();
    }

    @Override
    public void onPageFinished(WebView view, String url) {
        Log.i(TAG, "CWVC | onPageFinished : " + url);
        super.onPageFinished(view, url);
        closeProgress();
    }

    @Override
    public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
        Log.i(TAG, "CWVC | onReceivedError : " + failingUrl);
        super.onReceivedError(view, errorCode, description, failingUrl);
        closeProgress();
        Toast.makeText(_baseAct, "네트워크 장애", Toast.LENGTH_SHORT).show();
    }

    /*@Override
    public boolean shouldOverrideKeyEvent(WebView view, KeyEvent event) {
        Log.d(TAG, "CWVC | shouldOverrideKeyEvent keycode : " + event.getKeyCode() + " / can back : " + view.canGoBack());
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK && view.canGoBack()) {
            view.goBack();
            return true;
        }
        else return super.shouldOverrideKeyEvent(view, event);
    }*/


}
