package com.romens.yjk.health.pay;

import com.romens.yjk.health.R;

/**
 * @author Zhou Lisi
 * @create 16/2/26
 * @description  医保支付方式实体
 */
public class MedicarePayMode {
    public final int id;
    public final int iconResId;
    public final String name;
    public final String desc;

    public MedicarePayMode(int id, int iconResId, String name, String desc) {
        this.id = id;
        this.iconResId = iconResId;
        this.name = name;
        this.desc = desc;
    }
}
