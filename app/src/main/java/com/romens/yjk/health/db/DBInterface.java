package com.romens.yjk.health.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.romens.android.ApplicationLoader;
import com.romens.android.log.FileLog;
import com.romens.yjk.health.db.dao.CollectDataDao;
import com.romens.yjk.health.db.dao.DaoMaster;
import com.romens.yjk.health.db.dao.DaoSession;
import com.romens.yjk.health.db.dao.DiscoveryDao;
import com.romens.yjk.health.db.dao.DrugGroupDao;
import com.romens.yjk.health.db.dao.HistoryDao;
import com.romens.yjk.health.db.dao.ShopCarDao;
import com.romens.yjk.health.db.entity.DiscoveryEntity;
import com.romens.yjk.health.db.entity.DrugGroupEntity;
import com.romens.yjk.health.db.entity.HistoryEntity;
import com.romens.yjk.health.model.CollectDataEntity;
import com.romens.yjk.health.model.ShopCarEntity;
import com.romens.yjk.health.model.WeiShopEntity;

import java.util.List;

import de.greenrobot.dao.query.DeleteQuery;

public class DBInterface {
    private static DBInterface dbInterface = null;
    private DaoMaster.DevOpenHelper openHelper;
    private Context context = null;

    public static synchronized DBInterface instance() {
        if (dbInterface == null) {
            synchronized (DBInterface.class) {
                if (dbInterface == null) {
                    dbInterface = new DBInterface();
                    dbInterface.initDbHelp(ApplicationLoader.applicationContext);
                }
            }
        }
        return dbInterface;
    }

    private DBInterface() {
    }

    /**
     * 上下文环境的更新
     * 1. 环境变量的clear
     * check
     */
    public void close() {
        if (openHelper != null) {
            openHelper.close();
            openHelper = null;
            context = null;
        }
    }


    public void initDbHelp(Context ctx) {
        context = ctx;
        close();
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(ctx, "YJKHealth.db", null);
        this.openHelper = helper;
    }

    /**
     * Query for readable DB
     */
    public DaoSession openReadableDb() {
        isInitOk();
        SQLiteDatabase db = openHelper.getReadableDatabase();
        DaoMaster daoMaster = new DaoMaster(db);
        DaoSession daoSession = daoMaster.newSession();
        return daoSession;
    }

    /**
     * Query for writable DB
     */
    public DaoSession openWritableDb() {
        isInitOk();
        SQLiteDatabase db = openHelper.getWritableDatabase();
        DaoMaster daoMaster = new DaoMaster(db);
        DaoSession daoSession = daoMaster.newSession();
        return daoSession;
    }


    private void isInitOk() {
        if (openHelper == null) {
            FileLog.e("DBInterface", "DBInterface#isInit not success or start,cause by openHelper is null");
            // 抛出异常 todo
            throw new RuntimeException("DBInterface#isInit not success or start,cause by openHelper is null");
        }
    }


    /**
     * -------------------------下面开始department 操作相关---------------------------------------
     */
    public void batchInsertOrUpdateDepart(List<DiscoveryEntity> entityList) {
        if (entityList.size() <= 0) {
            return;
        }
        DiscoveryDao dao = openWritableDb().getDiscoveryDao();
        dao.insertOrReplaceInTx(entityList);
    }

    /**
     * update
     */
    public int getDiscoveryDataLastTime() {
        DiscoveryDao dao = openReadableDb().getDiscoveryDao();
        DiscoveryEntity entity = dao.queryBuilder()
                .orderDesc(DiscoveryDao.Properties.Updated)
                .limit(1)
                .unique();
        if (entity == null) {
            return 0;
        } else {
            return entity.getUpdated();
        }
    }

    //获取购物车数据上次的加载时间
    public int getShopCarDataLastTime() {
        ShopCarDao dao = openReadableDb().getShopCarDao();
        ShopCarEntity entity = dao.queryBuilder()
                .orderDesc(ShopCarDao.Properties.UpdatedTime)
                .limit(1)
                .unique();
        if (entity == null) {
            return 0;
        } else {
            return entity.getUpdatedTime();
        }
    }

    //查询购物车所有的数据
    public List<ShopCarEntity> loadAllShopCar() {
        ShopCarDao dao = openReadableDb().getShopCarDao();
        List<ShopCarEntity> result = dao.queryBuilder()
                .orderAsc(ShopCarDao.Properties.Id)
                .list();
        return result;
    }


    public int getDrugGroupDataLastTime() {
        DrugGroupDao dao = openReadableDb().getDrugGroupDao();
        DrugGroupEntity entity = dao.queryBuilder()
                .orderDesc(DrugGroupDao.Properties.Updated)
                .limit(1)
                .unique();
        if (entity == null) {
            return 0;
        } else {
            return entity.getUpdated();
        }
    }

    public List<DiscoveryEntity> loadAllDiscovery() {
        DiscoveryDao dao = openReadableDb().getDiscoveryDao();
        List<DiscoveryEntity> result = dao.queryBuilder()
                .orderAsc(DiscoveryDao.Properties.SortIndex)
                .list();
        return result;
    }

    public List<DiscoveryEntity> loadHomeCoverDiscovery() {
        DiscoveryDao dao = openReadableDb().getDiscoveryDao();
        List<DiscoveryEntity> result = dao.queryBuilder()
                .where(DiscoveryDao.Properties.IsCover.eq(1))
                .orderDesc(DiscoveryDao.Properties.Id)
                .list();
        return result;
    }

    public void deleteDiscovery(String key) {
        DiscoveryDao dao = openWritableDb().getDiscoveryDao();
        DeleteQuery<DiscoveryEntity> bd = dao.queryBuilder()
                .where(DiscoveryDao.Properties.Key.eq(key))
                .buildDelete();
        bd.executeDeleteWithoutDetachingEntities();
    }

    public List<DrugGroupEntity> loadAllDrugGroup() {
        DrugGroupDao dao = openReadableDb().getDrugGroupDao();
        List<DrugGroupEntity> result = dao.queryBuilder()
                .orderAsc(DrugGroupDao.Properties.SortIndex)
                .list();
        return result;
    }

    //查询浏览历史里面的所有的数据
    public List<HistoryEntity> loadAllHistory() {
        HistoryDao dao = openReadableDb().getHistoryDao();
        List<HistoryEntity> result = dao.queryBuilder()
                .orderAsc(HistoryDao.Properties.Id)
                .list();
        return result;
    }

    //Determine whether or not
    public boolean getFavorite(String guid) {
        CollectDataDao dataDao = openReadableDb().getCollectDataDao();
        List<CollectDataEntity> list = dataDao.queryBuilder().orderAsc(CollectDataDao.Properties.Id).list();
        for (int i = 0; i < list.size(); i++) {
            CollectDataEntity collectDataEntity = list.get(i);
            if (guid.equals(collectDataEntity.getMerchandiseId())) {
                return true;
            }
        }
        return false;
    }

    public void InsertToCollect(WeiShopEntity shopEntity) {
        CollectDataEntity collectDataEntity = new CollectDataEntity();
        collectDataEntity.setMerchandiseId(shopEntity.getGUID());
        collectDataEntity.setMedicineName(shopEntity.getNAME());
        collectDataEntity.setAssessCount(shopEntity.getASSESSCOUNT());
        collectDataEntity.setMedicineSpec(shopEntity.getSPEC());
        collectDataEntity.setShopName(shopEntity.getSHOPNAME());
        collectDataEntity.setShopId(shopEntity.getSHOPID());
        collectDataEntity.setPicBig(shopEntity.getURL());
        collectDataEntity.setPicSmall(shopEntity.getURL());
        collectDataEntity.setPrice(shopEntity.getUSERPRICE());
        collectDataEntity.setMemberPrice(shopEntity.getMARKETPRICE());
        collectDataEntity.setSaleCount(shopEntity.getTOTLESALEDCOUNT());
        CollectDataDao collectDataDao = openWritableDb().getCollectDataDao();
        collectDataDao.insert(collectDataEntity);
    }
    public void DeleteFavorite(WeiShopEntity shopEntity){
        CollectDataDao collectDataDao = openWritableDb().getCollectDataDao();
        DeleteQuery<CollectDataEntity> collectDataEntityDeleteQuery = collectDataDao.queryBuilder().where(CollectDataDao.Properties.MerchandiseId.eq(shopEntity.getGUID())).buildDelete();
        collectDataEntityDeleteQuery.executeDeleteWithoutDetachingEntities();
        Log.i("数据库长度----",collectDataDao.loadAll().size()+"");
    }
}
