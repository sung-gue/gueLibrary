package com.breakout.util.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.breakout.util.Log;


/**
 * Custom SharedPreferences<br/>
 *
 * @author sung-gue
 * @version 1.0 (2012. 5. 30.)
 */
public abstract class SharedStorage {
    /**
     * @see #clear(ClearMode)
     */
    public enum ClearMode {
        FLASH_CLEAR,
        USER_CLEAR,
        ALL_CLEAR,
        ;
    }

    protected final String TAG = getClass().getSimpleName();

    /**
     * constant data
     */
    protected SharedPreferences _sharedConst;
    /**
     * @see #_sharedConst
     */
    protected SharedPreferences.Editor _editorConst;
    /**
     * default data
     *
     * @see #clear(ClearMode)
     */
    protected SharedPreferences _shared;
    /**
     * @see #_shared
     */
    protected SharedPreferences.Editor _editor;
    /**
     * user data
     *
     * @see #clear(ClearMode)
     */
    protected SharedPreferences _sharedUser;
    /**
     * @see #_sharedUser
     */
    protected SharedPreferences.Editor _editorUser;
    /**
     * flash data
     * <p>
     * //@see #destroyInstance()
     */
    protected SharedPreferences _sharedFlash;
    /**
     * @see #_sharedFlash
     */
    protected SharedPreferences.Editor _editorFlash;
    /**
     * application context
     */
    protected Context _appContext;


    private SharedStorage() {
    }

    @Deprecated
    protected SharedStorage(Context appContext, String constantName, String normalName, String flashName) {
        this._appContext = appContext;
        _sharedConst = appContext.getSharedPreferences(constantName, Context.MODE_PRIVATE);
        _editorConst = _sharedConst.edit();
        _editorConst.apply();
        _shared = appContext.getSharedPreferences(normalName, Context.MODE_PRIVATE);
        _editor = _shared.edit();
        _editor.apply();
        _sharedFlash = appContext.getSharedPreferences(flashName, Context.MODE_PRIVATE);
        _editorFlash = _sharedFlash.edit();
        _editorFlash.apply();
        Log.w(TAG, TAG + " create instance (Deprecated Constructor)");
    }

    protected SharedStorage(Context appContext) {
        this(appContext, null, null, null, null);
    }

    /**
     * @param appContext     application context
     * @param constantSuffix {@link #_sharedConst} name
     * @param defaultSuffix  {@link #_shared} name
     * @param flashSuffix    {@link #_sharedFlash} name
     * @param userSuffix     {@link #_sharedUser} name
     */
    protected SharedStorage(Context appContext, String constantSuffix, String defaultSuffix, String flashSuffix, String userSuffix) {
        this._appContext = appContext;
        _sharedConst = getSharedPreferences(appContext, constantSuffix, "constant");
        _editorConst = getEditor(_sharedConst);
        _shared = getSharedPreferences(appContext, defaultSuffix, "default");
        _editor = getEditor(_shared);
        _sharedFlash = getSharedPreferences(appContext, flashSuffix, "flash");
        _editorFlash = _sharedFlash.edit();
        _editorFlash.apply();
        _sharedUser = getSharedPreferences(appContext, flashSuffix, "user");
        Log.i(TAG, TAG + " instance create");
        Log.i(TAG, TAG + " create instance (Deprecated Constructor)");
//        _instance = this;
    }

    private SharedPreferences getSharedPreferences(Context applicationContext, String name, String defaultName) {
        String key = applicationContext.getPackageName() + "_" + (TextUtils.isEmpty(name) ? defaultName : name);
        return applicationContext.getSharedPreferences(key, Context.MODE_PRIVATE);
    }

    private SharedPreferences.Editor getEditor(SharedPreferences sharedPreferences) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.apply();
        return editor;
    }

//    public static synchronized SharedStorage getInstance(Context context) {
//        if (_instance == null) _instance = new SharedStorage(context) {
//        };
//        return _instance;
//    }
//
//    public static SharedStorage getInstance() throws Exception {
//        return _instance;
//    }
//
//    public static void destroyInstance() {
//        if (_instance != null) _instance.destroy();
//    }

    /**
     * clear data
     *
     * @param mode FLASH_CLEAR | USER_CLEAR | ALL_CLEAR
     */
    public void clear(ClearMode mode) {
        switch (mode) {
            case FLASH_CLEAR:
                _editorFlash.clear();
                _editorFlash.commit();
                break;
            case USER_CLEAR:
                _editorUser.clear();
                _editorUser.commit();
                break;
            case ALL_CLEAR:
                _editor.clear();
                _editor.commit();
                _editorFlash.clear();
                _editorFlash.commit();
                _editorUser.clear();
                _editorUser.commit();
                break;
        }
    }

    protected void destroy() {
        Log.i(TAG, TAG + " destroy instance");
        clear(ClearMode.FLASH_CLEAR);

        _appContext = null;
        _shared = null;
        _editor = null;
        _sharedFlash = null;
        _editorFlash = null;
//        _instance = null;
    }


    /* ------------------------------------------------------------
        DESC: local DB open & close
     */
/*    private LocalDB db;
    
    private void db_read(){
        db = new LocalDB(mCtx);    
        db.openDB(Const.READ);
    }
    
    private void db_write(){
        db = new LocalDB(ctx);    
        db.openDB(Const.WRITE);
    }
    
    private void db_close(){
        if (db != null)    db.close();
        db = null;
    }*/

}
