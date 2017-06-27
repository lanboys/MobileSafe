package com.bing.mobilesafe.db.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.SystemClock;

import com.bing.mobilesafe.bean.BlackNumberBean;
import com.bing.mobilesafe.db.BlackSQLiteOpenHelper;
import com.bing.mobilesafe.db.DbCons;

import java.util.ArrayList;

/**
 * @author 蓝兵
 * @time 2016/12/17  11:30
 */
public class BlackNumberInfoDao {

    private static volatile BlackNumberInfoDao instance = null;
    private static BlackSQLiteOpenHelper mDbHelper;

    private BlackNumberInfoDao() {
        if (mDbHelper == null) {
            throw new RuntimeException("prepare方法未被调用,请先调用!");
        }
    }

    public static void prepare(Context context) {
        if (mDbHelper == null) {
            mDbHelper = new BlackSQLiteOpenHelper(context);
            //首次调用进行初始化
        } else {
            //第二次调用报异常
            throw new RuntimeException("prepare()方法已经被调用过了!");
        }
    }

    public static BlackNumberInfoDao getInstance() {

        if (instance == null) {
            synchronized (BlackNumberInfoDao.class) {
                if (instance == null) {
                    instance = new BlackNumberInfoDao();
                }
            }
        }
        return instance;
    }

    //增加一条数据
    public boolean insert(String newPhone, String mode) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DbCons.BLACK_PHONE, newPhone);
        values.put(DbCons.BLACK_MODE, mode);
        long insert = db.insert(DbCons.BLACK_TABLE, null, values);
        db.close();
        if (insert == -1) {
            return false;
        }
        return true;
    }

    //删除一条数据
    public boolean delete(String phone) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        int delete = db.delete(DbCons.BLACK_TABLE, DbCons.BLACK_PHONE + "=?", new String[]{phone});
        db.close();
        if (delete == 0) {
            return false;
        }
        return true;
    }

    //更新一条数据
    public boolean update(String oldPhone, String newPhone, String mode) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DbCons.BLACK_PHONE, newPhone);
        values.put(DbCons.BLACK_MODE, mode);
        int update = db.update(DbCons.BLACK_TABLE, values, DbCons.BLACK_PHONE + "=?", new String[]{oldPhone});
        db.close();
        if (update == 0) {
            return false;
        }
        return true;
    }

    //查询某个号码的拦截模式
    public String query(String phone) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        Cursor cursor = db.query(DbCons.BLACK_TABLE, new String[]{DbCons.BLACK_MODE}, DbCons.BLACK_PHONE + "=?", new String[]{phone}, null, null, null, null);
        String mode = null;
        //不需要判断非空,即使没有数据,也不是空
        // if (cursor != null) {
        while (cursor.moveToNext()) {
            mode = cursor.getString(cursor.getColumnIndex(DbCons.BLACK_MODE));
        }
        cursor.close();
        db.close();
        return mode;
    }

    //倒序  查询所有数据
    public ArrayList<BlackNumberBean> queryAll() {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        Cursor cursor = db.query(DbCons.BLACK_TABLE, null, null, null, null, null, DbCons._ID + " desc");
        ArrayList<BlackNumberBean> blackNumberBeens = new ArrayList<>();

        while (cursor.moveToNext()) {
            String mode = cursor.getString(cursor.getColumnIndex(DbCons.BLACK_MODE));
            String phone = cursor.getString(cursor.getColumnIndex(DbCons.BLACK_PHONE));
            BlackNumberBean blackNumberBean = new BlackNumberBean(mode, phone);
            blackNumberBeens.add(blackNumberBean);
            //模拟时间延迟,要删除
            SystemClock.sleep(10);
        }
        cursor.close();
        db.close();

        return blackNumberBeens;
    }

    public ArrayList<BlackNumberBean> queryPart(int start, int count) {

        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        ArrayList<BlackNumberBean> blackNumberBeens = new ArrayList<>();
        //没有系统提供的语句
        Cursor cursor = db.rawQuery("select " + DbCons.BLACK_PHONE + ", " + DbCons.BLACK_MODE
                        + " from  " + DbCons.BLACK_TABLE + " order by  " + DbCons._ID + " desc limit ? offset ? ",
                new String[]{String.valueOf(count), String.valueOf(start)});

        while (cursor.moveToNext()) {
            String mode = cursor.getString(cursor.getColumnIndex(DbCons.BLACK_MODE));
            String phone = cursor.getString(cursor.getColumnIndex(DbCons.BLACK_PHONE));
            BlackNumberBean blackNumberBean = new BlackNumberBean(mode, phone);
            blackNumberBeens.add(blackNumberBean);
            //模拟时间延迟,要删除
            SystemClock.sleep(10);
        }
        cursor.close();
        db.close();

        return blackNumberBeens;
    }

    public int getCount() {

        int count;
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("select count(*) from  " + DbCons.BLACK_TABLE, null);
        cursor.moveToNext();

        count = cursor.getInt(0);
        cursor.close();
        db.close();
        return count;
    }
}
