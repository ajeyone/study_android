package com.ajeyone.libdemo1;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import java.util.Date;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LibMainActivity extends AppCompatActivity {

    @BindView(R2.id.text)
    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lib_main);

        ButterKnife.bind(this);
    }

    @OnClick({R2.id.button007, R2.id.button2})
    void changeText(Button button) {
        int id = button.getId();
        if (id == R.id.button007) {
            textView.setText(new Date().toString());
        } else if (id == R.id.button2) {
            textView.setText(String.valueOf(new Random().nextInt()));
        }
    }
}
