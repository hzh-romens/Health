package com.romens.yjk.health.helper;

import com.umeng.update.UmengUpdateAgent;
import com.umeng.update.UpdateStatus;

/**
 * Created by siery on 15/12/24.
 */
public class UMengHelper {
    public static void setDefault() {
        UmengUpdateAgent.setUpdateOnlyWifi(true);
        UmengUpdateAgent.setDeltaUpdate(true);
        UmengUpdateAgent.setUpdateAutoPopup(true);
        UmengUpdateAgent.setRichNotification(true);
        UmengUpdateAgent.setUpdateUIStyle(UpdateStatus.STYLE_NOTIFICATION);
    }
}
