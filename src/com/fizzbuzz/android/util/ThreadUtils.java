package com.fizzbuzz.android.util;

import android.os.Looper;

public class ThreadUtils {
    public static boolean isUIThread() {
        return Looper.myLooper() != null && Looper.myLooper() == Looper.getMainLooper();
    }

    public static void ThrowIfUIThread() {
        if (isUIThread()) {
            throw new RuntimeException("This is not to be executed in the Main thread ");
        }
    }

    public static void ThrowIfNotUIThread() {
        if (!isUIThread()) {
            throw new RuntimeException("This is only to be executed in the Main-thread ");
        }
    }

}
