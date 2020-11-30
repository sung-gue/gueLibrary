package com.breakout.util.web;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.RequiresApi;

import com.breakout.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;

import de.jetwick.snacktory.ArticleTextExtractor;
import de.jetwick.snacktory.HtmlFetcher;
import de.jetwick.snacktory.JResult;
import de.jetwick.snacktory.OutputFormatter;
import de.jetwick.snacktory.SHelper;


/**
 * The type Analyze web page.
 *
 * @author sung-gue
 * @version 1.0 (2016-04-19)
 */
public class AnalyzeWebPage extends Thread {
    private final String TAG = getClass().getSimpleName();

    public class AnalyzeWebPageData {
        public String url;
        public String title;
        public String description;
        public String imageUrl;
        public String contents;

        public Document document;
        public String html;
    }

    private Context _context;
    private Handler _handler;
    private AnalyzeWebPageData _analyzeWebPageData = new AnalyzeWebPageData();
    private String _html;

    private final long _startTime;

    private int _tryCount = 0;

    private final String USER_AGENT_ANDROID = "Mozilla/5.0 (Linux; Android 5.1.1; SM-N916K Build/LMY47X) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/49.0.2623.91 Mobile Safari/537.36";
    private final String USER_AGENT_WINDOW10 = "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/57.0.2987.133 Safari/537.36";
    private final String USER_AGENT_WINDOW7 = "Mozilla/5.0 (Windows NT 6.2; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/32.0.1667.0 Safari/537.36";
    private final String USER_AGENT_MAC = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_3) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/80.0.3987.132 Safari/537.36";
    private final String USER_AGENT = USER_AGENT_WINDOW10;


    public AnalyzeWebPage(Context context, Handler handler, String url) {
        _context = context;
        _handler = handler;
        _analyzeWebPageData.url = url;
        _startTime = System.currentTimeMillis();
    }

    public AnalyzeWebPage setHtml(String html) {
        _html = html;
        return this;
    }

    @Override
    public void run() {
        super.run();
        analyzeWebPage(_html);
    }

    private void analyzeWebPage(String html) {
        // init value
        Document document = null;
        String url = _analyzeWebPageData.url;
        ArticleTextExtractor extractor = new ArticleTextExtractor();
        extractor.setOutputFormatter(new OutputFormatter(20));
        JResult jResult = new JResult();
        HtmlFetcher fetcher = new HtmlFetcher();

        // analyze web page
        try {
            // analyze url
            if (TextUtils.isEmpty(html)) {
                // exception) youtubu mobile
                if (!TextUtils.isEmpty(url) && Pattern.compile("^(http|https)://[^/]*youtube.*").matcher(url).matches()) {
                    if (Pattern.compile(".*\\?.*").matcher(url).matches()) {
                        url += "&app=desktop";
                    } else {
                        url += "?app=desktop";
                    }
                }

                // sync cookie
                Map<String, String> cookieMap = new HashMap<>();
                try {
                    CookieManager cookieManager = CookieManager.getInstance();
                    String cookies = cookieManager.getCookie(url);
                    StringBuilder log = new StringBuilder("print cookie\n  url : " + url);
                    String[] cookie = Pattern.compile(";\\s+").split(cookies);
                    for (String temp : cookie) {
                        String[] temps = Pattern.compile("=").split(temp);
                        if (temps.length > 1) {
                            log.append(String.format("\n  %s = %s", temps[0], temps[1]));
                            cookieMap.put(temps[0], temps[1]);
                        }
                    }
                    Log.i(TAG, log.toString());
                } catch (Exception e) {
                    Log.e(TAG, e.getMessage(), e);
                }
                // jsoup parsing
                document = Jsoup.connect(url)
                        .userAgent(USER_AGENT)
                        .timeout(4 * 1000)
//                        .referrer("https://www.google.com")
//                        .header("Referer", "https://www.google.com/")
//                        .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8")
//                        .header("Content-Type", "application/x-www-form-urlencoded")
//                        .header("Accept-Encoding", "gzip, deflate, br")
//                        .header("Accept-Language", "ko-KR,ko;q=0.8,en-US;q=0.6,en;q=0.4")
//                        .header("Set-Cookie", "foo=bar; SameSite=None; Secure")
//                        .cookies(cookieMap)
                        .get()
//                        .method(Connection.Method.GET)
//                        .execute().parse()
                ;
//                jResult = fetcher.fetchAndExtract(url, 1000 * 5, true);
            }
            // analyze html
            else {
                // jsoup parsing
                document = Jsoup.parse(html);
                //extractor.extractContent(jResult, html);
            }
            /*
            String selectorStr = "body [id*=content] p";
            selectorStr += ", body [id*=content] span";
            selectorStr += ", body [id*=article] p";
            selectorStr += ", body [id*=article] span";
            selectorStr += ", body [id*=title]";
            selectorStr += ", body article";
            Elements elements = document.select(selectorStr);
            String contents = "";
            for (Element temp : elements) {
                contents += temp.text();
            }
            */
            extractor.extractContent(jResult, document.outerHtml());
            if (jResult.getFaviconUrl().isEmpty()) {
                jResult.setFaviconUrl(SHelper.getDefaultFavicon(url));
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
//            completeAnalyze();
//            return;
        }

        if (document == null) {
            try {
                String responseHtml = fetcher.fetchAsString(url, 1000 * 4, false);
                document = Jsoup.parse(responseHtml);
            } catch (Exception e) {
                Log.e(TAG, e.getMessage(), e);
            }
        }
        setDocument(document);

        String contents = jResult.getText();
        if (!TextUtils.isEmpty(html) || (!TextUtils.isEmpty(contents) && !Pattern.compile(".*(?i)(오류|권한|error|auth)+.*").matcher(contents).matches())) {
            String title = extractTitle(document);
            if (!TextUtils.isEmpty(title)) {
                jResult.setTitle(title);
            }
            String imageUrl = extractImageUrl(document);
            if (!TextUtils.isEmpty(imageUrl)) {
                jResult.setImageUrl(imageUrl);
            }
            String description = extractDescription(document);
            if (!TextUtils.isEmpty(description)) {
                jResult.setDescription(description);
            }
            setTitle(jResult.getTitle());
            setDescription(jResult.getDescription());
            setImage(jResult.getImageUrl());
            setContents(contents);
            completeAnalyze();
        } else {
            if (_tryCount++ == 0) {
//                analyzeWebPageByWebView();
            } else {
                completeAnalyze();
            }
            completeAnalyze();
        }
    }

    public void setTitle(String title) {
        if (!TextUtils.isEmpty(title)) {
            _analyzeWebPageData.title = title.replace("&nbsp;", " ").replace("&lt;", "<").replace("&gt;", ">").replace("&amp;", "&").replace("&quot;", "\"");
        }
    }

    public void setDescription(String description) {
        if (!TextUtils.isEmpty(description)) {
            _analyzeWebPageData.description = description.replace("&nbsp;", " ").replace("&lt;", "<").replace("&gt;", ">").replace("&amp;", "&").replace("&quot;", "\"");
        }
    }

    public void setImage(String imageUrl) {
        if (!TextUtils.isEmpty(imageUrl)) {
            if (imageUrl.startsWith("http://") || imageUrl.startsWith("https://")) {
                _analyzeWebPageData.imageUrl = imageUrl;
            } else if (_analyzeWebPageData.url.startsWith("https://")) {
                _analyzeWebPageData.imageUrl = "https:" + imageUrl;
            } else {
                _analyzeWebPageData.imageUrl = "http:" + imageUrl;
            }
        }
    }

    public void setContents(String contents) {
        if (!TextUtils.isEmpty(contents)) {
            _analyzeWebPageData.contents = contents.replaceAll("[^ㄱ-ㅎㅏ-ㅣ가-힣a-zA-Z0-9_\\-\\s\\.]", "");
        }
    }

    public void setDocument(Document document) {
        if (document != null) {
            _analyzeWebPageData.document = document;
            _analyzeWebPageData.html = document.outerHtml();
        }
    }

    private String extractImageUrl(Document doc) {
        // use open graph tag to get image
        String imageUrl = SHelper.replaceSpaces(doc.select("head meta[property=og:image]").attr("content"));
        if (imageUrl.isEmpty()) {
            imageUrl = SHelper.replaceSpaces(doc.select("meta[property=og:image]").attr("content"));
            if (imageUrl.isEmpty()) {
                imageUrl = SHelper.replaceSpaces(doc.select("head meta[name=twitter:image]").attr("content"));
                if (imageUrl.isEmpty()) {
                    // prefer link over thumbnail-meta if empty
                    imageUrl = SHelper.replaceSpaces(doc.select("link[rel=image_src]").attr("href"));
                    if (imageUrl.isEmpty()) {
                        imageUrl = SHelper.replaceSpaces(doc.select("head meta[name=thumbnail]").attr("content"));
                    }
                }
            }
        }
        return imageUrl;
    }

    private String extractTitle(Document doc) {
        String title = SHelper.innerTrim(doc.select("head meta[property=og:title]").attr("content"));
        if (title.isEmpty()) {
            title = SHelper.innerTrim(doc.select("meta[property=og:title]").attr("content"));
            if (title.isEmpty()) {
                title = SHelper.innerTrim(doc.select("head title").text());
                if (title.isEmpty()) {
                    title = SHelper.innerTrim(doc.select("head meta[name=title]").attr("content"));
                    if (title.isEmpty()) {
                        title = SHelper.innerTrim(doc.select("head meta[property=og:title]").attr("content"));
                        if (title.isEmpty()) {
                            title = SHelper.innerTrim(doc.select("head meta[name=twitter:title]").attr("content"));
                        }
                    }
                }
            }
        }
        return title;
    }

    private String extractDescription(Document doc) {
        String description = SHelper.innerTrim(doc.select("head meta[property=og:description]").attr("content"));
        if (description.isEmpty()) {
            description = SHelper.innerTrim(doc.select("meta[property=og:description]").attr("content"));
            if (description.isEmpty()) {
                description = SHelper.innerTrim(doc.select("head meta[name=description]").attr("content"));
                if (description.isEmpty()) {
                    description = SHelper.innerTrim(doc.select("head meta[name=twitter:description]").attr("content"));
                }
            }
        }
        return description;
    }

    private void analyzeWebPageByWebView() {
        final AnalyzeWebPage analyzeWebPage = new AnalyzeWebPage(_context, _handler, _analyzeWebPageData.url);

        ((Activity) _context).runOnUiThread(new Runnable() {
            private boolean _isPageStart;

            @SuppressLint("SetJavaScriptEnabled")
            @Override
            public void run() {
                final WebView wv = new WebView(_context);
                wv.clearCache(true);
                wv.setHorizontalScrollBarEnabled(false);
                wv.setVerticalScrollBarEnabled(false);

                WebSettings settings = wv.getSettings();
                settings.setCacheMode(WebSettings.LOAD_DEFAULT);
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
                settings.setUserAgentString(USER_AGENT);
                wv.setWebViewClient(new WebViewClient() {
                    @Override
                    public boolean shouldOverrideUrlLoading(WebView view, String url) {
                        Log.i(TAG, " | shouldOverrideUrlLoading : " + url);
                        return super.shouldOverrideUrlLoading(view, url);
                    }

                    @Override
                    public void onPageStarted(WebView view, String url, Bitmap favicon) {
                        _isPageStart = true;
                        Log.i(TAG, " | onPageStarted : " + url);
                        super.onPageStarted(view, url, favicon);
                    }

                    @Override
                    public void onPageFinished(final WebView view, String url) {
                        _isPageStart = false;
                        Log.i(TAG, " | onPageFinished : " + url);
                        super.onPageFinished(view, url);
                        CookieSyncManager.getInstance().sync(); // 페이지 로드가 끝났을 경우 쿠키 분석

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if (!_isPageStart) {
                                    //String js = "javascript:window.BreakOutJs.print(document.getElementsByTagName('html')[0].innerHTML);";
                                    String js = "javascript:window.BreakOutJs.print(document.documentElement.outerHTML);";
                                    view.loadUrl(js);
                                }
                            }
                        }, 1000);
                    }

                    @Override
                    public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                        _isPageStart = false;
                        Log.i(TAG, " | onReceivedError : " + errorCode + " / " + description);
                        super.onReceivedError(view, errorCode, description, failingUrl);
                    }

                    @RequiresApi(api = Build.VERSION_CODES.M)
                    @Override
                    public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                        Log.i(TAG, " | onReceivedError 23 : " + error.getErrorCode() + " / " + error.getDescription());
                        super.onReceivedError(view, request, error);
                    }
                });
                wv.addJavascriptInterface(new Object() {
                    @JavascriptInterface
                    public void print(String html) {
                        Log.i("BreakOutJs.print " + html);
                        analyzeWebPage.setHtml(html);
                        analyzeWebPage.start();
                    }
                }, "BreakOutJs");

                Map<String, String> headers = new HashMap<>();
//                headers.put("Set-Cookie", "HttpOnly;Secure;SameSite=Strict");
                headers.put("Set-Cookie", "SameSite=None; Secure");
                wv.loadUrl(_analyzeWebPageData.url, headers);
            }
        });
    }

    private void completeAnalyze() {
        Log.d(TAG, String.format(Locale.getDefault(), "-------------------------------------------------------------\n" +
                        "| analyze web page (%dms)\n" +
                        "| url          | %s\n" +
                        "| title        | %s\n" +
                        "| description  | %s\n" +
                        "| image        | %s\n" +
                        "| contents     | %s\n" +
                        "-------------------------------------------------------------",
                (System.currentTimeMillis() - _startTime), _analyzeWebPageData.url, _analyzeWebPageData.title, _analyzeWebPageData.description, _analyzeWebPageData.imageUrl,
                _analyzeWebPageData.contents
                )
        );
        Message message = _handler.obtainMessage();
        message.obj = _analyzeWebPageData;
        if (!TextUtils.isEmpty(_analyzeWebPageData.contents)) {
            message.what = 1;
        } else {
            message.what = 2;
        }
        _handler.sendMessage(message);
    }
}