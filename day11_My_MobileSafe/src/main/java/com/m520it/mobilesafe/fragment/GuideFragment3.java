package com.m520it.mobilesafe.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.m520it.mobilesafe.R;
import com.m520it.mobilesafe.activity.ContactSelectActivity;
import com.m520it.mobilesafe.activity.LostGuideActivity;
import com.m520it.mobilesafe.cons.Constant;
import com.m520it.mobilesafe.utils.IntentUtil;
import com.m520it.mobilesafe.utils.SpUtil;
import com.m520it.mobilesafe.view.CirleView;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * A simple {@link Fragment} subclass.
 */
public class GuideFragment3 extends BaseFragment {

    @Bind(R.id.guide_contact_et)
    EditText mGuideContactEt;
    @Bind(R.id.guide_contact_save_btn)
    Button mGuideContactSaveBtn;
    @Bind(R.id.guide_contact_select_btn)
    Button mGuideContactSelectBtn;

    @Bind(R.id.guide_pre_iv)
    ImageView mGuidePreIv;
    @Bind(R.id.guide_next_iv)
    ImageView mGuideNextIv;
    @Bind(R.id.guide_safe_number_tv)
    TextView mGuideSafeNumberTv;

    public GuideFragment3() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

        Log.v(TAG, "GuideFragment3.onCreateView:::");

        mView = inflater.inflate(R.layout.fragment_guide_fragment3, container, false);
        mGuideCv= (CirleView) mView.findViewById(R.id.guide_cv);

        ButterKnife.bind(this, mView);
        updateEditEcho();
        return mView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @OnClick({R.id.guide_contact_et,
            R.id.guide_contact_save_btn,
            R.id.guide_contact_select_btn,
            R.id.guide_cv,
            R.id.guide_pre_iv,
            R.id.guide_next_iv})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.guide_contact_et:
                break;
            case R.id.guide_contact_save_btn:
                updateSafeNumber(mGuideContactEt.getText().toString());
                break;
            case R.id.guide_contact_select_btn:
                startActivityForResult(ContactSelectActivity.class);
                break;
            case R.id.guide_cv:
                break;
            case R.id.guide_pre_iv:
                sendMessageToActivity(LostGuideActivity.PRE_PAGE, null);
                break;
            case R.id.guide_next_iv:

                if (TextUtils.isEmpty(SpUtil.getString(Constant.SAFE_NUMBER))) {
                    Toast.makeText(mLostGuideActivity, "请设置安全号码后再往下一页", Toast.LENGTH_SHORT).show();
                    return;
                }
                sendMessageToActivity(LostGuideActivity.NEXT_PAGE, null);
                break;
        }
    }

    //从本界面到别的界面
    public void startActivityForResult(Class<?> cls) {
        IntentUtil.startActivityForResult(mLostGuideActivity, cls, mLostGuideActivity.CONTACT_SELECT_REQUEST_CODE);
    }

    public void updateSafeNumber(String value) {
        SpUtil.putString(Constant.SAFE_NUMBER, value);
        updateEditEcho();
        Toast.makeText(mLostGuideActivity, " 安全号码设置成功", Toast.LENGTH_SHORT).show();
    }

    public void updateEditEcho() {

        String safeNumber = SpUtil.getString(Constant.SAFE_NUMBER);
        updateEditEcho(safeNumber);
        if (safeNumber != null) {
            mGuideSafeNumberTv.setText("安全号码: " + safeNumber);
        } else {
            mGuideSafeNumberTv.setText("安全号码: ");
        }
    }

    public void updateEditEcho(String text) {
        mGuideContactEt.setText(text);
    }
}
