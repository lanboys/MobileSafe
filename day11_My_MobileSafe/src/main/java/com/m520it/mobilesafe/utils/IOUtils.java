package com.m520it.mobilesafe.utils;

import com.lidroid.xutils.util.LogUtils;

import java.io.Closeable;
import java.io.IOException;

public class IOUtils {

    /**
     * 关闭流
     */
    public static boolean close(Closeable io) {
        if (io != null) {
            try {
                io.close();
            } catch (IOException e) {
                LogUtils.e(e.toString());
            }
        }
        return true;
    }
}
