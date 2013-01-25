package com.fizzbuzz.android.socialize;

import static com.fizzbuzz.android.util.VersionedStrictModeWrapper.Permission.ALLOW_DISK_READ;
import static com.fizzbuzz.android.util.VersionedStrictModeWrapper.Permission.ALLOW_DISK_WRITE;

import java.util.EnumSet;

import com.fizzbuzz.android.event.BusProvider;
import com.fizzbuzz.android.gcm.GcmMessageReceivedEvent;
import com.fizzbuzz.android.gcm.GcmRegUnregErrorEvent;
import com.fizzbuzz.android.gcm.GcmRegisteredEvent;
import com.fizzbuzz.android.gcm.GcmUnregisteredEvent;
import com.fizzbuzz.android.util.VersionedStrictModeWrapper;
import com.socialize.SmartAlertUtils;
import com.squareup.otto.Subscribe;

public class SocializeEventHandler {

    private static final SocializeEventHandler INSTANCE = new SocializeEventHandler();

    public static SocializeEventHandler getInstance() {
        return INSTANCE;
    }

    private SocializeEventHandler() {
        // register with the event bus
        BusProvider.getInstance().register(this);
    }

    @Subscribe
    public static void onGcmRegistered(final GcmRegisteredEvent event) {
        VersionedStrictModeWrapper.runWithStrictModeOverride(
                EnumSet.of(ALLOW_DISK_READ, ALLOW_DISK_WRITE),
                new Runnable() {
                    @Override
                    public void run() {
                        SmartAlertUtils.onRegister(event.getContext(), event.getRegistrationId());
                    }
                });
    }

    @Subscribe
    public static void onGcmUnregistered(final GcmUnregisteredEvent event) {
        VersionedStrictModeWrapper.runWithStrictModeOverride(
                EnumSet.of(ALLOW_DISK_READ, ALLOW_DISK_WRITE),
                new Runnable() {
                    @Override
                    public void run() {
                        SmartAlertUtils.onUnregister(event.getContext(), event.getRegistrationId());
                    }
                });
    }

    @Subscribe
    public static void onGcmError(GcmRegUnregErrorEvent event) {
        SmartAlertUtils.onError(event.getContext(), event.getErrorId());
    }

    @Subscribe
    public static void onGcmMessageReceived(GcmMessageReceivedEvent event) {
        SmartAlertUtils.onMessage(event.getContext(), event.getIntent());
    }
}