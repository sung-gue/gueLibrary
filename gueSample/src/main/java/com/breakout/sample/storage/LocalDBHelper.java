package com.breakout.sample.storage;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;

import com.breakout.sample.Log;
import com.breakout.sample.constant.SharedData;
import com.breakout.sample.storage.data.TrainTimeTable;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;


public class LocalDBHelper extends SQLiteOpenHelper {
    private final String TAG = getClass().getName();

    private final Context _context;
    private SQLiteDatabase _db;

    public static final int DB_VERSION = 1;
    private final String DB_PATH;
    public static final String DB_NAME = "sample.db";


    @SuppressLint("ObsoleteSdkInt")
    public LocalDBHelper(Context context) throws SQLException, IOException {
        super(context, DB_NAME, null, DB_VERSION);
        _context = context;
        if (android.os.Build.VERSION.SDK_INT >= 17) {
            DB_PATH = context.getApplicationInfo().dataDir + "/databases/";
        } else {
//            DB_PATH = "/data/data/" + context.getPackageName() + "/databases/";
            DB_PATH = context.getFilesDir().getPath() + context.getPackageName() + "/databases/";
        }
        openDatabase();
    }

    public void openDatabase() throws SQLException, IOException {
        checkDatabase();
        String path = DB_PATH + DB_NAME;
        Log.d(TAG, "openDataBase: " + path);
//        _db = SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.CREATE_IF_NECESSARY);
        _db = SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.NO_LOCALIZED_COLLATORS);
        if (_db == null) {
            throw new SQLException("database file open error");
        }
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private void checkDatabase() throws IOException {
        String path = DB_PATH + DB_NAME;
        File folder = new File(DB_PATH);
        if (!folder.exists()) {
            folder.mkdirs();
        }
        File file = new File(path);
        int curVersion = SharedData.getInstance(_context).getDatabaseVersion();
        boolean isExist = file.exists();
        Log.d(TAG, String.format("checkDatabase: %s - %s  /  %s %s ",
                path, isExist, DB_VERSION, curVersion
        ));
        if (!isExist) {
            if (false) {
                this.getReadableDatabase();
                this.close();
            }
            copyDatabase();
        } else if (curVersion < DB_VERSION) {
            file.delete();
            copyDatabase();
        }
    }

    private void copyDatabase() throws IOException {
        InputStream inputStream = _context.getAssets().open(DB_NAME);
        String path = DB_PATH + DB_NAME;
        OutputStream outputStream = new FileOutputStream(path);
        byte[] buffer = new byte[1024];
        int length;
        while ((length = inputStream.read(buffer)) > 0) {
            outputStream.write(buffer, 0, length);
        }
        outputStream.flush();
        outputStream.close();
        inputStream.close();
        SharedData.getInstance(_context).setDatabaseVersion(DB_VERSION);
    }

    @Override
    public synchronized void close() {
        if (_db != null) {
            _db.close();
        }
        super.close();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d(TAG, "onCreate: ");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d(TAG, "onUpgrade: ");
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        Log.d(TAG, "onOpen: ");
    }

    /**
     * 기본 리스트
     */
    /*
    select * from TrainTimetable where type = '평일상행' order by time asc
    */
    public ArrayList<TrainTimeTable> getList(TrainTimeTable.Type type) {
        Cursor c = _db.rawQuery(
                "select * from TrainTimetable where type = ? order by time asc"
                , new String[]{type.name}
        );
        ArrayList<TrainTimeTable> list = new ArrayList<>();
        int i = 0;
        while (c.moveToNext()) {
            TrainTimeTable item = new TrainTimeTable();
            item.position = i++;
            item.id = c.getInt(c.getColumnIndexOrThrow(TrainTimeTable.Entry.id));
            String typeName = c.getString(c.getColumnIndexOrThrow(TrainTimeTable.Entry.type));
            item.type = TrainTimeTable.Type.getType(typeName);
            item.trainNum = c.getString(c.getColumnIndexOrThrow(TrainTimeTable.Entry.trainNum));
            item.time = c.getString(c.getColumnIndexOrThrow(TrainTimeTable.Entry.time));
            item.currentStation = c.getString(c.getColumnIndexOrThrow(TrainTimeTable.Entry.currentStation));
            item.arrivalStation = c.getString(c.getColumnIndexOrThrow(TrainTimeTable.Entry.arrivalStation));
            list.add(item);
        }
        c.close();
        return list;
    }

    /**
     * 첫차 막차 리스트
     */
    /*
    select type, '첫차' as timeType , min(id) as id , min(time) as time , arrivalStation
    from TrainTimetable
    where type like '평일%'
    group by type
    union
    select type, '막차' as timeType , max(id) as id , max(time) as time , arrivalStation
    from TrainTimetable
    where type like '평일%'
    group by type, arrivalStation
    order by type, time
     */
    public ArrayList<TrainTimeTable> getListFirstAndLast(String type) {
        if (TextUtils.isEmpty(type)) {
            type = "평일";
        }
        type += "%";
        Cursor c = _db.rawQuery(
                "    select type, '첫차' as timeType , min(id) as id , min(time) as time , arrivalStation " +
                        "    from TrainTimetable " +
                        "    where type like ? " +
                        "    group by type " +
                        "    union " +
                        "    select type, '막차' as timeType , max(id) as id , max(time) as time , arrivalStation " +
                        "    from TrainTimetable " +
                        "    where type like ? " +
                        "    group by type, arrivalStation " +
                        "    order by type, time"
                , new String[]{type, type}
        );
        ArrayList<TrainTimeTable> list = new ArrayList<>();
        while (c.moveToNext()) {
            TrainTimeTable item = new TrainTimeTable();
            String typeName = c.getString(c.getColumnIndexOrThrow(TrainTimeTable.Entry.type));
            item.type = TrainTimeTable.Type.getType(typeName);
            item.timeType = c.getString(c.getColumnIndexOrThrow("timeType"));
            item.id = c.getInt(c.getColumnIndexOrThrow(TrainTimeTable.Entry.id));
            item.time = c.getString(c.getColumnIndexOrThrow(TrainTimeTable.Entry.time));
            item.arrivalStation = c.getString(c.getColumnIndexOrThrow(TrainTimeTable.Entry.arrivalStation));
            list.add(item);
        }
        c.close();
        return list;
    }

    /**
     * 정차가 가장 많은 역을 기준으로 최상위 역 구하기
     */
    /*
    select group_concat(type), arrivalStation, sum(cnt) as sum
    from (select type, arrivalStation, count() as cnt
      from TrainTimetable
      group by type, arrivalStation
    )
    where type like '%상행'
    group by arrivalStation
    order by sum desc
    limit 1
     */
    public String getUpStation() {
        String result = "계양";
        Cursor c = _db.rawQuery(
                "    select group_concat(type), arrivalStation, sum(cnt) as sum " +
                        "    from (select type, arrivalStation, count() as cnt " +
                        "      from TrainTimetable " +
                        "      group by type, arrivalStation " +
                        "    )  " +
                        "    where type like '%상행' " +
                        "    group by arrivalStation " +
                        "    order by sum desc " +
                        "    limit 1"
                , null
        );
        if (c.moveToFirst()) {
            result = c.getString(c.getColumnIndexOrThrow("arrivalStation"));
        }
        c.close();
        return result;
    }

    public String getDownStation() {
        String result = "송도달빛축제공원";
        Cursor c = _db.rawQuery(
                "    select group_concat(type), arrivalStation, sum(cnt) as sum " +
                        "    from (select type, arrivalStation, count() as cnt " +
                        "      from TrainTimetable " +
                        "      group by type, arrivalStation " +
                        "    )  " +
                        "    where type like '%하행' " +
                        "    group by arrivalStation " +
                        "    order by sum desc " +
                        "    limit 1"
                , null
        );
        if (c.moveToFirst()) {
            result = c.getString(c.getColumnIndexOrThrow("arrivalStation"));
        }
        c.close();
        return result;
    }

    /*
    select * from TrainTimetable where type = '평일상행' and time > '11:00' order by time asc limit 1ñ
     */
    public TrainTimeTable getNextTime(TrainTimeTable.Type type, String time) {
        Cursor c = _db.rawQuery(
                "select * from TrainTimetable where type = ? and time > ? order by time asc limit 1"
                , new String[]{type.name, time}
        );
        TrainTimeTable item = new TrainTimeTable();
        if (c.moveToFirst()) {
            item.id = c.getInt(c.getColumnIndexOrThrow(TrainTimeTable.Entry.id));
            String typeName = c.getString(c.getColumnIndexOrThrow(TrainTimeTable.Entry.type));
            item.type = TrainTimeTable.Type.getType(typeName);
            item.trainNum = c.getString(c.getColumnIndexOrThrow(TrainTimeTable.Entry.trainNum));
            item.time = c.getString(c.getColumnIndexOrThrow(TrainTimeTable.Entry.time));
            item.currentStation = c.getString(c.getColumnIndexOrThrow(TrainTimeTable.Entry.currentStation));
            item.arrivalStation = c.getString(c.getColumnIndexOrThrow(TrainTimeTable.Entry.arrivalStation));
        }
        c.close();
        return item;
    }

}