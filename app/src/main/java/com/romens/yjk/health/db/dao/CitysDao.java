package com.romens.yjk.health.db.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import com.romens.yjk.health.db.entity.CitysEntity;
import com.romens.yjk.health.db.entity.RemindEntity;

import java.util.List;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.internal.DaoConfig;

/**
 * Created by anlc on 2015/9/19.
 * 保存城市选择的dao
 */
public class CitysDao extends AbstractDao<CitysEntity, Long> {

    public static final String TABLENAME = "citys";

    /**
     * Properties of entity DiscoveryEntity.<br/>
     * Can be used for QueryBuilder and for referencing column names.
     */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "key", true, "_id");
        public final static Property Guid = new Property(1, String.class, "guid", false, "GUID");
        public final static Property ParentGuid = new Property(2, String.class, "parentGuid", false, "PARENTGUID");
        public final static Property Name = new Property(3, String.class, "name", false, "NAME");
        public final static Property Level = new Property(4, String.class, "level", false, "LEVEL");
    }
    public CitysDao(DaoConfig config) {
        super(config);
    }
    public CitysDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }
    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
//        String constraint = ifNotExists ? "IF NOT EXISTS " : "";
        String constraint ="IF NOT EXISTS ";
        db.execSQL("CREATE TABLE " + constraint + "'" + TABLENAME + "' (" +
                "'_id' INTEGER PRIMARY KEY AUTOINCREMENT ," + // 0: key
                "'GUID' TEXT NOT NULL ," + // 2: user
                "'PARENTGUID' TEXT NOT NULL ," + // 3: drug
                "'NAME' TEXT NOT NULL ," + // 4: count
                "'LEVEL' TEXT NOT NULL );"); // 11: times
        // Add Indexes
        db.execSQL("CREATE INDEX " + constraint + "IDX_Remind_KEY ON " + TABLENAME +
                " (_id);");

        initCreatedTableData(db);
    }

    public static void upgradeTable(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    public static void initCreatedTableData(SQLiteDatabase db) {
    }

    public static void dropTable(SQLiteDatabase db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "'"+ TABLENAME +"'";
        db.execSQL(sql);
    }

    /**
     * @inheritdoc
     */
    @Override
    protected void bindValues(SQLiteStatement stmt, CitysEntity entity) {
        stmt.clearBindings();
        stmt.bindString(2, entity.getGuid());
        stmt.bindString(3, entity.getParentGuid());
        stmt.bindString(4, entity.getName());
        stmt.bindString(5, entity.getParentGuid());
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
    public CitysEntity readEntity(Cursor cursor, int offset) {
        CitysEntity entity = new CitysEntity();
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0)); // key
        entity.setGuid(cursor.getString(offset + 1));// user
        entity.setParentGuid(cursor.getString(offset + 2)); // drug
        entity.setName(cursor.getString(offset + 3)); // count
        entity.setLevel(cursor.getString(offset + 4)); // count

        return entity;
    }

    /**
     * @inheritdoc
     */
    @Override
    public void readEntity(Cursor cursor, CitysEntity entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0)); // key
        entity.setGuid(cursor.getString(offset + 1));// user
        entity.setParentGuid(cursor.getString(offset + 2)); // drug
        entity.setName(cursor.getString(offset + 3)); // count
        entity.setLevel(cursor.getString(offset + 4)); // count
    }

    /**
     * @inheritdoc
     */
    @Override
    protected Long updateKeyAfterInsert(CitysEntity entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }

    /**
     * @inheritdoc
     */
    @Override
    public Long getKey(CitysEntity entity) {
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

    public List<RemindEntity> readDb(RemindDao remindDao) {
        List<RemindEntity> entities = remindDao.queryBuilder().orderDesc(RemindDao.Properties.Id).list();
        return entities;
    }
}

