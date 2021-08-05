package com.breakout.sample.storage;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.breakout.sample.storage.data.User;
import com.breakout.util.storage.SQLiteHelper;

import java.util.ArrayList;


/**
 * {@link SQLiteHelper} example
 *
 * @author sung-gue
 * @version 1.0 (2013. 1. 2.)
 */
public class DbHelper extends SQLiteHelper {
    private static final int DB_VERSION = 2;
    private static final String DB_NAME = "sample.db";
    private final SQLiteDatabase _db;

    private final String TB_USER;

    public DbHelper(Context context) {
        this(context, Mode.read);
    }

    public DbHelper(Context context, Mode mode) {
        super(context, DB_NAME, DB_VERSION);
        _db = getDatabase(mode);
        TB_USER = User.Entry.TABLE_NAME;
    }

    @Override
    protected void onCreateAfter(SQLiteDatabase db) {
        User.Entry.createTable(db);
    }

    @Override
    protected void onUpgradeAfter(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion != newVersion) {
            User.Entry.deleteTable(db);
            this.onCreateAfter(db);
        }
    }

    protected void truncateUser() {
        _db.delete(TB_USER, null, null);
    }


    /*
        INFO: user table setting
     */
    public long insertUser(ContentValues values) {
        return _db.insert(TB_USER, null, values);
    }

    public long insertUser(User user) {
        long result = -1;
        _db.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            values.put(User.Entry.email, user.email);
            values.put(User.Entry.nick, user.nick);
            values.put(User.Entry.gender, user.gender);
            values.put(User.Entry.birth, user.birth);
            /*
            Date curDate = new Date();
            String date = new SimpleDateFormat(Const.Date, Locale.getDefault()).format(curDate);
            String time = new SimpleDateFormat(Const.Time, Locale.getDefault()).format(curDate);
            */
            result = _db.insert(TB_USER, null, values);
            _db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.d(TAG, e.getMessage(), e);
        } finally {
            _db.endTransaction();
        }

        return result;
    }

    public long updateUser(ContentValues values, String user_id) {
        return _db.update(TB_USER, values, "user_id='" + user_id + "'", null);
    }

    /**
     * userInfo del
     */
    @SuppressWarnings("UnusedReturnValue")
    public long delUserInfo(String userId) {
        if (userId == null) {
            return _db.delete(TB_USER, null, null);
        } else {
            return _db.delete(TB_USER, "user_id=" + userId, null);
        }
    }

    public Cursor getUserInfo(String user_id) {
        return _db.rawQuery("SELECT * FROM " + TB_USER + " WHERE user_id=" + user_id, null);
    }

    /**
     * userInfo list
     */
    public Cursor getUserList() {
        return _db.rawQuery("SELECT rowid as _id, * FROM " + TB_USER + " ORDER BY nick ASC", null);
    }

    /**
     * userInfo list
     */
    public ArrayList<User> getUserList1() {
        Cursor c = _db.rawQuery("SELECT rowid as _id, * FROM " + TB_USER + " ORDER BY nick ASC", null);
        ArrayList<User> list = getRecordList(c);
        c.close();
        return list;
    }

    public ArrayList<User> getRecordList(Cursor c) {
        ArrayList<User> list = new ArrayList<>();
        while (c.moveToNext()) {
            User user = new User();
            user.userId = c.getInt(c.getColumnIndexOrThrow(User.Entry.userId));
            user.email = c.getString(c.getColumnIndexOrThrow(User.Entry.email));
            user.nick = c.getString(c.getColumnIndexOrThrow(User.Entry.nick));
            user.gender = c.getString(c.getColumnIndexOrThrow(User.Entry.gender));
            user.birth = c.getString(c.getColumnIndexOrThrow(User.Entry.birth));
            list.add(user);
        }
        c.close();
        return list;
    }

    /**
     * get user id
     */
    public int getLastUserId() {
        int userId = 0;
        Cursor c = _db.rawQuery("SELECT * FROM " + TB_USER, null);
        if (c.getCount() > 0) {
            c.moveToLast();
            int columnIndex = c.getColumnIndex("user_id");
            if (columnIndex >= 0) {
                userId = c.getInt(columnIndex);
            }
        }
        c.close();
        return ++userId;
    }
}