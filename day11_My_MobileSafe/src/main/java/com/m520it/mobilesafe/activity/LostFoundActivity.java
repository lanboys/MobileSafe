package com.m520it.mobilesafe.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.m520it.mobilesafe.R;
import com.m520it.mobilesafe.cons.Constant;
import com.m520it.mobilesafe.utils.IntentUtil;
import com.m520it.mobilesafe.utils.SpUtil;

public class LostFoundActivity extends AppCompatActivity {

    private TextView tv_phone;
    private ImageView iv_lostfind_lock_unlock;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lost_found);

        initView();
        initData();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        initData();
        super.onNewIntent(intent);
    }

    private void initData() {
        String phone = SpUtil.getString(Constant.SAFE_NUMBER);
        tv_phone.setText(TextUtils.isEmpty(phone) ? "" : phone);
        boolean state = SpUtil.getBoolean(Constant.SAFE_STATE);
        iv_lostfind_lock_unlock.setImageResource(state ? R.drawable.lock : R.drawable.unlock);
    }

    private void initView() {
        tv_phone = (TextView) findViewById(R.id.tv_phone);
        iv_lostfind_lock_unlock = (ImageView) findViewById(R.id.iv_lostfind_lock_unlock);
    }

    public void reStartSetup(View view) {
        IntentUtil.startActivity(this, LostGuideActivity.class);
    }
}


