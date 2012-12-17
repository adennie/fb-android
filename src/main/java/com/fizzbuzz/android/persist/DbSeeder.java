package com.fizzbuzz.android.persist;

import android.database.sqlite.SQLiteDatabase;

public interface DbSeeder {
    public void seed(SQLiteDatabase db);
}
