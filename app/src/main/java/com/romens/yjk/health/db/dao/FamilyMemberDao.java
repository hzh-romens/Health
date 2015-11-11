package com.romens.yjk.health.db.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import com.romens.yjk.health.db.DBInterface;
import com.romens.yjk.health.db.entity.FamilyMemberEntity;
import com.romens.yjk.health.db.entity.RemindEntity;

import java.util.List;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.internal.DaoConfig;

/**
 * Created by anlc on 2015/11/9.
 */
public class FamilyMemberDao extends AbstractDao<FamilyMemberEntity, Long> {

    public static final String TABLENAME = "FamilyMember";

    /**
     * Properties of entity DiscoveryEntity.<br/>
     * Can be used for QueryBuilder and for referencing column names.
     */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "key", true, "_id");
        public final static Property Name = new Property(1, String.class, "name", false, "NAME");
        public final static Property Sex = new Property(2, String.class, "sex", false, "SEX");
        public final static Property Birthday = new Property(3, String.class, "birthday", false, "BIRTHDAY");
        public final static Property Age = new Property(4, String.class, "age", false, "AGE");
    }

    public FamilyMemberDao(DaoConfig config) {
        super(config);
    }

    public FamilyMemberDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /**
     * Creates the underlying database table.
     */
    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        String constraint = ifNotExists ? "IF NOT EXISTS " : "";
        db.execSQL("CREATE TABLE " + constraint + "'" + TABLENAME + "' (" +
                "'_id' INTEGER PRIMARY KEY AUTOINCREMENT ," + // 0: key
                "'NAME' TEXT NOT NULL ," + // 2: user
                "'SEX' TEXT NOT NULL ," + // 3: drug
                "'BIRTHDAY' TEXT NOT NULL ," + // 4: count
                "'AGE' TEXT NOT NULL );"); // 11: times
        // Add Indexes
        db.execSQL("CREATE INDEX " + constraint + "IDX_FamilyMember_KEY ON " + TABLENAME +
                " (_id);");
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
    protected void bindValues(SQLiteStatement stmt, FamilyMemberEntity entity) {
        stmt.clearBindings();
        stmt.bindString(2, entity.getName());
        stmt.bindString(3, entity.getSex());
        stmt.bindString(4, entity.getBirthday());
        stmt.bindString(5, entity.getAge());
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
    public FamilyMemberEntity readEntity(Cursor cursor, int offset) {
        FamilyMemberEntity entity = new FamilyMemberEntity();
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0)); // key
        entity.setName(cursor.getString(offset + 1));// user
        entity.setSex(cursor.getString(offset + 2)); // drug
        entity.setBirthday(cursor.getString(offset + 3)); // count
        entity.setAge(cursor.getString(offset + 4)); // count
        return entity;
    }

    /**
     * @inheritdoc
     */
    @Override
    public void readEntity(Cursor cursor, FamilyMemberEntity entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setName(cursor.getString(offset + 1));// user
        entity.setSex(cursor.getString(offset + 2)); // drug
        entity.setBirthday(cursor.getString(offset + 3)); // count
        entity.setAge(cursor.getString(offset + 4)); // count
    }

    /**
     * @inheritdoc
     */
    @Override
    protected Long updateKeyAfterInsert(FamilyMemberEntity entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }

    /**
     * @inheritdoc
     */
    @Override
    public Long getKey(FamilyMemberEntity entity) {
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