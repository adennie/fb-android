package com.fizzbuzz.android.gcm;

import android.content.Context;

public class GcmUnregisteredEvent {
    private final Context mContext;
    private final String mRegistrationId;

    public GcmUnregisteredEvent(final Context context,
            final String regId) {
        mContext = context;
        mRegistrationId = regId;
    }

    public Context getContext() {
        return mContext;
    }

    public String getRegistrationId() {
        return mRegistrationId;
    }
}
