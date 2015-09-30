package com.romens.yjk.health.db.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import com.romens.yjk.health.db.entity.EatDrugUserEntity;
import com.romens.yjk.health.db.entity.RemindEntity;

import java.util.List;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.internal.DaoConfig;

/**
 * Created by anlc on 2015/9/11.
 * 保存用药提醒用户的dao
 */
public class EatDrugUserDao  extends AbstractDao<EatDrugUserEntity, Long> {

    public static final String TABLENAME = "EatDrugUser";

    /**
     * Properties of entity DiscoveryEntity.<br/>
     * Can be used for QueryBuilder and for referencing column names.
     */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property Name = new Property(1, String.class, "name", false, "NAME");
    }

    public EatDrugUserDao(DaoConfig config) {
        super(config);
    }

    public EatDrugUserDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /**
     * Creates the underlying database table.
     */
    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        String constraint = ifNotExists ? "IF NOT EXISTS " : "";
        db.execSQL("CREATE TABLE " + constraint + "'" + TABLENAME + "' (" +
                "'_id' INTEGER PRIMARY KEY AUTOINCREMENT ," + // 0: id
                "'NAME' INTEGER NOT NULL );"); // 11: times
        // Add Indexes
        db.execSQL("CREATE INDEX " + "IF NOT EXISTS " + "IDX_Remind_KEY ON " + TABLENAME +
                " (_id);");

        initCreatedTableData(db);
    }

    public static void upgradeTable(SQLiteDatabase db, int oldVersion, int newVersion) {
//        if(newVersion==15){
//            //附近药店
//            ContentValues value = new ContentValues();
//            value.put("USERICON", DiscoveryCollection.FindDrugWithScanner.key);
//            value.put("USER", DiscoveryCollection.FindDrugWithScanner.iconRes);
//            value.put("DRUG", DiscoveryCollection.FindDrugWithScanner.iconUrl);
//            value.put("COUNT", DiscoveryCollection.FindDrugWithScanner.name);
//            db.insert(TABLENAME, null, value);
//        }
    }

    public static void initCreatedTableData(SQLiteDatabase db) {
//        //用药提醒
//        ContentValues value = new ContentValues();
//        value.put("USERICON", DiscoveryCollection.FindDrugWithScanner.key);
//        value.put("USER", DiscoveryCollection.FindDrugWithScanner.iconRes);
//        value.put("DRUG", DiscoveryCollection.FindDrugWithScanner.iconUrl);
//        value.put("COUNT", DiscoveryCollection.FindDrugWithScanner.name);
//        db.insert(TABLENAME, null, value);
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
    protected void bindValues(SQLiteStatement stmt, EatDrugUserEntity entity) {
        stmt.clearBindings();
        stmt.bindString(2, entity.getName());
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
    public EatDrugUserEntity readEntity(Cursor cursor, int offset) {
        EatDrugUserEntity entity = new EatDrugUserEntity();
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0)); // id
        entity.setName(cursor.getString(offset + 1));// user
        return entity;
    }

    /**
     * @inheritdoc
     */
    @Override
    public void readEntity(Cursor cursor, EatDrugUserEntity entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setName(cursor.getString(offset + 1));// user
    }

    /**
     * @inheritdoc
     */
    @Override
    protected Long updateKeyAfterInsert(EatDrugUserEntity entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }

    /**
     * @inheritdoc
     */
    @Override
    public Long getKey(EatDrugUserEntity entity) {
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
