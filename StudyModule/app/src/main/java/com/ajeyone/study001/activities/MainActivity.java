package com.ajeyone.study001.activities;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;

//import com.ajeyone.libdemo1.LibMainActivity;
import com.ajeyone.study001.R;
import com.ajeyone.study001.TaskUtils;
import com.ajeyone.study001.services.MyService;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;
import butterknife.OnClick;

import com.ajeyone.libanno1.MyBindView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "MainActivityLifeCycle";

    private boolean shouldUnbind = false;

    @MyBindView("abcdefg")
    private double pValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG, "onCreate: use butterknife");
        Log.d("LaunchModeActivity_Main", "onCreate: task: " + TaskUtils.getTaskDebugString(this));
        ButterKnife.bind(this);
    }

    @OnClick({R.id.startSecond, R.id.startDialogActivity, R.id.startDialog,
            R.id.refreshMenu, R.id.startLaunchModeA, R.id.startService,
            R.id.stopService, R.id.bindService, R.id.useService, R.id.unbindService
    })
    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.startSecond) {
            Intent intent = new Intent(this, SecondActivity.class);
            startActivity(intent);
        } else if (id == R.id.startDialogActivity) {
//            Intent intent = new Intent()
//            startActivity(intent);
        } else if (id == R.id.startDialog) {
            Log.d(TAG, "onClick: dialog");
            new AlertDialog.Builder(this)
                    .setTitle("Alert Dialog")
                    .setMessage("Message is displayed")
                    .setPositiveButton("OK", null)
                    .setNegativeButton("Cancel", null)
                    .show();
        } else if (id == R.id.refreshMenu) {
            invalidateOptionsMenu();
        } else if (id == R.id.startLaunchModeA) {
            Intent intent = new Intent(this, LaunchModeAActivity.class);
            startActivity(intent);
        } else if (id == R.id.startService) {
            Intent intent = new Intent(this, MyService.class);
            startService(intent);
        } else if (id == R.id.stopService) {
            Intent intent = new Intent(this, MyService.class);
            stopService(intent);
//            MyService.sInstance.stopOnce();
        } else if (id == R.id.bindService) {
            doBind();
        } else if (id == R.id.useService) {
            int result = mServiceBinder.calculate(10, 4);
            Log.d("MyService", "calculated: " + result);
        } else if (id == R.id.unbindService) {
            doUnbind();
        }
    }

    private void doBind() {
        Intent intent = new Intent(this, MyService.class);
        if (bindService(intent, mConnection, Context.BIND_AUTO_CREATE)) {
            shouldUnbind = true;
        } else {
            Log.e(TAG, "doBind: bind failed");
        }
    }

    private void doUnbind() {
        if (shouldUnbind) {
            unbindService(mConnection);
            shouldUnbind = false;
        }
    }

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.d("MyService", "onServiceConnected: ");
            mServiceBinder = (MyService.MyIBinder) service;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.d("MyService", "onServiceDisconnected: ");
            mServiceBinder = null;
        }
    };

    private MyService.MyIBinder mServiceBinder;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.d(TAG, "onCreateOptionsMenu: ");
        getMenuInflater().inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        Log.d(TAG, "onPrepareOptionsMenu: ");
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart: ");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: ");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause: ");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop: ");
    }

    @Override
    protected void onDestroy() {
        doUnbind();
        super.onDestroy();
        Log.d(TAG, "onDestroy: ");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d(TAG, "onRestart: ");
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.d(TAG, "onSaveInstanceState: 1");
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        Log.d(TAG, "onRestoreInstanceState: 1");
    }
}
