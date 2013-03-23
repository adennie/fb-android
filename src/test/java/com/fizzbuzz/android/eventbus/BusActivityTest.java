package com.fizzbuzz.android.eventbus;

import com.fizzbuzz.android.activity.BusActivity;

public class BusActivityTest extends AbstractBusActivityTest<BusActivity> {
    @Override
    protected BusActivity createTestActivity() {
        return new BusActivity();
    }
}
