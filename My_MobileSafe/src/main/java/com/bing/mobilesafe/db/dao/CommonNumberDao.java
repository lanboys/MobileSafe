package com.bing.mobilesafe.db.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by 520 on 2016/12/20.
 * 常用号码查询
 */

public class CommonNumberDao {

    public static int getGroupCount(SQLiteDatabase db) {
        int count;
        Cursor cursor = db.rawQuery("select count(*) from classlist", null);
        cursor.moveToNext();
        count = cursor.getInt(0);
        return count;
    }

    public static int getChildrenCount(SQLiteDatabase db, int groupPosition) {
        int count;
        int newGroupPosition = groupPosition + 1;
        Cursor cursor = db.rawQuery("select count(*) from table" + newGroupPosition, null);
        cursor.moveToNext();
        count = cursor.getInt(0);
        return count;
    }

    public static String getGroupView(SQLiteDatabase db, int groupPosition) {
        String name = "";
        int newGroupPosition = groupPosition + 1;
        Cursor cursor = db.query("classlist", new String[]{"name"}, "idx=?", new String[]{String.valueOf(newGroupPosition)}, null, null, null);
        cursor.moveToNext();
        name = cursor.getString(0);
        return name;
    }

    public static String getChildView(SQLiteDatabase db, int groupPosition, int childPosition) {
        String content = "";
        int count = 0;
        int newGroupPosition = groupPosition + 1;
        int newChildPosition = childPosition + 1;
        String table = "table" + newGroupPosition;//table1,tabl2
        Cursor cursor = db.query(table, new String[]{"number", "name"}, "_id=?", new String[]{String.valueOf(newChildPosition)}, null, null, null);

        cursor.moveToNext();
        String number = cursor.getString(cursor.getColumnIndex("number"));
        String name = cursor.getString(cursor.getColumnIndex("name"));
        content = name + "\n" + number;
        return content;
    }
}
