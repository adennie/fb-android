package com.fizzbuzz.android.util;

import static com.google.common.base.Preconditions.checkNotNull;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public abstract class DbHelper
        extends SQLiteOpenHelper {

    @SuppressWarnings("unused")
    // mAppContext could be useful in the future to check whether running in debug mode and do
    // something special in that case,
    // e.g. if (mAppContext.getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0) {}
    private final Context mAppContext;

    protected DbHelper(final Context appContext, final String dbName, final int dbSchemaVersion) {
        super(appContext, dbName, null, dbSchemaVersion);

        mAppContext = checkNotNull(appContext, "appContext");
    }

    @Override
    public void onOpen(final SQLiteDatabase db) {
        if (!db.isReadOnly()) {
            // Enable foreign key constraints
            db.execSQL("PRAGMA foreign_keys=ON;");
        }
    }

}
