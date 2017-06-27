package com.bing.mobilesafe.db.dao;

import android.test.AndroidTestCase;

import java.util.Random;

/**
 * @author 蓝兵
 * @time 2016/12/17  18:54
 */
public class BlackNumberInfoDaoTest extends AndroidTestCase {

    private BlackNumberInfoDao mInstance=BlackNumberInfoDao.getInstance();

    public void testInsert() {
        boolean insert = mInstance.insert("1223334", "0");
        assertEquals(insert, true);
    }

    //往数据库快速增加数据
    public void testInsertData() {
        Random random = new Random();
        for (int i = 0; i < 30; i++) {
            mInstance.insert("1355600480" + i, random.nextInt(2) + "");
        }
    }

    public void testDelete() {
        boolean insert = mInstance.delete("1212");
        assertEquals(insert, true);
    }

    public void testUpdata() {
        boolean insert = mInstance.update("1223334", "1212", "2");
        assertEquals(insert, true);
    }

    public void testQuery() {
        String insert = mInstance.query("1223334");
        assertEquals(insert, "1223334");
    }
}