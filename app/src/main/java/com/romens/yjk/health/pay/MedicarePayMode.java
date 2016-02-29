package com.romens.yjk.health.pay;

import com.romens.yjk.health.R;

/**
 * @author Zhou Lisi
 * @create 16/2/26
 * @description 医保支付方式实体
 */
public class MedicarePayMode {
    public final int id;
    public final int iconResId;
    public final String name;
    public final String desc;
    public final String mode;

    private MedicarePayMode(int id, int iconResId, String name, String desc, String mode) {
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
        private String mode;

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

        public Builder withMode(String mode) {
            this.mode = mode;
            return this;
        }

        public MedicarePayMode build() {
            MedicarePayMode medicarePayMode = new MedicarePayMode(id, iconResId, name, desc, mode);
            return medicarePayMode;
        }
    }
}
