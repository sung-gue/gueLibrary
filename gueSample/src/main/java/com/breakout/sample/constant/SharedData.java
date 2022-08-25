package com.breakout.sample.constant;

import android.content.Context;
import android.text.TextUtils;

import com.breakout.sample.Log;
import com.breakout.util.storage.SharedStorage;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import cz.msebera.android.httpclient.protocol.HTTP;


/**
 * App SharedPreferences<br/>
 *
 * @author sung-gue
 * @version 1.0 (2012. 9. 30.)
 */
public final class SharedData extends SharedStorage {

    private static SharedData _instance;

    public static synchronized SharedData getInstance(Context context) {
        if (_instance == null) _instance = new SharedData(context);
        return _instance;
    }

    public static synchronized SharedData getInstance() throws Exception {
        if (_instance == null) throw new Exception("SharedData instance is null");
        return _instance;
    }

    public static void destroyInstance() {
        if (_instance != null) {
            _instance.destroy();
            _instance = null;
        }
    }

    private SharedData(Context appContext) {
        super(appContext);
    }

    public void clearUserInfo() {
        clear(ClearMode.USER_CLEAR);
    }

    /* ------------------------------------------------------------
        default
     */
    private final String agreeAppArarmYN = "agreeAppArarmYN";

    public void setAgreeAppArarmYN(String agreeYN) {
        _editor.putString(agreeAppArarmYN, agreeYN);
        _editor.commit();
    }

    public String getAgreeAppArarmYN() {
        return _shared.getString(agreeAppArarmYN, "N");
    }


    /* ------------------------------------------------------------
        sqlite
     */
    private final String databaseVersion = "database_version";

    public void setDatabaseVersion(int databaseVersion) {
        _editorConst.putInt(this.databaseVersion, databaseVersion);
        _editorConst.commit();
    }

    public int getDatabaseVersion() {
        return _sharedConst.getInt(databaseVersion, -1);
    }


    /* ------------------------------------------------------------
        google
     */
    private final String googleAccountIdToken = "googleAccountIdToken";

    public void setGoogleAccountIdToken(String idToken) {
        _editorUser.putString(googleAccountIdToken, idToken);
        _editorUser.commit();
    }

    public String getGoogleAccountIdToken() {
        return _sharedUser.getString(googleAccountIdToken, null);
    }

    private final String googleAccountServerAuthCode = "googleAccountServerAuthCode";

    public void setGoogleAccountServerAuthCode(String serverAuthCode) {
        _editorUser.putString(googleAccountServerAuthCode, serverAuthCode);
        _editorUser.commit();
    }

    public String getGoogleAccountServerAuthCode() {
        return _sharedUser.getString(googleAccountServerAuthCode, null);
    }

    private final String googleAccountDisplayName = "googleAccountDisplayName";

    public void setGoogleAccountDisplayName(String displayName) {
        _editorUser.putString(googleAccountDisplayName, displayName);
        _editorUser.commit();
    }

    public String getGoogleAccountDisplayName() {
        return _sharedUser.getString(googleAccountDisplayName, null);
    }

    private final String googleAccountEmail = "googleAccountEmail";

    public void setGoogleAccountEmail(String email) {
        _editorUser.putString(googleAccountEmail, email);
        _editorUser.commit();
    }

    public String getGoogleAccountEmail() {
        return _sharedUser.getString(googleAccountEmail, null);
    }

    private final String googleAccountId = "googleAccountId";

    public void setGoogleAccountId(String id) {
        _editorUser.putString(googleAccountId, id);
        _editorUser.commit();
    }

    public String getGoogleAccountId() {
        return _sharedUser.getString(googleAccountId, null);
    }

    private final String googleAccountPhotoUrl = "googleAccountPhotoUrl";

    public void setGoogleAccountPhotoUrl(String photoUrl) {
        _editorUser.putString(googleAccountPhotoUrl, photoUrl);
        _editorUser.commit();
    }

    public String getGoogleAccountPhotoUrl() {
        return _sharedUser.getString(googleAccountPhotoUrl, null);
    }


    /* ------------------------------------------------------------
        user
     */
    private final String userSession = "userSession";

    public void setUserSession(String session) {
        _editorUser.putString(userSession, session);
        _editorUser.commit();
    }

    public String getUserSession() {
        return _sharedUser.getString(userSession, null);
    }

    /**
     * @return login true
     */
    public boolean isLoginUser() {
        return !TextUtils.isEmpty(getUserSession());
    }

    /**
     * SSO(Single Sign On) id
     */
    public String getUserSsoSerial() {
        return getGoogleAccountId();
    }

    private final String userId = "userId";

    public void setUserId(int userId) {
        _editor.putInt(this.userId, userId);
        _editor.commit();
    }

    public int getUserId() {
        return _shared.getInt(userId, 0);
    }


    /* ------------------------------------------------------------
        기간 설정
     */
    private final String dailyGuideDisplay = "dailyGuideDisplay";
    private final int maxCountOfDailyGuideDisplay = 5;
    private final String keyDate = "date";
    private final String keyCount = "count";

    /**
     * 하루에 5번 노출
     *
     * @return 현재 노출이 가능하다면 true
     */
    public boolean isShowDailyGuide() {
        String key = dailyGuideDisplay;
        String currentDate = new SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(new Date());

        String jsonStr = _sharedUser.getString(key, null);
        HashMap<String, Object> map;
        String saveDate = null;
        double saveCount = 0;
        if (!TextUtils.isEmpty(jsonStr)) {
            map = new Gson().fromJson(jsonStr, new TypeToken<HashMap<String, Object>>() {
            }.getType());
            saveDate = (String) map.get(keyDate);
            saveCount = (double) map.get(keyCount);
        } else {
            map = new HashMap<>();
        }

        boolean isShow = false;
        if (!currentDate.equals(saveDate) || saveCount < maxCountOfDailyGuideDisplay) {
            isShow = true;
        }
        return isShow;
    }

    public boolean addDailyGuideCount() {
        String key = dailyGuideDisplay;
        String currentDate = new SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(new Date());

        String jsonStr = _sharedUser.getString(key, null);
        HashMap<String, Object> map;
        String saveDate = null;
        double saveCount = 0;
        if (!TextUtils.isEmpty(jsonStr)) {
            map = new Gson().fromJson(jsonStr, new TypeToken<HashMap<String, Object>>() {
            }.getType());
            saveDate = (String) map.get(keyDate);
            saveCount = (double) map.get(keyCount);
        } else {
            map = new HashMap<>();
        }

        boolean isMaxShareCount = false;
        if (!currentDate.equals(saveDate) || saveCount < maxCountOfDailyGuideDisplay) {
            double count = currentDate.equals(saveDate) ? saveCount + 1 : 1;
            map.put(keyDate, currentDate);
            map.put(keyCount, count);
            String newJsonStr = new Gson().toJson(map);
            _editorUser.putString(key, newJsonStr);
            _editorUser.commit();
            if (count >= maxCountOfDailyGuideDisplay) {
                isMaxShareCount = true;
            }
        }
        return isMaxShareCount;
    }


    /* ------------------------------------------------------------
        FLASH 영역, 앱 시동 후 종료되면 사라져야할 data set
     */
    private final String androidAdid = "androidAdid";

    public void setAndroidAdid(String androidAdid) {
        _editorFlash.putString(this.androidAdid, androidAdid);
        _editorFlash.commit();
    }

    public String getAndroidAdid() {
        return _sharedFlash.getString(androidAdid, null);
    }


    /* ------------------------------------------------------------
        CONST 영역 앱 설치후 삭제하지 않는 data
     */
    private String isCheckInstallReferrer = "isCheckInstallReferrer";

    public void setIsCheckInstallReferrer() {
        _editorConst.putBoolean(isCheckInstallReferrer, true);
        _editorConst.commit();
    }

    public boolean getIsCheckInstallReferrer() {
        return _sharedConst.getBoolean(isCheckInstallReferrer, false);
    }

    private String facebookDeferredAppLinkData = "facebookDeferredAppLinkData";

    public void setFacebookDeferredAppLinkData(String facebookDeferredAppLinkData) {
        _editorConst.putString(this.facebookDeferredAppLinkData, facebookDeferredAppLinkData);
        _editorConst.commit();
    }

    public String getFacebookDeferredAppLinkData() {
        return _sharedConst.getString(facebookDeferredAppLinkData, null);
    }

    private String googleInstallReferrer = "googleInstallReferrer";

    public void setGoogleInstallReferrer(String googleInstallReferrer) {
        _editorConst.putString(this.googleInstallReferrer, googleInstallReferrer);
        _editorConst.commit();
    }

    public String getGoogleInstallReferrer() {
        return _sharedConst.getString(googleInstallReferrer, null);
    }

    private final String termsOfUseUrl = "termsOfUseUrl";

    public void setTermsOfUseUrl(String termsOfUseUrl) {
        if (!TextUtils.isEmpty(termsOfUseUrl)) {
            _editorConst.putString(this.termsOfUseUrl, termsOfUseUrl);
            _editorConst.commit();
        }
    }

    public String getTermsOfUseUrl() {
        return _sharedConst.getString(termsOfUseUrl, Const.TERMS_OF_USE_URL);
    }

    private final String privacyUrl = "privacyUrl";

    public void setPrivacyUrl(String privacyUrl) {
        if (!TextUtils.isEmpty(privacyUrl)) {
            _editorConst.putString(this.privacyUrl, privacyUrl);
            _editorConst.commit();
        }
    }

    public String getPrivacyUrl() {
        return _sharedConst.getString(privacyUrl, Const.PRIVACY_URL);
    }

    private final String lastLoginDialogViewTime = "userLoginNoMoreCheckTime";

    /**
     * 로그인창 다시보지 않기
     *
     * @param isInit true 이면 값 초기화
     */
    public void setLastLoginDialogViewTime(boolean isInit) {
        if (isInit) {
            _editorConst.putString(lastLoginDialogViewTime, null);
        } else {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
            _editorConst.putString(lastLoginDialogViewTime, formatter.format(new Date()));
        }
        _editorConst.commit();
    }

    /**
     * @return 오늘 다시보지 않기를 했다면 true
     */
    public boolean isLoginDialogShownToday() {
        String date = _sharedConst.getString(lastLoginDialogViewTime, "20191111");
        String now = new SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(new Date());
        return now.equals(date);
    }

    private final String lastTestViewTime = "lastTestViewTime";

    /**
     * 다시보지 않기
     *
     * @param isInit true 이면 값 초기화
     */
    public void setLastTestViewTime(boolean isInit) {
        if (isInit) {
            _editorConst.putString(lastTestViewTime, null);
        } else {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
            _editorConst.putString(lastTestViewTime, formatter.format(new Date()));
        }
        _editorConst.commit();
    }

    /**
     * @return 오늘 다시보지 않기를 했다면 true
     */
    public boolean isTestviewShownToday() {
        String date = _sharedConst.getString(lastTestViewTime, "20191111");
        String now = new SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(new Date());
        return now.equals(date);
    }

    private final String shownScreenGuideViewTime = "shownScreenGuideViewTime";

    /**
     * 2시간에 한번씩 노출
     *
     * @param screenName 화면이름
     * @return 현재 시간에 노출이 가능하다면 true
     */
    public boolean isShownScreenGuide(String screenName) {
        String key = shownScreenGuideViewTime + getGoogleAccountId();
        String jsonStr = _sharedConst.getString(key, null);
        HashMap<String, Date> map;
        Date saveDate = null;
        if (!TextUtils.isEmpty(jsonStr)) {
            try {
                map = new Gson().fromJson(jsonStr, new TypeToken<HashMap<String, Date>>() {
                }.getType());
                saveDate = map.get(screenName);
            } catch (Exception ignored) {
                map = new HashMap<>();
            }
        } else {
            map = new HashMap<>();
        }
        Date currentDate = new Date();
        boolean isShow = false;
        if (saveDate == null || (currentDate.getTime() > saveDate.getTime() + 1000 * 60 * 60 * 2)) {
            isShow = true;
            map.put(screenName, currentDate);
            String newJsonStr = new Gson().toJson(map);
            _editorConst.putString(key, newJsonStr);
            _editorConst.commit();
        }
        return isShow;
    }


    /* ------------------------------------------------------------
        최근 검색어
     */
    private final String recentSearchStr = "recentSearchStr";
    private final int MAX_SEARCH_CNT = 30;

    /**
     * 최근 검색어 등록
     */
    public void setRecentSearchStr(String str) {
        ArrayList<String> strs = getRecentSearchStrOrder();
        boolean isExist = false;
        for (String temp : strs) {
            if (temp.equals(str)) {
                isExist = true;
                break;
            }
        }
        if (!isExist && !TextUtils.isEmpty(str)) {
            if (strs.size() >= MAX_SEARCH_CNT) {
                while (strs.size() > MAX_SEARCH_CNT - 1) {
                    strs.remove(0);
                }
            }
            strs.add(str);
            String jsonStr = new Gson().toJson(strs);
            _editor.putString(recentSearchStr, jsonStr);
            _editor.commit();
        }
    }

    /**
     * 검색어 삭제, null 일경우 모두 삭제
     */
    public void deleteRecentSearchStr(String str) {
        if (str == null) {
            _editor.putString(recentSearchStr, null);
        } else {
            ArrayList<String> strs = getRecentSearchStr();
            for (int i = 0; i < strs.size(); i++) {
                String temp = strs.get(i);
                if (temp.equals(str)) {
                    strs.remove(i);
                    break;
                }
            }
            String jsonStr = new Gson().toJson(strs);
            _editor.putString(recentSearchStr, jsonStr);
            _editor.commit();
        }
        _editor.commit();
    }

    public ArrayList<String> getRecentSearchStrOrder() {
        String jsonStr = _shared.getString(recentSearchStr, null);
        ArrayList<String> tagList = new ArrayList<>();
        ArrayList<String> returnList = new ArrayList<>();
        try {
            if (!TextUtils.isEmpty(jsonStr)) {
                tagList = new Gson().fromJson(jsonStr, new TypeToken<ArrayList<String>>() {
                }.getType());
                for (int i = 0; i < tagList.size(); i++) {
                    returnList.add(tagList.get(i));
                }
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        }
        return returnList;
    }

    public ArrayList<String> getRecentSearchStr() {
        String jsonStr = _shared.getString(recentSearchStr, null);
        ArrayList<String> tagList = new ArrayList<>();
        ArrayList<String> returnList = new ArrayList<>();
        try {
            if (!TextUtils.isEmpty(jsonStr)) {
                tagList = new Gson().fromJson(jsonStr, new TypeToken<ArrayList<String>>() {
                }.getType());
                for (int i = tagList.size() - 1; -1 < i; i--) {
                    returnList.add(tagList.get(i));
                }
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        }
        return returnList;
    }


    /* ------------------------------------------------------------
        save option
     */
    static class Option {
        String op1;
        String op2;
        String op3;
        String op_all;
    }

    private final String appOption = "appOption";

    public void putAppOption(Option notiOption) {
        String jsonStr = new Gson().toJson(notiOption);
        _editor.putString(appOption, jsonStr);
        _editor.commit();
    }

    public Option getAppOption() {
        String jsonStr = _shared.getString(appOption, null);
        Option notiOption = null;
        try {
            if (!TextUtils.isEmpty(jsonStr)) {
                notiOption = new Gson().fromJson(jsonStr, Option.class);
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        }
        if (notiOption == null) {
            notiOption = new Option();
            notiOption.op_all = Const.NO;
            if (Const.YES.equals(getAgreeAppArarmYN())) {
                if (isLoginUser()) {
                    notiOption.op1 = Const.YES;
                    notiOption.op2 = Const.YES;
                    notiOption.op3 = Const.YES;
                } else {
                    notiOption.op1 = Const.YES;
                    notiOption.op2 = Const.NO;
                    notiOption.op3 = Const.NO;
                }
            } else {
                notiOption.op1 = Const.NO;
                notiOption.op2 = Const.NO;
                notiOption.op3 = Const.NO;
            }
        }
        return notiOption;
    }


    /* ------------------------------------------------------------
        클립 숨기기
     */
    private final String hideClipList = "hideClipList";

    /**
     * 클립 숨기기
     */
    public void putHideClip(String clipNo) {
        String jsonStr = null;
        ArrayList<String> clipList = getHideClipList();
        if (!TextUtils.isEmpty(clipNo)) {
            clipList.add(clipNo);
            jsonStr = new Gson().toJson(clipList);
        }
        _editor.putString(hideClipList, jsonStr);
        _editor.commit();
    }

    /**
     * 숨기기한 클립 삭제
     */
    public void clearHideClipList() {
        _editor.putString(hideClipList, null);
        _editor.commit();
    }

    private ArrayList<String> getHideClipList() {
        String jsonStr = _shared.getString(hideClipList, null);
        ArrayList<String> returnList = new ArrayList<>();
        try {
            if (!TextUtils.isEmpty(jsonStr)) {
                returnList = new Gson().fromJson(jsonStr, new TypeToken<ArrayList<String>>() {
                }.getType());
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        }
        return returnList;
    }

    /* ------------------------------------------------------------
        카메라로 인한 오류가 있을때 저장된 경로가 사라지지 않게 하기 위한 shared 영역
     */
    private final String SAVE_CAMERA = "save_camera";
    private final String CROP_PATH = "crop_path";

    /**
     * 직접촬영시의 temp photo path <br>
     * {@link #getCameraSavePath()}후에 null처리 하여준다.
     */
    public void putCameraSavePath(String path) {
        _editorFlash.putString(SAVE_CAMERA, path);
        _editorFlash.commit();
    }

    /**
     * 직접촬영시의 temp photo path <br>
     * 사용후에 {@link #putCameraSavePath(String)}을 사용하여 null처리 하여준다.
     */
    public String getCameraSavePath() {
        return _sharedFlash.getString(SAVE_CAMERA, null);
    }

    /**
     * Crop시의 temp image path <br>
     * {@link #getCropImagePath()}후에 null처리 하여준다.
     */
    public void putCropImagePath(String path) {
        _editorFlash.putString(CROP_PATH, path);
        _editorFlash.commit();
    }

    /**
     * Crop시의 temp image path <br>
     * 사용후에 {@link #putCropImagePath(String)}을 사용하여 null처리 하여준다.
     */
    public String getCropImagePath() {
        return _sharedFlash.getString(CROP_PATH, null);
    }


    /* ------------------------------------------------------------
        util
     */

    /**
     * URLEncode된 string을 URLDecode 한다.
     */
    final String urlDecoder(String str) {
        String decodeStr = null;
        try {
            if (str != null) decodeStr = URLDecoder.decode(str, HTTP.UTF_8);
        } catch (Exception e) {
            Log.e(TAG, "Exception - " + e.getMessage(), e);
        }
        return decodeStr;
    }

    /**
     * string을 URLEncode 한다.
     */
    final String urlEncoder(String str) {
        String encodeStr = null;
        try {
            if (str != null) encodeStr = URLEncoder.encode(str, HTTP.UTF_8);
        } catch (UnsupportedEncodingException e) {
            Log.e(TAG, "UnsupportedEncodingException - " + e.getMessage());
        } catch (Exception e) {
            Log.e(TAG, "Exception - " + e.getMessage());
        }
        return encodeStr;
    }


    /* ------------------------------------------------------------
        DESC: unuse
     */

}