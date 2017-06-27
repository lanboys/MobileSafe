package com.bing.www.myapplication;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
  private GridView gv;
    private List<ResolveInfo> mInfos;
    private List<Drawable> mDrawables;
    private List<String> mNames;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        gv = (GridView)findViewById(R.id.gv);
        //获得所有能够被启动的app
        PackageManager pm = getPackageManager();
        Intent intent=new Intent();
        intent.setAction("android.intent.action.MAIN");
        intent.addCategory("android.intent.category.LAUNCHER");
        mInfos = pm.queryIntentActivities(intent, PackageManager.GET_INTENT_FILTERS);
        mDrawables = new ArrayList<>();
        mNames = new ArrayList<>();
        //得到所有能够被启动的app的图片和名称
        for (ResolveInfo  info: mInfos) {
            Drawable drawable = info.activityInfo.applicationInfo.loadIcon(pm);
            String name = info.activityInfo.applicationInfo.loadLabel(pm).toString();
            mDrawables.add(drawable);
            mNames.add(name);
        }


        gv.setAdapter(new MyMAinAdapter());

    }

  class MyMAinAdapter extends BaseAdapter{

      @Override
      public int getCount() {
          return mInfos.size();
      }

      @Override
      public View getView(int position, View convertView, ViewGroup parent) {
        View view=  View.inflate(getApplicationContext(),R.layout.item,null);
        TextView tv= (TextView) view.findViewById(R.id.tv);
          tv.setText(mNames.get(position));

        ImageView iv= (ImageView) view.findViewById(R.id.iv);
          iv.setImageDrawable(mDrawables.get(position));
          return view;
      }

      @Override
      public Object getItem(int position) {
          return null;
      }

      @Override
      public long getItemId(int position) {
          return 0;
      }


  }
}
