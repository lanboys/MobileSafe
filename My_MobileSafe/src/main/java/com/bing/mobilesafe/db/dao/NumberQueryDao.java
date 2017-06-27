package com.bing.mobilesafe.db.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import static com.bing.mobilesafe.cons.Constant.ASSETS_DB_ADDRESS_PATH;

/**
 * Created by xmg on 2016/12/18.
 * 提供号码归属地查询
 */

public class NumberQueryDao {

    public static String findLocation(String phone) {
        String address = null;
        if (phone.matches("^1[3578]\\d{9}$")) {

            SQLiteDatabase db = SQLiteDatabase.openDatabase(ASSETS_DB_ADDRESS_PATH,null, SQLiteDatabase.OPEN_READONLY);
            String path = db.getPath();
            // Log.v(Constant.TAG, "NumberQueryDao.findLocation:::" + path);
            Cursor cursor = db.rawQuery("select location from data2 where id=(select  outkey from data1 where  id=?)", new String[]{phone.substring(0, 7)});
            while (cursor.moveToNext()) {
                address = cursor.getString(0);
            }
        } else {
            switch (phone.length()) {
                case 3:
                    switch (Integer.valueOf(phone)) {
                        case 110:
                            address = "匪警";
                            break;
                        case 120:
                            address = "急救";
                            break;
                        case 119:
                            address = "火警";
                            break;
                    }

                    break;
                case 4:
                    address = "模拟器";
                    break;
                case 5:
                    address = "客服";
                    break;
                case 7:
                    if (!phone.startsWith("0") && !phone.startsWith("1")) {
                        address = "本地号码";
                    }
                    break;
                case 8:
                    if (!phone.startsWith("0") && !phone.startsWith("1")) {
                        address = "本地号码";
                    }
                    break;
                case 10:
                    if (phone.startsWith("0") && phone.length() >= 10) {
                        SQLiteDatabase db = SQLiteDatabase.openDatabase(ASSETS_DB_ADDRESS_PATH, null, SQLiteDatabase.OPEN_READONLY);
                        Cursor cursor = db.rawQuery("select location from data2 where area=?", new String[]{phone.substring(1, 3)});
                        if (cursor.moveToNext()) {
                            address = cursor.getString(0);
                        }
                        cursor = db.rawQuery("select location from data2 where area=?", new String[]{phone.substring(1, 4)});
                        if (cursor.moveToNext()) {
                            address = cursor.getString(0);
                        }
                    }
                    break;
                case 11:
                    if (phone.startsWith("0") && phone.length() >= 10) {
                        SQLiteDatabase db = SQLiteDatabase.openDatabase(ASSETS_DB_ADDRESS_PATH, null, SQLiteDatabase.OPEN_READONLY);
                        Cursor cursor = db.rawQuery("select location from data2 where area=?", new String[]{phone.substring(1, 3)});
                        if (cursor.moveToNext()) {
                            address = cursor.getString(0);
                        }
                        cursor = db.rawQuery("select location from data2 where area=?", new String[]{phone.substring(1, 4)});
                        if (cursor.moveToNext()) {
                            address = cursor.getString(0);
                        }
                    }
                    break;
                case 12:
                    if (phone.startsWith("0") && phone.length() >= 10) {
                        SQLiteDatabase db = SQLiteDatabase.openDatabase(ASSETS_DB_ADDRESS_PATH, null, SQLiteDatabase.OPEN_READONLY);
                        Cursor cursor = db.rawQuery("select location from data2 where area=?", new String[]{phone.substring(1, 3)});
                        if (cursor.moveToNext()) {
                            address = cursor.getString(0);
                        }
                        cursor = db.rawQuery("select location from data2 where area=?", new String[]{phone.substring(1, 4)});
                        if (cursor.moveToNext()) {
                            address = cursor.getString(0);
                        }
                    }
                    break;
            }
        }
        // return address + "";//如果address 为null ,这返回的是 "null" 是一个字符串
        return address == null ? "" : address;
    }
}
