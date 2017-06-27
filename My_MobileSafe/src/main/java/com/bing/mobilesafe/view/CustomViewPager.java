package com.bing.mobilesafe.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * @author 蓝兵
 * @time 2016/12/15  19:32
 */
public class CustomViewPager extends android.support.v4.view.ViewPager {

    //默认可以滑动
    private boolean isCanScroll = true;
    private CanScrollListner mCanScrollListner;

    public CustomViewPager(Context context) {
        super(context);
    }

    public CustomViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    // public void setScanScroll(boolean isCanScroll) {
    //     this.isCanScroll = isCanScroll;
    // }
    // @Override
    // public boolean onInterceptTouchEvent(MotionEvent ev) {
    //     if(isCanScroll){
    //         return super.onInterceptTouchEvent(ev);
    //     }else{
    //         //false  不能左右滑动
    //         return false;
    //     }
    // }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        /* return false;//super.onTouchEvent(arg0); */

        if (mCanScrollListner == null || mCanScrollListner.onScrollBefore(event)) {
            return super.onTouchEvent(event);//可以左右滑动
        } else {
            return false;// false 或者 true 都不能滑动
        }

        // if (isCanScroll) {
        //     return super.onTouchEvent(arg0);//可以左右滑动
        // } else {
        //     return false;//不能左右滑动
        // }
    }

    public void setOnScanScrollListner(CanScrollListner canScrollListner) {
        mCanScrollListner = canScrollListner;
    }

    public interface CanScrollListner {

        boolean onScrollBefore(MotionEvent event);
    }
}
