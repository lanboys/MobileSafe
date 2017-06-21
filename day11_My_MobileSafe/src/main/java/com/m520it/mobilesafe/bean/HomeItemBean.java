package com.m520it.mobilesafe.bean;

/**
 * Created by 520 on 2016/12/13.
 */

public class HomeItemBean {

    private String names;
    private String desc;
    private int icons;

   public  HomeItemBean(String desc, int icons, String names) {
        this.desc = desc;
        this.icons = icons;
        this.names = names;
    }

    public String getDesc() {
        return desc;
    }

    public  int getIcons() {
        return icons;
    }

    public  String getNames() {
        return names;
    }

    public  void setDesc(String desc) {
        this.desc = desc;
    }

    public  void setIcons(int icons) {
        this.icons = icons;
    }

    public  void setNames(String names) {
        this.names = names;
    }
}
