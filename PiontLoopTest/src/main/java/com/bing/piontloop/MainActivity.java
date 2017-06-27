package com.bing.piontloop;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private TabLayout mTabLayout;
    private ViewPager mViewPager;
    private int mTabCount = 4;
    private ArrayList<View> mCirleViews;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mViewPager = (ViewPager) findViewById(R.id.id_viewpager);

        mCirleViews = new ArrayList<>();

        for (int i = 0; i < 4; i++) {
            View view = View.inflate(this, R.layout.vp_item_layout, null);
            mCirleViews.add(view);
        }

        MyAdapter myAdapter = new MyAdapter(this, mCirleViews);
        mViewPager.setAdapter(myAdapter);

        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
              View view =   mCirleViews.get(position);
                CirleView cirleView = (CirleView) view.findViewById(R.id.guide_cv);
                cirleView.updateEcho(position);
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });


    }

    class MyAdapter extends MyBasePagerAdapter {

        MyAdapter(Context context) {
            super(context);
        }

        public MyAdapter(Context context, ArrayList<View> childViews) {
            super(context, childViews);
        }

        @Override
        protected ArrayList<View> initChildItem(Object object) {

            return null;
        }
    }
}
