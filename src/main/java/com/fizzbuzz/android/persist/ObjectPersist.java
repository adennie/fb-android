package com.fizzbuzz.android.persist;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.fizzbuzz.model.PersistentObject;
import com.google.common.collect.ImmutableMap;

public abstract class ObjectPersist<M extends PersistentObject> {
    // columns
    protected static final String SQLITE_ID = "_id";
    protected static final String ID = "server_id";
    protected static final String CREATION_DATE = "creation_date";
    protected static final String TICK_STAMP = "tick_stamp";

    private static final String WHERE_ID_EQUALS = ID + "=?";

    protected static enum ColumnType {
        KEY, FOREIGN_KEY, INTEGER_NOTNULL, INTEGER_UNIQUE_NOTNULL, TEXT, TEXT_NOTNULL, DATE
    };

    protected static class ForeignKeyInfo {
        ColumnType mType;
        String mTable;
        String mColumn;

        public ForeignKeyInfo(final ColumnType type,
                final String table,
                final String column) {
            mType = type;
            mTable = table;
            mColumn = column;
        }
    }

    // map column types to SQLite keywords
    private static final ImmutableMap<ColumnType, String> mSQLiteColumnTypes = ImmutableMap
            .<ColumnType, String> builder()
            .put(ColumnType.KEY, "INTEGER PRIMARY KEY AUTOINCREMENT")
            .put(ColumnType.INTEGER_NOTNULL, "INTEGER NOT NULL")
            .put(ColumnType.INTEGER_UNIQUE_NOTNULL, "INTEGER UNIQUE NOT NULL")
            .put(ColumnType.TEXT, "TEXT")
            .put(ColumnType.TEXT_NOTNULL, "TEXT NOT NULL")
            .put(ColumnType.DATE, "TIMESTAMP")
            .build();

    private final Logger mLogger = LoggerFactory.getLogger(LoggingManager.TAG);
    private final ImmutableMap<String, ColumnType> mColumnSpec;
    private final ImmutableMap<String, ForeignKeyInfo> mForeignKeySpec;
    private final String mTableName;
    private final SQLiteDatabase mDb;

    protected ObjectPersist(final SQLiteDatabase db,
            final String tableName,
            final ImmutableMap<String, ColumnType> columnSpec,
            final ImmutableMap<String, ForeignKeyInfo> foreignKeySpec) {
        mDb = checkNotNull(db, "db");
        mTableName = checkNotNull(tableName, "tableName");
        mColumnSpec = ImmutableMap.<String, ColumnType> builder()
                .put(SQLITE_ID, ColumnType.KEY)
                .put(ID, ColumnType.INTEGER_UNIQUE_NOTNULL)
                .put(CREATION_DATE, ColumnType.DATE)
                .put(TICK_STAMP, ColumnType.INTEGER_NOTNULL)
                .putAll(checkNotNull(columnSpec, "columnSpec"))
                .build();
        mForeignKeySpec = foreignKeySpec; // null OK
    }

    protected final String buildCreateTableQuery() {
        StringBuilder builder = new StringBuilder("CREATE TABLE " + mTableName + " (");
        StringBuilder fkBuilder = new StringBuilder();

        boolean first = true;
        for (Map.Entry<String, ColumnType> entry : mColumnSpec.entrySet()) {
            if (!first)
                builder.append(", ");
            first = false;

            switch (entry.getValue()) {
            case FOREIGN_KEY:
                ForeignKeyInfo fkInfo = mForeignKeySpec.get(entry.getKey());
                fkBuilder.append(", FOREIGN KEY (").append(entry.getKey()).append(") REFERENCES ")
                        .append(fkInfo.mTable).append("(").append(fkInfo.mColumn).append(")");
                builder.append(entry.getKey()).append(" ").append(mSQLiteColumnTypes.get(fkInfo.mType));

                break;
            default:
                builder.append(entry.getKey()).append(" ").append(mSQLiteColumnTypes.get(entry.getValue()));
                break;
            }
        }

        builder.append(fkBuilder).append(");");
        return builder.toString();
    }

    protected String buildSelectAllQuery() {
        String result;

        boolean first = true;
        StringBuilder builder = new StringBuilder("SELECT ");
        for (Map.Entry<String, ColumnType> entry : mColumnSpec.entrySet()) {
            if (!first)
                builder.append(", ");
            first = false;

            switch (entry.getValue()) {
            case DATE:
                builder.append("(strftime('%s', ").append(entry.getKey()).append(", 'unixepoch') * 1000) AS ")
                        .append(entry.getKey());
                break;
            default:
                builder.append(entry.getKey());
                break;
            }
        }

        builder.append(" FROM ").append(mTableName);
        result = builder.toString();
        return result;
    }

    public void createTable() {
        mDb.execSQL(buildCreateTableQuery());
    }

    public static boolean cursorIsLoaded(final Cursor c) {
        return (c != null);

    }

    public static boolean cursorIsEmpty(final Cursor c) {
        if (c == null)
            throw new IllegalStateException("cursor not loaded yet");

        return c.getCount() == 0;
    }

    protected Cursor selectAll() {
        Cursor c = mDb.rawQuery(buildSelectAllQuery(), new String[] {});
        c.moveToFirst();
        return c;
    }

    public M getFirst(final Cursor c) {
        checkNotNull(c, "cursor must not be null");

        M result = null;
        c.moveToFirst();
        if (c.getCount() > 0)
            result = getModelObjectFromCursor(c);

        return result;
    }

    public boolean insert(final M m) {

        boolean result = false;
        if (mDb.insert(mTableName, TICK_STAMP, toCV(m)) == -1) {
            mLogger.error("ObjectPersist.insert: insert failed");
        }
        else {
            result = true;
        }
        return result;
    }

    public void update(final M m) {
        if (mDb.update(mTableName, toCV(m), WHERE_ID_EQUALS, new String[] { Long.toString(m.getId()) }) == -1) {
            mLogger.error("ObjectPersist.update: update failed");
        }
    }

    public void delete(final M m) {
        if (mDb.delete(mTableName, WHERE_ID_EQUALS, new String[] { Long.toString(m.getId()) }) == -1) {
            mLogger.error("ObjectPersist.delete: delete failed");
        }
    }

    public void delete(String whereClause,
            String[] whereArgs) {
        if (mDb.delete(mTableName, whereClause, whereArgs) == -1) {
            mLogger.error("ObjectPersist.delete: delete failed");
        }
    }

    protected abstract M getModelObjectFromCursor(final Cursor c);

    protected abstract ContentValues toCV(final M m);
}
