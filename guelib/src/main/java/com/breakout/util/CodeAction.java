package com.breakout.util;

import android.util.Base64;

import java.lang.reflect.Method;
import java.security.Key;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.DESedeKeySpec;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;


/**
 * SHmac-SHA1 generate & base64 encode,decode & AES256
 *
 * @author sung-gue
 * @version 1.0 (2011.10.15)
 */
public final class CodeAction {
    /**
     * class tag
     */
    private static final String TAG = "CodeAction";
    /**
     * Signature key
     */
//    private static final String HmacSHA1_KEY = "012345";
    /**
     * AES256 key 
     */
//    private static final String QR_AES256_KEY = "01234567890123456789012345678901";
    /**
     * AES256 seed 
     */
//    private static final String AES256_SEED = "sample_01_aes_256_seed";

    /**
     * Hmac-SHA1 generate
     *
     * @param HmacSHA1_KEY ex) HmacSHA1_KEY = "012345";
     */
    public static String CreateHmacSHA1(String value, String HmacSHA1_KEY) throws Exception {
        nullCheck(value);
        nullCheck(HmacSHA1_KEY);

        SecretKeySpec signingKey = new SecretKeySpec(HmacSHA1_KEY.getBytes(), "HmacSHA1");
        Mac mac = Mac.getInstance("HmacSHA1");
        mac.init(signingKey);
        byte[] rawHmac = mac.doFinal(value.getBytes());

        String result = Base64.encodeToString(rawHmac, Base64.NO_WRAP);
        Log.d(TAG, String.format("generate HmacSHA1 : %s -> %s", value, result));

        return result.trim();
    }

    /**
     * md5 encoding
     */
    public static String EncodeMD5(String value) throws Exception {
        nullCheck(value);

        MessageDigest digest = MessageDigest.getInstance("MD5");
        digest.update(value.getBytes());
        byte messageDigest[] = digest.digest();
        StringBuffer hexString = new StringBuffer();
        int length = messageDigest.length;

        for (int i = 0; i < length; i++) {
            hexString.append(Integer.toHexString(0xFF & messageDigest[i]));
        }

        String result = hexString.toString();
        Log.d(TAG, String.format("encode md5 : %s -> %s", value, result));

        return result;
    }

    /**
     * AES256 decrypt_QR <br>
     *
     * @param QR_AES256_KEY ex) QR_AES256_KEY = "01234567890123456789012345678901";
     * @return encrypt value
     */
    public static String DecryptAES_QR(String value, String QR_AES256_KEY) throws Exception {
        nullCheck(value);
        nullCheck(QR_AES256_KEY);

        if (value == null) return null;
        byte[] iv = new byte[]{
                0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00
        };

        String encrypted = toHex(Base64.decode(value, Base64.NO_WRAP));
        SecretKeySpec skeySpec = new SecretKeySpec(QR_AES256_KEY.getBytes(), "AES");

        AlgorithmParameterSpec paramSpec = new IvParameterSpec(iv);
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, skeySpec, paramSpec);
        byte[] buf = cipher.doFinal(toByte(encrypted));

        String result = new String(buf);
        Log.d(TAG, String.format("AESdecrypt_QR : %s -> %s", value, result));

        return result;
    }

    /**
     * KeyGenerate SecureRandom
     */
    static byte[] genKey(String seed) throws Exception {
        KeyGenerator kgen = KeyGenerator.getInstance("AES");
        SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
        sr.setSeed(seed.getBytes());
        kgen.init(256, sr);
        SecretKey skey = kgen.generateKey();
        return skey.getEncoded();
    }

    /**
     * AES256 encrypt
     *
     * @param AES256_SEED ex) AES256_SEED = "sample_01_aes_256_seed";
     * @return encrypt value
     */
    public static String EncryptAES(String value, String AES256_SEED) throws Exception {
        nullCheck(value);
        nullCheck(AES256_SEED);

        /*byte[] iv = new byte[] {
                0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08, 0x09,0x0a, 0x0b, 0x0c, 0x0d, 0x0e, 0x0f
        };*/
        byte[] iv = new byte[]{
                0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00
        };
        AlgorithmParameterSpec paramSpec = new IvParameterSpec(iv);
        SecretKeySpec sKeySpec = new SecretKeySpec(AES256_SEED.getBytes(), "AES");

//        Cipher cipher = Cipher.getInstance("AES");
//        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
//        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS7Padding");
//        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS7Padding");
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, sKeySpec, paramSpec);

        byte[] encrypted = cipher.doFinal(value.getBytes());
        String result = Base64.encodeToString(encrypted, Base64.NO_WRAP);

        Log.d(TAG, String.format("AES encrypt : %s -> %s", value, result));
        return result;
    }
/*    public static String EncryptAES(String value, String AES256_SEED) throws Exception {
        nullCheck(value);
        nullCheck(AES256_SEED);
        
        SecretKeySpec sKeySpec = new SecretKeySpec(genKey(AES256_SEED), "AES");
        
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, sKeySpec);
        byte[] encrypted = cipher.doFinal(value.getBytes());
        
        String result = Base64.encodeToString(encrypted, Base64.NO_WRAP); 
        Log.d(TAG, String.format("AES encrypt : %s -> %s", value, result));
        
        return result;
    }
*/

    /**
     * AES256 decrypt
     *
     * @param AES256_SEED ex) AES256_SEED = "sample_01_aes_256_seed";
     * @return decrypt value
     */
    public static String DecryptAES(String value, String AES256_SEED) throws Exception {
        nullCheck(value);
        nullCheck(AES256_SEED);

        String encrypted = value;
//        String encrypted = Base64.decode(value, Base64.NO_WRAP));
//        String encrypted = toHex(Base64.decode(value, Base64.NO_WRAP) );

        /*byte[] iv = new byte[] {
                0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08, 0x09,0x0a, 0x0b, 0x0c, 0x0d, 0x0e, 0x0f
        };*/
        byte[] iv = new byte[]{
                0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00
        };
        AlgorithmParameterSpec paramSpec = new IvParameterSpec(iv);
        SecretKeySpec sKeySpec = new SecretKeySpec(AES256_SEED.getBytes(), "AES");

//        Cipher cipher = Cipher.getInstance("AES");
//        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
//        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS7Padding");
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
//        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS7Padding");

        cipher.init(Cipher.DECRYPT_MODE, sKeySpec, paramSpec);
        byte[] decrypted = cipher.doFinal(Base64.decode(encrypted, Base64.NO_WRAP));
        String result = new String(decrypted);

        Log.d(TAG, String.format("AES decrypt : %s -> %s", value, result));
        return result;
    }
/*    public static String DecryptAES(String value, String AES256_SEED) throws Exception {
        nullCheck(value);
        nullCheck(AES256_SEED);
        
        String encrypted = toHex(Base64.decode(value, Base64.NO_WRAP) );
        SecretKeySpec sKeySpec = new SecretKeySpec(genKey(AES256_SEED), "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, sKeySpec);
        byte[] decrypted = cipher.doFinal(toByte(encrypted));
        
        String result = new String(decrypted); 
        Log.d(TAG, String.format("AESdecrypt : %s -> %s", value, result));
        
        return result;
    }
*/

    /**
     * hex string -->byte[]
     */
    public static byte[] toByte(String value) throws Exception {
        byte[] buf = new byte[value.length() / 2];
        for (int i = 0; i < buf.length; i++) {
            buf[i] = (byte) Integer.parseInt(value.substring(2 * i, 2 * i + 2), 16);
        }
        return buf;
    }

    /**
     * byte[] --> hex string
     */
    public static String toHex(byte value[]) throws Exception {
        StringBuffer sb = new StringBuffer(value.length * 2);
        for (int i = 0; i < value.length; i++) {
            if ((value[i] & 0xff) < 0x10)
                sb.append("0");
            sb.append(Long.toString(value[i] & 0xff, 16));
        }
        return sb.toString();
    }

    /**
     * value의 값이 null이거나 길이가 0이라면 Exception을 생성한다.
     */
    private static boolean nullCheck(String value) throws Exception {
        if (value != null && value.length() != 0) {
            return true;
        } else throw new Exception("value is null or value.length() 0");
    }

    /**
     * 문자열 대칭 암호화
     *
     * @param str 비밀키 암호화를 희망하는 문자열
     * @return String 암호화된 ID
     */
    public static String EncryptDES(String str, String keyValue) throws Exception {
        String result = "";
        if (str != null && str.length() != 0) {
            String instance = (keyValue.length() == 24) ? "DESede/ECB/PKCS5Padding" : "DES/ECB/PKCS5Padding";
            Cipher cipher = Cipher.getInstance(instance);
            cipher.init(Cipher.ENCRYPT_MODE, getKey(keyValue));

            byte[] inputBytes1 = str.getBytes("UTF8");
            byte[] outputBytes1 = cipher.doFinal(inputBytes1);

            byte[] outputStr1 = EncodeBase64(outputBytes1);
            result = new String(outputStr1, "UTF-8");
        }
        return result;
    }

    /**
     * 문자열 대칭 복호화
     *
     * @param str 비밀키 복호화를 희망하는 문자열
     */
    public static String DecryptDES(String str, String keyValue) throws Exception {
        String result = "";
        if (str != null && str.length() != 0) {
            String instance = (keyValue.length() == 24) ? "DESede/ECB/PKCS5Padding" : "DES/ECB/PKCS5Padding";
            Cipher cipher = Cipher.getInstance(instance);
            cipher.init(Cipher.DECRYPT_MODE, getKey(keyValue));

            byte[] inputBytes1 = DecodeBase64(str.getBytes());
            byte[] outputBytes2 = cipher.doFinal(inputBytes1);

            result = new String(outputBytes2, "UTF-8");
        }
        return result;
    }

    /**
     * 키값 24바이트인 경우 TripleDES 아니면 DES
     */
    private static Key getKey(String keyValue) throws Exception {
        return (keyValue.length() == 24) ? getKey2(keyValue) : getKey1(keyValue);
    }

    /**
     * 지정된 비밀키를 가지고 오는 메서드 (DES) require Key Size : 16 bytes
     */
    private static Key getKey1(String keyValue) throws Exception {
        DESKeySpec desKeySpec = new DESKeySpec(keyValue.getBytes());
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
        Key key = keyFactory.generateSecret(desKeySpec);
        return key;
    }

    /**
     * 지정된 비밀키를 가지고 오는 메서드 (TripleDES) require Key Size : 24 bytes
     */
    private static Key getKey2(String keyValue) throws Exception {
        DESedeKeySpec desKeySpec = new DESedeKeySpec(keyValue.getBytes());
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DESede");
        return keyFactory.generateSecret(desKeySpec);
    }

    /**
     *
     */
    private static byte[] EncodeBase64(byte[] binaryData) {
        byte[] buf = null;
        try {
            Class<?> Base64 = Class.forName("org.apache.commons.codec.binary.Base64");
            Class<?>[] parameterTypes = new Class[]{byte[].class};
            Method encodeBase64 = Base64.getMethod("encodeBase64", parameterTypes);
            buf = (byte[]) encodeBase64.invoke(Base64, binaryData);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return buf;
    }

    /**
     *
     */
    private static byte[] DecodeBase64(byte[] base64Data) {
        byte[] buf = null;
        try {
            Class<?> Base64 = Class.forName("org.apache.commons.codec.binary.Base64");
            Class<?>[] parameterTypes = new Class[]{byte[].class};
            Method decodeBase64 = Base64.getMethod("decodeBase64", parameterTypes);
            buf = (byte[]) decodeBase64.invoke(Base64, base64Data);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return buf;
    }

    /**
     * hex --> string
     */
    public static String HexToStr(String hex) throws Exception {
        byte[] buf = new byte[hex.length() / 2];
        for (int i = 0; i < buf.length; i++) {
            buf[i] = (byte) Integer.parseInt(hex.substring(2 * i, 2 * i + 2), 16);
        }
        return new String(buf);
    }

    /**
     * string --> hex string
     */
    public static String StrToHex(String str) throws Exception {
        byte buf[] = str.getBytes();
        StringBuffer sb = new StringBuffer(buf.length * 2);
        for (int i = 0; i < buf.length; i++) {
            if (((int) buf[i] & 0xff) < 0x10)
                sb.append("0");
            sb.append(Long.toString((int) buf[i] & 0xff, 16));
        }
        return sb.toString();
    }

}