package com.romens.yjk.health.db.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import com.romens.android.io.SerializedData;
import com.romens.yjk.health.db.entity.ShoppingCartDataEntity;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.internal.DaoConfig;

/**
 * @author Zhou Lisi
 * @create 16/2/27
 * @description
 */
public class ShoppingCartDataDao extends AbstractDao<ShoppingCartDataEntity, String> {

    public static final String TABLENAME = "SHOPPINGCARTDATA";

    /**
     * Properties of entity DepartmentEntity.<br/>
     * Can be used for QueryBuilder and for referencing column names.
     */
    public static class Properties {
        public final static Property Guid = new Property(0, String.class, "guid", true, "GUID");
        public final static Property CreateDate = new Property(1, long.class, "createDate", false, "CREATEDATE");
        public final static Property Data = new Property(2, byte[].class, "data", false, "DATA");
        public final static Property Updated = new Property(3, long.class, "updated", false, "UPDATED");
    }


    public ShoppingCartDataDao(DaoConfig config) {
        super(config);
        registerSerialize();
    }

    public ShoppingCartDataDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
        registerSerialize();
    }


    private void registerSerialize() {
        SerializedData.getInstance().register(ShoppingCartDataEntity.ClassId, ShoppingCartDataEntity.class);
    }

    /**
     * Creates the underlying database table.
     */
    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        String constraint = ifNotExists ? "IF NOT EXISTS " : "";
        db.execSQL("CREATE TABLE " + constraint + "'" + TABLENAME + "' (" +
                "'GUID' TEXT PRIMARY KEY," +
                "'CREATEDATE' TEXT NOT NULL," +
                "'DATA' BLOB ," +
                "'UPDATED' INTEGER NOT NULL );");
        // Add Indexes
        db.execSQL("CREATE INDEX " + constraint + "IDX_SHOPPINGCARTDATA_CREATEDATE ON SHOPPINGCARTDATA" +
                " (CREATEDATE);");
        db.execSQL("CREATE INDEX " + constraint + "IDX_SHOPPINGCARTDATA_UPDATED ON SHOPPINGCARTDATA" +
                " (UPDATED);");
    }

    /**
     * Drops the underlying database table.
     */
    public static void dropTable(SQLiteDatabase db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "'" + TABLENAME + "'";
        db.execSQL(sql);
    }

    public static void upgradeTable(SQLiteDatabase db, int oldVersion, int newVersion) {
        dropTable(db, true);
        createTable(db, false);
    }

    /**
     * @inheritdoc
     */
    @Override
    protected void bindValues(SQLiteStatement stmt, ShoppingCartDataEntity entity) {
        stmt.clearBindings();

        String id = entity.getGuid();
        if (id != null) {
            stmt.bindString(1, id);
        }
        stmt.bindLong(2, entity.getCrated());
        stmt.bindBlob(3, SerializedData.getInstance().toBytes(entity));
        stmt.bindLong(4, entity.getUpdated());
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
    public ShoppingCartDataEntity readEntity(Cursor cursor, int offset) {
        ShoppingCartDataEntity entity = (ShoppingCartDataEntity) SerializedData.getInstance().toEntity(ShoppingCartDataEntity.ClassId, cursor.getBlob(offset + 2));
        return entity;
    }

    /**
     * @inheritdoc
     */
    @Override
    public void readEntity(Cursor cursor, ShoppingCartDataEntity entity, int offset) {
        entity = (ShoppingCartDataEntity) SerializedData.getInstance().toEntity(ShoppingCartDataEntity.ClassId, cursor.getBlob(offset + 2));
    }

    /**
     * @inheritdoc
     */
    @Override
    protected String updateKeyAfterInsert(ShoppingCartDataEntity entity, long rowId) {
        return entity.getGuid();
    }

    /**
     * @inheritdoc
     */
    @Override
    public String getKey(ShoppingCartDataEntity entity) {
        if (entity != null) {
            return entity.getGuid();
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
