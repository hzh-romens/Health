package com.romens.yjk.health.config;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.text.TextUtils;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.romens.android.common.UniqueCode;
import com.romens.android.network.Message;
import com.romens.android.network.parser.JSONNodeParser;
import com.romens.android.network.protocol.FacadeProtocol;
import com.romens.android.network.protocol.ResponseProtocol;
import com.romens.android.network.request.Connect;
import com.romens.android.network.request.ConnectManager;
import com.romens.android.network.request.RMConnect;
import com.romens.yjk.health.MyApplication;
import com.romens.yjk.health.core.AppHelper;
import com.romens.yjk.health.core.AppNotificationCenter;
import com.romens.yjk.health.helper.AESHelper;
import com.romens.yjk.health.helper.Base64Helper;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by siery on 15/9/21.
 */
public class AppConfig {
    private static boolean isLoading = false;
    private static final Map<String, String> config = new HashMap<>();
    private final static Object sync = new Object();

    private static boolean isInitHXSDK = false;

    public static void initHXSDK() {
//        synchronized (sync) {
//            /**
//             * this function will initialize the HuanXin SDK
//             *
//             * @return boolean true if caller can continue to call HuanXin related APIs after calling onInit, otherwise false.
//             *
//             * 环信初始化SDK帮助函数
//             * 返回true如果正确初始化，否则false，如果返回为false，请在后续的调用中不要调用任何和环信相关的代码
//             *
//             * for example:
//             * 例子：
//             *
//             * public class DemoHXSDKHelper extends HXSDKHelper
//             *
//             * HXHelper = new DemoHXSDKHelper();
//             * if(HXHelper.onInit(context)){
//             *     // do HuanXin related work
//             * }
//             */
//            if (isInitHXSDK) {
//                return;
//            }
//            String hxAppId = getHXId();
//            hxAppId = "romens#yjkim888888";
//            if (!TextUtils.isEmpty(hxAppId)) {
//                EMChat.getInstance().setAppkey(hxAppId);
//                //IMHXSDKHelper.getInstance().onInit();
//                isInitHXSDK = true;
//            }
//        }
    }

    public static boolean isLoading() {
        return isLoading;
    }

    public static String getHXId() {
        if (config.containsKey("HXAPPKEY")) {
            return config.get("HXAPPKEY");
        }
        return null;
    }

    public static void loadConfig() {
        synchronized (sync) {
            if (isLoading) {
                return;
            }
            isLoading = true;
            SharedPreferences sharedPreferences = MyApplication.applicationContext.getSharedPreferences("app_config", Context.MODE_PRIVATE);
            long lastTime = sharedPreferences.getLong("LastTime", 0);
            Map<String, Object> args = new HashMap<>();
            args.put("LASTTIME", lastTime);
            args.put("ORGCODE", UserConfig.getInstance().getOrgCode());
            FacadeProtocol protocol = new FacadeProtocol(FacadeConfig.getUrl(), "UnHandle", "getAppConfig", args);
            protocol.withToken(FacadeToken.getInstance().getAuthToken());
            Connect connect = new RMConnect.Builder(AppConfig.class)
                    .withProtocol(protocol)
                    .withParser(new JSONNodeParser())
                    .withDelegate(new Connect.AckDelegate() {
                        @Override
                        public void onResult(Message message, Message errorMessage) {
                            isLoading = false;
                            if (errorMessage == null) {
                                ResponseProtocol<JsonNode> responseProtocol = (ResponseProtocol) message.protocol;
                                JsonNode jsonObject = responseProtocol.getResponse();
                                long newLastTime = jsonObject.get("LASTTIME").asLong();
                                JsonNode dataJsonArray = jsonObject.get("DATA");
                                Map<String, String> params = new HashMap<String, String>();
                                String keyTemp;
                                if (dataJsonArray != null && dataJsonArray.size() > 0) {
                                    JsonNode dataJsonObject = dataJsonArray.get(0);
                                    if (dataJsonObject.size() > 0) {
                                        Iterator<String> keys = dataJsonObject.fieldNames();
                                        while (keys.hasNext()) {
                                            keyTemp = keys.next();
                                            params.put(keyTemp, dataJsonObject.get(keyTemp).asText());
                                        }
                                    }
                                }
                                saveConfig(newLastTime, params);
                                return;
                            }
                            loadCache();
                        }
                    }).build();
            ConnectManager.getInstance().request(MyApplication.applicationContext, connect);
        }
    }

    static void loadCache() {
        SharedPreferences sharedPreferences = MyApplication.applicationContext.getSharedPreferences("app_config", Context.MODE_PRIVATE);
        String paramsSecret = sharedPreferences.getString("Config", "");
        Map<String, String> params = decodeConfig(paramsSecret);
        config.clear();
        if (params != null && params.size() > 0) {
            config.putAll(params);
        }
        initHXSDK();
        AppNotificationCenter.getInstance().postNotificationName(AppNotificationCenter.appConfigLoaded);
    }

    static void saveConfig(long lastTime, Map<String, String> params) {
        SharedPreferences sharedPreferences = MyApplication.applicationContext.getSharedPreferences("app_config", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putLong("LastTime", lastTime);
        String paramsSecret = encodeConfig(params);
        editor.putString("Config", paramsSecret);
        boolean isCommit = editor.commit();
        if (isCommit) {
            config.clear();
            config.putAll(params);
            initHXSDK();
            AppNotificationCenter.getInstance().postNotificationName(AppNotificationCenter.appConfigChanged);
        }
    }

    static String encodeConfig(Map<String, String> params) {
        String key = getID();
        String paramsSecret = null;
        if (!TextUtils.isEmpty(key)) {
            Gson gson = new Gson();
            paramsSecret = gson.toJson(params);
            paramsSecret = Base64Helper.encodeBase64String(paramsSecret);
            try {
                paramsSecret = AESHelper.encrypt(key, paramsSecret);
            } catch (Exception e) {
                paramsSecret = null;
            }
        }
        return paramsSecret;
    }

    static Map<String, String> decodeConfig(String paramsSecret) {
        String key = getID();
        if (!TextUtils.isEmpty(key)) {
            String paramsStr = null;
            try {
                paramsStr = AESHelper.decrypt(key, paramsSecret);
            } catch (Exception e) {
            }
            if (!TextUtils.isEmpty(paramsStr)) {
                paramsStr = Base64Helper.decodeBase64(paramsStr);
                Gson gson = new Gson();
                Map<String, String> params = gson.fromJson(paramsStr, new TypeToken<Map<String, String>>() {
                }.getType());
                return params;
            }
        }
        return null;
    }

    public static String getID() {
        return UniqueCode.create(MyApplication.applicationContext);
    }

    static String getAppKey() {
        String packageName = MyApplication.applicationContext.getPackageName();
        PackageManager packageManager = MyApplication.applicationContext.getPackageManager();
        PackageInfo packageInfo = null;
        try {
            packageInfo = packageManager.getPackageInfo(packageName, PackageManager.GET_SIGNATURES);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        String key = null;
        if (packageInfo != null) {
            key = AppHelper.getPublicKeyString(packageInfo);
        }
        return key;
    }


}
