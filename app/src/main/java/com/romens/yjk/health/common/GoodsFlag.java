package com.romens.yjk.health.common;

/**
 * @author Zhou Lisi
 * @create 2016-03-15 14:32
 * @description
 */
public class GoodsFlag {
    public static final String ARGUMENT_KEY_GOODS_FLAG = "key_goods_flag";

    public static final int NORMAL = 0;
    public static final int MEDICARE = 1;

    public static String checkFlagForArg(int flag) {
        if (flag == 1) {
            return "YB";
        }
        return "";
    }
}
