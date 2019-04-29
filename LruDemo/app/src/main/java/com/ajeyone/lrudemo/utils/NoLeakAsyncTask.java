package com.ajeyone.lrudemo.utils;

import android.os.AsyncTask;

import java.lang.ref.WeakReference;

public abstract class NoLeakAsyncTask<Param, Progress, Result> extends AsyncTask<Param, Progress, Result> {
    public interface CompleteListener<Result> {
        void onComplete(Result result);
    }

    private WeakReference<CompleteListener<Result>> completeListenerRef;

    public void setCompleteListener(CompleteListener<Result> completeListener) {
        completeListenerRef = new WeakReference<>(completeListener);
    }

    @Override
    protected void onPostExecute(Result result) {
        super.onPostExecute(result);
        CompleteListener<Result> completeListener = completeListenerRef.get();
        if (completeListener != null) {
            completeListener.onComplete(result);
        }
    }
}
