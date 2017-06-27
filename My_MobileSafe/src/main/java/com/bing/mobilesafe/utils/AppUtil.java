package com.bing.mobilesafe.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.widget.Toast;

import com.bing.mobilesafe.db.dao.AppLockInfoDao;
import com.bing.mobilesafe.db.dao.BlackNumberInfoDao;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by 520 on 2016/12/19.
 */

public class AppUtil {

    /**
     * 在application中调用,初始化所有工具类
     *
     * @param context
     * @param parms
     */
    public static void initOtherUtil(Context context, String... parms) {

        SpUtil.prepare(context, parms[0]);
        BlackNumberInfoDao.prepare(context);
        AppLockInfoDao.prepare(context);
        AssetsUtil.prepare(context);
    }

    /**
     * 获取系统所有app信息
     *
     * @param context
     * @return
     */
    public static List<AppInfoBean> getAllAppInfo(Context context) {

        List<AppInfoBean> appInfoBeens = new ArrayList<>();
        //获取包管理器
        PackageManager packageManager = context.getPackageManager();
        //获取安装包的信息
        List<PackageInfo> infos = packageManager.getInstalledPackages(0);

        //遍历
        for (PackageInfo info : infos) {
            AppInfoBean appInfoBean = new AppInfoBean();

            //获取包名
            String packageName = info.packageName;
            appInfoBean.setAppPackageName(packageName);

            //获取app名称
            String label = info.applicationInfo.loadLabel(packageManager).toString();
            appInfoBean.setAppName(label);

            //获取app图标
            Drawable icon = info.applicationInfo.loadIcon(packageManager);
            appInfoBean.setAppIcon(icon);

            //获取app大小
            String path = info.applicationInfo.sourceDir;
            long size = new File(path).length();
            appInfoBean.setAppSize(size);

            //用户标志
            int flags = info.applicationInfo.flags;
            //系统标志
            int flaSystem = ApplicationInfo.FLAG_SYSTEM;

            if ((flags & flaSystem) == 1) {
                //系统软件
                appInfoBean.setUserApp(false);
            } else {
                //用户软件
                appInfoBean.setUserApp(true);
            }
            //安装位置标志
            int flagExternalStorage = ApplicationInfo.FLAG_EXTERNAL_STORAGE;

            if ((flags & flagExternalStorage) == 1) {
                //说明安装在sd卡
                appInfoBean.setSDApp(true);
            } else {
                //说明安装在内存
                appInfoBean.setSDApp(false);
            }
            appInfoBeens.add(appInfoBean);
        }

        return appInfoBeens;
    }

    /**
     * 启动app详情界面
     *
     * @param activity
     * @param apkPackagename
     */
    public static void detailApp(Activity activity, String apkPackagename) {
       /* <action android:name="android.settings.APPLICATION_DETAILS_SETTINGS" />
        <category android:name="android.intent.category.DEFAULT" />
        <ta android:scheme="package" />*/
        Intent intent = new Intent();
        intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.setData(Uri.parse("package:" + apkPackagename));
        activity.startActivity(intent);
    }

    /**
     * 打开分享界面
     *
     * @param activity
     * @param shareText 分享时显示的文本内容
     */
    public static void shareApp(Activity activity, String shareText) {
       /* <action android:name="android.intent.action.SEND" />
        <category android:name="android.intent.category.DEFAULT" />
        <data android:mimeType="text/plain" />*/
        Intent intent = new Intent();
        intent.setAction("android.intent.action.SEND");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, shareText);
        activity.startActivity(intent);
    }

    /**
     * 开启其他app
     *
     * @param activity
     * @param apkPackagename
     */
    public static void startApp(Activity activity, String apkPackagename) {
        PackageManager pm = activity.getPackageManager();
        Intent intent = pm.getLaunchIntentForPackage(apkPackagename);
        if (intent != null) {
            activity.startActivity(intent);
        } else {
            Toast.makeText(activity, "该软件无法启动", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 卸载软件
     *
     * @param activity
     * @param apkPackagename
     */
    public static void uninstalllApp(Activity activity, String apkPackagename) {
       /* <action android:name="android.intent.action.VIEW" />
        <action android:name="android.intent.action.DELETE" />
        <category android:name="android.intent.category.DEFAULT" />
        <data android:scheme="package" />*/

        List<AppInfoBean> allAppInfo = getAllAppInfo(activity);
        boolean isExit = false;
        AppInfoBean apkInfoBean = null;
        for (AppInfoBean appInfoBean : allAppInfo) {
            String apkPackagename1 = appInfoBean.getAppPackageName();
            if (apkPackagename1.equals(apkPackagename)) {
                apkInfoBean = appInfoBean;
                isExit = true;
            }
        }
        if (!isExit) {
            Toast.makeText(activity, "不存在该软件,无法卸载", Toast.LENGTH_SHORT).show();
            return;
        }

        if (apkInfoBean.isUserApp()) {

            Intent intent = new Intent();
            intent.setAction("android.intent.action.VIEW");
            intent.setAction("android.intent.action.DELETE");
            intent.addCategory("android.intent.category.DEFAULT");
            intent.setData(Uri.parse("package:" + apkPackagename));
            activity.startActivity(intent);
        } else {
            Toast.makeText(activity, "系统软件root后才能卸载", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 回到launcher界面,即桌面
     *
     * @param activity
     */

    public static void startLauncher(Activity activity) {

        // <action android:name="android.intent.action.MAIN" />
        // <category android:name="android.intent.category.HOME" />
        // <category android:name="android.intent.category.DEFAULT" />
        // <category android:name="android.intent.category.MONKEY"/>

        Intent intent = new Intent();
        intent.setAction("android.intent.action.MAIN");
        intent.addCategory("android.intent.category.HOME");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.addCategory("android.intent.category.MONKEY");
        activity.startActivity(intent);
    }

    /**
     * 隐藏app图标
     *
     * @param activity
     */
    public static void hideAppIcon(Activity activity) {
        activity.getPackageManager().setComponentEnabledSetting(activity.getComponentName(),
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
    }

    /**
     * 在桌面生成快捷方式
     * <p>
     * -- 获取在桌面创建快捷方式的权限--
     * <uses-permission android:name="com.android.launcher.permission.INSTALL_SHORTCUT"/>
     *
     * @param activity
     */

    public static void createShortCut(Activity activity, Bitmap iconBitmap, String appName, String action) {

        //在桌面创建快捷方式的意图
        Intent intent = new Intent("com.android.launcher.action.INSTALL_SHORTCUT");
        // 传递的图片
        intent.putExtra(Intent.EXTRA_SHORTCUT_ICON, iconBitmap);
        // 软件的名称
        intent.putExtra(Intent.EXTRA_SHORTCUT_NAME, appName);
        // 点击这个快捷方式的意图(隐式意图)
        Intent shortCutIntent = new Intent();
        shortCutIntent.setAction(action);
        intent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortCutIntent);
        //发送广播给桌面,创建快捷方式
        activity.sendBroadcast(intent);
    }

    /**
     * 安装apk
     *
     * @param activity
     * @param requestCode
     * @param apkFile     安装包路径,用来生成uri
     */
    private void inStallApk(Activity activity, int requestCode, File apkFile) {
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
        intent.setDataAndType(Uri.fromFile(apkFile), "application/vnd.android.package-archive");

        // startActivity(intent);//这种方法启动安装界面,安装完成后,将不会自动进入下一个界面,造成假死界面
        activity.startActivityForResult(intent, requestCode);
    }

    public static class AppInfoBean {

        private Drawable appIcon;
        private String appName;
        private String appPackageName;

        private long appSize;
        private boolean isUserApp;
        private boolean isSDApp;

        public AppInfoBean() {
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

        public String getAppPackageName() {
            return appPackageName;
        }

        public void setAppPackageName(String appPackageName) {
            this.appPackageName = appPackageName;
        }

        public long getAppSize() {
            return appSize;
        }

        public void setAppSize(long appSize) {
            this.appSize = appSize;
        }

        public boolean isSDApp() {
            return isSDApp;
        }

        public void setSDApp(boolean isSDApp) {
            this.isSDApp = isSDApp;
        }

        public boolean isUserApp() {
            return isUserApp;
        }

        public void setUserApp(boolean isUserApp) {
            this.isUserApp = isUserApp;
        }
    }
}
