package com.romens.yjk.health.config;

import android.graphics.Color;

import com.romens.yjk.health.MyApplication;
import com.romens.yjk.health.R;

/**
 * Created by siery on 15/8/18.
 */
public class ResourcesConfig {
    public static int greyBackground = 0xffeeeeee;
    public static int greyColor = 0xffeeeeee;

    public static int primaryColor;
    public static int accentColor;

    public static final int textPrimary;

    public static final int bodyText1 = 0xff212121;
    public static final int bodyText2 = 0x80000000;
    public static final int bodyText3 = 0x60000000;

    public static final int emergencyText;

    public static final int priceFontColor = 0xffb0120a;

    public static final int favoritesColor = 0xffe51c23;

    public static final int shoppingAccent;

    public static final int tipColor=0xfffff9c4;

    public static final int transparentColor= Color.TRANSPARENT;

    static {
        primaryColor = MyApplication.applicationContext.getResources().getColor(R.color.theme_primary);
        accentColor = MyApplication.applicationContext.getResources().getColor(R.color.theme_accent);

        textPrimary = MyApplication.applicationContext.getResources().getColor(R.color.text_primary);

        emergencyText = MyApplication.applicationContext.getResources().getColor(R.color.font_emergency);

        shoppingAccent= MyApplication.applicationContext.getResources().getColor(R.color.md_red_500);
    }
}
