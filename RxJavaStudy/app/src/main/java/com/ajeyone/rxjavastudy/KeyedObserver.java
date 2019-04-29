package com.ajeyone.rxjavastudy;

import android.util.Log;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

public class KeyedObserver<T, K> implements Observer<T> {
    private K key;

    public KeyedObserver(K key) {
        this.key = key;
    }

    int time = 0;

    @Override
    public void onSubscribe(Disposable d) {
        logthread("onSubscribe");
    }

    @Override
    public void onNext(T t) {
        logthread("onNext: " + key + " -> " + t);
        time++;
    }

    @Override
    public void onError(Throwable e) {
        logthread("onError");
    }

    @Override
    public void onComplete() {
        logthread("onComplete: " + key + " -> " + time);
    }

    private static void logthread(String message) {
        Log.d("ajeyoneRxJava", message + " | " + Thread.currentThread().getName());
    }
}
