package com.romens.yjk.health.db.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import com.romens.android.io.SerializedData;
import com.romens.yjk.health.db.entity.OrderEntity;
import com.romens.yjk.health.db.entity.ShoppingCartDataEntity;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.internal.DaoConfig;

/**
 * @author Zhou Lisi
 * @create 2016-03-29 23:56
 * @description
 */
public class OrderDao extends AbstractDao<OrderEntity, String> {

    public static final String TABLENAME = "ORDER_CACHE_DATA";

    /**
     * Properties of entity DepartmentEntity.<br/>
     * Can be used for QueryBuilder and for referencing column names.
     */
    public static class Properties {
        public final static Property Id = new Property(0, String.class, "id", true, "ID");
        public final static Property Created = new Property(1, long.class, "created", false, "CREATED");
        public final static Property Status = new Property(2, String.class, "status", false, "STATUS");
        public final static Property Data = new Property(3, byte[].class, "data", false, "DATA");
        public final static Property Updated = new Property(4, long.class, "updated", false, "UPDATED");
    }


    public OrderDao(DaoConfig config) {
        super(config);
        registerSerialize();
    }

    public OrderDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
        registerSerialize();
    }


    private void registerSerialize() {
        SerializedData.getInstance().register(OrderEntity.ClassId, OrderEntity.class);
    }

    /**
     * Creates the underlying database table.
     */
    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        String constraint = ifNotExists ? "IF NOT EXISTS " : "";
        db.execSQL("CREATE TABLE " + constraint + "'" + TABLENAME + "' (" +
                "'ID' TEXT PRIMARY KEY," +
                "'CREATED' INTEGER NOT NULL," +
                "'STATUS' TEXT NOT NULL," +
                "'DATA' BLOB ," +
                "'UPDATED' INTEGER NOT NULL );");
        // Add Indexes
        db.execSQL("CREATE INDEX " + constraint + "IDX_ORDER_CACHE_DATA_CREATED ON ORDER_CACHE_DATA" +
                " (CREATED);");
        db.execSQL("CREATE INDEX " + constraint + "IDX_ORDER_CACHE_DATA_STATUS ON ORDER_CACHE_DATA" +
                " (STATUS);");
    }

    /**
     * Drops the underlying database table.
     */
    public static void dropTable(SQLiteDatabase db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "'" + TABLENAME + "'";
        db.execSQL(sql);
    }

    public static void upgradeTable(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 70) {
            dropTable(db, true);
            createTable(db, false);
        }
    }

    /**
     * @inheritdoc
     */
    @Override
    protected void bindValues(SQLiteStatement stmt, OrderEntity entity) {
        stmt.clearBindings();

        String id = entity.orderId;
        if (id != null) {
            stmt.bindString(1, id);
        }
        stmt.bindLong(2, entity.created);
        stmt.bindString(3, entity.orderStatus);
        stmt.bindBlob(4, SerializedData.getInstance().toBytes(entity));
        stmt.bindLong(5, entity.updated);
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
    public OrderEntity readEntity(Cursor cursor, int offset) {
        OrderEntity entity = (OrderEntity) SerializedData.getInstance().toEntity(OrderEntity.ClassId, cursor.getBlob(offset + 3));
        return entity;
    }

    /**
     * @inheritdoc
     */
    @Override
    public void readEntity(Cursor cursor, OrderEntity entity, int offset) {
        entity = (OrderEntity) SerializedData.getInstance().toEntity(OrderEntity.ClassId, cursor.getBlob(offset + 3));
    }

    /**
     * @inheritdoc
     */
    @Override
    protected String updateKeyAfterInsert(OrderEntity entity, long rowId) {
        return entity.orderId;
    }

    /**
     * @inheritdoc
     */
    @Override
    public String getKey(OrderEntity entity) {
        if (entity != null) {
            return entity.orderId;
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
