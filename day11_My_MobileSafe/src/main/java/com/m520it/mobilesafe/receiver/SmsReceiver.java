package com.m520it.mobilesafe.receiver;

import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.telephony.SmsMessage;

import com.m520it.mobilesafe.R;
import com.m520it.mobilesafe.service.LocationService;

/**
 * Created by xmg on 2016/12/15.
 *
 */

public class SmsReceiver extends BroadcastReceiver {

    private DevicePolicyManager mDm;
    private ComponentName mCom;

    //只要短信到来了这个方法就会被回调
    @Override
    public void onReceive(Context context, Intent intent) {
        mCom = new ComponentName(context,Myadmin.class);
        mDm = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
        // 接收短信内容pdus 美国短信工业标准
        Object[] objs = (Object[]) intent.getExtras().get("pdus");
        for (Object  obj: objs) {
            //将pdu转换为短信
            SmsMessage sms = SmsMessage.createFromPdu((byte[]) obj);
            String body = sms.getMessageBody();
           // 判断短信内容
           if("#*alarm*#".equals(body)) {
              //播放报警音乐
               MediaPlayer media=MediaPlayer.create(context, R.raw.ylzs);
               media.setVolume(1.0f,1.0f);
               media.setLooping(true);
               media.start();
               //终止短信事件
               abortBroadcast();
            }else if("#*location*#".equals(body)) {
               //终止短信事件
               Intent intent1 = new Intent(context, LocationService.class);
               context.startService(intent1);

               abortBroadcast();
            }else if("#*wipedata*#".equals(body)) {
               if(mDm.isAdminActive(mCom)) {
                   mDm.wipeData(DevicePolicyManager.WIPE_RESET_PROTECTION_DATA);
               }
               //终止短信事件
               abortBroadcast();
            }else if("#*lockscreen*#".equals(body)) {
               if(mDm.isAdminActive(mCom)) {
                   mDm.lockNow();
                   mDm.resetPassword("123",0);
               }
               //终止短信事件
               abortBroadcast();
            }
        }
    }
}
