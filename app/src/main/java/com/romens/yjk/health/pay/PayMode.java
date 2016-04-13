package com.romens.yjk.health.pay;

import android.text.TextUtils;

/**
 * @author Zhou Lisi
 * @create 16/2/26
 * @description 医保支付方式实体
 */
public class PayMode {
    public final int id;
    public final int iconResId;
    public final String name;
    public final String desc;
    public final PayModeEnum mode;

    private PayMode(int id, int iconResId, String name, String desc, PayModeEnum mode) {
        this.id = id;
        this.iconResId = iconResId;
        this.name = name;
        this.desc = desc;
        this.mode = mode;
    }

    public static class Builder {
        private int id;
        private int iconResId;
        private String name;
        private String desc;
        private PayModeEnum mode;

        public Builder(int id) {
            this.id = id;
        }

        public Builder withIconResId(int resId) {
            this.iconResId = resId;
            return this;
        }

        public Builder withName(String name) {
            this.name = name;
            return this;
        }

        public Builder withDesc(String desc) {
            this.desc = desc;
            return this;
        }

        public Builder withMode(PayModeEnum mode) {
            this.mode = mode;
            return this;
        }

        public PayMode build() {
            PayMode payMode = new PayMode(id, iconResId, name, desc, mode);
            return payMode;
        }
    }

    public String getPayModeKey() {
        if (mode == PayModeEnum.YB_HEB) {
            return "PAY_YB_HEB";
        } else if (mode == PayModeEnum.WX) {
            return "PAY_WX";
        } else if (mode == PayModeEnum.ALIPAY) {
            return "PAY_ALIPAY";
        }
        return "";
    }

    public static PayModeEnum getPayMode(String key) {
        if (TextUtils.equals("PAY_YB_HEB", key)) {
            return PayModeEnum.YB_HEB;
        } else if (TextUtils.equals("PAY_WX", key)) {
            return PayModeEnum.WX;
        } else if (TextUtils.equals("PAY_ALIPAY", key)) {
            return PayModeEnum.ALIPAY;
        }
        return null;
    }

    public static String getPayModeDesc(String key) {
        if (TextUtils.equals("PAY_YB_HEB", key)) {
            return "社保卡支付(哈尔滨银行)";
        } else if (TextUtils.equals("PAY_WX", key)) {
            return "微信支付";
        } else if (TextUtils.equals("PAY_ALIPAY", key)) {
            return "支付宝支付";
        }
        return "";
    }

    public static final String PAY_MODE_WX = "PAY_WX";
    public static final String PAY_MODE_ALIPAY = "PAY_ALIPAY";
    public static final String PAY_MODE_YB_HEB = "PAY_YB_HEB";
}
