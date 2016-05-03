package com.breakout.util.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.breakout.util.Log;


/**
 * SQLite3 db helper <br>
 * db의 생성과 db_version에따른 db의 재생성을 도와준다.
 * @author gue
 * @since 2012. 10. 4.
 * @copyright Copyright.2011.gue.All rights reserved.
 * @version 1.0
 * @history <ol>
 * 		<li>변경자/날짜 : 변경사항</li>
 * </ol>
 */
class DBHelper extends SQLiteOpenHelper {
	private final DBHelperDelegate DBHelperDelegate;
	
	DBHelper(Context context, String db_name, int db_version, DBHelperDelegate DBHelperDelegate) {
		super(context, db_name, null, db_version);
		this.DBHelperDelegate = DBHelperDelegate;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		Log.i(DBManager.TAG, "DBHelper.onCreate : Table Create");
		DBHelperDelegate.DBCreate(db);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		if (oldVersion != newVersion ) {
			Log.i(DBManager.TAG, "DBHelper.onUpgrade : Table drop & create");
			DBHelperDelegate.DBUpgrade(db);
			onCreate(db);
		}
	}
}
