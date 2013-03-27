package com.fizzbuzz.android.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

// This interface exposes Fragment lifecycle methods
public interface FragmentLifecycle {

    public void onAttach(Activity activity);

    public void onCreate(Bundle savedInstanceState);


    public View onCreateView(LayoutInflater inflater, ViewGroup viewGroup, Bundle bundle);

    public void onActivityCreated(Bundle bundle);

    public void onStart();

    public void onResume();

    public void onPause();

    public void onStop();

    public void onDestroyView();

    public void onDestroy();

    public void onDetach();
}
