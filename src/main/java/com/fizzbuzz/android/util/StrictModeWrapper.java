package com.fizzbuzz.android.util;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.EnumSet;
import java.util.Set;
import java.util.concurrent.Callable;


public abstract class StrictModeWrapper {
    public static enum Permission {
        ALLOW_DISK_READ,
        ALLOW_DISK_WRITE,
        ALLOW_NETWORK
    }

    public abstract void init(Context context);

    public abstract ThreadPolicyWrapper getThreadPolicy();

    public abstract VmPolicyWrapper getVmPolicy();

    public abstract ThreadPolicyWrapper allowThreadDiskReads();

    public abstract ThreadPolicyWrapper allowThreadDiskWrites();

    public abstract ThreadPolicyWrapper allowThreadNetwork();

    public abstract void restoreThreadPolicy(ThreadPolicyWrapper wrapper);

    public abstract void restoreVmPolicy(VmPolicyWrapper wrapper);

    public void runWithStrictModeOverride(Permission perm,
                                          Runnable r) {
        runWithStrictModeOverride(EnumSet.of(perm), r);
    }

    public void runWithStrictModeOverride(Set<Permission> perms,
                                          Runnable r) {
        StrictModeWrapper.ThreadPolicyWrapper origThreadPolicy = getThreadPolicy();

        if (perms.contains(Permission.ALLOW_DISK_READ))
            allowThreadDiskReads();
        if (perms.contains(Permission.ALLOW_DISK_WRITE))
            allowThreadDiskWrites();
        if (perms.contains(Permission.ALLOW_NETWORK))
            allowThreadNetwork();

        try {
            r.run();
        } finally {
            restoreThreadPolicy(origThreadPolicy);
        }
    }

    public <T> T callWithStrictModeOverride(Permission perm,
                                            Callable<T> c) {
        return callWithStrictModeOverride(EnumSet.of(perm), c);
    }

    public <T> T callWithStrictModeOverride(Set<Permission> perms,
                                            Callable<T> c) {
        T result = null;
        StrictModeWrapper.ThreadPolicyWrapper origThreadPolicy = getThreadPolicy();

        if (perms.contains(Permission.ALLOW_DISK_READ))
            allowThreadDiskReads();
        if (perms.contains(Permission.ALLOW_DISK_WRITE))
            allowThreadDiskWrites();
        if (perms.contains(Permission.ALLOW_NETWORK))
            allowThreadNetwork();

        try {
            result = c.call();
        } catch (Exception e) {
            throw new RuntimeException(e); // have to hold nose here, but Callable's checked Exception is basically
            // useless
        } finally {
            restoreThreadPolicy(origThreadPolicy);
        }
        return result;
    }

    public View inflateWithStrictModeOverride(final LayoutInflater inflater,
                                              final int resId,
                                              final ViewGroup container,
                                              final boolean attachToRoot) {
        return callWithStrictModeOverride(
                EnumSet.of(Permission.ALLOW_DISK_READ, Permission.ALLOW_DISK_WRITE),
                new Callable<View>() {
                    @Override
                    public View call() {
                        View result = inflater.inflate(resId,
                                container,
                                attachToRoot);
                        return result;
                    }
                });
    }

    public static interface ThreadPolicyWrapper {
    }

    public static interface VmPolicyWrapper {
    }


}
