package com.zsf.variousview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;


/**
 * Created by EWorld
 * 2019/8/5 0005
 */
public class ArcView extends View {
    private static final String TAG = "ArcView";
    public static final int STROKE_WIDTH = 26;
    private Paint mPaint;
    private Paint mBgArcPaint;

    private Paint mTestPaint;

    private int mHeight;
    private int mWidth;
    private float mSweepValue = 60;

    private static int COLOR = Color.WHITE;

    public ArcView(Context context) {
        this(context, null);
    }

    public ArcView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ArcView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        COLOR = context.getResources().getColor(R.color.color_BFC4C4);
        init();
    }

    private void init() {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setStrokeWidth(STROKE_WIDTH);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setColor(COLOR);
        mPaint.setStrokeCap(Paint.Cap.ROUND);

        mBgArcPaint = new Paint();
        mBgArcPaint.setAntiAlias(true);
        mBgArcPaint.setStrokeWidth(STROKE_WIDTH);
        mBgArcPaint.setStyle(Paint.Style.STROKE);
        mBgArcPaint.setColor(getResources().getColor(R.color.color_home_bg_area));
        mBgArcPaint.setStrokeCap(Paint.Cap.ROUND);

        mTestPaint = new Paint();
        mTestPaint.setStrokeWidth(1);
        mTestPaint.setStyle(Paint.Style.STROKE);
        mTestPaint.setColor(Color.RED);
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
        canvas.drawColor(Color.YELLOW);
        Log.d(TAG, ">>>>>>> mWidth = " + mWidth + " ,mHeight = " + mHeight);
        RectF rect = new RectF(STROKE_WIDTH / 2, STROKE_WIDTH / 2, mWidth - STROKE_WIDTH / 2, mHeight * 2 - 50);
        canvas.drawArc(rect, 180, 180, false, mBgArcPaint);
        canvas.drawArc(rect, 180, mSweepValue, false, mPaint);

    }


    public void setValue(float value) {
        this.mSweepValue = value;
    }
}
