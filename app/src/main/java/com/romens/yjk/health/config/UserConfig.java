package com.romens.yjk.health.config;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.text.TextUtils;
import android.util.Base64;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.romens.yjk.health.MyApplication;
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

    private static AppChannel appChannel;
    private static Data config;

    public static String getOrgCode() {
        return config == null ? null : config.orgCode;
    }

    public static String getOrgName() {
        return config == null ? null : config.orgName;
    }

    public static String getPassCode() {
        return config == null ? null : config.token;
    }

    public static String createToken() {
        if (config == null) {
            return null;
        }
        final String token = config.token;
        String md5Token = TextUtils.isEmpty(token) ? "" : MD5Helper.createMD5(token + "0");
        md5Token = String.format("%s|@%s|@%s", config.orgCode, config.userName, md5Token);
        md5Token = Base64Helper.encodeBase64String(md5Token);
        return md5Token;
    }

    public static boolean isClientActivated() {
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

    public static String getClientUserId() {
        synchronized (sync) {
            return config != null ? config.userName : null;
        }
    }

    public static String getClientUserPhone() {
        synchronized (sync) {
            return config != null ? config.phoneNumber : null;
        }
    }

    public static Data getClientUser() {
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

    public static void clearUserToken() {
        config.clearToken();
        boolean isCommit = saveConfig(config);
        if (isCommit) {
            FacadeToken.getInstance().expired();
        }
    }

    public static boolean saveConfig(Data data) {
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
                String json = new Gson().toJson(userValues);
                byte[] jsonBytes = json.getBytes(Charset.forName("utf-8"));
                String userString = Base64.encodeToString(jsonBytes, Base64.DEFAULT);
                editor.putString(PREFERENCE_KEY_USER, userString);
            }
            return editor.commit();
        }
    }

    public static void loadConfig() {
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
                data.setOrg(orgCode, orgName);
                data.setPhoneNumber(phoneNumber);
                data.setLogin(userName, token);
                config = data;
            }
        }
    }

    public static UserEntity getClientUserEntity() {
        UserEntity userEntity = null;
        if (isClientLogined()) {
            userEntity = new UserEntity(0, "", config.userName, "", config.phoneNumber, "", "", 0);
        }
        return userEntity;
    }

    public static class Data {
        protected String orgCode;
        protected String orgName;

        protected String phoneNumber;
        protected String userName;
        protected String token;

        public void setOrg(String code, String name) {
            this.orgCode = code == null ? "" : code;
            this.orgName = name == null ? "" : name;
        }

        public void setLogin(String name, String token) {
            this.userName = name == null ? "" : name;
            this.token = token == null ? "" : token;
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
        //packageName and appName
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
}
