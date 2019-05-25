package com.puhui.lib.widgets.progress;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.View;

import com.puhui.lib.utils.DMLog;
import com.puhui.lib.utils.DensityUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Copyright: 人人普惠
 * Created by TangJian on 2017/5/9.
 * Description:  环形进度
 * Modified By:
 */

public class MultiColorCircleProgressBar extends View {
    /**
     * 画笔对象的引用
     */
    private Paint paint;

    /**
     * 圆环的颜色
     */
    private int roundColor = 0xffe5e5e5;

    /**
     * 圆环前景的宽度
     */
    private float fgRoundWidth = DensityUtil.dip2px(getContext(), 25);

    /**
     * 圆环背景的宽度
     */
    private float bgRoundWidth = DensityUtil.dip2px(getContext(), 24);

    /**
     * 最大进度
     */
    private int maxProgress = 100;

    /**
     * 当前进度
     */
    private float progress;

    public static final int STROKE = 0;

    public static final int FILL = 1;

    public MultiColorCircleProgressBar(Context context) {
        this(context, null);
    }

    public MultiColorCircleProgressBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MultiColorCircleProgressBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private RectF mOval;

    /**
     * 初始化操作
     */
    private void init() {
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mOval = new RectF();
        colorList.add(0xffe35260);
        colorList.add(0xff96d67c);
        colorList.add(0xfff5ba63);
        colorList.add(0xff19d6b1);
        colorList.add(0xff1ab1e2);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        /**
         * 画最外层的大圆环
         */
        float tempWidth = bgRoundWidth - fgRoundWidth > 0 ? bgRoundWidth : fgRoundWidth;
        int center = getWidth() / 2; // 获取圆心的x坐标
        int radius = (int) (center - tempWidth / 2); // 圆环的半径
        paint.setColor(roundColor); // 设置圆环的颜色
        paint.setStyle(Paint.Style.STROKE); // 设置空心
        paint.setStrokeWidth(bgRoundWidth); // 设置圆环的宽度
        paint.setAntiAlias(true); // 消除锯齿

        mOval.set(center - radius, center - radius, center + radius, center + radius);// 用于定义的圆弧的形状和大小的界限
//        canvas.drawArc(mOval, -210, 240, false, paint); // // 画出圆环
        canvas.drawArc(mOval, 0, 360, false, paint); // // 画出圆环

        DMLog.e("*********", "***********" + progress + "***********");
        /**
         * 画圆弧 ，画圆环的进度
         */
        // 设置进度是实心还是空心
        paint.setStrokeWidth(fgRoundWidth); // 设置圆环的宽度
        mOval.set(center - radius, center - radius, center + radius, center + radius);// 用于定义的圆弧的形状和大小的界限
        if (null != progressList) {
            if (!isShowAnimate) {
                int startAngle = 0;
                for (int i = 0; i < progressList.size(); i++) {
                    int sweepAngle = 360 * progressList.get(i) / maxProgress - startAngle;
                    paint.setColor(colorList.get(i)); // 设置进度的颜色
                    canvas.drawArc(mOval, startAngle - i, i == 4 ? sweepAngle + 4 : sweepAngle, false, paint); // 根据进度画圆弧
                    startAngle = startAngle + sweepAngle;
                }
            } else {
                if (progressList.size() >= 5) {
                    //startAngle - n 是为了保证不同颜色的圆环衔接看起来正常，不会有细缝的现象
                    for (int i = 0; i < progressList.size(); i++) {
                        if (progress <= progressList.get(i)) {
                            for (int j = 0; j <= i; j++) {
                                float startAngle;
                                float sweepAngle;
                                if (j == i) {
                                    paint.setColor(colorList.get(j));
                                    if (j == 0) {
                                        startAngle = 0;
                                        sweepAngle = 360 * progress / maxProgress - startAngle;
                                    } else {
                                        startAngle = 360 * progressList.get(j - 1) / maxProgress;
                                        sweepAngle = 360 * progress / maxProgress - startAngle;
                                    }
                                    canvas.drawArc(mOval, startAngle - 1, sweepAngle + 1, false, paint);
                                } else {
                                    paint.setColor(colorList.get(j));
                                    startAngle = j == 0 ? 0 : 360 * progressList.get(j - 1) / maxProgress;
                                    sweepAngle = 360 * progressList.get(j) / maxProgress - startAngle;
                                    canvas.drawArc(mOval, startAngle - 1, sweepAngle + 1, false, paint);
                                }
                            }
                            return;
                        }
                    }
                }
            }
        } else {
            canvas.drawArc(mOval, 0, 360 * progress / maxProgress, false, paint); // 根据进度画圆弧
        }

    }

    private boolean isShowAnimate = false;  //是否动画展示
    private List<Integer> progressList;
    private List<Integer> colorList = new ArrayList<>();
    private boolean isAnimateFinished = true;  //动画效果是否执行完毕

    /**
     * 设置进度，此为线程安全控件，由于考虑多线的问题，需要同步刷新界面调用postInvalidate()能在非UI线程刷新
     */
    public synchronized void showView(List<Integer> progressList, boolean isShowAnimate) {
        this.progressList = progressList;
        if (isShowAnimate) {
            this.isShowAnimate = true;
            this.progress = 0;
            handler.removeMessages(0);
            handler.sendEmptyMessage(0);
        } else {
            this.progress = maxProgress;
            this.isShowAnimate = false;
            postInvalidate();
        }
    }

    public synchronized void finish() {
        if (null != handler && !isAnimateFinished) {
            isAnimateFinished = false;
            handler.removeMessages(0);
        }
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (progress == maxProgress) {
                handler.removeMessages(0);
                isAnimateFinished = true;
                return;
            }
            isAnimateFinished = false;
            progress += 0.5;
            if (progress >= maxProgress) {
                progress = maxProgress;
            }
            invalidate();
            sendEmptyMessageDelayed(0, 300 / maxProgress);
        }
    };

}
