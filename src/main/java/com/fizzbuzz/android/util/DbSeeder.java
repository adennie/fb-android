package com.fizzbuzz.android.util;

import android.database.sqlite.SQLiteDatabase;

public interface DbSeeder {
    public void seed(SQLiteDatabase db);
}
