package com.zsf.variousview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by eworld on 2016/12/29.
 */
@SuppressLint("AppCompatCustomView")
public class SparkTextView extends TextView {
    private int mViewWidth = 0;
    private Paint mPaint;
    private LinearGradient mLinearGradient;
    private Matrix mGradientMatrix;
    private int mTranslate = 0;

    public SparkTextView(Context context) {
        super(context);
    }

    public SparkTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    /**
     * 初始化控件
     */
    private void initView() {
    }

    public SparkTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        //调用setMeasuredDimension()方法存放视图的宽和高
        setMeasuredDimension(measureWidth(widthMeasureSpec), measureHeight(heightMeasureSpec));

    }

    private int measureHeight(int heightMeasureSpec) {
        int result = 0;
        //1.使用MeasureSpec类来获得模式和大小
        int specMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        //2.判断测量的模式，给出不同的值
        if (specMode == MeasureSpec.EXACTLY) {
            //如果是精确模式,具体值或match_parent情况下
            result = heightSize;
        } else if (specMode == MeasureSpec.AT_MOST) {
            //最大模式下，即wrap_content情况下
            result = 200;
            result = Math.min(result, heightSize);

        } else if (specMode == MeasureSpec.UNSPECIFIED) {
            //不指定大小
            result = 200;
        }
        return result;

    }

    private int measureWidth(int widthMeasureSpec) {
        int result = 0;
        int specMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        if (specMode == MeasureSpec.EXACTLY) {
            result = widthSize;
        } else if (specMode == MeasureSpec.AT_MOST) {
            result = 200;
            result = Math.min(result, widthSize);
        } else {

        }
        return result;


    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mGradientMatrix != null) {
            mTranslate += mViewWidth / 5;
            if (mTranslate > 2 * mViewWidth) {
                mTranslate = -mViewWidth;
            }
            mGradientMatrix.setTranslate(mTranslate, 0);
            mLinearGradient.setLocalMatrix(mGradientMatrix);
            postInvalidateDelayed(100);
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (mViewWidth == 0) {
            mViewWidth = getMeasuredWidth();
            if (mViewWidth > 0) {
                mPaint = getPaint();
                mLinearGradient = new LinearGradient
                        (0, 0, mViewWidth, 0, new int[]{Color.YELLOW, 0xffffffff, Color.RED},
                                null, Shader.TileMode.CLAMP);
                mPaint.setShader(mLinearGradient);
                mGradientMatrix = new Matrix();

            }
        }
    }
}
