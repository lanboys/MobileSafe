package com.m520it.mobilesafe.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * @author 蓝兵
 * @time 2016/12/17  11:15
 */
public class BlackSQLiteOpenHelper extends SQLiteOpenHelper {

    private static final String CREATE_BLACK_TABLE = "CREATE TABLE " + DbCons.BLACK_TABLE + " (\n" +
            DbCons._ID + "   INTEGER PRIMARY KEY AUTOINCREMENT, \n" +
            DbCons.BLACK_PHONE + " VARCHAR(20), \n" +
            DbCons.BLACK_MODE + " VARCHAR(2))";

    public BlackSQLiteOpenHelper(Context context) {
        super(context, DbCons.BLACK_DB_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_BLACK_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // db.execSQL("drop table if exists " + DbCons.BLACK_TABLE);
        // onCreate(db);
    }
}
