package com.fizzbuzz.android.gcm;

import android.content.Context;
import android.content.Intent;

public class GcmEvents {
    public static class GcmMessageReceivedEvent {
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

    public static class GcmRegisteredEvent {
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

    public static class GcmUnregisteredEvent {
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

    public static class GcmRegUnregErrorEvent {
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
}
