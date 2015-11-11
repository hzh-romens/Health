package com.romens.yjk.health.config;

/**
 * Created by anlc on 2015/10/19.
 */
public class UserGuidConfig {
    public static String USER_GUID = "";

    static {
        USER_GUID = UserConfig.getClientUserEntity().getGuid();
    }

    public final static int RESPONSE_MEMBER_TO_REMIND = 1003;
    public final static int RESPONSE_DRUGGROUP_TO_REMIND = 1004;
    public final static int RESPONSE_SEARCH_TO_DRUGGROUP = 1001;


    public final static int REQUEST_REMIND_TO_MEMBER = 1002;
    public final static int REQUEST_SEARCH = 1000;
    public final static int REQUEST_REMIND_TO_DRUGGROUP = 1005;

}
