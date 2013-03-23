package com.fizzbuzz.android.activity;

import com.fizzbuzz.android.injection.Injector;
import dagger.ObjectGraph;

import static com.google.common.base.Preconditions.checkState;

/**
 * Manages an ObjectGraph on behalf of an Activity.  This graph is created by extending the application-scope graph with
 * an Activity-specific module injected by the application-scope graph.  This works as follows:
 * <ul>
 *     <li>The derived InjectingActivity class calls setupObjectGraph from its onCreate method, prior to calling
 *     super.onCreate, and passing its Activity-specific module class as a parameter.</li>
 *     <li>setupObjectGraph fetches an instance of the Activity-specific module class from the application-scope
 *     object graph.  This technique allows the Activity-specific module class to be replaced with a test implementation
 *     in unit tests</li>
 *     <li>setupObjectGraph then uses ObjectGraph.plus to extend the application-scope object graph with the
 *     Application-specific module</li>
 * </ul>
 */
public class InjectingActivity
        extends BaseActivity
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

    @Override
    public void inject(Object target) {
        checkState(mObjectGraph != null, "object graph must be assigned prior to calling inject");
        mObjectGraph.inject(target);
    }

    @Override
    public void setActivityModuleClass(Class<? extends BaseActivityModule> activityModuleClass) {
        mActivityModuleClass = activityModuleClass;
    }


}
