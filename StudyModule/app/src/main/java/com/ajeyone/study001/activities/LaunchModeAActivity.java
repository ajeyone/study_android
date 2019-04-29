package com.ajeyone.study001.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.ajeyone.study001.R;
import com.ajeyone.study001.TaskUtils;

public class LaunchModeAActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "LaunchModeActivity_A";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch_mode_a);
        findViewById(R.id.startLaunchModeB).setOnClickListener(this);
        boolean newTask = (getIntent().getFlags() & Intent.FLAG_ACTIVITY_NEW_TASK) != 0;
        Log.d(TAG, "onCreate: task: " + TaskUtils.getTaskDebugString(this) + ", new task: " + newTask);

    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(this, LaunchModeBActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}
