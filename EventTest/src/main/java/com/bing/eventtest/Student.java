package com.bing.eventtest;

/**
 * Created by 520 on 2016/12/19.
 */

public class Student implements Cloneable {


    private String name;

    public  Student(String name) {
        this.name = name;
    }

    public  String getName() {
        return name;
    }

    public  void setName(String name) {
        this.name = name;
    }

    @Override
    public Object clone() {
        try {
            return super.clone();
        } catch (CloneNotSupportedException e) {
            return null;
        }
    }
}
