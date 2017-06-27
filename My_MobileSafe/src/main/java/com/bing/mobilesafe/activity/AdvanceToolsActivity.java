package com.bing.mobilesafe.activity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.bing.mobilesafe.R;
import com.bing.mobilesafe.utils.IntentUtil;
import com.bing.mobilesafe.utils.SmsBackupUtil;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AdvanceToolsActivity extends AppCompatActivity {

    @Bind(R.id.btn_advance_query)
    Button mBtnAdvanceQuery;
    @Bind(R.id.btn_advance_common_phone)
    Button mBtnAdvanceCommonPhone;
    @Bind(R.id.btn_advance_sms_backup)
    Button mBtnAdvanceSmsBackup;
    @Bind(R.id.activity_advance_tools)
    LinearLayout mActivityAdvanceTools;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_advance_tools);
        ButterKnife.bind(this);
    }

    @OnClick({R.id.btn_advance_query, R.id.btn_advance_common_phone, R.id.btn_advance_sms_backup,R.id.btn_advance_app_lock})
    public void onClick(View view) {
        switch (view.getId()) {
            //号码归属地查询
            case R.id.btn_advance_query:
                IntentUtil.startActivity(this, NumberQueryActivity.class);
                break;
            //常用号码查询
            case R.id.btn_advance_common_phone:
                IntentUtil.startActivity(this, CommonPhoneActivity.class);
                break;
            //短信备份
            case R.id.btn_advance_sms_backup:
                SmsBackupUtil.smsBackup(this, "backup.xml");
                break;
            //程序锁
            case R.id.btn_advance_app_lock:
                IntentUtil.startActivity(this, AppLockActivity.class);
                break;
        }
    }

    public void smsBackup() {

        final ProgressDialog progressDialog = new ProgressDialog(this, ProgressDialog.STYLE_HORIZONTAL);

        progressDialog.show();
        new Thread(new Runnable() {
            @Override
            public void run() {

                SmsBackupUtil.smsBackup(new SmsBackupUtil.SmsBackup() {
                    @Override
                    public void beforeBackup(int max) {
                        progressDialog.setMax(max);
                    }

                    @Override
                    public void processBackup(int process) {
                        progressDialog.setProgress(process);
                    }

                    @Override
                    public void backupResult(final boolean isSuccess) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (isSuccess) {
                                    Toast.makeText(AdvanceToolsActivity.this, "备份成功", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(AdvanceToolsActivity.this, "备份失败", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                }, AdvanceToolsActivity.this, "smsbackup.xml");
                progressDialog.dismiss();
            }
        }).start();
    }
}
