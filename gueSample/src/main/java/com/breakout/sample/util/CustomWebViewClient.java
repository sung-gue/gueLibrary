package com.breakout.sample.util;

import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.CookieManager;
import android.webkit.SslErrorHandler;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Toast;

import com.breakout.sample.BaseActivity;
import com.breakout.sample.Log;
import com.breakout.sample.R;
import com.breakout.sample.constant.Const;
import com.breakout.sample.constant.SharedData;
import com.breakout.util.web.CustomWebChromeClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;


public class CustomWebViewClient extends com.breakout.util.web.CustomWebViewClient {
    public interface CustomWebViewClientListenerEx extends CustomWebViewClientListener {

    }

    private final String TAG;
    private final BaseActivity _baseAct;
    private WebView _wv;
    private CustomWebViewClientListenerEx _listener;
    private Context _context;
    private SharedData _shared;

    private final String LINKAGE_SCHEME;
    private final String DOMAIN = Const.DOMAIN;
    private final String URL_HOME = Const.URL_HOME;

    private boolean _isProgress = false;
    private boolean _isProgressCancel = true;
    private boolean _isFirstFinishPage = true;

    public CustomWebViewClient(BaseActivity baseAct, CustomWebViewClientListenerEx listener, WebView wv, int mode) {
        super(baseAct, wv, listener);
        this._wv = wv;
        this.TAG = super.TAG + "|" + baseAct.getClass().getSimpleName();
        this._baseAct = baseAct;
        this._context = baseAct;
        this._shared = SharedData.getInstance(_context);
        this._listener = listener;
        LINKAGE_SCHEME = _context.getString(R.string.app_scheme);
        webViewSetting(wv, mode);
    }

    private void webViewSetting(WebView wv, int mode) {
        WebSettings settings = initWebView(wv, mode);
        setAllowCookie(_context, _wv);

        wv.setWebChromeClient(new CustomWebChromeClient(_listener) {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                Log.d(TAG, "onProgressChanged : " + newProgress);
            }

            @Override
            public void onCloseWindow(WebView window) {
                Log.d(TAG, "WebChromeClient : onCloseWindow");
                super.onCloseWindow(window);
                Object obj = window.getTag();
                if (obj instanceof Dialog) {
                    ((Dialog) obj).dismiss();
                } else {
                    ((ViewGroup) obj).removeView(window);
                }
                window.destroy();
            }

            @Override
            public boolean onCreateWindow(WebView view, boolean isDialog, boolean isUserGesture, Message resultMsg) {
                Log.d(TAG, "WebChromeClient : onCreateWindow");
                final WebView wv = new WebView(_context);
                CustomWebViewClient client = new CustomWebViewClient(_baseAct, _listener, wv, WebSettings.LOAD_DEFAULT);
                wv.setLayoutParams(new ViewGroup.LayoutParams(-1, -1));

                if (isDialog && false) {
                    Dialog dialog = new Dialog(_context) {
                        @Override
                        public void dismiss() {
                            super.dismiss();
                            wv.destroy();
                        }
                    };
                    dialog.setContentView(wv);
                    dialog.getWindow().setLayout(-1, -1);
                    dialog.setCancelable(true);
                    dialog.setCanceledOnTouchOutside(false);
                    dialog.show();
                    wv.setTag(dialog);
                } else {
                    WebView parent = getTopWebView(view);
                    parent.addView(wv);
                    if (parent.getChildCount() == 1) {
                        wv.setY(parent.getScrollY());
                    }
                    wv.setTag(view);
                }

                WebView.WebViewTransport transport = (WebView.WebViewTransport) resultMsg.obj;
                transport.setWebView(wv);
                resultMsg.sendToTarget();
                return true;
                //return super.onCreateWindow(view, isDialog, isUserGesture, resultMsg);
            }

            private WebView getTopWebView(WebView view) {
                if (view.getParent() instanceof WebView) {
                    return getTopWebView((WebView) view.getParent());
                } else return view;
            }
        });
//        manageKeyboard();
    }

    /**
     * @param isGo 상태체크만 하려면 false
     * @return canGoBack
     */
    public boolean webViewGoBack(boolean isGo) {
        boolean isCanGoBack = false;
        int childCount = _wv.getChildCount();
        if (childCount == 0) {
            isCanGoBack = _wv.canGoBack();
            if (isCanGoBack && isGo) {
                _wv.goBack();
            }
        } else if (childCount > 0) {
            View view = _wv.getChildAt(childCount - 1);
            if (view instanceof WebView) {
                isCanGoBack = true;
                WebView wv = (WebView) view;
                if (isGo) {
                    if (wv.canGoBack()) {
                        wv.goBack();
                    } else {
                        _wv.removeView(wv);
                        wv.destroy();
                        if (_listener != null) {
                            _listener.onWebViewLoadFinish(wv);
                        }
                    }
                }
            }
        }
        return isCanGoBack;
    }

    /**
     * @param isGo 상태체크만 하려면 false
     * @return canGoForward
     */
    public boolean webViewGoForward(boolean isGo) {
        boolean isCanGoForward = false;
        int childCount = _wv.getChildCount();
        if (childCount == 0) {
            isCanGoForward = _wv.canGoForward();
            if (isCanGoForward && isGo) {
                _wv.goForward();
            }
        } else if (childCount > 0) {
            View view = _wv.getChildAt(childCount - 1);
            if (view instanceof WebView) {
                WebView wv = (WebView) view;
                isCanGoForward = wv.canGoForward();
                if (isCanGoForward && isGo) {
                    wv.goForward();
                }
            }
        }
        return isCanGoForward;
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
            try {
                Dialog dialog = _baseAct.showProgress();
                dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
                if (dialog != null && _isProgressCancel) {
                    dialog.setCancelable(true);
                    dialog.setCanceledOnTouchOutside(true);
                }
            } catch (Exception e) {
                Log.e(TAG, e.getMessage(), e);
            }
        }
    }

    @Override
    protected void closeProgress() {
        if (_baseAct != null && !_baseAct.isFinishing() && _isProgress) {
            try {
                _baseAct.closeProgress();
            } catch (Exception e) {
                Log.e(TAG, e.getMessage(), e);
            }
        }
    }

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        Log.i(super.TAG, "shouldOverrideUrlLoading | url : " + url);
        /*boolean payResult = shouldOverrideUrlLoadingOfPay(view, url);
        if (payResult) {
            return true;
        }*/

        Uri uri = Uri.parse(url);
        String scheme = uri.getScheme();
        String host = uri.getHost();
        String path = uri.getPath();
        String query = uri.getQuery();

        String[] protectLaunchAppHostList = new String[]{
                "add-host"
                , "www.gmarket.co.kr"           // gmarket
                , "mobile.gmarket.co.kr"        // gmarket
                , "banner.auction.co.kr"        // auction
        };
        // INFO: intent://gmkt.page.link?link=url
        String[] protectLaunchChromeIntentHostList = new String[]{
                "add-host"
                , "gmkt.page.link"      // gmarket
                , "iac.page.link"       // auction
        };
        String[] protectLaunchChromeIntentQueryList = new String[]{
                "add-key"
                , "link"      // gmarket
                , "link"       // auction
        };
        String[] protectLaunchAppSchemeList = new String[]{
                "add-scheme"        // add
                //, "coupang"
                , "homeplusadlink"  // homplus
        };
        // INFO: market://id=com.coupang.mobile
        String[] protectLaunchMarketIdList = new String[]{
                "add-market-id"
                , "com.coupang.mobile"          // coupang
        };
        String[] urlListOfprotectLaunchMarketId = new String[]{
                "add-url"
                , "https://m.coupang.com"       // coupang
        };

        if (LINKAGE_SCHEME.equalsIgnoreCase(scheme)) {
            // INFO: 2020-03-31 쇼핑폴더 웹페이지가 추가되면 쇼핑폴더 앱 화면으로 이동이 가능한 코드 작성
        }
        // INFO: 2019-12-03 사이트주소로 핸들링이 필요할 때 현재 부분에 코드 작성
        else if ((HTTP.equalsIgnoreCase(scheme) || HTTPS.equalsIgnoreCase(scheme)) && host != null && host.contains(DOMAIN)) {
            /*String addQueryUrl = addQuery(url);
            if (addQueryUrl != null && !addQueryUrl.equals(url)) {
                view.loadUrl(addQueryUrl);
                return true;
            }*/
        }
        // INFO: 2019-12-03 external:// , externals:// 일 경우 외부 브라우저 호출
        else if (EXTERNAL.equalsIgnoreCase(scheme) || EXTERNALS.equalsIgnoreCase(scheme)) {
            if (EXTERNAL.equalsIgnoreCase(scheme)) {
                url = url.replace(EXTERNAL + "://", HTTP + "://");
            } else if (EXTERNALS.equalsIgnoreCase(scheme)) {
                url = url.replace(EXTERNALS + "://", HTTPS + "://");
            }
            startIntentForActionView(url, null);
            return true;
        }
        // INFO: 쇼핑몰 앱 실행 방지
        else if (Arrays.asList(protectLaunchAppHostList).contains(host)) {
            //if (uri.getQueryParameterNames().contains("app_gate")) {}
            if (!TextUtils.isEmpty(uri.getQueryParameter("app_gate"))) {
                url = url.replace("app_gate=Y", "app_gate=N");
                url = url.replace("app_gate=y", "app_gate=n");
            } else if (!TextUtils.isEmpty(uri.getQueryParameter("APP_GATE"))) {
                url = url.replace("APP_GATE=Y", "APP_GATE=N");
                url = url.replace("APP_GATE=y", "APP_GATE=n");
            } else if (!TextUtils.isEmpty(uri.getQueryParameter("appgate"))) {
                url = url.replace("appgate=Y", "appgate=N");
                url = url.replace("appgate=y", "appgate=n");
            }
            _wv.loadUrl(url);
            return true;
        }
        // INFO: 크롬 인텐트 처리 intent://host/path?
        else if (CHROME_INTENT.equalsIgnoreCase(scheme)) {
            try {
                Intent intent = Intent.parseUri(url, Intent.URI_INTENT_SCHEME);
                // Intent existPackage = _context.getPackageManager().getLaunchIntentForPackage(intent.getPackage());
                if (intent.resolveActivity(_context.getPackageManager()) == null) {
                    String packagename = intent.getPackage();
                    if (packagename != null) {
                        uri = Uri.parse("market://search?q=pname:" + packagename);
                        intent = new Intent(Intent.ACTION_VIEW, uri);
                        _baseAct.startActivity(intent);
                        return true;
                    }
                }
                // TODO: 테스트 필요 , 특정 광고의 브라우저 실힝 제한
                else if (false && Arrays.asList(protectLaunchChromeIntentHostList).contains(host)) {
                    for (int i = 0; i < protectLaunchChromeIntentHostList.length; i++) {
                        String linkUrl = uri.getQueryParameter(protectLaunchChromeIntentQueryList[i]);
                        if (protectLaunchChromeIntentHostList[i].equalsIgnoreCase(host) && !TextUtils.isEmpty(linkUrl)) {
                            _wv.loadUrl(linkUrl);
                            return true;
                        }
                    }
                }
                intent.addCategory(Intent.CATEGORY_BROWSABLE);
                intent.setComponent(null);
                if (_baseAct.startActivityIfNeeded(intent, -1)) {
                    return true;
                }
            } catch (URISyntaxException e) {
                Log.e(TAG, e.getMessage(), e);
            } catch (ActivityNotFoundException e) {
                Log.e(TAG, e.getMessage(), e);
                Toast.makeText(_baseAct, R.string.al_not_found_app, Toast.LENGTH_LONG).show();
                return true;
            } catch (Exception e) {
                Log.e(TAG, e.getMessage(), e);
            }
            return false;
        }
        // INFO: 웹페이지 로드와 동시에 관련 앱을 호출하는 scheme 방지
        else if (Arrays.asList(protectLaunchAppSchemeList).contains(scheme)) {
//                Toast.makeText(_baseAct, R.string.al_overrideurl_noapp, Toast.LENGTH_LONG).show();
            return true;
        }
        // INFO: play store 실행 scheme인 market:// 을 체크하여 특정 id일 경우 모바일 홈페이지를 구동
        else if ("market".equalsIgnoreCase(scheme) && Arrays.asList(protectLaunchMarketIdList).contains(uri.getQueryParameter("id"))) {
            String marketId = uri.getQueryParameter("id");
            for (int i = 0; i < protectLaunchMarketIdList.length; i++) {
                if (protectLaunchMarketIdList[i].equalsIgnoreCase(marketId)) {
                    _wv.loadUrl(urlListOfprotectLaunchMarketId[i]);
                    break;
                }
            }
            return true;
        }
        return super.shouldOverrideUrlLoading(view, url);
    }


    @Override
    public void onPageStarted(WebView view, String url, Bitmap favicon) {
        super.onPageStarted(view, url, favicon);
    }

    @Override
    public void onPageFinished(final WebView view, String url) {
        super.onPageFinished(view, url);
        printCookie("onPageFinished", url);

        /*_wv.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (_listener != null) {
                    _listener.onWebViewLoadFinish(_wv);
                }
            }
        }, 300);*/

        if (_isFirstFinishPage) {
            view.clearHistory();
            _isFirstFinishPage = false;
        }

        /*try {
            view.loadUrl("javascript:setSiteShareText()");
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        }*/
/*

        String js = getLoginScript();

//            view.loadUrl("javascript:$(document.defaultForm).find('[name=id]').val('test');");
        String script = "$(document.defaultForm).find([name=id]).val('testid');" +
                "$(document.defaultForm).find([name=pwd]).val('testpw');" +
                "//$(document.defaultForm).submit();";
        view.evaluateJavascript(script, new ValueCallback<String>() {
            @Override
            public void onReceiveValue(String value) {
                Log.i(TAG, "onReceiveValue |||  " + value);
            }
        });
*/
//        view.loadData(script, "text/html; charset=utf-8", "UTF-8");
    }

    public String getLoginScript() {
        String js = null;
        try {
            StringBuilder builder = new StringBuilder();
            InputStream stream = _baseAct.getAssets().open("script/test.js");
            BufferedReader reader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8));
            String str;
            while ((str = reader.readLine()) != null) {
                builder.append(str);
            }
            stream.close();
            js = builder.toString();
            Log.i(TAG, js);
        } catch (IOException e) {
            Log.e(TAG, e.getMessage(), e);
        }
        return js;
    }

    @Override
    public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
        super.onReceivedError(view, errorCode, description, failingUrl);
        Toast.makeText(_context, description, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
        super.onReceivedError(view, request, error);
    }

    @Override
    public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
        super.onReceivedSslError(view, handler, error);
    }

    @Override
    public Map<String, String> printCookie(String msg, String url) {
        Map<String, String> cookieMap = new HashMap<>();
        try {
            CookieManager cookieManager = CookieManager.getInstance();
            String cookies = cookieManager.getCookie(url);
            StringBuilder log = new StringBuilder();
            log.append(String.format("print cookie - %s\n  url : %s", msg, url));
            if (cookies != null) {
                String[] cookie = Pattern.compile(";\\s+").split(cookies);
                for (String temp : cookie) {
                    String[] temps = Pattern.compile("=").split(temp);
                    if (temps.length > 1) {
                        log.append(String.format("\n  %s = %s", temps[0], temps[1]));
                        cookieMap.put(temps[0], temps[1]);
                    }
                }
            }
            Log.i(TAG, log.toString());
        } catch (Exception e) {
            Log.e(true, TAG, e.getMessage(), e);
        }
        return cookieMap;
//        return super.printCookie(url);
    }
}

