package com.breakout.network;

import com.breakout.util.CodeAction;
import com.breakout.util.Log;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;


/**
 * url encode, decode
 *
 * @author sung-gue
 * @version 1.0 (2020. 8. 17.)
 */
public final class EncodeUtil {
    public static String TAG = "EncodeUtil";

    /**
     * URLEncode된 string을 URLDecode 한다.
     */
    public static String urlDecode(String str) {
        String decodeStr = null;
        try {
            if (str != null) decodeStr = URLDecoder.decode(str, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            Log.e(TAG, e.getMessage(), e);
        }
        return decodeStr;
    }

    public static String urlDecodeAndDecryptAES(String value, String key) {
        String returnStr = urlDecode(value);
        try {
            returnStr = decryptAES(value, key);
        } catch (Exception e) {
            Log.e(e.getMessage());
        }
        return returnStr;
    }

    public static String encryptAES(String value, String key) {
        String returnStr = value;
        try {
            returnStr = CodeAction.EncryptAES(value, key);
        } catch (Exception e) {
            Log.e(e.getMessage());
        }
        return returnStr;
    }

    public static String decryptAES(String value, String key) {
        String returnStr = value;
        try {
            returnStr = CodeAction.DecryptAES(value, key);
        } catch (Exception e) {
            Log.e(e.getMessage());
        }
        return returnStr;
    }

}