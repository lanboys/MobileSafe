package com.bing.piontloop;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by 520 on 2016/12/14.
 */

public class CirleView extends LinearLayout implements View.OnClickListener {

    private View mView;
    private LinearLayout mContainerll;
    private CircleImageView mCircleImageView;
    private ArrayList<CircleImageView> mCircleImageViews = new ArrayList<>();
    private Drawable mD1;
    private Drawable mD2;
    private int mNum;
    private int mPosition;
    private OnChildClickListener mOnChildClickListener;

    public CirleView(Context context) {
        super(context);
    }

    public CirleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
        initData(context, attrs);
    }

    private void initData(Context context, AttributeSet attrs) {

        //第二种获取方法
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.CirleView);
        //圆的个数
        mNum = typedArray.getInt(R.styleable.CirleView_points_num, 1);
        //特殊图片所在的位置
        mPosition = typedArray.getInt(R.styleable.CirleView_points_position, 0);
        //大小
        int size = (int) typedArray.getDimension(R.styleable.CirleView_points_size, 10.0f);
        //图片间隔
        int margin = (int) typedArray.getDimension(R.styleable.CirleView_points_margin, 0.1f);

        mD1 = typedArray.getDrawable(R.styleable.CirleView_points_src1);
        mD2 = typedArray.getDrawable(R.styleable.CirleView_points_src2);

        LayoutParams params = new LayoutParams(size, size);
        params.setMargins(margin, 0, margin, 0);
        Matrix matrix = mCircleImageView.getMatrix();

        mCircleImageView.setLayoutParams(params);
        mCircleImageView.setImageMatrix(matrix);

        int count = mCircleImageViews.size();
        for (int i = 0; i < mNum - count; i++) {
            CircleImageView circleImageView = new CircleImageView(context);

            circleImageView.setLayoutParams(params);
            circleImageView.setImageMatrix(matrix);

            mContainerll.addView(circleImageView);
            mCircleImageViews.add(circleImageView);
        }

        initChildImageDrawable();
    }

    private void initChildImageDrawable() {

        //初始化所有图片
        for (CircleImageView circleImageView : mCircleImageViews) {
            if (mD1 != null) {
                circleImageView.setImageDrawable(mD1);
            } else {
                circleImageView.setImageResource(R.drawable.default_icon);
            }

            circleImageView.setOnClickListener(this);
        }
        //初始话当前页图片
        CircleImageView circleImageView = mCircleImageViews.get(mPosition);
        if (mD2 != null) {
            circleImageView.setImageDrawable(mD2);
        } else {
            circleImageView.setImageResource(R.drawable.default_icon);
        }
    }

    public void updateEcho(int position) {

        //忘记判断了,出错,不提示什么错,导致bug找了很久
        if (position > mCircleImageViews.size()) {
            Log.v(Constant.TAG, "CirleView.change2Second:::图片position大于了图片总数");
            return;
        }
        if (mPosition == position) {
            Log.v(Constant.TAG, "CirleView.change2Second:::没有任何修改");
            return;
        }
        if (mD2 != null) {
            mCircleImageViews.get(position).setImageDrawable(mD2);
        } else {
            mCircleImageViews.get(position).setImageResource(R.drawable.default_icon);
        }

        CircleImageView circleImageView = mCircleImageViews.get(mPosition);
        if (mD1 != null) {
            circleImageView.setImageDrawable(mD1);
        } else {
            circleImageView.setImageResource(R.drawable.default_icon);
        }
        mPosition = position;
    }

    public void initView() {
        //一定要注意后面两个参数
        mView = LayoutInflater.from(getContext()).inflate(R.layout.view_custom_circle_points, this, true);
        mContainerll = (LinearLayout) mView.findViewById(R.id.cicle_points_containerll);

        mCircleImageView = (CircleImageView) mView.findViewById(R.id.circle_civ_first);
        mCircleImageViews.add(mCircleImageView);
    }

    @Override
    public void onClick(View v) {
        // TODO: 2016/12/21 如何区别几张图片
        if (mOnChildClickListener != null) {
            mOnChildClickListener.onChildClick(v);
        }
        Toast.makeText(getContext(), "别点我", Toast.LENGTH_SHORT).show();
    }

    public void setOnChildClickListener(OnChildClickListener onChildClickListener) {
        mOnChildClickListener = onChildClickListener;
    }

    public interface OnChildClickListener {

        void onChildClick(View v);
    }
}
