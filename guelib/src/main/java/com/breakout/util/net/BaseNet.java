package com.breakout.util.net;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.breakout.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.utils.URIUtils;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Vector;


/*
    TODO: 2018-07-21 | http 라이브러리 교체 필요
    useLibrary 'org.apache.http.legacy'     // api 23 : Apache HTTP Client Removal
 */

/**
 * http library class
 *
 * @author sung-gue
 * @version 1.0 (2012. 5. 30.)
 */
public class BaseNet {
    private final String TAG = getClass().getSimpleName();
    private HttpClient _client;
    private final int HTTP_TIMEOUT = 20 * 1000;
    private static BaseNet _this;

    private BaseNet() {
        Log.i("BaseNet Instance create");
    }

    public static synchronized BaseNet getInstance() {
        if (_this == null) _this = new BaseNet();
        return _this;
    }

    public static void destroyInstance() {
        if (_this != null) _this.shutDownHttpClient();
    }

    /**
     * request send : enctype = text/plain<br/>
     *
     * @param method           get, post, delete, put
     * @param sendUrl          target url
     * @param requestHeaderMap header map
     * @param requestMap       parameter map : stringBody for text/plain enctype
     * @return response string
     */
    @Deprecated
    public String sendGet(HttpMethod method, String sendUrl, HashMap<String, String> requestHeaderMap, HashMap<String, String> requestMap) throws Exception {
        if (_client == null) _client = createHttpClient();

        StringBuilder logBuilder = new StringBuilder(String.format("start http %s ... \n-- set params", method));
        StringBuilder urlBuilder = new StringBuilder(String.format("\n    url : %s?", sendUrl));

        Vector<NameValuePair> params = new Vector<>();
        for (String key : requestMap.keySet()) {
            String value = requestMap.get(key);
            if (value != null) {
                params.add(new BasicNameValuePair(key, value));
            }
            logBuilder.append(String.format("\n  %s : %s", key, value));
            urlBuilder.append(String.format("%s=%s&", key, value));
        }
        logBuilder.append(urlBuilder);
        logBuilder.append("\nend set params --");

        URI uri = new URI(sendUrl);
        uri = URIUtils.createURI(uri.getScheme(), uri.getHost(), uri.getPort(), uri.getPath(), URLEncodedUtils.format(params, HTTP.UTF_8), null);
        HttpGet httpRequest = new HttpGet(uri);

        logBuilder.append("\n-- set header");
        for (String key : requestHeaderMap.keySet()) {
            String value = requestHeaderMap.get(key);
            httpRequest.setHeader(key, value);
            logBuilder.append(String.format("\n    | %s : %s", key, value));
        }
        logBuilder.append("\nend set header --");

        HttpResponse response = _client.execute(httpRequest);
        String responseStr = EntityUtils.toString(response.getEntity(), HTTP.UTF_8);
        logBuilder.append(String.format("\n-- response | %s\n end response --", responseStr));
        Log.d(TAG, logBuilder.toString());
        return responseStr;
    }

    /**
     * request send : enctype = text/plain<br/>
     *
     * @param method           get, post, delete, put
     * @param sendUrl          target url
     * @param requestHeaderMap header map
     * @param requestMap       parameter map : stringBody for text/plain enctype
     * @return response string
     */
    public String sendRequest(HttpMethod method, String sendUrl, HashMap<String, String> requestHeaderMap, HashMap<String, String> requestMap) throws Exception {
        if (_client == null) _client = createHttpClient();

        StringBuilder logBuilder = new StringBuilder(String.format("start http %s request start ... \n-- set params", method));
        StringBuilder urlBuilder = new StringBuilder(String.format("\n-- url : %s?", sendUrl));

        Vector<NameValuePair> params = new Vector<>();
        for (String key : requestMap.keySet()) {
            String value = requestMap.get(key);
            if (value != null) {
                params.add(new BasicNameValuePair(key, value));
            }
            logBuilder.append(String.format("\n    | %s : %s", key, value));
            urlBuilder.append(String.format("%s=%s&", key, value));
        }
        logBuilder.append("\nend set params --");
        logBuilder.append(urlBuilder);

        HttpRequestBase httpRequest;
        StringEntity entity;
        URI uri;
        switch (method) {
            case GET:
            case DELETE:
            default:
                uri = new URI(sendUrl);
                uri = URIUtils.createURI(uri.getScheme(), uri.getHost(), uri.getPort(), uri.getPath(), URLEncodedUtils.format(params, HTTP.UTF_8), null);
                switch (method) {
                    case DELETE:
                        httpRequest = new HttpDelete(uri);
                        break;
                    case GET:
                    default:
                        httpRequest = new HttpGet(uri);
                        break;
                }
                break;
            case PUT:
            case POST:
                if (requestHeaderMap.containsValue("application/json")) {
                    entity = new StringEntity(requestMap.get("body"), HTTP.UTF_8);
                    entity.setContentType("application/json");
                } else {
                    entity = new UrlEncodedFormEntity(params, HTTP.UTF_8);
                }
                switch (method) {
                    case PUT:
                        HttpPut put = new HttpPut(sendUrl);
                        put.setEntity(entity);
                        httpRequest = put;
                        break;
                    case POST:
                    default:
                        HttpPost post = new HttpPost(sendUrl);
                        post.setEntity(entity);
                        httpRequest = post;
                        break;
                }
                break;
        }

        logBuilder.append("\n-- set header");
        for (String key : requestHeaderMap.keySet()) {
            String value = requestHeaderMap.get(key);
            httpRequest.setHeader(key, value);
            logBuilder.append(String.format("\n    | %s : %s", key, value));
        }
        logBuilder.append("\nend set header --");

        HttpResponse response = _client.execute(httpRequest);
        String responseStr = EntityUtils.toString(response.getEntity(), HTTP.UTF_8);
        logBuilder.append(String.format("\n-- response | %s\n end response --", responseStr));
        Log.d(TAG, logBuilder.toString());
        return responseStr;
    }

    /**
     * request send : enctype = multipart : 단일 이미지<br/>
     *
     * @param method           post, put
     * @param sendUrl          target url
     * @param requestHeaderMap header map
     * @param requestMap       parameter map : stringBody for text/plain enctype
     * @param imageParam       key : parameter name
     * @param imagePath        value : image absolute path
     * @return response string
     */
    @Deprecated
    public String sendMultiPartRequest(HttpMethod method, String sendUrl, HashMap<String, String> requestHeaderMap, HashMap<String, String> requestMap, String imageParam, String imagePath) throws Exception {
        HashMap<String, String> requestImageMap = new HashMap<>();
        requestImageMap.put(imageParam, imagePath);
        return this.sendMultiPartRequest(method, sendUrl, requestHeaderMap, requestMap, requestImageMap);
    }

    /**
     * request send : enctype = multipart : 다수 이미지<br/>
     *
     * @param method           post, put
     * @param sendUrl          target url
     * @param requestHeaderMap header map
     * @param requestMap       parameter map : stringBody for text/plain enctype
     * @param requestImageMap  parameter map : fileBody for multipart/form-data enctype<br/>
     *                         key : parameter name, value : image absolute path
     */
    public String sendMultiPartRequest(HttpMethod method, String sendUrl, HashMap<String, String> requestHeaderMap, HashMap<String, String> requestMap, HashMap<String, String> requestImageMap) throws Exception {
        switch (method) {
            case GET:
            case DELETE:
                throw new Exception("multipart not support GET, DELETE ...");
        }
        if (_client == null) _client = createHttpClient();

        StringBuilder logBuilder = new StringBuilder(String.format("start http %s multipart request start ... \n-- set params", method));
        StringBuilder urlBuilder = new StringBuilder(String.format("\n-- url : %s?", sendUrl));

        /*
            HttpMultipartMode.BROWSER_COMPATIBLE
            HttpMultipartMode.STRICT
         */
        MultipartEntity entity = new MultipartEntity(HttpMultipartMode.STRICT);

        for (String key : requestImageMap.keySet()) {
            String value = requestImageMap.get(key);
            if (value != null) {
                entity.addPart(key, new FileBody(new File(value), "image/jpeg"));
            }
            logBuilder.append(String.format("\n    | %s : %s", key, value));
            urlBuilder.append(String.format("%s=%s&", key, value));
        }

        for (String key : requestMap.keySet()) {
            String value = requestMap.get(key);
            if (value != null) {
                entity.addPart(key, new StringBody(value, HTTP.PLAIN_TEXT_TYPE, Charset.forName(HTTP.UTF_8)));
            }
            logBuilder.append(String.format("\n    | %s : %s", key, value));
            urlBuilder.append(String.format("%s=%s&", key, value));
        }
        logBuilder.append("\nend set params --");
        logBuilder.append(urlBuilder);

        HttpRequestBase httpRequest;
        switch (method) {
            case PUT:
                HttpPut put = new HttpPut(sendUrl);
                put.setEntity(entity);
                httpRequest = put;
                break;
            case POST:
            default:
                HttpPost post = new HttpPost(sendUrl);
                post.setEntity(entity);
                httpRequest = post;
                break;
        }

        logBuilder.append("\n-- set header");
        for (String key : requestHeaderMap.keySet()) {
            String value = requestHeaderMap.get(key);
            httpRequest.setHeader(key, value);
            logBuilder.append(String.format("\n    | %s : %s", key, value));
        }
        logBuilder.append("\nend set header --");

        HttpResponse response = _client.execute(httpRequest);
        String responseStr = EntityUtils.toString(response.getEntity(), HTTP.UTF_8);
        logBuilder.append(String.format("\n-- response | %s\n end response --", responseStr));
        Log.d(TAG, logBuilder.toString());
        return responseStr;
    }


    /* ------------------------------------------------------------
        DESC: connection pool setting
     */

    /**
     * connection pool setting <br/>
     * HttpClient Params, Scheme, ConnectionManager 설정
     *
     * @return defaultHttpClient
     */
    private HttpClient createHttpClient() {
        Log.i(TAG, "Create HttpClient...");
        HttpParams params = new BasicHttpParams();
        HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
        HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);
        HttpProtocolParams.setUseExpectContinue(params, true);
        HttpConnectionParams.setConnectionTimeout(params, HTTP_TIMEOUT);
        HttpConnectionParams.setSoTimeout(params, HTTP_TIMEOUT);
//        ConnManagerParams.setTimeout(params, HTTP_TIMEOUT);

        SchemeRegistry schemeReg = new SchemeRegistry();
        schemeReg.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
        schemeReg.register(new Scheme("https", SSLSocketFactory.getSocketFactory(), 443));
//        schemeReg.register(new Scheme("https", PlainSocketFactory.getSocketFactory(), 443));

        ClientConnectionManager conMgr = new ThreadSafeClientConnManager(params, schemeReg);
        return new DefaultHttpClient(conMgr, params);
    }

    /**
     * change http timeout<br/>
     * connection, socket
     *
     * @param timeout timeout < 1 일 경우 {@link #HTTP_TIMEOUT}값으로 초기화
     */
    public void setConnectTimeout(int timeout) {
        if (_client != null) {
            if (timeout < 1) timeout = HTTP_TIMEOUT;
            HttpParams params = _client.getParams();
            HttpConnectionParams.setConnectionTimeout(params, timeout);
            HttpConnectionParams.setSoTimeout(params, timeout);
        }
    }


    /* ------------------------------------------------------------
        DESC: ClientConnectionManager exit
     */

    /**
     * ClientConnectionManager exit
     */
    public void shutDownHttpClient() {
        Log.i(TAG, "shutDownHttpClient...");
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (_client != null && _client.getConnectionManager() != null) {
                    _client.getConnectionManager().shutdown();
                }
                _client = null;
                _this = null;
            }
        }).start();
    }

    public String urlDecode(String str) {
        String decodeStr = str;
        try {
            if (str != null) decodeStr = URLDecoder.decode(str, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            Log.e(TAG, "UnsupportedEncodingException - " + e.getMessage(), e);
        } catch (Exception e) {
            Log.e(TAG, "Exception - " + e.getMessage(), e);
        }
        return decodeStr;
    }


    /* ------------------------------------------------------------
        DESC: NetWork state check
     */
    private final int NET_NOT_CONN = -1;
    private final int NET_UNCHECKED = -2;
    /**
     * NetWork state value
     *
     * @see #getNetState(Context)
     */
    private static int netState = -3;

    /**
     * NetWork state check
     *
     * @param context application context
     * @return 현재 NetWork 상태값
     * <dd>
     * <li>1 : wifi</li>
     * <li>0 : mobile</li>
     * <li>-1 : NetWork에 연결할 수 없음</li>
     * <li>-2 : NetWork 상태 체크 실패</li>
     * </dd>
     */
    public int getNetState(Context context) {
        int state = NET_UNCHECKED;
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm != null) {
            @SuppressLint("MissingPermission") NetworkInfo ni = cm.getActiveNetworkInfo();
            if (ni == null) {
                state = NET_NOT_CONN;
            } else if (ni.isConnected() && ni.isAvailable()) {
                state = ni.getType();
                if (state == ConnectivityManager.TYPE_MOBILE && state != netState) netState = state;
            }
            if (state < 0) netState = state;
        }
        Log.d(TAG, "NetworkInfo : " + state);
        return state;
    }
}