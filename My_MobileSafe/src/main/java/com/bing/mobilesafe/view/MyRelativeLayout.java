package com.bing.mobilesafe.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.support.annotation.DrawableRes;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bing.mobilesafe.R;

/**
 * @author 蓝兵
 * @time 2016/12/20  20:06
 */
public class MyRelativeLayout extends RelativeLayout {

    private View mView;
    private TextView mSmallTitle;
    private TextView mBigTitle;
    private Drawable mBackground;
    private RelativeLayout mRoot;

    public MyRelativeLayout(Context context) {
        this(context, null);
    }

    public MyRelativeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
        initData(context, attrs);
    }

    private void initData(Context context, AttributeSet attrs) {

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.MyRelativeLayout);

        String bigTitle = typedArray.getString(R.styleable.MyRelativeLayout_big_title);
        String smallTitle = typedArray.getString(R.styleable.MyRelativeLayout_small_title);
        mBackground = typedArray.getDrawable(R.styleable.MyRelativeLayout_background);

        if (mBackground != null) {
            mRoot.setBackground(mBackground);
        }
        setText(bigTitle, smallTitle);

        typedArray.recycle();
    }

    public void setBackgroundResource(@DrawableRes int resid) {
        mRoot.setBackgroundResource(resid);
    }

    public void setSmallTitle(String smallTitle) {
        mSmallTitle.setText(smallTitle);
    }

    public void setBigTitle(String bigTitle) {
        mBigTitle.setText(bigTitle);
    }

    public void setText(String bigTitle, String smallTitle) {
        mBigTitle.setText(bigTitle);
        mSmallTitle.setText(smallTitle);
    }

    private void initView() {
        mView = LayoutInflater.from(getContext()).inflate(R.layout.view_custom_rl, this, true);

        mRoot = (RelativeLayout) mView.findViewById(R.id.root_custom_rl);
        mBigTitle = (TextView) mView.findViewById(R.id.tv_custom_big_title);
        mSmallTitle = (TextView) mView.findViewById(R.id.tv_custom_small_title);
    }
}
