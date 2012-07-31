package com.fizzbuzz.android.util;

import static com.fizzbuzz.android.util.VersionedStrictModeWrapper.Permission.ALLOW_DISK_READ;
import static com.fizzbuzz.android.util.VersionedStrictModeWrapper.Permission.ALLOW_DISK_WRITE;

import java.util.EnumSet;
import java.util.concurrent.Callable;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class SharedPreferencesUtils {
    public static boolean getBooleanNoStrict(final Activity activity, final String tag, final boolean defValue) {
        boolean result = VersionedStrictModeWrapper.callWithStrictModeOverride(
                ALLOW_DISK_READ,
                new Callable<Boolean>() {
                    @Override
                    public Boolean call() {
                        SharedPreferences prefs = activity.getPreferences(Context.MODE_PRIVATE);
                        return prefs.getBoolean(tag, defValue);
                    }
                });

        return result;
    }

    public static void setBooleanNoStrict(final Activity activity, final String tag, final boolean value) {
        VersionedStrictModeWrapper.runWithStrictModeOverride(
                ALLOW_DISK_WRITE,
                new Runnable() {
                    @Override
                    public void run() {
                        SharedPreferences prefs = activity.getPreferences(Context.MODE_PRIVATE);
                        prefs.edit().putBoolean(tag, value).commit();
                    }
                });
    }

    public static void setDefaultValuesNoStrict(final Context context, final int resourceId, final boolean readAgain) {
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