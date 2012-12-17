package com.fizzbuzz.android.util;

import static com.fizzbuzz.android.util.VersionedStrictModeWrapper.Permission.ALLOW_DISK_READ;
import static com.fizzbuzz.android.util.VersionedStrictModeWrapper.Permission.ALLOW_DISK_WRITE;

import java.util.EnumSet;
import java.util.Set;
import java.util.concurrent.Callable;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.os.Build;
import android.os.StrictMode;
import android.os.StrictMode.ThreadPolicy;
import android.os.StrictMode.VmPolicy;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.fizzbuzz.android.util.VersionedStrictModeWrapper.StrictModeWrapper.ThreadPolicyWrapper;
import com.fizzbuzz.android.util.VersionedStrictModeWrapper.StrictModeWrapper.VmPolicyWrapper;

public class VersionedStrictModeWrapper {

    public interface StrictModeWrapper {
        public void init(Context context);

        public ThreadPolicyWrapper getThreadPolicy();

        public VmPolicyWrapper getVmPolicy();

        public ThreadPolicyWrapper allowThreadDiskReads();

        public ThreadPolicyWrapper allowThreadDiskWrites();

        public ThreadPolicyWrapper allowThreadNetwork();

        public void restoreThreadPolicy(ThreadPolicyWrapper wrapper);

        public void restoreVmPolicy(VmPolicyWrapper wrapper);

        public static interface ThreadPolicyWrapper {
        }

        public static interface VmPolicyWrapper {
        }
    }

    public static enum Permission {
        ALLOW_DISK_READ,
        ALLOW_DISK_WRITE,
        ALLOW_NETWORK
    };

    static public StrictModeWrapper getInstance() {
        StrictModeWrapper wrapper = null;
        final int sdkVersion = Build.VERSION.SDK_INT;
        if (sdkVersion >= Build.VERSION_CODES.GINGERBREAD) {
            wrapper = new GingerbreadStrictModeWrapper();
        }
        else {
            wrapper = new NoopStrictModeWrapper();
        }
        return wrapper;
    }

    static public void runWithStrictModeOverride(Permission perm,
            Runnable r) {
        runWithStrictModeOverride(EnumSet.of(perm), r);
    }

    static public void runWithStrictModeOverride(Set<Permission> perms,
            Runnable r) {
        StrictModeWrapper strictMode = VersionedStrictModeWrapper.getInstance();
        ThreadPolicyWrapper origThreadPolicy = strictMode.getThreadPolicy();

        if (perms.contains(Permission.ALLOW_DISK_READ))
            strictMode.allowThreadDiskReads();
        if (perms.contains(Permission.ALLOW_DISK_WRITE))
            strictMode.allowThreadDiskWrites();
        if (perms.contains(Permission.ALLOW_NETWORK))
            strictMode.allowThreadNetwork();

        try {
            r.run();
        }
        finally {
            strictMode.restoreThreadPolicy(origThreadPolicy);
        }
    }

    static public <T> T callWithStrictModeOverride(Permission perm,
            Callable<T> c) {
        return callWithStrictModeOverride(EnumSet.of(perm), c);
    }

    static public <T> T callWithStrictModeOverride(Set<Permission> perms,
            Callable<T> c) {
        T result = null;
        StrictModeWrapper strictMode = VersionedStrictModeWrapper.getInstance();
        ThreadPolicyWrapper origThreadPolicy = strictMode.getThreadPolicy();

        if (perms.contains(Permission.ALLOW_DISK_READ))
            strictMode.allowThreadDiskReads();
        if (perms.contains(Permission.ALLOW_DISK_WRITE))
            strictMode.allowThreadDiskWrites();
        if (perms.contains(Permission.ALLOW_NETWORK))
            strictMode.allowThreadNetwork();

        try {
            result = c.call();
        }
        catch (Exception e) {
            throw new RuntimeException(e); // have to hold nose here, but Callable's checked Exception is basically
                                           // useless
        }
        finally {
            strictMode.restoreThreadPolicy(origThreadPolicy);
        }
        return result;
    }

    static public View inflateWithStrictModeOverride(final LayoutInflater inflater,
            final int resId,
            final ViewGroup container,
            final boolean attachToRoot)
    {
        return VersionedStrictModeWrapper.callWithStrictModeOverride(
                EnumSet.of(ALLOW_DISK_READ, ALLOW_DISK_WRITE),
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

    static class NoopStrictModeWrapper
            implements StrictModeWrapper {
        @Override
        public void init(final Context context) {
        }

        @Override
        public ThreadPolicyWrapper getThreadPolicy() {
            return null;
        }

        @Override
        public VmPolicyWrapper getVmPolicy() {
            return null;
        };

        @Override
        public ThreadPolicyWrapper allowThreadDiskReads() {
            return null;
        }

        @Override
        public ThreadPolicyWrapper allowThreadDiskWrites() {
            return null;
        }

        @Override
        public ThreadPolicyWrapper allowThreadNetwork() {
            return null;
        }

        @Override
        public void restoreThreadPolicy(final ThreadPolicyWrapper wrapper) {
        };

        @Override
        public void restoreVmPolicy(final VmPolicyWrapper wrapper) {
        }
    }

    @TargetApi(9)
    static class GingerbreadStrictModeWrapper
            implements StrictModeWrapper {
        @Override
        public void init(final Context context) {
            if ((context.getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0) {
                StrictMode.enableDefaults();
            }
        }

        @Override
        public ThreadPolicyWrapper getThreadPolicy() {
            return new GingerbreadThreadPolicyWrapper(StrictMode.getThreadPolicy());
        }

        @Override
        public VmPolicyWrapper getVmPolicy() {
            return new GingerbreadVmPolicyWrapper(StrictMode.getVmPolicy());
        };

        @Override
        public ThreadPolicyWrapper allowThreadDiskReads() {
            return new GingerbreadThreadPolicyWrapper(StrictMode.allowThreadDiskReads());
        }

        @Override
        public ThreadPolicyWrapper allowThreadDiskWrites() {
            return new GingerbreadThreadPolicyWrapper(StrictMode.allowThreadDiskWrites());
        }

        @Override
        public ThreadPolicyWrapper allowThreadNetwork() {
            ThreadPolicy origPolicy = StrictMode.getThreadPolicy();
            ThreadPolicy newPolicy = new ThreadPolicy.Builder(origPolicy).permitNetwork().build();
            StrictMode.setThreadPolicy(newPolicy);
            return new GingerbreadThreadPolicyWrapper(origPolicy);
        }

        @Override
        public void restoreThreadPolicy(final ThreadPolicyWrapper wrapper) {
            StrictMode.setThreadPolicy(((GingerbreadThreadPolicyWrapper) wrapper).getOrigPolicy());
        }

        @Override
        public void restoreVmPolicy(final VmPolicyWrapper wrapper) {
            StrictMode.setVmPolicy(((GingerbreadVmPolicyWrapper) wrapper).getOrigPolicy());
        }
    }

    @TargetApi(9)
    static class GingerbreadThreadPolicyWrapper
            implements ThreadPolicyWrapper {
        private final ThreadPolicy mOrigPolicy;

        public GingerbreadThreadPolicyWrapper(final StrictMode.ThreadPolicy origPolicy) {
            mOrigPolicy = origPolicy;
        }

        public StrictMode.ThreadPolicy getOrigPolicy() {
            return mOrigPolicy;
        }
    }

    @TargetApi(9)
    static class GingerbreadVmPolicyWrapper
            implements VmPolicyWrapper {
        private final VmPolicy mOrigPolicy;

        public GingerbreadVmPolicyWrapper(final StrictMode.VmPolicy origPolicy) {
            mOrigPolicy = origPolicy;
        }

        public StrictMode.VmPolicy getOrigPolicy() {
            return mOrigPolicy;
        }
    }
}
