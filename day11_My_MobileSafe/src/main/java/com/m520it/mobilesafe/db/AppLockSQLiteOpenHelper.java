package com.m520it.mobilesafe.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * @author 蓝兵
 * @time 2016/12/25  15:38
 */
public class AppLockSQLiteOpenHelper extends SQLiteOpenHelper {

    public AppLockSQLiteOpenHelper(Context context ) {
        super(context, DbCons.APP_LOCK_DB_NAME, null, 2);
    }
    private static final String CREATE_APP_LOCK_TABLE = "CREATE TABLE " + DbCons.APP_LOCK_TABLE + " (\n" +
            DbCons._ID + "   INTEGER PRIMARY KEY AUTOINCREMENT, \n" +
            DbCons.APP_PACKAGE_NAME + " VARCHAR(20))";

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_APP_LOCK_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists " + DbCons.APP_LOCK_TABLE);
        onCreate(db);

    }
}
