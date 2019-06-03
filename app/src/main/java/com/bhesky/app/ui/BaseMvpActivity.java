package com.bhesky.app.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.puhui.lib.base.BaseActivity;
import com.puhui.lib.base.BasePresenter;
import com.puhui.lib.utils.AppManager;

import butterknife.ButterKnife;
import butterknife.Unbinder;

public abstract class BaseMvpActivity<P extends BasePresenter> extends BaseActivity {
    protected P mPresenter;
    protected Unbinder mUnbinder;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppManager.getAppManager().addActivity(this);
        mPresenter = initPresenter();
        if (null != mPresenter) {
            mPresenter.attach(this);
        }
        mUnbinder = ButterKnife.bind(this);
    }

    protected abstract P initPresenter();

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null != mPresenter) {
            mPresenter.detach();
        }

        if (null != mUnbinder) {
            mUnbinder.unbind();
        }
    }
}
