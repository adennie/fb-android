package com.fizzbuzz.android.application;

import dagger.ObjectGraph;

public abstract class DaggerApplication
        extends BaseApplication {

    private static ObjectGraph sObjectGraph;

    @Override
    public void onCreate() {
        super.onCreate();

        // initialize object graph for this lib
        sObjectGraph = ObjectGraph.create(new BaseAppModule(this), new DaggerModule());

        // Note: we don't call mObjectGraph.inject(this) here. That's because "this" is going to be an instance of a
        // class derived from this class, which isn't present in the object graph at this point, and we can't inject
        // into a class that's not in the object graph. Therefore, derived classes are responsible for extending the
        // graph to include themselves as entry points, and then to inject themselves in their onCreate().

        // debug mode stuff
        if (isDebugMode()) {
            sObjectGraph.validate(); // validate dagger's object graph
        }
    }

    public static ObjectGraph getObjectGraph() {
        return sObjectGraph;
    }

    protected void setObjectGraph(final ObjectGraph objectGraph) {
        sObjectGraph = objectGraph;
    }
}
