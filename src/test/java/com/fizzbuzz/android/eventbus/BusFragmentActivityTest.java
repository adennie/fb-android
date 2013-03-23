package com.fizzbuzz.android.eventbus;

import com.fizzbuzz.android.activity.BusFragmentActivity;

public class BusFragmentActivityTest extends AbstractBusActivityTest<BusFragmentActivity>{
    @Override
    protected BusFragmentActivity createTestActivity() {
        return new BusFragmentActivity();
    }
}
