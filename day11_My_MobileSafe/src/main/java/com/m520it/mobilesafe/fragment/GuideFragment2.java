package com.m520it.mobilesafe.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.m520it.mobilesafe.R;
import com.m520it.mobilesafe.activity.LostGuideActivity;
import com.m520it.mobilesafe.cons.Constant;
import com.m520it.mobilesafe.utils.SpUtil;
import com.m520it.mobilesafe.view.CirleView;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * A simple {@link Fragment} subclass.
 */
public class GuideFragment2 extends BaseFragment {

    @Bind(R.id.guide_bind_tb)
    ToggleButton mGuideBindTb;
    @Bind(R.id.guide_pre_iv)
    ImageView mGuidePreIv;
    @Bind(R.id.guide_next_iv)
    ImageView mGuideNextIv;



    public GuideFragment2() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Log.v(TAG, "GuideFragment2.onCreateView:::");

        mView = inflater.inflate(R.layout.fragment_guide_fragment2, container, false);
        mGuideCv= (CirleView) mView.findViewById(R.id.guide_cv);

        ButterKnife.bind(this, mView);
        updateToggleButtonCheckedValue();
        initEvent();
        return mView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @OnClick({R.id.guide_bind_tb, R.id.guide_cv, R.id.guide_pre_iv, R.id.guide_next_iv})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.guide_bind_tb:
                break;
            case R.id.guide_cv:
                break;
            case R.id.guide_pre_iv:
                sendMessageToActivity(LostGuideActivity.PRE_PAGE, null);
                break;
            case R.id.guide_next_iv:

                if (TextUtils.isEmpty(SpUtil.getString(Constant.SIM_NUMBER))) {
                    Toast.makeText(mLostGuideActivity, "请绑定号码后再往下一页", Toast.LENGTH_SHORT).show();
                    return;
                }
                sendMessageToActivity(LostGuideActivity.NEXT_PAGE, null);
                break;
        }
    }

    //保证ToggleButton值与SharedPreferences一致
    private void updateToggleButtonCheckedValue() {
        mGuideBindTb.setChecked(!TextUtils.isEmpty(SpUtil.getString(Constant.SIM_NUMBER)));
    }

    private void initEvent() {

        mGuideBindTb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    SpUtil.putString(Constant.SIM_NUMBER, getSimSerialNumber());
                    Log.v(TAG, "GuideFragment2.onCheckedChanged:::SimSerialNumber: " + getSimSerialNumber());
                    Toast.makeText(mLostGuideActivity, "绑定成功", Toast.LENGTH_SHORT).show();
                } else {
                    SpUtil.remove(Constant.SIM_NUMBER);
                    Toast.makeText(mLostGuideActivity, "解绑成功", Toast.LENGTH_SHORT).show();
                }
                updateToggleButtonCheckedValue();
            }
        });
    }
    //获取sim号码
    public String getSimSerialNumber() {
        TelephonyManager tm = (TelephonyManager) mLostGuideActivity.getSystemService(Context.TELEPHONY_SERVICE);
        // int i = checkSelfPermission(mLostGuideActivity, Context.TELEPHONY_SERVICE);
        // ActivityCompat.requestPermissions( Context.TELEPHONY_SERVICE);

        // GCMRegistrar.CheckDevice(mLostGuideActivity);
        return tm.getSimSerialNumber();//23会有问题
    }
}
