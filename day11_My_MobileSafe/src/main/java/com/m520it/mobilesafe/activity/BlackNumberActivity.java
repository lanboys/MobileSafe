package com.m520it.mobilesafe.activity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.m520it.mobilesafe.R;
import com.m520it.mobilesafe.baseadapter.MyBaseAdapter;
import com.m520it.mobilesafe.bean.BlackNumberBean;
import com.m520it.mobilesafe.db.dao.BlackNumberInfoDao;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class BlackNumberActivity extends AppCompatActivity {

    @Bind(R.id.black_number_lv)
    ListView mBlackNumberLv;
    @Bind(R.id.add_black_number_tv)
    TextView mAddBlackNumberTv;
    @Bind(R.id.activity_black_number)
    LinearLayout mActivityBlackNumber;
    @Bind(R.id.black_number_ll_loadding)
    LinearLayout mBlackNumberLlLoadding;
    private BlackNumberAdapter mAdapter;
    private TextView mPsdDialogTitleTv;
    private EditText mBlackDialogEt;
    private RadioGroup mAddBlackRg;
    private Button mBlackDialogOkBtn;
    private Button mBlackDialogCancelBtn;
    private AlertDialog.Builder mBuilder;
    private AlertDialog mAlertDialog;
    private View mDialogView;
    private BlackNumberInfoDao mDao;
    private TextView mTitleTv;
    private RadioButton mRadioButton3;
    private RadioButton mRadioButton2;
    private RadioButton mRadioButton1;
    private String mOldPhone;
    private ArrayList<BlackNumberBean> mDatas;
    private int mStart = 0;
    private int mCount = 20;
    private int mTotalCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_black_number);
        ButterKnife.bind(this);
        //获取dao
        mDao = BlackNumberInfoDao.getInstance();
        initView();
        initData();
        initListener();
    }

    private void initListener() {

        String oldPhone;
        mBlackNumberLv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {

                BlackNumberBean bean = (BlackNumberBean) parent.getAdapter().getItem(position);
                initDialog();

                mTitleTv.setText("请修改拦截的号码");
                mOldPhone = bean.getPhone();
                mBlackDialogEt.setText(mOldPhone);

                String mode = bean.getMode();

                switch (mode) {
                    case "0":
                        mRadioButton1.setChecked(true);
                        break;
                    case "1":
                        mRadioButton2.setChecked(true);
                        break;
                    case "2":
                        mRadioButton3.setChecked(true);
                        break;
                }

                mBlackDialogOkBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String mode = "0";
                        String newPhone = mBlackDialogEt.getText().toString();
                        int checkedRadioButtonId = mAddBlackRg.getCheckedRadioButtonId();

                        switch (checkedRadioButtonId) {
                            case R.id.add_black_rb1:
                                mode = "0";
                                break;
                            case R.id.add_black_rb2:
                                mode = "1";
                                break;
                            case R.id.add_black_rb3:
                                mode = "2";
                                break;
                        }
                        if (TextUtils.isEmpty(newPhone)) {
                            Toast.makeText(BlackNumberActivity.this, "号码不能为空", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        //向数据库插入数据
                        mDao.update(mOldPhone, newPhone.trim(), mode);
                        //更新内存,及listview显示

                        BlackNumberBean mAdapterItem = mAdapter.getItem(position);
                        mAdapterItem.setPhone(newPhone);
                        mAdapterItem.setMode(mode);
                        mAdapter.notifyDataSetChanged();
                        //点击取消键销毁对话框
                        mAlertDialog.dismiss();
                    }
                });
                //创建对话框,显示对话框
                mBuilder.setView(mDialogView);
                mAlertDialog = mBuilder.create();
                mAlertDialog.show();

                return true;
            }
        });
        //ListView 监听
        mBlackNumberLv.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                switch (scrollState) {
                    case AbsListView.OnScrollListener.SCROLL_STATE_FLING:
                        //滑翔状态

                        break;
                    case AbsListView.OnScrollListener.SCROLL_STATE_IDLE:
                        //滑翔停止
                        int position = mBlackNumberLv.getLastVisiblePosition();
                        int count = mAdapter.getCount() - 1;
                        if (position == count) {
                            // 最后一个位置,加载新的数据到集合中
                            mStart = count + mCount;

                            if (mStart >= mTotalCount) {
                                Toast.makeText(BlackNumberActivity.this, "没有更多数据", Toast.LENGTH_SHORT).show();
                            } else {
                                initData();
                            }
                        }

                        break;
                    case AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL:
                        //手指一直在屏幕上滑动

                        break;
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }
        });
    }

    private void initData() {
        mTotalCount = mDao.getCount();
        mBlackNumberLlLoadding.setVisibility(View.VISIBLE);
        new Thread(new Runnable() {
            @Override
            public void run() {
                //每次加载20条数据
                if (mDatas == null) {
                    mDatas = mDao.queryPart(mStart, mCount);
                } else {
                    mDatas.addAll(mDao.queryPart(mStart, mCount));
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mBlackNumberLlLoadding.setVisibility(View.GONE);
                        mAdapter.setDatas(mDatas);
                        mAdapter.notifyDataSetChanged();
                    }
                });
            }
        }).start();
    }

    private void initView() {
        mAdapter = new BlackNumberAdapter( );
        mBlackNumberLv.setAdapter(mAdapter);
    }

    //初始化对话框
    private void initPasswordDialog() {
        initDialog();

        mBlackDialogOkBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mode = "0";

                String phone = mBlackDialogEt.getText().toString();
                int checkedRadioButtonId = mAddBlackRg.getCheckedRadioButtonId();
                switch (checkedRadioButtonId) {
                    case R.id.add_black_rb1:
                        mode = "0";
                        break;
                    case R.id.add_black_rb2:
                        mode = "1";
                        break;
                    case R.id.add_black_rb3:
                        mode = "2";
                        break;
                }
                if (TextUtils.isEmpty(phone)) {
                    Toast.makeText(BlackNumberActivity.this, "请输入号码", Toast.LENGTH_SHORT).show();
                    return;
                }
                //向数据库插入数据
                mDao.insert(phone.trim(), mode);
                //更新内存,及listview显示
                mAdapter.getDatas().add(0, new BlackNumberBean(mode, phone));
                mAdapter.notifyDataSetChanged();
                //点击取消键销毁对话框
                mAlertDialog.dismiss();
            }
        });
        //创建对话框,显示对话框
        mBuilder.setView(mDialogView);
        mAlertDialog = mBuilder.create();
        mAlertDialog.show();
    }

    private void initDialog() {
        mBuilder = new AlertDialog.Builder(this);
        mDialogView = View.inflate(this, R.layout.dialog_add_black_layout, null);

        mBlackDialogEt = (EditText) mDialogView.findViewById(R.id.add_black_et);

        mAddBlackRg = (RadioGroup) mDialogView.findViewById(R.id.add_black_rg);
        mRadioButton1 = (RadioButton) mDialogView.findViewById(R.id.add_black_rb1);
        mRadioButton2 = (RadioButton) mDialogView.findViewById(R.id.add_black_rb2);
        mRadioButton3 = (RadioButton) mDialogView.findViewById(R.id.add_black_rb3);

        mBlackDialogOkBtn = (Button) mDialogView.findViewById(R.id.add_black_ok_btn);
        mBlackDialogCancelBtn = (Button) mDialogView.findViewById(R.id.add_black_cancel_btn);

        mTitleTv = (TextView) mDialogView.findViewById(R.id.add_black_title_tv);

        mBlackDialogCancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //点击取消键销毁对话框
                mAlertDialog.dismiss();
            }
        });
    }

    @OnClick(R.id.add_black_number_tv)
    public void onClick() {
        initPasswordDialog();
    }

    class BlackNumberAdapter extends MyBaseAdapter<BlackNumberBean> {

        public BlackNumberAdapter( ) {

        }

        public BlackNumberAdapter( ArrayList<BlackNumberBean> datas) {
            super(  datas);
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if (convertView == null) {

                convertView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.lv_item_black_number, parent, false);
                holder = new ViewHolder();

                holder.phoneTv = (TextView) convertView.findViewById(R.id.item_phone_tv);
                holder.modeTv = (TextView) convertView.findViewById(R.id.item_mode_tv);
                holder.deletcIv = (ImageView) convertView.findViewById(R.id.item_delete_iv);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            final BlackNumberBean bean = mDatas.get(position);

            String mode = "";
            switch (bean.getMode()) {
                case "0":
                    mode = "只拦截电话";
                    break;
                case "1":
                    mode = "只拦截短信";
                    break;
                case "2":
                    mode = "拦截电话和短信";
                    break;
            }
            holder.modeTv.setText(mode);

            holder.phoneTv.setText(bean.getPhone());

            holder.deletcIv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    AlertDialog.Builder builder = new AlertDialog.Builder(BlackNumberActivity.this);
                    builder.setTitle("确定要删除这个号码吗").
                            setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    mAlertDialog.dismiss();
                                }
                            }).setPositiveButton("ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            mDao.delete(bean.getPhone());
                            mDatas.remove(position);
                            mAdapter.notifyDataSetChanged();
                            mAlertDialog.dismiss();
                        }
                    });

                    mAlertDialog = builder.show();
                }
            });

            return convertView;
        }

        class ViewHolder {

            ImageView deletcIv;
            TextView phoneTv;
            TextView modeTv;
        }
    }
}
