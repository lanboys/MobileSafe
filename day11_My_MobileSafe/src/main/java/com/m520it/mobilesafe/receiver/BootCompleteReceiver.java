package com.m520it.mobilesafe.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;

import com.m520it.mobilesafe.cons.Constant;
import com.m520it.mobilesafe.utils.SpUtil;

/**
 * Created by xmg on 2016/12/15.
 * 当手机重启后,就会有这样的广播监听
 */

public class BootCompleteReceiver extends BroadcastReceiver {

    //只有手机一重启完成这个方法就会被回调
    @Override
    public void onReceive(Context context, Intent intent) {
        //先判断是否开启了防盗保护
        boolean state = SpUtil.getBoolean(Constant.SAFE_STATE);
        if (state) {
            //说明防盗保护开启
            // 1 获得以前的sim串号
            String oldSim = SpUtil.getString(Constant.SIM_NUMBER);
            // 2 获得当前设备的串号
            TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            String newSim = tm.getSimSerialNumber() + "sfsf";
            // 3比对串号
            if (!oldSim.equals(newSim)) {
                //说明串号不相等
                /**
                 * 参数1 短信目标地址 号码
                 * 参数2 短信中心号码一般不写
                 * 参数3 短信文本
                 * 参数4 意图用不到
                 * 参数5 意图用不到
                 */
                SmsManager.getDefault().sendTextMessage(SpUtil.getString(Constant.SAFE_NUMBER), null, "sim change", null, null);
            }
        } else {
            //说明防盗保护没有开启
        }
    }
}
