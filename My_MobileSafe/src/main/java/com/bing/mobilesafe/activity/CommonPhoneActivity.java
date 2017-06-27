package com.bing.mobilesafe.activity;

import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bing.mobilesafe.R;
import com.bing.mobilesafe.db.dao.CommonNumberDao;

import butterknife.Bind;
import butterknife.ButterKnife;

import static com.bing.mobilesafe.cons.Constant.ASSETS_DB_COMMONNUM_PATH;

public class CommonPhoneActivity extends AppCompatActivity {

    @Bind(R.id.exlv_common_phone)
    ExpandableListView mExlvCommonPhone;
    @Bind(R.id.activity_common_phone)
    LinearLayout mActivityCommonPhone;
    private SQLiteDatabase mDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_common_phone);
        ButterKnife.bind(this);

        initData();
        initListener();
    }

    private void initListener() {
        mExlvCommonPhone.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {

                CharSequence text = ((TextView) v).getText();
                Toast.makeText(CommonPhoneActivity.this, text, Toast.LENGTH_SHORT).show();

                return false;
            }
        });
    }

    private void initData() {
        mDb = SQLiteDatabase.openDatabase(ASSETS_DB_COMMONNUM_PATH, null, SQLiteDatabase.OPEN_READONLY);
        mExlvCommonPhone.setAdapter(new CommonAdapter());
    }

    @Override
    protected void onDestroy() {
        mDb.close();
        super.onDestroy();
    }

    class CommonAdapter extends BaseExpandableListAdapter {

        @Override
        public int getGroupCount() {
            return CommonNumberDao.getGroupCount(mDb);
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            return CommonNumberDao.getChildrenCount(mDb, groupPosition);
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
            TextView tv = new TextView(getApplicationContext());
            tv.setTextSize(20);
            tv.setTextColor(Color.BLACK);
            tv.setText("     " + CommonNumberDao.getGroupView(mDb, groupPosition));
            return tv;
        }

        @Override
        public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
            TextView tv = new TextView(getApplicationContext());
            tv.setTextSize(20);
            tv.setTextColor(Color.BLACK);
            tv.setText(CommonNumberDao.getChildView(mDb, groupPosition, childPosition));
            return tv;
        }

        @Override
        public Object getGroup(int groupPosition) {
            return null;
        }

        @Override
        public Object getChild(int groupPosition, int childPosition) {
            return null;
        }

        @Override
        public long getGroupId(int groupPosition) {
            return 0;
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return 0;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }
    }
}
