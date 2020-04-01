package com.breakout.util.web;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.http.SslError;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.provider.Browser;
import android.text.TextUtils;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.JavascriptInterface;
import android.webkit.SslErrorHandler;
import android.webkit.ValueCallback;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import com.breakout.util.Log;
import com.breakout.util.device.RealPathUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;


/**
 * 기본적인 웹뷰 설정과 ovrride 함수의 로그를 제공한다.<br>
 * <p>
 * {@link #shouldOverrideUrlLoading(WebView, String)}<br/>
 * 1. {@link #EXTERNAL}, {@link #EXTERNALS} 일 경우 {@link #HTTP}, {@link #HTTPS} 로 변환하여 외부 브라우저를 호출<br/>
 * 2. play.google.com/store/apps 포함된 url을 외부 브라우저로 호출
 * <p>
 * https://developer.android.com/guide/webapps
 *
 * @author sung-gue
 * @copyright Copyright 2013. sung-gue All rights reserved.
 * @history <ol>
 * <li>변경자/날짜 : 변경사항</li>
 * </ol>
 * @since 2013. 3. 21.
 */
public class CustomWebViewClient extends WebViewClient {
    public interface CustomWebViewClientListener {

        void onWebViewLoadStart(WebView wv);

        /**
         * @param newProgress 0~100
         */
        void onWebViewProgressUpdate(WebView wv, int newProgress);

        void onWebViewLoadFinish(WebView wv);
    }

    protected final String HTTP = "http";
    protected final String HTTPS = "https";
    protected final String EXTERNAL = "external";
    protected final String EXTERNALS = "externals";
    protected final String CHROME_INTENT = "intent";
    private String msgOfNotFoundApp = "not found application";

    protected final String TAG = "CWVC";
    private final Activity _activity;
    private WebView _wv;
    protected CustomWebViewClientListener _listener;


    public CustomWebViewClient(Activity activity) {
        this._activity = activity;
        if (activity instanceof CustomWebViewClientListener) {
            _listener = (CustomWebViewClientListener) activity;
        }
        //_customScript = new CustomScript();
        //_wv.addJavascriptInterface(_customScript, "CustomJs");
    }

    public CustomWebViewClient(Activity activity, WebView wv) {
        this(activity);
        this._wv = wv;
    }

    public CustomWebViewClient(Activity activity, WebView wv, int mode) {
        this(activity, wv);
        initWebView(wv, mode);
    }

    public Activity getActivity() {
        return _activity;
    }

    public Context getContext() {
        return _activity != null ? _activity.getApplicationContext() : null;
    }

    public WebView getWebView() {
        return _wv;
    }

    /**
     * WebView을 전달받아 기본 쓰이는 설정을 하고 {@link WebViewClient}를 상속받은 현재클래스로 생성하여 준다.
     *
     * @param wv   사용되는 웹뷰
     * @param mode CacheMode {@link WebSettings#LOAD_DEFAULT}, {@link WebSettings#setCacheMode(int)}
     * @author gue
     */
    @SuppressLint("SetJavaScriptEnabled")
    public WebSettings initWebView(WebView wv, int mode) {
        wv.setWebViewClient(this);
        //wv.clearCache(true);
        wv.setHorizontalScrollBarEnabled(false);
        wv.setVerticalScrollBarEnabled(false);

        WebSettings settings = wv.getSettings();
        settings.setCacheMode(mode);
        settings.setAppCacheEnabled(true);

        settings.setSupportZoom(true);
        settings.setBuiltInZoomControls(true);
        settings.setDisplayZoomControls(false);

        settings.setUseWideViewPort(true);
        settings.setLoadWithOverviewMode(true);

        settings.setDomStorageEnabled(true);

        settings.setJavaScriptEnabled(true);
        settings.setJavaScriptCanOpenWindowsAutomatically(true);

        settings.setAllowContentAccess(true);

        try {
            settings.setPluginState(WebSettings.PluginState.ON);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WebView.setWebContentsDebuggingEnabled(true);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            settings.setMixedContentMode(WebSettings.MIXED_CONTENT_COMPATIBILITY_MODE);
        }
        /*
            INFO: 2014/01/28 4.4 에서 userAgent의 chrome 문자열 제거
         */
        /*String userAgentCustom = settings.getUserAgentString();
        if (!TextUtils.isEmpty(userAgentCustom)) {
            userAgentCustom = userAgentCustom.replaceAll("(?i)chrome", "");
            settings.setUserAgentString(userAgentCustom);
        }*/
        /*
            멀티뷰 사용
         */
        settings.setSupportMultipleWindows(true);
//        wv.setWebChromeClient(new CustomWebChromeClient() {});
        return settings;
    }

    protected void showProgress() {
        if (_listener != null && _wv != null) {
            _listener.onWebViewLoadStart(_wv);
        }
    }

    protected void closeProgress() {
        if (_listener != null && _wv != null) {
            _listener.onWebViewLoadFinish(_wv);
        }
    }

    protected void logWebResourceRequest(String functionName, WebResourceRequest request) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Log.i(TAG, String.format("%s %s request | url :  %s" +
                            "\ngesture: %s, main : %s, redirect : %s" +
                            "\nheader : %s",
                    functionName, request.getMethod(), request.getUrl().toString(),
                    request.hasGesture(),
                    request.isForMainFrame(),
                    Build.VERSION.SDK_INT >= Build.VERSION_CODES.N ? request.isRedirect() : "",
                    request.getRequestHeaders()
            ));
        }
    }

    @Nullable
    @Override
    public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
//        logWebResourceRequest("shouldInterceptRequest", request);
        return super.shouldInterceptRequest(view, request);
    }

    @Nullable
    @Override
    public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
//            Log.i(TAG, "shouldInterceptRequest | url : " + url);
        }
        return super.shouldInterceptRequest(view, url);
    }

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
        logWebResourceRequest("shouldOverrideUrlLoading", request);
        return super.shouldOverrideUrlLoading(view, request);
    }

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        Uri uri = Uri.parse(url);
        String scheme = uri.getScheme();

        if (!HTTP.equalsIgnoreCase(scheme) && !HTTPS.equalsIgnoreCase(scheme)) {
            if (EXTERNAL.equalsIgnoreCase(scheme)) {
                url = url.replace(EXTERNAL + "://", HTTP + "://");
            } else if (EXTERNALS.equalsIgnoreCase(scheme)) {
                url = url.replace(EXTERNALS + "://", HTTPS + "://");
            }
            // youtube
            /*else if (url.indexOf("www.youtube.com/watch?v=") > 0) {
                url = url.replace("http://www.youtube.com/watch?v=", "vnd.youtube:");
            }*/
            startIntentForActionView(url, null);
            return true;
        } else if (url.contains("play.google.com/store/apps")) {
            startIntentForActionView(url, null);
            return true;
        }
        return super.shouldOverrideUrlLoading(view, url);
    }

    public void startIntentForActionView(String url, String toastMessage) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            if (intent.resolveActivity(_activity.getPackageManager()) != null) {
                _activity.startActivity(intent);
            } else {
                throw new ActivityNotFoundException("not found app : " + url);
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
            if (!TextUtils.isEmpty(toastMessage)) {
                Toast.makeText(_activity, toastMessage, Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onPageStarted(WebView view, String url, Bitmap favicon) {
        Log.i(TAG, "onPageStarted | url : " + url);
        super.onPageStarted(view, url, favicon);
        showProgress();
    }

    @Override
    public void onPageCommitVisible(WebView view, String url) {
        Log.i(TAG, "onPageCommitVisible | url : " + url);
        super.onPageCommitVisible(view, url);
    }

    @Override
    public void onPageFinished(WebView view, String url) {
        Log.i(TAG, "onPageFinished | url : " + url);
        super.onPageFinished(view, url);
        closeProgress();
    }

    @Override
    //@Deprecated
    public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
        Log.i(TAG, "onReceivedError | " +
                "   failingUrl : " + failingUrl +
                "   errorCode : " + errorCode +
                "   description : " + description
        );
        super.onReceivedError(view, errorCode, description, failingUrl);
        closeProgress();
        //Toast.makeText(_activity, "네트워크 장애", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
        Log.i(TAG, "onReceivedHttpError | " +
                "   request : " + request +
                "   error : " + error
        );
        super.onReceivedError(view, request, error);
        closeProgress();
    }

    @Override
    public void onReceivedHttpError(WebView view, WebResourceRequest request, WebResourceResponse errorResponse) {
        Log.i(TAG, "onReceivedHttpError | " +
                "   request : " + request +
                "   errorResponse : " + errorResponse
        );
        super.onReceivedHttpError(view, request, errorResponse);
    }

    @Override
    public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
        Log.i(TAG, "onReceivedSslError | " +
                "   SslErrorHandler : " + handler +
                "   SslError : " + error
        );
        super.onReceivedSslError(view, handler, error);
    }

    /*@Override
    public boolean shouldOverrideKeyEvent(WebView view, KeyEvent event) {
        Log.d(TAG, "shouldOverrideKeyEvent keycode : " + event.getKeyCode() + " / can back : " + view.canGoBack());
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK && view.canGoBack()) {
            view.goBack();
            return true;
        }
        else return super.shouldOverrideKeyEvent(view, event);
    }*/


    /* ************************************************************
     * DESC: web setting
     */
    public void setSupportZoom(boolean flag) {
        if (_wv != null) {
            WebSettings settings = _wv.getSettings();
            settings.setSupportZoom(flag);
            settings.setBuiltInZoomControls(flag);
        }
    }

    public void manageKeyboard() {
        // sdk 2.3 이하버전에서 키보드 올라오지 않음
        if (_wv != null && Build.VERSION.SDK_INT < 11) {
            _wv.requestFocusFromTouch();
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


    /* ************************************************************
     * DESC: cookie
     */
    private static void flushCookie(CookieSyncManager cookieSyncManager, CookieManager cookieManager) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            cookieManager.flush();
        } else {
            cookieSyncManager.sync();
        }

    }

    public static void setAllowCookie(Context context, WebView wv) {
        CookieSyncManager cookieSyncManager = CookieSyncManager.createInstance(context);
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.setAcceptCookie(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            cookieManager.setAcceptThirdPartyCookies(wv, true);
        }
        flushCookie(cookieSyncManager, cookieManager);
    }

    public final void setCookie(String url, String key, String value) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            CookieManager.getInstance().setCookie(url, key + "=" + value);
            CookieManager.getInstance().flush();
        } else {
            CookieSyncManager cookieSyncManager = CookieSyncManager.createInstance(_activity);
            CookieManager cookieManager = CookieManager.getInstance();
            cookieManager.setAcceptCookie(true);
            cookieManager.setCookie(url, key + "=" + value);
            cookieSyncManager.sync();
        }
    }

    public Map<String, String> printCookie(String url) {
        Map<String, String> cookieMap = new HashMap<>();
        try {
            CookieManager cookieManager = CookieManager.getInstance();
            String cookies = cookieManager.getCookie(url);
            Log.i(TAG, "cookies | " + cookies);
            if (cookies != null) {
                String[] cookie = Pattern.compile(";\\s+").split(cookies);
                for (String temp : cookie) {
                    String[] temps = Pattern.compile("=").split(temp);
                    if (temps.length > 1) {
                        Log.i(TAG, "  cookie | " + temps[0] + "=" + temps[1]);
                        cookieMap.put(temps[0], temps[1]);
                    }
                }
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        }
        return cookieMap;
    }


    /**
     *
     */
    public static void clearCookie(Context context) {
        CookieSyncManager cookieSyncManager = CookieSyncManager.createInstance(context);
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.setAcceptCookie(true);
        cookieManager.removeAllCookie();
        flushCookie(cookieSyncManager, cookieManager);
    }

    /**
     *
     */
    public static void clearSession(Context context) {
        CookieSyncManager cookieSyncManager = CookieSyncManager.createInstance(context);
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.removeSessionCookie();
        flushCookie(cookieSyncManager, cookieManager);
    }


    /* ************************************************************
     * DESC: input file download
     */
    private static final String TYPE_IMAGE = "image/*";
    public static final int INPUT_FILE_REQUEST_CODE = 1333;

    private ValueCallback<Uri> mUploadMessage;
    private ValueCallback<Uri[]> mFilePathCallback;
    private String mCameraPhotoPath;

    private void openFileChooserCallBack(int requestCode, int resultCode, Intent data) {
        if (requestCode == INPUT_FILE_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                if (mFilePathCallback == null) {
                    return;
                }
                Uri[] results = new Uri[]{getResultUri(data)};

                mFilePathCallback.onReceiveValue(results);
                mFilePathCallback = null;
            } else {
                if (mUploadMessage == null) {
                    return;
                }
                Uri result = getResultUri(data);

                Log.d(TAG, "openFileChooser : " + result);
                mUploadMessage.onReceiveValue(result);
                mUploadMessage = null;
            }
        } else {
            if (mFilePathCallback != null) mFilePathCallback.onReceiveValue(null);
            if (mUploadMessage != null) mUploadMessage.onReceiveValue(null);
            mFilePathCallback = null;
            mUploadMessage = null;
        }
    }

    private Uri getResultUri(Intent data) {
        Uri result = null;
        if (data == null || TextUtils.isEmpty(data.getDataString())) {
            // If there is not data, then we may have taken a photo
            if (mCameraPhotoPath != null) {
                result = Uri.parse(mCameraPhotoPath);
            }
        } else {
            String filePath = "";
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                filePath = data.getDataString();
            } else {
                filePath = "file:" + RealPathUtil.getRealPath(_activity, data.getData());
            }
            result = Uri.parse(filePath);
        }

        return result;
    }


    /* ************************************************************
     * DESC: LG 전자결제
     */

    /**
     * Should override url loading of payment.
     * SmartXPay_AppToWeb | url check
     */
    private boolean shouldOverrideUrlLoadingOfPay(final WebView view, String url) {
        // 단계 2. 백신 APK 다운로드 구현
        if ((url.startsWith("http://") || url.startsWith("https://")) && url.endsWith(".apk")) {
            downloadFile(url);
            //return super.shouldOverrideUrlLoading(view, url);
            return false;
        }
        // 단계 3. 외부 앱 및 안드로이드 마켓 호출 구현
        else if ((url.startsWith("http://") || url.startsWith("https://")) && (url.contains("market.android.com") || url.contains("m.ahnlab.com/kr/site/download"))) {
            Uri uri = Uri.parse(url);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            try {
                _activity.startActivity(intent);
                return true;
            } catch (ActivityNotFoundException e) {
                return false;
            }
        } else if (url.startsWith("http://") || url.startsWith("https://")) {
            return false;
        } else if (url != null
                && (url.contains("vguard") || url.contains("droidxantivirus") || url.contains("smhyundaiansimclick://") || url.contains("samsungpay")
                || url.contains("smshinhanansimclick://") || url.contains("smshinhancardusim://") || url.contains("smartwall://") || url.contains("appfree://")
                || url.contains("v3mobile") || url.endsWith(".apk") || url.contains("market://") || url.contains("ansimclick")
                || url.contains("market://details?id=com.shcard.smartpay") || url.contains("shinhan-sr-ansimclick://"))) {
            Intent intent = null;
            // 인텐트 정합성 체크 : 2014 .01추가
            try {
                intent = Intent.parseUri(url, Intent.URI_INTENT_SCHEME);
                Log.e("intent getScheme     >", intent.getScheme());
                Log.e("intent getDataString >", intent.getDataString());
            } catch (URISyntaxException ex) {
                Log.e("Browser", "Bad URI " + url + ":" + ex.getMessage());
                return false;
            }
            try {
                boolean retval = true;
                //chrome 버젼 방식 : 2014.01 추가
                if (url.startsWith("intent")) { // chrome 버젼 방식
                    // 앱설치 체크를 합니다.
                    if (_activity.getPackageManager().resolveActivity(intent, 0) == null) {
                        String packagename = intent.getPackage();
                        if (packagename != null) {
                            Uri uri = Uri.parse("market://search?q=pname:" + packagename);
                            intent = new Intent(Intent.ACTION_VIEW, uri);
                            _activity.startActivity(intent);
                            retval = true;
                        }
                    } else {
                        intent.addCategory(Intent.CATEGORY_BROWSABLE);
                        intent.setComponent(null);
                        try {
                            if (_activity.startActivityIfNeeded(intent, -1)) {
                                retval = true;
                            }
                        } catch (ActivityNotFoundException ex) {
                            retval = false;
                        }
                    }
                } else { // 구 방식
                    Uri uri = Uri.parse(url);
                    intent = new Intent(Intent.ACTION_VIEW, uri);
                    _activity.startActivity(intent);
                    retval = true;
                }
                return retval;
            } catch (ActivityNotFoundException e) {
                Log.e("error ===>", e.getMessage());
                return true;
            }
        }
        // 앱카드 및 계좌이체 앱 설치 여부 확인(선택사항)
        // 계좌이체 커스텀 스키마
        else if (url.startsWith("smartxpay-transfer://")) {
            boolean isatallFlag = isPackageInstalled(_activity.getApplicationContext(), "kr.co.uplus.ecredit");
            if (isatallFlag) {
                boolean override = false;
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                intent.addCategory(Intent.CATEGORY_BROWSABLE);
                intent.putExtra(Browser.EXTRA_APPLICATION_ID, _activity.getPackageName());
                try {
                    _activity.startActivity(intent);
                    override = true;
                } catch (ActivityNotFoundException ex) {
                }
                return override;
            } else {
                showAlert("확인버튼을 누르시면 구글플레이로 이동합니다.", "확인", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(("market://details?id=kr.co.uplus.ecredit")));
                        intent.addCategory(Intent.CATEGORY_BROWSABLE);
                        intent.putExtra(Browser.EXTRA_APPLICATION_ID, _activity.getPackageName());
                        _activity.startActivity(intent);
                        _activity.overridePendingTransition(0, 0);
                    }
                }, "취소", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                return true;
            }
        }
        // 모바일ISP 커스텀 스키마
        else if (url.startsWith("ispmobile://")) {
            boolean isatallFlag = isPackageInstalled(_activity.getApplicationContext(), "kvp.jjy.MispAndroid320");
            if (isatallFlag) {
                boolean override = false;
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                intent.addCategory(Intent.CATEGORY_BROWSABLE);
                intent.putExtra(Browser.EXTRA_APPLICATION_ID, _activity.getPackageName());
                try {
                    _activity.startActivity(intent);
                    override = true;
                } catch (ActivityNotFoundException ex) {
                }
                return override;
            } else {
                showAlert("확인버튼을 누르시면 구글플레이로 이동합니다.", "확인", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        view.loadUrl("http://mobile.vpay.co.kr/jsp/MISP/andown.jsp");
                    }
                }, "취소", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                return true;
            }
        } else if (url.startsWith("paypin://")) {
            boolean isatallFlag = isPackageInstalled(_activity.getApplicationContext(), "com.skp.android.paypin");
            if (isatallFlag) {
                boolean override = false;
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                intent.addCategory(Intent.CATEGORY_BROWSABLE);
                intent.putExtra(Browser.EXTRA_APPLICATION_ID, _activity.getPackageName());

                try {
                    _activity.startActivity(intent);
                    override = true;
                } catch (ActivityNotFoundException ex) {
                }
                return override;
            } else {
                Intent intent = new Intent(Intent.ACTION_VIEW,
                        Uri.parse(("market://details?id=com.skp.android.paypin&feature=search_result#?t=W251bGwsMSwxLDEsImNvbS5za3AuYW5kcm9pZC5wYXlwaW4iXQ..")));
                intent.addCategory(Intent.CATEGORY_BROWSABLE);
                intent.putExtra(Browser.EXTRA_APPLICATION_ID, _activity.getPackageName());
                _activity.startActivity(intent);
                _activity.overridePendingTransition(0, 0);
                return true;
            }
        } else if (url.startsWith("lguthepay://")) {
            boolean isatallFlag = isPackageInstalled(_activity.getApplicationContext(), "com.lguplus.paynow");
            if (isatallFlag) {
                boolean override = false;
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                intent.addCategory(Intent.CATEGORY_BROWSABLE);
                intent.putExtra(Browser.EXTRA_APPLICATION_ID, _activity.getPackageName());
                try {
                    _activity.startActivity(intent);
                    override = true;
                } catch (ActivityNotFoundException ex) {
                }
                return override;
            } else {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(("market://details?id=com.lguplus.paynow")));
                intent.addCategory(Intent.CATEGORY_BROWSABLE);
                intent.putExtra(Browser.EXTRA_APPLICATION_ID, _activity.getPackageName());
                _activity.startActivity(intent);
                _activity.overridePendingTransition(0, 0);
                return true;
            }
        }
        return false;
    }

    // SmartXPay_AppToWeb | App 체크 메소드 // 존재:true, 존재하지않음:false
    private boolean isPackageInstalled(Context ctx, String pkgName) {
        try {
            ctx.getPackageManager().getPackageInfo(pkgName, PackageManager.GET_ACTIVITIES);
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(TAG, e.getMessage(), e);
            return false;
        }
        return true;
    }

    // SmartXPay_AppToWeb | 알림창
    private void showAlert(String message, String positiveButton, DialogInterface.OnClickListener positiveListener, String negativeButton, DialogInterface.OnClickListener negativeListener) {
        AlertDialog.Builder alert = new AlertDialog.Builder(_activity);
        alert.setMessage(message);
        alert.setPositiveButton(positiveButton, positiveListener);
        alert.setNegativeButton(negativeButton, negativeListener);
        alert.show();
    }

    // SmartXPay_AppToWeb 결제 | 백신 APK 다운로드 처리에 대한 구현 | 비동기 File Download 구현
    private void downloadFile(String mUrl) {
        new DownloadFileTask().execute(mUrl);
    }

    // SmartXPay_AppToWeb 결제 | 백신 APK 다운로드 처리에 대한 구현 | AsyncTask<Params,Progress,Result>
    private class DownloadFileTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... urls) {
            URL myFileUrl = null;
            try {
                myFileUrl = new URL(urls[0]);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            try {
                HttpURLConnection conn = (HttpURLConnection) myFileUrl.openConnection();
                conn.setDoInput(true);
                conn.connect();
                InputStream is = conn.getInputStream();

                // 다운 받는 파일의 경로는 sdcard/ 에 저장되며 sdcard에 접근하려면 uses-permission에 android.permission.WRITE_EXTERNAL_STORAGE을 추가해야만 가능.
                String mPath = "sdcard/v3mobile.apk";
                FileOutputStream fos;
                File f = new File(mPath);
                if (f.createNewFile()) {
                    fos = new FileOutputStream(mPath);
                    int read;
                    while ((read = is.read()) != -1) {
                        fos.write(read);
                    }
                    fos.close();
                }

                return "v3mobile.apk";
            } catch (IOException e) {
                e.printStackTrace();
                return "";
            }
        }

        @Override
        protected void onPostExecute(String filename) {
            if (!"".equals(filename)) {
                Toast.makeText(_activity.getApplicationContext(), "download complete", Toast.LENGTH_SHORT).show();

                // 안드로이드 패키지 매니저를 사용한 어플리케이션 설치.
                File apkFile = new File(Environment.getExternalStorageDirectory() + "/" + filename);
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(Uri.fromFile(apkFile), "application/vnd.android.package-archive");
                _activity.startActivity(intent);
            }
        }
    }

    private class CustomScript {
        private CustomScript() {
        }

        @JavascriptInterface
        public void test(String str) {
            Toast.makeText(_activity, str, Toast.LENGTH_SHORT).show();
        }

        @JavascriptInterface
        public void finishPage(String appExit) {
            if ("Y".equalsIgnoreCase(appExit)) {
                _activity.finish();
            } else {
                _activity.onBackPressed();
            }
        }

        /*@JavascriptInterface
        public void autoLogin(String isLogin) {
            _shared.putIsLogin(isLogin);
            if (Const.NO.equalsIgnoreCase(isLogin)) {
                if (StringUtil.nullCheckB(_shared.getMemberId()) && StringUtil.nullCheckB(_shared.getMemberOkey())) {
                    _wv.post(new Runnable() {
                        @Override
                        public void run() {
                            String params;
                            try {
                                params = "login_id=" + URLEncoder.encode(_shared.getMemberId(), "UTF-8") + "&login_pass=" + URLEncoder.encode(_shared.getMemberOkey(), "UTF-8");
                            } catch (UnsupportedEncodingException e) {
                                _shared.clearUserInfo();
                                return;
                            }
                            try {
                                params += "&rurl=" + Uri.parse(_wv.getUrl()).getPath();
                            } catch (Exception e) {
                                Log.e(TAG, e.getMessage(), e);
                            }
                            _wv.postUrl(Const.URL_HOME + "/member/login_process", params.getBytes());
                        }
                    });
                }
            }
        }*/

        @JavascriptInterface
        public void saveMemberInfo(String no, String id, String okey) {

        }

        @JavascriptInterface
        public void logout() {
        }

        @JavascriptInterface
        public void setSiteInfo(Uri uri) {

        }
    }
}
