package com.fizzbuzz.android.eventbus;

import com.fizzbuzz.android.activity.BusPreferenceActivity;

public class BusPreferenceActivityTest extends AbstractBusActivityTest<BusPreferenceActivity>{
    @Override
    protected BusPreferenceActivity createTestActivity() {
        return new BusPreferenceActivity();
    }}
