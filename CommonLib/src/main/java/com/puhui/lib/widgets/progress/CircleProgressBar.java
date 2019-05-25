package com.puhui.lib.widgets.progress;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.RectF;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.widget.ProgressBar;

import com.puhui.lib.R;

/**
 * Copyright: 人人普惠
 * Created by TangJian on 2017/5/9.
 * Description:
 * Modified By:
 */

public class CircleProgressBar extends ProgressBar {
    public static final int STYLE_TICK = 1;
    public static final int STYLE_ARC = 0;
    private int mStyleProgress = STYLE_TICK;
    private boolean mBgShow;
    private float mRadius;  //圆的半径
    private int mArcBgColor;
    private int mBoardWidth;
    private int mDegree;  //首尾未连接到的空白地方
    private RectF mArcRectf;
    private Paint mLinePaint;
    private Paint mArcPaint;
    private int mUnmProgressColor;
    private int mProgressColor;
    private int mTickWidth;
    private int mTickDensity;
    private Bitmap mCenterBitmap;
    private Canvas mCenterCanvas;
    private OnCenterDraw mOnCenter;

    public CircleProgressBar(Context context) {
        this(context, null);
    }

    public CircleProgressBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CircleProgressBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        final TypedArray attributes = getContext().obtainStyledAttributes(attrs, R.styleable.CircleProgressBar);
        if (null != attributes) {
            mBoardWidth = attributes.getDimensionPixelOffset(R.styleable.CircleProgressBar_borderWidth, dp2px(10));
            mUnmProgressColor = attributes.getColor(R.styleable.CircleProgressBar_unprogresColor, 0xffeaeaea);
            mProgressColor = attributes.getColor(R.styleable.CircleProgressBar_progressColor, Color.YELLOW);
            mTickWidth = attributes.getDimensionPixelOffset(R.styleable.CircleProgressBar_tickWidth, dp2px(2));
            mTickDensity = attributes.getInt(R.styleable.CircleProgressBar_tickDensity, 4);
            mTickDensity = Math.max(Math.min(mTickDensity, 8), 2);
            mRadius = attributes.getDimensionPixelOffset(R.styleable.CircleProgressBar_radius, dp2px(72));
            mArcBgColor = attributes.getColor(R.styleable.CircleProgressBar_arcbgColor, 0xffeaeaea);
            mBgShow = attributes.getBoolean(R.styleable.CircleProgressBar_bgShow, false);
            mDegree = attributes.getInt(R.styleable.CircleProgressBar_degree, 0);
            mStyleProgress = attributes.getInt(R.styleable.CircleProgressBar_progressStyle, STYLE_TICK);
            boolean capRount = attributes.getBoolean(R.styleable.CircleProgressBar_arcCapRound, false);
            mArcPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            mArcPaint.setColor(mArcBgColor);
            if (capRount)
                mArcPaint.setStrokeCap(Paint.Cap.ROUND);
            mArcPaint.setStrokeWidth(mBoardWidth);
            mArcPaint.setStyle(Paint.Style.STROKE);
            mLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            mLinePaint.setStrokeWidth(mTickWidth);
            attributes.recycle();
        }
    }

    public void setOnCenterDraw(OnCenterDraw mOnCenter) {
        this.mOnCenter = mOnCenter;
    }

    @Override
    protected synchronized void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected synchronized void onDraw(Canvas canvas) {
        canvas.save();
        float rotate = currentProgress * 1.0f / getMax();
        float x = mArcRectf.right / 2 + mBoardWidth / 2;
        float y = mArcRectf.right / 2 + mBoardWidth / 2;
        if (mOnCenter != null) {
            if (mCenterCanvas == null) {
                mCenterBitmap = Bitmap.createBitmap((int) mRadius * 2, (int) mRadius * 2, Bitmap.Config.ARGB_8888);
                mCenterCanvas = new Canvas(mCenterBitmap);
            }
            mCenterCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
            mOnCenter.draw(mCenterCanvas, mArcRectf, x, y, mBoardWidth, currentProgress);
            canvas.drawBitmap(mCenterBitmap, 0, 0, null);
        }
        int angle = mDegree / 2;
        if (mStyleProgress == STYLE_ARC) {
            Log.e(this.getClass().getSimpleName(), "rotate = " + rotate + "");
            float targetmDegree = (360 - mDegree) * rotate;
            //绘制未完成部分
            mArcPaint.setColor(mUnmProgressColor);
            canvas.drawArc(mArcRectf, angle + targetmDegree, 360 - mDegree - targetmDegree, false, mArcPaint);
            //绘制完成部分
            mArcPaint.setColor(mProgressColor);
            canvas.drawArc(mArcRectf, angle, targetmDegree, false, mArcPaint);
        } else {
            int count = (360 - mDegree) / mTickDensity;
            int target = (int) (rotate * count);
            if (mBgShow)
                canvas.drawArc(mArcRectf, angle, 360 - mDegree, false, mArcPaint);
            canvas.rotate(180 + angle, x, y);
            for (int i = 0; i < count; i++) {
                if (i < target) {
                    mLinePaint.setColor(mProgressColor);
                } else {
                    mLinePaint.setColor(mUnmProgressColor);
                }
                canvas.drawLine(x, mBoardWidth + mBoardWidth / 2, x, mBoardWidth - mBoardWidth / 2, mLinePaint);
                canvas.rotate(mTickDensity, x, y);
            }
        }
        canvas.restore();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mArcRectf = new RectF(mBoardWidth,
                mBoardWidth,
                mRadius * 2 - mBoardWidth,
                mRadius * 2 - mBoardWidth);
    }

    /***
     * 设置圆的半径，即确定了圆的大小
     *
     * @param radius
     */
    public void setRadius(float radius) {
        this.mRadius = radius;
    }

    /**
     * dp 2 px
     *
     * @param dpVal
     */
    protected int dp2px(int dpVal) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                dpVal, getResources().getDisplayMetrics());
    }

    public interface OnCenterDraw {
        /**
         * @param canvas
         * @param rectF       圆弧的Rect
         * @param x           圆弧的中心x
         * @param y           圆弧的中心y
         * @param strokeWidth 圆弧的边框宽度
         * @param progress    当前进度
         */
        void draw(Canvas canvas, RectF rectF, float x, float y, float strokeWidth, float progress);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (mCenterBitmap != null) {
            mCenterBitmap.recycle();
            mCenterBitmap = null;
        }
    }

    /***
     * 当前进度
     */
    private float currentProgress = 0;
    private int maxProgress = 100;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            setProgress((int) currentProgress);
            if (currentProgress >= maxProgress) {
                handler.removeMessages(0);
                return;
            }
            currentProgress += 0.1;
            sendEmptyMessageDelayed(0, 5000 / (maxProgress * 10));
        }
    };

    /**
     * 设置进度
     *
     * @param progress
     */
    public void setCircleProgress(int progress) {
        maxProgress = progress;
        if (maxProgress < 0) {
            maxProgress = 0;
        }
        if (maxProgress > 100) {
            maxProgress = 100;
        }

        currentProgress = 0;
        handler.removeMessages(0);
        handler.sendEmptyMessage(0);
    }
}
