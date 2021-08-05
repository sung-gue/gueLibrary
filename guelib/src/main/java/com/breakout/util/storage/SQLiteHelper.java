package com.breakout.util.storage;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.breakout.util.Log;


/**
 * SQLite manager
 * <p>
 * https://developer.android.com/training/data-storage/sqlite
 *
 * @author sung-gue
 * @version 1.0 (2012. 10. 4.)
 */
public abstract class SQLiteHelper extends SQLiteOpenHelper {
    public final String TAG = getClass().getName();

    public enum Mode {
        read, write
    }

    /**
     * @param context application context
     * @param name    database file name
     * @param version database version
     */
    protected SQLiteHelper(Context context, String name, int version) {
        super(context, name, null, version);
    }

    /**
     * TODO: use in working thread
     */
    protected SQLiteDatabase getDatabase(Mode mode) {
        SQLiteDatabase db = null;
        switch (mode) {
            case read:
                db = getReadableDatabase();
                break;
            case write:
                db = getWritableDatabase();
                break;
        }
        Log.d(TAG, "database open : " + mode.name());
        return db;
    }

    @SuppressWarnings("unused")
    protected SQLiteDatabase openRead() {
        return getDatabase(Mode.read);
    }

    @SuppressWarnings("unused")
    protected SQLiteDatabase openWrite() {
        return getDatabase(Mode.write);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d(TAG, "database onCreate");
        onCreateAfter(db);
    }

    protected abstract void onCreateAfter(SQLiteDatabase db);

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d(TAG, String.format("database onUpgrade | old=%s, new=%s", oldVersion, newVersion));
        onUpgradeAfter(db, oldVersion, newVersion);
    }

    protected abstract void onUpgradeAfter(SQLiteDatabase db, int oldVersion, int newVersion);

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        Log.d(TAG, "database onOpen");
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        super.onDowngrade(db, oldVersion, newVersion);
        Log.d(TAG, String.format("database onDowngrade | old=%s, new=%s", oldVersion, newVersion));
    }

    @Override
    public synchronized void close() {
        super.close();
        Log.d(TAG, "database close");
    }

    /*
        INFO: query sample
     */

    /**
     * whereColumn 과 whereValue 둘중 하나라도 null 이면 전체 리스트
     *
     * @param whereColumn pk
     */
    public Cursor getList(SQLiteDatabase db, String tableName, String whereColumn, String whereValue) {
        Cursor cursor;
        if (whereColumn == null || whereValue == null) {
            cursor = db.rawQuery(String.format("SELECT * FROM %s", tableName), null);
        } else {
            cursor = db.rawQuery(String.format("SELECT * FROM %s WHERE %s = '%s'", tableName, whereColumn, whereValue), null);
        }
        return cursor;
    }

    /**
     * whereColumn,의 whereValue 가 존재하면 delete 후 insert
     *
     * @param whereColumn pk
     * @param update      true: update, false: insert
     */
    public long insert(SQLiteDatabase db, String tableName, ContentValues values, String whereColumn, String whereValue, boolean update) {
        Cursor cursor = db.rawQuery(String.format("SELECT * FROM %s WHERE %s = '%s'", tableName, whereColumn, whereValue), null);
        int rows = cursor.getCount();
        cursor.close();
        if (rows == 1 && update) {
            db.update(tableName, values, String.format("%s='%s'", whereColumn, whereValue), null);
        } else {
            db.delete(tableName, String.format("%s='%s'", whereColumn, whereValue), null);
        }
        return db.insert(tableName, null, values);
    }
}