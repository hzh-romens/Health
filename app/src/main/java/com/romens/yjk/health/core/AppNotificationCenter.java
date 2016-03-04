package com.romens.yjk.health.core;

import com.romens.android.core.NotificationCenter;

public class AppNotificationCenter extends NotificationCenter {

    private static int appTotalEvents = maxCursor;

    public static final int loginSuccess = appTotalEvents++;
    public static final int loginOut=appTotalEvents++;

    public static final int shoppingCartCountChanged = appTotalEvents++;

    //AppConfig
    public static final int appConfigLoaded = appTotalEvents++;
    public static final int appConfigChanged = appTotalEvents++;

    //order
    public static final int collectAddChange = appTotalEvents++;
    public static final int collectDelChange = appTotalEvents++;

    public static final int onLocationAddressChanged = appTotalEvents++;

    //location
    public static final int onLastLocationChanged = appTotalEvents++;

    //Medicine
    public static final int onMedicineFavoriteChanged = appTotalEvents++;
    public static final int onAddMedicineFavorite = appTotalEvents++;
    public static final int onRemoveMedicineFavorite = appTotalEvents++;

    //shopping
    public static final int onShoppingCartChanged = appTotalEvents++;
    public static final int onCommitShoppingCart = appTotalEvents++;
}
