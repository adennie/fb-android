package com.fizzbuzz.android.activity;

import com.fizzbuzz.ottoext.GuaranteedDeliveryOttoBus;

public interface BusManagingActivity {
    public GuaranteedDeliveryOttoBus getApplicationBus();
    public GuaranteedDeliveryOttoBus getVisibilityScopedApplicationBus();
    public GuaranteedDeliveryOttoBus getActivityBus();
}
