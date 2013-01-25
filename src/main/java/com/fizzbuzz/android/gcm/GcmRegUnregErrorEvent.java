package com.fizzbuzz.android.gcm;

import android.content.Context;

public class GcmRegUnregErrorEvent {
    private final Context mContext;
    private final String mErrorId;

    public GcmRegUnregErrorEvent(final Context context,
            final String errorId) {
        mContext = context;
        mErrorId = errorId;
    }

    public Context getContext() {
        return mContext;
    }

    public String getErrorId() {
        return mErrorId;
    }
}
