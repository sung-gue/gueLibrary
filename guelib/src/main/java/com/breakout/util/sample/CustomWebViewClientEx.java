package com.breakout.util.sample;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Bitmap;
import android.net.Uri;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.breakout.util.ActivityEx;
import com.breakout.util.Log;
import com.breakout.util.web.CustomWebViewClient;

import java.net.URLEncoder;


/**
 * {@link CustomWebViewClient} 상속 class sample
 *
 * @author sung-gue
 * @copyright Copyright 2013. sung-gue All rights reserved.
 * @history <ol>
 * <li>변경자/날짜 : 변경사항</li>
 * </ol>
 * @since 2013. 3. 21.
 */
public class CustomWebViewClientEx extends CustomWebViewClient {
    private final String TAG;

    private final ActivityEx _baseAct;
    private WebView _wv;

    private boolean _isProgress = true;
    private boolean _isProgressCancel = true;


    public CustomWebViewClientEx(Activity activity, WebView wv) {
        super(activity, wv);
        this.TAG = activity.getClass().getSimpleName();
        this._baseAct = (ActivityEx) activity;
        this._wv = wv;
        initWebView(wv, WebSettings.LOAD_DEFAULT);
    }

    public CustomWebViewClientEx(Activity activity, WebView wv, int mode) {
        super(activity, wv, mode);
        this.TAG = activity.getClass().getSimpleName();
        this._baseAct = (ActivityEx) activity;
        this._wv = wv;
        initWebView(wv, mode);
    }

    /**
     * @return 서버에 전송될 쿼리 파라미터를 연결한 url
     */
    public String addQuery(String url) {
        String resultUrl = url;
        try {
            Uri uri = Uri.parse(url);
            if (uri.getHost() == null) {
//                resultUrl = URL_HOME + uri.getPath();
            }
            if (resultUrl.contains("atype=user") && !resultUrl.contains("&auth_key=")) {
                Log.i(TAG, "CWVC | addQuery key url encode : " + url);
                resultUrl = url +
                        "&auth_key=" + URLEncoder.encode(url, "UTF-8");
            }
            /*if (uri.getHost().contains(Const.DOMAIN)) {
                if (resultUrl.contains("atype=user") && !resultUrl.contains("&auth_key=")) {
                    String authKey = SharedData.getInstance(_context).getUserXsession();
                    if (!StringUtil.nullCheckB(authKey)) authKey = "nologin";
                    String encryptAuthkey = CodeAction.EncryptAES(authKey, Const.AES_KEY);
                    Log.i(TAG, "CWVC | addQuery key des encrypt : " + encryptAuthkey);
                    encryptAuthkey = URLEncoder.encode(encryptAuthkey, "UTF-8");
                    Log.i(TAG, "CWVC | addQuery key url encode : " + encryptAuthkey);
                    resultUrl = url +
                            "&auth_key=" + encryptAuthkey +
                            "&channel=" + Const.CHANNEL_VLAUE +
                            "&ver=" + DeviceUtil.getAppVersionName(_context);
                }
            }*/
        } catch (Exception e) {
            resultUrl = url;
            Log.e(TAG, e.getMessage(), e);
        }
        return resultUrl;
    }

    /**
     * Load url.
     *
     * @param url        url
     * @param isUrlCheck url을 체크하여 사이트 url로 변환하려면 true, 그대로 load 하려면 false
     */
    public void loadUrl(String url, boolean isUrlCheck) {
        /*if (StringUtil.nullCheckB(url)) {
            if (isUrlCheck) {
                try {
                    Uri uri = Uri.parse(url);
                    String host = uri.getHost();
                    String path = uri.getPath();
                    String query = uri.getQuery();
                    if (host == null) {
                        if (!url.startsWith("/")) {
                            url = "/" + url;
                        }
                        url = Const.URL_HOME + url;
                    } else if (host.contains(Const.DOMAIN)) {
                        url = Const.URL_HOME + path;
                        if (query != null) {
                            url += query;
                        }
                    }
                } catch (Exception e) {
                    Log.e(TAG, e.getMessage(), e);
                }
            }
            _wv.loadUrl(url);
        }*/
    }

    public void setShowProgress(boolean flag) {
        _isProgress = flag;
    }

    public void setProgressCancel(boolean flag) {
        _isProgressCancel = flag;
    }

    @Override
    protected void showProgress() {
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

    @Override
    protected void closeProgress() {
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
        return super.shouldOverrideUrlLoading(view, url);
    }

    @Override
    public void onPageStarted(WebView view, String url, Bitmap favicon) {
        super.onPageStarted(view, url, favicon);
    }

    @Override
    public void onPageFinished(WebView view, String url) {
        super.onPageFinished(view, url);
    }

    @Override
    public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
        super.onReceivedError(view, errorCode, description, failingUrl);
    }
}