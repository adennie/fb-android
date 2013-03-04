package com.fizzbuzz.android.persist;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public abstract class DbHelper
        extends SQLiteOpenHelper {

    protected DbHelper(final Context appContext,
            final String dbName,
            final int dbSchemaVersion) {
        super(appContext, dbName, null, dbSchemaVersion);
    }

    @Override
    public void onOpen(final SQLiteDatabase db) {
        if (!db.isReadOnly()) {
            // Enable foreign key constraints
            db.execSQL("PRAGMA foreign_keys=ON;");
        }
    }

}
