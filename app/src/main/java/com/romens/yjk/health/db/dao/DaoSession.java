package com.romens.yjk.health.db.dao;

import android.database.sqlite.SQLiteDatabase;

import com.romens.yjk.health.db.entity.DiscoveryEntity;
import com.romens.yjk.health.db.entity.DrugGroupEntity;
import com.romens.yjk.health.db.entity.LocationAddressEntity;

import java.util.Map;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.AbstractDaoSession;
import de.greenrobot.dao.identityscope.IdentityScopeType;
import de.greenrobot.dao.internal.DaoConfig;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.

/**
 * {@inheritDoc}
 *
 * @see AbstractDaoSession
 */
public class DaoSession extends AbstractDaoSession {

    private final DaoConfig discoveryDaoConfig;
    private final DaoConfig drugGroupDaoConfig;

    private final DiscoveryDao discoveryDao;
    private final DrugGroupDao drugGroupDao;

    private final DaoConfig locationAddressDaoConfig;
    private final LocationAddressDao locationAddressDao;

    public DaoSession(SQLiteDatabase db, IdentityScopeType type, Map<Class<? extends AbstractDao<?, ?>>, DaoConfig>
            daoConfigMap) {
        super(db);

        discoveryDaoConfig = daoConfigMap.get(DiscoveryDao.class).clone();
        discoveryDaoConfig.initIdentityScope(type);
        discoveryDao = new DiscoveryDao(discoveryDaoConfig, this);
        registerDao(DiscoveryEntity.class, discoveryDao);

        drugGroupDaoConfig = daoConfigMap.get(DrugGroupDao.class).clone();
        drugGroupDaoConfig.initIdentityScope(type);
        drugGroupDao = new DrugGroupDao(drugGroupDaoConfig, this);
        registerDao(DrugGroupEntity.class, drugGroupDao);

        locationAddressDaoConfig = daoConfigMap.get(LocationAddressDao.class).clone();
        locationAddressDaoConfig.initIdentityScope(type);
        locationAddressDao = new LocationAddressDao(locationAddressDaoConfig, this);
        registerDao(LocationAddressEntity.class, locationAddressDao);
    }

    public void clear() {
        discoveryDaoConfig.getIdentityScope().clear();
        drugGroupDaoConfig.getIdentityScope().clear();
        locationAddressDaoConfig.getIdentityScope().clear();
    }

    public DiscoveryDao getDiscoveryDao() {
        return discoveryDao;
    }

    public DrugGroupDao getDrugGroupDao() {
        return drugGroupDao;
    }

    public LocationAddressDao getLocationAddressDao() {
        return locationAddressDao;
    }

}
