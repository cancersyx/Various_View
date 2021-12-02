package com.zsf.variousview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

/**
 * @author EWorld
 * 2019/6/12
 */
public class GuaGuaView extends View {
    private static final String TAG = "GuaGuaView";
    private Paint mOuterPaint;
    private Path mPath;
    private Canvas mCanvas;//
    private Bitmap mBitmap;

    private int mLastX;//记录X坐标
    private int mLastY;

    private Bitmap mOutterBitmap;

    private Bitmap mBottomBitmap;//底部的图片

    private String mText;//刮奖的文本信息
    private Paint mBackPaint;//
    private Rect mTextBound;//记录刮奖信息文本的宽和高
    private int mTextSize;

    private volatile boolean mComplete = false;//判断遮盖层区域是否消除达到阈值

    /**
     * 刮刮卡刮完的回调
     */
    public interface OnGuaGuaKaCompleteListener {
        void complete();
    }

    private OnGuaGuaKaCompleteListener mGuaGuaKaCompleteListener;

    public void setGuaGuaKaCompleteListener(OnGuaGuaKaCompleteListener guaGuaKaCompleteListener) {
        mGuaGuaKaCompleteListener = guaGuaKaCompleteListener;
    }

    public GuaGuaView(Context context) {
        this(context, null);
    }

    public GuaGuaView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public GuaGuaView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.GuaGuaKa, defStyleAttr, 0);


        ta.recycle();

        init();
    }

    private void init() {
        mOuterPaint = new Paint();
        mPath = new Path();
        //Bitmap需要得到当前控件的宽和高以后创建

        //mBottomBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.img_ym);
        mOutterBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.img_guaguale);

        //中奖信息
        mText = "谢谢惠顾";
        mTextBound = new Rect();
        mBackPaint = new Paint();
        mTextSize = 30;
    }

    public void setText(String text){
        this.mText = text;
        //获得当前画笔绘制的宽和高
        mBackPaint.getTextBounds(mText, 0, mText.length(), mTextBound);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        //得到控件的宽和高
        int width = getMeasuredWidth();
        int height = getMeasuredHeight();

        //得到控件宽和高以后初始化bitmap
        mBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        //有了Bitmap，初始化canvas,因为canvas在bitmap上
        mCanvas = new Canvas(mBitmap);

        //设置绘制path画笔属性
        setupOutPaint();
        //设置中将信息画笔属性
        setupBackPaint();

        //mCanvas.drawColor(Color.parseColor("#c0c0c0"));
        //mCanvas.drawColor(Color.BLACK);
        mCanvas.drawRoundRect(new RectF(0, 0, width, height), 30, 30, mOuterPaint);


        //绘制图片
        mCanvas.drawBitmap(mOutterBitmap, null, new Rect(0, 0, width, height), null);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        int action = event.getAction();

        int x = (int) event.getX();
        int y = (int) event.getY();
        //Log.d(TAG, ">>>>> x = " + x);
        //Log.d(TAG, ">>>>> y = " + y);

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mLastX = x;
                mLastY = y;

                mPath.moveTo(mLastX, mLastY);
                break;
            case MotionEvent.ACTION_MOVE:

                int dx = Math.abs(x - mLastX);//得到横向距离
                int dy = Math.abs(y - mLastY);

                if (dx > 3 || dy > 3) {
                    mPath.lineTo(x, y);
                }

                //更新LastX,LastY
                mLastX = x;
                mLastY = y;

                break;
            case MotionEvent.ACTION_UP:
                new Thread(mRunnable).start();
                break;
        }

        invalidate();
        return true;

    }

    private Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            int w = getWidth();
            int h = getHeight();

            float wipeArea = 0;
            float totalArea = w * h;

            Bitmap bitmap = mBitmap;//图层区域绘制到bitmap上
            int[] mPixels = new int[w * h];
            //获得Bitmap上所有像素信息
            bitmap.getPixels(mPixels, 0, w, 0, 0, w, h);

            for (int i = 0; i < w; i++) {
                for (int j = 0; j < h; j++) {
                    //统计用户擦除
                    int index = i + j * w;

                    try {
                        if (mPixels[index] == 0) {
                            wipeArea++;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            }

            //获得百分比
            if (wipeArea > 0 && totalArea > 0) {
                int percent = (int) (wipeArea * 100 / totalArea);
                Log.d(TAG, "percent = " + percent);

                if (percent > 60) {
                    //清除掉图层 区域
                    mComplete = true;
                    postInvalidate();//子线程，所以使用该方法
                }
            }
        }
    };

    @Override
    protected void onDraw(Canvas canvas) {
        //底部的图片
        //canvas.drawBitmap(mBottomBitmap,0,0,null);

        canvas.drawText(mText, getWidth() / 2 - mTextBound.width() / 2,
                getHeight() / 2 + mTextBound.height() / 2, mBackPaint);

        if (mComplete) {
            if (mGuaGuaKaCompleteListener != null) {
                mGuaGuaKaCompleteListener.complete();
            }
        }

        if (!mComplete) {
            drawPath();
            canvas.drawBitmap(mBitmap, 0, 0, null);//注意这里传入的null值
        }


    }

    private void drawPath() {
        mOuterPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OUT));


        mCanvas.drawPath(mPath, mOuterPaint);
    }

    /**
     * 设置绘制path画笔属性
     */
    private void setupOutPaint() {
        mOuterPaint.setColor(Color.parseColor("#c0c0c0"));
        mOuterPaint.setAntiAlias(true);
        mOuterPaint.setDither(true);
        mOuterPaint.setStrokeJoin(Paint.Join.ROUND);
        mOuterPaint.setStrokeCap(Paint.Cap.ROUND);
        mOuterPaint.setStyle(Paint.Style.FILL);
        mOuterPaint.setStrokeWidth(20);
    }

    /**
     * 设置获奖信息的画笔属性
     */
    private void setupBackPaint() {
        mBackPaint.setColor(Color.BLACK);
        mBackPaint.setStyle(Paint.Style.FILL);
        mBackPaint.setTextSize(mTextSize);
        //获得当前画笔绘制的宽和高
        mBackPaint.getTextBounds(mText, 0, mText.length(), mTextBound);

    }
}
