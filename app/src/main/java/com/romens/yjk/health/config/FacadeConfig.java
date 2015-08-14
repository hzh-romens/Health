package com.romens.yjk.health.config;

/**
 * Created by siery on 15/6/16.
 */
public class FacadeConfig {
    private static final int MODE_TEST = 0;
    private static final int MODE_RELEASE = 1;
    private static final int facadeMode = MODE_RELEASE;
    public static final String URL = "http://115.28.244.190/index.php/";

    public static final String URL_TEST = "http://115.28.244.190/index.php/";

    public static String getUrl() {
        if (facadeMode == MODE_TEST) {
            return URL_TEST;
        }
        return URL;
    }
}
