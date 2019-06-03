package com.bhesky.app.mvp.presenter.interfs;

import com.puhui.lib.base.IBasePresenter;
import com.bhesky.app.mvp.view.ILoginView;

public interface ILoginPresenter extends IBasePresenter<ILoginView> {
    void getCommonData();
}
