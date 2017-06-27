package com.bing.mobilesafe.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.bing.mobilesafe.R;
import com.bing.mobilesafe.db.dao.NumberQueryDao;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class NumberQueryActivity extends AppCompatActivity {

    private static final String TAG = "-->mobile";
    @Bind(R.id.et_number_query)
    EditText mEtNumberQuery;
    @Bind(R.id.btn_number_query)
    Button mBtnNumberQuery;
    @Bind(R.id.tv_number_query)
    TextView mTvNumberQuery;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_number_query);
        ButterKnife.bind(this);
    }

    @OnClick({R.id.btn_number_query})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_number_query:
                queryNumber();
                break;
        }
    }

    private void queryNumber() {
        String phone = mEtNumberQuery.getText().toString().trim();

        if (TextUtils.isEmpty(phone)) {
            // Animation shake = AnimationUtils.loadAnimation(ShowAddressActivity.this, R.anim.shake);
            // et_number.startAnimation(shake);
            Toast.makeText(this, "请输入号码", Toast.LENGTH_SHORT).show();
        } else {
            String location = NumberQueryDao.findLocation(phone);
            location = ("".equals(location) ? "无记录" : location);
            mTvNumberQuery.setText("查询的位置为:" + location);
        }
    }
}
