package com.fizzbuzz.android.async;

import static com.google.common.base.Preconditions.checkNotNull;
import android.app.Activity;
import android.os.AsyncTask;

/**
 * AsyncTaskHelper is an extension of {@link AsyncTask} that
 * <ol>
 * <li>decouples the work to be performed from the orchestration of that work
 * <li>reduces the amount of boilerplate code needed to execute an asynchronous task
 * <li>expands the richness of progress information passed from the background job to the client code
 * <li>implements an interface (AsyncTaskControllee) to assist client code in overseeing multiple asynchronous tasks
 * <li>delivers exceptions emanating from the background task to the client's foreground thread
 * <li>facilitates proper handling of device configuration changes (e.g. rotations) by providing mechanisms to integrate the Activity lifecycle with
 * the result processing callback, such that if the Activity that created the AsyncTaskHelper is different than the one that's running when the task
 * completes, the callback that processes the result will have access to the correct, current one.
 * <li>allows an arbitrary number of parameters of arbitrary types to be passed to the background job (unlike AsyncTask.doInBackground(Params...))
 * </ol>
 * <p>
 * Unlike AsyncTask, where you implement a subclass to define task-specific behavior, with AsyncTaskHelper you supply that behavior to its constructor
 * via an {@link AsyncTaskJob} and (optionally) an {@link AsyncTaskResultProcessor}. By instantiating AsyncTaskJobs on the fly in the context of the
 * method initiating the task, the code inside the job has access to the local and class member variables available to the context of that calling
 * method, obviating the need to package that contextual information into something that works with the "Params..." signature of doInBackground().
 * <p>
 * AsyncTaskHelper provides plumbing to receive progress updates from the AsyncTaskJob running on the background thread, and to forward those updates
 * to the {@link ProgressListener} interface of an object the UI thread. In addition to a numeric indicator of progress completion, this plumbing also
 * allows the background job to provide two messages that describe the ongoing work. This information maps well to a ProgressDialog with numeric
 * progress and up to two status messages (e.g. showing that a task that is 40% complete, currently executing "step 2", "subtask c").
 * <p>
 * It also provides facilities for client code to determine whether a previously started task is still running, what it is currently doing, and how
 * much progress it has made. This can be useful when the original calling context is replaced by a new context that wants to pick up where the prior
 * one left off (e.g. when an Activity that has been recreated due to device rotation wants to display ongoing progress to the user, post-rotation).
 * <p>
 * AsyncTaskHelper assists with exception handling by putting a try/catch block around the execution of the background job, saving any thrown
 * exceptions and passing them to the provided AsyncTaskResultProcessor on the foreground thread, to be handled as needed.
 * <p>
 * An AsyncTaskHelper may optionally be registered with and controlled by an {@link AsyncTaskController} (such as {@link AsyncTaskManager}) via its
 * {@link AsyncTaskControllee} interface. This allows higher-level code which may be interacting with multiple AsyncTaskHelpers to keep track of them
 * and manage them more easily.
 * <p>
 * If an AsyncTaskHelper instance will have a lifetime that exceeds that of the Activity that creates it, be sure to call onActivityDetach and
 * onActivityAttach at the appropriate times. This will ensure that the AsyncTaskResultProcessor is provided a valid Activity in its callbacks.
 * 
 * @author Andy Dennie
 * 
 * @param <Result>
 */

/*
 * implementation note: The first template argument to AsyncTask is Void. An alternate design approach would have been
 * to make it AsyncTaskJob<Result> instead; then we wouldn't need to pass an AsyncTaskJob<Result> to the constructor,
 * and users of this class would call execute(job) instead of execute(). The reason it is done this way instead is
 * because the alternate approach results in a compiler warning on the execute(job) call:
 * "a generic array of AsyncTaskJob<Result> is created for a varargs parameter"
 * which can only be eliminated with a method-level @SuppressWarnings annotation (an unattractively big hammer).
 * Supposedly there is a Java 7 enhancement that would allow warning suppression to be specified on the called method
 * (as opposed to the caller), but Java 7 can't be used for Android app development at the time of this writing.
 */
public class AsyncTaskHelper<Result>
        extends AsyncTask<Void, Integer, Result>
        implements AsyncTaskControllee, AsyncTaskJobListener {
    private final AsyncTaskJob<Result> mJob;
    private final AsyncTaskResultProcessor<Result> mResultProcessor;
    private ProgressListener mProgressListener;
    private AsyncTaskController mController;
    private int mProgress;
    private String mProgressMessage1;
    private String mProgressMessage2;
    private Exception mJobException;

    /**
     * A lighter-weight constructor.
     * 
     * @param activity the current activity context
     * @param job a "function object" implementing the {@link AsyncTaskJob} interface, representing the job to be
     *            executed on the background thread
     * @param resultProcessor a "function object" implementing the {@link AsyncTaskResultProcessor} interface,
     *            representing the logic to be executed on the result, on the UI thread (optional, may be null)
     */
    public AsyncTaskHelper(final AsyncTaskJob<Result> job,
            final AsyncTaskResultProcessor<Result> resultProcessor) {
        mJob = checkNotNull(job, "job");
        mResultProcessor = resultProcessor; // may be null
    }

    // with a top-level progress message provided
    public AsyncTaskHelper(final AsyncTaskJob<Result> job,
            final AsyncTaskResultProcessor<Result> resultProcessor,
            final String progressMessage1) {
        this(job, resultProcessor, progressMessage1, null, null);
    }

    // with top-level and secondary progress messages provided
    public AsyncTaskHelper(final AsyncTaskJob<Result> job,
            final AsyncTaskResultProcessor<Result> resultProcessor,
            final String progressMessage1,
            final String progressMessage2) {
        this(job, resultProcessor, progressMessage1, progressMessage2, null);
    }

    /**
     * 
     * If you are going to register this AsyncTaskHelper with an AsyncTaskController, pass a null
     * ProgressListener to this constructor, or use one of the simpler constructors instead. This
     * is because the AsyncTaskController is responsible for assigning the AsyncProgressListener to the
     * AsyncTaskControllee objects that it manages.
     * 
     * @param activity the current activity context
     * @param job a "function object" implementing the {@link AsyncTaskJob} interface, representing the job to be
     *            executed on the background thread
     * @param resultProcessor a "function object" implementing the {@link AsyncTaskResultProcessor} interface,
     *            representing the logic to be executed on the result, on the UI thread (optional, may be null)
     * @param progressListener an object interested in receiving progress updates from the task (optional, may be null)
     * @param progressMessage1 a 1st-level description of the work to be performed (optional, may be null)
     * @param progressMessage2 a 2nd-level description of the work to be performed (optional, may be null)
     */
    public AsyncTaskHelper(final AsyncTaskJob<Result> job,
            final AsyncTaskResultProcessor<Result> resultProcessor,
            final String progressMessage1,
            final String progressMessage2,
            final ProgressListener progressListener) {

        mJob = checkNotNull(job, "job");
        mResultProcessor = resultProcessor; // may be null
        mProgressListener = progressListener; // may be null
        mProgressMessage1 = progressMessage1; // may be null
        mProgressMessage2 = progressMessage2; // may be null
    }

    public int getProgress() {
        return mProgress;
    }

    void setProgress(final int progress) {
        mProgress = progress;
    }

    public String getProgressMessage1() {
        return mProgressMessage1;
    }

    public void setProgressMessage1(final String progressMessage1) {
        mProgressMessage1 = checkNotNull(progressMessage1, "progressMessage1");
    }

    public String getProgressMessage2() {
        return mProgressMessage2;
    }

    public void setProgressMessage2(final String progressMessage2) {
        mProgressMessage2 = checkNotNull(progressMessage2, "progressMessage2");
    }

    // Override AsyncTask methods...

    // called on the UI thread, prior to kicking off the background thread
    @Override
    protected void onPreExecute() {
        // notify the outbound progress listener that we're getting started.
        if (mProgressListener != null) {
            mProgressListener.onStartProgress(mProgressMessage1, mProgressMessage2);
        }
    }

    @Override
    protected Result doInBackground(final Void... params) {
        try {
            // passing 'this' to doJob allows the job to invoke onUpdateProgress() or isCancelled().
            return mJob.doJob(this);
        }
        catch (Exception e) {
            mJobException = e;
            return null;
        }
    }

    // called on the UI thread, in response to a call to publishProgress on the background thread
    @Override
    protected void onProgressUpdate(final Integer... params) {
        // notify the outbound progress listener regarding ongoing progress
        if (mProgressListener != null) {
            mProgressListener.onUpdateProgress(params[0], mProgressMessage1, mProgressMessage2);
        }

    }

    // called on the UI thread, after doInBackground completes.
    @Override
    protected void onPostExecute(final Result result) {
        if (mResultProcessor != null) {
            if (mJobException != null)
                mResultProcessor.processException(mJobException);
            else
                mResultProcessor.processResult(result);

        }

        // notify the outbound progress listener that we're done.
        if (mProgressListener != null)
            mProgressListener.onEndProgress();

        // notify the task manager that we're done
        if (mController != null)
            mController.onTaskCompleted(this);
    }

    // called by on the UI thread, after a task is cancelled.
    @Override
    protected void onCancelled() {
        // notify the outbound progress listener that we're done.
        if (mProgressListener != null)
            mProgressListener.onEndProgress();

        // notify the task manager that we're done
        if (mController != null)
            mController.onTaskCompleted(this);
    }

    /*
     * Implement the AsyncTaskControllee interface.
     */
    @Override
    public void setController(final AsyncTaskController controller) {
        mController = checkNotNull(controller, "controller");
    }

    @Override
    public ProgressListener getProgressListener() {
        return mProgressListener;
    }

    @Override
    public void setProgressListener(final ProgressListener listener) {
        mProgressListener = checkNotNull(listener, "listener");
    }

    @Override
    public boolean isRunning() {
        return (getStatus() != AsyncTask.Status.FINISHED) && (!isCancelled());
    }

    @Override
    public void cancelTask() {
        cancel(true);
    }

    @Override
    public void onUiPause() {
        if (mProgressListener != null)
            mProgressListener.onUiPause();
    }

    @Override
    public void onUiResume() {
        if (mProgressListener != null)
            mProgressListener.onUiResume();
    }

    @Override
    public void onActivityAttached(Activity activity) {
        if (mResultProcessor != null)
            mResultProcessor.onActivityAttached(activity);
        if (mProgressListener != null)
            mProgressListener.onActivityAttached(activity);
    }

    @Override
    public void onActivityDetached() {
        if (mResultProcessor != null)
            mResultProcessor.onActivityDetached();
        if (mProgressListener != null)
            mProgressListener.onActivityDetached();
    }

    // implement the AsyncTaskJobListener interface

    /*
     * This method is called by jobs running on the background thread to communicate intermediate progress back to this
     * object, which then forwards that information (on the UI thread, via publishProgress) to the progress listener
     * attached to this object.
     */
    @Override
    public void publishProgress(final int progress,
            final String progressMessage1,
            final String progressMessage2) {
        setProgress(progress);
        // if a non-null progress title and/or message was supplied, replace the current ones, otherwise leave them as
        // they are.
        if (progressMessage1 != null)
            setProgressMessage1(progressMessage1);
        if (progressMessage2 != null)
            setProgressMessage2(progressMessage2);
        publishProgress(progress);
    }

    @Override
    public boolean isTaskCancelled() {
        return isCancelled();
    }

}
