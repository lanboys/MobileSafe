package com.bing.mobilesafe.service;

import android.app.ActivityManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;

import com.bing.mobilesafe.activity.WatchDogActivity;
import com.bing.mobilesafe.cons.Constant;
import com.bing.mobilesafe.db.dao.AppLockInfoDao;

import java.util.List;

import static com.bing.mobilesafe.activity.WatchDogActivity.LOCK_APP_PACKAGENAME;

public class WatchDogService extends Service {

    public static final String LOCK_ACTION = "com.m520it.mobilesafe.service.WatchDogService.InnerReceiver";
    public static final String OBSERVER_URI = "content://com.a520it.seemygo";

    private AppLockInfoDao mDao;
    private InnerReceiver mReceiver;
    private String tempNotLockPackageName;
    private boolean lockFalgs = false;
    private ActivityManager mActivityManager;
    private List<String> mAppLockLists;
    private InnerObserver mInnerObserver;
    private ContentResolver mContentResolver;

    public WatchDogService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        Log.v(Constant.TAG, "WatchDogService.onCreate:::");
        super.onCreate();

        mInnerObserver = new InnerObserver(new Handler());
        mContentResolver = getContentResolver();
        Uri uri = Uri.parse(OBSERVER_URI);
        mContentResolver.registerContentObserver(uri, true, mInnerObserver);

        mReceiver = new InnerReceiver();
        IntentFilter fileter = new IntentFilter();
        fileter.addAction(LOCK_ACTION);
        fileter.addAction(Intent.ACTION_SCREEN_OFF);
        fileter.addAction(Intent.ACTION_SCREEN_ON);

        registerReceiver(mReceiver, fileter);

        mDao = AppLockInfoDao.getInstance();
        mActivityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        startAppLock();
    }

    private void startAppLock() {
        mAppLockLists = mDao.queryAll();
        lockFalgs = true;

        new Thread(new Runnable() {
            @Override
            public void run() {
                while (lockFalgs) {
                    List<ActivityManager.RunningTaskInfo> runningTasks = mActivityManager.getRunningTasks(5);
                    String packageName = runningTasks.get(0).topActivity.getPackageName();

                    boolean contains = mAppLockLists.contains(packageName);
                    Log.v(Constant.TAG, "WatchDogService.run:::" + packageName);
                    Log.v(Constant.TAG, "WatchDogService.run:::" + mAppLockLists);

                    if (contains && !packageName.equals(tempNotLockPackageName)) {
                        Intent intent = new Intent(WatchDogService.this, WatchDogActivity.class);
                        intent.putExtra(WatchDogActivity.LOCK_APP_PACKAGENAME, packageName);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }

                    SystemClock.sleep(10);
                    // Log.v(Constant.TAG, "WatchDogService.run:::");
                }
            }
        }).start();
    }

    @Override
    public void onDestroy() {
        Log.v(Constant.TAG, "WatchDogService.onDestro:::");

        unregisterReceiver(mReceiver);
        mReceiver = null;

        mContentResolver.unregisterContentObserver(mInnerObserver);
        mInnerObserver = null;

        //关闭子线程的循环
        lockFalgs = false;

        super.onDestroy();
    }

    class InnerReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            String action = intent.getAction();

            if (action.equals(LOCK_ACTION)) {
                tempNotLockPackageName = intent.getStringExtra(LOCK_APP_PACKAGENAME);
            } else if (action.equals(Intent.ACTION_SCREEN_OFF)) {
                lockFalgs = false;
                tempNotLockPackageName = null;
            } else if (action.equals(Intent.ACTION_SCREEN_ON)) {
                lockFalgs = true;
                startAppLock();
            }
        }
    }

    class InnerObserver extends ContentObserver {

        public InnerObserver(Handler handler) {
            super(handler);
        }

        @Override
        public void onChange(boolean selfChange) {
            Log.v(Constant.TAG, "InnerObserver.onChange:::");

            mAppLockLists = mDao.queryAll();

            super.onChange(selfChange);
        }
    }
}
