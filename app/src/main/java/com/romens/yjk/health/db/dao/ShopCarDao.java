package com.romens.yjk.health.db.dao;


import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import com.romens.yjk.health.model.ShopCarEntity;
import com.romens.yjk.health.model.ShopCarTestEntity;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.internal.DaoConfig;

/**
 * Created by romens007 on 2015/8/28.
 */
public class ShopCarDao extends AbstractDao<ShopCarEntity, Long> {
    public static final String TABLENAME = "ShopCar";

    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property USERGUID = new Property(1, String.class, "USERGUID", false, "USERGUID");
        public final static Property GUID = new Property(2, String.class, "GUID", false, "GUID");
        public final static Property GOODSGUID = new Property(3, String.class, "GOODSGUID", false, "GOODSGUID");
        public final static Property BUYCOUNT = new Property(4, int.class, "BUYCOUNT", false, "BUYCOUNT");
        public final static Property GOODSPRICE = new Property(5, double.class, "GOODSPRICE", false, "GOODSPRICE");
        public final static Property CREATETIME = new Property(6, String.class, "CREATETIME", false, "CREATETIME");
        public final static Property NAME = new Property(7, String.class, "NAME", false, "NAME");
        public final static Property GOODSCLASSNAME = new Property(8, String.class, "GOODSCLASSNAME", false, "GOODSCLASSNAME");
        public final static Property CODE = new Property(9, String.class, "CODE", false, "CODE");
        public final static Property GOODURL = new Property(10, String.class, "GOODURL", false, "GOODURL");
        public final static Property DETAILDESCRIPTION = new Property(11, String.class, "DETAILDESCRIPTION", false, "DETAILDESCRIPTION");
        public final static Property SPEC = new Property(12, String.class, "SPEC", false, "SPEC");
        public final static Property GOODSSORTGUID = new Property(13, String.class, "GOODSSORTGUID", false, "GOODSSORTGUID");
        public final static Property NUM = new Property(14, int.class, "NUM", false, "NUM");
        public final static Property CHECK = new Property(15, String.class, "CHECK", false, "CHECK");
        public final static Property CreatedTime = new Property(16, int.class, "createdTime", false, "CREATED_TIME");
        public final static Property UpdatedTime = new Property(17, int.class, "updatedTime", false, "UPDATED_TIME");
    }

    public ShopCarDao(DaoConfig config) {
        super(config);
    }

    public ShopCarDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        String constraint = ifNotExists ? "IF NOT EXISTS " : "";
        db.execSQL("CREATE TABLE " + constraint + "'ShopCar' (" + //
                "'_id' INTEGER PRIMARY KEY AUTOINCREMENT ," + // 0: id
                "'USERGUID' TEXT ," +
                "'GUID' INTEGER ," +
                "'GOODSGUID' TEXT ," +
                "'BUYCOUNT' INTEGER ," +
                "'GOODSPRICE' INTEGER ," +
                "'CREATETIME' TEXT ," +
                "'NAME' TEXT ," +
                "'GOODSCLASSNAME' TEXT ," +
                "'CODE' TEXT ," +
                "'GOODURL' TEXT ," +
                "'DETAILDESCRIPTION' TEXT ," +
                "'SPEC' TEXT ," +
                "'GOODSSORTGUID' TEXT ," +
                "'NUM' INTEGER ," +
                "'CHECK' TEXT ," +
                "'CREATED_TIME' INTEGER ," +
                "'UPDATED_TIME' INTEGER );");

        // Add Indexes
//        db.execSQL("CREATE INDEX " + constraint + "IDX_ShopCar_NAME ON "+ TABLENAME +
//                " (NAME);");
//        db.execSQL("CREATE INDEX " + constraint + "IDX_ShopCar_CODE ON "+ TABLENAME +
//                " (CODE);");
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
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "'ShopCar'";
        db.execSQL(sql);
    }


    @Override
    protected ShopCarEntity readEntity(Cursor cursor, int offset) {
        ShopCarEntity entity = new ShopCarEntity();
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0)); // id
        entity.setUSERGUID(cursor.getString(offset + 1));
        entity.setGUID(cursor.getString(offset + 2));
        entity.setGOODSGUID(cursor.getString(offset + 3));
        entity.setBUYCOUNT(cursor.isNull(offset + 4) ? null : cursor.getInt(offset + 4));
        entity.setGOODSPRICE(cursor.isNull(offset + 5) ? null : cursor.getDouble(offset + 5)); // Price
        entity.setCREATETIME(cursor.getString(offset + 6));
        entity.setNAME(cursor.getString(offset + 7));
        entity.setGOODSCLASSNAME(cursor.getString(offset + 8));
        entity.setCODE(cursor.getString(offset + 9));
        entity.setGOODURL(cursor.getString(offset + 10));
        entity.setDETAILDESCRIPTION(cursor.getString(offset + 11));
        entity.setSPEC(cursor.getString(offset + 12));
        entity.setGOODSSORTGUID(cursor.getString(offset + 13));
        entity.setNUM(cursor.getInt(offset + 14));
        entity.setCHECK(cursor.getString(offset + 15));
        entity.setCreatedTime(cursor.getInt(offset + 16));
        entity.setUpdatedTime(cursor.getInt(offset + 17));
        return entity;
    }

    @Override
    protected Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }

    @Override
    protected void readEntity(Cursor cursor, ShopCarEntity entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0)); // id
        entity.setUSERGUID(cursor.getString(offset + 1));
        entity.setGUID(cursor.getString(offset + 2));
        entity.setGOODSGUID(cursor.getString(offset + 3));
        entity.setBUYCOUNT(cursor.isNull(offset + 4) ? null : cursor.getInt(offset + 4));
        entity.setGOODSPRICE(cursor.isNull(offset + 5) ? null : cursor.getDouble(offset + 5)); // Price
        entity.setCREATETIME(cursor.getString(offset + 6));
        entity.setNAME(cursor.getString(offset + 7));
        entity.setGOODSCLASSNAME(cursor.getString(offset + 8));
        entity.setCODE(cursor.getString(offset + 9));
        entity.setGOODURL(cursor.getString(offset + 10));
        entity.setDETAILDESCRIPTION(cursor.getString(offset + 11));
        entity.setSPEC(cursor.getString(offset + 12));
        entity.setGOODSSORTGUID(cursor.getString(offset + 13));
        entity.setNUM(cursor.getInt(offset + 14));
        entity.setCHECK(cursor.getString(offset + 15));
        entity.setCreatedTime(cursor.getInt(offset + 16));
        entity.setUpdatedTime(cursor.getInt(offset + 17));
    }

    @Override
    protected void bindValues(SQLiteStatement stmt, ShopCarEntity entity) {
        stmt.clearBindings();
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }

        stmt.bindString(2, entity.getUSERGUID());
       if(entity.getGUID()!=null) {
            stmt.bindString(3, entity.getGUID());
        }
        stmt.bindString(4, entity.getGOODSGUID());
        stmt.bindLong(5, entity.getBUYCOUNT());
        stmt.bindDouble(6, entity.getGOODSPRICE());
        if(entity.getCREATETIME()!=null) {
            stmt.bindString(7, entity.getCREATETIME());
        }
        stmt.bindString(8, entity.getNAME());
        stmt.bindString(9, entity.getGOODSCLASSNAME());
        stmt.bindString(10, entity.getCODE());
        stmt.bindString(11, entity.getGOODURL());
        stmt.bindString(12, entity.getDETAILDESCRIPTION());
        if(entity.getSPEC()!=null) {
            //stmt.bindString(13, entity.getSPEC());
            stmt.bindString(13,"2");
        }
        stmt.bindString(14, entity.getGOODSSORTGUID());
        stmt.bindLong(15, entity.getNUM());
        stmt.bindString(16, entity.getCHECK());
        stmt.bindLong(17, entity.createdTime);
        stmt.bindLong(18, entity.updatedTime);
    }

    @Override
    protected Long updateKeyAfterInsert(ShopCarEntity entity, long rowId) {
        return entity.id;
    }

    @Override
    protected Long getKey(ShopCarEntity entity) {
        if (entity != null) {
            return entity.id;
        } else {
            return null;
        }
    }

    @Override
    protected boolean isEntityUpdateable() {
        return true;
    }


}
