package com.bing.eventtest;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

import butterknife.Bind;
import butterknife.ButterKnife;

public class Main2Activity extends AppCompatActivity {

    @Bind(R.id.button2)
    Button mButton2;
    @Bind(R.id.button3)
    Button mButton3;
    @Bind(R.id.activity_main2)
    MyLayout mActivityMain2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        ButterKnife.bind(this);

        initView();
    }



    private void initView() {
        mActivityMain2.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Log.v(Constant.TAG, "Main2Activity.onTouch:::" + "布局被touch");

                return false;
            }
        });

        mButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v(Constant.TAG, "Main2Activity.onClick:::按钮1被点击了");

            }
        });     mButton3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v(Constant.TAG, "Main2Activity.onClick:::按钮2被点击了");

            }
        });




    }
}
