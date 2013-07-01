package com.fizzbuzz.android.util;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.os.Build;
import android.os.StrictMode;
import dagger.Module;
import dagger.Provides;

public class VersionedStrictModeWrapper {
    // see StrictModeWrapperModule for a dependency-injection-based alternative to this factory
    public static synchronized StrictModeWrapper getInstance() {
        StrictModeWrapper wrapper = null;
        final int sdkVersion = Build.VERSION.SDK_INT;
        if (sdkVersion >= Build.VERSION_CODES.GINGERBREAD) {
            wrapper = new GingerbreadStrictModeWrapper();
        } else {
            wrapper = new NoopStrictModeWrapper();
        }
        return wrapper;
    }

    @Module (library = true)
    public static class StrictModeWrapperModule {
        @Provides
        synchronized StrictModeWrapper provideStrictModeWrapper() {
            return getInstance();
        }
    }

    public static class NoopStrictModeWrapper
            extends StrictModeWrapper {
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
        }

        ;

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
        }

        ;

        @Override
        public void restoreVmPolicy(final VmPolicyWrapper wrapper) {
        }
    }

    @TargetApi(9)
    public static class GingerbreadStrictModeWrapper
            extends StrictModeWrapper {
        @TargetApi(9)
        static class GingerbreadThreadPolicyWrapper
                implements StrictModeWrapper.ThreadPolicyWrapper {
            private final StrictMode.ThreadPolicy mOrigPolicy;

            public GingerbreadThreadPolicyWrapper(final StrictMode.ThreadPolicy origPolicy) {
                mOrigPolicy = origPolicy;
            }

            public StrictMode.ThreadPolicy getOrigPolicy() {
                return mOrigPolicy;
            }
        }

        @Override
        public void init(final Context context) {
            if ((context.getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0) {
                StrictMode.enableDefaults();
            }
        }

        @TargetApi(9)
        static class GingerbreadVmPolicyWrapper
                implements StrictModeWrapper.VmPolicyWrapper {
            private final StrictMode.VmPolicy mOrigPolicy;

            public GingerbreadVmPolicyWrapper(final StrictMode.VmPolicy origPolicy) {
                mOrigPolicy = origPolicy;
            }

            public StrictMode.VmPolicy getOrigPolicy() {
                return mOrigPolicy;
            }
        }

        @Override
        public ThreadPolicyWrapper getThreadPolicy() {
            return new GingerbreadThreadPolicyWrapper(StrictMode.getThreadPolicy());
        }

        @Override
        public VmPolicyWrapper getVmPolicy() {
            return new GingerbreadVmPolicyWrapper(StrictMode.getVmPolicy());
        }

        ;

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
            StrictMode.ThreadPolicy origPolicy = StrictMode.getThreadPolicy();
            StrictMode.ThreadPolicy newPolicy = new StrictMode.ThreadPolicy.Builder(origPolicy).permitNetwork().build();
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


}
