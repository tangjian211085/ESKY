package com.puhui.lib.widgets.progress;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.SweepGradient;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;

import com.puhui.lib.R;
import com.puhui.lib.utils.DensityUtil;

/**
 * Copyright: 人人普惠
 * Created by TangJian on 2017/5/9.
 * Description:  环形进度
 * Modified By:
 */

public class FanProgressBar extends View {
    /**
     * 画笔对象的引用
     */
    private Paint circlePaint;  //底部圆环画笔
    private Paint arcPaint;  //上部圆弧画笔
    private Paint topCirclePoint;  //顶部圆点
    private Path mPath;
    private boolean capRound;  //画笔形状
    private int roundColor;  //圆环的颜色
    private int roundProgressColor;  //圆弧进度的颜色
    private float roundWidth;  //圆环的宽度
    private int maxProgress = 100;  //最大进度
    private float progress;  //当前进度
    private int style = STROKE;  //进度的风格，实心或者空心
    public static final int STROKE = 0;
    public static final int FILL = 1;

    public FanProgressBar(Context context) {
        this(context, null);
    }

    public FanProgressBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FanProgressBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        TypedArray mTypedArray = context.obtainStyledAttributes(attrs, R.styleable.FanProgressBar);
        // 获取自定义属性和默认值
        roundColor = mTypedArray.getColor(R.styleable.FanProgressBar_bgColor, Color.RED);
        roundProgressColor = mTypedArray.getColor(R.styleable.FanProgressBar_fgColor, Color.GREEN);
        roundWidth = mTypedArray.getDimension(R.styleable.FanProgressBar_fanRoundWidth, DensityUtil.dip2px(getContext(), 15));
        progress = mTypedArray.getInt(R.styleable.FanProgressBar_fanProgress, 0);
        capRound = mTypedArray.getBoolean(R.styleable.FanProgressBar_capRound, true);

        mTypedArray.recycle();
    }

    private RectF mOval;
    private SweepGradient sweepGradient;
    private static final int[] colors = new int[]{Color.GREEN, Color.BLUE, Color.RED};

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        float size = Math.min(w, h);
        mOval = new RectF(roundWidth / 2, roundWidth / 2, size - roundWidth / 2, size - roundWidth / 2);
        init();
    }

    /**
     * 初始化操作
     */
    private void init() {
        circlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        circlePaint.setColor(roundColor); // 设置圆环的颜色
        circlePaint.setStyle(Paint.Style.STROKE); // 设置空心
        circlePaint.setStrokeWidth(roundWidth); // 设置圆环的宽度
        circlePaint.setAntiAlias(true); // 消除锯齿
        circlePaint.setStrokeCap(capRound ? Paint.Cap.ROUND : Paint.Cap.SQUARE);


        arcPaint = new Paint();
        arcPaint.setColor(roundProgressColor); // 设置圆环的颜色
        arcPaint.setStyle(Paint.Style.STROKE); // 设置空心
        arcPaint.setStrokeWidth(roundWidth); // 设置圆环的宽度
        arcPaint.setAntiAlias(true); // 消除锯齿
        arcPaint.setStrokeCap(capRound ? Paint.Cap.ROUND : Paint.Cap.SQUARE);

//      参数一为渐变起初点坐标x位置，参数二为y轴位置，参数三和四分别对应渐变终点，最后参数为平铺方式，这里设置为镜像
//      Gradient是基于Shader类，所以我们通过Paint的setShader方法来设置这个渐变
        float[] position = new float[]{0.167f, 0.33f, 0.5f};
//        LinearGradient lg = new LinearGradient(200, 0, 200, 400, colors, null, Shader.TileMode.MIRROR);
//        arcPaint.setShader(lg);
//        sweepGradient = new SweepGradient(mOval.centerX(), mOval.centerY(), colors, position);
//        arcPaint.setShader(sweepGradient);

        mPath = new Path();

        topCirclePoint = new Paint();
        topCirclePoint.setColor(ContextCompat.getColor(getContext(), R.color.white)); // 设置圆环的颜色
        topCirclePoint.setStyle(Paint.Style.FILL_AND_STROKE);
        topCirclePoint.setAntiAlias(true); // 消除锯齿
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.save();
        canvas.drawArc(mOval, 0, 360, false, circlePaint); // 画出圆环

        //画圆弧 ，画圆环的进度
//        arcPaint.setColor(roundProgressColor); // 设置进度的颜色
//        arcPaint.setShader(sweepGradient);
        switch (style) {
            case STROKE: {
//                Paint p = new Paint();
//                mPath.reset();
//                mPath.addArc(mOval, -90, 360 * progress / maxProgress);
//                canvas.drawPath(mPath, arcPaint);
                canvas.drawArc(mOval, -90, 360 * progress / maxProgress, false, arcPaint); // 根据进度画圆弧
                break;
            }
            case FILL: {
                arcPaint.setStyle(Paint.Style.FILL_AND_STROKE);
                if (progress != 0)
                    canvas.drawArc(mOval, 0, 360 * progress / maxProgress, true, arcPaint); // 根据进度画圆弧
                break;
            }
        }

        canvas.drawCircle(mOval.centerX(), roundWidth / 2, DensityUtil.dip2px(getContext(), 4), topCirclePoint);
        canvas.restore();
    }

    /**
     * 获取进度.需要同步
     */
    public synchronized float getProgress() {
        return progress;
    }

    /**
     * 设置进度，此为线程安全控件，由于考虑多线的问题，需要同步 刷新界面调用postInvalidate()能在非UI线程刷新
     */
    public synchronized void setProgress(int progress) {
        if (progress < 0) {
            progress = 0;
        }
        if (progress > maxProgress) {
            progress = maxProgress;
        }
        setAnimation(progress);
    }

    /**
     * 为进度设置动画
     */
    private void setAnimation(float progress) {
        ValueAnimator progressAnimator = ValueAnimator.ofFloat(0, progress);
        progressAnimator.setDuration(2000);
        progressAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                FanProgressBar.this.progress = (float) animation.getAnimatedValue();
                invalidate();
            }
        });
        progressAnimator.start();
    }
}
