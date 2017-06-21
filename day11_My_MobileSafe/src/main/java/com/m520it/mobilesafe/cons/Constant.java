package com.m520it.mobilesafe.cons;

import com.m520it.mobilesafe.R;

/**
 * @author 蓝兵
 * @time 2016/12/12  21:18
 */
public class Constant {

    public static final String TAG = "-->mobile";
    //配置的文件名
    public static final String SP_FILE_NAME = "config";
    //用户是否自动更新设置
    public static final String AUTO_UPDATA_CHECK = "updateflag";
    public static final String PASSWORD = "password";
    // public static final String ISGUIDE = "isGuide";
    public static final String FINISH_SETUP = "finishSetup";
    public static final String SIM_NUMBER = "simSerialNumber";
    public static final String SAFE_NUMBER = "safeNumber";
    public static final String SAFE_STATE = "safeState";

    public static final String ASSETS_DB_BASE_PATH = "/data/data/com.m520it.seemygomobilesafe/files/";

    public static final String ASSETS_DB_ADDRESS = "address.db";
    public static final String ASSETS_DB_ADDRESS_PATH = ASSETS_DB_BASE_PATH + ASSETS_DB_ADDRESS;

    public static final String ASSETS_DB_COMMONNUM = "commonnum.db";
    public static final String ASSETS_DB_COMMONNUM_PATH = ASSETS_DB_BASE_PATH + ASSETS_DB_COMMONNUM;

    public static final String ASSETS_DB_VIRUS = "antivirus.db";
    public static final String ASSETS_DB_VIRUS_PATH = ASSETS_DB_BASE_PATH + ASSETS_DB_VIRUS;

    public static final String TAOST_PARAMS_X = "toastParamsX";
    public static final String TAOST_PARAMS_Y = "toastParamsY";
    public static final String STYLE = "style";
    public static final  String[] STYLE_NAMES = {"半透明", "维仕蓝","苹果绿", "金属灰", "橘子黄" };
    public static final  int[] STYLE_RESID = {
            R.drawable.call_locate_white,
            R.drawable.call_locate_blue,
            R.drawable.call_locate_green,
            R.drawable.call_locate_gray,
            R.drawable.call_locate_orange,
    };

    public static final String SHOWSYS = "showSystemProcess";
    public  static final String CREATE_SHORTCUT = "createShortCut";

    public class URL {

        //版本更新检测url
        // public static final String APP_UPDATA_URL = "http://192.168.33.237:8080/mobilsafe/info.json";
        public static final String APP_UPDATA_URL = "http://192.168.100.101:8080/mobilsafe/info.json";
        public static final String VIRUS_DB_UPDATA_URL = "http://192.168.100.101:8080/mobilsafe/update.txt";
    }
}
