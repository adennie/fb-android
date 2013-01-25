package com.fizzbuzz.android.gcm;

import android.content.Context;
import android.content.Intent;

public class GcmMessageReceivedEvent {
    private final Context mContext;
    private final Intent mIntent;

    public GcmMessageReceivedEvent(final Context context,
            final Intent intent) {
        mContext = context;
        mIntent = intent;
    }

    public Context getContext() {
        return mContext;
    }

    public Intent getIntent() {
        return mIntent;
    }
}
