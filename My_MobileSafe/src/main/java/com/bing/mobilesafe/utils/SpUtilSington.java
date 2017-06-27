package com.bing.mobilesafe.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Log;

/**
 * @author 蓝兵
 * @time 2016/12/12  14:29
 */
public class SpUtilSington {

    private static final String TAG = "-->mobile";

    private static SharedPreferences SP;
    private static volatile SpUtilSington instance = null;

    //单例模式可以使用标志,运行调用一次
    //如何防止暴力反射???
    private SpUtilSington() {
        // throw new UnsupportedOperationException();
        isSPNull();
    }

    //对sp进行非空判断
    private static void isSPNull() {
        if (SP == null) {
            throw new RuntimeException("prepare方法未被调用,请先调用!");
        }
    }

    //不同的context对sharepreferences 有没有影响
    public static void prepare(Context context, String spFileName) {
        if (SP == null) {
            //首次调用进行初始化
            SP = context.getSharedPreferences(spFileName, Context.MODE_PRIVATE);
        } else {
            //第二次调用报异常
            throw new RuntimeException("prepare()方法已经被调用过了!");
        }
    }

    public static SpUtilSington getInstance() {

        if (instance == null) {
            synchronized (SpUtilSington.class) {
                if (instance == null) {
                    instance = new SpUtilSington();
                }
            }
        }
        return instance;
    }

    private boolean getBoolean(String key) {
        // isSPNull();
        if (!SP.contains(key)) {
            Log.w(TAG, "SpUtil.getBoolean:::" + key + " 不存在!");
        }
        return SP.getBoolean(key, false);
    }

    public String getString(String key) {
        // isSPNull();
        if (!SP.contains(key)) {
            Log.w(TAG, "SpUtil.getBoolean:::" + key + " 不存在!");
        }
        return SP.getString(key, null);
    }

    public boolean putBoolean(String key, boolean value) {
        // isSPNull();
        if (TextUtils.isEmpty(key)) {
            //键不能为空,值可以
            throw new RuntimeException("键不能为空");
        }
        return SP.edit().putBoolean(key, value).commit();
    }

    public boolean putString(String key, String value) {
        // isSPNull();
        if (TextUtils.isEmpty(key)) {
            throw new RuntimeException("键不能为空");
        }
        return SP.edit().putString(key, value).commit();
    }

    //清空所有值
    public boolean clear() {
        // isSPNull();
        return SP.edit().clear().commit();
    }

    //移除某一个值
    public boolean remove(String... keys) {
        // isSPNull();
        //有待测试,比如 remove 不存在的key
        SharedPreferences.Editor editor = SP.edit();
        for (int i = 0; i < keys.length; i++) {
            if (SP.contains(keys[i])) {
                editor.remove(keys[i]);
            }
        }
        return editor.commit();
    }
}
