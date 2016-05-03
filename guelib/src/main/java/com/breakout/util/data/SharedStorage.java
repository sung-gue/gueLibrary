package com.breakout.util.data;

import android.content.Context;
import android.content.SharedPreferences;

import com.breakout.util.Log;


/**
 * Custom SharedPreferences <br>
 * {@link #_sharedFlash} : 중간에 삭제의 필요성이 있는 데이터
 * {@link #_shared} : 앱의 삭제전까지 가지고 가야할 데이터
 *
 * @author gue
 * @copyright Copyright.2011.gue.All rights reserved.
 * @history <ol>
 * <li>변경자/날짜 : 변경사항</li>
 * </ol>
 * @since 2012. 5. 30.
 */
public class SharedStorage {
    protected final String TAG = getClass().getSimpleName();

    protected static SharedStorage _instance;

    /**
     * app 삭제나 데이터영역 삭제 전까지 내용 유지
     */
    protected SharedPreferences _sharedConst;
    /**
     * app 삭제나 데이터영역 삭제 전까지 내용 유지
     */
    protected SharedPreferences.Editor _editorConst;
    /**
     * 내용 유지, {@link #clear(ClearMode)} 로 삭제 가능 영역
     */
    protected SharedPreferences _shared;
    /**
     * 내용 유지, {@link #clear(ClearMode)} 로 삭제 가능 영역
     */
    protected SharedPreferences.Editor _editor;
    /**
     * flash 내용, {@link #destroyInstance()} 를 통하여 app 종료시 삭제
     */
    protected SharedPreferences _sharedFlash;
    /**
     * flash 내용, {@link #destroyInstance()} 를 통하여 app 종료시 삭제
     */
    protected SharedPreferences.Editor _editorFlash;
    /**
     * application context
     */
    protected Context _context;

    /**
     * {@link #clear(ClearMode)}의 인자값
     *
     * @author gue
     * @since 2013. 9. 23.
     */
    public enum ClearMode {
        /**
         * flash 영역만 삭제
         */
        FLASH_CLEAR,
        /**
         * 전체 영역 삭제
         */
        ALL_CLEAR,;
    }


    protected SharedStorage() {
    }

    /**
     * @param constantName {@link #_sharedConst} name
     * @param normalName   {@link #_shared} name
     * @param flashName    {@link #_sharedFlash} name
     */
    protected SharedStorage(Context context, String constantName, String normalName, String flashName) {
        this._context = context;
        _sharedConst = _context.getSharedPreferences(constantName, Context.MODE_PRIVATE);
        _editorConst = _sharedConst.edit();
        _shared = _context.getSharedPreferences(normalName, Context.MODE_PRIVATE);
        _editor = _shared.edit();
        _sharedFlash = _context.getSharedPreferences(flashName, Context.MODE_PRIVATE);
        _editorFlash = _sharedFlash.edit();
        Log.i(TAG, TAG + " instance create");
        _instance = this;
    }

    /*public static synchronized SharedStorage getInstance(Context context) {
        if(_this == null) _this = new SharedStorage(context);
        return _this;
    }*/

    protected static SharedStorage getInstance() {
        return _instance;
    }

    public static void destroyInstance() {
        if (_instance != null) _instance.destroy();
    }

    /**
     * SharedPreferences 내용 삭제
     *
     * @param mode FLASH_CLEAR or ALL_CLEAR
     * @author gue
     * @since 2012. 7. 30.
     */
    public void clear(ClearMode mode) {
        switch (mode) {
            case FLASH_CLEAR:
                _editorFlash.clear();
                _editorFlash.commit();
                break;
            case ALL_CLEAR:
                _editor.clear();
                _editor.commit();
                _editorFlash.clear();
                _editorFlash.commit();
                break;
        }
    }


    protected void destroy() {
        Log.i(TAG, TAG + " destroy instance");
        clear(ClearMode.FLASH_CLEAR);

        _context = null;
        _shared = null;
        _editor = null;
        _sharedFlash = null;
        _editorFlash = null;
        _instance = null;
    }

    
    
/* ***************************************************************************************************
 * local DB open & close
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
