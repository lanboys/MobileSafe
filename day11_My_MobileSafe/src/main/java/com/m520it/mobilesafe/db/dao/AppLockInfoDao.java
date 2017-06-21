package com.m520it.mobilesafe.db.dao;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.text.TextUtils;

import com.m520it.mobilesafe.db.AppLockSQLiteOpenHelper;
import com.m520it.mobilesafe.db.DbCons;

import java.util.ArrayList;
import java.util.List;

import static com.m520it.mobilesafe.service.WatchDogService.OBSERVER_URI;

/**
 * @author 蓝兵
 * @time 2016/12/25  15:33
 */
public class AppLockInfoDao {

    private static volatile AppLockInfoDao instance = null;
    private static AppLockSQLiteOpenHelper mDbHelper;
    private static Context mContext;

    private AppLockInfoDao() {
        if (mDbHelper == null) {
            throw new RuntimeException("prepare方法未被调用,请先调用!");
        }
    }

    public static void prepare(Context context) {
        if (mDbHelper == null) {
            mContext = context;
            mDbHelper = new AppLockSQLiteOpenHelper(context);
            //首次调用进行初始化
        } else {
            //第二次调用报异常
            throw new RuntimeException("prepare()方法已经被调用过了!");
        }
    }

    public static AppLockInfoDao getInstance() {

        if (instance == null) {
            synchronized (BlackNumberInfoDao.class) {
                if (instance == null) {
                    instance = new AppLockInfoDao();
                }
            }
        }
        return instance;
    }

    //增加一条数据
    public boolean insert(String newAppName) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DbCons.APP_PACKAGE_NAME, newAppName);

        long insert = db.insert(DbCons.APP_LOCK_TABLE, DbCons.APP_PACKAGE_NAME, values);
        db.close();

        Uri uri = Uri.parse(OBSERVER_URI);
        ContentResolver contentResolver = mContext.getContentResolver();
        contentResolver.notifyChange(uri,null);

        return !(insert == -1);
    }

    public boolean delete(String appName) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        int delete = db.delete(DbCons.APP_LOCK_TABLE, DbCons.APP_PACKAGE_NAME + "=?", new String[]{appName});
        db.close();

        Uri uri = Uri.parse(OBSERVER_URI);
        ContentResolver contentResolver = mContext.getContentResolver();
        contentResolver.notifyChange(uri,null);

        return !(delete == 0);
    }

    public boolean query(String appName) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        String string = null;
        Cursor cursor = db.query(DbCons.APP_LOCK_TABLE, new String[]{DbCons.APP_PACKAGE_NAME}, DbCons.APP_PACKAGE_NAME + "=?", new String[]{appName}, null, null, null);
        //最好使用while (cursor.moveToNext())
        if (cursor.moveToFirst()) {
            string = cursor.getString(0);
        }
        cursor.close();
        db.close();
        return !TextUtils.isEmpty(string);
    }

    public List<String> queryAll() {
        List<String> appLockNames = new ArrayList<>();
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        Cursor cursor = db.query(DbCons.APP_LOCK_TABLE, new String[]{DbCons.APP_PACKAGE_NAME}, null, null, null, null, null);
        while (cursor.moveToNext()) {
            String string = cursor.getString(0);
            appLockNames.add(string);
        }
        cursor.close();
        db.close();
        return appLockNames;
    }
}
