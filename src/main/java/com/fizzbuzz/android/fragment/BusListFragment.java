package com.fizzbuzz.android.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.fizzbuzz.android.activity.BusManagingActivity;
import com.fizzbuzz.android.application.BusApplication;
import com.fizzbuzz.ottoext.GuaranteedDeliveryOttoBus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.List;

/*
 * BusFragment is a base class for Fragments that want to (a) post and/or subscribe to events on an application-wide bus
 * and (b) take advantage of a built-in Fragment-specific event bus that automatically distributes Fragment lifecycle
 * events. The Fragment-specific bus can also be used by application components to post and subscribe to
 * events that are related to a particular fragment.
 */
public class BusListFragment
        extends InjectingListFragment implements FragmentLifecycle, BusManagingFragment {

    @Inject
    BusFragmentHelper mBusHelper;

    private boolean mIsFirstAttach = true;
    private boolean mIsDestroyed = false;
    private final Logger mLogger = LoggerFactory.getLogger(LoggingManager.TAG);

    // add module to graph
    @Override
    protected List<Object> getModules() {
        List<Object> modules = super.getModules();
        modules.add(new BusFragmentModule());
        return modules;
    }

    // implement BusManagingFragment interface

    @Override
    public final GuaranteedDeliveryOttoBus getApplicationBus() {
        return ((BusApplication) getActivity().getApplication()).getApplicationBus();
    }

    @Override
    public final GuaranteedDeliveryOttoBus getVisibilityScopedApplicationBus() {
        return ((BusManagingActivity)getActivity()).getVisibilityScopedApplicationBus();
    }

    @Override
    public final GuaranteedDeliveryOttoBus getActivityBus() {
        return ((BusManagingActivity)getActivity()).getActivityBus();
    }

    @Override
    public final GuaranteedDeliveryOttoBus getFragmentBus() {
        return mBusHelper.getFragmentBus();
    }

    // intercept fragment lifecycle methods and delegate to helper object

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity); // builds fragment-scoped graph and does injection
        mBusHelper.onAttach(this, activity);

        // on the first time through, register with the application bus (this is really for the benefit of subclasses,
        // who may want to subscribe to events)
        if (mIsFirstAttach) {
            ((BusManagingActivity)activity).getApplicationBus().register(this);
            mIsFirstAttach = false;
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBusHelper.onCreate();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup viewGroup, Bundle bundle) {
        View result = super.onCreateView(inflater, viewGroup, bundle);
        mBusHelper.onViewCreated(result);
        return result;
    }

    @Override
    public void onActivityCreated(final Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mBusHelper.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
        mBusHelper.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        mBusHelper.onResume();
    }

    @Override
    public void onPause() {
        mBusHelper.onPause();
        super.onPause();
    }

    @Override
    public void onStop() {
        mBusHelper.onStop();
        super.onStop();
    }

    @Override
    public void onDestroyView() {
        mBusHelper.onDestroyView();
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        mBusHelper.onDestroy();
        mIsDestroyed = true;
        super.onDestroy();
    }

    @Override
    public void onDetach() {
        mBusHelper.onDetach();

        // on the last detach, unregister from the application bus
        if (mIsDestroyed)
            getApplicationBus().unregister(this);

        super.onDetach();
    }
}
