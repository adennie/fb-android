package com.fizzbuzz.android.gcm;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.content.Context;
import android.content.Intent;

import com.fizzbuzz.android.gcm.GcmEvents.GcmMessageReceivedEvent;
import com.fizzbuzz.android.gcm.GcmEvents.GcmRegUnregErrorEvent;
import com.fizzbuzz.android.gcm.GcmEvents.GcmRegisteredEvent;
import com.fizzbuzz.android.gcm.GcmEvents.GcmUnregisteredEvent;
import com.fizzbuzz.ottoext.BusProvider;
import com.fizzbuzz.ottoext.GuaranteedDeliveryBus;
import com.fizzbuzz.ottoext.MainThreadBus;
import com.google.android.gcm.GCMBaseIntentService;
import com.squareup.otto.OttoBus;

public abstract class GcmBusIntentService
        extends GCMBaseIntentService {
    private final Logger mLogger = LoggerFactory.getLogger(LoggingManager.TAG);
    // use DeadEventThrowingBus to verify that all posted events are received by someone.
    private final OttoBus mBus = new GuaranteedDeliveryBus(new MainThreadBus(BusProvider.getInstance()));

    @Override
    protected void onError(Context context,
            String errorId) {
        mLogger.error("GCMIntentService.onError: {}", errorId);
        mBus.post(new GcmRegUnregErrorEvent(context, errorId));
    }

    @Override
    protected void onMessage(Context context,
            Intent intent) {
        mLogger.info("GCMIntentService.onMessage: received GCM message with intent {}", intent);
        mBus.post(new GcmMessageReceivedEvent(context, intent));
    }

    @Override
    protected void onRegistered(Context context,
            String regId) {
        mLogger.info("GCMIntentService.onRegistered: regId={}", regId);
        mBus.post(new GcmRegisteredEvent(context, regId));
    }

    @Override
    protected void onUnregistered(Context context,
            String regId) {
        mLogger.info("GCMIntentService.onUnregistered: regId={}", regId);
        mBus.post(new GcmUnregisteredEvent(context, regId));
    }
}
