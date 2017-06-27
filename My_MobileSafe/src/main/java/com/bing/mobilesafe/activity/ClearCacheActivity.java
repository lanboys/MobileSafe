package com.bing.mobilesafe.activity;

import android.content.Context;
import android.content.pm.IPackageDataObserver;
import android.content.pm.IPackageStatsObserver;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageStats;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.RemoteException;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.text.format.Formatter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bing.mobilesafe.R;
import com.bing.mobilesafe.baseadapter.MyBaseAdapter;
import com.bing.mobilesafe.cons.Constant;
import com.bing.mobilesafe.utils.AppUtil;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ClearCacheActivity extends AppCompatActivity {

    @Bind(R.id.btn_clear_all_cache)
    Button mBtnClearAllCache;
    @Bind(R.id.pb_clear_cache)
    ProgressBar mPbClearCache;
    @Bind(R.id.tv_clear_cache)
    TextView mTvClearCache;
    @Bind(R.id.fl_clear_cache_pb_container)
    FrameLayout mFlClearCachePbContainer;
    @Bind(R.id.btn_clear_scanf)
    Button mBtnClearScanf;
    @Bind(R.id.lv_clear_cache)
    ListView mLvClearCache;
    @Bind(R.id.activity_clear_cache)
    LinearLayout mActivityClearCache;
    private PackageManager mPackageManager;
    private List<AppInfoBean> mAppInfoBeens;
    private MyAdapter mAdapter;
    private int mMax;
    private int mProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clear_cache);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        mAdapter = new MyAdapter(ClearCacheActivity.this);
        mLvClearCache.setAdapter(mAdapter);
        mAppInfoBeens = new ArrayList<>();
        mAdapter.setDatas(mAppInfoBeens);
    }

    @OnClick({R.id.btn_clear_all_cache, R.id.btn_clear_scanf})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_clear_all_cache:
                clearAll();
                break;
            case R.id.btn_clear_scanf:
                scanf();
                break;
        }
    }

    private void clearAll() {
        //向系统申请内存
        try {
            Method method = PackageManager.class.getDeclaredMethod("freeStorageAndNotify");
            method.invoke(mPackageManager, Long.MAX_VALUE, new MyIPackageDataObserver());
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        new Thread(new Runnable() {
            @Override
            public void run() {

                while (mAppInfoBeens.size() > 0) {

                    SystemClock.sleep(300);//需要比下面的时间大
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            mLvClearCache.setSelection(0);//没有此句导致动画错乱

                            TranslateAnimation animation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_PARENT, 1.0f,
                                    Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0);
                            animation.setDuration(200);//需要比上面的时间小
                            View view = mAdapter.getFirstItemView();

                            view.startAnimation(animation);

                            animation.setAnimationListener(new Animation.AnimationListener() {
                                @Override
                                public void onAnimationStart(Animation animation) {

                                }

                                @Override
                                public void onAnimationEnd(Animation animation) {

                                    mAppInfoBeens.remove(0);
                                    mAdapter.notifyDataSetChanged();
                                }

                                @Override
                                public void onAnimationRepeat(Animation animation) {

                                }
                            });
                        }
                    });
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(ClearCacheActivity.this, "清理成功", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }).start();
    }

    private void scanf() {
        mAppInfoBeens.clear();
        mAdapter.notifyDataSetChanged();

        new Thread(new Runnable() {
            @Override
            public void run() {
                mPackageManager = getPackageManager();

                List<PackageInfo> packageInfos = mPackageManager.getInstalledPackages(0);
                mMax = packageInfos.size();
                mPbClearCache.setMax(mMax);

                for (PackageInfo info : packageInfos) {
                    //模拟真实扫描
                    SystemClock.sleep(100);

                    String packageName = info.packageName;
                    try {
                        Method method = PackageManager.class.getDeclaredMethod("getPackageSizeInfo", String.class, IPackageStatsObserver.class);
                        method.invoke(mPackageManager, packageName, new MyPackageStateOberver());
                    } catch (InvocationTargetException e) {
                        Log.e(Constant.TAG, "ClearCacheActivity.run:::" + e.getLocalizedMessage());
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                        Log.e(Constant.TAG, "ClearCacheActivity.run:::" + e.getLocalizedMessage());
                    } catch (NoSuchMethodException e) {
                        e.printStackTrace();
                        Log.e(Constant.TAG, "ClearCacheActivity.run:::" + e.getLocalizedMessage());
                    }
                }
            }
        }).start();
    }

    class MyPackageStateOberver extends IPackageStatsObserver.Stub {

        @Override
        public void onGetStatsCompleted(PackageStats pStats, boolean succeeded) throws RemoteException {
            try {
                PackageInfo packageInfo = mPackageManager.getPackageInfo(pStats.packageName, 0);

                final String appName = (String) packageInfo.applicationInfo.loadLabel(mPackageManager);

                long cacheSize = pStats.cacheSize;
                if (cacheSize > 0) {

                AppInfoBean appInfoBean = new AppInfoBean();
                appInfoBean.appPackageName = packageInfo.packageName;
                appInfoBean.appName = appName;
                appInfoBean.appCacheSize = cacheSize;
                appInfoBean.appIcon = packageInfo.applicationInfo.loadIcon(mPackageManager);
                mAppInfoBeens.add(0, appInfoBean);
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mTvClearCache.setText("正在扫描: " + appName + "...");

                        mAdapter.notifyDataSetChanged();

                        mProgress++;
                        mPbClearCache.setProgress(mProgress);
                        if (mProgress == mMax) {

                            mTvClearCache.setText("扫描完成");
                            mProgress = 0;
                        }
                    }
                });
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    class MyIPackageDataObserver extends IPackageDataObserver.Stub {

        //只要一点击删除就会回调这个方法,如果删除成功就会放回true
        @Override
        public void onRemoveCompleted(String packageName, boolean succeeded) throws RemoteException {
            if (succeeded) {
                Toast.makeText(getApplicationContext(), "删除成功", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getApplicationContext(), "删除失败", Toast.LENGTH_SHORT).show();
            }
        }
    }

    class MyAdapter extends MyBaseAdapter<AppInfoBean> {

        private View view;

        public MyAdapter(Context context) {
            super(context);
        }

        View getFirstItemView() {
            return view;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if (convertView != null) {
                holder = (ViewHolder) convertView.getTag();
            } else {
                convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.lv_item_clear_cache, parent, false);
                holder = new ViewHolder(convertView);
                convertView.setTag(holder);
            }

            final AppInfoBean appInfoBean = mDatas.get(position);

            holder.mIvClearAppIconItem.setImageDrawable(appInfoBean.appIcon);
            holder.mTvClearAppNameItem.setText(appInfoBean.appName);
            holder.mTvClearCacheSizeItem.setText(Formatter.formatFileSize(mContext, appInfoBean.appCacheSize));
            holder.mIvClearCacheCleanItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // try {
                    //     PackageManager pm = getPackageManager();
                    //     //实际上获取不到权限,删除不了
                    //     Method method = PackageManager.class.getDeclaredMethod("deleteApplicationCacheFiles", String.class, IPackageDataObserver.class);
                    //     method.invoke(pm, appInfoBean.appPackageName, new MyIPackageDataObserver());
                    // } catch (NoSuchMethodException e) {
                    //     e.printStackTrace();
                    //     Log.e(Constant.TAG, "MyAdapter.onClick:::" + e.getLocalizedMessage());
                    // } catch (InvocationTargetException e) {
                    //     e.printStackTrace();
                    //     Log.e(Constant.TAG, "MyAdapter.onClick:::" + e.getLocalizedMessage());
                    // } catch (IllegalAccessException e) {
                    //     e.printStackTrace();
                    //     Log.e(Constant.TAG, "MyAdapter.onClick:::" + e.getLocalizedMessage());
                    // }
                    //只能启动系统界面进行删除
                    AppUtil.detailApp(ClearCacheActivity.this, appInfoBean.appPackageName);
                }
            });

            if (position == 0) {
                view = convertView;
            }

            return convertView;
        }

        class ViewHolder {

            @Bind(R.id.iv_clear_app_icon_item)
            ImageView mIvClearAppIconItem;

            @Bind(R.id.tv_clear_app_name_item)
            TextView mTvClearAppNameItem;
            @Bind(R.id.tv_clear_cache_size_item)
            TextView mTvClearCacheSizeItem;
            @Bind(R.id.iv_clear_cache_clean_item)
            ImageView mIvClearCacheCleanItem;

            ViewHolder(View view) {
                ButterKnife.bind(this, view);
            }
        }
    }

    class AppInfoBean {

        public Drawable appIcon;
        public String appName;
        public String appPackageName;
        public long appCacheSize;
    }
}
