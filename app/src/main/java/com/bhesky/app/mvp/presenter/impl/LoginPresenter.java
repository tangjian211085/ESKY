package com.bhesky.app.mvp.presenter.impl;

import com.puhui.lib.base.BasePresenter;
import com.bhesky.app.mvp.presenter.interfs.ILoginPresenter;
import com.bhesky.app.mvp.view.ILoginView;

public class LoginPresenter extends BasePresenter<ILoginView> implements ILoginPresenter {

    @Override
    public void getCommonData() {
        mBaseView.get();
    }
}
