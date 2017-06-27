package com.bing.mobilesafe.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.text.format.Formatter;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.bing.mobilesafe.R;
import com.bing.mobilesafe.baseadapter.MyBaseAdapter;
import com.bing.mobilesafe.utils.AppUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

import static com.bing.mobilesafe.utils.AppUtil.getAllAppInfo;

public class AppManagerActivity extends AppCompatActivity implements View.OnClickListener {

    @Bind(R.id.tv_appmanager_datasize)
    TextView mTvAppmanagerDatasize;
    @Bind(R.id.tv_appmanager_sdsize)
    TextView mTvAppmanagerSdsize;
    @Bind(R.id.lv_appmanager)
    ListView mLvAppmanager;
    @Bind(R.id.tv_appmanager_content)
    TextView mTvAppmanagerContent;
    private List<AppUtil.AppInfoBean> mSystemAppInfo;
    private List<AppUtil.AppInfoBean> mUserAppInfo;
    private List<AppUtil.AppInfoBean> mAllAppInfo;
    private Handler mHandler = new Handler();
    private AppAdapter mAdapter;
    private PopupWindow mPw;
    private AppUtil.AppInfoBean mAppInfoBean;
    private InnerUninstallReceiver mReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_manager);
        ButterKnife.bind(this);

        mReceiver = new InnerUninstallReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_PACKAGE_REMOVED);
        intentFilter.addDataScheme("package");
        registerReceiver(mReceiver, intentFilter);

        initData();
        initListener();
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(mReceiver);
        mReceiver = null;
        super.onDestroy();
    }

    private void initListener() {
        mLvAppmanager.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                dismissPP();
                if (mSystemAppInfo != null && mUserAppInfo != null) {
                    if (firstVisibleItem > mUserAppInfo.size()) {
                        //显示的系统软件
                        mTvAppmanagerContent.setText("系统程序: " + mSystemAppInfo.size() + "个");
                    } else {
                        //用户软件的显示
                        mTvAppmanagerContent.setText("用户程序: " + mUserAppInfo.size() + "个");
                    }
                }
            }
        });
        mLvAppmanager.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mAppInfoBean = (AppUtil.AppInfoBean) parent.getAdapter().getItem(position);
                if (mAppInfoBean == null) {
                    return;
                }

                dismissPP();

                View contentView = View.inflate(getApplicationContext(), R.layout.popwindow_app_manage, null);
                mPw = new PopupWindow(contentView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                mPw.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                int[] location = new int[2];
                view.getLocationInWindow(location);
                mPw.showAtLocation(view, Gravity.TOP + Gravity.LEFT, 60, location[1]);

                // 透明的渐变动画
                AlphaAnimation aa = new AlphaAnimation(0.0f, 1.0f);
                aa.setDuration(400);
                //缩放动画
                ScaleAnimation sa = new ScaleAnimation(0.0f, 1.0f, 0.0f, 1.0f, Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 0.5f);
                sa.setDuration(400);
                //动画容器
                AnimationSet set = new AnimationSet(false);
                set.addAnimation(aa);
                set.addAnimation(sa);
                //启动动画
                contentView.startAnimation(set);

                contentView.findViewById(R.id.tv_uninstall).setOnClickListener(AppManagerActivity.this);
                contentView.findViewById(R.id.tv_start).setOnClickListener(AppManagerActivity.this);
                contentView.findViewById(R.id.tv_share).setOnClickListener(AppManagerActivity.this);
                contentView.findViewById(R.id.tv_detail).setOnClickListener(AppManagerActivity.this);
            }
        });
    }

    private void dismissPP() {
        if (mPw != null) {
            mPw.dismiss();
            mPw = null;
        }
    }

    private void initData() {
        File dataDirectory = Environment.getDataDirectory();
        long dataFreeSpace = dataDirectory.getFreeSpace();
        mTvAppmanagerDatasize.setText("内存可用: " + Formatter.formatFileSize(this, dataFreeSpace));

        File storageDirectory = Environment.getExternalStorageDirectory();
        long storageFreeSpace = storageDirectory.getFreeSpace();
        mTvAppmanagerSdsize.setText("SD卡可用: " + Formatter.formatFileSize(this, storageFreeSpace));

        new Thread(new Runnable() {
            @Override
            public void run() {

                mUserAppInfo = new ArrayList<AppUtil.AppInfoBean>();
                mSystemAppInfo = new ArrayList<AppUtil.AppInfoBean>();
                mAllAppInfo = getAllAppInfo(AppManagerActivity.this);
                for (AppUtil.AppInfoBean appInfoBean : mAllAppInfo) {
                    if (appInfoBean.isUserApp()) {
                        mUserAppInfo.add(appInfoBean);
                    } else {
                        mSystemAppInfo.add(appInfoBean);
                    }
                }

                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        mAdapter = new AppAdapter(mAllAppInfo, mSystemAppInfo, mUserAppInfo);
                        mLvAppmanager.setAdapter(mAdapter);
                        mAdapter.setDatas(mAllAppInfo);
                    }
                });
            }
        }).start();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_uninstall://卸载
                uninstalllApp();
                break;
            case R.id.tv_start://启动
                startApp();
                break;
            case R.id.tv_share://分享
                shareApp();
                break;
            case R.id.tv_detail://详情
                detailApp();
                break;
        }
        dismissPP();
    }

    private void detailApp() {

        AppUtil.detailApp(this, mAppInfoBean.getAppPackageName());
    }

    private void shareApp() {
        AppUtil.shareApp(this, "赶紧跟我一起来使用吧..");
    }

    private void startApp() {
        AppUtil.startApp(this, mAppInfoBean.getAppPackageName());
    }

    private void uninstalllApp() {
        if (mAppInfoBean.isUserApp()) {
            AppUtil.uninstalllApp(this, mAppInfoBean.getAppPackageName());
        } else {
            Toast.makeText(this, "系统软件root后才能卸载", Toast.LENGTH_SHORT).show();
        }
    }

    class InnerUninstallReceiver extends BroadcastReceiver {

        //只要有apk被卸载这个方法就会被回调
        @Override
        public void onReceive(Context context, Intent intent) {
            if (mAppInfoBean.isUserApp()) {
                mUserAppInfo.remove(mAppInfoBean);
                mAdapter.notifyDataSetChanged();
            }
        }
    }

    class AppAdapter extends MyBaseAdapter<AppUtil.AppInfoBean> {

        private List<AppUtil.AppInfoBean> mSystemAppInfo;
        private List<AppUtil.AppInfoBean> mUserAppInfo;
        private AppUtil.AppInfoBean mAppInfoBean;

        public AppAdapter(List<AppUtil.AppInfoBean> allAppInfo,
                List<AppUtil.AppInfoBean> systemAppInfo,
                List<AppUtil.AppInfoBean> userAppInfo) {
            super(allAppInfo);
            this.mSystemAppInfo = systemAppInfo;
            this.mUserAppInfo = userAppInfo;
        }

        @Override
        public int getCount() {
            return mDatas != null ? mSystemAppInfo.size() + mUserAppInfo.size() + 2 : 0;
        }

        @Override
        public AppUtil.AppInfoBean getItem(int position) {

            if (position == 0 || position == mUserAppInfo.size() + 1) {
                return null;
            } else if (position <= mUserAppInfo.size()) {
                position = position - 1;
                mAppInfoBean = mUserAppInfo.get(position);
            } else {
                position = position - 1 - 1 - mUserAppInfo.size();
                mAppInfoBean = mSystemAppInfo.get(position);
            }
            return mAppInfoBean;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            if (position == 0 || position == mUserAppInfo.size() + 1) {
                convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.lv_item_app_manager_layout2, parent, false);
                TextView tv = (TextView) convertView.findViewById(R.id.tv_appmanager_content);
                String text = position == 0 ? "用户程序: " + mUserAppInfo.size() + "个" : "系统程序: " + mSystemAppInfo.size() + "个";
                tv.setText(text);
                return convertView;
            } else if (position <= mUserAppInfo.size()) {
                position = position - 1;
                mAppInfoBean = mUserAppInfo.get(position);
            } else {
                position = position - 1 - 1 - mUserAppInfo.size();
                mAppInfoBean = mSystemAppInfo.get(position);
            }

            ViewHolder holder = null;
            if (convertView != null && convertView.getTag() != null) {
                holder = (ViewHolder) convertView.getTag();
            } else {
                convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.lv_item_app_manager_layout, parent, false);
                holder = new ViewHolder(convertView);
                convertView.setTag(holder);
            }

            holder.mIvAppIconItem.setImageDrawable(mAppInfoBean.getAppIcon());
            holder.mTvAppNameItem.setText(mAppInfoBean.getAppName());
            holder.mTvAppSizeItem.setText(Formatter.formatFileSize(AppManagerActivity.this, mAppInfoBean.getAppSize()));
            holder.mTvAppLocationItem.setText(mAppInfoBean.isSDApp() ? "SD卡" : "手机内存");

            return convertView;
        }

        class ViewHolder {

            @Bind(R.id.iv_app_icon_item)
            ImageView mIvAppIconItem;
            @Bind(R.id.tv_app_name_item)
            TextView mTvAppNameItem;
            @Bind(R.id.tv_app_location_item)
            TextView mTvAppLocationItem;
            @Bind(R.id.tv_app_size_item)
            TextView mTvAppSizeItem;

            ViewHolder(View view) {
                ButterKnife.bind(this, view);
            }
        }
    }
}
