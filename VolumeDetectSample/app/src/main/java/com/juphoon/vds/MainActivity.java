package com.juphoon.vds;

import android.Manifest;
import android.content.pm.PackageManager;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.media.audiofx.AutomaticGainControl;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.lang.ref.WeakReference;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    public static final int REQUEST_CODE = 10001;

    private static final int SAMPLE_RATE_IN_HZ = 44100;
    static final int BUFFER_SIZE = AudioRecord.getMinBufferSize(SAMPLE_RATE_IN_HZ,
            AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT);

    private AudioRecord mAudioRecord;
    private AutomaticGainControl _AGC;
    private int mSessionId;

    private volatile boolean mRecording = false;
    private MyHandler mHandler = new MyHandler(this);

    private VolumeView mVolumeView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mVolumeView = findViewById(R.id.volume_view);

        int result = ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO);
        if (result != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE && grantResults.length == 1) {
            int result = grantResults[0];
            if (result == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Record audio permission granted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Record audio permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void onStart(View view) {
        if (mRecording) {
            return;
        }
        mAudioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC,
                SAMPLE_RATE_IN_HZ, AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_PCM_16BIT, BUFFER_SIZE);

        try {
            mSessionId = mAudioRecord.getAudioSessionId();
            if (AutomaticGainControl.isAvailable()) {
                _AGC = AutomaticGainControl.create(mSessionId);
                if (_AGC != null) {
                    _AGC.setEnabled(true);
                }
            }
        } catch (Exception e) {
            Log.d(TAG, "Set AGC failed: " + e.getMessage());
        }

        if (mAudioRecord.getState() != AudioRecord.STATE_INITIALIZED) {
            Log.d(TAG, "not initialized");
            return;
        }

        mAudioRecord.startRecording();
        mRecording = true;

        new Thread() {
            @Override
            public void run() {
                short[] buffer = new short[BUFFER_SIZE / 2];
                while (mRecording) {

                    //r是实际读取的数据长度，一般而言r会小于buffersize
                    int r = mAudioRecord.read(buffer, 0, buffer.length);
                    double v = 0;
//                    // 将 buffer 内容取出，进行平方和运算
//                    for (short value : buffer) {
//                        v += value * value;
//                    }
//                    // 平方和除以数据总长度，得到音量大小。
//                    double mean = v / (double) r;
                    for (short value : buffer) {
                        v += Math.abs(value);
                    }
                    double mean = v / r;
                    double volume = 20 * Math.log10(mean);
                    Log.d(TAG, "run: " + v + "," + r + ", " + mean + "," + volume + " | " + BUFFER_SIZE);
                    mHandler.sendMessage(Message.obtain(mHandler, MESSAGE_UPDATE, (int) volume, 0));
                    // 大概一秒十次
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                mAudioRecord.stop();
                mAudioRecord.release();
                mAudioRecord = null;
            }
        }.start();
    }

    public void onStop(View view) {
        mRecording = false;
    }

    private static final int MESSAGE_UPDATE = 90900;

    private static class MyHandler extends Handler {
        private WeakReference<MainActivity> ref;

        MyHandler(MainActivity activity) {
            ref = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            MainActivity activity = ref.get();
            if (activity == null) {
                return;
            }
            switch (msg.what) {
                case MESSAGE_UPDATE:
                    activity.updateVolume(msg.arg1);
                    break;
            }
        }
    }

    private void updateVolume(int volume) {
        Log.d(TAG, "updateVolume: " + volume);
        if (volume > 0) {
            mVolumeView.appendVolume(volume);
        }
    }
}
