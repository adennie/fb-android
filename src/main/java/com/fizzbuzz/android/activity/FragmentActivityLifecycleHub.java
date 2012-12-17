package com.fizzbuzz.android.activity;

import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

public class FragmentActivityLifecycleHub
        extends FragmentActivity
{
    private final Logger mLogger = LoggerFactory.getLogger(LoggingManager.TAG);
    private final ArrayList<ActivityLifecycleListener> mListeners = new ArrayList<ActivityLifecycleListener>();

    public FragmentActivityLifecycleHub() {
    }

    public void addLifecycleListener(ActivityLifecycleListener listener) {
        mListeners.add(listener);
    }

    public void removeLifecycleListener(ActivityLifecycleListener listener) {
        mListeners.remove(listener);
    }

    public void clearLifecycleListeners() {
        mListeners.clear();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mLogger.debug("FragmentActivityLifecycleHub.onCreate: for class {}, instance {}", getClass().getName(), this);
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onRestart() {
        mLogger.debug("FragmentActivityLifecycleHub.onRestart: for class {}, instance {}", getClass().getName(), this);
        super.onRestart();
        for (ActivityLifecycleListener listener : mListeners) {
            listener.onRestart();
        }
    }

    @Override
    protected void onStart() {
        mLogger.debug("FragmentActivityLifecycleHub.onStart: for class {}, instance {}", getClass().getName(), this);
        super.onStart();
        for (ActivityLifecycleListener listener : mListeners) {
            listener.onStart();
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        mLogger.debug("FragmentActivityLifecycleHub.onRestoreInstanceState: for class {}, instance {}", getClass().getName(), this);
        super.onRestoreInstanceState(savedInstanceState);
        for (ActivityLifecycleListener listener : mListeners) {
            listener.onRestoreInstanceState(savedInstanceState);
        }
    }

    @Override
    protected void onResume() {
        mLogger.debug("FragmentActivityLifecycleHub.onResume: for class {}, instance {}", getClass().getName(), this);
        super.onResume();
        for (ActivityLifecycleListener listener : mListeners) {
            listener.onResume();
        }
    }

    @Override
    protected void onPause() {
        mLogger.debug("FragmentActivityLifecycleHub.onPause: for class {}, instance {}", getClass().getName(), this);
        for (ActivityLifecycleListener listener : mListeners) {
            listener.onPause();
        }
        super.onPause();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        mLogger.debug("FragmentActivityLifecycleHub.onSaveInstanceState: for class {}, instance {}", getClass().getName(), this);
        super.onSaveInstanceState(outState);
        for (ActivityLifecycleListener listener : mListeners) {
            listener.onSaveInstanceState(outState);
        }
    }

    @Override
    protected void onStop() {
        mLogger.debug("FragmentActivityLifecycleHub.onStop: for class {}, instance {}", getClass().getName(), this);
        for (ActivityLifecycleListener listener : mListeners) {
            listener.onStop();
        }
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        mLogger.debug("FragmentActivityLifecycleHub.onDestroy: for class {}, instance {}", getClass().getName(), this);
        for (ActivityLifecycleListener listener : mListeners) {
            listener.onDestroy();
        }
        clearLifecycleListeners();
        super.onDestroy();
    }

}
