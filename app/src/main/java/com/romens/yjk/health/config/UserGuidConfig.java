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
    public final static int RESPONSE_REMIND_TIMES_TO_NEW_REMIND = 1006;
    public final static int RESPONSE_EDITACTIVITY = 1008;
    public final static int RESPONSE_USERLABELS_TO_ACCOUNTSETTING = 1012;
    public final static int RESPONSE_SETTING_TO_HOMEMY = 1012;


    public final static int REQUEST_REMIND_TO_MEMBER = 1002;
    public final static int REQUEST_SEARCH = 1000;
    public final static int REQUEST_REMIND_TO_DRUGGROUP = 1005;
    public final static int REQUEST_NEW_REMIND_TO_REMIND_TIMES = 1007;
    public final static int REQUEST_ACCOUNTSETTING_TO_EDITACTIVITY = 1009;
    public final static int REQUEST_ACCOUNTSETTING2_TO_EDITACTIVITY = 1010;
    public final static int REQUEST_ACCOUNTSETTING_TO_USERLABELS = 1011;
    public final static int REQUEST_HOMEMY_TO_SETTING = 1013;

}
