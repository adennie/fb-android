package com.fizzbuzz.android.persist;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import com.fizzbuzz.android.util.StrictModeWrapper;
import com.fizzbuzz.android.util.StrictModeWrapper.Permission;

import javax.inject.Inject;
import java.util.EnumSet;
import java.util.concurrent.Callable;

public class SharedPrefHelper {
    @Inject StrictModeWrapper mStrictMode;

    public boolean getBooleanNoStrict(final Context context,
            final String tag,
            final boolean defValue) {
        boolean result = mStrictMode.callWithStrictModeOverride(
                Permission.ALLOW_DISK_READ,
                new Callable<Boolean>() {
                    @Override
                    public Boolean call() {
                        SharedPreferences prefs = context.getSharedPreferences(context.getClass().getSimpleName(), Context.MODE_PRIVATE);
                        return prefs.getBoolean(tag, defValue);
                    }
                });

        return result;
    }

    public void setBooleanNoStrict(final Context context,
            final String tag,
            final boolean value) {
        mStrictMode.runWithStrictModeOverride(
                Permission.ALLOW_DISK_WRITE,
                new Runnable() {
                    @Override
                    public void run() {
                        SharedPreferences prefs = context.getSharedPreferences(context.getClass().getSimpleName(), Context.MODE_PRIVATE);
                        prefs.edit().putBoolean(tag, value).commit();
                    }
                });
    }

    public long getLongNoStrict(final Context context,
            final String tag,
            final long defValue) {
        long result = mStrictMode.callWithStrictModeOverride(
                Permission.ALLOW_DISK_READ,
                new Callable<Long>() {
                    @Override
                    public Long call() {
                        SharedPreferences prefs = context.getSharedPreferences(context.getClass().getSimpleName(), Context.MODE_PRIVATE);
                        return prefs.getLong(tag, defValue);
                    }
                });

        return result;
    }

    public void setLongNoStrict(final Context context,
            final String tag,
            final long value) {
        mStrictMode.runWithStrictModeOverride(
                Permission.ALLOW_DISK_WRITE,
                new Runnable() {
                    @Override
                    public void run() {
                        SharedPreferences prefs = context.getSharedPreferences(context.getClass().getSimpleName(), Context.MODE_PRIVATE);
                        prefs.edit().putLong(tag, value).commit();
                    }
                });
    }

    public String getStringNoStrict(final Context context,
            final String tag,
            final String defValue) {
        String result = mStrictMode.callWithStrictModeOverride(
                Permission.ALLOW_DISK_READ,
                new Callable<String>() {
                    @Override
                    public String call() {
                        SharedPreferences prefs = context.getSharedPreferences(context.getClass().getSimpleName(), Context.MODE_PRIVATE);
                        return prefs.getString(tag, defValue);
                    }
                });

        return result;
    }

    public void setStringNoStrict(final Context context,
            final String tag,
            final String value) {
        mStrictMode.runWithStrictModeOverride(
                Permission.ALLOW_DISK_WRITE,
                new Runnable() {
                    @Override
                    public void run() {
                        SharedPreferences prefs = context.getSharedPreferences(context.getClass().getSimpleName(), Context.MODE_PRIVATE);
                        prefs.edit().putString(tag, value).commit();
                    }
                });
    }

    public void setDefaultValuesNoStrict(final Context context,
            final int resourceId,
            final boolean readAgain) {
        mStrictMode.runWithStrictModeOverride(
                EnumSet.of(Permission.ALLOW_DISK_READ, Permission.ALLOW_DISK_WRITE),
                new Runnable() {
                    @Override
                    public void run() {
                        PreferenceManager.setDefaultValues(context, resourceId, readAgain);
                    }
                });
    }
}