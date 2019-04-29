package com.ajeyone.study001.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.Layout;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.ajeyone.study001.R;

public class CircleGroupView extends ViewGroup {
    private static final String TAG = "CircleGroupView";

    public static class LayoutParams extends ViewGroup.LayoutParams {
        static final int DEFAULT_DISTANCE = 30;
        static final int DEFAULT_ANGLE = 0;

        public int distance;
        public float angle;

        public LayoutParams(Context c, AttributeSet attrs) {
            super(c, attrs);
            TypedArray a = c.obtainStyledAttributes(attrs, R.styleable.CircleGroupView_Layout);
            distance = a.getDimensionPixelSize(R.styleable.CircleGroupView_Layout_layout_distance, DEFAULT_DISTANCE);
            angle = degree2radian(a.getFloat(R.styleable.CircleGroupView_Layout_layout_angle, DEFAULT_ANGLE));
            a.recycle();
            Log.d(TAG, "LayoutParams: <init>: attrs");
        }

        public LayoutParams(int width, int height) {
            super(width, height);
            setDefault();
            Log.d(TAG, "LayoutParams: <init>: width, height");
        }

        public LayoutParams(ViewGroup.LayoutParams source) {
            super(source);
            if (source instanceof LayoutParams) {
                LayoutParams other = (LayoutParams) source;
                angle = other.angle;
                distance = other.distance;
            } else {
                setDefault();
            }
            Log.d(TAG, "LayoutParams: <init>: source");
        }

        private void setDefault() {
            distance = DEFAULT_DISTANCE;
            angle = degree2radian(DEFAULT_ANGLE);
        }

        public int getX() {
            return (int) (distance * Math.cos(angle));
        }

        public int getY() {
            return (int) (distance * Math.sin(angle));
        }

        private static float degree2radian(float angle) {
            return (float) (angle / 180 * Math.PI);
        }
    }

    public CircleGroupView(Context context) {
        super(context);
    }

    public CircleGroupView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CircleGroupView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int specWidthMode = MeasureSpec.getMode(widthMeasureSpec);
        int specWidthSize = MeasureSpec.getSize(widthMeasureSpec);
        int specHeightMode = MeasureSpec.getMode(heightMeasureSpec);
        int specHeightSize = MeasureSpec.getSize(heightMeasureSpec);

        Log.d(TAG, "onMeasure: widthMeasureSpec: " + MeasureSpec.toString(widthMeasureSpec));
        Log.d(TAG, "onMeasure: heightMeasureSpec: " + MeasureSpec.toString(heightMeasureSpec));

        int maxAbsX = 0, maxAbsY = 0;
        int n = getChildCount();
        Log.d(TAG, "onMeasure: child count: " + n);
        for (int i = 0; i < n; i++) {
            View child = getChildAt(i);
            Log.d(TAG, "onMeasure: child[" + i + "]: " + child.toString());
            LayoutParams lp = (LayoutParams) child.getLayoutParams();
            Log.d(TAG, "onMeasure: child[" + i + "]: lp: " + lp.distance + "," + lp.angle);
            int polarX = lp.getX();
            int polarY = lp.getY();
            Log.d(TAG, "onMeasure: child[" + i + "]: polar: (" + polarX + "," + polarY + ")");

            int spaceXCannotUse = Math.abs(polarX) * 2;
            int spaceYCannotUse = Math.abs(polarY) * 2;
            measureChild(child, widthMeasureSpec, spaceXCannotUse, heightMeasureSpec, spaceYCannotUse);

            int childMeasuredWidth = child.getMeasuredWidth();
            int childMeasuredHeight = child.getMeasuredHeight();
            Log.d(TAG, "onMeasure: child[" + i + "]: child measured: (" + childMeasuredWidth + "," + childMeasuredHeight + ")");

            int x0 = polarX - childMeasuredWidth / 2;
            int x1 = polarX + childMeasuredWidth / 2;
            maxAbsX = Math.max(maxAbsX, Math.max(Math.abs(x0), Math.abs(x1)));

            int y0 = polarY - childMeasuredHeight / 2;
            int y1 = polarY + childMeasuredHeight / 2;
            maxAbsY = Math.max(maxAbsY, Math.max(Math.abs(y0), Math.abs(y1)));

            Log.d(TAG, "onMeasure: child[" + i + "]: x0x1y0y1:" + x0 + "," + x1 + "," + y0 + "," + y1 + ")");
            Log.d(TAG, "onMeasure: child[" + i + "]: maxAbs: (" + maxAbsX + "," + maxAbsY + ")");
        }

        int width = getSize(specWidthMode, specWidthSize, maxAbsX * 2);
        int height = getSize(specHeightMode, specHeightSize, maxAbsY * 2);
        Log.d(TAG, "onMeasure: setMeasuredDimension: " + width + "," + height);
        setMeasuredDimension(width, height);
    }

    private int getSize(int specMode, int specSize, int size) {
        switch (specMode) {
            case MeasureSpec.UNSPECIFIED:
                return size;
            case MeasureSpec.EXACTLY:
                return specSize;
            case MeasureSpec.AT_MOST:
                return Math.min(size, specSize);
        }
        return size;
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        Log.d(TAG, "onLayout: " + l + "," + t + "," + r + "," + b);
        int viewGroupWidth = r - l;
        int viewGroupHeight = b - t;
        Log.d(TAG, "onLayout: view group size: " + viewGroupWidth + "," + viewGroupHeight);

        int n = getChildCount();
        for (int i = 0; i < n; i++) {
            View child = getChildAt(i);
            LayoutParams lp = (LayoutParams) child.getLayoutParams();
            int childMeasuredWidth = child.getMeasuredWidth();
            int childMeasuredHeight = child.getMeasuredHeight();

            int x = lp.getX();
            int y = lp.getY();

            int childLeft = viewGroupWidth / 2 + x - childMeasuredWidth / 2;
            int childRight = childLeft + childMeasuredWidth;
            int childTop = viewGroupHeight / 2 + y - childMeasuredHeight / 2;
            int childBottom = childTop + childMeasuredHeight;
            Log.d(TAG, "onLayout: child: " + childLeft + "," + childTop + "," + childRight + "," + childBottom);
            child.layout(childLeft, childTop, childRight, childBottom);
        }
    }

    @Override
    protected boolean checkLayoutParams(ViewGroup.LayoutParams p) {
        return p instanceof LayoutParams;
    }

    @Override
    protected ViewGroup.LayoutParams generateLayoutParams(ViewGroup.LayoutParams p) {
        return new LayoutParams(p);
    }

    @Override
    public ViewGroup.LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new LayoutParams(getContext(), attrs);
    }

    @Override
    protected ViewGroup.LayoutParams generateDefaultLayoutParams() {
        return new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
    }

    private void measureChild(View child, int parentWidthMeasureSpec, int widthUsed, int parentHeightMeasureSpec, int heightUsed) {
        final ViewGroup.LayoutParams lp = child.getLayoutParams();
        final int childWidthMeasureSpec = getChildMeasureSpec(parentWidthMeasureSpec,
                widthUsed + getPaddingLeft() + getPaddingRight(),
                lp.width);
        final int childHeightMeasureSpec = getChildMeasureSpec(parentHeightMeasureSpec,
                heightUsed + getPaddingTop() + getPaddingBottom(),
                lp.height);
        child.measure(childWidthMeasureSpec, childHeightMeasureSpec);
    }
}
