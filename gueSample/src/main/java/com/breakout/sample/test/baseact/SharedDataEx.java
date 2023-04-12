package com.breakout.sample.test.baseact;

import android.content.Context;

import com.breakout.util.storage.SharedStorage;

/**
 * App SharedPreferences<br/>
 *
 * @author sung-gue
 */
public class SharedDataEx extends SharedStorage {

    protected SharedDataEx(Context context) {
        super(context);
    }

    private static SharedDataEx _instance;

    public static synchronized SharedDataEx getInstance(Context context) {
        if (_instance == null) _instance = new SharedDataEx(context);
        return _instance;
    }

    public static synchronized SharedDataEx getInstance() throws Exception {
        if (_instance == null) throw new Exception("IMSharedData instance is null");
        return _instance;
    }

    public static void destroyInstance() {
        if (_instance != null) {
            _instance.destroy();
            _instance = null;
        }
    }

    public void clearUserInfo() {
        clear(ClearMode.USER_CLEAR);
    }


    /* ------------------------------------------------------------
        default
     */
    private final String agreeAppArarmYN = "agreeAppArarmYN";

    public void setAgreeAppArarmYN(String agreeAppArarmYN) {
        _editor.putString(this.agreeAppArarmYN, agreeAppArarmYN);
        _editor.apply();
    }

    public String getAgreeAppArarmYN() {
        return _shared.getString(agreeAppArarmYN, "N");
    }


}
