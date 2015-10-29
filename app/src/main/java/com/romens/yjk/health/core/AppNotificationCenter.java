package com.romens.yjk.health.core;

import com.romens.android.core.NotificationCenter;

public class AppNotificationCenter extends NotificationCenter {

    private static int totalEvents = maxCursor;

    public static final int loginSuccess = totalEvents++;
    public static final int shoppingCartCountChanged = totalEvents++;

    //AppConfig
    public static final int appConfigLoaded = totalEvents++;
    public static final int appConfigChanged = totalEvents++;

    //order
    public static final int orderCompleteAdd = totalEvents++;

}
