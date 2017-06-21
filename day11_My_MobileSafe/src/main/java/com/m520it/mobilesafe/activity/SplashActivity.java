package com.m520it.mobilesafe.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.m520it.mobilesafe.R;
import com.m520it.mobilesafe.bean.UpdateInfoBean;
import com.m520it.mobilesafe.bean.VirusUpdateBean;
import com.m520it.mobilesafe.cons.Constant;
import com.m520it.mobilesafe.db.dao.VirusDao;
import com.m520it.mobilesafe.utils.AppUtil;
import com.m520it.mobilesafe.utils.AssetsUtil;
import com.m520it.mobilesafe.utils.RequestUtil;
import com.m520it.mobilesafe.utils.SpUtil;
import com.m520it.mobilesafe.utils.StreamUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import butterknife.Bind;
import butterknife.ButterKnife;

import static com.m520it.mobilesafe.cons.Constant.ASSETS_DB_ADDRESS;
import static com.m520it.mobilesafe.cons.Constant.ASSETS_DB_COMMONNUM;
import static com.m520it.mobilesafe.cons.Constant.ASSETS_DB_VIRUS;

public class SplashActivity extends AppCompatActivity {

    private static final String TAG = "-->mobile";
    private static final int STARTHOME = 0x0001;
    private static final int NEEDUPDATE = 0x0002;
    @Bind(R.id.version_code_tv)
    TextView mVersionCodeTv;
    @Bind(R.id.version_name_tv)
    TextView mVersionNameTv;
    @Bind(R.id.activity_splash_layout)
    RelativeLayout mActivitySplashLayout;
    @Bind(R.id.contain_rl)
    RelativeLayout mContainRl;
    private int mVersionCode;
    private String mVersionName;
    private AnimatorSet mAnimatorSet;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case STARTHOME:
                    startHome();
                    break;
                case NEEDUPDATE:
                    showDialogUpdate(msg);
                    break;
                default:
                    Toast.makeText(SplashActivity.this, "网络异常,检测更新失败(网址错误)", Toast.LENGTH_SHORT).show();
                    startHome();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        ButterKnife.bind(this);

        //模拟用户需要更新的设置
        // SpUtil.putBoolean(getApplicationCxt(), Constant.AUTO_UPDATA_CHECK, true);

        initView();
        initData();
        initAnimation();
        initListener();
        checkVirusDbUpdate();
        createShortCut();
    }

    /**
     * 在桌面生成快捷方式
     */
    private void createShortCut() {

        boolean result = SpUtil.getBoolean(Constant.CREATE_SHORTCUT);
        if (!result) {
            try {
                Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);

                PackageManager packageManager = getPackageManager();
                String packageName = getPackageName();

                Drawable applicationIcon = packageManager.getApplicationIcon(packageName);
                ApplicationInfo applicationInfo = packageManager.getApplicationInfo(packageName, 0);
                Drawable applicationLogo = packageManager.getApplicationLogo(packageName);

                String appName = (String) packageManager.getApplicationLabel(applicationInfo);

                PackageInfo packageInfo = packageManager.getPackageInfo(packageName, 0);
                String appName1 = (String) packageInfo.applicationInfo.loadLabel(packageManager);

                String shortIntentAction = "com.m520it.mobilesafe.activity.HomeActivity";

                AppUtil.createShortCut(this, bitmap, appName, shortIntentAction);

                SpUtil.putBoolean(Constant.CREATE_SHORTCUT, true);
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
                Log.v(Constant.TAG, "SplashActivity.createShortCut:::" + e.getLocalizedMessage());
            }
        }
    }

    private void initListener() {

    }

    private void checkVirusDbUpdate() {
        //网络请求
        RequestUtil.loadStringByRequest(this, Constant.URL.VIRUS_DB_UPDATA_URL, new RequestUtil.CallBackListner<String>() {
            @Override
            public void OnFinish(String response) {
                //json数据解析
                VirusUpdateBean virusUpdateBean = VirusUpdateBean.objectFromData(response);
                //更新数据版本
                VirusDao.updateVersion(virusUpdateBean.version);
                // 将新的病毒数据加入到数据库中
                VirusDao.insertData(virusUpdateBean);
            }

            @Override
            public void OnError(Exception e) {
                Log.e(Constant.TAG, "SplashActivity.OnError:::可能是网址错误::" + e.getLocalizedMessage());
            }
        });
    }

    private void checkAppUpdate() {
        //软件检测更新
        new Thread(new Runnable() {
            @Override
            public void run() {
                long startTime = SystemClock.currentThreadTimeMillis();
                Message message = Message.obtain();

                try {
                    URL url = new URL(Constant.URL.APP_UPDATA_URL);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setConnectTimeout(5 * 1000);

                    int responseCode = connection.getResponseCode();
                    if (responseCode == 200) {
                        InputStream inputStream = connection.getInputStream();
                        String result = StreamUtil.stream2String(inputStream);
                        // UpdateInfoBean updateInfoBean = parseJson(result);
                        UpdateInfoBean updateInfoBean = UpdateInfoBean.objectFromData(result);
                        Log.v(TAG, "SplashActivity.run:::" + updateInfoBean.toString());
                        if (mVersionCode == Integer.valueOf(updateInfoBean.getVersion())) {
                            message.what = STARTHOME;
                        } else {
                            message.what = NEEDUPDATE;
                            message.obj = updateInfoBean;
                        }
                    } else {
                        Log.w(TAG, "SplashActivity.run:::请求失败 responseCode: " + responseCode);
                    }
                } catch (MalformedURLException e) {
                    Log.e(TAG, "SplashActivity.run:::" + e.getLocalizedMessage());
                } catch (IOException e) {
                    Log.e(TAG, "SplashActivity.run:::" + e.getLocalizedMessage());
                } finally {
                    long endTime = SystemClock.currentThreadTimeMillis();
                    long time = endTime - startTime;
                    //保证动画结束后才开始发消息,否则消息可能先处理
                    if (time < 2200) {
                        SystemClock.sleep(2200 - (time));
                    }
                    mHandler.sendMessage(message);
                }
            }
        }).start();
    }

    //弹出对话框的方法,让用户选择是否升级
    private void showDialogUpdate(Message msg) {
        final UpdateInfoBean infoBean = (UpdateInfoBean) msg.obj;
        AlertDialog.Builder builder = new AlertDialog.Builder(SplashActivity.this);
        builder.setTitle("提示");
        builder.setMessage(infoBean.getDesc());
        builder.setPositiveButton("下载", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                downLoadApk(infoBean);
            }
        }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startHome();
            }
        });
        builder.show();
    }

    //下载apk
    private void downLoadApk(final UpdateInfoBean infoBean) {
        HttpUtils httpUtils = new HttpUtils();
        // File file1 = Environment.getExternalStorageDirectory();
        File file2 = getFilesDir();
        final File file = new File(file2, "xx.apk");
        /**参数一 下载地址
         * 参数二 下载后的apk放在那里
         * 参数三 是否单点下载
         * 参数四回调
         *
         */
        httpUtils.download(infoBean.getDownloadurl(), file.getAbsolutePath(), false, new RequestCallBack<File>() {
            //如果下载成功,就会回调该方法
            @Override
            public void onSuccess(ResponseInfo<File> responseInfo) {
                Log.v(TAG, "SplashActivity.onSuccess():::下载成功");
                Toast.makeText(SplashActivity.this, "下载成功,即将安装", Toast.LENGTH_SHORT).show();
                //下载成功后安装apk
                Uri uri = Uri.fromFile(file);
                inStallApk(uri);
            }

            //如果下载失败就会回调该方法
            @Override
            public void onFailure(HttpException e, String s) {
                Log.v(TAG, "SplashActivity.onFailure:::下载失败" + e.getLocalizedMessage());
                Toast.makeText(SplashActivity.this, "下载失败", Toast.LENGTH_SHORT).show();
                startHome();
            }
        });
    }

    //安装apk
    private void inStallApk(Uri uri) {
        //android系统里面要求系统做什么事
        //都是通过意图来表达(Intent)
               /* <action android:name="android.intent.action.VIEW" />
                <category android:name="android.intent.category.DEFAULT" />
                <data android:scheme="content" />
                <data android:scheme="file" />
                <data android:mimeType="application/vnd.android.package-archive" />*/
        Intent intent = new Intent();
        intent.setAction("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.setDataAndType(uri, "application/vnd.android.package-archive");

        // startActivity(intent);//这种方法启动安装界面,安装完成后,将不会自动进入下一个界面,造成假死界面
        startActivityForResult(intent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        startHome();
        Log.v(TAG, "SplashActivity.onActivityResult:::" + requestCode);

        super.onActivityResult(requestCode, resultCode, data);
    }

    //解析json数据
    private UpdateInfoBean parseJson(String string) throws JSONException {
        JSONObject jsonObject = new JSONObject(string);
        UpdateInfoBean infoBean = new UpdateInfoBean();
        infoBean.setVersion(jsonObject.getString("version"));
        infoBean.setDesc(jsonObject.getString("desc"));
        infoBean.setDownloadurl(jsonObject.getString("downloadurl"));
        return infoBean;
    }

    private void initAnimation() {
        //添加进入动画
        ObjectAnimator alpha = ObjectAnimator.ofFloat(mContainRl, "Alpha", 0, 1);
        ObjectAnimator rotation = ObjectAnimator.ofFloat(mContainRl, "Rotation", 0, 360);
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(mContainRl, "ScaleX", 0, 1);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(mContainRl, "ScaleY", 0, 1);

        mAnimatorSet = new AnimatorSet();
        mAnimatorSet.setDuration(2 * 1000);
        mAnimatorSet.playTogether(alpha, rotation, scaleX, scaleY);
        mAnimatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                //获取用户是否更新设置,是,进行检查更新
                if (SpUtil.getBoolean(Constant.AUTO_UPDATA_CHECK)) {
                    //在线检查更新
                    checkAppUpdate();
                }
            }

            @Override
            public void onAnimationEnd(Animator animation) {

                if (!SpUtil.getBoolean(Constant.AUTO_UPDATA_CHECK)) {
                    //不更新直接进入主界面,更新--->在动画开始时已经有动作
                    startHome();
                } else {
                    Toast.makeText(SplashActivity.this, "正在检测更新", Toast.LENGTH_SHORT).show();
                }
            }
        });
        mAnimatorSet.start();
    }

    private void initData() {

        //将资产文件中的数据库文件拷贝到本地文件
        AssetsUtil.copyDb(getFilesDir(), ASSETS_DB_COMMONNUM);
        AssetsUtil.copyDb(getFilesDir(), ASSETS_DB_ADDRESS);
        AssetsUtil.copyDb(getFilesDir(), ASSETS_DB_VIRUS);
        //获取包管理器
        PackageManager mPackageManager = getPackageManager();
        try {
            //获取包信息
            PackageInfo packageInfo = mPackageManager.getPackageInfo(getPackageName(), 0);
            mVersionCode = packageInfo.versionCode;
            mVersionName = packageInfo.versionName;
            //设置界面版本号和版本名
            mVersionCodeTv.setText(mVersionCode + "");
            mVersionNameTv.setText(mVersionName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void initView() {

    }

    private void startHome() {
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
        finish();
    }
}
