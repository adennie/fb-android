package com.fizzbuzz.android.fragment;

import android.support.v4.app.ListFragment;

import com.fizzbuzz.android.event.BusProvider;

public class BusListFragment
        extends ListFragment {
    @Override
    public void onResume() {
        super.onResume();
        BusProvider.getInstance().register(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        BusProvider.getInstance().unregister(this);
    }
}
