package com.zsf.variousview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.drawable.ClipDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

/**
 * Created by EWorld
 * 2021/9/7
 */
public class RankStarView extends FrameLayout {
    private static final String TAG = "RankStarView";
    private Drawable mForeground;
    private Drawable mBackground;
    private float mViewSpace = 10;
    private int count = 5;
    private float mStepSize;//步幅
    private float mRating;//
    private LinearLayout mLinearLayout2;

    private Context mContext;

    public RankStarView(Context context) {
        this(context, null);
    }

    public RankStarView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RankStarView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.RankStar);
        count = ta.getInteger(R.styleable.RankStar_starCount, 5);
        mViewSpace = ta.getDimension(R.styleable.RankStar_starSpace, 10);
        mForeground = ta.getDrawable(R.styleable.RankStar_foregroundSrc);
        mBackground = ta.getDrawable(R.styleable.RankStar_backgroundSrc);
        mStepSize = ta.getFloat(R.styleable.RankStar_stepSize, 1.0f);
        mRating = ta.getFloat(R.styleable.RankStar_rating, 3);
        ta.recycle();

        addImgView(context);
    }

    private void addImgView(Context context) {
        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(LinearLayout.HORIZONTAL);
        for (int j = 0; j < count; j++) {
            ImageView backView = new ImageView(context);
            backView.setBackground(mBackground);
            linearLayout.addView(backView);

            View spaceView = new View(context);
            spaceView.setLayoutParams(new LinearLayout.LayoutParams((int) mViewSpace, 20));
            spaceView.setBackgroundColor(getResources().getColor(android.R.color.transparent));
            linearLayout.addView(spaceView);
        }
        this.addView(linearLayout);

        mLinearLayout2 = new LinearLayout(context);
        mLinearLayout2.setOrientation(LinearLayout.HORIZONTAL);
        int number = (int) Math.floor(mRating);
        int number2 = (int) Math.ceil(mRating);
        for (int i = 0; i < number; i++) {
            ImageView foregroundView = new ImageView(context);
            foregroundView.setBackground(mForeground);
            mLinearLayout2.addView(foregroundView);
            View spaceView = new View(context);
            spaceView.setLayoutParams(new LinearLayout.LayoutParams((int) mViewSpace, 20));
            spaceView.setBackgroundColor(getResources().getColor(android.R.color.transparent));
            mLinearLayout2.addView(spaceView);
        }
        this.addView(mLinearLayout2);
        if (number2 > number) {
            drawHalfStar(mContext);//再加半颗星
        }
    }

    public void setRating(float rating) {
        this.mRating = rating;
        this.removeAllViews();
        addImgView(mContext);
    }

    private void drawHalfStar(Context context) {
        ClipDrawable drawable = new ClipDrawable(mForeground, Gravity.START, ClipDrawable.HORIZONTAL);
        drawable.setLevel(5000);
        ImageView imageView = new ImageView(context);
        imageView.setBackground(drawable);
        mLinearLayout2.addView(imageView);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

    }
}
