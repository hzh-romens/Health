package com.romens.yjk.health.db.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import com.romens.yjk.health.db.entity.DrugGroupEntity;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.internal.DaoConfig;

/**
 * Created by siery on 15/8/18.
 */
public class DrugGroupDao extends AbstractDao<DrugGroupEntity, String> {

    public static final String TABLENAME = "DrugGroup";

    /**
     * Properties of entity DiscoveryEntity.<br/>
     * Can be used for QueryBuilder and for referencing column names.
     */
    public static class Properties {
        public final static Property Id = new Property(0, String.class, "id", true, "_id");
        public final static Property Key = new Property(1, String.class, "key", false, "KEY");
        public final static Property Name = new Property(2, String.class, "name", false, "NAME");
        public final static Property PID = new Property(3, String.class, "parentId", false, "PID");
        public final static Property Status = new Property(4, int.class, "status", false, "STATUS");
        public final static Property Created = new Property(5, int.class, "created", false, "CREATED");
        public final static Property Updated = new Property(6, int.class, "updated", false, "UPDATED");
        public final static Property SortIndex = new Property(7, int.class, "sortIndex", false, "SORTINDEX");
    }


    public DrugGroupDao(DaoConfig config) {
        super(config);
    }

    public DrugGroupDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /**
     * Creates the underlying database table.
     */
    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        String constraint = ifNotExists ? "IF NOT EXISTS " : "";
        db.execSQL("CREATE TABLE " + constraint + "'" + TABLENAME + "' (" +
                "'_id' INTEGER PRIMARY KEY AUTOINCREMENT ," +
                "'KEY' TEXT UNIQUE ," +     //  "'KEY' TEXT NOT NULL UNIQUE ," +
                "'NAME' TEXT NOT NULL ," +
                "'PID' TEXT," +
                "'STATUS' INTEGER NOT NULL ," +
                "'CREATED' INTEGER NOT NULL ," +
                "'UPDATED' INTEGER NOT NULL ," +
                "'SORTINDEX' INTEGER NOT NULL );");
        // Add Indexes
        db.execSQL("CREATE INDEX " + constraint + "IDX_DrugGroup_KEY ON " + TABLENAME +
                " (KEY);");

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
    protected void bindValues(SQLiteStatement stmt, DrugGroupEntity entity) {
        stmt.clearBindings();
        if(entity.getId()!=null) {
            stmt.bindString(2, entity.getId());
        }
        stmt.bindString(3, entity.getName());
        if(entity.getPID()!=null) {
            stmt.bindString(4, entity.getPID());
        }
        stmt.bindLong(5, entity.getStatus());
        stmt.bindLong(6, entity.getCreated());
        stmt.bindLong(7, entity.getUpdated());
        stmt.bindLong(8, entity.getSortIndex());
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
    public DrugGroupEntity readEntity(Cursor cursor, int offset) {
        DrugGroupEntity entity = new DrugGroupEntity();
        entity.setId(cursor.getString(offset + 1));
        entity.setName(cursor.getString(offset + 2));
        entity.setPID(cursor.getString(offset + 3));
        entity.setStatus(cursor.getInt(offset + 4));
        entity.setCreated(cursor.getInt(offset + 5));
        entity.setUpdated(cursor.getInt(offset + 6));
        entity.setSortIndex(cursor.getInt(offset + 7));
        return entity;
    }

    /**
     * @inheritdoc
     */
    @Override
    public void readEntity(Cursor cursor, DrugGroupEntity entity, int offset) {
        entity.setId(cursor.getString(offset + 1));
        entity.setName(cursor.getString(offset + 2));
        entity.setPID(cursor.getString(offset + 3));
        entity.setStatus(cursor.getInt(offset + 4));
        entity.setCreated(cursor.getInt(offset + 5));
        entity.setUpdated(cursor.getInt(offset + 6));
        entity.setSortIndex(cursor.getInt(offset + 7));
    }

    /**
     * @inheritdoc
     */
    @Override
    protected String updateKeyAfterInsert(DrugGroupEntity entity, long rowId) {
        return entity.getId();
    }

    /**
     * @inheritdoc
     */
    @Override
    public String getKey(DrugGroupEntity entity) {
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
