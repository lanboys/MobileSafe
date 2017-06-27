package com.bing.mobilesafe.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.bing.mobilesafe.R;

/**
 * Created by 520 on 2016/12/13.
 */

public class SettingItemLayout extends LinearLayout {

    public static final int TOGGLE_BUTTON_CLICK = 0X0001;
    public static final int TOGGLE_BUTTON_CHANGE = 0X0002;
    public static final int CONTAINER_LL = 0X0003;
    private static final String TAG = "-->mobile";
    private TextView mTv;
    private ToggleButton mToggleButton;
    private LinearLayout mContainerll;
    private String mTextOn;
    private String mTextOff;
    private OnToggleButtonCheckedChangeListner mCheckedChangeListner;

    public SettingItemLayout(Context context) {
        super(context);
    }

    public SettingItemLayout(Context context, AttributeSet attrs) {
        super(context, attrs);

        initView();
        initListener();
        initData(context, attrs);
    }

    private void initView() {
        //一定要注意后面两个参数
        View view = LayoutInflater.from(getContext()).inflate(R.layout.view_custom_toggle_text, this, true);

        mTv = (TextView) view.findViewById(R.id.setting_item_tv);
        mToggleButton = (ToggleButton) view.findViewById(R.id.setting_item_toggleButton);
        //整个自定义控件布局容器
        mContainerll = (LinearLayout) view.findViewById(R.id.setting_item_containerll);
    }

    private void initData(Context context, AttributeSet attrs) {
        /**
         * 要注意自定义控件的自定义属性的命名空间不要搞错了,错了就显示不出来
         */

        //获取安卓命名空间的属性值
        String layout_height = attrs.getAttributeValue("http://schemas.android.com/apk/res/android", "layout_height");
        //获取自定义命名空间的属性值
        String toggle_select = attrs.getAttributeValue("http://schemas.android.com/apk/res-auto", "toggle_select");

        //第二种获取方法
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.SettingItemLayout);

        //设置控件默认的选中状态
        boolean checked = typedArray.getBoolean(R.styleable.SettingItemLayout_toggle_checked, true);
        mToggleButton.setChecked(checked);

        //设置控件默认的文本内容
        mTextOn = typedArray.getString(R.styleable.SettingItemLayout_textOn);
        mTextOff = typedArray.getString(R.styleable.SettingItemLayout_textOff);
        mTv.setText(checked ? mTextOn : mTextOff);

        //设置控件的背景图
        int anInt = typedArray.getInt(R.styleable.SettingItemLayout_backgroundLocation, 2);
        switch (anInt) {
            case 1:
                mContainerll.setBackgroundResource(R.drawable.first_selector);
                break;
            case 0:
                mContainerll.setBackgroundResource(R.drawable.middle_selector);
                break;
            case -1:
                mContainerll.setBackgroundResource(R.drawable.last_selector);
                break;
            default:
                break;
        }

        //资源回收
        typedArray.recycle();
    }

    private void initListener() {
        //设置布局点击事件，点击布局改变toggleButton的checked状态
        mContainerll.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                //对toggleButton进行取反操作
                boolean checked = mToggleButton.isChecked();
                mToggleButton.setChecked(!checked);
            }
        });

        mToggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                //当ToggleButton状态改变时改变文字,并调用下面的方法
                mTv.setText(isChecked ? mTextOn : mTextOff);
                if (mCheckedChangeListner != null) {
                    mCheckedChangeListner.onToggleButtonCheckedChange(buttonView, isChecked);
                }
            }
        });
    }

    //设置ToggleButton状态改变时的监听器
    public void setOnToggleButtonCheckedChangeListner(OnToggleButtonCheckedChangeListner listner) {
        mCheckedChangeListner = listner;
    }

    //给外界暴露更改toggleButton值的方法,不要在外界通过找id来更改(保证封装性)
    public void setToggleButtonChecked(boolean checked) {
        mToggleButton.setChecked(checked);
    }

    //给外界暴露设置监听器的方法
    // public SettingItemLayout setChildViewOnClickListener(int value, OnClickListener listener) {
    //     switch (value) {
    //         case TOGGLE_BUTTON_CLICK:
    //             mToggleButton.setOnClickListener(listener);
    //             break;
    //         case CONTAINER_LL:
    //             mContainerll.setOnClickListener(listener);
    //             break;
    //         default:
    //             //如果value值匹配不上,将发出警告
    //             Log.v(TAG, "SettingItemLayout.setChildViewOnClickListener:::监听器设置出错！！");
    //             break;
    //     }
    //     //链式编程
    //     return this;
    // }

    //给外界暴露设置监听器的方法
    // public SettingItemLayout setChildViewOnClickListener(int value, CompoundButton.OnCheckedChangeListener listener) {
    //     switch (value) {
    //         case TOGGLE_BUTTON_CHANGE:
    //             //设置toggleButton的checked状态改变监听器
    //             mToggleButton.setOnCheckedChangeListener(listener);
    //             break;
    //         default:
    //             //如果value值匹配不上,将发出警告
    //             Log.w(TAG, "SettingItemLayout.setChildViewOnClickListener:: 监听器设置出错！！");
    //             break;
    //     }
    //     return this;
    // }

    public interface OnToggleButtonCheckedChangeListner {

        void onToggleButtonCheckedChange(CompoundButton buttonView, boolean isChecked);
    }

    //枚举可以在switch中使用
    // enum Direction {L, LU, U, RU, R, RD, D, LD, STOP};
    // Direction[] dirs = Direction.values();
}
