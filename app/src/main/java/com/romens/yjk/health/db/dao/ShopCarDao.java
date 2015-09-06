package com.romens.yjk.health.db.dao;


import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import com.romens.yjk.health.model.ShopCarTestEntity;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.internal.DaoConfig;

/**
 * Created by romens007 on 2015/8/28.
 */
public class ShopCarDao extends AbstractDao<ShopCarTestEntity,Long>{
    public static final String TABLENAME = "ShopCar";
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property Code = new Property(1, String.class, "code", false, "CODE");
        public final static Property Type = new Property(2, String.class, "type", false, "TYPE");
        public final static Property Name = new Property(3, String.class, "name", false, "NAME");
        public final static Property ImageUrl = new Property(4, String.class, "imageUrl", false, "IMAGE_URL");
        public final static Property Info = new Property(5, String.class, "infor", false, "INFOR");
        public final static Property Check = new Property(6, String.class, "check", false, "CHECK");
        public final static Property Price = new Property(7, double.class, "price", false, "PRICE");
        public final static Property Num=new Property(8,int.class,"num",false,"NUM");
       // public final static Property CreatedTime = new Property(8, int.class, "createdTime", false, "CREATED_TIME");
       // public final static Property UpdatedTime = new Property(9, int.class, "updatedTime", false, "UPDATED_TIME");
    }
    public ShopCarDao(DaoConfig config) {
        super(config);
    }
    public ShopCarDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    public static void createTable(SQLiteDatabase db,boolean ifNotExists){
        String constraint = ifNotExists ? "IF NOT EXISTS " : "";
        db.execSQL("CREATE TABLE " + constraint + "'ShopCar' (" + //
                "'_id' INTEGER PRIMARY KEY AUTOINCREMENT ," + // 0: id
                "'CODE' TEXT ," +
                "'TYPE' INTEGER NOT NULL ," +
                "'NAME' TEXT NOT NULL," +
                "'IMAGE_URL' TEXT NOT NULL ," +
                "'INFOR' TEXT NOT NULL ," +
                "'CHECK' TEXT NOT NULL ," +
                "'PRICE' TEXT NOT NULL ," +
                "'NUM' INTEGER NOT NULL ,"+
                "'CREATED_TIME' INTEGER ," +
                "'UPDATED_TIME' INTEGER  );");

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
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") +  "'ShopCar'";
        db.execSQL(sql);
    }



    @Override
    protected ShopCarTestEntity readEntity(Cursor cursor, int offset) {
        ShopCarTestEntity entity = new ShopCarTestEntity();
        entity.setId(cursor.isNull(offset + 1) ? null : cursor.getLong(offset + 1)); // id
        entity.code=cursor.getString(offset + 2);
        entity.type=cursor.getInt(offset + 3);
        entity.name=cursor.getString(offset + 4);
        entity.imageUrl=cursor.getString(offset + 5);
        entity.infor=cursor.getString(offset + 6);
        entity.check=cursor.getString(offset + 7);
        entity.price=cursor.getDouble(offset + 8);
        entity.num=cursor.getInt(offset + 9);
        //entity.createdTime=cursor.getInt(offset + 10);
       // entity.updatedTime=cursor.getInt(offset+11);
        return entity;
    }

    @Override
    protected Long readKey(Cursor cursor, int offset) {
       return cursor.isNull(offset + 1) ? null : cursor.getLong(offset + 1);
    }

    @Override
    protected void readEntity(Cursor cursor, ShopCarTestEntity entity, int offset) {
        entity.setId(cursor.isNull(offset + 1) ? null : cursor.getLong(offset + 1));
        entity.code=cursor.getString(offset + 2);
        entity.type=cursor.getInt(offset + 3);
        entity.name=cursor.getString(offset + 4);
        entity.imageUrl=cursor.getString(offset + 5);
        entity.infor=cursor.getString(offset + 6);
        entity.check=cursor.getString(offset + 7);
        entity.price=cursor.getDouble(offset + 8);
        entity.num=cursor.getInt(offset + 9);
        //entity.createdTime=cursor.getInt(offset + 10);
        //entity.updatedTime=cursor.getInt(offset+11);
    }

    @Override
    protected void bindValues(SQLiteStatement stmt, ShopCarTestEntity entity) {
        stmt.clearBindings();
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
        stmt.bindString(2, entity.code);
        stmt.bindLong(3, entity.type);
        stmt.bindString(4, entity.name);
        stmt.bindString(5, entity.imageUrl);
        stmt.bindString(6, entity.infor);
        stmt.bindString(7, entity.check);
        stmt.bindDouble(8, entity.price);
        stmt.bindLong(9, entity.num);
       // stmt.bindLong(10, entity.createdTime);
       // stmt.bindLong(11, entity.updatedTime);
    }

    @Override
    protected Long updateKeyAfterInsert(ShopCarTestEntity entity, long rowId) {
        return entity.id;
    }

    @Override
    protected Long getKey(ShopCarTestEntity entity) {
        if(entity!=null){
            return entity.id;
        }else {
            return null;
        }
    }

    @Override
    protected boolean isEntityUpdateable() {
        return true;
    }


}
