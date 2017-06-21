package com.m520it.mobilesafe.utils;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.os.SystemClock;
import android.util.Xml;
import android.widget.Toast;

import org.xmlpull.v1.XmlSerializer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by 520 on 2016/12/20.
 */

public class SmsBackupUtil {

    public static void smsBackup(final SmsBackup smsBackup, final Context context, final String fileName) {
        // new Thread(new Runnable() {
        //     @Override
        //     public void run() {

                try {
                    File file = new File(Environment.getExternalStorageDirectory(), fileName);
                    FileOutputStream fos = new FileOutputStream(file);
                    XmlSerializer xmlSerializer = Xml.newSerializer();
                    xmlSerializer.setOutput(fos, "utf-8");
                    xmlSerializer.startDocument("utf-8", true);
                    xmlSerializer.startTag(null, "info");

                    ContentResolver resolver = context.getContentResolver();
                    Uri uri = Uri.parse("content://sms/");
                    Cursor cursor = resolver.query(uri, new String[]{"address", "body"}, null, null, null);

                    int process = 0;
                    assert cursor != null;

                    if (smsBackup != null) {
                        smsBackup.beforeBackup(cursor.getCount());
                    }

                    while (cursor.moveToNext()) {
                        process++;
                        if (smsBackup != null) {
                            smsBackup.processBackup(process);
                        }
                        xmlSerializer.startTag(null, "sms");

                        xmlSerializer.startTag(null, "address");
                        String address = cursor.getString(0);
                        xmlSerializer.text(address);
                        xmlSerializer.endTag(null, "address");

                        xmlSerializer.startTag(null, "body");
                        String body = cursor.getString(1);
                        xmlSerializer.text(body);
                        xmlSerializer.endTag(null, "body");

                        xmlSerializer.endTag(null, "sms");
                        SystemClock.sleep(2000);
                    }

                    xmlSerializer.endTag(null, "info");
                    xmlSerializer.endDocument();
                    cursor.close();
                    IOUtils.close(fos);
                    if (smsBackup != null) {
                        smsBackup.backupResult(true);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    if (smsBackup != null) {
                        smsBackup.backupResult(false);
                    }
                }
            }
        // }).start();
    // }

    public static void smsBackup(final Activity activity, final String fileName) {
        final ProgressDialog progressDialog = new ProgressDialog(activity, ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setTitle("正在备份中,请稍等片刻...");
        progressDialog.show();
        new Thread(new Runnable() {
            @Override
            public void run() {

                SmsBackupUtil.smsBackup(new SmsBackupUtil.SmsBackup() {
                    @Override
                    public void beforeBackup(int max) {
                        //设置最大值,可以在子线程中操作ui
                        progressDialog.setMax(max);
                    }

                    @Override
                    public void processBackup(int process) {
                        progressDialog.setProgress(process);
                    }

                    @Override
                    public void backupResult(final boolean isSuccess) {
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (isSuccess) {
                                    Toast.makeText(activity, "备份成功", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(activity, "备份失败", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                }, activity, fileName);
                //销毁对话框
                progressDialog.dismiss();
            }
        }).start();
    }



    public interface SmsBackup {

        //短信备份前调用
        void beforeBackup(int max);

        //短信备份中调用
        void processBackup(int process);

        void  backupResult(boolean isSuccess);
    }
}
