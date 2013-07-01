package com.fizzbuzz.android.application;

import com.fizzbuzz.ottoext.GuaranteedDeliveryBus;

import javax.inject.Inject;
import java.util.List;

public class BusApplication extends BaseApplication {
    @Inject
    @BusApplicationModule.ApplicationMainThread
    GuaranteedDeliveryBus mAppBus;

    public GuaranteedDeliveryBus getApplicationBus() {
        return mAppBus;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        // register with the application bus (this is really for the benefit of subclasses, who may want to subscribe to events)
        mAppBus.register(this);

    }

    @Override
    protected List<Object> getModules() {
        List<Object> modules = super.getModules();
        modules.add(new BusApplicationModule());
        return modules;
    }
}
