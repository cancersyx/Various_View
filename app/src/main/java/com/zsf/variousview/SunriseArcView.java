package com.zsf.variousview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;


import java.util.function.LongUnaryOperator;

import androidx.annotation.Nullable;


/**
 * Created by EWorld
 * 2019/8/5 0005
 * 日出日落View
 */
public class SunriseArcView extends View {
    private static final String TAG = "SunriseArcView";
    private Paint mPaint;
    private Paint mBgArcPaint;
    private int mHeight;
    private int mWidth;
    private float mSweepValue = 90;//0-180取值

    private static int COLOR = Color.WHITE;

    public SunriseArcView(Context context) {
        this(context, null);
    }

    public SunriseArcView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SunriseArcView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        COLOR = context.getResources().getColor(R.color.color_BFC4C4);
        init();
    }

    private void init() {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setStrokeWidth(3);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(COLOR);

        mBgArcPaint = new Paint();
        mBgArcPaint.setAntiAlias(true);
        mBgArcPaint.setStrokeWidth(3);
        mBgArcPaint.setStyle(Paint.Style.STROKE);
        mBgArcPaint.setColor(getResources().getColor(R.color.color_sunrise_bg_area));


    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mHeight = getMeasuredHeight();
        mWidth = getMeasuredWidth();
        setMeasuredDimension(measureWidth(widthMeasureSpec), measureHeight(heightMeasureSpec));
    }

    private int measureHeight(int measureSpec) {
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);
        int result = 0;
        if (specMode == MeasureSpec.EXACTLY) {
            result = specSize;
        } else {
            result = 120;
            if (specMode == MeasureSpec.AT_MOST) {
                result = Math.min(result, specSize);
            }
        }
        return result;
    }

    private int measureWidth(int measureSpec) {
        //1.从MeasureSpec中提取出具体的测量模式和大小
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);
        //2.根据测量模式给出不同的测量值
        int result = 0;
        if (specMode == MeasureSpec.EXACTLY) {
            result = specSize;
        } else {
            result = 120;
            if (specMode == MeasureSpec.AT_MOST) {
                result = Math.min(result, specSize);
            }
        }
        return result;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Log.d(TAG, ">>>>>>> mWidth = " + mWidth + " ,mHeight = " + mHeight);
        //图标
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.icon_sunrise);
        int bitmapW = bitmap.getWidth();
        int bitmapH = bitmap.getHeight();
        Log.d(TAG, ">>>>>>> bitmapW = " + bitmapW);
        Log.d(TAG, ">>>>>>> bitmapH = " + bitmapH);
        //画半圆
        canvas.drawCircle(mWidth / 2, mHeight, mWidth / 2 - 10, mBgArcPaint);
        //画弧
        RectF rectF = new RectF(0, mHeight - (mWidth / 2), mWidth, 2 * mHeight);
        //canvas.drawRect(rectF, mBgArcPaint);
        canvas.drawArc(rectF, 180, mSweepValue, true, mPaint);
        //计算图标left、top
        float left = (float) (mWidth / 2 - Math.cos(mSweepValue * Math.PI / 180) * (mWidth / 2 - 10) - bitmapW / 2);
        float top = (float) (mHeight - Math.sin(mSweepValue * Math.PI / 180) * (mWidth / 2 - 10) - bitmapH / 2);
        //将图标添加到画布
        canvas.drawBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.icon_sunrise), left, top, mPaint);
    }


    public void setValue(float value) {
        this.mSweepValue = value;
    }
}
