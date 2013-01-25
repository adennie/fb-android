package com.fizzbuzz.android.gcm;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.content.Context;
import android.content.Intent;

import com.fizzbuzz.android.event.BusProvider;
import com.google.android.gcm.GCMBaseIntentService;

public abstract class GCMBusIntentService
        extends GCMBaseIntentService {
    private final Logger mLogger = LoggerFactory.getLogger(LoggingManager.TAG);

    @Override
    protected void onError(Context context,
            String errorId) {
        mLogger.error("GCMIntentService.onError: {}", errorId);
        BusProvider.getInstance().post(new GcmRegUnregErrorEvent(context, errorId));
    }

    @Override
    protected void onMessage(Context context,
            Intent intent) {
        mLogger.info("GCMIntentService.onMessage: received GCM message with intent {}", intent);
        BusProvider.getInstance().post(new GcmMessageReceivedEvent(context, intent));
    }

    @Override
    protected void onRegistered(Context context,
            String regId) {
        mLogger.info("GCMIntentService.onRegistered: regId={}", regId);
        BusProvider.getInstance().post(new GcmRegisteredEvent(context, regId));
    }

    @Override
    protected void onUnregistered(Context context,
            String regId) {
        mLogger.info("GCMIntentService.onUnregistered: regId={}", regId);
        BusProvider.getInstance().post(new GcmUnregisteredEvent(context, regId));
    }
}
