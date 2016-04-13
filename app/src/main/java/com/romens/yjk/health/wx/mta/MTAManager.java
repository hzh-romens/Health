package com.romens.yjk.health.wx.mta;

import android.content.Context;

import com.romens.yjk.health.MyApplication;
import com.romens.yjk.health.config.FacadeConfig;
import com.tencent.stat.StatConfig;
import com.tencent.stat.StatService;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by siery on 15/12/8.
 */
public class MTAManager {
    public static boolean init(Context context) {
        //            ApplicationInfo appInfo = context.getPackageManager()
//                    .getApplicationInfo(context.getPackageName(),
//                            PackageManager.GET_META_DATA);
//            String mtaAppKey = appInfo.metaData.getString("TA_APPKEY");
        return init(context, null);

    }

    public static boolean init(Context context, String mtaAppKey) {
        try {
            // 开启MTA debug，发布时一定要删除本行或设置为false
            StatConfig.setAutoExceptionCaught(false);
            StatConfig.setDebugEnable(false);
            return StatService.startStatService(context, mtaAppKey,
                    com.tencent.stat.common.StatConstants.VERSION);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 测试服务器网速
     */
    public static void testConnectServerSpeed() {
        Map<String, Integer> map = new HashMap<>();
        map.put(FacadeConfig.IP, 80);
        StatService.testSpeed(MyApplication.applicationContext, map);
    }
}
