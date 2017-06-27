package com.bing.mobilesafe.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.bing.mobilesafe.R;
import com.bing.mobilesafe.utils.ContactUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ContactSelectActivity extends AppCompatActivity {

    public static final String NAME = "name";
    public static final String NUMBER = "number";
    @Bind(R.id.guide_contact_select_lv)
    ListView mGuideContactSelectLv;
    private List<ContactUtils.ContactInfoBean> mDatas1;
    private List<Map<String, String>> mDatas;
    private Handler mHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_select);
        ButterKnife.bind(this);

        initDatas();
        initEvent();
    }

    private void initEvent() {
        mGuideContactSelectLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Map<String, String> item = (Map<String, String>) parent.getAdapter().getItem(position);

                String name = null;
                String num = null;
                for (Map.Entry<String, String> stringStringEntry : item.entrySet()) {
                    name = stringStringEntry.getKey();
                    num = stringStringEntry.getValue();
                }

                Intent intent = new Intent();
                intent.putExtra(NAME, name);
                intent.putExtra(NUMBER, num);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }

    private void initDatas() {

        new Thread() {
            public void run() {

                mDatas = new ArrayList<Map<String, String>>();

                List<ContactUtils.ContactInfoBean> allContacts = ContactUtils.getAllContacts(getApplicationContext());
                for (ContactUtils.ContactInfoBean allContact : allContacts) {
                    String name  = allContact.getName();
                    String phone  = allContact.getPhone();
                    HashMap<String, String> stringStringHashMap = new HashMap<>();
                    stringStringHashMap.put(NAME, name);
                    stringStringHashMap.put(NUMBER, phone);
                    mDatas.add(stringStringHashMap);
                }

                mHandler.post(new Runnable() {
                    @Override
                    public void run() {

                        if (mDatas != null) {

                            SimpleAdapter simpleAdapter = new SimpleAdapter(
                                    ContactSelectActivity.this,
                                    mDatas,
                                    android.R.layout.simple_list_item_2,
                                    new String[]{NAME, NUMBER},
                                    new int[]{android.R.id.text2, android.R.id.text1});

                            // public SimpleAdapter(
                            // Context context,
                            // List<? extends Map<String, ?>> data,
                            // @LayoutRes int resource,
                            // String[] from,
                            // @IdRes int[] to) {

                            mGuideContactSelectLv.setAdapter(simpleAdapter);
                        }
                    }
                });
            }
        }.start();
    }
}
