package com.bing.mobilesafe.db.dao;

import android.test.AndroidTestCase;
import android.util.Log;

import com.bing.mobilesafe.cons.Constant;

import java.util.List;

/**
 * @author 蓝兵
 * @time 2016/12/26  20:40
 */
public class AppLockInfoDaoTest extends AndroidTestCase {


    private AppLockInfoDao mInstance=AppLockInfoDao.getInstance();

    public  void testQueryAll() {
        List<String> strings = mInstance.queryAll();
        Log.v(Constant.TAG, "AppLockInfoDaoTest.testQueryAll:::" + strings.size());
        for (String string : strings) {
            Log.v(Constant.TAG, "AppLockInfoDaoTest.testQueryAll:::" + string);

        }
    }


}