package com.bing.checkbox;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import static com.bing.checkbox.R.id.listView;

public class ListViewCheckBoxActivity extends AppCompatActivity {

    private static final String TAG = "-->mobile";

    private List<TestBean> list;

    private Adapter1 mAdapter;
    private ListView mListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_view_check_box);

        initView();
        initDate();
    }

    /**
     * 初始化view
     */
    private void initView() {

        TextView textView = new TextView(this);
        textView.setText("我是标题");
        textView.setGravity(Gravity.CENTER);
        textView.setTextSize(45);

        mListView = (ListView) findViewById(listView);
        mListView.addHeaderView(textView);
        // mListView.addHeaderView(textView);//添加两个头,很奇怪
        // TODO: 2017/1/4 测试onclickitemListener
    }

    /**
     * 模拟40个数据，奇数数据为选中状态，偶数数据为非选中状态
     */
    private void initDate() {
        mAdapter = new Adapter1(this);
        mListView.setAdapter(mAdapter);

        list = new ArrayList<TestBean>();
        TestBean testBean;

        for (int i = 0; i < 40; i++) {
            if (i % 2 == 0) {
                testBean = new TestBean("第"+i + "个checkbox", true);
                list.add(testBean);
            } else {
            testBean = new TestBean("第" + i + "个checkbox", false);
            list.add(testBean);
            }
        }

        mAdapter.setDatas(list);
    }

    class Adapter1 extends MyBaseAdapter<TestBean> {

        TestBean testBean;

        Adapter1(Context context) {
            super(context);
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            ViewHolder viewHolder;
            if (convertView == null) {
                viewHolder = new ViewHolder();
                convertView = LayoutInflater.from(mContext).inflate(R.layout.listview_checkbox, parent, false);
                viewHolder.layout = (LinearLayout) convertView.findViewById(R.id.layout);
                viewHolder.textView = (TextView) convertView.findViewById(R.id.textView);
                viewHolder.checkBox = (CheckBox) convertView.findViewById(R.id.checkBox);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            // final TestBean testBean;
            testBean = list.get(position);

            viewHolder.textView.setText(testBean.name);
            viewHolder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                    testBean.isCheck = isChecked;
                    // notifyDataSetChanged();
                }
            });
            viewHolder.checkBox.setChecked(testBean.isCheck);

            /*点击checkBox所在行改变checkBox状态*/
            final ViewHolder vv = viewHolder;
            viewHolder.layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    boolean checked = vv.checkBox.isChecked();
                    vv.checkBox.setChecked(!checked);
                    // testBean.isCheck = !checked;
                }
            });

            return convertView;
        }

        class ViewHolder {

            LinearLayout layout;
            TextView textView;
            CheckBox checkBox;
        }
    }

    class TestBean {

        String name;

        boolean isCheck;

        TestBean(String name, boolean isCheck) {
            this.name = name;
            this.isCheck = isCheck;
        }
    }
}
