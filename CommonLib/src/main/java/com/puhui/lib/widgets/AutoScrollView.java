package com.puhui.lib.widgets;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.puhui.lib.R;

import java.util.List;


public class AutoScrollView extends ViewGroup {

    private List<String> noticeDatas;

    private int picId;

    private Context context;

    private boolean isScroll;

    private int defaultDuration = 600;

    private OnAutoScrollViewClickListener onAutoScrollViewClickListener;

    public void setDefaultDuration(int defaultDuration) {
        this.defaultDuration = defaultDuration;
    }

    @SuppressLint("HandlerLeak")
    private Handler hanlder = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            if (!isScroll)
                return;
            if (preView == null || nextView == null)
                return;
            ObjectAnimator preAnimator = ObjectAnimator.ofFloat(preView, "translationY", 0, -preView.getMeasuredHeight());
            preAnimator.setDuration(defaultDuration);
            preAnimator.setRepeatCount(0);
            preAnimator.start();
            ObjectAnimator nextAnimator = ObjectAnimator.ofFloat(nextView, "translationY", 0, -nextView.getMeasuredHeight());
            nextAnimator.setDuration(defaultDuration);
            nextAnimator.setRepeatCount(0);
            nextAnimator.setStartDelay((int) (defaultDuration * 0.02));
            nextAnimator.start();

            nextAnimator.addListener(new AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animator) {
                }

                @Override
                public void onAnimationRepeat(Animator animator) {
                }

                @Override
                public void onAnimationEnd(Animator animator) {
                    startDatas = startDatas + 1;
                    if (startDatas > noticeDatas.size() - 2) {
                        startDatas = 0;
                    }

                    removeAllViews();
                    genericTextView((startDatas + 2));
                    requestLayout();
                    startScroll(3f);
                }

                @Override
                public void onAnimationCancel(Animator animator) {
                }
            });
            super.handleMessage(msg);
        }
    };

    public AutoScrollView(Context context, List<String> noticeDatas) {
        super(context);
        this.noticeDatas = noticeDatas;
        this.context = context;
        this.setFocusable(false);
        this.setClickable(false);
        this.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        init(context);
    }

    public AutoScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        this.setFocusable(false);
        this.setClickable(false);
        this.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        init(context);
    }

    public AutoScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        this.setFocusable(false);
        this.setClickable(false);
        this.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        init(context);
    }

    public void init(Context context) {
        if (null == noticeDatas)
            return;

        if (noticeDatas.size() > 1) {
            noticeDatas.add(noticeDatas.get(0));
            genericTextView(2);
        } else {
            genericTextView(1);
        }

    }

    private int startDatas = 0;

    @SuppressLint({"NewApi", "InflateParams"})
    private void genericTextView(int size) {
        if (noticeDatas.size() >= size) {
            for (int j = startDatas; j < size; j++) {
                final View view = LayoutInflater.from(context).inflate(R.layout.notice, this, false);
                view.setId(j);
                //				TextView date = (TextView)view.findViewById(R.id.notice_date);
                TextView title = (TextView) view.findViewById(R.id.notice_title);
//				ImageView noticePic = (ImageView)view.findViewById(R.id.notice_img);
                title.setText(noticeDatas.get(j));
//				noticePic.setImageResource(picId);
                LinearLayout.LayoutParams params =
                        new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
                addView(view, params);
                view.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onAutoScrollViewClickListener.onAutoScrollViewClick(view.getId());
                    }
                });
            }
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int childCount = getChildCount();
        int height = 0;
        for (int i = 0; i < childCount; i++) {
            //			View view = getChildAt(i);
            //			int childWidhtMeasureSpec = MeasureSpec.makeMeasureSpec(widthMeasureSpec, MeasureSpec.AT_MOST);
            //			int childHeightMeasureSpec = MeasureSpec.makeMeasureSpec(heightMeasureSpec, MeasureSpec.AT_MOST);
            //			view.measure(childWidhtMeasureSpec, childHeightMeasureSpec);
            //			height = view.getMeasuredHeight();

            View child = getChildAt(i);
            measureChildWithMargins(child, widthMeasureSpec, 0, heightMeasureSpec, 0);
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean arg0, int left, int top, int right, int bottom) {
        int t = 0;
        for (int i = 0; i < getChildCount(); i++) {
            View view = getChildAt(i);
            view.layout(left, t + top, view.getMeasuredWidth(), t + view.getMeasuredHeight());
            t += view.getMeasuredHeight();
        }
    }

    private View preView;

    private View nextView;

    public void startScroll(float seconds) {
        float delayMillis = seconds * 1000;
        int childCount = getChildCount();
        if (childCount > 0)
            preView = getChildAt(0);
        if (childCount > 1) {
            nextView = getChildAt(1);
        }

        if (preView == null || nextView == null)
            return;

        hanlder.sendEmptyMessageDelayed(0, (int) delayMillis);
        isScroll = true;
    }

    /**
     * onDestory的时候调用
     */
    public void onRelease() {
        hanlder.removeCallbacksAndMessages(null);
    }

    /**
     * @param onAutoScrollViewClickListener 对onAutoScrollViewClickListener进行赋值
     */
    public void setOnAutoScrollViewClickListener(OnAutoScrollViewClickListener onAutoScrollViewClickListener) {
        this.onAutoScrollViewClickListener = onAutoScrollViewClickListener;
    }

    public interface OnAutoScrollViewClickListener {
        void onAutoScrollViewClick(int id);
    }
}