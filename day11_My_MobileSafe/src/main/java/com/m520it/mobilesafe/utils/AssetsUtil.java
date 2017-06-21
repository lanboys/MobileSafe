package com.m520it.mobilesafe.utils;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by 520 on 2016/12/19.
 */

public class AssetsUtil {

    private static final String TAG = "-->mobile";

    private static Context mContext;

    private AssetsUtil() {
        throw new UnsupportedOperationException();
    }

    public static void prepare(Context context) {
        if (mContext == null) {
            mContext = context;
        } else {
            throw new RuntimeException("AssetsUtil.prepare()方法已经被调用过了!");
        }
    }

    private static void isContextNull() {
        if (mContext == null) {
            throw new RuntimeException("AssetsUtil.prepare方法未被调用,请先调用!");
        }
    }

    public static InputStream openFile(String fileName) {
        isContextNull();
        AssetManager assets = mContext.getAssets();
        InputStream open = null;
        try {
            open = assets.open(fileName);
        } catch (IOException e) {
            e.printStackTrace();
            Log.v(TAG, "AssetsUtil.openFile:::" + e.getLocalizedMessage());
        }
        return open;
    }

    public static void copyDb(final File filePath, final String dbName) {
        // isContextNull();
        new Thread() {
            public void run() {
                InputStream in = null;
                FileOutputStream fos = null;
                File file = new File(filePath, dbName);

                if (!file.exists() || file.length() <= 0) {
                    try {
                        //打开资产文件
                        in = AssetsUtil.openFile(dbName);
                        fos = new FileOutputStream(file);
                        int len;
                        byte[] buffer = new byte[1024];
                        while ((len = in.read(buffer)) != -1) {
                            fos.write(buffer, 0, len);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        Log.v(TAG, "AssetsUtil.run:::" + e.getLocalizedMessage());
                    } finally {
                        IOUtils.close(in);
                        IOUtils.close(fos);
                    }
                }
            }
        }.start();
    }
}
