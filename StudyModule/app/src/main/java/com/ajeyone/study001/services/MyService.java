package com.ajeyone.study001.services;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import java.util.Random;

public class MyService extends Service {
    private static final String TAG = "MyService";

    public static MyService sInstance = null;

    private int mLastId = 0;
    private Random random = new Random();

    public MyService() {
        Log.d(TAG, "MyService: init()");
    }

    public void stopOnce() {
        int id = random.nextInt(mLastId + 1) + 1;
        Log.d(TAG, "stopOnce: random, id=" + id);
        stopSelfResult(id);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate: ");
        sInstance = this;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand: startId=" + startId);
        mLastId = startId;
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
        Log.d(TAG, "onStart: startId=" + startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: ");
        sInstance = null;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.d(TAG, "onUnbind: ");
        return super.onUnbind(intent);
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "onBind: ");
        return new MyBinder();
    }

    public interface MyIBinder {
        int calculate(int a, int b);
    }

    public class MyBinder extends Binder implements MyIBinder {
        @Override
        public int calculate(int a, int b) {
            return MyService.this.calculate(a, b);
        }
    }

    private int calculate(int a, int b) {
        return a - b / 2;
    }
}
