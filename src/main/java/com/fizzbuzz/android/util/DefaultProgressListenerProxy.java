package com.fizzbuzz.android.util;

/*
 * Mediates between a task and the use of the AsyncTaskManager's default progress listener. Only one task at a time
 * can use the default progress listener, and the manager offers access to that default listener to the listeners
 * assigned to tasks (via onDefaultListenerAvailable) when it becomes available. Whenever a DefaultProgressListenerProxy
 * has ownership of the default progress listener, it will forward incoming calls to it, otherwise it will just drop
 * them.
 */
public class DefaultProgressListenerProxy
        extends BaseProgressListener {
    private ProgressListener mDefaultListener;

    public DefaultProgressListenerProxy() {
        mDefaultListener = null;
    }

    @Override
    public void onStartProgress(final String message1, final String message2) {
        if (mDefaultListener != null)
            mDefaultListener.onStartProgress(message1, message2);
    }

    @Override
    public void onUpdateProgress(final int progress, final String message1, final String message2) {
        if (mDefaultListener != null)
            mDefaultListener.onUpdateProgress(progress, message1, message2);
    }

    @Override
    public void onEndProgress() {
        if (mDefaultListener != null)
            mDefaultListener.onEndProgress();
    }

    @Override
    public void onUiPause() {
        if (mDefaultListener != null)
            mDefaultListener.onUiPause();
    }

    @Override
    public void onUiResume() {
        if (mDefaultListener != null)
            mDefaultListener.onUiResume();

    }

    @Override
    public boolean onDefaultListenerAvailable(final ProgressListener defaultListener) {
        mDefaultListener = defaultListener;
        return true;
    }

    @Override
    public boolean ownsDefaultListener() {
        return mDefaultListener != null;
    }

}