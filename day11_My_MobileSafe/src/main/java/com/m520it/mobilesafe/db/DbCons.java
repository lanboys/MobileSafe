package com.m520it.mobilesafe.db;

import android.provider.BaseColumns;

/**
 * @author 蓝兵
 * @time 2016/12/17  11:46
 */
public class DbCons implements BaseColumns {

    /**
     * 黑名单数据库常量
     */
    public static final String BLACK_DB_NAME = "black.db";
    public static final String BLACK_TABLE = "blacknumberinfo";

    public static final String BLACK_PHONE = "phone";
    public static final String BLACK_MODE = "mode";
    /**
     * 程序锁数据库常量
     */
    public static final String APP_LOCK_DB_NAME = "applock.db";
    public static final String APP_LOCK_TABLE = "applockinfo";

    public static final String APP_PACKAGE_NAME = "appname";
}
