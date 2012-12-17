package com.fizzbuzz.android.fragment;

import android.os.Bundle;

public interface FragmentLifecycleListener {

    public void onAttach(android.app.Activity activity);

    public void onCreate(final Bundle savedInstanceState);

    public void onActivityCreated(final Bundle savedInstanceState);

    public void onStart();

    public void onResume();

    public void onPause();

    public void onStop();

    public void onDestroyView();

    public void onDestroy();

    public void onDetach();

}
