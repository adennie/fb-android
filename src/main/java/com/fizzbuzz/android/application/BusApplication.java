package com.fizzbuzz.android.application;

import com.fizzbuzz.ottoext.GuaranteedDeliveryBus;

import javax.inject.Inject;
import java.util.List;

public class BusApplication extends InjectingApplication {
    @Inject
    @BusApplicationModule.ApplicationScopedMainThread
    GuaranteedDeliveryBus mAppBus;

    public GuaranteedDeliveryBus getApplicationBus() {
        return mAppBus;
    }

    @Override
    protected List<Object> getModules() {
        List<Object> modules = super.getModules();
        modules.add(new BusApplicationModule());
        return modules;
    }
}
