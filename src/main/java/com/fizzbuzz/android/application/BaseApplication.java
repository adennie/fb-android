package com.fizzbuzz.android.application;

import android.app.Application;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager.NameNotFoundException;

import com.fizzbuzz.android.persist.SharedPreferencesUtils;
import com.fizzbuzz.android.util.VersionedStrictModeWrapper;

public abstract class BaseApplication
        extends Application {

    private static String PREF_TAG_CURRENT_APP_VERSION = "currentAppVersion";
    private static String PREF_TAG_PREVIOUS_APP_VERSION = "previousAppVersion";

    @Override
    public void onCreate() {
        super.onCreate();

        // debug mode stuff
        if (isDebugMode()) {
            VersionedStrictModeWrapper.getInstance().init(this); // turn on strict mode
        }

        // make sure AsyncTask's static members get initialized on a UI thread
        // (http://code.google.com/p/android/issues/detail?id=20915)
        try {
            Class.forName("android.os.AsyncTask");
        } catch (ClassNotFoundException e) {
        }
    }

    // convenience method, analogous to Application.getString
    public final int getInteger(final int resId) {
        return getResources().getInteger(resId);
    }

    public boolean isDebugMode() {
        return (getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE) == 1;
    }

    protected void handleNewInstallsAndUpgrades() {
        if (isNewInstall())
            processNewInstall(getVersionCode());
        else
            processUpgrade(getVersionCode(), readVersionCodeFromPref(PREF_TAG_PREVIOUS_APP_VERSION));

        updateSavedVersionCodes();
    }

    private boolean isNewInstall() {
        return readVersionCodeFromPref(PREF_TAG_CURRENT_APP_VERSION) == -1;
    }

    abstract protected void processNewInstall(final int newVersionCode);

    abstract protected void processUpgrade(final int newVersionCode,
            final int prevInstallVersionCode);

    private void updateSavedVersionCodes() {
        // copy the previous value of the version code for later reference before we overwrite it
        saveVersionCodeToPref(PREF_TAG_PREVIOUS_APP_VERSION, readVersionCodeFromPref(PREF_TAG_CURRENT_APP_VERSION));

        // save the current version code so it can be used by the line of code above, the next time we upgrade.
        saveVersionCodeToPref(PREF_TAG_CURRENT_APP_VERSION, getVersionCode());
    }

    private void saveVersionCodeToPref(final String prefTag,
            final int versionCode) {
        SharedPreferencesUtils.setLongNoStrict(this, prefTag, versionCode);
    }

    private int readVersionCodeFromPref(final String prefTag) {
        return (int) SharedPreferencesUtils.getLongNoStrict(this, prefTag, -1);
    }

    private int getVersionCode() {
        int result = 0;
        try
        {
            result = getApplicationContext().getPackageManager().getPackageInfo(
                    getApplicationContext().getPackageName(), 0).versionCode;
        } catch (NameNotFoundException e)
        {
            // squelch
        }
        return result;
    }
}
