package com.romens.yjk.health.db.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import com.romens.erp.chain.db.entity.DataCacheEntity;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.internal.DaoConfig;

/**
 * Created by siery on 15/9/30.
 */
public class DataCacheDao extends AbstractDao<DataCacheEntity, String> {

    public static final String TABLENAME = "DataCache";

    /**
     * Properties of entity CookiesEntity.<br/>
     * Can be used for QueryBuilder and for referencing column names.
     */
    public static class Properties {
        public final static Property CacheKey = new Property(0, String.class, "cacheKey", true, "CACHEKEY");
        public final static Property CacheUpdated = new Property(1, Long.class, "cacheUpdated", false, "CACHEUPDATED");
        public final static Property CacheValidity = new Property(2, int.class, "cacheValidity", false, "CACHEVALIDITY");
    }


    public DataCacheDao(DaoConfig config) {
        super(config);
    }

    public DataCacheDao(DaoConfig config, AppDaoSession appDaoSession) {
        super(config, appDaoSession);
    }

    /**
     * Creates the underlying database table.
     */
    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        String constraint = ifNotExists ? "IF NOT EXISTS " : "";
        db.execSQL("CREATE TABLE " + constraint + "'" + TABLENAME + "' (" +
                "'CACHEKEY' TEXT PRIMARY KEY  ," +
                "'CACHEUPDATED' LONG NOT NULL," +
                "'CACHEVALIDITY' INTEGER NOT NULL );");
        // Add Indexes
        db.execSQL("CREATE INDEX " + constraint + "IDX_DataCache_CACHE_KEY ON " + TABLENAME +
                " (CACHEKEY);");
    }

    public static void upgradeTable(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    /**
     * Drops the underlying database table.
     */
    public static void dropTable(SQLiteDatabase db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "'" + TABLENAME + "'";
        db.execSQL(sql);
    }

    /**
     * @inheritdoc
     */
    @Override
    protected void bindValues(SQLiteStatement stmt, DataCacheEntity entity) {
        stmt.clearBindings();

//        Long id = entity.getId();
//        if (id != null) {
//            stmt.bindLong(1, id);
//        }
        stmt.bindString(1, entity.getCacheKey());
        stmt.bindLong(2, entity.getCacheUpdated());
        stmt.bindLong(3, entity.getCacheValidity());
    }

    /**
     * @inheritdoc
     */
    @Override
    public String readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getString(offset + 0);
    }

    /**
     * @inheritdoc
     */
    @Override
    public DataCacheEntity readEntity(Cursor cursor, int offset) {
        DataCacheEntity entity = new DataCacheEntity();
        //entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setCacheKey(cursor.getString(offset + 0));
        entity.setCacheUpdated(cursor.getLong(offset + 1));
        entity.setCacheValidity(cursor.getInt(offset + 2));
        return entity;
    }

    /**
     * @inheritdoc
     */
    @Override
    public void readEntity(Cursor cursor, DataCacheEntity entity, int offset) {
        //entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setCacheKey(cursor.getString(offset + 0));
        entity.setCacheUpdated(cursor.getLong(offset + 1));
        entity.setCacheValidity(cursor.getInt(offset + 2));
    }

    /**
     * @inheritdoc
     */
    @Override
    protected String updateKeyAfterInsert(DataCacheEntity entity, long rowId) {
       // entity.setId(rowId);
        return entity.getCacheKey();
    }

    /**
     * @inheritdoc
     */
    @Override
    public String getKey(DataCacheEntity entity) {
        if (entity != null) {
            return entity.getCacheKey();
        } else {
            return null;
        }
    }

    /**
     * @inheritdoc
     */
    @Override
    protected boolean isEntityUpdateable() {
        return true;
    }

}
