package com.ajeyone.study001.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.ajeyone.study001.R;
import com.ajeyone.study001.TaskUtils;

public class LaunchModeBActivity extends AppCompatActivity {
    private static final String TAG = "LaunchModeActivity_B";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch_mode_b);
        boolean newTask = (getIntent().getFlags() & Intent.FLAG_ACTIVITY_NEW_TASK) != 0;
        Log.d(TAG, "onCreate: task: " + TaskUtils.getTaskDebugString(this) + ", new task: " + newTask);
    }
}
