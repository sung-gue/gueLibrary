package com.breakout.util.data;

import android.database.sqlite.SQLiteDatabase;

/**
 * {@link DBHelper} 에서 db의 생성과 재생성을 할 때 {@link DBManager}를 extends 한 class에게
 * 생성과 재생성의 테이블 작업을 맏기기 위한 callback interface
 *
 * @author sung-gue
 * @version 1.0 (2012. 10. 4.)
 */
interface DBHelperDelegate {

    /**
     * db의 생성 작업을 위한 execute create sql
     */
    void DBCreate(SQLiteDatabase db);

    /**
     * db의 재생성 작업을 위한 execute drop sql
     */
    void DBUpgrade(SQLiteDatabase db);
}
