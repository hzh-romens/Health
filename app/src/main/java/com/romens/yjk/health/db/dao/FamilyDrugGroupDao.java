package com.romens.yjk.health.db.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import com.romens.yjk.health.db.entity.FamilyDrugGroupEntity;
import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.internal.DaoConfig;

/**
 * Created by anlc on 2015/11/9.
 */
public class FamilyDrugGroupDao extends AbstractDao<FamilyDrugGroupEntity, Long> {

    public static final String TABLENAME = "FamilyDrugGroup";

    /**
     * Properties of entity DiscoveryEntity.<br/>
     * Can be used for QueryBuilder and for referencing column names.
     */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "key", true, "_id");
        public final static Property DrugName = new Property(1, String.class, "drugName", false, "DRUGNAME");
        public final static Property Remark = new Property(2, String.class, "remark", false, "REMARK");
        public final static Property DrugGuid = new Property(3, String.class, "drugGuid", false, "DRUGGUID");
    }

    public FamilyDrugGroupDao(DaoConfig config) {
        super(config);
    }

    public FamilyDrugGroupDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /**
     * Creates the underlying database table.
     */
    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        String constraint = ifNotExists ? "IF NOT EXISTS " : "";
        db.execSQL("CREATE TABLE " + constraint + "'" + TABLENAME + "' (" +
                "'_id' INTEGER PRIMARY KEY AUTOINCREMENT ," + // 0: key
                "'DRUGNAME' TEXT NOT NULL ," + // 2: user
                "'REMARK' TEXT NOT NULL ," + // 2: user
                "'DRUGGUID' TEXT NOT NULL );"); // 11: times
        // Add Indexes
        db.execSQL("CREATE INDEX " + "IF NOT EXISTS " + "IDX_FamilyDrugGroup_KEY ON " + TABLENAME +
                " (_id);");

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
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "'" + TABLENAME + "'";
        db.execSQL(sql);
    }

    /**
     * @inheritdoc
     */
    @Override
    protected void bindValues(SQLiteStatement stmt, FamilyDrugGroupEntity entity) {
        stmt.clearBindings();
        stmt.bindString(2, entity.getDrugName());
        stmt.bindString(3, entity.getRemark());
        stmt.bindString(4, entity.getDrugGuid());
    }

    /**
     * @inheritdoc
     */
    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }

    /**
     * @inheritdoc
     */
    @Override
    public FamilyDrugGroupEntity readEntity(Cursor cursor, int offset) {
        FamilyDrugGroupEntity entity = new FamilyDrugGroupEntity();
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0)); // key
        entity.setDrugName(cursor.getString(offset + 1));// user
        entity.setRemark(cursor.getString(offset + 2));// user
        entity.setDrugGuid(cursor.getString(offset + 3));// user
        return entity;
    }

    /**
     * @inheritdoc
     */
    @Override
    public void readEntity(Cursor cursor, FamilyDrugGroupEntity entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setDrugName(cursor.getString(offset + 1));// user
        entity.setRemark(cursor.getString(offset + 2));// user
        entity.setDrugGuid(cursor.getString(offset + 3));// user
    }

    /**
     * @inheritdoc
     */
    @Override
    protected Long updateKeyAfterInsert(FamilyDrugGroupEntity entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }

    /**
     * @inheritdoc
     */
    @Override
    public Long getKey(FamilyDrugGroupEntity entity) {
        if (entity != null) {
            return entity.getId();
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