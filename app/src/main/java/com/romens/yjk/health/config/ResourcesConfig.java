package com.romens.yjk.health.config;

import com.romens.yjk.health.MyApplication;
import com.romens.yjk.health.R;

/**
 * Created by siery on 15/8/18.
 */
public class ResourcesConfig {
    public static int primaryColor;
    public static int greyColor=0xffeeeeee;
    public static final int textPrimary;

    public static final int bodyText1 = 0xff212121;
    public static final int bodyText2 = 0x80000000;
    public static final int bodyText3 = 0x60000000;

    public static final int priceFontColor=0xffc41411;

    static {
        primaryColor = MyApplication.applicationContext.getResources().getColor(R.color.theme_primary);
        textPrimary = MyApplication.applicationContext.getResources().getColor(R.color.text_primary);
    }
}
