package com.m520it.mobilesafe.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @author 蓝兵
 * @time 2016/12/17  11:01
 */
public class MD5Util {

    //银行采用加盐..



    public static String password2md5(String password) {
        // 通过MD5加密 先获得数字摘要器
        MessageDigest digest = null;
        try {
            digest = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        byte[] digests = digest.digest(password.getBytes());
        //程序员操作的,看到都因该是String字符串
        //将这个byte[]的每个元素 转换为字符串
        //将转换得到string拼接
        StringBuffer sb = new StringBuffer();
        for (byte b : digests) {
            String s = Integer.toHexString(b & 0xff);
            if (s.length() == 1) {
                sb.append("0");
            }
            sb.append(s);
        }

        return sb.toString();
    }
}
