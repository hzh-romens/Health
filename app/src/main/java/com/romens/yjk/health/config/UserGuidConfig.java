package com.romens.yjk.health.config;

/**
 * Created by anlc on 2015/10/19.
 */
public class UserGuidConfig {
    public static String USER_GUID = "";

    static {
        USER_GUID = UserConfig.getClientUserEntity().getGuid();
    }
}
