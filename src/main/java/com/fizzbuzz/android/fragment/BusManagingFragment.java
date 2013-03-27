package com.fizzbuzz.android.fragment;

import com.fizzbuzz.ottoext.GuaranteedDeliveryOttoBus;

public interface BusManagingFragment {
    public GuaranteedDeliveryOttoBus getApplicationBus();
    public GuaranteedDeliveryOttoBus getVisibilityScopedApplicationBus();
    public GuaranteedDeliveryOttoBus getActivityBus();
    public GuaranteedDeliveryOttoBus getFragmentBus();
}
