package com.bing.mobilesafe.activity;

import android.test.AndroidTestCase;
import android.util.Log;

import com.bing.mobilesafe.service.CallSmsService;

/**
 * Created by 520 on 2016/12/17.
 */
public class HomeActivityTest  extends AndroidTestCase{

    private static final String TAG = "-->mobile";
    public void testPackname() {
        String name = CallSmsService.class.getPackage().getName();
        String name1 =CallSmsService.class.getName();
        Log.v(TAG, "HomeActivityTest.testPackname:::" + name);

    }

}