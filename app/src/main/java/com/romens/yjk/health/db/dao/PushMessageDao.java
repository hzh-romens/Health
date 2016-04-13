package com.romens.yjk.health.db.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import com.romens.yjk.health.db.entity.PushMessageEntity;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.internal.DaoConfig;

/**
 * @author Zhou Lisi
 * @create 2016-04-13 22:19
 * @description
 */
public class PushMessageDao extends AbstractDao<PushMessageEntity, Long> {

    public static final String TABLENAME = "PUSH_MESSAGES";

    /**
     * Properties of entity DepartmentEntity.<br/>
     * Can be used for QueryBuilder and for referencing column names.
     */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property CreateDate = new Property(1, Long.class, "createDate", false, "CREATEDATE");
        public final static Property Title = new Property(2, String.class, "title", false, "TITLE");
        public final static Property Content = new Property(3, String.class, "content", false, "CONTENT");
        public final static Property Extras = new Property(4, String.class, "extras", false, "EXTRAS");
        public final static Property MessageId = new Property(5, Long.class, "messageId", false, "MESSAGEID");
    }


    public PushMessageDao(DaoConfig config) {
        super(config);
    }

    public PushMessageDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /**
     * Creates the underlying database table.
     */
    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        String constraint = ifNotExists ? "IF NOT EXISTS " : "";
        db.execSQL("CREATE TABLE " + constraint + "'" + TABLENAME + "' (" +
                "'_id' INTEGER PRIMARY KEY AUTOINCREMENT ," + // 0: key
                "'CREATEDATE' INTEGER NOT NULL," +
                "'TITLE' TEXT," +
                "'CONTENT' TEXT," +
                "'EXTRAS' TEXT ," +
                "'MESSAGEID' INTEGER );");
        // Add Indexes
        db.execSQL("CREATE INDEX " + constraint + "IDX_PUSH_MESSAGES_CREATEDATE ON PUSH_MESSAGES" +
                " (CREATEDATE);");

        db.execSQL("CREATE INDEX " + constraint + "IDX_PUSH_MESSAGES_MESSAGEID ON PUSH_MESSAGES" +
                " (MESSAGEID);");
    }

    /**
     * Drops the underlying database table.
     */
    public static void dropTable(SQLiteDatabase db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "'" + TABLENAME + "'";
        db.execSQL(sql);
    }

    public static void upgradeTable(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 78) {
            dropTable(db, true);
            createTable(db, false);
        }
    }

    /**
     * @inheritdoc
     */
    @Override
    protected void bindValues(SQLiteStatement stmt, PushMessageEntity entity) {
        stmt.clearBindings();

        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
        stmt.bindLong(2, entity.getCreate());
        stmt.bindString(3, entity.getTitle());
        stmt.bindString(4, entity.getContent());
        stmt.bindString(5, entity.getExtras());
        stmt.bindLong(6, entity.getMessageId());
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
    public PushMessageEntity readEntity(Cursor cursor, int offset) {
        PushMessageEntity entity = new PushMessageEntity();
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setCreate(cursor.getLong(offset + 1));
        entity.setTitle(cursor.getString(offset + 2));
        entity.setContent(cursor.getString(offset + 3));
        entity.setExtras(cursor.getString(offset + 4));
        entity.setMessageId(cursor.getLong(offset + 5));
        return entity;
    }

    /**
     * @inheritdoc
     */
    @Override
    public void readEntity(Cursor cursor, PushMessageEntity entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setCreate(cursor.getLong(offset + 1));
        entity.setTitle(cursor.getString(offset + 2));
        entity.setContent(cursor.getString(offset + 3));
        entity.setExtras(cursor.getString(offset + 4));
        entity.setMessageId(cursor.getLong(offset + 5));
    }

    /**
     * @inheritdoc
     */
    @Override
    protected Long updateKeyAfterInsert(PushMessageEntity entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }

    /**
     * @inheritdoc
     */
    @Override
    public Long getKey(PushMessageEntity entity) {
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
