package com.fizzbuzz.android.fragment;

import java.util.ArrayList;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.ListFragment;

public class ListFragmentLifecycleHub
        extends ListFragment
        implements FragmentLifecycleListener
{
    private final ArrayList<FragmentLifecycleListener> mListeners = new ArrayList<FragmentLifecycleListener>();

    public ListFragmentLifecycleHub() {
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
        super.onAttach(activity);
        for (FragmentLifecycleListener listener : mListeners) {
            listener.onAttach(activity);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        for (FragmentLifecycleListener listener : mListeners) {
            listener.onCreate(savedInstanceState);
        }
    }

    @Override
    public void onActivityCreated(final Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        for (FragmentLifecycleListener listener : mListeners) {
            listener.onActivityCreated(savedInstanceState);
        }

    }

    @Override
    public void onStart() {
        super.onStart();
        for (FragmentLifecycleListener listener : mListeners) {
            listener.onStart();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        for (FragmentLifecycleListener listener : mListeners) {
            listener.onResume();
        }
    }

    @Override
    public void onPause() {
        for (FragmentLifecycleListener listener : mListeners) {
            listener.onPause();
        }
        super.onPause();
    }

    @Override
    public void onStop() {
        for (FragmentLifecycleListener listener : mListeners) {
            listener.onStop();
        }
        super.onStop();
    }

    @Override
    public void onDestroyView() {
        for (FragmentLifecycleListener listener : mListeners) {
            listener.onDestroyView();
        }
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        for (FragmentLifecycleListener listener : mListeners) {
            listener.onDestroy();
        }
        clearLifecycleListeners();
        super.onDestroy();
    }

    @Override
    public void onDetach() {
        for (FragmentLifecycleListener listener : mListeners) {
            listener.onDetach();
        }
        super.onDetach();
    }
}
