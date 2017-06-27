package com.bing.mobilesafe.bean;

import com.google.gson.Gson;

/**
 * Created by 520 on 2016/12/12.
 */

public class UpdateInfoBean {

    /**
     * version : 2
     * downloadurl : http://192.168.100.101:8080/mobilsafe/xxx.apk
     * desc : 这个是360卫士的最新版本，赶紧来下载
     */

    private String version;
    private String downloadurl;
    private String desc;

    public static UpdateInfoBean objectFromData(String str) {

        return new Gson().fromJson(str, UpdateInfoBean.class);
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getDownloadurl() {
        return downloadurl;
    }

    public void setDownloadurl(String downloadurl) {
        this.downloadurl = downloadurl;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    @Override
    public String toString() {
        return "UpdateInfoBean{" +
                "desc='" + desc + '\'' +
                ", version='" + version + '\'' +
                ", downloadurl='" + downloadurl + '\'' +
                '}';
    }
}
