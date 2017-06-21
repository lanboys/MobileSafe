package com.m520it.eventtest;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    Button mButton;

    ImageView mImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
        initListener();
    }

    private void initView() {
        mImageView = (ImageView) findViewById(R.id.imageView);
        mButton = (Button) findViewById(R.id.button);
    }

    private void initListener() {

        // mButton.setOnClickListener(this);
        // mImageView.setOnClickListener(this);

        mButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Log.v(Constant.TAG, "MainActivity.onTouch:::----");

                switch (event.getAction()) {
                    case MotionEvent.ACTION_BUTTON_PRESS:
                        Log.v(Constant.TAG, "MainActivity.onTouch:::BUTTON压");

                        break;
                    case MotionEvent.ACTION_UP:
                        Log.v(Constant.TAG, "MainActivity.onTouch:::BUTTON松开");

                        break;
                    case MotionEvent.ACTION_DOWN:
                        Log.v(Constant.TAG, "MainActivity.onTouch:::BUTTON按下");

                        break;
                    case MotionEvent.ACTION_MOVE:
                        Log.v(Constant.TAG, "MainActivity.onTouch:::BUTTON移动");

                        break;
                    case MotionEvent.ACTION_HOVER_MOVE:
                        Log.v(Constant.TAG, "MainActivity.onTouch:::BUTTON光标移动");

                        break;
                    case MotionEvent.ACTION_HOVER_ENTER:
                        Log.v(Constant.TAG, "MainActivity.onTouch:::BUTTON光标进入");

                        break;
                }
                return false;
            }
        });
        mImageView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Log.v(Constant.TAG, "MainActivity.onTouch:::----");

                switch (event.getAction()) {
                    case MotionEvent.ACTION_BUTTON_PRESS:
                        Log.v(Constant.TAG, "MainActivity.onTouch:::Image压");

                        break;
                    case MotionEvent.ACTION_UP:
                        Log.v(Constant.TAG, "MainActivity.onTouch:::Image松开");

                        break;
                    case MotionEvent.ACTION_DOWN:
                        Log.v(Constant.TAG, "MainActivity.onTouch:::Image按下");

                        break;
                    case MotionEvent.ACTION_MOVE:
                        Log.v(Constant.TAG, "MainActivity.onTouch:::Image移动");

                        break;
                    case MotionEvent.ACTION_HOVER_MOVE:
                        Log.v(Constant.TAG, "MainActivity.onTouch:::Image光标移动");

                        break;
                    case MotionEvent.ACTION_HOVER_ENTER:
                        Log.v(Constant.TAG, "MainActivity.onTouch:::Image光标进入");

                        break;
                }
                return false;
            }
        });
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button:

                Log.v(Constant.TAG, "MainActivity.onClick:::" + "button点击事件");

                break;
            case R.id.imageView:
                Log.v(Constant.TAG, "MainActivity.onClick:::" + "imageView点击事件");

                break;
        }
    }
}
