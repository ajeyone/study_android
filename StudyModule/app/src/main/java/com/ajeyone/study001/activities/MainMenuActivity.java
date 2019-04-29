package com.ajeyone.study001.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.ajeyone.study001.R;

public class MainMenuActivity extends AppCompatActivity {
    private ImageView imageView;
    private CircleAdapter circleAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        findViewById(R.id.button_center).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animate2();
            }
        });

        imageView = findViewById(R.id.imageView);
        circleAdapter = new CircleAdapter(imageView);
    }

    private void animate1() {
        ValueAnimator animator = ValueAnimator.ofFloat(0, 360);
        animator.setDuration(500);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float currentValue = (float) animation.getAnimatedValue();
                circleAdapter.setAngle(currentValue);
            }
        });
        animator.start();
    }

    private void animate2() {
        PropertyValuesHolder holderAngle = PropertyValuesHolder.ofFloat("angle", 0, -90, -180, -270);
        PropertyValuesHolder holderRadius = PropertyValuesHolder.ofInt("radius", 0, 100, 200, 300);

        ObjectAnimator animator = ObjectAnimator.ofPropertyValuesHolder(circleAdapter, holderAngle, holderRadius);

        animator.setDuration(2000);
        animator.start();
    }

    private static class CircleAdapter {
        private View view;

        public CircleAdapter(View view) {
            this.view = view;
        }

        public float getAngle() {
            ViewGroup.LayoutParams lp = view.getLayoutParams();
            if (lp instanceof ConstraintLayout.LayoutParams) {
                return ((ConstraintLayout.LayoutParams) lp).circleAngle;
            }
            return 0;
        }

        public void setAngle(float angle) {
            ViewGroup.LayoutParams lp = view.getLayoutParams();
            if (lp instanceof ConstraintLayout.LayoutParams) {
                ((ConstraintLayout.LayoutParams) lp).circleAngle = angle;
                view.requestLayout();
            }
        }

        public int getRadius() {
            ViewGroup.LayoutParams lp = view.getLayoutParams();
            if (lp instanceof ConstraintLayout.LayoutParams) {
                return ((ConstraintLayout.LayoutParams) lp).circleRadius;
            }
            return 0;
        }

        public void setRadius(int radius) {
            ViewGroup.LayoutParams lp = view.getLayoutParams();
            if (lp instanceof ConstraintLayout.LayoutParams) {
                ((ConstraintLayout.LayoutParams) lp).circleRadius = radius;
                view.requestLayout();
            }
        }
    }
}
