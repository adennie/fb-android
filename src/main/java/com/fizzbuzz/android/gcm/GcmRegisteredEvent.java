package com.fizzbuzz.android.gcm;

import android.content.Context;

public class GcmRegisteredEvent {
    private final Context mContext;
    private final String mRegistrationId;

    public GcmRegisteredEvent(final Context context,
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
