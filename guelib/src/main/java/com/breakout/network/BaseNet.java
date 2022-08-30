package com.breakout.network;

import com.breakout.util.Log;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Vector;

import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.HttpVersion;
import cz.msebera.android.httpclient.NameValuePair;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.entity.UrlEncodedFormEntity;
import cz.msebera.android.httpclient.client.methods.HttpDelete;
import cz.msebera.android.httpclient.client.methods.HttpGet;
import cz.msebera.android.httpclient.client.methods.HttpPost;
import cz.msebera.android.httpclient.client.methods.HttpPut;
import cz.msebera.android.httpclient.client.methods.HttpRequestBase;
import cz.msebera.android.httpclient.client.utils.URIUtils;
import cz.msebera.android.httpclient.client.utils.URLEncodedUtils;
import cz.msebera.android.httpclient.conn.ClientConnectionManager;
import cz.msebera.android.httpclient.conn.scheme.PlainSocketFactory;
import cz.msebera.android.httpclient.conn.scheme.Scheme;
import cz.msebera.android.httpclient.conn.scheme.SchemeRegistry;
import cz.msebera.android.httpclient.conn.ssl.SSLSocketFactory;
import cz.msebera.android.httpclient.entity.ContentType;
import cz.msebera.android.httpclient.entity.StringEntity;
import cz.msebera.android.httpclient.entity.mime.MultipartEntityBuilder;
import cz.msebera.android.httpclient.entity.mime.content.FileBody;
import cz.msebera.android.httpclient.entity.mime.content.StringBody;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;
import cz.msebera.android.httpclient.impl.conn.tsccm.ThreadSafeClientConnManager;
import cz.msebera.android.httpclient.message.BasicNameValuePair;
import cz.msebera.android.httpclient.params.BasicHttpParams;
import cz.msebera.android.httpclient.params.HttpConnectionParams;
import cz.msebera.android.httpclient.params.HttpParams;
import cz.msebera.android.httpclient.params.HttpProtocolParams;
import cz.msebera.android.httpclient.protocol.HTTP;
import cz.msebera.android.httpclient.util.EntityUtils;


/*
    TODO: 2018-07-21 | http 라이브러리 교체 필요
 */

/**
 * http library class
 *
 * @author sung-gue
 * @version 1.0 (2020. 5. 30.)
 */
public class BaseNet {
    private final String TAG = getClass().getSimpleName();

    public enum HttpMethod {
        GET,
        POST,
        PUT,
        DELETE
    }

    private final int HTTP_TIMEOUT = 20 * 1000;
    /**
     * Content-type : application/json
     */

    private final String APPLICATION_JSON = ContentType.APPLICATION_JSON.getMimeType();

    private HttpClient httpClient;
    private static BaseNet _instance;

    private BaseNet() {
        Log.i("BaseNet Instance create");
    }

    public static synchronized BaseNet getInstance() {
        if (_instance == null) _instance = new BaseNet();
        return _instance;
    }

    public static void destroyInstance() {
        if (_instance != null) {
            _instance.shutDownHttpClient();
        }
    }

    /**
     * connection pool setting <br/>
     * HttpClient Params, Scheme, ConnectionManager settings ...
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
        // return new DefaultHttpClient(conMgr, params);
        return new DefaultHttpClient(params);
    }

    /**
     * change http timeout<br/>
     * connection, socket
     *
     * @param timeout timeout < 1 일 경우 {@link #HTTP_TIMEOUT}값으로 초기화
     */
    public void setConnectTimeout(int timeout) {
        if (httpClient != null) {
            if (timeout < 1) timeout = HTTP_TIMEOUT;
            HttpParams params = httpClient.getParams();
            HttpConnectionParams.setConnectionTimeout(params, timeout);
            HttpConnectionParams.setSoTimeout(params, timeout);
        }
    }

    /**
     * ClientConnectionManager exit
     */
    public void shutDownHttpClient() {
        Log.i(TAG, "shutDownHttpClient...");
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (httpClient != null && httpClient.getConnectionManager() != null) {
                    httpClient.getConnectionManager().shutdown();
                }
                httpClient = null;
                _instance = null;
            }
        }).start();
    }

    public String sendRequest(HttpMethod method, String sendUrl,
                              HashMap<String, String> requestHeaderMap,
                              HashMap<String, String> requestMap) throws Exception {
        return sendRequest(method, sendUrl, requestHeaderMap, requestMap, null, null);
    }

    public String sendRequestJsonBody(HttpMethod method, String sendUrl,
                                      HashMap<String, String> requestHeaderMap,
                                      String body) throws Exception {
        return sendRequest(method, sendUrl, requestHeaderMap, null, APPLICATION_JSON, body);
    }

    /**
     * request send : enctype = text/plain<br/>
     *
     * @param method  get, post, delete, put
     * @param url     target url
     * @param headers header map
     * @param params  parameter map : stringBody for text/plain enctype
     * @return response string
     */
    public String sendRequest(HttpMethod method, String url,
                              HashMap<String, String> headers,
                              HashMap<String, String> params,
                              String contentType, String body
    ) throws Exception {
        // if (httpClient == null) httpClient = createHttpClient();
        HttpClient httpClient = createHttpClient();

        StringBuilder logBuilder = new StringBuilder(String.format("\n* http %s request start ...", method));
        logBuilder.append(String.format("\n-- url : %s", url));

        Vector<NameValuePair> requestParams = new Vector<>();
        if (params != null && !params.isEmpty()) {
            StringBuilder query = new StringBuilder();
            logBuilder.append("\n-- set param");
            for (String key : params.keySet()) {
                String value = params.get(key);
                if (value != null) {
                    requestParams.add(new BasicNameValuePair(key, value));
                }
                logBuilder.append(String.format("\n    | %s : %s", key, value));
                query.append(String.format("%s=%s&", key, value));
            }
            logBuilder.append("\n== end set param");
            if (method == HttpMethod.GET) {
                logBuilder.append(String.format("\n-- url (get) : %s?%s", url, query));
            }
        }

        HttpRequestBase httpRequest;
        StringEntity entity;
        URI uri;
        switch (method) {
            case GET:
            case DELETE:
            default:
                uri = new URI(url);
                uri = URIUtils.createURI(
                        uri.getScheme(),
                        uri.getHost(),
                        uri.getPort(),
                        uri.getPath(),
                        URLEncodedUtils.format(requestParams, HTTP.UTF_8),
                        null
                );
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
                if (APPLICATION_JSON.equalsIgnoreCase(contentType)) {
                    entity = new StringEntity(body, HTTP.UTF_8);
                    entity.setContentType(APPLICATION_JSON);
                    logBuilder.append(String.format("\n-- set json body\n    | %s ", body));
                } else {
                    entity = new UrlEncodedFormEntity(requestParams, HTTP.UTF_8);
                }
                switch (method) {
                    case PUT:
                        HttpPut put = new HttpPut(url);
                        put.setEntity(entity);
                        httpRequest = put;
                        break;
                    case POST:
                    default:
                        HttpPost post = new HttpPost(url);
                        post.setEntity(entity);
                        httpRequest = post;
                        break;
                }
                break;
        }

        if (headers != null && !headers.isEmpty()) {
            logBuilder.append("\n-- set header");
            for (String key : headers.keySet()) {
                String value = headers.get(key);
                if (value != null) {
                    httpRequest.setHeader(key, value);
                }
                logBuilder.append(String.format("\n    | %s : %s", key, value));
            }
            logBuilder.append("\n== end set header");
        }

        HttpResponse response = null;
        try {
            response = httpClient.execute(httpRequest);
        } catch (Exception e) {
            logBuilder.append(String.format("\n-- Exception | %s\n=== http request end", e.getMessage()));
            Log.e(TAG, logBuilder.toString());
            throw e;
        }
        String responseStr = EntityUtils.toString(response.getEntity(), HTTP.UTF_8);
        logBuilder.append(String.format("\n-- response \n%s\n=== http request end", responseStr));
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
        // if (httpClient == null) httpClient = createHttpClient();
        HttpClient httpClient = createHttpClient();

        StringBuilder logBuilder = new StringBuilder(String.format(
                "\n* http %s multipart request start ... \n-- set params",
                method
        ));
        StringBuilder urlBuilder = new StringBuilder(String.format("\n-- url : %s?", sendUrl));

        /*
            HttpMultipartMode.BROWSER_COMPATIBLE
            HttpMultipartMode.STRICT
         */
        MultipartEntityBuilder entityBuilder = MultipartEntityBuilder.create();
        for (String key : requestImageMap.keySet()) {
            String value = requestImageMap.get(key);
            if (value != null) {
                entityBuilder.addPart(key, new FileBody(new File(value), "image/jpeg"));
            }
            logBuilder.append(String.format("\n    | %s : %s", key, value));
            urlBuilder.append(String.format("%s=%s&", key, value));
        }
        for (String key : requestMap.keySet()) {
            String value = requestMap.get(key);
            if (value != null) {
                entityBuilder.addPart(key, new StringBody(value, HTTP.PLAIN_TEXT_TYPE, Charset.forName(HTTP.UTF_8)));
            }
            logBuilder.append(String.format("\n    | %s : %s", key, value));
            urlBuilder.append(String.format("%s=%s&", key, value));
        }
        logBuilder.append("\nend set params --");
        logBuilder.append(urlBuilder);

        HttpEntity entity = entityBuilder.build();
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

        HttpResponse response = httpClient.execute(httpRequest);
        String responseStr = EntityUtils.toString(response.getEntity(), HTTP.UTF_8);
        logBuilder.append(String.format("\n-- response | %s\n end response --", responseStr));
        Log.d(TAG, logBuilder.toString());
        return responseStr;
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

}