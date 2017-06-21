package com.m520it.www.myapplication;

import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.graphics.drawable.AnimationDrawable;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

/**
 * Created by xmg on 2016/12/20.
 */

public class ShowLocationService extends Service {

    private WindowManager mMWM;
    private ImageView mIv;
    private WindowManager.LayoutParams mParams;
    private Handler handler = new Handler(){
        public void handleMessage(Message msg){
            mMWM.updateViewLayout(mIv,mParams);
        }
    };



    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        mIv = new ImageView(this);
        mIv.setBackgroundResource(R.drawable.function_greenbutton_normal);

        AnimationDrawable rocketAnimation = (AnimationDrawable) mIv.getBackground();
          rocketAnimation.start();
        mMWM = (WindowManager) getSystemService(WINDOW_SERVICE);
        mParams = new WindowManager.LayoutParams();

        this.mParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        this.mParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        this.mParams.format = PixelFormat.TRANSLUCENT;
        this.mParams.type = WindowManager.LayoutParams.TYPE_PRIORITY_PHONE;
        this.mParams.gravity= Gravity.TOP+Gravity.LEFT;
        this.mParams.flags = WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
               ;
        mMWM.addView(mIv, mParams);
        mIv.setOnTouchListener(new View.OnTouchListener() {

            private float mStartY;
            private float mStartX;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        // 1
                        mStartX = event.getRawX();
                        mStartY = event.getRawY();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        //2
                        float newX = event.getRawX();
                        float newY = event.getRawY();
                        //3
                        int dx= (int) (newX-mStartX+.5f);
                        int dy= (int) (newY-mStartY+.5f);
                        // 4
                        mParams.x+=dx;
                        mParams.y+=dy;
                        //5
                        mStartX = event.getRawX();
                        mStartY = event.getRawY();
                        // 通知更新
                        mMWM.updateViewLayout(mIv,mParams);

                        break;
                    case MotionEvent.ACTION_UP:
                        if(mParams.y>250) {
                          new Thread(){
                              public void run(){
                                  for (int i=0;i<20;i++){
                                      mParams.y-=i*15;
                                      SystemClock.sleep(50);
                                     handler.sendEmptyMessage(0);
                                  }
                              }
                          }.start();

                        }

                        break;
                }
                return true;
            }
        });

        super.onCreate();
    }

    @Override
    public void onDestroy() {
        mMWM.removeView(mIv);
        super.onDestroy();
    }
}
