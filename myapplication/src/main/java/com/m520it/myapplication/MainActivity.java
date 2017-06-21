package com.m520it.myapplication;

import android.content.Context;
import android.graphics.PixelFormat;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;

public class MainActivity extends AppCompatActivity {

    @butterknife.Bind(R.id.iv)
    ImageView mIv;
    @butterknife.Bind(R.id.activity_main)
    RelativeLayout mActivityMain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        butterknife.ButterKnife.bind(this);

        // Toast text = Toast.makeText(this, "我我我我我哦我我", Toast.LENGTH_SHORT);
        // text.show();

        toastTest();
        // initListerner();
    }

    private void initListerner() {
        mIv.setOnTouchListener(new View.OnTouchListener() {
            float startX;
            float startY;

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        startX = event.getRawX();
                        startY = event.getRawY();

                        // mIv.layout(mIv.getLeft()+100,mIv.getTop()+100,mIv.getRight()+100,mIv.getBottom()+100);


                        break;
                    case MotionEvent.ACTION_MOVE:

                        float rawX = event.getRawX();
                        float rawY = event.getRawY();

                        int v1 = (int) (rawX - startX + 0.5f);
                        int v2 = (int) (rawY - startY + 0.5f);

                        // * @param l Left position, relative to parent
                        // * @param t Top position, relative to parent
                        // * @param r Right position, relative to parent
                        // * @param b Bottom position, relative to paren

                        mIv.layout(mIv.getLeft()+v1,mIv.getTop()+v2,mIv.getRight()+v1,mIv.getBottom()+v2);

                        startX = event.getRawX();
                        startY = event.getRawY();
                        break;
                    case MotionEvent.ACTION_UP:

                        break;
                }

                return true;
            }
        });
    }

    private void toastTest() {

        WindowManager.LayoutParams params = new WindowManager.LayoutParams();

        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.width = WindowManager.LayoutParams.WRAP_CONTENT;
        params.format = PixelFormat.TRANSLUCENT;
        params.type = WindowManager.LayoutParams.TYPE_TOAST;
        params.setTitle("Toast");
        params.flags = WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE;

        // TextView textView = new TextView(this);
        // textView.setText("我是一个吐司吐司吐司......");

        ImageView imageView = new ImageView(this);
        imageView.setBackgroundResource(R.drawable.rocket);

        AnimationDrawable background = (AnimationDrawable) imageView.getBackground();
        background.start();

        WindowManager mWM;
        mWM = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        mWM.addView(imageView, params);
    }
}
