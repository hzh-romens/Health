package com.romens.bug;

import android.content.Context;

/**
 * @author Zhou Lisi
 * @create 2016-03-24 23:47
 * @description
 */
public class BugConfig {
    private String appId;
    private String appChannel;
    private String appVersion;
    private String packageName;

    private BugConfig() {
    }

    public String getAppId(){
        return appId;
    }

    public String getAppChannel() {
        return appChannel;
    }

    public String getAppVersion() {
        return appVersion;
    }

    public String getPackageName() {
        return packageName;
    }

    public static class Builder {
        private String appId;
        private String appChannel;
        private String appVersion;
        private String packageName;

        public Builder(Context context, String appId) {
            this.packageName = context.getPackageName();
            this.appId = appId;
        }

        public Builder withAppChannel(String appChannel) {
            this.appChannel = appChannel;
            return this;
        }

        public Builder withAppVersion(String appVersion) {
            this.appVersion = appVersion;
            return this;
        }

        public Builder withPackageName(String packageName) {
            this.packageName = packageName;
            return this;
        }

        public BugConfig build() {
            BugConfig bugConfig = new BugConfig();
            bugConfig.appId = appId;
            bugConfig.appChannel = appChannel;
            bugConfig.appVersion = appVersion;
            bugConfig.packageName = packageName;
            return bugConfig;
        }

    }

}
