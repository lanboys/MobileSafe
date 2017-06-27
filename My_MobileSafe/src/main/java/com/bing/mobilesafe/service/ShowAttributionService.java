package com.bing.mobilesafe.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bing.mobilesafe.R;
import com.bing.mobilesafe.cons.Constant;
import com.bing.mobilesafe.db.dao.NumberQueryDao;
import com.bing.mobilesafe.utils.SpUtil;

public class ShowAttributionService extends Service {

    private MyPhoneStateListener mListener;
    private TelephonyManager mTelephonyManager;
    private InnerOutCallReceiver mCallReceiver;
    private WindowManager mWindowManager;
    private View mView;
    private WindowManager.LayoutParams mParams;

    public ShowAttributionService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        Log.v(Constant.TAG, "ShowAttributionService.onCreate:::号码归属地显示服务开启");

        mTelephonyManager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        mListener = new MyPhoneStateListener();
        mTelephonyManager.listen(mListener, PhoneStateListener.LISTEN_CALL_STATE);

        mCallReceiver = new InnerOutCallReceiver();
        IntentFilter intentFilter = new IntentFilter();
        // 短信: android.provider.Telephony.SMS_RECEIVED
        // 外拨电话: android.intent.action.NEW_OUTGOING_CALL
        //注意添加权限  android.permission.PROCESS_OUTGOING_CALLS  ,不添加的话,也不报错,注意!!!!!!
        intentFilter.addAction(Intent.ACTION_NEW_OUTGOING_CALL);
        registerReceiver(mCallReceiver, intentFilter);

        super.onCreate();
    }

    @Override
    public void onDestroy() {
        Log.v(Constant.TAG, "ShowAttributionService.onDestroy:::号码归属地显示服务开启");
        mTelephonyManager.listen(mListener, PhoneStateListener.LISTEN_NONE);
        mListener = null;
        //隐藏自定义吐司
        hideCustomToast();

        unregisterReceiver(mCallReceiver);
        mCallReceiver = null;

        super.onDestroy();
    }

    public void showCustomToast(String location) {

        // Toast.makeText(ShowAttributionService.this, location, Toast.LENGTH_LONG).show();

        mWindowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        mParams = new WindowManager.LayoutParams();

        LayoutInflater inflate = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mView = inflate.inflate(R.layout.view_custom_toast, null);
        TextView tv = (TextView) mView.findViewById(R.id.tv_custom_toast);
        LinearLayout root =  (LinearLayout) mView.findViewById(R.id.root_custom_toast);
        tv.setText(location);
        root.setBackgroundResource(Constant.STYLE_RESID[SpUtil.getInt(Constant.STYLE)]);

        mParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        mParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        mParams.format = PixelFormat.TRANSLUCENT;
        // params.windowAnimations = com.android.internal.R.style.Animation_Toast;
        // mParams.type = WindowManager.LayoutParams.TYPE_TOAST;
        //更改为最高权限显示在窗口上面(需要设置权限)
        mParams.type = WindowManager.LayoutParams.TYPE_PRIORITY_PHONE;
        // params.setTitle("Toast");
        //回显窗体位置
        mParams.x = SpUtil.getInt(Constant.TAOST_PARAMS_X);
        mParams.y = SpUtil.getInt(Constant.TAOST_PARAMS_Y);



        //更改toast的默认居中显示
        mParams.gravity = Gravity.TOP + Gravity.LEFT;
        mParams.flags = WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
                //更改默认的不更触摸设置,foze
                // | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE;

        // mWindowManager.removeView(mView);
        mWindowManager.addView(mView, mParams);

        mView.setOnTouchListener(new View.OnTouchListener() {
            float startX;
            float startY;

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        //1.获取开始坐标
                        startX = event.getRawX();
                        startY = event.getRawY();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        //2.获取移动后的坐标
                        float newX = event.getRawX();
                        float newY = event.getRawY();
                        //3.获取差值
                        int dx = (int) (newX - startX + 0.5f);
                        int dy = (int) (newY - startY + 0.5f);

                        // * @param l Left position, relative to parent
                        // * @param t Top position, relative to parent
                        // * @param r Right position, relative to parent
                        // * @param b Bottom position, relative to paren

                        //4.更新位置
                        // mView.layout();这个方法只在activity中有效果
                        // mView.layout(mView.getLeft()+dx,mView.getTop()+dy,mView.getRight()+dx,mView.getBottom()+dy);
                        mParams.x += dx;
                        mParams.y += dy;
                        //5.计算toast的最大滑动范围
                        int maxX = mWindowManager.getDefaultDisplay().getWidth() - mView.getWidth();
                        int maxY = mWindowManager.getDefaultDisplay().getHeight() - mView.getHeight();

                        if (mParams.x > maxX) {
                            mParams.x = maxX;
                        }
                        if (mParams.y > maxY) {
                            mParams.y = maxY;
                        }
                        if (mParams.x < 0) {
                            mParams.x = 0;
                        }
                        if (mParams.y < 0) {
                            mParams.y = 0;
                        }
                        //6.更新窗体ui
                        mWindowManager.updateViewLayout(mView,mParams);
                        //7.重新初始化开始坐标
                        startX = event.getRawX();
                        startY = event.getRawY();
                        break;
                    case MotionEvent.ACTION_UP:
                        float rawX = event.getRawX();
                        float rawY = event.getRawY();

                        SpUtil.putInt(Constant.TAOST_PARAMS_X, mParams.x);
                        SpUtil.putInt(Constant.TAOST_PARAMS_Y, mParams.y);


                        break;
                }

                return true;
            }
        });
    }

    private void hideCustomToast() {
        if (mWindowManager != null) {
            mWindowManager.removeView(mView);
            mWindowManager = null;
            mView = null;
        }
    }

    //外拨电话
    class InnerOutCallReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            String phone = getResultData();
            String location = NumberQueryDao.findLocation(phone);
            showCustomToast(location);

            // Toast.makeText(ShowAttributionService.this, location, Toast.LENGTH_LONG).show();
            Log.v(Constant.TAG, "InnerOutCallReceiver.onReceive:::" + phone + location);
        }
    }

    //电话打进来
    private class MyPhoneStateListener extends PhoneStateListener {

        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            switch (state) {
                case TelephonyManager.CALL_STATE_IDLE:
                    hideCustomToast();

                    break;
                case TelephonyManager.CALL_STATE_RINGING:
                    String location = NumberQueryDao.findLocation(incomingNumber);
                    showCustomToast(location);
                    // Toast.makeText(ShowAttributionService.this, location, Toast.LENGTH_LONG).show();

                    break;
                case TelephonyManager.CALL_STATE_OFFHOOK:

                    break;
            }
        }
    }
}
