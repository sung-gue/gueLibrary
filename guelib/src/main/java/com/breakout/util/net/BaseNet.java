package com.breakout.util.net;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.breakout.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIUtils;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.Header;
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
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

/**
 * httpclient를 사용하여 서버에 parameter 전송
 *
 * @author gue
 * @version 1.0
 * @copyright Copyright.2011.gue.All rights reserved.
 * @history <ol>
 * <li>변경자/날짜 : 변경사항</li>
 * </ol>
 * @since 2012. 5. 30.
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

/* ************************************************************************************************
 * INFO ewxcute request : enctype = text/plain
 * 2012.08.03 throw 처리를 해당 method를 사용한 쪽에서 처리하게끔 변경
 */

    /**
     * request send : enctype = text/plain<br/>
     * method : get
     *
     * @param sendUrl    url
     * @param requestMap
     * @author gue
     * @since 2015. 3. 31.
     */
    public synchronized String sendGet(String sendUrl, HashMap<String, String> requestHeaderMap, HashMap<String, String> requestMap) throws ClientProtocolException, IOException, Exception {
        if (_client == null) _client = createHttpClient();

        StringBuilder logBuilder = new StringBuilder("[set request parameter] start----------------------------");
        StringBuilder urlBuilder = new StringBuilder(String.format("\n    url : %s?", sendUrl));

        Vector<NameValuePair> params = new Vector<NameValuePair>();
        Iterator<String> keys = requestMap.keySet().iterator();
        while (keys.hasNext()) {
            String key = keys.next();
            params.add(new BasicNameValuePair(key, requestMap.get(key)));
            logBuilder.append(String.format("\n    %s : %s", key, requestMap.get(key)));
            urlBuilder.append(String.format("%s=%s&", key, requestMap.get(key)));
        }

        logBuilder.append(urlBuilder);
        logBuilder.append("\n[set request parameter] end------------------------------");

        URI uri = new URI(sendUrl);
        uri = URIUtils.createURI(uri.getScheme(), uri.getHost(), uri.getPort(), uri.getPath(), URLEncodedUtils.format(params, HTTP.UTF_8), null);
        HttpGet get = new HttpGet(uri);

        logBuilder.append("\n[set header] start------------------------------");
        Iterator<String> headerKeys = requestHeaderMap.keySet().iterator();
        while (headerKeys.hasNext()) {
            String key = headerKeys.next();
            get.setHeader(key,requestHeaderMap.get(key));
            logBuilder.append(String.format("\n    | %s : %s", key, requestHeaderMap.get(key)));
        }
        logBuilder.append("\n[set header] end------------------------------");
        Log.d(TAG, logBuilder.toString());

        HttpResponse response = _client.execute(get);
        return EntityUtils.toString(response.getEntity(), HTTP.UTF_8);
    }
    /**
     * request send : enctype = text/plain<br/>
     * method : get
     *
     * @param sendUrl    url
     * @author gue
     * @since 2015. 3. 31.
     */
    public synchronized String sendGet(String sendUrl) throws ClientProtocolException, IOException, Exception {
        if (_client == null) _client = createHttpClient();

        Log.d(TAG, String.format("url request : %s?", sendUrl));

        HttpGet get = new HttpGet(sendUrl);
        HttpResponse response = _client.execute(get);
        return EntityUtils.toString(response.getEntity(), HTTP.UTF_8);
    }

    /**
     * request send : enctype = text/plain<br/>
     * method : post
     *
     * @param requestMap
     * @author gue
     * @since 2012.05.30
     */
    public synchronized String sendPost(String sendUrl, HashMap<String, String> requestHeaderMap, HashMap<String, String> requestMap) throws ClientProtocolException, IOException, Exception {
        if (_client == null) _client = createHttpClient();

        StringBuilder logBuilder = new StringBuilder("[set request parameter] start----------------------------");
        logBuilder.append(String.format("\n    url : %s?", sendUrl));
        StringBuilder urlBuilder = new StringBuilder(String.format("\n    url : %s?", sendUrl));

        Vector<NameValuePair> params = new Vector<>();
        Iterator<String> keys = requestMap.keySet().iterator();
        while (keys.hasNext()) {
            String key = keys.next();
            params.add(new BasicNameValuePair(key, requestMap.get(key)));
            logBuilder.append(String.format("\n    %s : %s", key, requestMap.get(key)));
            urlBuilder.append(String.format("%s=%s&", key, requestMap.get(key)));
        }

        logBuilder.append(urlBuilder);
        logBuilder.append("\n[set request parameter] end------------------------------");

        HttpPost post = new HttpPost(sendUrl);
//        post.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));

        if (requestHeaderMap.containsValue("application/json")) {
            StringEntity entity = new StringEntity(requestMap.get("body"), HTTP.UTF_8);
            entity.setContentType("application/json");
            post.setEntity(entity);
//            post.setEntity(new StringEntity(requestMap.get("body"), HTTP.UTF_8));
        }
        else {
            post.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
        }

        logBuilder.append("\n[set header] start------------------------------");
        Iterator<String> headerKeys = requestHeaderMap.keySet().iterator();
        while (headerKeys.hasNext()) {
            String key = headerKeys.next();
            post.setHeader(key,requestHeaderMap.get(key));
            logBuilder.append(String.format("\n    | %s : %s", key, requestHeaderMap.get(key)));
        }
        logBuilder.append("\n[set header] end------------------------------");
        Log.d(TAG, logBuilder.toString());

        HttpResponse response = _client.execute(post);
        return EntityUtils.toString(response.getEntity(), HTTP.UTF_8);
    }
/*	public synchronized String send(String sendURL, HashMap<String,String> requestMap) throws ClientProtocolException, IOException, Exception  {
        if (_client == null) _client = createHttpClient();
		
		String log = "[set request parameter] start----------------------------";
		int size = requestMap.size();
		StringBuilder builder = new StringBuilder();
		builder.append(String.format("%s?", sendURL));
		Vector<NameValuePair> params = new Vector<NameValuePair>();
		Iterator<String> keys = requestMap.keySet().iterator();
		while (keys.hasNext()) {
			size--;
			String key = keys.next();
			params.add(new BasicNameValuePair(key, requestMap.get(key)));
			log += "\n    " + key + " : " + requestMap.get(key);
			if (size==0){
				builder.append(String.format("%s=%s", key,requestMap.get(key)));
			}else {
				builder.append(String.format("%s=%s&", key,requestMap.get(key)));
			}
		}
		log += "\n    url : " + builder;
		log += "\n[set request parameter] end------------------------------";
		Log.d(TAG, log);
		
		HttpPost post = new HttpPost(sendURL);
		post.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
		HttpResponse response = _client.execute(post);
		return EntityUtils.toString(response.getEntity(), HTTP.UTF_8);
	}
*/	


/* ************************************************************************************************
 * INFO ewxcute request : enctype = multipart/form-data
 * 2012.08.03 throw 처리를 해당 method를 사용한 쪽에서 처리하게끔 변경
 */

    /**
     * request send multipart : 단일 이미지
     *
     * @param sendUrl    post target
     * @param requestMap
     * @param imageParam parameter name
     * @param imagePath  image absolute path
     * @author gue
     * @since 2012.05.30
     */
    public String sendMultiPart(String sendUrl, HashMap<String, String> requestHeaderMap, HashMap<String, String> requestMap, String imageParam, String imagePath) throws ParseException, IOException, Exception {
        HashMap<String, String> requestImageMap = new HashMap<>();
        requestImageMap.put(imageParam, imagePath);
        return this.sendMultiPart(sendUrl, requestHeaderMap, requestMap, requestImageMap);
    }

    /**
     * request send multipart : 다수 이미지
     *
     * @param sendUrl         post target
     * @param requestMap
     * @param requestImageMap key = parameter name, value = image absolute path
     * @author gue
     * @since 2012.05.30
     */
    public String sendMultiPart(String sendUrl, HashMap<String, String> requestHeaderMap, HashMap<String, String> requestMap, HashMap<String, String> requestImageMap) throws ParseException, IOException, Exception {
        if (_client == null) _client = createHttpClient();

        StringBuilder logBuilder = new StringBuilder("[set request parameter] start----------------------------");
        StringBuilder urlBuilder = new StringBuilder(String.format("\n    url : %s?", sendUrl));

        MultipartEntity entity = new MultipartEntity(HttpMultipartMode.STRICT);  // HttpMultipartMode.BROWSER_COMPATIBLE
        Iterator<String> keys = requestImageMap.keySet().iterator();
        while (keys.hasNext()) {
            String key = keys.next();
            entity.addPart(key, new FileBody(new File(requestImageMap.get(key)), "image/jpeg"));
            logBuilder.append(String.format("\n    %s : %s", key, requestMap.get(key)));
            urlBuilder.append(String.format("%s=%s&", key, requestImageMap.get(key)));
        }

        keys = requestMap.keySet().iterator();
        while (keys.hasNext()) {
            String key = keys.next();
            entity.addPart(key, new StringBody(requestMap.get(key), HTTP.PLAIN_TEXT_TYPE, Charset.forName(HTTP.UTF_8)));
            logBuilder.append(String.format("\n    %s : %s", key, requestMap.get(key)));
            urlBuilder.append(String.format("%s=%s&", key, requestMap.get(key)));
        }

        logBuilder.append(urlBuilder);
        logBuilder.append("\n[set request parameter] end------------------------------");
        Log.d(TAG, logBuilder.toString());

        HttpPost post = new HttpPost(sendUrl);
        post.setEntity(entity);

        // TODO set reauest header

        HttpResponse response = _client.execute(post);
        return EntityUtils.toString(response.getEntity(), HTTP.UTF_8);
    }
/*	public String sendMultiPart(String sendURL, HashMap<String,String> requestMap, HashMap<String,String> requestImageMap) throws ParseException, IOException, Exception {
        if (_client == null) _client = createHttpClient();
		
		MultipartEntity entity = new MultipartEntity(HttpMultipartMode.STRICT);  // HttpMultipartMode.BROWSER_COMPATIBLE
		
		String log = "[set request parameter] start----------------------------";
		int size = requestImageMap.size();
		StringBuilder builder = new StringBuilder();
		builder.append(String.format("%s?", sendURL));
		Iterator<String> keys = requestImageMap.keySet().iterator();
		while (keys.hasNext()) {
			size--;
			String key = keys.next();
			entity.addPart(key, new FileBody(new File(requestImageMap.get(key)), "image/jpeg"));
			log += "\n    " + key + " : " + requestImageMap.get(key);
			if (size==0){
				builder.append(String.format("%s=%s", key,requestImageMap.get(key)));
			}else {
				builder.append(String.format("%s=%s&", key,requestImageMap.get(key)));
			}
		}
		
		keys = requestMap.keySet().iterator();
		size = requestMap.size();
		while (keys.hasNext()) {
			size--;
			String key = keys.next();
			entity.addPart(key, new StringBody(requestMap.get(key), HTTP.PLAIN_TEXT_TYPE, Charset.forName(HTTP.UTF_8)));
			log += "\n    " + key + " : " + requestMap.get(key);
			if (size==0){
				builder.append(String.format("%s=%s", key,requestMap.get(key)));
			}else {
				builder.append(String.format("%s=%s&", key,requestMap.get(key)));
			}
		}
		log += "\n    url : " + builder;
		log += "\n[set request parameter] end------------------------------";
		Log.d(TAG, log);
		
		HttpPost post = new HttpPost(sendURL);
		post.setEntity(entity);
		HttpResponse response = _client.execute(post);
		return EntityUtils.toString(response.getEntity(),"UTF-8");
	}
*/
//	/**
//	 * request에 이미지 추가, MultipartMode일때만 가능
//	 * @author gue
//	 * @since 2012.05.30
//	 * @param param parameter name
//	 * @param imagePath absolute path
//	 */
//	protected void filebody_Image(String param, String imagePath) {
//		File file = new File(imagePath);
//		ContentBody fileBody = new FileBody(file, "image/jpeg");
//		_entity.addPart(param, fileBody);
//	}

	
/* ************************************************************************************************
 * INFO connection pool setting
 */

    /**
     * connection pool setting <br>
     * HttpClient Params, Scheme, ConnectionManager 설정
     *
     * @return defaultHttpClient
     * @author gue
     * @since 2012.05.30.
     */
    private HttpClient createHttpClient() {
        Log.i(TAG, "Create HttpClient...");
        HttpParams params = new BasicHttpParams();
        HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
        HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);
        HttpProtocolParams.setUseExpectContinue(params, true);
        HttpConnectionParams.setConnectionTimeout(params, HTTP_TIMEOUT);
        HttpConnectionParams.setSoTimeout(params, HTTP_TIMEOUT);
//		ConnManagerParams.setTimeout(params, HTTP_TIMEOUT);

        SchemeRegistry schemeReg = new SchemeRegistry();
        schemeReg.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
        schemeReg.register(new Scheme("https", SSLSocketFactory.getSocketFactory(), 443));
//		schemeReg.register(new Scheme("https", PlainSocketFactory.getSocketFactory(), 443));
//		schemeReg.register(new Scheme("https", SSLSocketFactory.getSocketFactory(), 443));

        ClientConnectionManager conMgr = new ThreadSafeClientConnManager(params, schemeReg);
        return new DefaultHttpClient(conMgr, params);
    }

    /**
     * change http timeout<br/>
     * connection, socket
     *
     * @param timeout timeout < 1 일 경우 {@link #HTTP_TIMEOUT}값으로 초기화
     * @author gue
     * @since 2015. 3. 25.
     */
    public void setConnectTimeout(int timeout) {
        if (_client != null) {
            if (timeout < 1) timeout = HTTP_TIMEOUT;
            HttpParams params = _client.getParams();
            HttpConnectionParams.setConnectionTimeout(params, timeout);
            HttpConnectionParams.setSoTimeout(params, timeout);
        }
    }

	
/* ************************************************************************************************
 * INFO ClientConnectionManager exit
 */

    /**
     * ClientConnectionManager exit
     *
     * @author gue
     * @since 2012. 5. 30.
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


    /* ************************************************************************************************
     * INFO urlDecode
     */
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

    /* ************************************************************************************************
     * INFO NetWork state check
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
    public synchronized int getNetState(Context context) {
        int state = NET_UNCHECKED;
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        if (ni == null) state = NET_NOT_CONN;
        else if (ni.isConnected() && ni.isAvailable()) {
            state = ni.getType();
            if (state == ConnectivityManager.TYPE_MOBILE && state != netState) netState = state;
        }
        if (state < 0) netState = state;
        Log.d(TAG, "NetworkInfo : " + state);
        return state;
    }
}