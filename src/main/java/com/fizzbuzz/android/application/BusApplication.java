package com.fizzbuzz.android.application;

import com.fizzbuzz.ottoext.GuaranteedDeliveryBus;

import javax.inject.Inject;

public class BusApplication extends InjectingApplication {
    @Inject
    @BusApplicationModule.ApplicationScopedMainThread
    GuaranteedDeliveryBus mAppBus;

    public GuaranteedDeliveryBus getApplicationBus() {
        return mAppBus;
    }
}
