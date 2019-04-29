package com.ajeyone.study001.views;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;

import com.ajeyone.study001.R;

import java.util.ArrayList;

import androidx.annotation.Nullable;

public class ClockView extends View {
    private static final String TAG = "ClockView";

    private ArrayList<Segment[]> listOfSegmentArray = new ArrayList<>();

    public static class Segment {
        public float startDegree;
        public float endDegree;
        public int color;

        public Segment(float startDegree, float endDegree, int color) {
            this.startDegree = startDegree;
            this.endDegree = endDegree;
            this.color = color;
        }

        public Segment() {
        }
    }

    public ClockView(Context context) {
        super(context);
        init(null);
    }

    public ClockView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public ClockView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    private void init(@Nullable AttributeSet attrs) {
        mDensity = getDensity();

        mArcPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mArcPaint.setStyle(Paint.Style.STROKE);
        mArcPaint.setStrokeCap(Paint.Cap.ROUND);
        mArcPaint.setStrokeJoin(Paint.Join.BEVEL);

        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.ClockView);
        int lineWidth = a.getDimensionPixelSize(R.styleable.ClockView_lineWidth, 20);
        a.recycle();

        mArcPaint.setStrokeWidth(lineWidth);
    }

    private Paint mArcPaint;
    private float mDensity;

    private ArrayList<RectF> arcRects = new ArrayList<>();

    public void addSegments(Segment[] segments) {
        listOfSegmentArray.add(segments);
        arcRects.add(new RectF());
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = getDefaultSize(getSuggestedMinimumWidth(), widthMeasureSpec);
        int height = getDefaultSize(getSuggestedMinimumHeight(), heightMeasureSpec);
        int size = Math.min(width, height);
        setMeasuredDimension(size, size);

        float x = size / 2.0f;
        float y = size / 2.0f;
        float r = size / 2.0f - mArcPaint.getStrokeWidth() / 2;

        float left = x - r;
        float right = x + r;
        float top = y - r;
        float bottom = y + r;

        float step = mArcPaint.getStrokeWidth();
        for (RectF rect : arcRects) {
            rect.left = left;
            rect.right = right;
            rect.top = top;
            rect.bottom = bottom;
            left += step;
            right -= step;
            top += step;
            bottom -= step;
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int n = listOfSegmentArray.size();
        for (int i = 0; i < n; i++) {
            Segment[] segments = listOfSegmentArray.get(i);
            RectF rect = arcRects.get(i);
            for (Segment s : segments) {
                mArcPaint.setColor(s.color);
                canvas.drawArc(rect, s.startDegree, s.endDegree - s.startDegree, false, mArcPaint);
            }
        }
    }

    private float getDensity() {
        DisplayMetrics dm = new DisplayMetrics();
        Activity activity = (Activity) getContext();
        activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
        return dm.density;
    }

    private static void log(String format, Object... args) {
        String s = String.format(format, args);
        Log.d(TAG, s);
    }
}
