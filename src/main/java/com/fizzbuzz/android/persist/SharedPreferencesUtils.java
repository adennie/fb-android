package com.fizzbuzz.android.persist;

import static com.fizzbuzz.android.util.VersionedStrictModeWrapper.Permission.ALLOW_DISK_READ;
import static com.fizzbuzz.android.util.VersionedStrictModeWrapper.Permission.ALLOW_DISK_WRITE;

import java.util.EnumSet;
import java.util.concurrent.Callable;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.fizzbuzz.android.util.VersionedStrictModeWrapper;

public class SharedPreferencesUtils {

    public static boolean getBooleanNoStrict(final Context context,
            final String tag,
            final boolean defValue) {
        boolean result = VersionedStrictModeWrapper.callWithStrictModeOverride(
                ALLOW_DISK_READ,
                new Callable<Boolean>() {
                    @Override
                    public Boolean call() {
                        SharedPreferences prefs = context.getSharedPreferences(context.getClass().getSimpleName(), Context.MODE_PRIVATE);
                        return prefs.getBoolean(tag, defValue);
                    }
                });

        return result;
    }

    public static void setBooleanNoStrict(final Context context,
            final String tag,
            final boolean value) {
        VersionedStrictModeWrapper.runWithStrictModeOverride(
                ALLOW_DISK_WRITE,
                new Runnable() {
                    @Override
                    public void run() {
                        SharedPreferences prefs = context.getSharedPreferences(context.getClass().getSimpleName(), Context.MODE_PRIVATE);
                        prefs.edit().putBoolean(tag, value).commit();
                    }
                });
    }

    public static long getLongNoStrict(final Context context,
            final String tag,
            final long defValue) {
        long result = VersionedStrictModeWrapper.callWithStrictModeOverride(
                ALLOW_DISK_READ,
                new Callable<Long>() {
                    @Override
                    public Long call() {
                        SharedPreferences prefs = context.getSharedPreferences(context.getClass().getSimpleName(), Context.MODE_PRIVATE);
                        return prefs.getLong(tag, defValue);
                    }
                });

        return result;
    }

    public static void setLongNoStrict(final Context context,
            final String tag,
            final long value) {
        VersionedStrictModeWrapper.runWithStrictModeOverride(
                ALLOW_DISK_WRITE,
                new Runnable() {
                    @Override
                    public void run() {
                        SharedPreferences prefs = context.getSharedPreferences(context.getClass().getSimpleName(), Context.MODE_PRIVATE);
                        prefs.edit().putLong(tag, value).commit();
                    }
                });
    }

    public static String getStringNoStrict(final Context context,
            final String tag,
            final String defValue) {
        String result = VersionedStrictModeWrapper.callWithStrictModeOverride(
                ALLOW_DISK_READ,
                new Callable<String>() {
                    @Override
                    public String call() {
                        SharedPreferences prefs = context.getSharedPreferences(context.getClass().getSimpleName(), Context.MODE_PRIVATE);
                        return prefs.getString(tag, defValue);
                    }
                });

        return result;
    }

    public static void setStringNoStrict(final Context context,
            final String tag,
            final String value) {
        VersionedStrictModeWrapper.runWithStrictModeOverride(
                ALLOW_DISK_WRITE,
                new Runnable() {
                    @Override
                    public void run() {
                        SharedPreferences prefs = context.getSharedPreferences(context.getClass().getSimpleName(), Context.MODE_PRIVATE);
                        prefs.edit().putString(tag, value).commit();
                    }
                });
    }

    public static void setDefaultValuesNoStrict(final Context context,
            final int resourceId,
            final boolean readAgain) {
        VersionedStrictModeWrapper.runWithStrictModeOverride(
                EnumSet.of(ALLOW_DISK_READ, ALLOW_DISK_WRITE),
                new Runnable() {
                    @Override
                    public void run() {
                        PreferenceManager.setDefaultValues(context, resourceId, readAgain);
                    }
                });
    }
}