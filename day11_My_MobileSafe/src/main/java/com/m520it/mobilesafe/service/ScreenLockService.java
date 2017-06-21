package com.m520it.mobilesafe.service;

import android.app.ActivityManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.support.annotation.Nullable;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class ScreenLockService extends Service {

    private InnerLockReceiver mReceiver;
    private Timer mTimer;
    private TimerTask mTask;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        System.out.println("服务开启");
        mReceiver = new InnerLockReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        registerReceiver(mReceiver, filter);
        // mTask = new TimerTask() {
        //     @Override
        //     public void run() {
        //         // System.out.println("我是定时器中的逻辑");
        //     }
        // };
        // mTimer = new Timer();
        // mTimer.schedule(mTask, 0, 5000);

        super.onCreate();
    }

    @Override
    public void onDestroy() {
        unregisterReceiver(mReceiver);
        mReceiver = null;
        super.onDestroy();
    }

    class InnerLockReceiver extends BroadcastReceiver {

        //屏幕一旦锁住,就开始清理所有的进程
        @Override
        public void onReceive(Context context, Intent intent) {
            ActivityManager am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
            List<ActivityManager.RunningAppProcessInfo> processes = am.getRunningAppProcesses();
            for (ActivityManager.RunningAppProcessInfo info : processes) {
                am.killBackgroundProcesses(info.processName);
            }
        }
    }
}
