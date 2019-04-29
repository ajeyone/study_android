package com.juphoon.vds;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import java.util.LinkedList;
import java.util.Queue;

public class VolumeView extends View {
    private int DEFAULT_COLOR = 0xff8fc320;

    private int mColorLess20;
    private int mColorLess40;
    private int mColorLess60;
    private int mColor;

    private Queue<Integer> mVolumeQueue = new LinkedList<>();
    private Paint mPaint;
    private int mBarSize;
    private int mBarWidthPerDB;

    public VolumeView(Context context) {
        this(context, null);
    }

    public VolumeView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public VolumeView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(DEFAULT_COLOR);

        mBarSize = getContext().getResources().getDimensionPixelSize(R.dimen.bar_size);
        mBarWidthPerDB = getContext().getResources().getDimensionPixelSize(R.dimen.bar_width_per_db);

        mColorLess20 = getColorWithBrightness(DEFAULT_COLOR, 0.4);
        mColorLess40 = getColorWithBrightness(DEFAULT_COLOR, 0.7);
        mColorLess60 = getColorWithBrightness(DEFAULT_COLOR, 0.8);
        mColor = DEFAULT_COLOR;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int x = 0;
        int height = getMeasuredHeight();
        int lastV = -1;
        for (int v : mVolumeQueue) {
            mPaint.setColor(getCalculatedColor(v));
            canvas.drawRect(x, height - v * mBarWidthPerDB, x + mBarSize, height, mPaint);
            if (lastV > 0) {
                if (v - lastV > 20) {
                    int y = height - v * mBarWidthPerDB - mBarSize * 2;
                    mPaint.setColor(0xffff0000);
                    canvas.drawRect(x, y, x + mBarSize, y + mBarSize, mPaint);
                }
            }
            lastV = v;
            x += mBarSize;
        }
    }

    private int getCalculatedColor(int v) {
        if (v < 20) {
            return mColorLess20;
        } else if (v < 40) {
            return mColorLess40;
        } else if (v < 60) {
            return mColorLess60;
        } else {
            return mColor;
        }
    }

    public static int getColorWithBrightness(int color, double factor) {
        int r = (int) (((color >> 16) & 0XFF) * factor) & 0XFF;
        int g = (int) (((color >> 8) & 0XFF) * factor) & 0XFF;
        int b = (int) ((color & 0XFF) * factor) & 0XFF;
        return (color & 0XFF000000) | (r << 16) | (g << 8) | b;
    }

    public void appendVolume(int volume) {
        mVolumeQueue.add(volume);
        int max = getMeasuredWidth() / mBarSize;
        if (mVolumeQueue.size() > max) {
            mVolumeQueue.poll();
        }
        postInvalidate();
    }
}
