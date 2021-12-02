package com.zsf.variousview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Created by EWorld
 * 2021/11/30
 */
public class PickerView extends View {
    private static final String TAG = "PickerView";
    private List<String> mDataList;
    private int mCurrentSelected;
    private float mMaxTextSize;
    private float mMinTextSize;
    private float mMaxTextAlpha = 250.0f;
    private float mMinTextAlpha = 100.0f;
    private boolean isInit;
    private float moveLen = 0.0f;
    private float baseline;
    private MyTimerTask mTask;
    private Timer timer;
    private float mLastDownX;
    private MyHandler mHandler;
    private int viewWidth;
    private int viewHeight;
    private Paint mPaint;


    public PickerView(Context context) {
        this(context, null);
    }

    public PickerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mHandler = new MyHandler();
        init();
    }

    private void init() {
        timer = new Timer();
        mDataList = initData();
        mPaint = new Paint();
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setTextAlign(Paint.Align.CENTER);
        mPaint.setColor(Color.RED);
    }

    /**
     * 模拟数据
     * @return
     */
    private List<String> initData() {
        List<String> list = new ArrayList();
        for (int i = 1; i <= 100; ++i) {
            list.add(i + "");
        }
        mCurrentSelected = list.size() / 2;
        return list;
    }

    public void setData(List<String> dataList) {
        this.mDataList = dataList;
        mCurrentSelected = dataList.size() / 2;
        invalidate();
    }

    private void performSelect() {
        if (mSelectListener != null) {
            mSelectListener.onSelect(mDataList.get(mCurrentSelected));
        }
    }

    /**
     * 自定义View如果需要使用wrap_content属性必须重写onMeasure方法
     *
     * @param widthMeasureSpec
     * @param heightMeasureSpec
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        viewWidth = getMeasuredWidth();
        viewHeight = getMeasuredHeight();
        Log.d(TAG, ">>>>>>> viewWidth = " + viewWidth);
        Log.d(TAG, ">>>>>>> viewHeight = " + viewHeight);
        mMaxTextSize = viewWidth / 12.0f;
        mMinTextSize = mMaxTextSize / 2.0f;
        this.isInit = true;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (isInit) {
            //画屏幕中间的数字
            float scale = parabola(viewWidth / 2.0f, moveLen);
            float size = (mMaxTextSize - mMinTextSize) * scale + mMinTextSize + 5.0f;
            mPaint.setTextSize(size);
            mPaint.setAlpha((int) ((mMaxTextAlpha - mMinTextAlpha) * scale + mMinTextAlpha));
            float x = (float) (viewWidth / 2.0 + moveLen);
            float y = (float) (viewHeight / 2.0);
            Paint.FontMetrics fontMetrics = mPaint.getFontMetrics();
            baseline = (float) (y - (fontMetrics.bottom / 2.0d + fontMetrics.top / 2.0d));
            canvas.drawText(mDataList.get(mCurrentSelected), x, baseline, mPaint);

            int i;
            for (i = 1; mCurrentSelected - i >= 0; ++i) {
                this.drawOtherText(canvas, i, -1);
            }
            for (i = 1; mCurrentSelected + i < mDataList.size(); ++i) {
                this.drawOtherText(canvas, i, 1);
            }
        }
    }

    /**
     * 画两侧数字
     * @param canvas
     * @param position
     * @param type
     */
    private void drawOtherText(Canvas canvas, int position, int type) {
        float d = 3.5f * mMinTextSize * position + type * moveLen;
        float scale = parabola(viewWidth / 2.0f, d);
        float size = (mMaxTextSize - mMinTextSize) * scale + mMinTextSize;
        mPaint.setTextSize(size);
        mPaint.setAlpha((int) ((mMaxTextAlpha - mMinTextAlpha) * scale));
        float x = (float) (viewWidth / 2.0d + (double) ((float) type * d));
        canvas.drawText((String) mDataList.get(mCurrentSelected + type * position), x, baseline, mPaint);
    }

    /**
     * 使用抛物线方程 计算缩放比
     *
     * @param zero
     * @param x
     * @return
     */
    private float parabola(float zero, float x) {
        float f = (float) (1.0d - Math.pow((double) (x / zero), 2.0d));
        return f < 0.0f ? 0.0f : f;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                if (mTask != null) {
                    mTask.cancel();
                    mTask = null;
                }
                mLastDownX = event.getX();
                break;
            case MotionEvent.ACTION_MOVE:
                moveLen += event.getX() - mLastDownX;
                Log.i(TAG, ">>>>>> moveLen = " + moveLen);
                if (moveLen > 3.5f * mMinTextSize / 2.0f) {
                    moveTailToHead();
                    moveLen -= 3.5f * mMinTextSize;
                } else if (moveLen < -3.5f * mMinTextSize / 2.0f) {
                    moveHeadToTail();
                    moveLen += 3.5f * mMinTextSize;
                }
                mLastDownX = event.getX();
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                if (Math.abs(moveLen) < 1.0E-4D) {
                    moveLen = 0.0f;
                } else {
                    if (mTask != null) {
                        mTask.cancel();
                        mTask = null;
                    }
                    mTask = new MyTimerTask(mHandler);
                    timer.schedule(mTask, 0L, 10L);
                }
                break;
        }
        return true;
    }

    private void moveTailToHead() {
        String head = mDataList.get(0);
        mDataList.remove(0);
        mDataList.add(head);
    }

    private void moveHeadToTail() {
        String tail = mDataList.get(mDataList.size() - 1);
        mDataList.remove(mDataList.size() - 1);
        mDataList.add(0, tail);
    }

    //------------------------------------------------------------------------------------------
    onSelectListener mSelectListener;

    public void setSelectListener(onSelectListener selectListener) {
        mSelectListener = selectListener;
    }

    public interface onSelectListener {
        void onSelect(String var1);
    }
    //-------------------------------------------------------------------------------------------

    class MyTimerTask extends TimerTask {
        Handler handler;

        public MyTimerTask(Handler handler) {
            this.handler = handler;
        }

        public void run() {
            this.handler.sendMessage(this.handler.obtainMessage());
        }
    }

    //--------------------------------------------------------------------------------------
    class MyHandler extends Handler {

        public MyHandler() {

        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            if (Math.abs(moveLen) < 3.0f) {
                moveLen = 0.0f;
                if (mTask != null) {
                    mTask.cancel();
                    mTask = null;
                    performSelect();
                }
            } else {
                moveLen = moveLen - moveLen / Math.abs(moveLen) * 3.0f;
            }
            invalidate();
        }
    }
}
