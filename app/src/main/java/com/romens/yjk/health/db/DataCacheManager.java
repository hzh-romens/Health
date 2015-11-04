package com.romens.yjk.health.db;


import com.romens.yjk.health.db.dao.DataCacheDao;
import com.romens.yjk.health.db.entity.DataCacheEntity;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

/**
 * Created by siery on 15/10/30.
 */
public class DataCacheManager {
    private static final HashMap<String, DataCacheEntity> dataCaches = new HashMap<>();

    private static volatile DataCacheManager Instance = null;
    private static Object sync = new Object();

    public static DataCacheManager getInstance() {
        DataCacheManager localInstance = Instance;
        if (localInstance == null) {
            synchronized (DataCacheManager.class) {
                localInstance = Instance;
                if (localInstance == null) {
                    Instance = localInstance = new DataCacheManager();
                }
            }
        }
        return localInstance;
    }

    private DataCacheManager() {
        DBInterface.instance();
        loadDataCaches();
    }

    private void loadDataCaches() {
        DataCacheDao dataCacheDao = DBInterface.instance().openReadableDb().getDataCacheDao();
        List<DataCacheEntity> dataCacheEntities = dataCacheDao.loadAll();
        for (DataCacheEntity entity :
                dataCacheEntities) {
            dataCaches.put(entity.getCacheKey(), entity);
        }
    }


    public boolean hasSafeCache(String cacheKey) {
        synchronized (sync) {
            if (dataCaches.containsKey(cacheKey)) {
                DataCacheEntity dataCacheEntity = dataCaches.get(cacheKey);
                long currTime = Calendar.getInstance().getTimeInMillis();
                long cacheTime = dataCacheEntity.getCacheUpdated();
                int validity = dataCacheEntity.getCacheValidity() * 1000;
                if (validity <= 0 || (currTime - cacheTime) < validity) {
                    return true;
                }
            }
            return false;
        }
    }

    public void updateCache(String key) {
        updateCache(key, 0);
    }

    public void updateCache(String key, int validity) {
        synchronized (sync) {
            DataCacheEntity dataCacheEntity;
            if (dataCaches.containsKey(key)) {
                dataCacheEntity = dataCaches.get(key);
            } else {
                dataCacheEntity = new DataCacheEntity();
            }
            dataCacheEntity.setCacheKey(key);
            dataCacheEntity.setCacheUpdated(Calendar.getInstance().getTimeInMillis());
            dataCacheEntity.setCacheValidity(validity);

            DataCacheDao dataCacheDao = DBInterface.instance().openWritableDb().getDataCacheDao();
            dataCacheDao.insertOrReplace(dataCacheEntity);
            dataCaches.put(key, dataCacheEntity);
        }
    }
}
