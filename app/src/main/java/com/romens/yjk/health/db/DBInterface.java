package com.romens.yjk.health.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.romens.android.ApplicationLoader;
import com.romens.android.log.FileLog;
import com.romens.yjk.health.db.dao.DaoMaster;
import com.romens.yjk.health.db.dao.DaoSession;
import com.romens.yjk.health.db.dao.DiscoveryDao;
import com.romens.yjk.health.db.dao.DrugGroupDao;
import com.romens.yjk.health.db.entity.DiscoveryEntity;
import com.romens.yjk.health.db.entity.DrugGroupEntity;

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
}
