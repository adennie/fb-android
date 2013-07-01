package com.fizzbuzz.android.application;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import com.fizzbuzz.android.dagger.InjectingApplication;
import com.fizzbuzz.android.persist.SharedPrefHelper;
import com.fizzbuzz.android.util.StrictModeWrapper;
import com.fizzbuzz.android.util.VersionedStrictModeWrapper;
import dagger.Module;
import dagger.Provides;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;

public abstract class BaseApplication
        extends InjectingApplication {

    @Inject StrictModeWrapper mStrictMode;
    @Inject SharedPrefHelper mSharedPrefHelper;

    private static String PREF_TAG_CURRENT_APP_VERSION = "currentAppVersion";
    private static String PREF_TAG_PREVIOUS_APP_VERSION = "previousAppVersion";

    @Override
    public void onCreate() {
        super.onCreate();

        // debug mode stuff
        if (isDebugMode()) {
            mStrictMode.init(this); // turn on strict mode
        }

        // make sure AsyncTask's static members get initialized on a UI thread
        // (http://code.google.com/p/android/issues/detail?id=20915)
        try {
            Class.forName("android.os.AsyncTask");
        } catch (ClassNotFoundException e) {
        }
    }

    @Override
    protected List<Object> getModules() {
        List<Object> modules = super.getModules();
        modules.add(new BaseApplicationModule(this));
        return modules;
    }

    // convenience method, analogous to Application.getString
    public final int getInteger(final int resId) {
        return getResources().getInteger(resId);
    }

    public boolean isDebugMode() {
        return (getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE) == 1;
    }

    protected void handleNewInstallsAndUpgrades() {
        int savedVersion = readVersionCodeFromPref(PREF_TAG_CURRENT_APP_VERSION);
        if (savedVersion == -1)
            processNewInstall(getVersionCode());
        else if (savedVersion != getVersionCode())
            processUpgrade(getVersionCode(), readVersionCodeFromPref(PREF_TAG_PREVIOUS_APP_VERSION));
    }

    protected void processNewInstall(final int newVersionCode) {
        updateSavedVersionCodes();
    }

    protected void processUpgrade(final int newVersionCode,
                                  final int prevInstallVersionCode) {
        updateSavedVersionCodes();
    }

    private void updateSavedVersionCodes() {
        // copy the previous value of the version code for later reference before we overwrite it
        saveVersionCodeToPref(PREF_TAG_PREVIOUS_APP_VERSION, readVersionCodeFromPref(PREF_TAG_CURRENT_APP_VERSION));

        // save the current version code so it can be used by the line of code above, the next time we upgrade.
        saveVersionCodeToPref(PREF_TAG_CURRENT_APP_VERSION, getVersionCode());
    }

    private void saveVersionCodeToPref(final String prefTag,
                                       final int versionCode) {
        mSharedPrefHelper.setLongNoStrict(this, prefTag, versionCode);
    }

    private int readVersionCodeFromPref(final String prefTag) {
        return (int) mSharedPrefHelper.getLongNoStrict(this, prefTag, -1);
    }

    private int getVersionCode() {
        int result = 0;
        try {
            result = getApplicationContext().getPackageManager().getPackageInfo(
                    getApplicationContext().getPackageName(), 0).versionCode;
        } catch (NameNotFoundException e) {
            // squelch
        }
        return result;
    }

    @Module(library = true,
            includes=VersionedStrictModeWrapper.StrictModeWrapperModule.class)
    public class BaseApplicationModule {
        private final BaseApplication mBaseApplication;

        public BaseApplicationModule(final BaseApplication baseApplication) {
            mBaseApplication = baseApplication;
        }

        @Provides
        @Singleton
        public BaseApplication provideBaseApplication() {
            return mBaseApplication;
        }

    }


}
