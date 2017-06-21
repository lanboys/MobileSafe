package com.m520it.mobilesafe.widget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.util.Log;

import com.m520it.mobilesafe.cons.Constant;

/**
 * Implementation of App Widget functionality.
 */
public class ClearWidget extends AppWidgetProvider {

    private ServiceConnection mConnection;

    public ClearWidget() {
        super();
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.v(Constant.TAG, "ClearWidget.onReceive:::");
        super.onReceive(context, intent);
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        Log.v(Constant.TAG, "ClearWidget.onDeleted:::");
        super.onDeleted(context, appWidgetIds);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        Intent intent = new Intent(context, ClearWidgetService.class);
        context.startService(intent);

        // IntentReceiver components are not allowed to bind to services
        // mConnection = new ServiceConnection() {
        //     @Override
        //     public void onServiceConnected(ComponentName name, IBinder service) {
        //
        //     }
        //
        //     @Override
        //     public void onServiceDisconnected(ComponentName name) {
        //
        //     }
        // };
        // context.bindService(intent, mConnection,Context.BIND_AUTO_CREATE);
        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }

    @Override
    public void onEnabled(Context context) {
        Log.v(Constant.TAG, "ClearWidget.onEnabled:::");
    }

    @Override
    public void onDisabled(Context context) {
        Log.v(Constant.TAG, "ClearWidget.onDisabled:::");
        Intent intent = new Intent(context, ClearWidgetService.class);
        context.stopService(intent);
        // context.unbindService(mConnection);
    }
}

