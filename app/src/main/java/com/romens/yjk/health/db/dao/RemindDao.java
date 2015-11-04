package com.romens.yjk.health.db.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import com.romens.yjk.health.db.entity.RemindEntity;

import java.util.List;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.internal.DaoConfig;

/**
 * Created by anlc on 2015/8/26.
 * 保存用药提醒的dao
 */
public class RemindDao extends AbstractDao<RemindEntity, Long> {

    public static final String TABLENAME = "Remind";

    /**
     * Properties of entity DiscoveryEntity.<br/>
     * Can be used for QueryBuilder and for referencing column names.
     */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "key", true, "_id");
        public final static Property User = new Property(1, String.class, "user", false, "USER");
        public final static Property Drug = new Property(2, String.class, "drug", false, "DRUG");
        public final static Property IntervalDay = new Property(3, String.class, "intervalDay", false, "INTERVALDAY");
        public final static Property StartDate = new Property(4, String.class, "startDate", false, "STARTDATE");

        public final static Property FirstTime = new Property(5, String.class, "firstTime", false, "FIRSTTIME");
        public final static Property Secondtime = new Property(6, String.class, "secondtime", false, "SECONDTIME");
        public final static Property ThreeTime = new Property(7, String.class, "threeTime", false, "THREETIME");
        public final static Property FourTime = new Property(8, String.class, "fourTime", false, "FOURTIME");
        public final static Property FiveTime = new Property(9, String.class, "fiveTime", false, "FIVETIME");
        public final static Property TimesInDay = new Property(10, int.class, "timesInDay", false, "TIMESINDAY");
        public final static Property IsRemind = new Property(11, int.class, "isRemind", false, "ISREMIND");
        public final static Property Dosage = new Property(12, int.class, "dosage", false, "DOSAGE");
        public final static Property Remark = new Property(13, int.class, "remark", false, "REMARK");
    }


    public RemindDao(DaoConfig config) {
        super(config);
    }

    public RemindDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /**
     * Creates the underlying database table.
     */
    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        String constraint = ifNotExists ? "IF NOT EXISTS " : "";
        db.execSQL("CREATE TABLE " + constraint + "'" + TABLENAME + "' (" +
                "'_id' INTEGER PRIMARY KEY AUTOINCREMENT ," + // 0: key
                "'USER' TEXT NOT NULL ," + // 2: user
                "'DRUG' TEXT NOT NULL ," + // 3: drug
                "'INTERVALDAY' TEXT NOT NULL ," + // 4: count
                "'STARTDATE' TEXT NOT NULL ," + // 5: startDate
                "'FIRSTTIME' TEXT NOT NULL ," + // 6: firstTime
                "'SECONDTIME' TEXT NOT NULL ," + // 7: secondTiime
                "'THREETIME' TEXT NOT NULL ," + // 8: threeTime
                "'FOURTIME' TEXT NOT NULL ," + // 9: fourTime
                "'FIVETIME' TEXT NOT NULL ," + // 10: fiveTime
                "'TIMESINDAY' INTEGER NOT NULL ," + // 10: fiveTime
                "'ISREMIND' INTEGER NOT NULL ," + // 10: fiveTime
                "'DOSAGE' INTEGER NOT NULL ," + // 10: fiveTime
                "'REMARK' INTEGER NOT NULL );"); // 11: times
        // Add Indexes
        db.execSQL("CREATE INDEX " + constraint + "IDX_Remind_KEY ON " + TABLENAME +
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
    protected void bindValues(SQLiteStatement stmt, RemindEntity entity) {
        stmt.clearBindings();
        stmt.bindString(2, entity.getUser());
        stmt.bindString(3, entity.getDrug());
        stmt.bindLong(4, entity.getIntervalDay());
        stmt.bindString(5, entity.getStartDate());

        stmt.bindString(6, entity.getFirstTime());
        stmt.bindString(7,entity.getSecondtime());
        stmt.bindString(8,entity.getThreeTime());
        stmt.bindString(9,entity.getFourTime());
        stmt.bindString(10,entity.getFiveTime());
        stmt.bindLong(11, entity.getTimesInDay());
        stmt.bindLong(12, entity.getIsRemind());
        stmt.bindString(13, entity.getDosage());
        stmt.bindString(14, entity.getRemark());
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
    public RemindEntity readEntity(Cursor cursor, int offset) {
        RemindEntity entity = new RemindEntity();
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0)); // key
        entity.setUser(cursor.getString(offset + 1));// user
        entity.setDrug(cursor.getString(offset + 2)); // drug
        entity.setIntervalDay(cursor.getInt(offset + 3)); // count
        entity.setStartDate(cursor.getString(offset + 4)); // count

        entity.setFirstTime(cursor.getString(offset + 5)); // count
        entity.setSecondtime(cursor.getString(offset + 6)); // count
        entity.setThreeTime(cursor.getString(offset + 7)); // count
        entity.setFourTime(cursor.getString(offset + 8)); // count
        entity.setFiveTime(cursor.getString(offset + 9)); // count
        entity.setTimesInDay(cursor.getInt(offset + 10)); // count
        entity.setIsRemind(cursor.getInt(offset + 11)); // count
        entity.setDosage(cursor.getString(offset + 12)); // count
        entity.setRemark(cursor.getString(offset + 13)); // count
        return entity;
    }

    /**
     * @inheritdoc
     */
    @Override
    public void readEntity(Cursor cursor, RemindEntity entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setUser(cursor.getString(offset + 1));// user
        entity.setDrug(cursor.getString(offset + 2)); // drug
        entity.setIntervalDay(cursor.getInt(offset + 3)); // count
        entity.setStartDate(cursor.getString(offset + 4)); // count

        entity.setFirstTime(cursor.getString(offset + 5)); // count
        entity.setSecondtime(cursor.getString(offset + 6)); // count
        entity.setThreeTime(cursor.getString(offset + 7)); // count
        entity.setFourTime(cursor.getString(offset + 8)); // count
        entity.setFiveTime(cursor.getString(offset + 9)); // count
        entity.setTimesInDay(cursor.getInt(offset + 10)); // count
        entity.setIsRemind(cursor.getInt(offset + 11)); // count
        entity.setDosage(cursor.getString(offset + 12)); // count
        entity.setRemark(cursor.getString(offset + 13)); // count
    }

    /**
     * @inheritdoc
     */
    @Override
    protected Long updateKeyAfterInsert(RemindEntity entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }

    /**
     * @inheritdoc
     */
    @Override
    public Long getKey(RemindEntity entity) {
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

    public  List<RemindEntity> readDb(RemindDao remindDao) {
        List<RemindEntity> entities = remindDao.queryBuilder().orderDesc(RemindDao.Properties.Id).list();
        return entities;
    }
}
