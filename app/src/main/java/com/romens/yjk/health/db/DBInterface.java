package com.romens.yjk.health.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.romens.android.ApplicationLoader;
import com.romens.android.log.FileLog;
import com.romens.yjk.health.common.OrderStatus;
import com.romens.yjk.health.db.dao.DaoMaster;
import com.romens.yjk.health.db.dao.DaoSession;
import com.romens.yjk.health.db.dao.DiscoveryDao;
import com.romens.yjk.health.db.dao.DrugGroupDao;
import com.romens.yjk.health.db.dao.FavoritesDao;
import com.romens.yjk.health.db.dao.HistoryDao;
import com.romens.yjk.health.db.dao.OrderDao;
import com.romens.yjk.health.db.dao.PushMessageDao;
import com.romens.yjk.health.db.dao.ShopCarDao;
import com.romens.yjk.health.db.dao.ShoppingCartDataDao;
import com.romens.yjk.health.db.entity.DiscoveryEntity;
import com.romens.yjk.health.db.entity.DrugGroupEntity;
import com.romens.yjk.health.db.entity.FavoritesEntity;
import com.romens.yjk.health.db.entity.HistoryEntity;
import com.romens.yjk.health.db.entity.OrderEntity;
import com.romens.yjk.health.db.entity.PushMessageEntity;
import com.romens.yjk.health.db.entity.ShoppingCartDataEntity;
import com.romens.yjk.health.model.ShopCarEntity;

import java.util.ArrayList;
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

    public long getFavoritesDataLastTime() {
        FavoritesDao dao = openReadableDb().getFavoritesDao();
        FavoritesEntity entity = dao.queryBuilder()
                .orderDesc(FavoritesDao.Properties.Updated)
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
    public boolean isFavorite(String medicineGuid) {
        FavoritesDao dataDao = openReadableDb().getFavoritesDao();
        List<FavoritesEntity> entities = dataDao.queryBuilder()
                .where(FavoritesDao.Properties.MerchandiseId.eq(medicineGuid))
                .orderAsc(FavoritesDao.Properties.Updated).list();
        if (entities != null && entities.size() > 0) {
            return true;
        }
        return false;
    }

    public void removeFavoriteFromDB(String medicineGuid) {
        FavoritesDao favoritesDao = openWritableDb().getFavoritesDao();
        favoritesDao.queryBuilder()
                .where(FavoritesDao.Properties.MerchandiseId.eq(medicineGuid))
                .buildDelete()
                .executeDeleteWithoutDetachingEntities();
    }

    public void removeFavoriteFromDB(List<String> medicineGuid) {
        FavoritesDao favoritesDao = openWritableDb().getFavoritesDao();
        favoritesDao.queryBuilder()
                .where(FavoritesDao.Properties.MerchandiseId.in(medicineGuid))
                .buildDelete()
                .executeDeleteWithoutDetachingEntities();
    }


    /**
     * 获取当前本地购物车
     *
     * @return
     */
    public List<ShoppingCartDataEntity> getCurrClientShoppingCart() {
        ShoppingCartDataDao dao = openReadableDb().getShoppingCartDataDao();
        List<ShoppingCartDataEntity> entities = dao.queryBuilder()
                .orderAsc(ShoppingCartDataDao.Properties.CreateDate).list();
        return entities;
    }

    public void syncClientShoppingCart(List<ShoppingCartDataEntity> list) {
        ShoppingCartDataDao dao = openWritableDb().getShoppingCartDataDao();
        dao.deleteAll();
        if (list != null && list.size() > 0) {
            dao.insertOrReplaceInTx(list);
        }
    }

    public void updateShoppingCartCount(ShoppingCartDataEntity entity) {
        ShoppingCartDataDao dao = openWritableDb().getShoppingCartDataDao();
        dao.insertOrReplace(entity);
    }

    public void deleteShoppingCartGoods(String... keys) {
        ShoppingCartDataDao dao = openWritableDb().getShoppingCartDataDao();
        dao.deleteByKeyInTx(keys);
    }

    public void deleteShoppingCartGoods(List<String> entity) {
        ShoppingCartDataDao dao = openWritableDb().getShoppingCartDataDao();
        dao.deleteByKeyInTx(entity);
    }

    public long getClientShoppingCartUpdated() {
        ShoppingCartDataDao dao = openReadableDb().getShoppingCartDataDao();
        ShoppingCartDataEntity entity = dao.queryBuilder()
                .orderDesc(ShoppingCartDataDao.Properties.Updated)
                .limit(1)
                .unique();
        if (entity == null) {
            return 0;
        } else {
            return entity.getUpdated();
        }
    }

    public List<ShoppingCartDataEntity> findShoppingCartData(ArrayList<String> ids) {
        ShoppingCartDataDao dao = openReadableDb().getShoppingCartDataDao();
        List<ShoppingCartDataEntity> entities = dao.queryBuilder().where(ShoppingCartDataDao.Properties.Guid.in(ids))
                .orderDesc(ShoppingCartDataDao.Properties.CreateDate)
                .list();
        return entities;
    }

    public Long getOrderSyncLastTime() {
        OrderDao dao = openReadableDb().getOrderDataDao();
        OrderEntity entity = dao.queryBuilder()
                .orderDesc(OrderDao.Properties.Updated)
                .limit(1)
                .unique();
        if (entity != null) {
            return entity.updated;
        }
        return 0l;
    }

    public List<OrderEntity> loadOrderData(int orderStatus) {
        OrderDao orderDao = DBInterface.instance().openReadableDb().getOrderDataDao();
        List<OrderEntity> orderEntities;
        if (orderStatus == OrderStatus.ALL) {
            orderEntities = orderDao.queryBuilder()
                    .orderDesc(OrderDao.Properties.Created)
                    .list();
        } else if (orderStatus == OrderStatus.NO_COMPLETED) {
            //待处理
            orderEntities = orderDao.queryBuilder()
                    .where(OrderDao.Properties.Status.in("2", "4", "5", "8", "13"))
                    .orderDesc(OrderDao.Properties.Created)
                    .list();
        } else if (orderStatus == OrderStatus.COMPLETED) {
            //已完成
            orderEntities = orderDao.queryBuilder()
                    .where(OrderDao.Properties.Status.eq("11"))
                    .orderDesc(OrderDao.Properties.Created)
                    .list();
        } else if (orderStatus == OrderStatus.COMMIT) {
            //已完成
            orderEntities = orderDao.queryBuilder()
                    .where(OrderDao.Properties.Status.eq("16"))
                    .orderDesc(OrderDao.Properties.Created)
                    .list();
        } else {
            orderEntities = new ArrayList<>();
        }
        return orderEntities;
    }

    public void savePushMessage(PushMessageEntity messageEntity) {
        PushMessageDao dao = openWritableDb().getPushMessageDao();
        dao.insertOrReplaceInTx(messageEntity);
    }

    public List<PushMessageEntity> loadLocalPushMessage() {
        PushMessageDao dao = openReadableDb().getPushMessageDao();
        List<PushMessageEntity> pushMessageEntities = dao.queryBuilder()
                .orderDesc(PushMessageDao.Properties.CreateDate)
                .list();
        return pushMessageEntities;
    }

    public boolean hasUnReadPushMessage() {
        PushMessageDao dao = openReadableDb().getPushMessageDao();
        long count = dao.queryBuilder().where(PushMessageDao.Properties.State.eq(1)).buildCount().count();
        return count > 0;
    }
}
