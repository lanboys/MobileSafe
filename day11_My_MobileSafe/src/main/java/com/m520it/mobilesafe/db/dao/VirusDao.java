package com.m520it.mobilesafe.db.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.m520it.mobilesafe.bean.VirusUpdateBean;

import static com.m520it.mobilesafe.cons.Constant.ASSETS_DB_VIRUS_PATH;

/**
 * Created by 520 on 2016/12/26.
 */

public class VirusDao  {

    public static String getDesc(String md5) {
        String desc = null;
        SQLiteDatabase db = SQLiteDatabase.openDatabase(ASSETS_DB_VIRUS_PATH, null, SQLiteDatabase.OPEN_READONLY);
        Cursor cursor = db.query("datable", new String[]{"desc"}, "md5=?", new String[]{md5}, null, null, null);
        while (cursor.moveToNext()) {
            desc = cursor.getString(0);
        }
        cursor.close();
        db.close();
        return desc;
    }

    public static String getVersion(){
        String version="0";
        SQLiteDatabase db = SQLiteDatabase.openDatabase(ASSETS_DB_VIRUS_PATH, null, SQLiteDatabase.OPEN_READONLY);
        Cursor cursor = db.query("version", new String[]{"subcnt"}, null, null, null, null, null);
        while (cursor.moveToNext()){
            version=cursor.getString(0);
        }
        cursor.close();
        db.close();
        return version;
    }

    public static  void updateVersion(String  version){
        SQLiteDatabase db = SQLiteDatabase.openDatabase(ASSETS_DB_VIRUS_PATH, null, SQLiteDatabase.OPEN_READWRITE);
        ContentValues values=new ContentValues();
        values.put("subcnt",version);
        db.update("version",values,null,null);
        db.close();
    }

    public static void insertData(VirusUpdateBean virusUpdateBean) {
        SQLiteDatabase db = SQLiteDatabase.openDatabase(ASSETS_DB_VIRUS_PATH, null, SQLiteDatabase.OPEN_READWRITE);
        ContentValues values=new ContentValues();
        values.put("md5",virusUpdateBean.md5);
        values.put("type",virusUpdateBean.type);
        values.put("name",virusUpdateBean.name);
        values.put("desc",virusUpdateBean.desc);
        db.insert("datable",null,values);
        db.close();
    }



}
