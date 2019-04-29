package com.ajeyone.lrudemo;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements ImageListFragment.OnListFragmentInteractionListener {
    private static final String TAG = "ajeyonerxjava";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
    }

    @Override
    public void onListFragmentInteraction(String imageUrl) {
        Toast.makeText(this, imageUrl, Toast.LENGTH_SHORT).show();
    }
}
