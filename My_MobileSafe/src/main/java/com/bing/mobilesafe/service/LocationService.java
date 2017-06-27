package com.bing.mobilesafe.service;

import android.Manifest;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.telephony.SmsManager;
import android.util.Log;

import com.bing.mobilesafe.cons.Constant;
import com.bing.mobilesafe.utils.SpUtil;


public class LocationService extends Service {

    private static final String TAG = "-->mobile";

    public LocationService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        //1.获得定位管理器
        final LocationManager manager = (LocationManager) getSystemService(LOCATION_SERVICE);
        //2.获得最好的提供者
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_LOW);
        criteria.setPowerRequirement(Criteria.POWER_HIGH);

        String bestProvider = manager.getBestProvider(criteria, true);
        //动态权限
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult`(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more detais.
            return;
        }

        final String safeNum = SpUtil.getString(Constant.SAFE_NUMBER);
        if (bestProvider != null) {

            manager.requestLocationUpdates(bestProvider, 0, 0, new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    //获取经纬度
                    double longitude = location.getLongitude();
                    double latitude = location.getLatitude();

                    String msg = "经度: " + location + "纬度: " + latitude;
                    //发送短信
                    SmsManager.getDefault().sendTextMessage(safeNum, null, msg, null, null);
                    Log.v(TAG, "LocationService.onLocationChanged:::" +msg );

                    //移除监听 停止服务
                    manager.removeUpdates(this);
                    stopSelf();
                }

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {

                }

                @Override
                public void onProviderEnabled(String provider) {

                }

                @Override
                public void onProviderDisabled(String provider) {

                }
            });
        } else {
            //发送短信
            SmsManager.getDefault().sendTextMessage(safeNum, null, "定位失败,请重新定位", null, null);
            Log.v(TAG, "LocationService.onCreate:::定位失败,请重新定位");
            
            //停止服务
            stopSelf();
        }
    }
}
