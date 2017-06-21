package com.m520it.mobilesafe.bean;

/**
 * @author 蓝兵
 * @time 2016/12/17  15:11
 */
public class BlackNumberBean {

    private String mode;
    private String phone;

    public BlackNumberBean(String mode, String phone) {
        this.mode = mode;
        this.phone = phone;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
