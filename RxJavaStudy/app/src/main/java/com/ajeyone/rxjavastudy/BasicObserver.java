package com.ajeyone.rxjavastudy;

import android.util.Log;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

public class BasicObserver<T> implements Observer<T> {
    int time = 0;

    @Override
    public void onSubscribe(Disposable d) {
        logthread("onSubscribe");
    }

    @Override
    public void onNext(T t) {
        logthread("onNext: " + t);
        time++;
    }

    @Override
    public void onError(Throwable e) {
        logthread("onError");
    }

    @Override
    public void onComplete() {
        logthread("onComplete:" + time);
    }

    private static void logthread(String message) {
        Log.d("ajeyoneRxJava", message + " | " + Thread.currentThread().getName());
    }
}
