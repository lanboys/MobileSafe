package com.m520it.mobilesafe.activity;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.m520it.mobilesafe.R;
import com.m520it.mobilesafe.baseadapter.MyBaseAdapter;
import com.m520it.mobilesafe.db.dao.AppLockInfoDao;
import com.m520it.mobilesafe.utils.AppUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AppLockActivity extends AppCompatActivity {

    @Bind(R.id.lv_app_lock)
    ListView mLvAppLock;
    @Bind(R.id.tb_app_unlock)
    ToggleButton mTbAppUnlock;
    @Bind(R.id.tb_app_lock)
    ToggleButton mTbAppLock;
    @Bind(R.id.tv_app_lock_count)
    TextView mTvAppLockCount;
    @Bind(R.id.tv_appmanager_content)
    TextView mTvAppmanagerContent;
    private AppLockInfoDao mDao;
    private List<LockAppInfoBean> mSystemLockApp;
    private List<LockAppInfoBean> mSystemUnLockApp;
    private List<LockAppInfoBean> mUserLockApp;
    private List<LockAppInfoBean> mUserUnLockApp;

    private boolean mLockFlags = true;
    private List<AppUtil.AppInfoBean> mAllAppInfo;
    private AppLockUnLockAdapter mLockAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_lock);
        ButterKnife.bind(this);
        initView();
        initData();
        initListener();
    }

    private void initView() {
        updataToggleButton();
    }

    private void initListener() {

        mLvAppLock.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

                // if (userApp && lock) {
                //     mUserLockApp.add(new LockAppInfoBean(appIcon, appName, true, true));
                // }
                // if (userApp && !lock) {
                //     mUserUnLockApp.add(new LockAppInfoBean(appIcon, appName, false, true));
                // }
                // if (!userApp && lock) {
                //     mSystemLockApp.add(new LockAppInfoBean(appIcon, appName, true, false));
                // }
                // if (!userApp && !lock) {
                //     mSystemUnLockApp.add(new LockAppInfoBean(appIcon, appName, false, false));
                // }

                if (!mLockFlags) {
                    if (mUserUnLockApp != null && mSystemUnLockApp != null) {
                        if (firstVisibleItem > mUserUnLockApp.size()) {
                            //显示的系统软件
                            mTvAppmanagerContent.setText("系统程序: " + mSystemUnLockApp.size() + "个");
                        } else {
                            //用户软件的显示
                            mTvAppmanagerContent.setText("用户程序: " + mUserUnLockApp.size() + "个");
                        }
                    }
                } else {
                    if (mUserLockApp != null && mSystemLockApp != null) {
                        if (firstVisibleItem > mUserLockApp.size()) {
                            //显示的系统软件
                            mTvAppmanagerContent.setText("系统程序: " + mSystemLockApp.size() + "个");
                        } else {
                            //用户软件的显示
                            mTvAppmanagerContent.setText("用户程序: " + mUserLockApp.size() + "个");
                        }
                    }
                }
            }
        });
    }

    private void initData() {
        mDao = AppLockInfoDao.getInstance();

        new Thread(new Runnable() {
            @Override
            public void run() {

                mSystemLockApp = new ArrayList<>();
                mSystemUnLockApp = new ArrayList<>();
                mUserLockApp = new ArrayList<>();
                mUserUnLockApp = new ArrayList<>();

                mAllAppInfo = AppUtil.getAllAppInfo(AppLockActivity.this);

                for (AppUtil.AppInfoBean appInfoBean : mAllAppInfo) {

                    Drawable appIcon = appInfoBean.getAppIcon();
                    String appName = appInfoBean.getAppName();
                    String appPackageName = appInfoBean.getAppPackageName();
                    boolean userApp = appInfoBean.isUserApp();
                    boolean lock = mDao.query(appPackageName);

                    if (userApp && lock) {
                        mUserLockApp.add(new LockAppInfoBean(appIcon, appName, appPackageName, true, true));
                    }
                    if (userApp && !lock) {
                        mUserUnLockApp.add(new LockAppInfoBean(appIcon, appName, appPackageName, false, true));
                    }
                    if (!userApp && lock) {
                        mSystemLockApp.add(new LockAppInfoBean(appIcon, appName, appPackageName, true, false));
                    }
                    if (!userApp && !lock) {
                        mSystemUnLockApp.add(new LockAppInfoBean(appIcon, appName, appPackageName, false, false));
                    }
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mLockAdapter = new AppLockUnLockAdapter();
                        mLvAppLock.setAdapter(mLockAdapter);
                    }
                });
            }
        }).start();
    }

    @OnClick({R.id.tb_app_unlock, R.id.tb_app_lock})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tb_app_unlock:
                unLock();
                break;
            case R.id.tb_app_lock:
                lock();
                break;
        }
    }

    private void lock() {
        mLockFlags = true;
        updataToggleButton();
        mLockAdapter.notifyDataSetChanged();
    }

    private void unLock() {
        mLockFlags = false;
        updataToggleButton();
        mLockAdapter.notifyDataSetChanged();
    }

    private void updataToggleButton() {
        mTbAppUnlock.setChecked(!mLockFlags);
        mTbAppLock.setChecked(mLockFlags);
    }

    class AppLockUnLockAdapter extends MyBaseAdapter<LockAppInfoBean> {

        @Override
        public int getCount() {

            if (mAllAppInfo == null) {
                return 0;
            }

            int count = 0;

            if (mLockFlags) {
                count = mSystemLockApp.size() + mUserLockApp.size();
                mTvAppLockCount.setText("已加锁软件:" + count + "个");
            } else {
                count = mSystemUnLockApp.size() + mUserUnLockApp.size();
                mTvAppLockCount.setText("未加锁软件:" + count + "个");
            }
            return count + 2;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final LockAppInfoBean lockAppInfoBean;
            if (mLockFlags) {
                if (position == 0 || position == mUserLockApp.size() + 1) {
                    convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.lv_item_app_manager_layout2, parent, false);
                    TextView tv = (TextView) convertView.findViewById(R.id.tv_appmanager_content);
                    String text = position == 0 ? "用户程序: " + mUserLockApp.size() + "个" : "系统程序: " + mSystemLockApp.size() + "个";
                    tv.setText(text);
                    return convertView;
                } else if (position <= mUserLockApp.size()) {
                    position = position - 1;
                    lockAppInfoBean = mUserLockApp.get(position);
                } else {
                    position = position - 1 - 1 - mUserLockApp.size();
                    lockAppInfoBean = mSystemLockApp.get(position);
                }
            } else {
                if (position == 0 || position == mUserUnLockApp.size() + 1) {
                    convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.lv_item_app_manager_layout2, parent, false);
                    TextView tv = (TextView) convertView.findViewById(R.id.tv_appmanager_content);
                    String text = position == 0 ? "用户程序: " + mUserUnLockApp.size() + "个" : "系统程序: " + mSystemUnLockApp.size() + "个";
                    tv.setText(text);
                    return convertView;
                } else if (position <= mUserUnLockApp.size()) {
                    position = position - 1;
                    lockAppInfoBean = mUserUnLockApp.get(position);
                } else {
                    position = position - 1 - 1 - mUserUnLockApp.size();
                    lockAppInfoBean = mSystemUnLockApp.get(position);
                }
            }

            ViewHolder holder = null;
            if (convertView != null && convertView.getTag() != null) {
                holder = (ViewHolder) convertView.getTag();
            } else {
                convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.lv_item_app_lock_layout, parent, false);
                holder = new ViewHolder(convertView);
                convertView.setTag(holder);
            }

            holder.mIvAppIconItem.setImageDrawable(lockAppInfoBean.getAppIcon());
            holder.mTvAppNameItem.setText(lockAppInfoBean.getAppName());
            holder.mIvAppLockItem.setImageResource(lockAppInfoBean.isLock() ? R.drawable.list_button_unlock_default : R.drawable.list_button_lock_default);

            final View finalConvertView = convertView;
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //默认从左往右滑动
                    float toXValue = 1.0f;
                    //如果是在加锁界面,更改为从右往左
                    if (mLockFlags) {
                        toXValue = -1.0f;
                    }
                    TranslateAnimation animation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_PARENT, toXValue,
                            Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0);
                    animation.setDuration(500);
                    finalConvertView.startAnimation(animation);
                    animation.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {

                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {

                            boolean userApp = lockAppInfoBean.isUserApp();
                            boolean lock = lockAppInfoBean.isLock();
                            if (userApp && lock) {
                                mUserUnLockApp.add(lockAppInfoBean);
                                mUserLockApp.remove(lockAppInfoBean);
                            }
                            if (userApp && !lock) {
                                mUserLockApp.add(lockAppInfoBean);
                                mUserUnLockApp.remove(lockAppInfoBean);
                            }
                            if (!userApp && lock) {
                                mSystemUnLockApp.add(lockAppInfoBean);
                                mSystemLockApp.remove(lockAppInfoBean);
                            }
                            if (!userApp && !lock) {
                                mSystemLockApp.add(lockAppInfoBean);
                                mSystemUnLockApp.remove(lockAppInfoBean);
                            }

                            if (mLockFlags) {
                                //已经加锁,一点就解锁,并从数据库删除
                                mDao.delete(lockAppInfoBean.getAppPackagename());
                                lockAppInfoBean.setLock(false);
                            } else {
                                mDao.insert(lockAppInfoBean.getAppPackagename());
                                lockAppInfoBean.setLock(true);
                            }

                            notifyDataSetChanged();
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {

                        }
                    });
                }
            });

            return convertView;
        }

        class ViewHolder {

            @Bind(R.id.iv_app_icon_item)
            ImageView mIvAppIconItem;
            @Bind(R.id.tv_app_name_item)
            TextView mTvAppNameItem;
            @Bind(R.id.iv_app_lock_item)
            ImageView mIvAppLockItem;

            ViewHolder(View view) {
                ButterKnife.bind(this, view);
            }
        }
    }

    class LockAppInfoBean {

        private Drawable appIcon;
        private String appName;
        private String appPackagename;
        private boolean isUserApp;
        private boolean isLock;

        LockAppInfoBean(Drawable appIcon, String appName, String appPackagename, boolean isLock, boolean isUserApp) {
            this.appIcon = appIcon;
            this.appName = appName;
            this.appPackagename = appPackagename;
            this.isLock = isLock;
            this.isUserApp = isUserApp;
        }

        public String getAppPackagename() {
            return appPackagename;
        }

        public void setAppPackagename(String appPackagename) {
            this.appPackagename = appPackagename;
        }

        public Drawable getAppIcon() {
            return appIcon;
        }

        public void setAppIcon(Drawable appIcon) {
            this.appIcon = appIcon;
        }

        public String getAppName() {
            return appName;
        }

        public void setAppName(String appName) {
            this.appName = appName;
        }

        public boolean isLock() {
            return isLock;
        }

        public void setLock(boolean lock) {
            isLock = lock;
        }

        public boolean isUserApp() {
            return isUserApp;
        }

        public void setUserApp(boolean userApp) {
            isUserApp = userApp;
        }
    }
}
