package com.m520it.mobilesafe.fragment;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.View;

import com.m520it.mobilesafe.activity.LostGuideActivity;
import com.m520it.mobilesafe.cons.Constant;
import com.m520it.mobilesafe.view.CirleView;

/**
 * Created by 520 on 2016/12/16.
 */

public abstract class BaseFragment extends Fragment {

    protected static final String TAG = "-->mobile";
    protected Handler mHandler;
    protected LostGuideActivity mLostGuideActivity;

    protected CirleView mGuideCv;

    protected View mView;

    //在activity的attach中调用,多个fragment用接口或者继承来实现
    public void setHandler(Handler handler) {
        mHandler = handler;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.v(Constant.TAG, "BaseFragment.onAttach:::");
        // mLostGuideActivity = (LostGuideActivity) context;
        mLostGuideActivity = (LostGuideActivity) getActivity();
        mHandler = mLostGuideActivity.getHandler();
    }

    protected void sendMessageToActivity(int msgWhat, Object msgObj) {
        if (mHandler != null) {
            Message message = Message.obtain();
            message.what = msgWhat;
            message.obj = msgObj;
            mHandler.sendMessage(message);
        } else {
            Log.v(TAG, "BaseFragment.sendMessageToActivity:::发送消息失败!");
        }
    }

    public void updateCircleView(int position) {
        if (mGuideCv != null) {
            mGuideCv.updateEcho(position);
        }
    }
}
