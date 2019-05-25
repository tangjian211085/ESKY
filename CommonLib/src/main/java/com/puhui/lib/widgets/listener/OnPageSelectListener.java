package com.puhui.lib.widgets.listener;

import android.support.v4.view.ViewPager;

/**
 * Copyright: 人人普惠
 * Created by TangJian on 2017/3/2.
 * Description: ViewPager类的页面切换监听
 * Modified: by TangJian on 2017/3/2.
 */

public abstract class OnPageSelectListener implements ViewPager.OnPageChangeListener {
    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public abstract void onPageSelected(int position);

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}
