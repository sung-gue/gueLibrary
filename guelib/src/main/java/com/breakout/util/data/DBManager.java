package com.breakout.util.data;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.breakout.util.CValue;
import com.breakout.util.Log;



/**
 * SQLite3를 사용하여 local db 를 생성하여 필요한 data를 저장한다.
 * @author gue
 * @since 2012. 10. 4.
 * @copyright Copyright.2011.gue.All rights reserved.
 * @version 1.0
 * @history <ol>
 * 		<li>변경자/날짜 : 변경사항</li>
 * </ol>
 */
public abstract class DBManager implements DBHelperDelegate {
	/**
	 * class tag 
	 */
	public static final String TAG = "LocalDB";
	
	/**
	 * {@link DBHelper}
	 */
	private DBHelper dbHelper;
	
//	/**
//	 * {@link DBHelperDelegate}
//	 */
//	private final DBHelperDelegate dbHelperDelegate;
	
	/**
	 * manage a SQLite database
	 */
	protected SQLiteDatabase DB;
	
	/**
	 * current {@link #DB} name<br>
	 * ex) "sample.db"
	 */
	private final String DB_NAME;
	
	/**
	 * current {@link #DB} version
	 */
	private final int DB_VERSION;
	
	/** 
	 * Application Context
	 */
	protected final Context _context;
	// member variable end
	//*********************************************************************************************
	
	
	//*********************************************************************************************
	// base setting
	/**
	 * @param context Application Context
	 * @param db_name {@link #DB_NAME}
	 * @param db_version {@link #DB_VERSION}
	 */
	protected DBManager(Context context, String db_name, int db_version) {
		this._context = context;
		this.DB_NAME = db_name;
		this.DB_VERSION = db_version;
//		this.dbHelperDelegate = this;
	}
	
	/**
	 * 연결 목적에 따라 {@link #DB} 를 생성한다.
	 * @author gue
	 * @since 2012. 10. 4.
	 * @param aimOfConnection 연결 목적
	 * 		<li>{@link CValue#DB_WRITE} : 쓰기</li>
	 * 		<li>{@link CValue#DB_READ} : 읽기</li>
	 */
	protected synchronized void openDB(String aimOfConnection) throws Exception {
		// 1. 열려진 dbHelper 가 있다면 close
		if (dbHelper != null) close();
		
		// 2. DBHelper 생성
		dbHelper = new DBHelper(_context, DB_NAME, DB_VERSION, this);
		
		// 3-1. open db write mode
		if ( CValue.DB_WRITE.equals(aimOfConnection) ) {
			DB = dbHelper.getWritableDatabase();
			Log.d(TAG, "DB open write");
		} 
		// 3-2. open db read mode
		else if ( CValue.DB_READ.equals(aimOfConnection) ) {
			DB = dbHelper.getReadableDatabase();
			Log.d(TAG, "DB open read");
		} 
		// 3-2. open db fail, make exception
		else {
			Log.e(TAG, "Exception - DB open Fall");
			throw new Exception("DB open Fall");
		}
	}
	
	/**
	 * 
	 * @author gue
	 * @since 2012. 10. 4.
	 */
	public void read() {
		try {
			openDB(CValue.DB_READ);
		} catch (Exception e) {
			DB = null;
		}
	}
	
	/**
	 * 
	 * @author gue
	 * @since 2012. 10. 4.
	 */
	public void write() {
		try {
			openDB(CValue.DB_WRITE);
		} catch (Exception e) {
			DB = null;
		}
	}

	public void close() {
		dbHelper.close();
		Log.d(TAG, "DB Close");
	}
	
	/**
	 * 전체 table 내용 삭제<br>
	 * <code>ex) DB.delete(table name, null, null);</code>
	 * @author gue
	 * @since 2012. 10. 4.
	 */
	protected abstract void clear();
	
	/**
	 * 특정 영역의 table 내용 삭제<br>
	 * <code>ex) DB.delete(table name, null, null);</code>
	 * @author gue
	 * @since 2012. 10. 4.
	 */
	protected abstract void deleteSection();
	
	
	//*********************************************************************************************
	// custom query
	/**
	 * where_column 과 where_value 둘중 하나라도 null 이면 전체 리스트
	 * @param table_name
	 * @param where_column pk
	 * @param where_value 
	 * @return
	 */
	public Cursor get_list(String table_name, String where_column, String where_value){
		Cursor cursor = null;
		if ( where_column == null || where_value == null ) {
			cursor = DB.rawQuery("SELECT * FROM " + table_name, null);
		}
		else {
			cursor = DB.rawQuery("SELECT * FROM " + table_name + " WHERE " + where_column + "='" + where_value + "'", null);
		}
		return cursor;
	}
	
	/**
	 * where_column의 where_value 가 존재하면 delete 후 insert
	 * @param table
	 * @param values
	 * @param where_column pk
	 * @param where_value
	 * @param update true-update or false-insert
	 * @return
	 */
	public long insert(String table, ContentValues values, String where_column, String where_value, boolean update){
		Cursor cursor = DB.rawQuery("SELECT * FROM " + table + " WHERE " + where_column + "='" + where_value + "'", null);
		int rows = cursor.getCount();
		cursor.close();
		if (rows == 1 && update) DB.update(table, values, where_column + "='" + where_value + "'", null);
		else DB.delete(table, where_column + "='" + where_value + "'", null);
		return DB.insert(table, null, values);
	}
	
	
	public void execSQL(String query){
		DB.execSQL(query);
	}
	
	public Cursor rawQuery(String query){
		return DB.rawQuery(query, null);
	}
	
}

