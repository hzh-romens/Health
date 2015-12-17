package com.romens.yjk.health.db.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import com.romens.android.io.SerializedData;
import com.romens.yjk.health.db.entity.FavoritesEntity;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.internal.DaoConfig;

/**
 * Created by anlc on 2015/11/23.
 */
public class FavoritesDao extends AbstractDao<FavoritesEntity, String> {

    public static final String TABLENAME = "MedicineFavorites";

    /**
     * Properties of entity DiscoveryEntity.<br/>
     * Can be used for QueryBuilder and for referencing column names.
     */
    public static class Properties {
        public final static Property FId = new Property(0, String.class, "fId", true, "FID");
        public final static Property MerchandiseId = new Property(1, String.class, "merchandiseId", false, "MERCHANDISEID");
        public final static Property MedicineName = new Property(2, String.class, "medicineName", false, "MEDICINENAME");
        public final static Property ShopId = new Property(3, String.class, "shopId", false, "SHOPID");
        public final static Property CacheData = new Property(4, byte[].class, "cacheData", false, "CACHEDATA");
        public final static Property Updated = new Property(5, Long.class, "updated", false, "UPDATED");

//    public final static Property Id = new Property(0, Long.class, "key", true, "_id");
//    public final static Property MerchandiseId = new Property(1, String.class, "merchandiseId", false, "MERCHANDISEID");
//    public final static Property MedicineName = new Property(2, String.class, "medicineName", false, "MEDICINENAME");
//    public final static Property MedicineSpec = new Property(3, String.class, "medicineSpec", false, "MEDICINESPEC");
//    public final static Property ShopId = new Property(4, String.class, "shopId", false, "SHOPID");
//    public final static Property ShopName = new Property(5, String.class, "shopName", false, "SHOPNAME");
//    public final static Property PicBig = new Property(6, String.class, "picBig", false, "PICBIG");
//    public final static Property PicSmall = new Property(7, String.class, "picSmall", false, "PICSMALL");
//    public final static Property Price = new Property(8, String.class, "price", false, "PRICE");
//    public final static Property MemberPrice = new Property(9, String.class, "memberPrice", false, "MEMBERPRICE");
//    public final static Property AssessCount = new Property(10, String.class, "assessCount", false, "ASSESSCOUNT");
//    public final static Property Updated = new Property(11, int.class, "updated", false, "UPDATED");
//    public final static Property SaleCount = new Property(12, String.class, "saleCount", false, "SALECOUNT");
    }

    public FavoritesDao(DaoConfig config) {
        super(config);
        registerSerialize();
    }

    public FavoritesDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
        registerSerialize();
    }

    private void registerSerialize() {
        SerializedData.getInstance().register(FavoritesEntity.ClassId, FavoritesEntity.class);
    }

    /**
     * Creates the underlying database table.
     */
    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        String constraint = ifNotExists ? "IF NOT EXISTS " : "";
        db.execSQL("CREATE TABLE " + constraint + "'" + TABLENAME + "' (" +
                "'FID' TEXT PRIMARY KEY  ," + // 0: key
                "'MERCHANDISEID' TEXT NOT NULL UNIQUE ," + // 2: user
                "'MEDICINENAME' TEXT NOT NULL ," + // 3: drug
                "'SHOPID' TEXT NOT NULL ," + // 4: count
                "'CACHEDATA' BLOB NOT NULL ," + // 4: count
                "'UPDATED' INTEGER NOT NULL);"); // 11: times
//        db.execSQL("CREATE TABLE " + constraint + "'" + TABLENAME + "' (" +
//                "'_id' INTEGER PRIMARY KEY AUTOINCREMENT ," + // 0: key
//                "'MERCHANDISEID' TEXT NOT NULL UNIQUE ," + // 2: user
//                "'MEDICINENAME' TEXT NOT NULL ," + // 3: drug
//                "'MEDICINESPEC' TEXT NOT NULL ," + // 4: count
//                "'SHOPID' TEXT NOT NULL ," + // 4: count
//                "'SHOPNAME' TEXT NOT NULL ," + // 4: count
//                "'PICBIG' TEXT NOT NULL ," + // 4: count
//                "'PICSMALL' TEXT NOT NULL ," + // 4: count
//                "'PRICE' TEXT NOT NULL ," + // 4: count
//                "'MEMBERPRICE' TEXT NOT NULL ," + // 4: count
//                "'ASSESSCOUNT' TEXT NOT NULL ," + // 4: count
//                "'UPDATED' INTEGER NOT NULL ," +
//                "'SALECOUNT' TEXT NOT NULL );"); // 11: times
        // Add Indexes
        db.execSQL("CREATE INDEX " + constraint + "IDX_MedicineFavorites_MERCHANDISEID ON " + TABLENAME +
                " (MERCHANDISEID);");
        db.execSQL("CREATE INDEX " + constraint + "IDX_MedicineFavorites_MEDICINENAME ON " + TABLENAME +
                " (MEDICINENAME);");
        db.execSQL("CREATE INDEX " + constraint + "IDX_MedicineFavorites_SHOPID ON " + TABLENAME +
                " (SHOPID);");
    }

    public static void upgradeTable(SQLiteDatabase db, int oldVersion, int newVersion) {
        if(oldVersion<35){
            db.execSQL("DROP TABLE IF EXISTS 'CollectData'");
        }
        dropTable(db, true);
        createTable(db, false);
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
    protected void bindValues(SQLiteStatement stmt, FavoritesEntity entity) {
        stmt.clearBindings();
        stmt.bindString(1, entity.getId());
        stmt.bindString(2, entity.getMerchandiseId());
        stmt.bindString(3, entity.getMedicineName());
        stmt.bindString(4, entity.getShopId());
        stmt.bindBlob(5, SerializedData.getInstance().toBytes(entity));
        stmt.bindLong(6, entity.getUpdated());
    }

    /**
     * @inheritdoc
     */
    @Override
    public String readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? "" : cursor.getString(offset + 0);
    }

    /**
     * @inheritdoc
     */
    @Override
    public FavoritesEntity readEntity(Cursor cursor, int offset) {
        FavoritesEntity entity = (FavoritesEntity) SerializedData.getInstance().toEntity(FavoritesEntity.ClassId, cursor.getBlob(offset + 4));
//        entity.setId(cursor.isNull(offset + 0) ? "" : cursor.getString(offset + 0)); // key
//        entity.setMerchandiseId(cursor.getString(offset + 1));// user
//        entity.setMedicineName(cursor.getString(offset + 2)); // drug
//        entity.setShopId(cursor.getString(offset + 4)); // count
//        entity.setShopName(cursor.getString(offset + 5)); // count
//        entity.setPicBig(cursor.getString(offset + 6)); // count
//        entity.setPicSmall(cursor.getString(offset + 7)); // count
//        entity.setPrice(cursor.getString(offset + 8)); // count
//        entity.setMemberPrice(cursor.getString(offset + 9)); // count
//        entity.setAssessCount(cursor.getString(offset + 10)); // count
//        entity.setUpdated(cursor.getInt(offset + 11));
//        entity.setSaleCount(cursor.getString(offset + 12)); // count
        return entity;
    }

    /**
     * @inheritdoc
     */
    @Override
    public void readEntity(Cursor cursor, FavoritesEntity entity, int offset) {
        entity = (FavoritesEntity) SerializedData.getInstance().toEntity(FavoritesEntity.ClassId, cursor.getBlob(offset + 4));
//        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
//        entity.setMerchandiseId(cursor.getString(offset + 1));// user
//        entity.setMedicineName(cursor.getString(offset + 2)); // drug
//        entity.setMedicineSpec(cursor.getString(offset + 3)); // count
//        entity.setShopId(cursor.getString(offset + 4)); // count
//        entity.setShopName(cursor.getString(offset + 5)); // count
//        entity.setPicBig(cursor.getString(offset + 6)); // count
//        entity.setPicSmall(cursor.getString(offset + 7)); // count
//        entity.setPrice(cursor.getString(offset + 8)); // count
//        entity.setMemberPrice(cursor.getString(offset + 9)); // count
//        entity.setAssessCount(cursor.getString(offset + 10)); // count
//        entity.setUpdated(cursor.getInt(offset + 11));
//        entity.setSaleCount(cursor.getString(offset + 12)); // count
    }

    /**
     * @inheritdoc
     */
    @Override
    protected String updateKeyAfterInsert(FavoritesEntity entity, long rowId) {
        return entity.getId();
    }

    /**
     * @inheritdoc
     */
    @Override
    public String getKey(FavoritesEntity entity) {
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