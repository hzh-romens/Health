package com.romens.yjk.health.db.dao;

import android.database.sqlite.SQLiteDatabase;

import com.romens.yjk.health.db.entity.CitysEntity;
import com.romens.yjk.health.db.entity.DataCacheEntity;
import com.romens.yjk.health.db.entity.DiscoveryEntity;
import com.romens.yjk.health.db.entity.DrugGroupEntity;
import com.romens.yjk.health.db.entity.EatDrugUserEntity;
import com.romens.yjk.health.db.entity.FamilyDrugGroupEntity;
import com.romens.yjk.health.db.entity.FamilyMemberEntity;
import com.romens.yjk.health.db.entity.FavoritesEntity;
import com.romens.yjk.health.db.entity.HistoryEntity;
import com.romens.yjk.health.db.entity.LocationAddressEntity;
import com.romens.yjk.health.db.entity.OrderEntity;
import com.romens.yjk.health.db.entity.RemindEntity;
import com.romens.yjk.health.db.entity.SearchHistoryEntity;
import com.romens.yjk.health.db.entity.ShoppingCartDataEntity;
import com.romens.yjk.health.model.ShopCarEntity;

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

    private final DataCacheDao dataCacheDao;
    private final DaoConfig dataCacheDaoConfig;


    private final DaoConfig discoveryDaoConfig;
    private final DaoConfig drugGroupDaoConfig;

    private final DiscoveryDao discoveryDao;
    private final DrugGroupDao drugGroupDao;

    private final DaoConfig locationAddressDaoConfig;
    private final LocationAddressDao locationAddressDao;

    private final RemindDao remindDao;
    private final DaoConfig remindDaoConfig;

    private final SearchHistoryDao searchHistoryDao;
    private final DaoConfig searchResultDaoConfig;

    private final EatDrugUserDao eatDrugUserDao;
    private final DaoConfig eatDrugUserDaoConfig;

    private final ShopCarDao shopCarDao;
    private final DaoConfig shopCarDaoConfig;

    private final CitysDao citysDao;
    private final DaoConfig cityDaoConfig;

    private final FamilyMemberDao familyMemberDao;
    private final DaoConfig familyMemberDaoConfig;

    private final FamilyDrugGroupDao familyDrugGroupDao;
    private final DaoConfig familyDrugGroupDaoConfig;

    private final HistoryDao historyDao;
    private final DaoConfig historyDaoConfig;

    private final FavoritesDao favoritesDao;
    private final DaoConfig favoritesDataDaoConfig;

    //购物车
    private final ShoppingCartDataDao shoppingCartDataDao;
    private final DaoConfig shoppingCartDataDaoConfig;

    //订单数据
    private final OrderDao orderDataDao;
    private final DaoConfig orderDataDaoConfig;


    public DaoSession(SQLiteDatabase db, IdentityScopeType type, Map<Class<? extends AbstractDao<?, ?>>, DaoConfig>
            daoConfigMap) {
        super(db);

        dataCacheDaoConfig = daoConfigMap.get(DataCacheDao.class).clone();
        dataCacheDaoConfig.initIdentityScope(type);
        dataCacheDao = new DataCacheDao(dataCacheDaoConfig, this);
        registerDao(DataCacheEntity.class, dataCacheDao);


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

        remindDaoConfig = daoConfigMap.get(RemindDao.class).clone();
        remindDaoConfig.initIdentityScope(type);
        remindDao = new RemindDao(remindDaoConfig, this);
        registerDao(RemindEntity.class, remindDao);

        searchResultDaoConfig = daoConfigMap.get(SearchHistoryDao.class).clone();
        searchResultDaoConfig.initIdentityScope(type);
        searchHistoryDao = new SearchHistoryDao(searchResultDaoConfig, this);
        registerDao(SearchHistoryEntity.class, searchHistoryDao);

        eatDrugUserDaoConfig = daoConfigMap.get(EatDrugUserDao.class).clone();
        eatDrugUserDaoConfig.initIdentityScope(type);
        eatDrugUserDao = new EatDrugUserDao(eatDrugUserDaoConfig, this);
        registerDao(EatDrugUserEntity.class, eatDrugUserDao);

        shopCarDaoConfig = daoConfigMap.get(ShopCarDao.class).clone();
        shopCarDaoConfig.initIdentityScope(type);
        shopCarDao = new ShopCarDao(shopCarDaoConfig, this);
        registerDao(ShopCarEntity.class, shopCarDao);

        cityDaoConfig = daoConfigMap.get(CitysDao.class).clone();
        cityDaoConfig.initIdentityScope(type);
        citysDao = new CitysDao(cityDaoConfig, this);
        registerDao(CitysEntity.class, citysDao);

        familyMemberDaoConfig = daoConfigMap.get(FamilyMemberDao.class).clone();
        familyMemberDaoConfig.initIdentityScope(type);
        familyMemberDao = new FamilyMemberDao(familyMemberDaoConfig, this);
        registerDao(FamilyMemberEntity.class, familyMemberDao);

        familyDrugGroupDaoConfig = daoConfigMap.get(FamilyDrugGroupDao.class).clone();
        familyDrugGroupDaoConfig.initIdentityScope(type);
        familyDrugGroupDao = new FamilyDrugGroupDao(familyDrugGroupDaoConfig, this);
        registerDao(FamilyDrugGroupEntity.class, familyDrugGroupDao);

        historyDaoConfig = daoConfigMap.get(HistoryDao.class).clone();
        historyDaoConfig.initIdentityScope(type);
        historyDao = new HistoryDao(historyDaoConfig, this);
        registerDao(HistoryEntity.class, historyDao);

        favoritesDataDaoConfig = daoConfigMap.get(FavoritesDao.class).clone();
        favoritesDataDaoConfig.initIdentityScope(type);
        favoritesDao = new FavoritesDao(favoritesDataDaoConfig, this);
        registerDao(FavoritesEntity.class, favoritesDao);

        //购物车
        shoppingCartDataDaoConfig = daoConfigMap.get(ShoppingCartDataDao.class).clone();
        shoppingCartDataDaoConfig.initIdentityScope(type);
        shoppingCartDataDao = new ShoppingCartDataDao(shoppingCartDataDaoConfig, this);
        registerDao(ShoppingCartDataEntity.class, shoppingCartDataDao);

        //订单

        orderDataDaoConfig = daoConfigMap.get(OrderDao.class).clone();
        orderDataDaoConfig.initIdentityScope(type);
        orderDataDao = new OrderDao(orderDataDaoConfig, this);
        registerDao(OrderEntity.class, orderDataDao);
    }

    public void clear() {
        dataCacheDaoConfig.getIdentityScope().clear();
        discoveryDaoConfig.getIdentityScope().clear();
        drugGroupDaoConfig.getIdentityScope().clear();
        locationAddressDaoConfig.getIdentityScope().clear();
        remindDaoConfig.getIdentityScope().clear();
        searchResultDaoConfig.getIdentityScope().clear();
        eatDrugUserDaoConfig.getIdentityScope().clear();
        shopCarDaoConfig.getIdentityScope().clear();
        cityDaoConfig.getIdentityScope().clear();
        familyMemberDaoConfig.getIdentityScope().clear();
        familyDrugGroupDaoConfig.getIdentityScope().clear();
        historyDaoConfig.getIdentityScope().clear();
        favoritesDataDaoConfig.getIdentityScope().clear();

        //购物车
        shoppingCartDataDaoConfig.getIdentityScope().clear();

        orderDataDaoConfig.getIdentityScope().clear();
    }

    public DataCacheDao getDataCacheDao() {
        return dataCacheDao;
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

    public RemindDao getRemindDao() {
        return remindDao;
    }

    public SearchHistoryDao getSearchHistoryDao() {
        return searchHistoryDao;
    }

    public EatDrugUserDao getEatDrugUserDao() {
        return eatDrugUserDao;
    }

    public ShopCarDao getShopCarDao() {
        return shopCarDao;
    }

    public CitysDao getCitysDao() {
        return citysDao;
    }

    public HistoryDao getHistoryDao() {
        return historyDao;
    }

    public FamilyMemberDao getFamilyMemberDao() {
        return familyMemberDao;
    }

    public FamilyDrugGroupDao getFamilyDrugGroupDao() {
        return familyDrugGroupDao;
    }

    public FavoritesDao getFavoritesDao() {
        return favoritesDao;
    }

    /**
     * 购物车
     *
     * @return
     */
    public ShoppingCartDataDao getShoppingCartDataDao() {
        return shoppingCartDataDao;
    }

    public OrderDao getOrderDataDao() {
        return orderDataDao;
    }
}
