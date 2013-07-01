package com.fizzbuzz.android.broadcastreceiver;

import android.content.Context;
import android.content.Intent;
import com.fizzbuzz.android.application.BusApplication;
import com.fizzbuzz.android.dagger.InjectingBroadcastReceiver;
import com.fizzbuzz.ottoext.GuaranteedDeliveryOttoBus;


/* A BroadcastReceiver with a convenience method providing easy access to the application bus.  Unlike other base
 * classes like BusActivity, BusService, etc., a BusBroadcastReceiver doesn't maintain its own local bus for
 * use by components running within its context, because a BroadcastReceiver is only valid within the scope of a single
 * call to onReceive().
 */
public class BusBroadcastReceiver extends InjectingBroadcastReceiver {
    private Context mContext;

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        mContext = context;
    }

    protected final GuaranteedDeliveryOttoBus getApplicationBus() {
        return ((BusApplication) mContext.getApplicationContext()).getApplicationBus();
    }
}
