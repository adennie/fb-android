package com.fizzbuzz.android.fragment;

import java.lang.ref.WeakReference;

/**
 * A Runnable that holds a weak reference to a target object
 */
public abstract class WeakRunnable<T> implements Runnable {
    private WeakReference<T> mTarget;
    public WeakRunnable(T target) {
        mTarget = new WeakReference<T>(target);
    }

    public T get() {
        return mTarget.get();
    }
}
