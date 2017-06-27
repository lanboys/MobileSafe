package com.bing.mobilesafe.activity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.bing.mobilesafe.R;
import com.bing.mobilesafe.cons.Constant;
import com.bing.mobilesafe.service.CallSmsService;
import com.bing.mobilesafe.service.ShowAttributionService;
import com.bing.mobilesafe.service.WatchDogService;
import com.bing.mobilesafe.utils.IntentUtil;
import com.bing.mobilesafe.utils.ServiceUtil;
import com.bing.mobilesafe.utils.SpUtil;
import com.bing.mobilesafe.view.MyRelativeLayout;
import com.bing.mobilesafe.view.SettingItemLayout;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.bing.mobilesafe.R.id.setting_clear_data_btn;
import static com.bing.mobilesafe.cons.Constant.STYLE_NAMES;
import static com.bing.mobilesafe.utils.SpUtil.getAllKeyArray;

public class SettingActivity extends AppCompatActivity {

    @Bind(R.id.setting_update)
    SettingItemLayout mSettingUpdate;
    @Bind(R.id.setting_black_block)
    SettingItemLayout mSettingBlackBlock;
    @Bind(R.id.setting_caller_loc)
    SettingItemLayout mSettingCallerLoc;
    @Bind(R.id.setting_app_lock)
    SettingItemLayout mSettingAppLock;
    @Bind(R.id.activity_setting)
    LinearLayout mActivitySetting;
    @Bind(setting_clear_data_btn)
    Button mSettingClearDataBtn;

    @Bind(R.id.setting_style_select)
    MyRelativeLayout mSettingStyleSelect;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        ButterKnife.bind(this);

        initView();
        initEvent();
    }

    private void initEvent() {
        //自动更新按钮的监听事件
        mSettingUpdate.setOnToggleButtonCheckedChangeListner(new SettingItemLayout.OnToggleButtonCheckedChangeListner() {
            @Override
            public void onToggleButtonCheckedChange(CompoundButton buttonView, boolean isChecked) {
                //不要设置点击布局时也更改 SharedPreferences 的值,保证此处是唯一的与 SharedPreferences 交互的地方
                SpUtil.putBoolean(Constant.AUTO_UPDATA_CHECK, isChecked);
                //有可能 SharedPreferences 保存时发生异常未保存成功,但toggleButton发生改变,所以toggleButton的checked值要
                // 从 SharedPreferences 中再次获取并更新一次,不然有可能 值 不一致
                updateUpdateToggleButton();
            }
        });

        //黑名单拦截按钮的监听事件
        mSettingBlackBlock.setOnToggleButtonCheckedChangeListner(new SettingItemLayout.OnToggleButtonCheckedChangeListner() {
            @Override
            public void onToggleButtonCheckedChange(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    IntentUtil.startService(SettingActivity.this, CallSmsService.class);
                } else {
                    IntentUtil.stopService(SettingActivity.this, CallSmsService.class);
                }
                // 保存到sharePrefrence中会有bug,比如服务被手动关闭,但是界面仍然显示开启状态
                // SpUtil.putBoolean(Constant.INTERFAC, isChecked);
                //更新按钮值
                updateBlackBlockToggleButton();
            }
        });

        //号码归属地显示按钮的监听事件
        mSettingCallerLoc.setOnToggleButtonCheckedChangeListner(new SettingItemLayout.OnToggleButtonCheckedChangeListner() {
            @Override
            public void onToggleButtonCheckedChange(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    IntentUtil.startService(SettingActivity.this, ShowAttributionService.class);
                } else {
                    IntentUtil.stopService(SettingActivity.this, ShowAttributionService.class);
                }
                //更新按钮值
                updateCallerLocToggleButton();
            }
        });
        //程序锁按钮的监听事件
        mSettingAppLock.setOnToggleButtonCheckedChangeListner(new SettingItemLayout.OnToggleButtonCheckedChangeListner() {
            @Override
            public void onToggleButtonCheckedChange(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    IntentUtil.startService(SettingActivity.this, WatchDogService.class);
                } else {
                    IntentUtil.stopService(SettingActivity.this, WatchDogService.class);
                }
                //更新按钮值
                updateAppLockToggleButton();
            }
        });
    }

    private void initView() {
        //自动更新设置
        updateUpdateToggleButton();
        //黑名单拦截设置
        updateBlackBlockToggleButton();
        //程序锁显示
        updateAppLockToggleButton();
        //归属地风格
        updateStyleSelect();
    }

    //保证ToggleButton值与SharedPreferences一致
    private void updateUpdateToggleButton() {
        mSettingUpdate.setToggleButtonChecked(SpUtil.getBoolean(Constant.AUTO_UPDATA_CHECK));
    }

    //黑名单拦截
    private void updateBlackBlockToggleButton() {
        boolean serviceRunning = ServiceUtil.isServiceRunning(getApplicationContext(), CallSmsService.class.getName());
        mSettingBlackBlock.setToggleButtonChecked(serviceRunning);
    }

    //号码归属地显示
    private void updateCallerLocToggleButton() {
        boolean serviceRunning = ServiceUtil.isServiceRunning(getApplicationContext(), ShowAttributionService.class.getName());
        mSettingCallerLoc.setToggleButtonChecked(serviceRunning);
    }

    //程序锁显示
    private void updateAppLockToggleButton() {
        boolean serviceRunning = ServiceUtil.isServiceRunning(getApplicationContext(), WatchDogService.class.getName());
        mSettingAppLock.setToggleButtonChecked(serviceRunning);
    }

    //归属地风格选择
    private void updateStyleSelect() {
        mSettingStyleSelect.setSmallTitle(STYLE_NAMES[SpUtil.getInt(Constant.STYLE)]);
        mSettingStyleSelect.setBackgroundResource(Constant.STYLE_RESID[SpUtil.getInt(Constant.STYLE)]);
    }

    @Override
    protected void onStart() {
        //activity可见的时候就更新一次黑名单拦截按钮显示,防止服务被手动关闭
        updateBlackBlockToggleButton();
        updateCallerLocToggleButton();
        updateAppLockToggleButton();
        updateStyleSelect();

        super.onStart();
    }

    @OnClick({R.id.setting_clear_data_btn, R.id.setting_style_select})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.setting_clear_data_btn:
                clearData();
                break;
            case R.id.setting_style_select:
                showSytleDialog();
                break;
        }
    }

    private void showSytleDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(SettingActivity.this);
        builder.setTitle("归属地框提示风格");

        builder.setSingleChoiceItems(STYLE_NAMES, SpUtil.getInt(Constant.STYLE), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                SpUtil.putInt(Constant.STYLE, which);
                updateStyleSelect();
                dialog.dismiss();
            }
        });

        builder.show();
    }

    private void clearData() {
        String[] allKeyArray = getAllKeyArray();

        //用集合,remove方法,????有空看看
        for (String s : allKeyArray) {
            //不清除防盗界面的密码
            if (Constant.PASSWORD.equals(s)) {
                continue;
            }
            SpUtil.remove(s);
        }

        updateUpdateToggleButton();
        Toast.makeText(this, " 数据清除成功", Toast.LENGTH_SHORT).show();
    }
}
