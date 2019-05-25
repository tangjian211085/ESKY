package com.puhui.lib.widgets;

import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.puhui.lib.R;

import java.lang.reflect.Field;

/**
 * 描 述: 下拉刷新控件
 * 修 改 人: tangjian
 * 修改时间: 2016-6-27
 */
public class DMSwipeRefreshLayout extends SwipeRefreshLayout {
    private Context mContext;

    public DMSwipeRefreshLayout(Context context) {
        super(context);
        mContext = context;
        init();
    }

    public DMSwipeRefreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        init();
    }

    /**
     * 一些初始化设置
     */
    private void init() {
        this.setColorSchemeResources(R.color.swipe_color1, R.color.swipe_color2, R.color.swipe_color3, R.color.swipe_color4);
        Field f = null;
        try {
            f = SwipeRefreshLayout.class.getDeclaredField("mTouchSlop");
            f.setAccessible(true);
            f.set(this, 150);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }

    private float donwX = 0;
    private float donwY = 0;

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                donwX = ev.getX();
                donwY = ev.getY();
                break;
            case MotionEvent.ACTION_UP:
                if (Math.abs(ev.getX() - donwX) < 5 && Math.abs(ev.getY() - donwY) < 5) {
//                    ToastUtil.getInstant().show(getContext(), "action_up");
                    return false;
                }
                break;
            case MotionEvent.ACTION_MOVE:
                break;
        }
        return super.onInterceptTouchEvent(ev);
    }
}
