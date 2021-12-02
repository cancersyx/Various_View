package com.zsf.variousview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;

/**
 * @author EWorld
 * 2018/12/25
 */
public class VolumeView extends View {
    private static final String TAG = "VolumeView";
    private int mRectCount = 12;
    private int mWidth;
    private int mHeight;
    private int mRectWidth;
    private int mRectHeight;
    private Paint mPaint;
    private int offset = 5;
    private LinearGradient mLinearGradient;

    public VolumeView(Context context) {
        this(context, null);
    }

    public VolumeView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public VolumeView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mPaint = new Paint();
        mPaint.setColor(Color.YELLOW);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setAntiAlias(true);


    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = getWidth();//返回你的View的宽度
        Log.d(TAG,"mWidth = " + mWidth);
        mRectHeight = getHeight();//返回你的View的高度
        Log.d(TAG,"mRectHeight = " + mRectHeight);
        mRectWidth = (int) (mWidth * 0.6 / mRectCount);
        Log.d(TAG,"mRectWidth = " + mRectWidth);
        mLinearGradient = new LinearGradient(0, 0, mRectWidth, mRectHeight, Color.YELLOW, Color.RED,
                Shader.TileMode.CLAMP);
        mPaint.setShader(mLinearGradient);


    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        for (int i = 0; i < mRectCount; i++) {
            double random = Math.random();
            float currentHeight = (float) (mRectHeight * random);
            canvas.drawRect((float) (mWidth * 0.4 / 2 + mRectWidth * i + offset),
                    currentHeight,
                    (float) (mWidth * 0.4 / 2 + mRectWidth * (i + 1)),
                    mRectHeight,
                    mPaint);
            Log.d(TAG,"left : " + (float) (mWidth * 0.4 / 2 + mRectWidth * i + offset)
             + ",top :" + currentHeight + ",right : " + (float) (mWidth * 0.4 / 2 + mRectWidth * (i + 1))
             + ",bottom : " + mRectHeight);
            canvas.drawRect((float) (mWidth * 0.4 / 2 + mRectWidth * i + offset),
                    currentHeight,
                    (float) (mWidth * 0.4 / 2 + mRectWidth * (i + 1)),
                    (currentHeight + mRectHeight),
                    mPaint);
        }
        postInvalidateDelayed(300);
    }
}
