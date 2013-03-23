package com.fizzbuzz.android.activity;

import com.fizzbuzz.android.injection.Injector;
import dagger.ObjectGraph;

import static com.google.common.base.Preconditions.checkState;

public class InjectingPreferenceActivity
        extends BasePreferenceActivity
        implements ActivityInjector {
    private ObjectGraph mObjectGraph;
    private Class<? extends BaseActivityModule> mActivityModuleClass;

    @Override
    public final ObjectGraph getObjectGraph() {
        return mObjectGraph;
    }

    @Override
    protected void onCreate(android.os.Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        checkState(mActivityModuleClass != null,
                "Activity-specific module class must be assigned prior to calling onCreate");

        // first, get the Activity-specific module from the application's object graph
        ObjectGraph appGraph = ((Injector) getApplication()).getObjectGraph();
        BaseActivityModule activityModule = appGraph.get(mActivityModuleClass);
        activityModule.setActivity(this);

        // OK, now expand the application graph with the ActivityModule
        mObjectGraph = appGraph.plus(activityModule);

        // now we can inject ourselves
        inject(this);
    }

    public void inject(Object target) {
        checkState(mObjectGraph != null, "object graph must be assigned prior to calling inject");
        mObjectGraph.inject(target);
    }

    @Override
    public void setActivityModuleClass(Class<? extends BaseActivityModule> activityModuleClass) {
        mActivityModuleClass = activityModuleClass;
    }
}
