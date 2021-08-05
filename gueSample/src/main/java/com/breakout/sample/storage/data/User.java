package com.breakout.sample.storage.data;

import android.database.sqlite.SQLiteDatabase;
import android.os.Parcel;
import android.os.Parcelable;
import android.provider.BaseColumns;

public class User implements Parcelable {
    public static class Entry implements BaseColumns {
        public static final String TABLE_NAME = "user";
        public static final String SQL_CREATE =
                "CREATE TABLE " + TABLE_NAME + "(" +
                        " user_id        INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT," +
                        " email          TEXT UNIQUE, " +
                        " nick           TEXT, " +
                        " gender         TEXT, " +
                        " birth          TEXT " +
                        ")";
        private static final String SQL_DELETE = "DROP TABLE IF EXISTS " + TABLE_NAME;

        public static final String userId = "user_id";
        public static final String email = "email";
        public static final String nick = "nick";
        public static final String gender = "gender";
        public static final String birth = "birth";

        public static void createTable(SQLiteDatabase db) {
            db.execSQL(SQL_CREATE);
        }

        public static void deleteTable(SQLiteDatabase db) {
            db.execSQL(SQL_DELETE);
        }
    }

    public int userId;
    public String email;
    public String nick;
    public String gender;
    public String birth;

    public User() {
        
    }

    protected User(Parcel in) {
        userId = in.readInt();
        email = in.readString();
        nick = in.readString();
        gender = in.readString();
        birth = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(userId);
        dest.writeString(email);
        dest.writeString(nick);
        dest.writeString(gender);
        dest.writeString(birth);
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };
}