package com.m520it.mobilesafe.activity;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.m520it.mobilesafe.R;
import com.m520it.mobilesafe.baseadapter.MyBaseAdapter;
import com.m520it.mobilesafe.bean.HomeItemBean;
import com.m520it.mobilesafe.cons.Constant;
import com.m520it.mobilesafe.utils.IntentUtil;
import com.m520it.mobilesafe.utils.SpUtil;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

import static com.m520it.mobilesafe.R.drawable.a;
import static com.m520it.mobilesafe.R.drawable.b;

public class HomeActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    @Bind(R.id.home_logo_iv)
    ImageView mHomeLogoIv;
    @Bind(R.id.home_setting_tv)
    ImageView mHomeSettingTv;
    @Bind(R.id.home_item_gv)
    GridView mHomeItemGv;
    private TextView mPsdDialogTitleTv;
    private EditText mPsdDialogEt1;
    private EditText mPsdDialogEt2;
    private Button mPsdDialogOkBtn;
    private Button mPsdDialogCancelBtn;
    private AlertDialog.Builder mBuilder;
    private AlertDialog mAlertDialog;
    private View mDialogView;
    private String[] names = {
            "手机防盗", "通讯卫士",
            "软件管家", "进程管理",
            "流量统计", "病毒查杀",
            "缓存清理", "高级工具"};
    private String[] desc = {
            "手机丢失好找", "防骚扰监听",
            "方便管理软件", "保持手机通畅",
            "注意流量超标", "手机安全保障",
            "手机快步如飞", "特性处理"};
    private int[] icons = {
            a, b,
            R.drawable.c, R.drawable.d,
            R.drawable.e, R.drawable.f,
            R.drawable.j, R.drawable.h};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ButterKnife.bind(this);

        initAnimation();
        initData();
        initEvent();
    }

    private void initEvent() {
        mHomeSettingTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, SettingActivity.class);
                startActivity(intent);
            }
        });

        mHomeItemGv.setOnItemClickListener(this);
    }

    private void initAnimation() {
        //设置logo的动画
        ObjectAnimator rotationY = ObjectAnimator.ofFloat(mHomeLogoIv, "RotationY", 0, 360, 0);
        rotationY.setRepeatMode(ObjectAnimator.REVERSE);
        rotationY.setRepeatCount(ObjectAnimator.INFINITE);
        rotationY.setDuration(2000);
        rotationY.start();
    }

    private void initData() {
        ArrayList<HomeItemBean> homeItemBeens = new ArrayList<>();

        for (int i = 0; i < names.length; i++) {
            HomeItemBean homeItemBean = new HomeItemBean(desc[i], icons[i], names[i]);
            homeItemBeens.add(homeItemBean);
        }
        mHomeItemGv.setAdapter(new GridViewAdapter(homeItemBeens));
        // mHomeItemGv.deferNotifyDataSetChanged();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        switch (position) {
            case 0:
                if (SpUtil.getString(Constant.PASSWORD) == null) {
                    //没有设置过密码,弹设置密码对话框,否则弹输入密码对话框
                    showSetPasswordDialog();
                } else {
                    showInputPasswordDialog();
                }
                break;
            case 1:
                startActivity(BlackNumberActivity.class);
                break;
            case 2:
                startActivity(AppManagerActivity.class);
                break;
            case 3:
                startActivity(ProcessManagerActivity.class);
                break;
            case 5:
                startActivity(VirusActivity.class);
                break;
            case 6:
                startActivity(ClearCacheActivity.class);
                break;
            case 7:
                startActivity(AdvanceToolsActivity.class);
                break;
            default:
                break;
        }
    }

    private void isStartLostGuideActivity() {
        if (!SpUtil.getBoolean(Constant.FINISH_SETUP)) {
            //密码成功后,从未设置向导则进入向导界面
            startActivity(LostGuideActivity.class);
        } else {
            //密码成功后,设置过向导则进入防盗界面
            startActivity(LostFoundActivity.class);
        }
    }

    private void showInputPasswordDialog() {
        initPasswordDialog();

        mPsdDialogTitleTv.setText("请输入密码");
        mPsdDialogEt2.setVisibility(View.GONE);

        //test用需要删除
        mPsdDialogEt1.setText("1");

        mPsdDialogOkBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String psd1 = mPsdDialogEt1.getText().toString();
                if (TextUtils.isEmpty(psd1)) {
                    Toast.makeText(HomeActivity.this, "密码不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!SpUtil.getString(Constant.PASSWORD).equals(psd1.trim())) {
                    Toast.makeText(HomeActivity.this, "密码错误,请重新输入", Toast.LENGTH_SHORT).show();
                    return;
                }
                //密码成功后销毁对话框
                mAlertDialog.dismiss();
                isStartLostGuideActivity();
            }
        });
        //创建对话框,显示对话框
        mAlertDialog = mBuilder.create();
        mAlertDialog.show();
    }

    private void showSetPasswordDialog() {
        initPasswordDialog();
        mPsdDialogTitleTv.setText("请设置密码");
        mPsdDialogOkBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String psd1 = mPsdDialogEt1.getText().toString();
                String psd2 = mPsdDialogEt2.getText().toString();

                if (TextUtils.isEmpty(psd1) || TextUtils.isEmpty(psd2)) {
                    Toast.makeText(HomeActivity.this, "密码不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!psd1.trim().equals(psd2.trim())) {
                    Toast.makeText(HomeActivity.this, "前后输入的密码不一致,请重新输入", Toast.LENGTH_SHORT).show();
                    return;
                }
                Toast.makeText(HomeActivity.this, "密码设置成功", Toast.LENGTH_SHORT).show();
                //将密码保存起来
                SpUtil.putString(Constant.PASSWORD, psd1.trim());
                //点击取消键销毁对话框
                mAlertDialog.dismiss();
                isStartLostGuideActivity();
            }
        });
        //创建对话框,显示对话框
        mAlertDialog = mBuilder.create();
        mAlertDialog.show();
    }

    //初始化对话框
    private void initPasswordDialog() {
        mBuilder = new AlertDialog.Builder(this);
        mDialogView = View.inflate(this, R.layout.dialog_password_layout, null);

        mPsdDialogEt1 = (EditText) mDialogView.findViewById(R.id.psd_dialog_et1);
        mPsdDialogEt2 = (EditText) mDialogView.findViewById(R.id.psd_dialog_et2);

        mPsdDialogTitleTv = (TextView) mDialogView.findViewById(R.id.psd_dialog_title_tv);

        mPsdDialogOkBtn = (Button) mDialogView.findViewById(R.id.psd_dialog_ok_btn);
        mPsdDialogCancelBtn = (Button) mDialogView.findViewById(R.id.psd_dialog_cancel_btn);

        mPsdDialogCancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //点击取消键销毁对话框
                mAlertDialog.dismiss();
            }
        });
        mBuilder.setView(mDialogView);
    }

    //从本界面到别的界面
    public void startActivity(Class<?> cls) {
        IntentUtil.startActivity(HomeActivity.this, cls);
    }

    private class GridViewAdapter extends MyBaseAdapter<HomeItemBean> {

        public GridViewAdapter(Context context) {
            super();
        }

        GridViewAdapter(ArrayList<HomeItemBean> datas) {
            super(datas);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if (convertView == null) {

                convertView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.gv_item_layout_home_activity, parent, false);
                holder = new ViewHolder();

                holder.iconIv = (ImageView) convertView.findViewById(R.id.item_icon_iv);
                holder.nameTv = (TextView) convertView.findViewById(R.id.item_name_tv);
                holder.descTv = (TextView) convertView.findViewById(R.id.item_desc_tv);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            HomeItemBean bean = mDatas.get(position);
            holder.iconIv.setImageResource(bean.getIcons());
            holder.nameTv.setText(bean.getNames());
            holder.descTv.setText(bean.getDesc());
            return convertView;
        }

        class ViewHolder {

            ImageView iconIv;
            TextView nameTv;
            TextView descTv;
        }
    }
}
