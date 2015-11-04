package com.romens.yjk.health.core;

import com.romens.android.core.NotificationCenter;

public class AppNotificationCenter extends NotificationCenter {

    private static int appTotalEvents = maxCursor;

    public static final int loginSuccess = appTotalEvents++;
    public static final int shoppingCartCountChanged = appTotalEvents++;

    //AppConfig
    public static final int appConfigLoaded = appTotalEvents++;
    public static final int appConfigChanged = appTotalEvents++;

    //order
    public static final int orderCompleteAdd = appTotalEvents++;

    public static final int onLocationAddressChanged = appTotalEvents++;

}
