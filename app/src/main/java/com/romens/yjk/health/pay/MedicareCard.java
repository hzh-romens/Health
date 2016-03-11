package com.romens.yjk.health.pay;

/**
 * @author Zhou Lisi
 * @create 16/2/26
 * @description
 */
public class MedicareCard {
    public final String id;
    public final String cardNo;
    public final String userName;
    public final String certNo;

    private MedicareCard(String id, String name, String certNo, String cardNo) {
        this.id = id;
        this.userName = name;
        this.certNo = certNo;
        this.cardNo = cardNo;
    }

    public static class Builder {
        private String id;
        private String cardNo;
        private String userName;
        private String certNo;

        public Builder(String id) {
            this.id = id;
        }

        public Builder withUserName(String userName) {
            this.userName = userName;
            return this;
        }

        public Builder withCertNo(String certNo) {
            this.certNo = certNo;
            return this;
        }

        public Builder withCardNo(String cardNo) {
            this.cardNo = cardNo;
            return this;
        }

        public MedicareCard build() {
            return new MedicareCard(id, userName, certNo, cardNo);
        }
    }
}
