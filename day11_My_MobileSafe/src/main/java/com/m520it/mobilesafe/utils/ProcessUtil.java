package com.m520it.mobilesafe.utils;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Debug;

import com.m520it.mobilesafe.R;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import static android.content.Context.ACTIVITY_SERVICE;

/**
 * @author 蓝兵
 * @time 2016/12/22  9:32
 */
public class ProcessUtil {

    public static List<ProcessInfoBean> getAllProcessInfo(Context context) {
        List<ProcessInfoBean> processInfoBeans = new ArrayList<>();
        //获取包管理器
        PackageManager packageManager = context.getPackageManager();
        //获取activity管理器
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        //获取正在运行的进程的信息
        List<ActivityManager.RunningAppProcessInfo> processes = activityManager.getRunningAppProcesses();
        //遍历
        for (ActivityManager.RunningAppProcessInfo process : processes) {
            ProcessInfoBean processInfoBean = new ProcessInfoBean();
            //获取进程名
            String processName = process.processName;
            processInfoBean.setProcessPackagename(processName);
            try {
                //跟获取app信息差不多

                //获取进程占用内存大小
                Debug.MemoryInfo memoryInfo = activityManager.getProcessMemoryInfo(new int[]{process.pid})[0];
                long size = memoryInfo.getTotalPrivateDirty() * 1024;
                processInfoBean.setProcessSize(size);

                //通过进程名字获取包信息(可能会出现NameNotFoundException异常)
                PackageInfo packageInfo = packageManager.getPackageInfo(processName, 0);
                //通过包信息获取app图标
                Drawable icon = packageInfo.applicationInfo.loadIcon(packageManager);
                processInfoBean.setProcessIcon(icon);
                //通过包信息获取app名称
                String label = packageInfo.applicationInfo.loadLabel(packageManager).toString();
                processInfoBean.setProcessName(label);
                //用户标志
                int flags = packageInfo.applicationInfo.flags;
                //系统标志
                int flaSystem = ApplicationInfo.FLAG_SYSTEM;

                if ((flags & flaSystem) == 1) {
                    //系统进程
                    processInfoBean.setUserProcess(false);
                } else {
                    //用户进程
                    processInfoBean.setUserProcess(true);
                }
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
                //通过进程名字获取不到包信息,进行默认处理
                processInfoBean.setProcessIcon(context.getResources().getDrawable(R.mipmap.ic_launcher));
                processInfoBean.setProcessName(processName);
            }

            processInfoBeans.add(processInfoBean);
        }
        return processInfoBeans;
    }

    //获取当前设备正则运行的进程数
    public static int getRunningProcessCount(Context context) {

        //获取activity的管理器
        ActivityManager manager = (ActivityManager) context.getSystemService(ACTIVITY_SERVICE);

        List<ActivityManager.RunningAppProcessInfo> runningAppProcesses = manager.getRunningAppProcesses();
        return runningAppProcesses.size();
    }

    //得到可用剩余内存
    public static long getAvailMem(Context context) {
        //获取activity的管理器
        ActivityManager manager = (ActivityManager) context.getSystemService(ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
        manager.getMemoryInfo(memoryInfo);
        return memoryInfo.availMem;
    }

    //得到总内存
    public static long getTotalMem(Context context) {
        //高版本的sdk使用

        //获取activity的管理器
        // ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        // ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
        // manager.getMemoryInfo(memoryInfo);
        // return memoryInfo.otalMem;

        //低版本的sdk使用
        StringBuffer sb = null;
        try {
            File file = new File("/proc/meminfo");//指向包括内存信息的文件
            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
            String line = reader.readLine();
            sb = new StringBuffer();
            for (char c : line.toCharArray()) {
                if (c >= '0' && c <= '9') {
                    sb.append(c);
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return sb != null ? Long.valueOf(sb.toString()) * 1024 : 0;
    }

    public static class ProcessInfoBean {

        private Drawable processIcon;
        private String processName;
        private String processPackagename;

        private long processSize;
        private boolean isUserProcess;
        private boolean isCheck;

        @Override
        public String toString() {
            return "ProcessInfoBean{" +
                    "processName='" + processName + '\'' +
                    ", isCheck=" + isCheck +
                    '}';
        }

        public boolean isCheck() {
            return isCheck;
        }

        public void setCheck(boolean check) {
            isCheck = check;
        }

        public Drawable getProcessIcon() {
            return processIcon;
        }

        public void setProcessIcon(Drawable processIcon) {
            this.processIcon = processIcon;
        }

        public String getProcessName() {
            return processName;
        }

        public void setProcessName(String processName) {
            this.processName = processName;
        }

        public String getProcessPackagename() {
            return processPackagename;
        }

        public void setProcessPackagename(String processPackagename) {
            this.processPackagename = processPackagename;
        }

        public long getProcessSize() {
            return processSize;
        }

        public void setProcessSize(long processSize) {
            this.processSize = processSize;
        }

        public boolean isUserProcess() {
            return isUserProcess;
        }

        public void setUserProcess(boolean isUserProcess) {
            this.isUserProcess = isUserProcess;
        }
    }
}
