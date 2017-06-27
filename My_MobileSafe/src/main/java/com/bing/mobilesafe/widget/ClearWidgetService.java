package com.bing.mobilesafe.widget;

import android.app.ActivityManager;
import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.IBinder;
import android.text.format.Formatter;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.bing.mobilesafe.R;
import com.bing.mobilesafe.cons.Constant;
import com.bing.mobilesafe.utils.ProcessUtil;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class ClearWidgetService extends Service {

    private WidgetBroadcastReceiver mReceiver;

    public ClearWidgetService() {
    }

    @Override
    public void onDestroy() {
        Log.v(Constant.TAG, "ClearWidgetService.onDestroy:::");

        unregisterReceiver(mReceiver);
        mReceiver = null;
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.v(Constant.TAG, "ClearWidgetService.onStartCommand:::");

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onCreate() {
        Log.v(Constant.TAG, "ClearWidgetService.onCreate:::");


        mReceiver = new WidgetBroadcastReceiver();

        IntentFilter intentFilter = new IntentFilter("com.m520it.mobilesafe.widget.ClearWidgetService.WidgetBroadcastReceiver");
        registerReceiver(mReceiver, intentFilter);

        //创建远程view
        final RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout.widget_clear_layout);

        final AppWidgetManager widgetManager = AppWidgetManager.getInstance(ClearWidgetService.this);
        final ComponentName componentName = new ComponentName(getApplicationContext(), ClearWidget.class);

        Intent intent = new Intent("com.m520it.mobilesafe.widget.ClearWidgetService.WidgetBroadcastReceiver");
        final PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {

                //对view进行赋值
                String s = Formatter.formatFileSize(getApplicationContext(), ProcessUtil.getAvailMem(getApplicationContext()));
                remoteViews.setTextViewText(R.id.process_count, "正在运行的软件: " + ProcessUtil.getRunningProcessCount(ClearWidgetService.this));
                remoteViews.setTextViewText(R.id.process_memory, "可用内存: " + s);
                remoteViews.setOnClickPendingIntent(R.id.btn_clear, pendingIntent);

                widgetManager.updateAppWidget(componentName, remoteViews);
            }
        }, 0, 5000);

        super.onCreate();
    }

    class WidgetBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            ActivityManager am= (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            List<ActivityManager.RunningAppProcessInfo> processes = am.getRunningAppProcesses();

            //获取包管理器
            PackageManager packageManager = context.getPackageManager();

            for (ActivityManager.RunningAppProcessInfo  info: processes) {
                String processName = info.processName;
                PackageInfo packageInfo = null;
                //通过进程名字获取包信息
                try {
                      packageInfo = packageManager.getPackageInfo(processName, 0);

                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }
                //不杀死自己
                if (packageInfo != null && packageInfo.packageName.equals(getPackageName())) {
                    continue;
                }
                am.killBackgroundProcesses(processName);
            }
            Toast.makeText(context, "杀掉了:"+ processes.size()+"个进程", Toast.LENGTH_SHORT).show();
            Log.v(Constant.TAG, "WidgetBroadcastReceiver.onReceive:::");
            
        }
    }
}
