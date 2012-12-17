package com.fizzbuzz.android.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ApplicationInfo;

import com.google.android.gcm.GCMRegistrar;

public class GcmHelper {
    private final Logger mLogger = LoggerFactory.getLogger(LoggingManager.TAG);
    private final String[] mSenderIds;

    public GcmHelper(String... senderIds) {
        mSenderIds = senderIds;
    }

    public boolean checkSetup(Activity activity) {

        boolean result = false;
        try {
            // this throws UnsupportedOperationException if GCM not supported, e.g. on emulator virtual device without Google APIs)
            GCMRegistrar.checkDevice(activity);

            // check that manifest is set up correctly for GCM (debug mode only)
            if ((activity.getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0)
                GCMRegistrar.checkManifest(activity);

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

    public void registerIfNeeded(Context appContext) {
        // see if we've already done the GCM registration
        String regId = GCMRegistrar.getRegistrationId(appContext);

        // if not, register
        if (regId.equals("")) {
            GCMRegistrar.register(appContext, mSenderIds);
        }
    }
}
