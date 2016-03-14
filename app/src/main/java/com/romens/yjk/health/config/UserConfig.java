package com.romens.yjk.health.config;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.text.TextUtils;
import android.util.Base64;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.romens.android.log.FileLog;
import com.romens.yjk.health.MyApplication;
import com.romens.yjk.health.core.AppNotificationCenter;
import com.romens.yjk.health.db.entity.UserEntity;
import com.romens.yjk.health.helper.Base64Helper;
import com.romens.yjk.health.helper.MD5Helper;

import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by zhoulisi on 15/6/25.
 */
public class UserConfig {
    private final static String PREFERENCE_NAME = "user_cache";
    private final static String PREFERENCE_KEY_USER = "user";
    private final static Object sync = new Object();

    private static volatile AppChannel appChannel;
    private static volatile Data config;
    private Account currentAccount;

    private static volatile UserConfig Instance = null;

    public static UserConfig getInstance() {
        UserConfig localInstance = Instance;
        if (localInstance == null) {
            synchronized (UserConfig.class) {
                localInstance = Instance;
                if (localInstance == null) {
                    Instance = localInstance = new UserConfig();
                }
            }
        }
        return localInstance;
    }

    private UserConfig() {
        appChannel = loadAppChannel();
    }

    public String getOrgCode() {
        return appChannel == null ? null : appChannel.orgCode;
    }

    public String getOrgName() {
        return appChannel == null ? null : appChannel.orgName;
    }

    public String getPassCode() {
        return config == null ? null : config.token;
    }


    public String createToken() {
        // final String token = config.token;
        //  String md5Token = TextUtils.isEmpty(token) ? "" : MD5Helper.createMD5(token + "0");
        //  md5Token = String.format("%s|@%s|@%s", config.orgCode, config.userName, md5Token);
        //md5Token = Base64Helper.encodeBase64String(md5Token);
        String md5Token;
        if (config == null) {
            md5Token = String.format("%s|@%s|@%s", appChannel.orgCode, "", "");
        } else {
            md5Token = MD5Helper.createMD5(config.token);
            md5Token = String.format("%s|@%s|@%s", appChannel.orgCode, config.userName, md5Token);
        }

        md5Token = Base64Helper.encodeBase64String(md5Token);
        return md5Token;

    }

    public boolean isClientActivated() {
        synchronized (sync) {
            SharedPreferences preferences = MyApplication.applicationContext.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
            if (config == null) {
                return false;
            }
            if (TextUtils.isEmpty(config.orgCode)) {
                return false;
            }
            if (TextUtils.isEmpty(config.phoneNumber)) {
                return false;
            }
            if (TextUtils.isEmpty(config.userName)) {
                return false;
            }
            return true;
        }
    }


    public static boolean isClientLogined() {
        synchronized (sync) {
            SharedPreferences preferences = MyApplication.applicationContext.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
            if (config == null) {
                return false;
            }
            if (TextUtils.isEmpty(config.orgCode)) {
                return false;
            }
            if (TextUtils.isEmpty(config.userName)) {
                return false;
            }
            if (TextUtils.isEmpty(config.token)) {
                return false;
            }
            return true;
        }
    }

    public String getClientUserId() {
        synchronized (sync) {
            return config != null ? config.userName : null;
        }
    }


    public String getClientUserPhone() {
        synchronized (sync) {
            return config != null ? config.phoneNumber : null;
        }
    }

    public Data getClientUser() {
        synchronized (sync) {
            return config;
        }
    }

    public static void clearConfig() {
        synchronized (sync) {
            config = null;
            SharedPreferences preferences = MyApplication.applicationContext.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.remove(PREFERENCE_KEY_USER);
            boolean isCommit = editor.commit();
            if (isCommit) {
                FacadeToken.getInstance().expired();
            }
        }
    }

    public static void clearUser() {
        synchronized (sync) {
            config = null;
            FacadeToken.getInstance().expired();
            SharedPreferences preferences = MyApplication.applicationContext.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.remove(PREFERENCE_KEY_USER);
            editor.commit();
        }
    }

    public void clearUserToken() {
        config.clearToken();
        saveConfig(config);
//        if (isCommit) {
//            FacadeToken.getInstance().expired();
//        }
        FacadeToken.getInstance().expired();
    }

    public boolean saveConfig(Data data) {
        synchronized (sync) {
            SharedPreferences preferences = MyApplication.applicationContext.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            if (data == null) {
                editor.remove(PREFERENCE_KEY_USER);
            } else {
                Map<String, String> userValues = new HashMap<>();
                userValues.put("OrgCode", data.orgCode);
                userValues.put("OrgName", data.orgName);
                userValues.put("PhoneNumber", data.phoneNumber);
                userValues.put("UserName", data.userName);
                userValues.put("Token", data.token);
                //TODO 增加UserGuid
                userValues.put("UserGuid", data.userGuid);
                String json = new Gson().toJson(userValues);
                byte[] jsonBytes = json.getBytes(Charset.forName("utf-8"));
                String userString = Base64.encodeToString(jsonBytes, Base64.DEFAULT);
                editor.putString(PREFERENCE_KEY_USER, userString);
            }
            return editor.commit();
        }
    }

    public void loadConfig() {
        synchronized (sync) {
            SharedPreferences preferences = MyApplication.applicationContext.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
            String user = preferences.getString(PREFERENCE_KEY_USER, "");
            if (TextUtils.isEmpty(user)) {
                config = null;
            } else {
                byte[] bytes = Base64.decode(user, Base64.DEFAULT);
                String json = new String(bytes, Charset.forName("utf-8"));
                Gson gson = new Gson();
                Map<String, String> userValues = gson.fromJson(json, new TypeToken<Map<String, String>>() {
                }.getType());
                Data data = new Data();
                String orgCode = userValues.get("OrgCode");
                String orgName = userValues.get("OrgName");
                String phoneNumber = userValues.get("PhoneNumber");
                String userName = userValues.get("UserName");
                String token = userValues.get("Token");
                String userGuid = userValues.get("UserGuid");
                data.setOrg(orgCode, orgName);
                data.setPhoneNumber(phoneNumber);
                data.setLogin(userName, token);
                data.setUserGuid(userGuid);
                config = data;
            }
        }
    }

    public UserEntity getClientUserEntity() {
        UserEntity userEntity = null;
        if (isClientLogined()) {
            //   userEntity = new UserEntity(0, "", config.userName, "", config.phoneNumber, "", "", 0);
            userEntity = new UserEntity(0, config.userGuid, config.userName, "", config.phoneNumber, "", "", 0);
        }
        return userEntity;
    }

    public static class Data {
        protected String orgCode;
        protected String orgName;

        protected String phoneNumber;
        //手机号码
        protected String userName;
        //登录密码
        protected String token;
        protected String userGuid;

        public void setLogin(String name, String token) {
            this.userName = name == null ? "" : name;
            this.token = token == null ? "" : token;
        }


        public void setOrg(String code, String name) {
            this.orgCode = code == null ? "" : code;
            this.orgName = name == null ? "" : name;
        }

        public void setUserGuid(String userGuid) {
            this.userGuid = userGuid == null ? "" : userGuid;
        }

        public void setPhoneNumber(String phone) {
            this.phoneNumber = phone == null ? "" : phone;
        }

        public void clearToken() {
            this.token = "";
        }
    }

    public static String formatCode(String code) {
        if (TextUtils.isEmpty(code)) {
            return "";
        }
        String md5Code = MD5Helper.createMD5(code);
        md5Code = String.format("%s0", md5Code);
        md5Code = MD5Helper.createMD5(md5Code);
        return md5Code;
    }

    public static AppChannel loadAppChannel() {
        if (appChannel == null) {
            appChannel = new AppChannel();
        }
        return appChannel;
    }

    public static class AppChannel {
        public final String orgCode;
        public final String orgName;

        public AppChannel() {
            ApplicationInfo appInfo = null;
            try {
                appInfo = MyApplication.applicationContext.getPackageManager()
                        .getApplicationInfo(MyApplication.applicationContext.getPackageName(), PackageManager.GET_META_DATA);
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
            String channel = null;
            if (appInfo != null) {
                channel = appInfo.metaData.getString("APK_CHANNEL");
            }
            if (TextUtils.isEmpty(channel)) {
                orgCode = null;
                orgName = null;
            } else {
                String[] temp = channel.split(";");
                orgCode = temp[0];
                orgName = temp[1];
            }
        }
    }


    public void checkAppAccount() {
        final String packageName = MyApplication.applicationContext.getPackageName();
        AccountManager am = AccountManager.get(MyApplication.applicationContext);
        Account[] accounts = am.getAccountsByType(packageName);
        boolean recreateAccount = false;
        if (isClientActivated()) {
            if (accounts.length == 1) {
                Account acc = accounts[0];
                if (!acc.name.equals("" + getClientUserId())) {
                    recreateAccount = true;
                } else {
                    currentAccount = acc;
                }
            } else {
                recreateAccount = true;
            }
        } else {
            if (accounts.length > 0) {
                recreateAccount = true;
            }
        }
        if (recreateAccount) {
            try {
                for (int a = 0; a < accounts.length; a++) {
                    am.removeAccount(accounts[a], null, null);
                }
            } catch (Exception e) {
                FileLog.e(e);
            }
            loadConfig();
            if (UserConfig.getInstance().isClientActivated()) {
                try {
                    currentAccount = new Account("" + getClientUserId(), packageName);
                    am.addAccountExplicitly(currentAccount, config.token, null);
                } catch (Exception e) {
                    FileLog.e(e);
                }
            }
        }
    }

    public void deleteAllAppAccounts() {
        try {
            final String packageName = MyApplication.applicationContext.getPackageName();
            AccountManager am = AccountManager.get(MyApplication.applicationContext);
            Account[] accounts = am.getAccountsByType(packageName);
            for (int a = 0; a < accounts.length; a++) {
                am.removeAccount(accounts[a], null, null);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
