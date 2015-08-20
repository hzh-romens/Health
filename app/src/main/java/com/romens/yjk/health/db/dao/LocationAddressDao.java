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
public class LocationAddressDao  extends AbstractDao<LocationAddressEntity, String> {

    public static final String TABLENAME = "LocationAddress";

    /**
     * Properties of entity DiscoveryEntity.<br/>
     * Can be used for QueryBuilder and for referencing column names.
     */
    public static class Properties {
        public final static Property Id = new Property(0, String.class, "id", true, "_id");
        public final static Property Key = new Property(1, String.class, "key", false, "KEY");
        public final static Property ParentId = new Property(2, String.class, "parentId", false, "PARENT_ID");
        public final static Property Code = new Property(3, String.class, "code", false, "CODE");
        public final static Property Name = new Property(4, String.class, "name", false, "NAME");
        public final static Property ZipCode = new Property(5, String.class, "zipCode", false, "ZIP_CODE");
        public final static Property Description = new Property(6, String.class, "description", false, "DESCRIPTION");
        public final static Property Status = new Property(7, int.class, "status", false, "STATUS");
        public final static Property Created = new Property(8, int.class, "created", false, "CREATED");
        public final static Property Updated = new Property(9, int.class, "updated", false, "UPDATED");
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
        db.execSQL("CREATE TABLE " + constraint + "'"+ TABLENAME +"' (" +
                "'_id' INTEGER PRIMARY KEY AUTOINCREMENT ," +
                "'KEY' TEXT NOT NULL UNIQUE ," +
                "'PARENT_ID' TEXT," +
                "'CODE' TEXT NOT NULL ," +
                "'NAME' TEXT NOT NULL ," +
                "'ZIP_CODE' TEXT ," +
                "'DESCRIPTION' TEXT ," +
                "'STATUS' INTEGER NOT NULL ," +
                "'CREATED' INTEGER NOT NULL ," +
                "'UPDATED' INTEGER NOT NULL );");
        // Add Indexes
        db.execSQL("CREATE INDEX " + constraint + "IDX_LocationAddress_KEY ON "+ TABLENAME +
                " (KEY);");
        db.execSQL("CREATE INDEX " + constraint + "IDX_LocationAddress_PARENT_ID ON "+ TABLENAME +
                " (PARENT_ID);");
        db.execSQL("CREATE INDEX " + constraint + "IDX_LocationAddress_CODE ON "+ TABLENAME +
                " (CODE);");

        initCreatedTableData(db);
    }

    public static void upgradeTable(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    public static void initCreatedTableData(SQLiteDatabase db) {
    }

    /**
     * Drops the underlying database table.
     */
    public static void dropTable(SQLiteDatabase db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "'"+ TABLENAME +"'";
        db.execSQL(sql);
    }

    /**
     * @inheritdoc
     */
    @Override
    protected void bindValues(SQLiteStatement stmt, LocationAddressEntity entity) {
        stmt.clearBindings();
        stmt.bindString(2, entity.id);
        stmt.bindString(3, entity.parentId);
        stmt.bindString(4, entity.code);
        stmt.bindString(5, entity.name);
        stmt.bindString(6, entity.zipCode);
        stmt.bindString(7, entity.description);
        stmt.bindLong(8, entity.state);
        stmt.bindLong(9, entity.createdTime);
        stmt.bindLong(10, entity.updatedTime);
    }

    /**
     * @inheritdoc
     */
    @Override
    public String readKey(Cursor cursor, int offset) {
        return cursor.getString(offset + 1);
    }

    /**
     * @inheritdoc
     */
    @Override
    public LocationAddressEntity readEntity(Cursor cursor, int offset) {
        LocationAddressEntity entity = new LocationAddressEntity();
        entity.id=cursor.getString(offset + 1);
        entity.parentId=cursor.getString(offset + 2);
        entity.code=cursor.getString(offset + 3);
        entity.name=cursor.getString(offset + 4);
        entity.zipCode=cursor.getString(offset + 5);
        entity.description=cursor.getString(offset + 6);
        entity.state=cursor.getInt(offset + 7);
        entity.createdTime=cursor.getInt(offset + 8);
        entity.updatedTime=cursor.getInt(offset + 9);
        return entity;
    }

    /**
     * @inheritdoc
     */
    @Override
    public void readEntity(Cursor cursor, LocationAddressEntity entity, int offset) {
        entity.id=cursor.getString(offset + 1);
        entity.parentId=cursor.getString(offset + 2);
        entity.code=cursor.getString(offset + 3);
        entity.name=cursor.getString(offset + 4);
        entity.zipCode=cursor.getString(offset + 5);
        entity.description=cursor.getString(offset + 6);
        entity.state=cursor.getInt(offset + 7);
        entity.createdTime=cursor.getInt(offset + 8);
        entity.updatedTime=cursor.getInt(offset + 9);
    }

    /**
     * @inheritdoc
     */
    @Override
    protected String updateKeyAfterInsert(LocationAddressEntity entity, long rowId) {
        return entity.id;
    }

    /**
     * @inheritdoc
     */
    @Override
    public String getKey(LocationAddressEntity entity) {
        if (entity != null) {
            return entity.id;
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
