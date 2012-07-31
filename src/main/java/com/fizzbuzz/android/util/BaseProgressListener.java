package com.fizzbuzz.android.util;

public abstract class BaseProgressListener
        implements ProgressListener {

    @Override
    public boolean onDefaultListenerAvailable(final ProgressListener defaultListener) {
        return false;
    }

    @Override
    public boolean ownsDefaultListener() {
        return false;
    }

}
