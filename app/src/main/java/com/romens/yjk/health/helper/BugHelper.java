package com.romens.yjk.health.helper;

import android.content.Context;

import com.romens.bug.BugManager;
import com.romens.yjk.health.config.UserConfig;

/**
 * @author Zhou Lisi
 * @create 2016-03-25 01:00
 * @description
 */
public class BugHelper {
    public static void updateBugUser(Context context){
        if(UserConfig.getInstance().isClientLogined()) {
            BugManager.createBugUser(UserConfig.getInstance().getClientUserId());
            BugManager.createBugUser(context, "Org", UserConfig.getInstance().getOrgCode());
            BugManager.createBugUser(context, "Phone", UserConfig.getInstance().getClientUserPhone());
        }
    }
}
