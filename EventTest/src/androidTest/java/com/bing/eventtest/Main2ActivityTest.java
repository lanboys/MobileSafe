package com.bing.eventtest;

import android.test.AndroidTestCase;
import android.util.Log;

/**
 * Created by 520 on 2016/12/19.
 */
public class Main2ActivityTest extends AndroidTestCase{

    public void testClone() {
        Student xiaoming = new Student("xiaoming");
        PeopleBean peopleBean = new PeopleBean(12, 1111000, xiaoming, "ren");

        Log.v(Constant.TAG, "Main2Activity.initClone:::" + xiaoming+"----"+xiaoming.hashCode());
        PeopleBean clone = (PeopleBean) peopleBean.clone();

        Log.v(Constant.TAG, "Main2Activity.initClone:::" + peopleBean.equals(clone));

        Log.v(Constant.TAG, "Main2Activity.initClone:::" + peopleBean);
        Log.v(Constant.TAG, "Main2Activity.initClone:::" + clone);

        Log.v(Constant.TAG, "Main2ActivityTest.testClone:::" + peopleBean.getStudent() +"----"+peopleBean.getStudent().hashCode());


    }

}