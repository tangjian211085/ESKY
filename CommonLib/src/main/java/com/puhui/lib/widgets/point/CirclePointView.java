package com.puhui.lib.widgets.point;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import com.puhui.lib.R;

/**
 * Copyright: 人人普惠
 * Created by TangJian on 2017/5/12.
 * Description:
 * Modified By:
 */

public class CirclePointView extends View {
    /**
     * 画笔对象的引用
     */
    private Paint paint;
    private int radius = 10;
    private int paintColor = Color.RED;

    public CirclePointView(Context context) {
        super(context);
        init(null);
    }

    public CirclePointView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public CirclePointView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        if (null != attrs) {
            TypedArray mTypedArray = getContext().obtainStyledAttributes(attrs, R.styleable.PointViewStyle);
            // 获取自定义属性和默认值
            radius = (int) (mTypedArray.getDimension(R.styleable.PointViewStyle_pointRadius, getScale() * 5) / getScale()) * 2;
            paintColor = mTypedArray.getColor(R.styleable.PointViewStyle_paintColor, Color.RED);
            mTypedArray.recycle();
        }
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        paint.setAntiAlias(true); // 消除锯齿
        paint.setColor(paintColor);
    }

    /***
     * case ViewGroup.LayoutParams.MATCH_PARENT:
     * // Window can't resize. Force root view to be windowSize.
     * measureSpec = MeasureSpec.makeMeasureSpec(windowSize, MeasureSpec.EXACTLY);
     * break;
     * <p>
     * case ViewGroup.LayoutParams.WRAP_CONTENT:
     * // Window can resize. Set max size for root view.
     * measureSpec = MeasureSpec.makeMeasureSpec(windowSize, MeasureSpec.AT_MOST);
     * break;
     * <p>
     * default:
     * // Window wants to be an exact size. Force root view to be that size.
     * measureSpec = MeasureSpec.makeMeasureSpec(rootDimension, MeasureSpec.EXACTLY);
     * break;
     *
     * @param widthMeasureSpec
     * @param heightMeasureSpec
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int computedWidth = resolveMeasured(widthMeasureSpec, radius * 2);
        int computedHeight = resolveMeasured(heightMeasureSpec, radius * 2);

        setMeasuredDimension(computedWidth, computedHeight);
    }

    private int resolveMeasured(int measureSpec, int desired) {
        int result = 0;
        int specSize = MeasureSpec.getSize(measureSpec);
        switch (MeasureSpec.getMode(measureSpec)) {
            case MeasureSpec.UNSPECIFIED:
                result = desired;
                break;
            case MeasureSpec.AT_MOST:
                result = Math.min(specSize, desired);
                break;
            case MeasureSpec.EXACTLY:
            default:
                result = specSize;
        }
        return result;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
//        int center = getWidth() / 2; // 获取圆心的x坐标
//        paint.setStrokeWidth(center); // 设置圆环的宽度
//        RectF mOval = new RectF();
//        mOval.set(center, center, center, center);// 用于定义的圆弧的形状和大小的界限
////        canvas.drawArc(mOval, -210, 240, false, paint); // 画出圆环
//        canvas.drawArc(mOval, 0, 360, false, paint); // 画出圆环

        int centerX = getWidth() / 2; // 获取圆心的x坐标
        int centerY = getMeasuredHeight() / 2;
        canvas.drawCircle(centerX, centerY, radius, paint);
    }

    private float getScale() {
        return getContext().getResources().getDisplayMetrics().density;
    }
}
