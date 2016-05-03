package com.breakout.sample.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.breakout.util.CValue;
import com.breakout.util.data.DBManager;


/**
 * com.com.breakout.util 라이브러리의 {@link DBManager}를 상속받아 사용하는 예제 class
 * @author gue
 * @since 2013. 1. 2.
 * @copyright Copyright.2011.gue.All rights reserved.
 * @version 
 * @history <ol>
 * 		<li>변경자/날짜 : 변경사항</li>
 * </ol>
 */
public class LocalDB extends DBManager {
	/**
	 * {@link DBManager#DB_VERSION}
	 */
	private final static int DB_VERSION = 1;
	
	/**
	 *  {@link DBManager#DB_NAME}
	 */
	private final static String DB_NAME = "sample.db";
	
	/** 
	 * table name : userinfo 
	 */
	private static final String TABLE_USER_INFO = "userInfo";
	
	/** 
	 * create table sql : userinfo 
	 */
	private static final String CREATE_T_USERINFO = 
							"CREATE TABLE " + TABLE_USER_INFO +
							" (	email 				TEXT unique, " +
							"	nick 				TEXT NULL, " +
							"	gender 				TEXT NULL, " +
							"	birth 				TEXT NULL, " + 
							"	user_id 			INTEGER PRIMARY KEY);" ;
	
//	/** table name : TABLE_dbname */
//	private static final String TABLE_dbname = "";
//	/** create table sql : TABLE_dbname */
//	private static final String CREATE_TABLE_dbnamee = 
//			"CREATE TABLE " + TABLE_dbname +
//			"	(	root_id	TEXT PRIMARY KEY, " +
//			"		, " +
//			"		);" ;

	public LocalDB(Context ctx) {
		super(ctx, DB_NAME, DB_VERSION);
	}
	
	/**
	 * @param aimOfConnection 연결 목적
	 * 		<li>{@link CValue#DB_WRITE} : 쓰기</li>
	 * 		<li>{@link CValue#DB_READ} : 읽기</li>
	 */
	public LocalDB(Context ctx, String aimOfConnection) throws Exception {
		super(ctx, DB_NAME, DB_VERSION);
		openDB(aimOfConnection);
	}
	
	@Override
	public void DBCreate(SQLiteDatabase db) {
		db.execSQL(CREATE_T_USERINFO);
	}

	@Override
	public void DBUpgrade(SQLiteDatabase db) {
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER_INFO);
	}

	@Override
	protected void clear() {
		DB.delete(TABLE_USER_INFO, null, null);
	}


	@Override
	protected void deleteSection() {
		DB.delete(TABLE_USER_INFO, null, null);
	}
	
	
	//*********************************************************************************************
	// user table setting
	/** userInfo del */
	public long delUserInfo(String user_id ) {
		if (user_id == null) {
			return DB.delete(TABLE_USER_INFO, null, null);
		}
		else return DB.delete(TABLE_USER_INFO, "user_id=" + user_id, null);
	}
	
	/** userInfo save */
	public long setUserInfo(ContentValues value ) {
		return DB.insert(TABLE_USER_INFO, null, value);
	}
	
	/** userInfo save */
	public long updateUserInfo(ContentValues value, String user_id ) {
		return DB.update(TABLE_USER_INFO, value, "user_id='" + user_id + "'", null);
	}
	
	/** userInfo load */
	public Cursor getUserInfo(String user_id) {
		return DB.rawQuery("SELECT * FROM " + TABLE_USER_INFO + " WHERE user_id=" + user_id, null);
	}
	
	/** userInfo list */
	public Cursor getUserList() {
		return DB.rawQuery("SELECT rowid as _id, * FROM " + TABLE_USER_INFO + " ORDER BY nick ASC" , null);
	}
	
	/** get user id */
	public int getLastUserId(){
		int userId = 0;
		Cursor c = DB.rawQuery("SELECT * FROM " + TABLE_USER_INFO , null);
		if (c.getCount() > 0) {
			c.moveToLast();
			userId = c.getInt(c.getColumnIndex("user_id"));
		}
		c.close();
		return ++userId;
	}
	
}
