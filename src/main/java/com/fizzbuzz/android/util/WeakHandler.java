package com.fizzbuzz.android.util;

import java.lang.ref.WeakReference;

import android.os.Handler;
import android.os.Message;

/**
 * A handler implementation that stores a weak reference to an object and only calls {@link #handleMessage(Message, T)} on subclasses when the
 * reference to the object is not lost yet.
 * <p>
 * Basically this enables us to create static handler classes while still be able to call through to the activity or context and store a reference to
 * the handler as a normal class member.
 * <p>
 * Example for activity <code>MyActivity</code>:
 * 
 * <pre>
 * private static class MyHandler
 *         extends AbstractWeakHandler&lt;MyActivity&gt; {
 *     public MyHandler(MyActivity t) {
 *         super(t);
 *     }
 * 
 *     &#064;Override
 *     public handleMessage(Message msg,
 *             MyActivity t) {
 *         // handle message normally here...
 *     }
 * 
 * }
 * </pre>
 */
public class WeakHandler<T>
        extends Handler {

    private final WeakReference<T> ref;

    public WeakHandler(T t) {
        super();
        ref = new WeakReference<T>(t);
    }

    /**
     * Can not be used by subclasses. Ensures that the reference is not gone yet. If
     * the reference is ok, it calls through to {@link #handleMessage(Message, T)}.
     */
    @Override
    public final void handleMessage(Message msg) {
        final T t = ref.get();
        if (t != null) {
            handleMessage(msg, t);
        }
    }

    /**
     * Must be implemented by subclasses in order to handle messages.
     * 
     * @param msg The message.
     * @param t A hard reference to the object, usually an activity. Never <code>null</code>.
     * @see Handler#handleMessage(Message)
     */
    public void handleMessage(Message msg,
            T t) {
    }

}