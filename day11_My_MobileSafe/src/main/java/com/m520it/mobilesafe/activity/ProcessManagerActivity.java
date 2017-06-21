package com.m520it.mobilesafe.activity;

import android.app.ActivityManager;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.text.format.Formatter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.m520it.mobilesafe.R;
import com.m520it.mobilesafe.baseadapter.MyBaseAdapter;
import com.m520it.mobilesafe.cons.Constant;
import com.m520it.mobilesafe.utils.IntentUtil;
import com.m520it.mobilesafe.utils.ProcessUtil;
import com.m520it.mobilesafe.utils.SpUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ProcessManagerActivity extends AppCompatActivity {

    @Bind(R.id.tv_appmanager_datasize)
    TextView mTvAppmanagerDatasize;
    @Bind(R.id.tv_appmanager_sdsize)
    TextView mTvAppmanagerSdsize;
    @Bind(R.id.lv_appmanager)
    ListView mLvAppmanager;
    @Bind(R.id.tv_processmanager_content)
    TextView mTvAppmanagerContent;
    @Bind(R.id.btn_process_manager_all)
    Button mBtnProcessManagerAll;
    @Bind(R.id.btn_process_manager_invert)
    Button mBtnProcessManagerInvert;
    @Bind(R.id.btn_process_manager_kill)
    Button mBtnProcessManagerKill;
    @Bind(R.id.btn_process_manager_set)
    Button mBtnProcessManagerSet;
    private List<ProcessUtil.ProcessInfoBean> mSystemProcessInfo;
    private List<ProcessUtil.ProcessInfoBean> mUserProcessInfo;
    private List<ProcessUtil.ProcessInfoBean> mAllProcessInfo;
    private List<ProcessUtil.ProcessInfoBean> mKillList = new ArrayList<>();
    private Handler mHandler = new Handler();
    private ProcessManagerAdapter mAdapter;

    private int mRunningProcessCount;
    private long mAvailMem;
    private long mTotalMem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_process_manager);
        ButterKnife.bind(this);

        initData();
        initListener();
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (mAdapter != null) {

            mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    protected void onDestroy() {

        super.onDestroy();
    }

    private void initListener() {
        mLvAppmanager.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

                if (mSystemProcessInfo != null && mUserProcessInfo != null) {
                    if (firstVisibleItem > mUserProcessInfo.size()) {
                        //显示的系统软件
                        mTvAppmanagerContent.setText("系统进程: " + mSystemProcessInfo.size() + "个");
                    } else {
                        //用户软件的显示
                        mTvAppmanagerContent.setText("用户进程: " + mUserProcessInfo.size() + "个");
                    }
                }
            }
        });
        // mLvAppmanager.setOnItemClickListener(new AdapterView.OnItemClickListener() {
        //     @Override
        //     public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        //         ProcessUtil.ProcessInfoBean processInfoBean = (ProcessUtil.ProcessInfoBean) parent.getAdapter().getItem(position);
        //         if (processInfoBean == null) {
        //             return;
        //         }
        //
        //         if (processInfoBean.getProcessPackagename().equals(getPackageName())) {
        //             //说明是点击的自己
        //             return;
        //         }
        //
        //         //布局中还要设置获取焦点......
        //         processInfoBean.setCheck(!processInfoBean.isCheck());
        //         mAdapter.notifyDataSetChanged();
        //     }
        // });
    }

    private void initData() {
        mRunningProcessCount = ProcessUtil.getRunningProcessCount(this);
        mTvAppmanagerDatasize.setText("运行中的进程:" + mRunningProcessCount + "个  ");

        mAvailMem = ProcessUtil.getAvailMem(this);
        mTotalMem = ProcessUtil.getTotalMem(this);

        String availMemStr = Formatter.formatFileSize(this, mAvailMem);
        String totallMemStr = Formatter.formatFileSize(this, mTotalMem);

        mTvAppmanagerSdsize.setText("剩余/总内存:" + availMemStr + "/" + totallMemStr);

        new Thread(new Runnable() {
            @Override
            public void run() {

                mUserProcessInfo = new ArrayList<>();
                mSystemProcessInfo = new ArrayList<>();
                mAllProcessInfo = ProcessUtil.getAllProcessInfo(ProcessManagerActivity.this);
                for (ProcessUtil.ProcessInfoBean appInfoBean : mAllProcessInfo) {
                    if (appInfoBean.isUserProcess()) {
                        mUserProcessInfo.add(appInfoBean);
                    } else {
                        mSystemProcessInfo.add(appInfoBean);
                    }
                }

                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        mAdapter = new ProcessManagerAdapter(ProcessManagerActivity.this, mAllProcessInfo, mSystemProcessInfo, mUserProcessInfo);
                        mLvAppmanager.setAdapter(mAdapter);
                        mAdapter.setDatas(mAllProcessInfo);
                    }
                });
            }
        }).start();
    }

    @OnClick({R.id.btn_process_manager_all, R.id.btn_process_manager_invert, R.id.btn_process_manager_kill, R.id.btn_process_manager_set})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_process_manager_all:
                selectAll();
                break;
            case R.id.btn_process_manager_invert:
                invertAll();
                break;
            case R.id.btn_process_manager_kill:
                killAll();
                break;
            case R.id.btn_process_manager_set:
                startSettingActivity();
                break;
        }
    }

    private void startSettingActivity() {

        IntentUtil.startActivity(this, ProcessSettingActivity.class);
    }

    //干掉所有
    private void killAll() {
        long countMem = 0;
        ActivityManager am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        //集合中的元素在遍历的时候,大小是不容许改变
        for (ProcessUtil.ProcessInfoBean info : mUserProcessInfo) {
            if (info.isCheck()) {
                //这个方法只能杀掉别人,不能杀掉自己
                am.killBackgroundProcesses(info.getProcessPackagename());
                mKillList.add(info);
            }
        }

        for (ProcessUtil.ProcessInfoBean info : mSystemProcessInfo) {
            if (info.isCheck()) {
                am.killBackgroundProcesses(info.getProcessPackagename());
                mKillList.add(info);
            }
        }
        for (ProcessUtil.ProcessInfoBean info : mKillList) {

            if (info.isUserProcess()) {
                countMem += info.getProcessSize();
                mUserProcessInfo.remove(info);
            } else {
                mSystemProcessInfo.remove(info);
                countMem += info.getProcessSize();
            }
        }
        mTvAppmanagerDatasize.setText("运行中的进程:" + (mRunningProcessCount - mKillList.size()) + "个");

        String availMemStr = Formatter.formatFileSize(this, (mAvailMem + countMem));
        String totallMemStr = Formatter.formatFileSize(this, mTotalMem);
        mTvAppmanagerSdsize.setText("剩余/总内存:" + availMemStr + "/" + totallMemStr);

        String countMemStr = Formatter.formatFileSize(this, countMem);
        Toast.makeText(ProcessManagerActivity.this, "为您清理了:" + mKillList.size() + "个进程,节约了" + countMemStr + "内存", Toast.LENGTH_SHORT).show();

        mKillList.clear();
        countMemStr = "0";
        mAdapter.notifyDataSetChanged();
    }

    //反选
    private void invertAll() {
        for (ProcessUtil.ProcessInfoBean info : mUserProcessInfo) {
            if (info.getProcessPackagename().equals(getPackageName())) {
                continue;
            }
            info.setCheck(!info.isCheck());
        }

        for (ProcessUtil.ProcessInfoBean info : mSystemProcessInfo) {
            info.setCheck(!info.isCheck());
        }

        mAdapter.notifyDataSetChanged();
    }

    //全选
    private void selectAll() {
        for (ProcessUtil.ProcessInfoBean info : mUserProcessInfo) {
            if (info.getProcessPackagename().equals(getPackageName())) {
                continue;
            }
            info.setCheck(true);
        }

        for (ProcessUtil.ProcessInfoBean info : mSystemProcessInfo) {
            info.setCheck(true);
        }

        mAdapter.notifyDataSetChanged();
    }

    class ProcessManagerAdapter extends MyBaseAdapter<ProcessUtil.ProcessInfoBean>

    {

        private List<ProcessUtil.ProcessInfoBean> mSystemProcessInfo;
        private List<ProcessUtil.ProcessInfoBean> mUserProcessInfo;

        public ProcessManagerAdapter(Context context, List<ProcessUtil.ProcessInfoBean> allAppInfo,
                List<ProcessUtil.ProcessInfoBean> systemAppInfo,
                List<ProcessUtil.ProcessInfoBean> userAppInfo) {
            super(context, allAppInfo);
            this.mSystemProcessInfo = systemAppInfo;
            this.mUserProcessInfo = userAppInfo;
        }

        @Override
        public int getCount() {

            boolean showSysy = SpUtil.getBoolean(Constant.SHOWSYS);
            if (showSysy) {
                return mUserProcessInfo.size() + 1 + mSystemProcessInfo.size() + 1;
            } else {
                return mUserProcessInfo.size() + 1;
            }

            // return mDatas != null ? mDatas.size() : 0;
            // return mDatas != null ? mSystemProcessInfo.size() + mUserProcessInfo.size() + 2 : 0;
        }

        @Override
        public ProcessUtil.ProcessInfoBean getItem(int position) {

            ProcessUtil.ProcessInfoBean processInfoBean;

            if (position == 0 || position == mUserProcessInfo.size() + 1) {
                return null;
            } else if (position <= mUserProcessInfo.size()) {
                position = position - 1;
                processInfoBean = mUserProcessInfo.get(position);
            } else {
                position = position - 1 - 1 - mUserProcessInfo.size();
                processInfoBean = mSystemProcessInfo.get(position);
            }
            return processInfoBean;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final ProcessUtil.ProcessInfoBean processInfoBean;
            if (position == 0 || position == mUserProcessInfo.size() + 1) {
                convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.lv_item_process_manager_layout2, parent, false);
                TextView tv = (TextView) convertView.findViewById(R.id.tv_processmanager_content);
                String text = position == 0 ? "用户进程: " + mUserProcessInfo.size() + "个" : "系统进程: " + mSystemProcessInfo.size() + "个";
                tv.setText(text);
                return convertView;
            } else if (position <= mUserProcessInfo.size()) {
                position = position - 1;
                processInfoBean = mUserProcessInfo.get(position);
            } else {
                position = position - 1 - 1 - mUserProcessInfo.size();
                processInfoBean = mSystemProcessInfo.get(position);
            }

            // processInfoBean = mDatas.get(position);
            ProcessManagerAdapter.ViewHolder holder = null;
            if (convertView != null && convertView.getTag() != null) {
                holder = (ProcessManagerAdapter.ViewHolder) convertView.getTag();
            } else {
                convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.lv_item_process_manager_layout, parent, false);
                holder = new ProcessManagerAdapter.ViewHolder(convertView);
                convertView.setTag(holder);
            }

            //这样设置监听器,会造成不断的new 监听器,有待优化
            holder.mTvAppSizeItem.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                //  1---->0---->onCheckedChanged----->显示
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                    processInfoBean.setCheck(isChecked);
                    // notifyDataSetChanged();//不需要加这句,只是单个item发生改变
                }
            });

            //onCheckedchanged里面的 processInfoBean 不能是adapter的成员变量!!!!!!!!
            //只要把添加监听器的方法加到初始化view中checkBox状态的代码之前,就不会出现数据错乱
            holder.mTvAppSizeItem.setChecked(processInfoBean.isCheck());

            holder.mIvAppIconItem.setImageDrawable(processInfoBean.getProcessIcon());
            holder.mTvAppNameItem.setText(processInfoBean.getProcessName());
            holder.mTvAppLocationItem.setText("占用内存" + Formatter.formatFileSize(mContext.getApplicationContext(), processInfoBean.getProcessSize()));

            final ViewHolder finalHolder = holder;
            holder.mItemContainerll.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    boolean checked = finalHolder.mTvAppSizeItem.isChecked();
                    //对原来的值取反
                    finalHolder.mTvAppSizeItem.setChecked(!checked);

                }
            });

            String packageName = processInfoBean.getProcessPackagename();
            if (packageName.equals(mContext.getPackageName())) {
                holder.mTvAppSizeItem.setVisibility(View.INVISIBLE);
            } else {
                holder.mTvAppSizeItem.setVisibility(View.VISIBLE);//注意添加
            }

            return convertView;
        }

        class ViewHolder {

            @Bind(R.id.iv_app_icon_item)
            ImageView mIvAppIconItem;

            @Bind(R.id.item_containerll)
            LinearLayout mItemContainerll;
            @Bind(R.id.tv_app_name_item)
            TextView mTvAppNameItem;
            @Bind(R.id.tv_app_location_item)
            TextView mTvAppLocationItem;
            @Bind(R.id.tv_app_size_item)
            CheckBox mTvAppSizeItem;

            ViewHolder(View view) {
                ButterKnife.bind(this, view);
            }
        }
    }
}
