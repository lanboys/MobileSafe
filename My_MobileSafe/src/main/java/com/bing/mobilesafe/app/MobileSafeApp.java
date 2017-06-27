package com.bing.mobilesafe.app;

import android.app.Application;
import android.os.Build;

import com.bing.mobilesafe.cons.Constant;
import com.bing.mobilesafe.utils.AppUtil;

import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Field;

/**
 * @author 蓝兵
 * @time 2016/12/15  16:46
 */
public class MobileSafeApp extends Application {

    @Override
    public void onCreate() {

        Thread.currentThread().setUncaughtExceptionHandler(new MyExceptionHander());

        AppUtil.initOtherUtil(getApplicationContext(), Constant.SP_FILE_NAME);
        super.onCreate();
    }

    private class MyExceptionHander implements Thread.UncaughtExceptionHandler {

        @Override
        public void uncaughtException(Thread thread, Throwable ex) {
            // Logger.i("MobileSafeApplication", "发生了异常,但是被哥捕获了...");
            //并不能把异常给消化掉. 只是应用程序挂掉之前,来一个留遗嘱的时间
            try {
                Field[] fields = Build.class.getDeclaredFields();
                StringBuffer sb = new StringBuffer();
                for (Field field : fields) {
                    String value = field.get(null).toString();
                    String name = field.getName();
                    sb.append(name);
                    sb.append(":");
                    sb.append(value);
                    sb.append("\n");
                }

                FileOutputStream out = new FileOutputStream("/mnt/sdcard/error.log");
                StringWriter wr = new StringWriter();
                PrintWriter err = new PrintWriter(wr);
                ex.printStackTrace(err);
                String errorlog = wr.toString();
                sb.append(errorlog);
                out.write(sb.toString().getBytes());
                out.flush();
                out.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            //专注自杀, 早死早超生
            android.os.Process.killProcess(android.os.Process.myPid());
        }
    }
}

