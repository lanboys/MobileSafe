package com.m520it.mobilesafe.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xmg on 2016/12/15.
 * 专门用来获取联系人信息
 */

public class ContactUtils {

    public static List<ContactInfoBean> getAllContacts(Context context) {
        List<ContactInfoBean> lists = new ArrayList<>();
        // 1获得内容解析器
        ContentResolver resolver = context.getContentResolver();
        //准备对应的uri"content://主机面/表名/路径(一般不用写,查询的所有的)"
        Uri uri = Uri.parse("content://com.android.contacts/raw_contacts");
        Uri dataUri = Uri.parse("content://com.android.contacts/data");
        /**
         * 参数1 查询的路径
         * 参数2 查询的字段
         * 参数3 查询的条件
         * 参数4 查询条件的值
         * 参数5 排序
         */
        Cursor cursor = resolver.query(uri, new String[]{"contact_id"}, null, null, null);
        while (cursor.moveToNext()) {
            String contact_id = cursor.getString(0);
            if (!TextUtils.isEmpty(contact_id)) {
                //要根据id查询对应的联系人数据
                ContactInfoBean infoBean = new ContactInfoBean();
                Cursor datacursor = resolver.query(dataUri, new String[]{"mimetype", "data1"}, "contact_id=?", new String[]{contact_id}, null);
                while (datacursor.moveToNext()) {
                    String mimetype = datacursor.getString(0);
                    if ("vnd.android.cursor.item/phone_v2".equals(mimetype)) {
                        String data1 = datacursor.getString(1);
                        infoBean.setPhone(data1);
                    } else if ("vnd.android.cursor.item/name".equals(mimetype)) {
                        String data1 = datacursor.getString(1);
                        infoBean.setName(data1);
                    }
                }
                lists.add(infoBean);
            }
        }

        return lists;
    }

    public static class ContactInfoBean {

        public String name;
        public String phone;


        public ContactInfoBean( ) {

        }
        public ContactInfoBean(String name, String phone) {
            this.name = name;
            this.phone = phone;
        }

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}
