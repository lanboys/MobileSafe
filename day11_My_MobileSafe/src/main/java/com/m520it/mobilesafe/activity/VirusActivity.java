package com.m520it.mobilesafe.activity;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.m520it.mobilesafe.R;
import com.m520it.mobilesafe.db.dao.VirusDao;

import java.io.File;
import java.io.FileInputStream;
import java.security.MessageDigest;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class VirusActivity extends AppCompatActivity {

    @Bind(R.id.iv_virus_anim)
    ImageView mIvVirusAnim;
    @Bind(R.id.tv_virus)
    TextView mTvVirus;
    @Bind(R.id.pb_virus)
    ProgressBar mPbVirus;
    @Bind(R.id.ll_scaninfo_tv)
    LinearLayout mLlScaninfoTv;
    @Bind(R.id.tv_virus_percent)
    TextView mTvVirusPercent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_virus);
        ButterKnife.bind(this);
        initAnimation();
        scanf();
    }

    private void scanf() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    PackageManager pm = getPackageManager();
                    MessageDigest digest = MessageDigest.getInstance("md5");

                    List<PackageInfo> infos = pm.getInstalledPackages(PackageManager.GET_UNINSTALLED_PACKAGES + PackageManager.GET_SIGNATURES);
                    final int size = infos.size();
                    mPbVirus.setMax(size);
                    int progress = 0;
                    for (PackageInfo info : infos) {
                        progress++;
                        mPbVirus.setProgress(progress);

                        final String name = info.applicationInfo.loadLabel(pm).toString();
                        String sourceDir = info.applicationInfo.sourceDir;

                        File file = new File(sourceDir);
                        FileInputStream inputStream = new FileInputStream(file);

                        byte[] buffer = new byte[1024];
                        int len = -1;
                        while ((len = inputStream.read(buffer)) != -1) {
                            digest.update(buffer, 0, len);
                        }
                        // TODO: 2016/12/27  找时间好好研究加密
                        byte[] bytes = digest.digest();
                        StringBuffer sb = new StringBuffer();

                        for (byte b : bytes) {
                            String s = Integer.toHexString(b & 0xff);
                            if (s.length() == 1) {
                                sb.append("0");
                            }
                            sb.append(s);
                        }
                        final String desc = VirusDao.getDesc(sb.toString());

                        final TextView tv = new TextView(getApplicationContext());
                        final int finalProgress = progress;
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (TextUtils.isEmpty(desc)) {
                                    tv.setText(name + "没有发现病毒");
                                    tv.setTextSize(20);
                                    tv.setTextColor(Color.BLACK);
                                } else {
                                    System.out.println(name + "有病毒----");
                                    tv.setText(name + "有发现病毒");
                                    tv.setTextSize(20);
                                    tv.setTextColor(Color.RED);
                                }
                                mLlScaninfoTv.addView(tv, 0);

                                int i = finalProgress * 100 / size;
                                mTvVirusPercent.setText(i + "%");
                                if (i == 100) {
                                    mTvVirus.setText("查杀完成");
                                }

                            }
                        });

                        SystemClock.sleep(100);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void initAnimation() {
        RotateAnimation ra = new RotateAnimation(0.0f, 360.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        ra.setDuration(2000);
        //设置无限循环
        ra.setRepeatCount(Animation.INFINITE);
        mIvVirusAnim.startAnimation(ra);
        //如何停止????????????
    }
}
