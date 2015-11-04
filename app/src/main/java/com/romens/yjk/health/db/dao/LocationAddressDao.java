package com.romens.yjk.health.db.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import com.romens.yjk.health.db.entity.LocationAddressEntity;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.internal.DaoConfig;

/**
 * Created by siery on 15/8/20.
 */
public class LocationAddressDao extends AbstractDao<LocationAddressEntity, String> {

    public static final String TABLENAME = "LocationAddress";

    /**
     * Properties of entity DiscoveryEntity.<br/>
     * Can be used for QueryBuilder and for referencing column names.
     */
    public static class Properties {
        public final static Property Key = new Property(0, String.class, "key", true, "KEY");
        public final static Property ParentId = new Property(1, String.class, "parentId", false, "PARENT_ID");
        public final static Property Name = new Property(2, String.class, "name", false, "NAME");
    }


    public LocationAddressDao(DaoConfig config) {
        super(config);
    }

    public LocationAddressDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /**
     * Creates the underlying database table.
     */
    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        String constraint = ifNotExists ? "IF NOT EXISTS " : "";
        db.execSQL("CREATE TABLE " + constraint + "'" + TABLENAME + "' (" +
                "'KEY' TEXT PRIMARY KEY ," +
                "'PARENT_ID' TEXT," +
                "'NAME' TEXT NOT NULL  );");
        // Add Indexes
        db.execSQL("CREATE INDEX IDX_LocationAddress_KEY ON " + TABLENAME +
                " (KEY);");
        db.execSQL("CREATE INDEX IDX_LocationAddress_PARENT_ID ON " + TABLENAME +
                " (PARENT_ID);");

        initCreatedTableData(db);
    }

    public static void upgradeTable(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion <= 19) {
            dropTable(db, false);
            createTable(db, false);
        }
    }

    public static void initCreatedTableData(SQLiteDatabase db) {
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
    protected void bindValues(SQLiteStatement stmt, LocationAddressEntity entity) {
        stmt.clearBindings();
        stmt.bindString(1, entity.getKey());
        stmt.bindString(2, entity.getParentId());
        stmt.bindString(3, entity.getName());
    }

    /**
     * @inheritdoc
     */
    @Override
    public String readKey(Cursor cursor, int offset) {
        return cursor.getString(offset + 0);
    }

    /**
     * @inheritdoc
     */
    @Override
    public LocationAddressEntity readEntity(Cursor cursor, int offset) {
        LocationAddressEntity entity = new LocationAddressEntity();
        entity.setKey(cursor.getString(offset + 0));
        entity.setParentId(cursor.getString(offset + 1));
        entity.setName(cursor.getString(offset + 2));
        return entity;
    }

    /**
     * @inheritdoc
     */
    @Override
    public void readEntity(Cursor cursor, LocationAddressEntity entity, int offset) {
        entity.setKey(cursor.getString(offset + 0));
        entity.setParentId(cursor.getString(offset + 1));
        entity.setName(cursor.getString(offset + 2));
    }

    /**
     * @inheritdoc
     */
    @Override
    protected String updateKeyAfterInsert(LocationAddressEntity entity, long rowId) {
        return entity.getKey();
    }

    /**
     * @inheritdoc
     */
    @Override
    public String getKey(LocationAddressEntity entity) {
        if (entity != null) {
            return entity.getKey();
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
