package com.fizzbuzz.android.fragment;

import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.app.Activity;
import android.os.Bundle;

public class FragmentLifecycleHub
        extends BusFragment
        implements FragmentLifecycleListener
{
    private final Logger mLogger = LoggerFactory.getLogger(LoggingManager.TAG);
    private final ArrayList<FragmentLifecycleListener> mListeners = new ArrayList<FragmentLifecycleListener>();

    public FragmentLifecycleHub() {
    }

    public void addLifecycleListener(FragmentLifecycleListener listener) {
        mListeners.add(listener);
    }

    public void removeLifecycleListener(FragmentLifecycleListener listener) {
        mListeners.remove(listener);
    }

    public void clearLifecycleListeners() {
        mListeners.clear();
    }

    @Override
    public void onAttach(Activity activity) {
        mLogger.debug("FragmentLifecycleHub.onAttach: for class {}", getClass().getName());
        super.onAttach(activity);
        for (FragmentLifecycleListener listener : mListeners) {
            listener.onAttach(activity);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        mLogger.debug("FragmentLifecycleHub.onCreate: for class {}", getClass().getName());
        super.onCreate(savedInstanceState);
        for (FragmentLifecycleListener listener : mListeners) {
            listener.onCreate(savedInstanceState);
        }
    }

    @Override
    public void onActivityCreated(final Bundle savedInstanceState) {
        mLogger.debug("FragmentLifecycleHub.onActivityCreated: for class {}", getClass().getName());
        super.onActivityCreated(savedInstanceState);

        for (FragmentLifecycleListener listener : mListeners) {
            listener.onActivityCreated(savedInstanceState);
        }

    }

    @Override
    public void onStart() {
        mLogger.debug("FragmentLifecycleHub.onStart: for class {}", getClass().getName());
        super.onStart();
        for (FragmentLifecycleListener listener : mListeners) {
            listener.onStart();
        }
    }

    @Override
    public void onResume() {
        mLogger.debug("FragmentLifecycleHub.onResume: for class {}", getClass().getName());
        super.onResume();
        for (FragmentLifecycleListener listener : mListeners) {
            listener.onResume();
        }
    }

    @Override
    public void onPause() {
        mLogger.debug("FragmentLifecycleHub.onPause: for class {}", getClass().getName());
        for (FragmentLifecycleListener listener : mListeners) {
            listener.onPause();
        }
        super.onPause();
    }

    @Override
    public void onStop() {
        mLogger.debug("FragmentLifecycleHub.onStop: for class {}", getClass().getName());
        for (FragmentLifecycleListener listener : mListeners) {
            listener.onStop();
        }
        super.onStop();
    }

    @Override
    public void onDestroyView() {
        mLogger.debug("FragmentLifecycleHub.onDestroyView: for class {}", getClass().getName());
        for (FragmentLifecycleListener listener : mListeners) {
            listener.onDestroyView();
        }
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        mLogger.debug("FragmentLifecycleHub.onDestroy: for class {}", getClass().getName());
        for (FragmentLifecycleListener listener : mListeners) {
            listener.onDestroy();
        }
        clearLifecycleListeners();
        super.onDestroy();
    }

    @Override
    public void onDetach() {
        mLogger.debug("FragmentLifecycleHub.onDetach: for class {}", getClass().getName());
        for (FragmentLifecycleListener listener : mListeners) {
            listener.onDetach();
        }
        super.onDetach();
    }
}
