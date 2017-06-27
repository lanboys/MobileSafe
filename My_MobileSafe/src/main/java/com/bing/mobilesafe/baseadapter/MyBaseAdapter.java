package com.bing.mobilesafe.baseadapter;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.BaseAdapter;

import java.util.List;

// import com.m520it.mobilesafe.activity.AppManagerActivity;

/**
 * Created by 520 on 2016/12/5.
 */

public abstract class MyBaseAdapter<T> extends BaseAdapter {

    private static final String TAG = "-->mobile";

    private final Object mLock = new Object();
    protected List<T> mDatas;
    protected Context mContext;
    private LayoutInflater mInflater;

    //需要改进构造器
    public MyBaseAdapter() {
    }

    public MyBaseAdapter(Context context) {
        this(context, null);
    }

    //需要改进构造器
    public MyBaseAdapter(List<T> datas) {
        this.mDatas = datas;
    }

    public MyBaseAdapter(Context context, List<T> datas) {
        this.mContext = context;
        this.mDatas = datas;
        mInflater = LayoutInflater.from(mContext);
    }

    public boolean insert(@Nullable T object, int index) {
        synchronized (mLock) {
            if (mDatas != null) {
                mDatas.add(index, object);
                notifyDataSetChanged();
                return true;
            } else {
                Log.w(TAG, "MyBaseAdapter.insert:::添加数据失败");
                return false;
            }
        }
    }

    public List<T> getDatas() {
        return mDatas;
    }

    public void setDatas(List<T> mDatas) {
        this.mDatas = mDatas;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mDatas == null ? 0 : mDatas.size();
    }

    @Override
    public T getItem(int position) {
        return mDatas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    // @Override
    // public View getView(int position, View convertView, ViewGroup parent) {
    //     ViewHolder holder = null;
    //     if (convertView != null) {
    //         holder = (ViewHolder) convertView.getTag();
    //     } else {
    //         convertView = mInflater.inflate(R.layout.lv_item_xxx, parent, false);
    //         holder = new ViewHolder(convertView);
    //         convertView.setTag(holder);
    //     }
    //     initViewHolderDatas(holder, position);
    //     return convertView;
    // }
    //
    // private void initViewHolderDatas(ViewHolder holder, int position) {
    //     //初始化数据
    // }
}

