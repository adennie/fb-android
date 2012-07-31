package com.fizzbuzz.android.util;

import static com.fizzbuzz.util.base.Reflections.newInstance;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Collection;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.fizzbuzz.model.PersistentObject;
import com.google.common.collect.ImmutableMap;

public abstract class CollectionPersist<M extends PersistentObject, CM extends Collection<M>>
        extends
        ObjectPersist<M> {
    private final Class<CM> mCollectionModelClass;

    protected CollectionPersist(final SQLiteDatabase db, final String tableName,
            final ImmutableMap<String, ColumnType> columnSpec,
            final ImmutableMap<String, ForeignKeyInfo> foreignKeySpec, final Class<CM> collectionModelClass) {
        super(db, tableName, columnSpec, foreignKeySpec);
        mCollectionModelClass = checkNotNull(collectionModelClass, "collectionModelClass");
    }

    protected CM getAll() {
        Cursor c = selectAll();

        CM collection = newInstance(mCollectionModelClass);
        if (c.moveToFirst()) {
            do {
                M m = getModelObjectFromCursor(c);
                collection.add(m);
            }
            while (c.moveToNext());
        }
        c.close();
        return collection;

    }

    public void insert(final CM collection) {
        for (M m : collection) {
            insert(m);
        }
    }

    public void update(final CM collection) {
        for (M m : collection) {
            update(m);
        }
    }

    public void delete(final CM collection) {
        for (M m : collection) {
            delete(m);
        }
    }

}
