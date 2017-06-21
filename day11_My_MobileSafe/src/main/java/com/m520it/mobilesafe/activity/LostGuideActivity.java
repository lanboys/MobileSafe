package com.m520it.mobilesafe.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.Toast;

import com.m520it.mobilesafe.R;
import com.m520it.mobilesafe.cons.Constant;
import com.m520it.mobilesafe.fragment.BaseFragment;
import com.m520it.mobilesafe.fragment.GuideFragment1;
import com.m520it.mobilesafe.fragment.GuideFragment2;
import com.m520it.mobilesafe.fragment.GuideFragment3;
import com.m520it.mobilesafe.fragment.GuideFragment4;
import com.m520it.mobilesafe.utils.IntentUtil;
import com.m520it.mobilesafe.utils.SpUtil;
import com.m520it.mobilesafe.view.CustomViewPager;

public class LostGuideActivity extends AppCompatActivity {

    public static final int NEXT_PAGE = 0X0001;
    public static final int PRE_PAGE = 0X0002;
    public static final int CONTACT_SELECT_REQUEST_CODE = 0X0003;
    public static final int DEVICE_POLICY_REQUEST_CODE = 0X0004;
    private static final String TAG = "-->mobile";
    //手指按下的点为(x1, y1)手指离开屏幕的点为(x2, y2)
    float x1 = 0;
    float x2 = 0;
    float y1 = 0;
    float y2 = 0;
    private CustomViewPager mCustomViewPager;
    // private String[] title = new String[]{"主题", "专栏"};
    // private int[] icon = new int[]{R.drawable.zhuti_bar_selector, R.drawable.zhuanlan_bar_selector};
    private BaseFragment[] fragments = new BaseFragment[]{
            new GuideFragment1(),
            new GuideFragment2(),
            new GuideFragment3(),
            new GuideFragment4()};
    private int mCurrentItem;
    public Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            changerViewPagerItem(msg.what);
        }
    };
    private FragmentPagerAdapter mAdapter;

    public Handler getHandler() {
        return mHandler;
    }

    public void changerViewPagerItem(int nextPage) {

        mCurrentItem = mCustomViewPager.getCurrentItem();
        switch (nextPage) {
            case PRE_PAGE:
                mCurrentItem--;
                break;
            case NEXT_PAGE:
                mCurrentItem++;
                break;
        }
        if (mCurrentItem > mCustomViewPager.getChildCount() || mCurrentItem < 0) {
            if (mCurrentItem < 0) {
                Toast.makeText(this, "您可以在主页再次进入向导", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "设置成功", Toast.LENGTH_SHORT).show();
            }
            startActivity(LostFoundActivity.class);
            return;
        }

        mCustomViewPager.setCurrentItem(mCurrentItem, true);
    }

    //从本界面到别的界面
    public void startActivity(Class<?> cls) {
        IntentUtil.startActivity(this, cls);
        finish();
    }

    @Override
    public void onAttachFragment(Fragment fragment) {
        Log.v(TAG, "LostGuideActivity.onAttachFragment:::fragment 初始化了");

        super.onAttachFragment(fragment);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == CONTACT_SELECT_REQUEST_CODE && resultCode == RESULT_OK) {
            if (data != null) {

                String stringExtra = data.getStringExtra(ContactSelectActivity.NUMBER).replace("-", "").replace(" ", "").trim();
                mCurrentItem = mCustomViewPager.getCurrentItem();

                FragmentPagerAdapter adapter = (FragmentPagerAdapter) mCustomViewPager.getAdapter();
                GuideFragment3 fragment = (GuideFragment3) adapter.getItem(mCurrentItem);
                fragment.updateEditEcho(stringExtra);
            }
        } else if (requestCode == DEVICE_POLICY_REQUEST_CODE && resultCode == RESULT_OK) {
            //fragment tag 怎么设置
            // TODO: 2016/12/18  
            SpUtil.putBoolean(Constant.SAFE_STATE, true);
            //找fragment容易出错
            // GuideFragment4 item = (GuideFragment4) mAdapter.getItem(mCustomViewPager.getCurrentItem());
            // item.updateToggleButtonCheckedValue();
        } else if (requestCode == DEVICE_POLICY_REQUEST_CODE && resultCode == RESULT_CANCELED) {
            SpUtil.putBoolean(Constant.SAFE_STATE, false);
            //在fragment的onStart中更新
            // GuideFragment4 item = (GuideFragment4) mAdapter.getItem(mCustomViewPager.getCurrentItem());
            // item.updateToggleButtonCheckedValue();
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lost_guide);

        initView();
        initDatas();
        initListner();
    }

    private void initView() {
        mCustomViewPager = (CustomViewPager) findViewById(R.id.id_viewpager);
        // for (BaseFragment fragment : fragments) {
        //
        // }
        // FragmentManager fragmentManager = getFragmentManager();
        // fragments[1].getFragmentTag(mCustomViewPager.getCurrentItem());
        // fragmentManager.findFragmentByTag();
    }

    private void initDatas() {
        mAdapter = new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return fragments[position];
            }

            @Override
            public int getCount() {
                return fragments.length;
            }

            @Override
            public CharSequence getPageTitle(int position) {
                return null;
            }
        };
        mCustomViewPager.setAdapter(mAdapter);
    }

    private void initListner() {

        mCustomViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                FragmentPagerAdapter adapter = (FragmentPagerAdapter) mCustomViewPager.getAdapter();

                BaseFragment item = (BaseFragment) adapter.getItem(mCurrentItem);
                item.updateCircleView(position);
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        mCustomViewPager.setOnScanScrollListner(new CustomViewPager.CanScrollListner() {
            @Override
            public boolean onScrollBefore(MotionEvent event) {

                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    //当手指按下的时候
                    x1 = event.getX();
                    y1 = event.getY();
                }
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    //当手指离开的时候
                    x2 = event.getX();
                    y2 = event.getY();
                }
                int state = 0;
                if (y1 - y2 > 5) {
                    state = 1;
                    // Toast.makeText(LostGuideActivity.this, "向上滑", Toast.LENGTH_SHORT).show();
                    // Log.v(TAG, "LostGuideActivity.onScrollBefore:::向上滑");
                } else if (y2 - y1 > 5) {
                    state = 2;
                    // Toast.makeText(LostGuideActivity.this, "向下滑", Toast.LENGTH_SHORT).show();
                    // Log.v(TAG, "LostGuideActivity.onScrollBefore:::向下滑");
                } else if (x1 - x2 > 5) {
                    state = 3;
                    // Toast.makeText(LostGuideActivity.this, "向左滑", Toast.LENGTH_SHORT).show();
                    // Log.v(TAG, "LostGuideActivity.onScrollBefore:::向左滑");
                } else if (x2 - x1 > 5) {
                    state = 4;
                    // Toast.makeText(LostGuideActivity.this, "向右滑", Toast.LENGTH_SHORT).show();
                    // Log.v(TAG, "LostGuideActivity.onScrollBefore:::向右滑");
                }

                mCurrentItem = mCustomViewPager.getCurrentItem();
                if (mCurrentItem == 1 && TextUtils.isEmpty(SpUtil.getString(Constant.SIM_NUMBER)) && state == 3) {
                    // Toast.makeText(LostGuideActivity.this, "请绑定号码后再往下一页", Toast.LENGTH_SHORT).show();
                    return false;
                } else if (mCurrentItem == 2 && TextUtils.isEmpty(SpUtil.getString(Constant.SAFE_NUMBER)) && state == 3) {
                    // Toast.makeText(LostGuideActivity.this, "请设置安全号码后再往下一页", Toast.LENGTH_SHORT).show();
                    return false;
                } else if (state == 4 || state == 3) {
                    return true;
                }
                return false;
            }
        });
    }
}
