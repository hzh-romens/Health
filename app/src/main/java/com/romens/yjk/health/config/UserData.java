package com.romens.yjk.health.config;

/**
 * @author Zhou Lisi
 * @create 2016-03-14 22:25
 * @description
 */
public class UserData {
    public final String phoneNumber;
    //手机号码
    public final String userName;
    //登录密码
    public String token;
    public final String userGuid;

    private UserData(Builder builder) {
        phoneNumber = builder.phone;
        userName = builder.userName;
        token = builder.token;
        userGuid = builder.userGuid;
    }

    public void clearToken() {
        token = "";
    }

    public static class Builder {
        private String phone;
        private String userName;
        private String token;
        private String userGuid;

        public Builder withPhone(String phone) {
            this.phone = phone;
            return this;
        }

        public Builder withLogin(String userName, String token) {
            this.userName = userName;
            this.token = token;
            return this;
        }

        public Builder withUserGuid(String userGuid) {
            this.userGuid = userGuid;
            return this;
        }

        public UserData build() {
            UserData userData = new UserData(this);
            return userData;
        }
    }
}
