package com.m520it.mobilesafe.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.telephony.PhoneStateListener;
import android.telephony.SmsMessage;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.android.internal.telephony.ITelephony;
import com.m520it.mobilesafe.db.dao.BlackNumberInfoDao;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class CallSmsService extends Service {

    private static final String TAG = "-->mobile";
    private InnerSmsReceiver mInnerSmsReceiver;
    private IntentFilter mIntentFilter;
    private BlackNumberInfoDao mDao;
    private TelephonyManager mTelephonyManager;
    private MyPhoneStateListner mListener;

    public CallSmsService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        Log.v(TAG, "CallSsService.onCreate:::黑名单服务开启");
        //获取操作数据库的dao
        mDao = BlackNumberInfoDao.getInstance();
        //动态 创建广播接收者
        mInnerSmsReceiver = new InnerSmsReceiver();
        //创建Intent过滤器
        mIntentFilter = new IntentFilter();
        //添加action
        mIntentFilter.addAction("android.provider.Telephony.SMS_RECEIVED");
        //设置最大的优先级,确保最先收到广播
        mIntentFilter.setPriority(Integer.MAX_VALUE);
        //动态 注册广播接收者
        registerReceiver(mInnerSmsReceiver, mIntentFilter);

        //监听电话状态
        mTelephonyManager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        mListener = new MyPhoneStateListner();
        //设置监听
        mTelephonyManager.listen(mListener, PhoneStateListener.LISTEN_CALL_STATE);
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        Log.v(TAG, "CallSmsService.onDestroy:::黑名单服务关闭");
        //解除监听
        mTelephonyManager.listen(mListener, PhoneStateListener.LISTEN_NONE);
        mListener=null;
        //解除注册
        unregisterReceiver(mInnerSmsReceiver);
        mInnerSmsReceiver=null;
        super.onDestroy();
    }

    //权限 读写联系人权限 三四个
    //子线程睡一会删除,不是根本方法
    //内容观察者
    private void deleteCall(String incomingNumber) {
        ContentResolver resolver = getContentResolver();

        Uri uri = Uri.parse("content://call_log/calls");//// TODO: 2016/12/18 复习
        resolver.delete(uri, "number=?", new String[]{incomingNumber});
    }

    private void deleteCallBefore(final String incomingNuber) {
        //内容观察者
        final ContentResolver resolver = getContentResolver();
        final Uri uri = Uri.parse("content://call_log/calls");
        resolver.registerContentObserver(uri, true, new ContentObserver(new Handler()) {
            @Override
            public void onChange(boolean selfChange) {
                //发生改变就删除
                resolver.delete(uri, "number=?", new String[]{incomingNuber});
                super.onChange(selfChange);
            }
        });
    }

    public void endCall() {
        //权限
        // IBinder binder=ServiceManager.getService(ACCOUNT_SERVICE);//拿不到服务管理器
        try {
            Class<?> clzz = CallSmsService.this.getClassLoader().loadClass("android.os.ServiceManager");
            Method method = clzz.getDeclaredMethod("getService", String.class);
            IBinder binder = (IBinder) method.invoke(null, TELEPHONY_SERVICE);

            ITelephony iTelephony = ITelephony.Stub.asInterface(binder);
            iTelephony.endCall();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
    //监听短信到来的广播接收者
    // Action      android.provider.Telephony.SMS_RECEIVED
    private  class InnerSmsReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.v(TAG, "InnerSmsReceiver.onReceive:::收到短信");
            //
            Bundle extras = intent.getExtras();
            Object[] objs = (Object[]) extras.get("pdus");//Object---->Object[]---->byte[]
            for (Object obj : objs) {
                SmsMessage message = SmsMessage.createFromPdu((byte[]) obj);
                //获取短信号码
                String address = message.getOriginatingAddress();
                //在数据库中查询号码的拦截模式
                String mode = mDao.query(address);
                //如果拦截模式匹配,则终止广播
                if ("1".equals(mode)) {
                    Log.v(TAG, "InnerSmsReceiver.onReceive:::短信拦截");
                    abortBroadcast();
                } else if ("2".equals(mode)) {
                    abortBroadcast();
                    Log.v(TAG, "InnerSmsReceiver.onReceive:::全部拦截");
                }
                //数据库中还有短信内容吗
                //如果程序终止,dao还能执行吗
                // TODO: 2016/12/18 需要测试
                // TODO: 2016/12/18 有时间加一个按 关键字拦截的数据库
                String body = message.getMessageBody();
                if ("fapiao".contains(body)) {
                    Log.v(TAG, "InnerSmReceiver.onReceive:::内容被拦截");
                    abortBroadcast();
                }
            }
        }
    }

    private   class MyPhoneStateListner extends PhoneStateListener {

        @Override
        public void onCallStateChanged(int state, String incomingNumber) {

            switch (state) {
                case TelephonyManager.CALL_STATE_RINGING:
                    Log.v(TAG, "MyListner.onCallStateChanged:::来电: " + incomingNumber);

                    String mode = mDao.query(incomingNumber);
                    if ("0".equals(mode) || "2".equals(mode)) {
                        // mTelephonyManager.endCall();//调不到,被隐藏
                        //挂断电话----需要权限::android.permission.CALL_PHONE
                        endCall();
                        //删除电话----需要权限::android.permission.READ_CALL_LOG    android.permission.WRITE_CALL_LOG
                        deleteCall(incomingNumber);
                    }
                    break;
                case TelephonyManager.CALL_STATE_IDLE:
                    Log.v(TAG, "MyListner.onCallStateChanged:::空闲: " + incomingNumber);

                    break;
                case TelephonyManager.CALL_STATE_OFFHOOK:
                    Log.v(TAG, "MyListner.onCallStateChanged:::挂断: " + incomingNumber);

                    break;
            }

        }
    }
}
