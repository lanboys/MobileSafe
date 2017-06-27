package com.bing.mobilesafe.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

/**
 * @author 蓝兵
 * @time 2016/12/14  21:07
 */
public class IntentUtil {

    public static void startActivity(Context packageContext, Class<?> cls) {
        Intent intent = new Intent(packageContext, cls);
        packageContext.startActivity(intent);
    }


    public static void startActivityForResult(Activity activity, Class<?> cls, int requestCode) {
        Intent intent = new Intent(activity, cls);
        activity.startActivityForResult(intent,requestCode);
    }

    // public static void startActivityForResult(@NonNull Fragment fragment,
    //         @RequiresPermission Intent intent, int requestCode) {
    //     Intent intent = new Intent(activity, cls);
    //     activity.startActivityFromFragment(fragment, intent, requestCode);
    // }

    public static void startService(Context packageContext, Class<?> cls) {
        Intent intent = new Intent(packageContext, cls);
        packageContext.startService(intent);
    }

    public static void stopService(Context packageContext, Class<?> cls) {
        Intent intent = new Intent(packageContext, cls);
        packageContext.stopService(intent);
    }

}
