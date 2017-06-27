package com.bing.mobilesafe;

import android.test.AndroidTestCase;

import com.bing.mobilesafe.fragment.GuideFragment2;

/**
 * Created by 520 on 2016/12/16.
 */

public class GuideFragmentTest extends AndroidTestCase {

    public void testGetSimSerialNumber() {
        GuideFragment2 guideFragmentTest = new GuideFragment2();
        guideFragmentTest.getSimSerialNumber();
    }
}
