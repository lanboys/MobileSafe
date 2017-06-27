package com.bing.mobilesafe.fragment;

import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;

import com.bing.mobilesafe.R;
import com.bing.mobilesafe.activity.LostGuideActivity;
import com.bing.mobilesafe.cons.Constant;
import com.bing.mobilesafe.receiver.Myadmin;
import com.bing.mobilesafe.utils.SpUtil;
import com.bing.mobilesafe.view.CirleView;
import com.bing.mobilesafe.view.SettingItemLayout;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * A simple {@link Fragment} subclass.
 */
public class GuideFragment4 extends BaseFragment {

    @Bind(R.id.setting_black_block)
    SettingItemLayout mSettingBlackBlock;

    @Bind(R.id.guide_pre_iv)
    ImageView mGuidePreIv;
    @Bind(R.id.guide_next_iv)
    Button mGuideNextIv;

    public GuideFragment4() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        Log.v(TAG, "GuideFragment4.onCreateView:::");

        mView = inflater.inflate(R.layout.fragment_guide_fragment4, container, false);
        mGuideCv= (CirleView) mView.findViewById(R.id.guide_cv);

        ButterKnife.bind(this, mView);

        initView();
        initEvent();
        return mView;
    }

    private void initEvent() {
        mSettingBlackBlock.setOnToggleButtonCheckedChangeListner(new SettingItemLayout.OnToggleButtonCheckedChangeListner() {
            @Override
            public void onToggleButtonCheckedChange(CompoundButton buttonView, boolean isChecked) {
                SpUtil.putBoolean(Constant.SAFE_STATE, isChecked);
                updateToggleButtonCheckedValue();
                if (isChecked) {
                    //开启高级管理员权限,激活后,再次开启将无效
                    startDevicePolicyService();
                }
            }
        });
    }

    //开启高级管理员权限
    private void startDevicePolicyService() {
        DevicePolicyManager manager = (DevicePolicyManager) mLostGuideActivity.getSystemService(Context.DEVICE_POLICY_SERVICE);
        ComponentName componentName = new ComponentName(mLostGuideActivity, Myadmin.class);

        Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
        intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, componentName);
        intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION,
                "开启高级管理员权限");
        // startActivity(intent);
        startActivityForResult(intent,LostGuideActivity.DEVICE_POLICY_REQUEST_CODE);
    }

    private void initView() {
        updateToggleButtonCheckedValue();
    }

    //保证ToggleButton值与SharedPreferences一致
    public void updateToggleButtonCheckedValue() {
        mSettingBlackBlock.setToggleButtonChecked(SpUtil.getBoolean(Constant.SAFE_STATE));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @Override
    public void onStart() {
        updateToggleButtonCheckedValue();
        super.onStart();
    }

    @OnClick({R.id.setting_black_block, R.id.guide_cv, R.id.guide_pre_iv, R.id.guide_next_iv})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.guide_cv:
                break;
            case R.id.guide_pre_iv:
                sendMessageToActivity(LostGuideActivity.PRE_PAGE, null);
                break;
            case R.id.guide_next_iv:
                sendMessageToActivity(LostGuideActivity.NEXT_PAGE, null);
                //进入该界面表示设置成功
                SpUtil.putBoolean(Constant.FINISH_SETUP, true);
                break;
        }
    }
}
