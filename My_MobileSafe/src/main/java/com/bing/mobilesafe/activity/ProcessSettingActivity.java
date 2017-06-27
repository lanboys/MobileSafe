package com.bing.mobilesafe.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.CompoundButton;

import com.bing.mobilesafe.R;
import com.bing.mobilesafe.cons.Constant;
import com.bing.mobilesafe.utils.SpUtil;
import com.bing.mobilesafe.view.SettingItemLayout;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ProcessSettingActivity extends AppCompatActivity {

    @Bind(R.id.process_setting)
    SettingItemLayout mProcessSetting;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_process_setting);
        ButterKnife.bind(this);

        updateToggleButtonCheckedValue();

        initEvent();
    }

    private void initEvent() {
        mProcessSetting.setOnToggleButtonCheckedChangeListner(new SettingItemLayout.OnToggleButtonCheckedChangeListner() {
            @Override
            public void onToggleButtonCheckedChange(CompoundButton buttonView, boolean isChecked) {
                SpUtil.putBoolean(Constant.SHOWSYS, isChecked);
                updateToggleButtonCheckedValue();
            }
        });
    }

    //保证ToggleButton值与SharedPreferences一致
    public void updateToggleButtonCheckedValue() {
        mProcessSetting.setToggleButtonChecked(SpUtil.getBoolean(Constant.SHOWSYS));
    }
}