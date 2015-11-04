package com.romens.yjk.health.db.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import com.romens.yjk.health.db.entity.HistoryEntity;
import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.internal.DaoConfig;

/**
 * Created by AUSU on 2015/10/16.
 */
public class HistoryDao extends AbstractDao<HistoryEntity, Long> {
    public static final String TABLENAME = "History";
    public static class Properties {
    public final static Property Id = new Property(0, Long.class, "key", true, "_id");
    public final static Property ShopName = new Property(1, String.class, "shopName", false, "SHOPNAME");
    public final static Property ImgUrl = new Property(2, String.class, "imgUrl", false, "IMGURL");
    public final static Property IsSelect = new Property(3, int.class, "isSelect", false, "ISSELECT");
    public final static Property MedicinalName = new Property(4, String.class, "medicinalName", false, "MEDICINALNAME");
    public final static Property CurrentPrice = new Property(5, String.class, "currentPrice", false, "CURRENTPRICE");
    public final static Property DiscountPrice = new Property(6, String.class, "discountPrice", false, "DISCOUNTPRICE");
    public final static Property SaleCount = new Property(7, String.class, "saleCount", false, "SALECOUNT");
    public final static Property CommentCount = new Property(8, String.class, "commentCount", false, "COMMENTCOUNT");
    public final static Property Guid = new Property(9, String.class, "guid", false, "GUID");
        public final static Property ShopId= new Property(10, String.class, "shopIp", false, "SHOPID");
}

    public HistoryDao(DaoConfig config) {
        super(config);
    }

    public HistoryDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        String constraint = ifNotExists ? "IF NOT EXISTS " : "";
        db.execSQL("CREATE TABLE " + constraint + "'History' (" + //
                "'_id' INTEGER PRIMARY KEY AUTOINCREMENT ," + // 0: key
                "'SHOPNAME' TEXT NOT NULL ," +
                "'IMGURL' TEXT ," +
                "'ISSELECT' INTEGER ," +
                "'MEDICINALNAME' TEXT NOT NULL ," +
                "'CURRENTPRICE' TEXT ," +
                "'DISCOUNTPRICE' TEXT ," +
                "'SALECOUNT' TEXT ," +
                "'COMMENTCOUNT' TEXT ," +
                "'GUID' TEXT ,"+
                "'SHOPID' TEXT );");

        // Add Indexes
//        db.execSQL("CREATE INDEX " + constraint + "IDX_ShopCar_NAME ON "+ TABLENAME +
//                " (NAME);");
//        db.execSQL("CREATE INDEX " + constraint + "IDX_ShopCar_CODE ON "+ TABLENAME +
//                " (CODE);");ＮＵＬＬ
        // initCreatedTableData(db);
    }

    public static void upgradeTable(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    public static void initCreatedTableData(SQLiteDatabase db) {
    }

    /**
     * Drops the underlying database table.
     */
    public static void dropTable(SQLiteDatabase db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "'History'";
        db.execSQL(sql);
    }


    @Override
    protected HistoryEntity readEntity(Cursor cursor, int offset) {
        HistoryEntity entity = new HistoryEntity();
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0)); // key
        entity.setShopName(cursor.getString(offset + 1));
        entity.setImgUrl(cursor.getString(offset + 2));
        entity.setIsSelect((cursor.getInt(offset + 3) == 1));
        entity.setMedicinalName(cursor.getString(offset + 4));
        entity.setCurrentPrice(cursor.getString(offset + 5));
        entity.setDiscountPrice(cursor.getString(offset + 6));
        entity.setSaleCount(cursor.getString(offset + 7));
        entity.setCommentCount(cursor.getString(offset + 8));
        entity.setGuid(cursor.getString(offset + 9));
        entity.setShopIp(cursor.getString(offset+10));
        return entity;
    }

    @Override
    protected Long readKey(Cursor cursor, int offset) {

        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }

    @Override
    protected void readEntity(Cursor cursor, HistoryEntity entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0)); // key
        entity.setShopName(cursor.getString(offset + 1));
        entity.setImgUrl(cursor.getString(offset + 2));
        entity.setIsSelect((cursor.getInt(offset + 3) == 1));
        entity.setMedicinalName(cursor.getString(offset + 4));
        entity.setCurrentPrice(cursor.getString(offset + 5));
        entity.setDiscountPrice(cursor.getString(offset + 6));
        entity.setSaleCount(cursor.getString(offset + 7));
        entity.setCommentCount(cursor.getString(offset + 8));
        entity.setGuid(cursor.getString(offset + 9));
        entity.setShopIp(cursor.getString(offset + 10));
    }

    @Override
    protected void bindValues(SQLiteStatement stmt, HistoryEntity entity) {
        stmt.clearBindings();
     //   Long key = entity.key;
       // if (key != null) {
         //   stmt.bindLong(1, key);
        //}
        stmt.bindString(2,entity.getShopName());
        stmt.bindString(3,entity.getImgUrl());
        stmt.bindLong(4, entity.isSelect() ? 1 : 0);
        stmt.bindString(5, entity.getMedicinalName());
        stmt.bindString(6, entity.getCurrentPrice());
        stmt.bindString(7, entity.getDiscountPrice());
        stmt.bindString(8, entity.getSaleCount());
        stmt.bindString(9, entity.getCommentCount());
        stmt.bindString(10,entity.getGuid());
        stmt.bindString(11,entity.getShopIp());
    }


    @Override
    protected Long updateKeyAfterInsert(HistoryEntity entity, long rowId) {
        return entity.getId();
    }

    @Override
    protected Long getKey(HistoryEntity entity) {
        if (entity != null) {
            return entity.getId();
        } else {
            return null;
        }
    }

    @Override
    protected boolean isEntityUpdateable() {
        return true;
    }

}
