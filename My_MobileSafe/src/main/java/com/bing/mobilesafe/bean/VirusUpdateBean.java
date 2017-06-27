package com.bing.mobilesafe.bean;

import com.google.gson.Gson;

/**
 * Created by 520 on 2016/12/26.
 */

public class VirusUpdateBean {

    /**
     * version : 1725
     * md5 : 04dd4ea1347f1152e7b183503918aba1
     * type : 6
     * name : wangdoujia
     * desc : sfsfsfsf
     */

    public String version;
    public String md5;
    public String type;
    public String name;
    public String desc;

    public static VirusUpdateBean objectFromData(String str) {

        return new Gson().fromJson(str, VirusUpdateBean.class);
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
