package com.m520it.mobilesafe.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import java.util.Map;
import java.util.Set;

/**
 * @author 蓝兵
 * @time 2016/12/12  14:29
 */
public class SpUtil {

    private static final String TAG = "-->mobile";

    private static SharedPreferences SP;

    //单例模式可以使用标志,运行调用一次
    private SpUtil() {
        throw new UnsupportedOperationException();
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

    //对sp进行非空判断
    private static void isSPNull() {
        if (SP == null) {
            throw new RuntimeException("prepare方法未被调用,请先调用!");
        }
    }

    public static boolean getBoolean(@NonNull String key) {
        isSPNull();
        if (!SP.contains(key)) {
            Log.w(TAG, "SpUtil.getBoolean:::" + key + " 不存在!");
        }
        return SP.getBoolean(key, false);
    }

    public static boolean putBoolean(@NonNull String key, boolean value) {
        isSPNull();
        if (TextUtils.isEmpty(key)) {
            //键不能为空,值可以
            throw new RuntimeException("键不能为空");
        }
        return SP.edit().putBoolean(key, value).commit();
    }

    public static String getString(@NonNull String key) {
        isSPNull();
        if (!SP.contains(key)) {
            Log.w(TAG, "SpUtil.getBoolean:::" + key + " 不存在!");
        }
        return SP.getString(key, null);
    }

    public static boolean putString(@NonNull String key, @NonNull String value) {
        isSPNull();
        if (TextUtils.isEmpty(key)) {
            throw new RuntimeException("键不能为空");
        }
        return SP.edit().putString(key, value).commit();
    }

    public static int getInt(@NonNull String key) {
        isSPNull();
        if (!SP.contains(key)) {
            Log.w(TAG, "SpUtil.getBoolean:::" + key + " 不存在!");
        }
        return SP.getInt(key, 0);
    }

    public static boolean putInt(@NonNull String key, @NonNull int value) {
        isSPNull();
        if (TextUtils.isEmpty(key)) {
            throw new RuntimeException("键不能为空");
        }
        return SP.edit().putInt(key, value).commit();
    }

    //清空所有值
    public static boolean clear() {
        isSPNull();
        return SP.edit().clear().commit();
    }

    //移除某一个值
    public static boolean remove(@NonNull String... keys) {
        isSPNull();
        //有待测试,比如 remove 不存在的key
        SharedPreferences.Editor editor = SP.edit();
        for (int i = 0; i < keys.length; i++) {
            if (SP.contains(keys[i])) {
                editor.remove(keys[i]);
            }
        }
        return editor.commit();
    }

    public static String[] getAllKeyArray() {
        isSPNull();
        //集合的遍历不熟悉
        Map<String, Object> all = (Map<String, Object>) SP.getAll();

        Set<String> strings = all.keySet();
        String[] array = strings.toArray(new String[0]);

        return array;

        // Collection<Object> values = all.values();
        // Set<Map.Entry<String, Object>> entries = all.entrySet();
        // Object[] objects = strings.toArray();
    }






}
