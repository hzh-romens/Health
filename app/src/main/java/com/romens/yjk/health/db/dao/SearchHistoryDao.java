package com.romens.yjk.health.db.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import com.romens.yjk.health.db.entity.SearchHistoryEntity;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.internal.DaoConfig;

/**
 * Created by anlc on 2015/9/8.
 */
public class SearchHistoryDao extends AbstractDao<SearchHistoryEntity, Long> {

    public static final String TABLENAME = "SearchHistory";

    /**
     * Properties of entity DiscoveryEntity.<br/>
     * Can be used for QueryBuilder and for referencing column names.
     */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property HISTORYKEYWORD = new Property(1, int.class, "historyKeyword", false, "HISTORYKEYWORD");
    }


    public SearchHistoryDao(DaoConfig config) {
        super(config);
    }

    public SearchHistoryDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /**
     * Creates the underlying database table.
     */
    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        String constraint = ifNotExists ? "IF NOT EXISTS " : "";
        db.execSQL("CREATE TABLE " + constraint + "'" + TABLENAME + "' (" +
                "'_id' INTEGER PRIMARY KEY AUTOINCREMENT ," + // 0: id
                "'HISTORYKEYWORD' TEXT NOT NULL );"); // 11: times
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
    protected void bindValues(SQLiteStatement stmt, SearchHistoryEntity entity) {
        stmt.clearBindings();
        stmt.bindString(2, entity.getHistoryKeyword());
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
    public SearchHistoryEntity readEntity(Cursor cursor, int offset) {
        SearchHistoryEntity entity = new SearchHistoryEntity();
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0)); // id
        entity.setHistoryKeyword(cursor.getString(offset + 1)); // userIcon
        return entity;
    }

    /**
     * @inheritdoc
     */
    @Override
    public void readEntity(Cursor cursor, SearchHistoryEntity entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setHistoryKeyword(cursor.getString(offset + 1)); // userIcon
    }

    /**
     * @inheritdoc
     */
    @Override
    protected Long updateKeyAfterInsert(SearchHistoryEntity entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }

    /**
     * @inheritdoc
     */
    @Override
    public Long getKey(SearchHistoryEntity entity) {
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
