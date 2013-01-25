package com.fizzbuzz.android.fragment;

import android.support.v4.app.Fragment;

import com.fizzbuzz.android.event.BusProvider;

public class BusFragment
        extends Fragment {
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
