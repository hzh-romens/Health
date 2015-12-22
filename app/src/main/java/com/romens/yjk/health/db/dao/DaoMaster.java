package com.romens.yjk.health.db.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import de.greenrobot.dao.AbstractDaoMaster;
import de.greenrobot.dao.identityscope.IdentityScopeType;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.

/**
 * Master of DAO (schema version 12): knows all DAOs.
*/
public class DaoMaster extends AbstractDaoMaster {
    public static final int SCHEMA_VERSION = 39;

    /** Creates underlying database table using DAOs. */
    public static void createAllTables(SQLiteDatabase db, boolean ifNotExists) {
        DataCacheDao.createTable(db,ifNotExists);
        DiscoveryDao.createTable(db, ifNotExists);
        DrugGroupDao.createTable(db,ifNotExists);
        LocationAddressDao.createTable(db,ifNotExists);
        RemindDao.createTable(db,ifNotExists);
        SearchHistoryDao.createTable(db, ifNotExists);
        EatDrugUserDao.createTable(db, ifNotExists);
        ShopCarDao.createTable(db,ifNotExists);
        CitysDao.createTable(db,ifNotExists);
        FamilyMemberDao.createTable(db, ifNotExists);
        FamilyDrugGroupDao.createTable(db, ifNotExists);
        HistoryDao.createTable(db, ifNotExists);
        FavoritesDao.createTable(db, ifNotExists);
    }
    
    /** Drops underlying database table using DAOs. */
    public static void dropAllTables(SQLiteDatabase db, boolean ifExists) {
        DataCacheDao.dropTable(db, ifExists);
        DiscoveryDao.dropTable(db, ifExists);
        DrugGroupDao.dropTable(db,ifExists);
        LocationAddressDao.dropTable(db,ifExists);
        RemindDao.dropTable(db,ifExists);
        SearchHistoryDao.dropTable(db, ifExists);
        EatDrugUserDao.dropTable(db, ifExists);
        ShopCarDao.dropTable(db,ifExists);
        CitysDao.dropTable(db, ifExists);
        FamilyMemberDao.dropTable(db,ifExists);
        FamilyDrugGroupDao.dropTable(db,ifExists);
        HistoryDao.dropTable(db,ifExists);
        FavoritesDao.dropTable(db, ifExists);
    }

    public static void upgradeAllTables(SQLiteDatabase db, int oldVersion, int newVersion){
        DataCacheDao.upgradeTable(db, oldVersion, newVersion);
        DiscoveryDao.upgradeTable(db, oldVersion, newVersion);
        DrugGroupDao.upgradeTable(db,oldVersion,newVersion);
        LocationAddressDao.upgradeTable(db,oldVersion,newVersion);
        RemindDao.upgradeTable(db,oldVersion,newVersion);
        SearchHistoryDao.upgradeTable(db, oldVersion, newVersion);
        EatDrugUserDao.upgradeTable(db, oldVersion, newVersion);
        ShopCarDao.upgradeTable(db,oldVersion,newVersion);
        CitysDao.upgradeTable(db,oldVersion,newVersion);
        FamilyMemberDao.upgradeTable(db, oldVersion, newVersion);
        FamilyDrugGroupDao.upgradeTable(db, oldVersion, newVersion);
        HistoryDao.upgradeTable(db, oldVersion, newVersion);
        FavoritesDao.upgradeTable(db, oldVersion, newVersion);
    }
    
    public static abstract class OpenHelper extends SQLiteOpenHelper {

        public OpenHelper(Context context, String name, CursorFactory factory) {
            super(context, name, factory, SCHEMA_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            Log.i("greenDAO", "Creating tables for schema version " + SCHEMA_VERSION);
            createAllTables(db, false);
        }
    }

    /** WARNING: Drops all table on Upgrade! Use only during development. */
    public static class DevOpenHelper extends OpenHelper {
        public DevOpenHelper(Context context, String name, CursorFactory factory) {
            super(context, name, factory);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.i("greenDAO", "Upgrading schema from version " + oldVersion + " to " + newVersion + " by dropping all tables");
            upgradeAllTables(db, oldVersion,newVersion);
        }
    }

    public DaoMaster(SQLiteDatabase db) {
        super(db, SCHEMA_VERSION);
        registerDaoClass(DataCacheDao.class);
        registerDaoClass(DiscoveryDao.class);
        registerDaoClass(DrugGroupDao.class);
        registerDaoClass(LocationAddressDao.class);
        registerDaoClass(RemindDao.class);
        registerDaoClass(SearchHistoryDao.class);
        registerDaoClass(EatDrugUserDao.class);
        registerDaoClass(ShopCarDao.class);
        registerDaoClass(CitysDao.class);
        registerDaoClass(FamilyMemberDao.class);
        registerDaoClass(FamilyDrugGroupDao.class);
        registerDaoClass(HistoryDao.class);
        registerDaoClass(FavoritesDao.class);
    }
    
    public DaoSession newSession() {
        return new DaoSession(db, IdentityScopeType.Session, daoConfigMap);
    }
    
    public DaoSession newSession(IdentityScopeType type) {
        return new DaoSession(db, type, daoConfigMap);
    }
    
}
