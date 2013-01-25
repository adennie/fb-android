package com.fizzbuzz.android.gcm;

import static com.fizzbuzz.android.util.VersionedStrictModeWrapper.Permission.ALLOW_DISK_READ;

import java.util.concurrent.Callable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.content.Context;
import android.content.pm.ApplicationInfo;

import com.fizzbuzz.android.util.VersionedStrictModeWrapper;
import com.google.android.gcm.GCMRegistrar;

public class GcmRegistrationHelper {
    private final Logger mLogger = LoggerFactory.getLogger(LoggingManager.TAG);
    private final Context mAppContext;

    public GcmRegistrationHelper(Context appContext) {
        mAppContext = appContext;
    }

    public boolean checkSetup() {

        boolean result = false;
        try {
            // this throws UnsupportedOperationException if GCM not supported, e.g. on emulator virtual device without Google APIs)
            GCMRegistrar.checkDevice(mAppContext);

            // check that manifest is set up correctly for GCM (debug mode only)
            if ((mAppContext.getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0)
                GCMRegistrar.checkManifest(mAppContext);

            result = true;
        }
        catch (UnsupportedOperationException e) {
            mLogger.warn("GcmHelper.checkSetup: GCM not supported on this device (maybe this is an emulator virtual device without Google APIs?");
        }
        catch (IllegalStateException e) {
            mLogger.warn("GcmHelper.checkSetup: manifest not configured properly for GCM");
        }

        return result;
    }

    public boolean isRegistered() {
        return VersionedStrictModeWrapper.callWithStrictModeOverride(
                ALLOW_DISK_READ,
                new Callable<Boolean>() {
                    @Override
                    public Boolean call() {
                        return GCMRegistrar.isRegistered(mAppContext);
                    }
                });

    }

    public String getRegistrationId() {
        return GCMRegistrar.getRegistrationId(mAppContext);
    }

    public void register(String... senderIds) {
        GCMRegistrar.register(mAppContext, senderIds);
    }

    public boolean isRegisteredOnServer() {
        return GCMRegistrar.isRegisteredOnServer(mAppContext);
    }

    public void setRegisteredOnServer(final boolean flag) {
        GCMRegistrar.setRegisteredOnServer(mAppContext, flag);
    }
}
