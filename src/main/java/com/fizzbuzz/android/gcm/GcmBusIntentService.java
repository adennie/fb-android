package com.fizzbuzz.android.gcm;

import javax.inject.Inject;

import com.fizzbuzz.android.application.BusApplicationModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.content.Context;
import android.content.Intent;

import com.fizzbuzz.android.injection.Injector;
import com.fizzbuzz.android.gcm.GcmEvents.GcmMessageReceivedEvent;
import com.fizzbuzz.android.gcm.GcmEvents.GcmRegUnregErrorEvent;
import com.fizzbuzz.android.gcm.GcmEvents.GcmRegisteredEvent;
import com.fizzbuzz.android.gcm.GcmEvents.GcmUnregisteredEvent;
import com.fizzbuzz.ottoext.GuaranteedDeliveryBus;
import com.google.android.gcm.GCMBaseIntentService;

public class GcmBusIntentService
        extends GCMBaseIntentService {
    @Inject @BusApplicationModule.ApplicationScopedMainThread
    GuaranteedDeliveryBus mGuaranteedDeliveryBus;

    private final Logger mLogger = LoggerFactory.getLogger(LoggingManager.TAG);

    @Override
    public void onCreate() {
        ((Injector) getApplication()).getObjectGraph().inject(this);

    };

    @Override
    protected void onError(Context context,
            String errorId) {
        mLogger.error("GCMIntentService.onError: {}", errorId);
        mGuaranteedDeliveryBus.postGuaranteed(new GcmRegUnregErrorEvent(context, errorId));
    }

    @Override
    protected void onMessage(Context context,
            Intent intent) {
        mLogger.info("GCMIntentService.onMessage: received GCM message with intent {}", intent);
        mGuaranteedDeliveryBus.postGuaranteed(new GcmMessageReceivedEvent(context, intent));
    }

    @Override
    protected void onRegistered(Context context,
            String regId) {
        mLogger.info("GCMIntentService.onRegistered: regId={}", regId);
        mGuaranteedDeliveryBus.postGuaranteed(new GcmRegisteredEvent(context, regId));
    }

    @Override
    protected void onUnregistered(Context context,
            String regId) {
        mLogger.info("GCMIntentService.onUnregistered: regId={}", regId);
        mGuaranteedDeliveryBus.postGuaranteed(new GcmUnregisteredEvent(context, regId));
    }
}
