package com.fizzbuzz.android.util;

public class AsyncTaskTester {

    static class Mediator {
        private final AsyncTaskManager mTaskManager = new AsyncTaskManager();
        private final ProgressListener mProgressListener;
        private final WorkToDo mWork = new WorkToDo();

        Mediator(final ProgressListener listener) {
            mProgressListener = listener;

            if (mProgressListener != null)
                mTaskManager.setDefaultProgressListener(mProgressListener);
        }

        String DoWorkSync() {
            return mWork.DoSomething(null); // no AsyncTaskHelper in the picture, so pass null
        }

        void DoWorkAsync(final AsyncTaskResultProcessor<String> resultProcessor) {

            // Create an AsyncTaskJob defining the work to be performed by the task
            AsyncTaskJob<String> job = new AsyncTaskJob<String>() {
                @Override
                public String doJob(final AsyncTaskJobListener jobListener) {
                    return mWork.DoSomething(jobListener);
                }
            };

            // Create the AsyncTaskHelper, initializing it with the passed-in result processor
            AsyncTaskHelper<String> task = new AsyncTaskHelper<String>(job, resultProcessor);
            task.setProgressMessage1("Testing Async utilities");

            // the task manager will manage this and other tasks created by this mediator
            mTaskManager.manage(task);

            // OK, let's go
            task.execute();
        }
    }

    static class WorkToDo {
        String DoSomething(final AsyncTaskJobListener jobListener) {
            for (int i = 1; i <= 5; i++) {
                try {
                    Thread.sleep(1000); // for testing orientation change behavior
                }
                catch (InterruptedException e) {
                    if ((jobListener != null) && jobListener.isTaskCancelled()) {
                        return new String("DoSomething was cancelled");
                    }
                }

                if (jobListener != null)
                    jobListener.publishProgress(i * 10, null, "step 1");
            }

            for (int i = 6; i <= 10; i++) {
                try {
                    Thread.sleep(1000); // for testing orientation change behavior
                }
                catch (InterruptedException e) {
                }

                if (jobListener != null)
                    jobListener.publishProgress(i * 10, null, "step 2");
            }
            return new String("DoSomething completed");
        }
    }
}
