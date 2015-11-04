package com.romens.yjk.health.core;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.gson.internal.LinkedTreeMap;
import com.romens.android.network.FacadeArgs;
import com.romens.android.network.FacadeClient;
import com.romens.android.network.Message;
import com.romens.android.network.parser.JsonParser;
import com.romens.android.network.protocol.FacadeProtocol;
import com.romens.android.network.protocol.ResponseProtocol;
import com.romens.yjk.health.MyApplication;
import com.romens.yjk.health.config.FacadeConfig;
import com.romens.yjk.health.config.FacadeToken;
import com.romens.yjk.health.db.DBInterface;
import com.romens.yjk.health.db.DataCacheManager;
import com.romens.yjk.health.db.dao.LocationAddressDao;
import com.romens.yjk.health.db.entity.LocationAddressEntity;
import com.romens.yjk.health.ui.activity.LocationAddressSelectActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by siery on 15/8/20.
 * 省市县初始化
 */
public class LocationAddressHelper {
    private static final String DataCacheKey = "SyncLocationAddress";
    public static final String PREFERENCE_KEY_NAME = "is_syncing_address_location_data";
    private static Object sync = new Object();

    public static final void changeSyncLocationAddressStatus(boolean syncing) {
        synchronized (sync) {
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(MyApplication.applicationContext);
            sharedPreferences.edit().putBoolean(PREFERENCE_KEY_NAME, syncing).commit();
        }
    }

    public static final boolean getSyncLocationAddressStatus() {
        synchronized (sync) {
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(MyApplication.applicationContext);
            return sharedPreferences.getBoolean(PREFERENCE_KEY_NAME, false);
        }
    }


    //private static Thread dataLocationAddressThread = null;

//    public static boolean isSetupAddressLocationData() {
//        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(ApplicationLoader.applicationContext);
//        boolean value = sharedPreferences.getBoolean(PREFERENCE_KEY_NAME, false);
//        return value;
//    }
//
//    public static void completedSetupAddressLocationData() {
//        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(ApplicationLoader.applicationContext);
//        sharedPreferences.edit().putBoolean(PREFERENCE_KEY_NAME, true);
//    }
//
//    public static void failedSetupAddressLocationData() {
//        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(ApplicationLoader.applicationContext);
//        sharedPreferences.edit().putBoolean(PREFERENCE_KEY_NAME, true);
//    }
//
//    public static void trySetupAddressLocationData() {
//        if (isSetupAddressLocationData()) {
//            return;
//        }
//        synchronized (sync) {
//            dataLocationAddressThread = new Thread(new Runnable() {
//                @Override
//                public void run() {
//                    try {
//                        String addressJson = JSONHandler.parseResource(ApplicationLoader.applicationContext, R.raw.address);
//                        Gson gson = new Gson();
//                        List<LinkedTreeMap<String, String>> data = gson.fromJson(addressJson, new TypeToken<List<LinkedTreeMap<String, String>>>() {
//                        }.getType());
//                        List<LocationAddressEntity> needDb = new ArrayList<>();
//                        if (data != null) {
//                            for (LinkedTreeMap<String, String> item : data) {
//                                needDb.add(LocationAddressEntity.cacheMapToEntity(item));
//                            }
//                        }
//                        if (needDb.size() > 0) {
//                            LocationAddressDao dao = DBInterface.instance().openWritableDb().getLocationAddressDao();
//                            dao.insertOrReplaceInTx(needDb);
//                            AppNotificationCenter.getInstance().postNotificationName(AppNotificationCenter.onLocationAddressChanged);
//                        }
//                        completedSetupAddressLocationData();
//                    } catch (IOException e) {
//                        failedSetupAddressLocationData();
//                        FileLog.e("AddressHelper.setupAddressLocationData", e);
//                    }
//                }
//            });
//            dataLocationAddressThread.start();
//        }
//    }

    public static void syncServerLocationAddress(Context context) {
        boolean enableSync = !DataCacheManager.getInstance().hasSafeCache(DataCacheKey);
        if (enableSync) {
            changeSyncLocationAddressStatus(true);
            Map<String, String> args = new FacadeArgs.MapBuilder().build();
            FacadeProtocol protocol = new FacadeProtocol(FacadeConfig.getUrl(), "UnHandle", "GetAllDistrict", args);
            protocol.withToken(FacadeToken.getInstance().getAuthToken());
            Message message = new Message.MessageBuilder()
                    .withProtocol(protocol)
                    .withParser(new JsonParser(new com.google.gson.reflect.TypeToken<List<LinkedTreeMap<String, String>>>() {
                    }))
                    .build();
            FacadeClient.request(context, message, new FacadeClient.FacadeCallback() {
                @Override
                public void onTokenTimeout(Message msg) {
                    changeSyncLocationAddressStatus(false);
                }

                @Override
                public void onResult(Message msg, Message errorMsg) {
                    changeSyncLocationAddressStatus(false);
                    if (errorMsg == null) {

                        ResponseProtocol<List<LinkedTreeMap<String, String>>> responseProtocol = (ResponseProtocol) msg.protocol;
                        List<LinkedTreeMap<String, String>> responseData = responseProtocol.getResponse();
                        List<LocationAddressEntity> needDb = new ArrayList<>();
                        if (responseData != null) {
                            for (LinkedTreeMap<String, String> item : responseData) {
                                needDb.add(LocationAddressEntity.serverMapToEntity(item));
                            }
                        }
                        if (needDb.size() > 0) {
                            DataCacheManager.getInstance().updateCache(DataCacheKey, 24 * 60 * 60);
                            LocationAddressDao dao = DBInterface.instance().openWritableDb().getLocationAddressDao();
                            dao.insertOrReplaceInTx(needDb);
                            AppNotificationCenter.getInstance().postNotificationName(AppNotificationCenter.onLocationAddressChanged);
                        }
                    }
                }
            });
        }
    }

    public static boolean openLocationAddress(Activity activity, int requestCode,String title) {
        LocationAddressDao dao = DBInterface.instance().openReadableDb().getLocationAddressDao();
        List<LocationAddressEntity> provinceData = dao.queryBuilder()
                .where(LocationAddressDao.Properties.ParentId.eq(""))
                .orderDesc(LocationAddressDao.Properties.Key)
                .list();
        if (provinceData == null || provinceData.size() <= 0) {
            return false;
        }
        ArrayList<String> nameList = new ArrayList<>();
        ArrayList<String> valueList = new ArrayList<>();
        for (LocationAddressEntity entity :
                provinceData) {
            nameList.add(entity.getName());
            valueList.add(entity.getKey());
        }
        Intent intent = new Intent(activity, LocationAddressSelectActivity.class);
        intent.putExtra(LocationAddressSelectActivity.ARGUMENTS_KEY_ADDRESS_DEEP, 0);
        intent.putExtra(LocationAddressSelectActivity.ARGUMENTS_KEY_TITLE, title);
        intent.putExtra(LocationAddressSelectActivity.ARGUMENTS_KEY_NAME_LIST, nameList);
        intent.putExtra(LocationAddressSelectActivity.ARGUMENTS_KEY_VALUE_LIST, valueList);
        intent.putExtra(LocationAddressSelectActivity.ARGUMENTS_KEY_ONLY_SELECT, false);
        activity.startActivityForResult(intent, requestCode);
        return true;
    }
}
