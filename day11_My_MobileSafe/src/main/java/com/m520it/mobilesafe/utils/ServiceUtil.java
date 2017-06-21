package com.m520it.mobilesafe.utils;

import android.app.ActivityManager;
import android.content.Context;

import java.util.List;

/**
 * @author 蓝兵
 * @time 2016/12/18  10:19
 */
public class ServiceUtil {

    private static final String TAG = "-->mobile";

    /**
     *
     * @param context    getApplicationContext()
     * @param className  xxx.class.getName() 获取
     * @return
     */
    public static boolean isServiceRunning(Context context, String className) {
        //获取activity的管理器
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        //列出返回的服务数
        List<ActivityManager.RunningServiceInfo> runningServices = manager.getRunningServices(20);//代表我们希望返回的服务数目大小
        //遍历运行的服务
        for (ActivityManager.RunningServiceInfo runningService : runningServices) {

            String runningClassName = runningService.service.getClassName();
            // Log.v(TAG, "ServiceUtil.isServiceRunning:::正在运行的服务: "+runningClassName);
            if (runningClassName.equals(className)) {
                return true;
            }
        }

        return false;
    }
}
