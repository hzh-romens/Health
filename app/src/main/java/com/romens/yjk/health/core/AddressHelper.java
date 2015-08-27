package com.romens.yjk.health.core;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import com.romens.android.ApplicationLoader;
import com.romens.android.log.FileLog;
import com.romens.yjk.health.R;
import com.romens.yjk.health.db.DBInterface;
import com.romens.yjk.health.db.dao.LocationAddressDao;
import com.romens.yjk.health.db.entity.LocationAddressEntity;
import com.romens.yjk.health.io.JSONHandler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by siery on 15/8/20.
 * 省市县初始化
 */
public class AddressHelper {
    public static final String PREFERENCE_KEY_NAME = "is_setup_address_location_data";

    private static Object sync = new Object();
    private static Thread dataLocationAddressThread = null;

    public static boolean isSetupAddressLocationData() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(ApplicationLoader.applicationContext);
        boolean value = sharedPreferences.getBoolean(PREFERENCE_KEY_NAME, false);
        return value;
    }

    public static void completedSetupAddressLocationData() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(ApplicationLoader.applicationContext);
        sharedPreferences.edit().putBoolean(PREFERENCE_KEY_NAME, true);
    }

    public static void failedSetupAddressLocationData() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(ApplicationLoader.applicationContext);
        sharedPreferences.edit().putBoolean(PREFERENCE_KEY_NAME, true);
    }

    public static void trySetupAddressLocationData() {
        if(isSetupAddressLocationData()){
            return;
        }
        synchronized (sync) {
            dataLocationAddressThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        String addressJson = JSONHandler.parseResource(ApplicationLoader.applicationContext, R.raw.address);
                        Gson gson = new Gson();
                        List<LinkedTreeMap<String, String>> data = gson.fromJson(addressJson, new TypeToken<List<LinkedTreeMap<String, String>>>() {
                        }.getType());
                        List<LocationAddressEntity> needDb = new ArrayList<>();
                        if (data != null) {
                            for (LinkedTreeMap<String, String> item : data) {
                                needDb.add(LocationAddressEntity.mapToEntity(item));
                            }
                        }
                        if (needDb.size() > 0) {
                            LocationAddressDao dao = DBInterface.instance().openWritableDb().getLocationAddressDao();
                            dao.insertOrReplaceInTx(needDb);

                        }
                        completedSetupAddressLocationData();
                    } catch (IOException e) {
                        failedSetupAddressLocationData();
                        FileLog.e("AddressHelper.setupAddressLocationData", e);
                    }
                }
            });
            dataLocationAddressThread.start();
        }
    }
}
