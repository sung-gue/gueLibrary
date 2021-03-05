package com.breakout.util.device;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.provider.Settings.Secure;
import android.telephony.TelephonyManager;
import android.util.Base64;
import android.util.DisplayMetrics;

import com.breakout.util.CodeAction;
import com.breakout.util.Log;
import com.breakout.util.Util;
import com.breakout.util.string.StringUtil;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.security.MessageDigest;
import java.util.Collections;

/**
 * {@link Util}의 method를 속성에 따라 class로 분리<br/>
 * Device Util
 *
 * @author sung-gue
 * @version 1.0 (2013. 10. 1.)
 */
public final class DeviceUtil {
    private static String TAG = "DeviceUtil";
//    private  static Locale DEFAULT_LOCALE = Locale.getDefault();


    private DeviceUtil() {
    }

    /* ------------------------------------------------------------
        DESC: device
     */
    public static String getPhoneNum(Context context) {
        String resultStr = null;
        try {
            TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            resultStr = tm.getLine1Number();
            if (StringUtil.nullCheckB(resultStr) && resultStr.startsWith("+82")) {
                resultStr = resultStr.replace("+82", "0");
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        }
        return resultStr;
    }

    /**
     * device가 전화기능이 없을 경우나 오류로 인하여 device id를 얻지 못하므로 device info를 조합하여 unique id 생성
     *
     * @param context
     * @return device unique id
     */
    public static String getDeviceUniqueId(Context context) {
        String result = null;
        String log = "get device uniqueId ";
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String deviceId = tm.getDeviceId();

        // 1. deivceId가 있고 0으로 이루어진 값이 아닐 때
        if (StringUtil.nullCheckB(deviceId) && !deviceId.matches("^[0]+")) {
            result = deviceId;
            log += "(device id) : %s";
        }
        // 2. deviceId가 존재하지 않을 때
        else {
            result = getDeviceUniqueIdOfCombine(context);
            log += "(combined id MD5) : %s";
        }
        Log.d(TAG, String.format(log, result));

        return result;
    }

    public static String getDeviceUniqueIdOfCombine(Context context) {
        String result = null;
        String log = "-------------------------------------------";
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

        // android id - os version 2.3 부터 정상 작동
        String androidId = Secure.getString(context.getContentResolver(), Secure.ANDROID_ID);
        log += "\n- 1. Android id : " + androidId;

        // sim card serial number - usim serial은 deviceId가 존재하지 않을경우에도 존재할 가능성 있음
        String simNumber = tm.getSimSerialNumber();
        log += "\n- 2. SimSerialNumber id : " + androidId;

        // deviceInfo
        String deviceInfo = "" + android.os.Build.BOARD.length() % 10 +
                android.os.Build.BRAND.length() % 10 +
                android.os.Build.CPU_ABI.length() % 10 +
                android.os.Build.DEVICE.length() % 10 +
                android.os.Build.DISPLAY.length() % 10 +
                android.os.Build.HOST.length() % 10 +
                android.os.Build.ID.length() % 10 +
                android.os.Build.MANUFACTURER.length() % 10 +
                android.os.Build.MODEL.length() % 10 +
                android.os.Build.PRODUCT.length() % 10 +
                android.os.Build.TAGS.length() % 10 +
                android.os.Build.TYPE.length() % 10 +
                android.os.Build.USER.length() % 10;
        log += "\n- 3. device info : " + deviceInfo;

        // wifi mac address - wifi가 작동중이지 않을때에는 값을 받지 못할수도 있음
        WifiManager wm = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        String macAddr = wm.getConnectionInfo().getMacAddress();
        log += "\n- 4. mac address : " + macAddr;

        // combine device info, create unique id
        String combinedInfo = androidId + simNumber + deviceInfo + macAddr;
        log += "\n- combine id (1+2+3+4) : " + combinedInfo;

        // device info encode md5
        String combinedInfoMD5 = null;
        try {
            combinedInfoMD5 = CodeAction.EncodeMD5(combinedInfo);
            result = combinedInfoMD5;
            log += "\n- combine id md5 : " + combinedInfoMD5;
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        }

        if (!StringUtil.nullCheckB(result)) {
            result = deviceInfo;
        }
        log += "\n get device combine id : " + result;
        log += "\n-------------------------------------------";
        Log.d(TAG, log);

        return result;
    }

    /**
     * get app version.
     *
     * @return version name, exception - "1.0"
     */
    public static String getAppVersionName(Context context) {
        String ver = null;
        try {
            PackageInfo pi = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            ver = pi.versionName;
        } catch (Exception e) {
            Log.e(TAG, "Exception : " + e.getMessage(), e);
            ver = "1.0";
        }
        return ver;
    }

    /**
     * get app version code.
     *
     * @return version code, exception : 0
     */
    public static int getAppVersionCode(Context context) {
        int code = 0;
        try {
            PackageInfo pi = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            code = pi.versionCode;
        } catch (Exception e) {
            Log.e(TAG, "Exception : " + e.getMessage(), e);
        }
        return code;
    }

    /**
     * get device info log(debug) and return result string
     */
    @SuppressLint("NewApi")
    @SuppressWarnings("deprecation")
    public static String getDeviceInfo(Context context) {
        String androidId = Secure.getString(context.getContentResolver(), Secure.ANDROID_ID);
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String deviceId = null;
        String simNumber = null;
        try {
            deviceId = tm.getDeviceId();
            simNumber = tm.getSimSerialNumber();
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        }
        WifiManager wm = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        String macAddr = null;
        try {
            macAddr = wm.getConnectionInfo().getMacAddress();
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        }

        DisplayMetrics m = context.getResources().getDisplayMetrics();
        int displayWidth = m.widthPixels;
        int displayHeight = m.heightPixels;
        int displayWidthDP = (int) (displayWidth * 1.0 / m.density);
        int displayHeightDP = (int) (displayHeight * 1.0 / m.density);

        String result = "------------------- Device info -------------------" +
                "\n|  androidId : " + androidId +
                "\n|  deviceId : " + deviceId +
                "\n|  SimSerialNumber : " + simNumber +
                "\n|  ip : " + getLocalIpAddress(true, true) +
                "\n|  mac address : " + macAddr +
                "\n|  Build.BOARD : " + android.os.Build.BOARD +
                "\n|  Build.BOOTLOADER : " + android.os.Build.BOOTLOADER +
                "\n|  Build.BRAND : " + android.os.Build.BRAND +
                "\n|  Build.CPU_ABI : " + android.os.Build.CPU_ABI +
                "\n|  Build.CPU_ABI2 : " + android.os.Build.CPU_ABI2 +
                "\n|  Build.DEVICE : " + android.os.Build.DEVICE +
                "\n|  Build.DISPLAY : " + android.os.Build.DISPLAY +
                "\n|  Build.FINGERPRINT : " + android.os.Build.FINGERPRINT +
                "\n|  Build.HARDWARE : " + android.os.Build.HARDWARE +
                "\n|  Build.HOST : " + android.os.Build.HOST +
                "\n|  Build.ID : " + android.os.Build.ID +
                "\n|  Build.MANUFACTURER : " + android.os.Build.MANUFACTURER +
                "\n|  Build.MODEL : " + android.os.Build.MODEL +
                "\n|  Build.PRODUCT : " + android.os.Build.PRODUCT +
                "\n|  Build.USER : " + android.os.Build.USER +
                "\n|  Build.RADIO : " + (android.os.Build.VERSION.SDK_INT >= 14 ? android.os.Build.getRadioVersion() : android.os.Build.RADIO) +
                "\n|  Build.SERIAL : " + (android.os.Build.VERSION.SDK_INT >= 9 ? android.os.Build.SERIAL : null) +
                "\n|  Build.TAGS : " + android.os.Build.TAGS +
                "\n|  Build.TIME : " + android.os.Build.TIME +
                "\n|  Build.TYPE : " + android.os.Build.TYPE +
                "\n|  Build.UNKNOWN : " + android.os.Build.UNKNOWN +
                "\n|  Build.VERSION.CODENAME : " + android.os.Build.VERSION.CODENAME +
                "\n|  Build.VERSION.INCREMENTAL : " + android.os.Build.VERSION.INCREMENTAL +
                "\n|  Build.VERSION.RELEASE : " + android.os.Build.VERSION.RELEASE +
                "\n|  Build.VERSION.SDK_INT : " + android.os.Build.VERSION.SDK_INT +
                "\n|  Display density : " + m.density + " / densityDpi : " + m.densityDpi +
                "\n|  Display size - px : " + displayWidth + " x " + displayHeight + " / dp : " + displayWidthDP + " x " + displayHeightDP +
                "\n|  Local IP address - IPv4 : " + getLocalIpAddress(true, true) + ", " + getLocalIpAddress(true, false) + " / IPv6 : " + getLocalIpAddress(false, false) +
                "\n|------------------------------------------------";

        Log.d(TAG, result);
        return result;
    }


    /**
     * get ip address
     *
     * @param useIPv4      true ipv4 형식반환, false ipv6 형식반환
     * @param getPrivateIP sdk 16이상일 경우 wifi 연결시에 ip가 3g상태와 wifi연결 ip 둘다 나오기 때문에 true일경우 wifi상태의 ip를 return한다
     * @return ip
     */
    public static String getLocalIpAddress(boolean useIPv4, boolean getPrivateIP) {
        String WIFI_DEVICE_PREFIX = "eth";
        String LocalIP = null;
        try {
            // 1. use Socket
            /*java.net.Socket socket = new java.net.Socket("www.google.com", 80);
            LocalIP = socket.getLocalAddress().toString();
            android.util.Log.i("socket addr", "local ip of socket : " + LocalIP);
            */

            // 2. use InetAddress
            /*for (Enumeration<NetworkInterface> enumNetwork = NetworkInterface.getNetworkInterfaces(); enumNetwork.hasMoreElements();) {
                NetworkInterface networkInterface = enumNetwork.nextElement();
                for (Enumeration<InetAddress> enumAddr = networkInterface.getInetAddresses(); enumAddr.hasMoreElements();) {
                    InetAddress inetAddress = enumAddr.nextElement();
                }
            }*/
            for (NetworkInterface networkInterface : Collections.list(NetworkInterface.getNetworkInterfaces())) {
                for (InetAddress inetAddress : Collections.list(networkInterface.getInetAddresses())) {
                    if (!inetAddress.isLoopbackAddress()) {
                        String ip = inetAddress.getHostAddress();//.toUpperCase();
                        //boolean isIPv4 = InetAddressUtils.isIPv4Address(ip);
                        boolean isIPv4 = ip.indexOf(':') < 0;
                        int suffix = ip.indexOf('%'); // remove ipv6 port suffix
                        if (LocalIP == null || getPrivateIP) {
                            if (useIPv4 && isIPv4) {
                                LocalIP = ip;
                            } else if (!useIPv4 && !isIPv4) {
                                LocalIP = suffix < 0 ? ip : ip.substring(0, suffix);
                            }
                        } else if (networkInterface.getName().startsWith(WIFI_DEVICE_PREFIX)) {
                            if (useIPv4 && isIPv4) {
                                LocalIP = ip;
                            } else if (!useIPv4 && !isIPv4) {
                                LocalIP = suffix < 0 ? ip : ip.substring(0, suffix);
                            }
                        }
                    } else {
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return LocalIP;
    }


    public static void printLogAppKeyHash(Context context) {
        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(context.getPackageName(), PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.i(TAG, "app key hash : " + Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        }
    }


    /* ------------------------------------------------------------
        DESC: media
     */

    /**
     * sdcard전체를 미디어 스캔
     */
    public static void runMediaScan(Context context) {
        try {
//            context.sendBroadcast(new Intent(    Intent.ACTION_MEDIA_MOUNTED,
//                                                Uri.parse("file://" + Environment.getExternalStorageDirectory())
//                                                ));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 파일을 기준으로 하는 미디어 스캔
     *
     * @param filePath 스캔할 폴더의 이름, null일경우 sdcard의 전체 스캔을 한다. {@link #runMediaScan(Context)}
     */
    public static void updateMediaScan(Context context, String filePath) {
        if (StringUtil.nullCheckB(filePath)) {
            try {
                MediaScannerConnection.scanFile(context, new String[]{filePath}, null, null);
            } catch (Exception e) {
                context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
                        Uri.parse("file://" + filePath)
                ));
            }
        } else {
            runMediaScan(context);
        }
    }


    /* ------------------------------------------------------------
        DESC: window size
     */

    /**
     * display size
     *
     * @return <li>int[0] : width</li>
     * <li>int[1] : height</li>
     */
    public static int[] getDisplaySize(Context context) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return new int[]{metrics.widthPixels, metrics.heightPixels};
        /*WindowManager manager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = manager.getDefaultDisplay();
        return new int[]{display.getWidth(), display.getHeight()};*/
    }

    /**
     * @return display width
     */
    public static int getDisplayWidth(Context context) {
        return getDisplaySize(context)[0];
    }

    /**
     * @return display height
     */
    public static int getDisplayHeight(Context context) {
        return getDisplaySize(context)[1];
    }


    /* ------------------------------------------------------------
        DESC: google play service
     */

    /**
     * Check the device to make sure it has the Google Play Services APK. If
     * it doesn't, display a dialog that allows users to download the APK from
     * the Google Play Store or enable it in the device's system settings.
     */
    public static boolean checkPlayServices(final Activity context) {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(context);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
                Dialog dialog = apiAvailability.getErrorDialog(context, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST, new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        context.finish();
                    }
                });
                dialog.show();
            } else {
                Log.e(context.getPackageName(), "This device is not supported.");
                context.finish();
            }
            return false;
        }
        return true;
    }
}