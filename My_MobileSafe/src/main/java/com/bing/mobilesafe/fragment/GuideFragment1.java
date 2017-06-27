package com.bing.mobilesafe.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.bing.mobilesafe.R;
import com.bing.mobilesafe.activity.LostGuideActivity;
import com.bing.mobilesafe.cons.Constant;
import com.bing.mobilesafe.view.CirleView;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * A simple {@link Fragment} subclass.
 */
public class GuideFragment1 extends BaseFragment {

    @Bind(R.id.guide_pre_iv)
    Button mGuidePreIv;
    @Bind(R.id.guide_next_iv)
    ImageView mGuideNextIv;

    public GuideFragment1() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Log.v(TAG, "GuideFragment1.onCreateView:::");

        mView = inflater.inflate(R.layout.fragment_guide_fragment1, container, false);
        mGuideCv= (CirleView) mView.findViewById(R.id.guide_cv);

        ButterKnife.bind(this, mView);
        return mView;
    }

    @Override
    public void onDestroyView() {
        Log.v(Constant.TAG, "GuideFragment1.onDestroyView:::");
        
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @OnClick({R.id.guide_cv, R.id.guide_pre_iv, R.id.guide_next_iv})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.guide_cv:
                break;
            case R.id.guide_pre_iv:
                sendMessageToActivity(LostGuideActivity.PRE_PAGE, null);
                break;
            case R.id.guide_next_iv:
                sendMessageToActivity(LostGuideActivity.NEXT_PAGE, null);
                break;
        }
    }
}
