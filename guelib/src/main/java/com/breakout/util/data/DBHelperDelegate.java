package com.breakout.util.data;

import android.database.sqlite.SQLiteDatabase;

/**
 * {@link DBHelper} 에서 db의 생성과 재생성을 할 때 {@link DBManager}를 extends 한 class에게
 * 생성과 재생성의 테이블 작업을 맏기기 위한 callback interface
 * @author gue
 * @since 2012. 10. 4.
 * @copyright Copyright.2011.gue.All rights reserved.
 * @version 
 * @history <ol>
 * 		<li>변경자/날짜 : 변경사항</li>
 * </ol>
 */
interface DBHelperDelegate {
	
	/**
	 * db의 생성 작업을 위한 execute create sql
	 * @author gue
	 * @since 2012. 10. 4.
	 */
	public void DBCreate(SQLiteDatabase db);
	
	/**
	 * db의 재생성 작업을 위한 execute drop sql 
	 * @author gue
	 * @since 2012. 10. 4.
	 */
	public void DBUpgrade(SQLiteDatabase db);
}
