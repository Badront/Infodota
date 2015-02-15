package com.badr.infodota.util;

import android.annotation.TargetApi;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;

/**
 * User: ABadretdinov
 * Date: 06.02.14
 * Time: 13:52
 */
public class LoaderProgressTask<R> extends AsyncTask<Object, String, R> implements ProgressTask.OnPublishProgressListener {
    private final ProgressTask<R> task;
    private OnProgressUpdateListener listener;
    private String error = null;

    public LoaderProgressTask(ProgressTask<R> task, OnProgressUpdateListener listener) {
        this.task = task;
        this.listener = listener;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        if (listener != null) {
            listener.onStart();
        }
    }

    @Override
    protected R doInBackground(Object... params) {
        try {
            return task.doTask(this);
        } catch (Exception e) {
            error = e.getLocalizedMessage();
            Log.e(getClass().getName(), "Error in: " + task.getName(), e);
            return null;
        }
    }

    @Override
    protected void onProgressUpdate(String... values) {
        if (listener != null && !isCancelled()) {
            //todo хз как сделать. если OnProgressUpdateListener
            listener.onProgressUpdate(values);
        }
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
        task.handleError(ErrorCodes.USER_CANCELED);
    }

    @Override
    protected void onPostExecute(R r) {
        super.onPostExecute(r);
        if (!isCancelled()) {
            if (r == null) {
                task.handleError(error);
            } else {
                task.doAfterTask(r);
            }
            //todo проверить еще, мб зря вынес
            if (listener != null) {
                listener.onFinish();
            }
        } else {
            task.handleError(ErrorCodes.USER_CANCELED);
        }
    }

    @Override
    public void progressUpdated(String... progress) {
        //todo если прогресс есть, но <1, то не вызывать, иначе слишком затратно.
        if (!isCancelled()) {
            publishProgress(progress);
        }
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB) // API 11
    public final AsyncTask<Object, String, R> execute() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            return executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, 1);
        } else {
            return execute(1);
        }
    }


    public interface OnProgressUpdateListener {
        void onStart();

        void onProgressUpdate(String... progress);

        void onFinish();
    }
}
