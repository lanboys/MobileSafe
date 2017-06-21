package com.m520it.mobilesafe.activity;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.m520it.mobilesafe.R;
import com.m520it.mobilesafe.service.WatchDogService;
import com.m520it.mobilesafe.utils.AppUtil;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class WatchDogActivity extends AppCompatActivity {

    public static final String LOCK_APP_PACKAGENAME = "packageName";
    @Bind(R.id.tv_watch_dog_title)
    TextView mTvWatchDogTitle;
    @Bind(R.id.tv_watch_name)
    TextView mTvWatchName;
    @Bind(R.id.iv_watch_dog_icon)
    ImageView mIvWatchDogIcon;
    @Bind(R.id.et_watch_dog_psd)
    EditText mEtWatchDogPsd;
    @Bind(R.id.btn_watch_dog_ok)
    Button mBtnWatchDogOk;
    @Bind(R.id.btn_watch_dog_cancel)
    Button mBtnWatchDogOkCancel;
    private String mPackageName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_watch_dog);
        ButterKnife.bind(this);

        initDatas();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {

        finish();
        super.onStop();
    }

    private void initDatas() {

        try {
            Intent intent = getIntent();
            mPackageName = intent.getStringExtra(LOCK_APP_PACKAGENAME);
            PackageManager packageManager = getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(mPackageName, 0);

            String name = (String) packageInfo.applicationInfo.loadLabel(packageManager);
            Drawable drawable = packageInfo.applicationInfo.loadIcon(packageManager);

            mTvWatchName.setText(name);
            mIvWatchDogIcon.setImageDrawable(drawable);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        //回到桌面
        AppUtil.startLauncher(this);
    }

    @OnClick({R.id.btn_watch_dog_ok, R.id.btn_watch_dog_cancel})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_watch_dog_ok:
                okButton();

                break;
            case R.id.btn_watch_dog_cancel:
                cancelButton();
                break;
        }
    }

    private void cancelButton() {
        AppUtil.startLauncher(this);
    }

    private void okButton() {
        String psd = mEtWatchDogPsd.getText().toString().trim();

        if (TextUtils.isEmpty(psd)) {
            Toast.makeText(WatchDogActivity.this, "请输入密码", Toast.LENGTH_LONG).show();
        } else if ("123".equals(psd)) {
            finish();
            Intent intent = new Intent();
            intent.setAction(WatchDogService.LOCK_ACTION);
            intent.putExtra(LOCK_APP_PACKAGENAME, mPackageName);
            sendBroadcast(intent);
        } else {
            Toast.makeText(WatchDogActivity.this, "密码错误", Toast.LENGTH_SHORT).show();
        }
    }
}
