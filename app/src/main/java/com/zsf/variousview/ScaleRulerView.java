package com.zsf.variousview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.text.Layout;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.Scroller;

import androidx.annotation.Nullable;

/**
 * Created by EWorld
 *
 */
public class ScaleRulerView extends View {
    private static final String TAG = "ScaleRulerView";
    private static final int mDefaultSelectColor = Color.parseColor("#F7577F");
    private static final int mDefaultNormalLineColor = Color.parseColor("#E8E8E8");

    private VelocityTracker mVelocityTracker;
    private Scroller mScroller;

    private Paint mSelectPaint;
    private Paint mLinePaint;
    private int mNormalLineWidth;
    private int mNormalLineColor;
    private float mDensity;
    private int mWidth;
    private int mHeight;
    private int mMove;
    private int mLineDivider;
    private float mValue;
    private float mMaxValue;
    private int mModType;
    private float mDefaultMinValue;
    private int mSelectWidth;
    private int mSelectColor;
    private int mLastX;
    private int mMinVelocity;


    public ScaleRulerView(Context context) {
        this(context, null);
    }

    public ScaleRulerView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ScaleRulerView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.ScaleRulerView);
        mSelectColor = ta.getColor(R.styleable.ScaleRulerView_color_select_paint, mDefaultSelectColor);
        mNormalLineColor = ta.getColor(R.styleable.ScaleRulerView_color_normal_line, mDefaultNormalLineColor);
        ta.recycle();

        mLinePaint = new Paint();
        mSelectPaint = new Paint();
        mValue = 50.0f;
        mMaxValue = 100.0f;
        mDefaultMinValue = 0.0f;
        mModType = 5;
        mLineDivider = 12;
        mSelectWidth = 8;
        mNormalLineWidth = 4;
         /*
        需要让它有惯性的效果，就算手指松开，它也能自己滑动一段距离
         */
        mScroller = new Scroller(context);
        mDensity = context.getResources().getDisplayMetrics().density;
        //获得允许执行一个fling手势动作的最小速度值
        mMinVelocity = ViewConfiguration.get(context).getScaledMinimumFlingVelocity();

    }


    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        //视图宽高
        mWidth = getWidth();
        mHeight = getHeight();
        Log.d(TAG, ">>>>>> width = " + mWidth);
        Log.d(TAG, ">>>>>> height = " + mHeight);
        Log.d(TAG, ">>>>>> left = " + left);
        Log.d(TAG, ">>>>>> top = " + top);
        Log.d(TAG, ">>>>>> right = " + right);
        Log.d(TAG, ">>>>>> bottom = " + bottom);
        Log.d(TAG, ">>>>>> right - left = " + (right - left));
        Log.d(TAG, ">>>>>> bottom - top = " + (bottom - top));
        Log.d(TAG, "####################################################################");
        super.onLayout(changed, left, top, right, bottom);


    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawScaleLine(canvas);
        drawMiddleLine(canvas);
    }

    /**
     * @param canvas
     */
    private void drawScaleLine(Canvas canvas) {
        canvas.save();
        mLinePaint.setStrokeWidth(mNormalLineWidth);
        mLinePaint.setColor(mNormalLineColor);
        TextPaint textPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setTextSize(18.0f * mDensity);
        float textWidth = Layout.getDesiredWidth("0", textPaint);
        int width = mWidth;
        int drawCount = 0;
        for (int i = 0; drawCount <= (4 * width); ++i) {
            float xPosition = (width / 2 - mMove) + (i * mLineDivider) * mDensity;
            if (xPosition + getPaddingRight() < mWidth && mValue + i <= mMaxValue) {
                if ((mValue + i) % mModType == 0.0f) {
                    canvas.drawLine(xPosition, getHeight(), xPosition, getHeight() - mDensity * 36.0f, mLinePaint);
                } else {
                    canvas.drawLine(xPosition, getHeight(), xPosition, getHeight() - mDensity * 25.0f, mLinePaint);
                }
            }

            xPosition = (width / 2 - mMove) - (i * mLineDivider) * mDensity;
            if (xPosition > getPaddingLeft() && mValue - i >= mDefaultMinValue) {
                if ((mValue - i) % mModType == 0.0f) {
                    canvas.drawLine(xPosition, getHeight(), xPosition, getHeight() - mDensity * 36.0f, mLinePaint);
                } else {
                    canvas.drawLine(xPosition, getHeight(), xPosition, getHeight() - mDensity * 25.0f, mLinePaint);
                }
            }
            drawCount = (int) (drawCount + (2 * mLineDivider * mDensity));
        }
        canvas.restore();
    }

    /**
     *
     * @param canvas
     */
    private void drawMiddleLine(Canvas canvas) {
        canvas.save();
        mSelectPaint.setStrokeWidth(mSelectWidth);
        mSelectPaint.setColor(mSelectColor);
        canvas.drawLine(mWidth / 2, 0.0f, mWidth / 2, mHeight, mSelectPaint);
        canvas.restore();
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        int xPos = (int) event.getX();
        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain();
        }
        mVelocityTracker.addMovement(event);
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mScroller.forceFinished(true);
                mLastX = xPos;
                mMove = 0;
                break;
            case MotionEvent.ACTION_MOVE:
                mMove += mLastX - xPos;
                changeMoveAndValue();
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                countMoveEnd();
                countVelocityTracker(event);
                return false;
        }
        mLastX = xPos;
        return true;
    }

    /**
     *
     */
    private void changeMoveAndValue() {
        int value = (int) (mMove / (mLineDivider * mDensity));
        if (Math.abs(value) > 0) {
            mValue += value;
            mMove = (int) (mMove - (value * mLineDivider) * mDensity);
            if (mValue <= mDefaultMinValue || mValue > mMaxValue) {
                mValue = mValue <= mDefaultMinValue ? mDefaultMinValue : mMaxValue;
                mMove = 0;
                mScroller.forceFinished(true);
            }
            notifyValueChange();
        }
        postInvalidate();
    }

    /**
     *
     */
    private void notifyValueChange() {
        if (null != mChangeListener && mModType == 5) {
            mChangeListener.onValueChange(this.mValue);
        }
    }

    /**
     *
     */
    private void countMoveEnd() {
        int roundMove = Math.round(mMove / mLineDivider * mDensity);
        mValue += roundMove;
        mValue = mValue <= 0.0f ? 0.0f : mValue;
        mValue = mValue > mMaxValue ? mMaxValue : mValue;
        mLastX = 0;
        mMove = 0;
        notifyValueChange();
        postInvalidate();
    }

    /**
     * @param event
     */
    private void countVelocityTracker(MotionEvent event) {
        mVelocityTracker.computeCurrentVelocity(1000);
        float xVelocity = mVelocityTracker.getXVelocity();
        if (Math.abs(xVelocity) > mMinVelocity) {
            mScroller.fling(0, 0, (int) xVelocity, 0, -2147483648, 2147483647, 0, 0);
        }
    }

    //------------------------------------------------------------------------------------
    private OnValueChangeListener mChangeListener;

    public void setChangeListener(OnValueChangeListener changeListener) {
        mChangeListener = changeListener;
    }

    public interface OnValueChangeListener {
        void onValueChange(float value);
    }
}
